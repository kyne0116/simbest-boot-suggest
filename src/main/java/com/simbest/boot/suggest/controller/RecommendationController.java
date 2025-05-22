package com.simbest.boot.suggest.controller;

import static com.simbest.boot.suggest.model.JsonResponse.MSG_SUCCESS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.simbest.boot.suggest.config.AppConfig;
import com.simbest.boot.suggest.model.JsonResponse;
import com.simbest.boot.suggest.model.Leader;
import com.simbest.boot.suggest.model.LeaderDTO;
import com.simbest.boot.suggest.model.Organization;
import com.simbest.boot.suggest.model.RecommendationFeedback;
import com.simbest.boot.suggest.model.RecommendationRequest;
import com.simbest.boot.suggest.model.RecommendationResult;
import com.simbest.boot.suggest.model.RecommendationType;
import com.simbest.boot.suggest.service.LeaderService;
import com.simbest.boot.suggest.service.OrganizationService;
import com.simbest.boot.suggest.service.RecommendationFeedbackService;
import com.simbest.boot.suggest.service.RecommendationService;
import com.simbest.boot.suggest.util.ChineseTokenizer;
import com.simbest.boot.suggest.util.JsonUtil;
import com.simbest.boot.suggest.util.TenantValidator;

import lombok.extern.slf4j.Slf4j;

/**
 * 领导推荐控制器（POST方法）
 * 提供领导推荐相关的REST API，使用POST方法和JSON请求体
 */
@RestController
@RequestMapping("/recommend")
@Slf4j
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private LeaderService leaderService;

    @Autowired
    private RecommendationFeedbackService recommendationFeedbackService;

    @Autowired
    private TenantValidator tenantValidator;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private com.simbest.boot.suggest.service.HistoricalMatchingService historicalMatchingService;

    /**
     * 推荐领导（POST方法，使用JSON请求体）
     *
     * @param request 推荐请求对象，包含以下字段：
     *                - tenantCode: 租户编码（必填）
     *                - taskTitle: 任务标题（必填）
     *                - workflowDirection:
     *                工作流方向（必填）：向下指派(DOWNWARD)、向上请示(UPWARD)或同级协办(PARALLEL)
     *                - userAccount: 当前办理人账号（必填，如果未提供orgId）
     *                - orgId: 当前办理人组织ID（必填，如果未提供userAccount）
     *                - taskId: 任务ID（可选）
     *                - candidateAccounts: 候选账号列表（可选），如果提供，则推荐结果必须在此列表中
     *                - recommendationType: 推荐类型（可选，默认为单选推荐人）
     *                单选推荐人(SINGLE)：只返回一个推荐命中率最高最匹配的推荐领导
     *                多选推荐人(MULTIPLE)：按照推荐命中率倒排，可以推荐一个或多个领导
     * @return 推荐结果
     */

    @PostMapping(value = "/getRecommendation", produces = "application/json;charset=UTF-8")
    public JsonResponse<?> getRecommendation(@RequestBody RecommendationRequest request) {
        try {
            // 参数校验
            if (request.getTenantCode() == null || request.getTenantCode().isEmpty()) {
                return JsonResponse.fail("租户编码不能为空");
            }
            if (request.getTaskTitle() == null || request.getTaskTitle().isEmpty()) {
                return JsonResponse.fail("任务标题不能为空");
            }
            if (request.getWorkflowDirection() == null) {
                return JsonResponse.fail("工作流方向不能为空");
            }

            // 验证userAccount和orgId至少有一个不为空
            if ((request.getUserAccount() == null || request.getUserAccount().isEmpty())
                    && (request.getOrgId() == null || request.getOrgId().isEmpty())) {
                return JsonResponse.fail("用户账号和组织ID不能同时为空，必须提供其中一个");
            }

            // 验证租户
            try {
                tenantValidator.validateTenant(request.getTenantCode());
            } catch (IllegalArgumentException e) {
                return JsonResponse.fail("租户验证失败: " + e.getMessage());
            }

            // 创建请求参数Map用于日志记录
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("tenantCode", request.getTenantCode());
            requestMap.put("userAccount", request.getUserAccount());
            requestMap.put("orgId", request.getOrgId() != null ? request.getOrgId() : "未提供");
            requestMap.put("taskTitle", request.getTaskTitle());
            requestMap.put("taskId", request.getTaskId() != null ? request.getTaskId() : "未提供");
            requestMap.put("workflowDirection", request.getWorkflowDirection());
            requestMap.put("candidateAccounts",
                    request.getCandidateAccounts() != null ? request.getCandidateAccounts() : "未提供");

            // 使用规范格式记录请求参数
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            String userAccountForLog = request.getUserAccount() != null ? request.getUserAccount() : "anonymous";
            log.info("【{}-getRecommendation-{}-START】", userAccountForLog, timestamp);
            log.info("请求参数信息: {}", JsonUtil.toJson(requestMap));

            // 检查数据加载情况 - 使用DEBUG级别记录详细信息
            if (log.isDebugEnabled()) {
                log.debug("【系统状态】");
                log.debug("组织数量: {}", organizationService.getAllOrganizations().size());
                log.debug("领导数量: {}", leaderService.getAllLeaders().size());

                // 检查分词结果
                List<String> textTokens = ChineseTokenizer.tokenize(request.getTaskTitle());
                log.debug("  分词结果: {}", textTokens);

                // 检查动态阈值
                double threshold = recommendationService.calculateDynamicThreshold(request.getTaskTitle());
                log.debug("动态阈值: {}", threshold);
            }

            // 记录开始处理时间
            long startTime = System.currentTimeMillis();

            // 设置默认推荐类型
            RecommendationType recommendationType = request.getRecommendationType() != null
                    ? request.getRecommendationType()
                    : RecommendationType.SINGLE;

            // 使用推荐服务进行推荐
            RecommendationResult result = recommendationService.recommendLeader(
                    request.getUserAccount(),
                    request.getOrgId() != null ? request.getOrgId() : "",
                    request.getTaskTitle(),
                    request.getWorkflowDirection(),
                    request.getCandidateAccounts(),
                    request.getTenantCode(),
                    recommendationType);

            // 计算处理时间
            long processingTime = System.currentTimeMillis() - startTime;

            // 打印请求结果
            if (result != null) {
                // 创建结果Map用于日志记录
                Map<String, Object> resultMap = new HashMap<>();

                // 添加领导信息
                List<Map<String, String>> leadersInfo = new ArrayList<>();
                for (LeaderDTO leader : result.getLeaders()) {
                    Map<String, String> leaderInfo = new HashMap<>();
                    leaderInfo.put("account", leader.getSuggestAccount());
                    leaderInfo.put("name", leader.getSuggestTruename());
                    leadersInfo.add(leaderInfo);
                }
                resultMap.put("leaders", leadersInfo);
                resultMap.put("count", result.getLeaders().size());

                // 单个领导的信息（如果有）
                if (!result.getLeaders().isEmpty()) {
                    resultMap.put("leaderAccount", result.getLeaders().get(0).getSuggestAccount());
                    resultMap.put("leaderName", result.getLeaders().get(0).getSuggestTruename());
                } else {
                    resultMap.put("leaderAccount", null);
                    resultMap.put("leaderName", null);
                }

                resultMap.put("score", result.getScore());
                resultMap.put("confidenceLevel", result.getConfidenceLevel());
                resultMap.put("recommendationType", result.getRecommendationType());
                resultMap.put("reason", result.getReason());
                resultMap.put("aiMetrics", result.getAiMetrics());
                resultMap.put("processingTimeMs", processingTime);
                resultMap.put("selectionMode", recommendationType.toString());

                // 打印请求结果信息
                log.info("推荐结果信息: {}", JsonUtil.toJson(resultMap));

                // 如果启用了DEBUG日志，打印详细信息
                if (log.isDebugEnabled()) {
                    if (recommendationType == RecommendationType.SINGLE) {
                        // 生成详细的AI分析报告
                        String detailedReport = result.generateDetailedAIReport();
                        log.debug("详细AI分析报告: {}", detailedReport);
                    } else {
                        log.debug("详细推荐结果: {}", result.toString());
                    }
                }
            } else {
                // 打印无推荐结果的信息
                log.info("推荐结果信息: 无匹配的推荐结果, 处理时间: {}毫秒", processingTime);
            }

            // 使用规范格式记录请求结束
            String endTimestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            log.info("【{}-getRecommendation-{}-END】", userAccountForLog, endTimestamp);
            JsonResponse<RecommendationResult> response = JsonResponse.success(result, MSG_SUCCESS);
            response.setSuccess(true); // 确保success字段始终为true
            return response;
        } catch (Exception e) {
            log.error("推荐服务执行失败: {}", e.getMessage(), e);
            return JsonResponse.fail("推荐服务执行失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有组织
     *
     * @return 所有组织的列表
     */
    @GetMapping("/organizations")
    public List<Organization> getAllOrganizations() {
        return organizationService.getAllOrganizations();
    }

    /**
     * 获取所有领导
     *
     * @return 所有领导的列表
     */
    @GetMapping("/leaders")
    public List<Leader> getAllLeaders() {
        return leaderService.getAllLeaders();
    }

    /**
     * 获取系统信息
     *
     * @return 系统信息
     */
    @GetMapping("/info")
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", appConfig.getName());
        info.put("version", appConfig.getVersion());
        info.put("organizationCount", organizationService.getAllOrganizations().size());
        info.put("leaderCount", leaderService.getAllLeaders().size());
        return info;
    }

    /**
     * 检查组织ID是否存在
     *
     * @param orgId 组织ID
     * @return 检查结果
     */
    @GetMapping("/check-org")
    public Map<String, Object> checkOrganization(@RequestParam String orgId) {
        Map<String, Object> result = new HashMap<>();
        Organization org = organizationService.getOrganizationById(orgId);
        result.put("exists", org != null);
        result.put("orgId", orgId);
        if (org != null) {
            result.put("orgName", org.getOrgName());
            result.put("mainLeaderAccount", org.getMainLeaderAccount());
            result.put("deputyLeaderAccounts", org.getDeputyLeaderAccounts());
            result.put("superiorLeaderAccount", org.getSuperiorLeaderAccount());
        }

        // 打印所有组织ID
        List<String> allOrgIds = new ArrayList<>();
        for (Organization organization : organizationService.getAllOrganizations()) {
            allOrgIds.add(organization.getOrgId());
        }
        result.put("allOrgIds", allOrgIds);

        return result;
    }

    /**
     * 测试关键词匹配
     *
     * @param text 文本
     * @return 匹配结果
     */
    @GetMapping("/test-match")
    public Map<String, Object> testMatch(@RequestParam String text) {
        Map<String, Object> result = new HashMap<>();
        result.put("text", text);

        // 分词结果
        List<String> textTokens = ChineseTokenizer.tokenize(text);
        result.put("tokens", textTokens);

        // 匹配结果
        Map<String, Object> matchResults = new HashMap<>();
        // 不再使用DomainService，改为使用ResponsibilityDomainService
        // 但由于ResponsibilityDomainService的接口与DomainService不同，这里暂时简化处理
        result.put("matchResults", matchResults);

        // 动态阈值
        double threshold = recommendationService.calculateDynamicThreshold(text);
        result.put("threshold", threshold);

        // 最佳匹配结果
        // 不再使用DomainService，改为使用ResponsibilityDomainService
        // 但由于ResponsibilityDomainService的接口与DomainService不同，这里暂时简化处理
        result.put("bestMatchDomainId", null);
        result.put("bestMatchScore", 0.0);
        result.put("aboveThreshold", false);

        return result;
    }

    /**
     * 验证租户
     *
     * @param tenantCode 租户编码
     * @return 验证结果
     */
    @GetMapping("/validate-tenant")
    public JsonResponse<Boolean> validateTenant(@RequestParam String tenantCode) {
        try {
            boolean valid = tenantValidator.isValidTenant(tenantCode);
            if (valid) {
                return JsonResponse.success(true, "租户有效");
            } else {
                return JsonResponse.fail("租户无效或不存在");
            }
        } catch (Exception e) {
            log.error("验证租户失败: {}", e.getMessage(), e);
            return JsonResponse.fail("验证租户失败: " + e.getMessage());
        }
    }

    /**
     * 测试历史批复记录匹配
     *
     * @param tenantCode        租户编码
     * @param taskTitle         任务标题
     * @param workflowDirection 工作流方向
     * @return 匹配结果
     */
    @GetMapping("/test-history-match")
    public JsonResponse<?> testHistoryMatch(
            @RequestParam String tenantCode,
            @RequestParam String taskTitle,
            @RequestParam com.simbest.boot.suggest.model.WorkflowDirection workflowDirection) {
        try {
            // 验证租户
            tenantValidator.validateTenant(tenantCode);

            // 调用历史批复记录匹配服务
            RecommendationResult result = historicalMatchingService.recommendLeaderByHistory(
                    tenantCode, taskTitle, null, workflowDirection);

            if (result != null) {
                return JsonResponse.success(result, "匹配成功");
            } else {
                return JsonResponse.fail("未找到匹配的历史批复记录");
            }
        } catch (Exception e) {
            log.error("测试历史批复记录匹配失败: {}", e.getMessage(), e);
            return JsonResponse.fail("测试历史批复记录匹配失败: " + e.getMessage());
        }
    }

    /**
     * 保存推荐反馈
     *
     * @param feedback 推荐反馈对象
     * @return 保存结果
     */
    @PostMapping("/save-feedback")
    public JsonResponse<?> saveFeedback(@RequestBody RecommendationFeedback feedback) {
        try {
            // 验证租户
            tenantValidator.validateTenant(feedback.getTenantCode());

            // 保存推荐反馈
            RecommendationFeedback savedFeedback = recommendationFeedbackService.saveFeedback(feedback);

            return JsonResponse.success(savedFeedback, "保存推荐反馈成功");
        } catch (Exception e) {
            log.error("保存推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("保存推荐反馈失败: " + e.getMessage());
        }
    }

    /**
     * 获取推荐反馈统计信息
     *
     * @param tenantCode 租户编码
     * @return 统计信息
     */
    @GetMapping("/feedback-stats")
    public JsonResponse<?> getFeedbackStats(@RequestParam String tenantCode) {
        try {
            // 验证租户
            tenantValidator.validateTenant(tenantCode);

            // 计算统计信息
            double acceptanceRate = recommendationFeedbackService.calculateAcceptanceRate(tenantCode);
            double singleAcceptanceRate = recommendationFeedbackService.calculateAcceptanceRateByType(tenantCode,
                    "SINGLE");
            double multipleAcceptanceRate = recommendationFeedbackService.calculateAcceptanceRateByType(tenantCode,
                    "MULTIPLE");
            double averageRating = recommendationFeedbackService.calculateAverageRating(tenantCode);

            // 创建统计信息Map
            Map<String, Object> stats = new HashMap<>();
            stats.put("acceptanceRate", acceptanceRate);
            stats.put("singleAcceptanceRate", singleAcceptanceRate);
            stats.put("multipleAcceptanceRate", multipleAcceptanceRate);
            stats.put("averageRating", averageRating);

            return JsonResponse.success(stats, "获取推荐反馈统计信息成功");
        } catch (Exception e) {
            log.error("获取推荐反馈统计信息失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取推荐反馈统计信息失败: " + e.getMessage());
        }
    }
}
