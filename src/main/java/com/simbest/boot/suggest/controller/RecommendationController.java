package com.simbest.boot.suggest.controller;

import static com.simbest.boot.suggest.model.JsonResponse.MSG_SUCCESS;

import java.util.AbstractMap;
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

import com.simbest.boot.suggest.config.SystemConfig;
import com.simbest.boot.suggest.model.JsonResponse;
import com.simbest.boot.suggest.model.Leader;
import com.simbest.boot.suggest.model.Organization;
import com.simbest.boot.suggest.model.RecommendationRequest;
import com.simbest.boot.suggest.model.RecommendationResult;
import com.simbest.boot.suggest.model.ResponsibilityDomain;
import com.simbest.boot.suggest.service.DomainService;
import com.simbest.boot.suggest.service.LeaderService;
import com.simbest.boot.suggest.service.OrganizationService;
import com.simbest.boot.suggest.service.RecommendationService;
import com.simbest.boot.suggest.util.ChineseTokenizer;
import com.simbest.boot.suggest.util.JsonUtil;

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
    private DomainService domainService;

    @Autowired
    private SystemConfig systemConfig;

    /**
     * 推荐领导（POST方法，使用JSON请求体）
     *
     * @param request 推荐请求对象，包含以下字段：
     *                - userAccount: 当前办理人账号（必填）
     *                - orgId: 当前办理人组织ID（可选）
     *                - taskTitle: 任务标题（必填）
     *                - workflowDirection:
     *                工作流方向（必填）：向下指派(DOWNWARD)、向上请示(UPWARD)或同级协办(PARALLEL)
     *                - useOrg: 是否使用组织关系（可选，默认为true）
     *                - candidateAccounts: 候选账号列表（可选），如果提供，则推荐结果必须在此列表中
     * @return 推荐结果
     */
    @PostMapping("/getRecommendation")
    public JsonResponse<RecommendationResult> getRecommendation(@RequestBody RecommendationRequest request) {
        // 参数校验
        if (request.getUserAccount() == null || request.getUserAccount().isEmpty()) {
            return JsonResponse.fail("用户账号不能为空");
        }
        if (request.getTaskTitle() == null || request.getTaskTitle().isEmpty()) {
            return JsonResponse.fail("任务标题不能为空");
        }
        if (request.getWorkflowDirection() == null) {
            return JsonResponse.fail("工作流方向不能为空");
        }

        // 设置默认值
        boolean useOrg = request.getUseOrg() != null ? request.getUseOrg() : true;

        // 创建请求参数Map用于日志记录
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("userAccount", request.getUserAccount());
        requestMap.put("orgId", request.getOrgId() != null ? request.getOrgId() : "未提供");
        requestMap.put("taskTitle", request.getTaskTitle());
        requestMap.put("workflowDirection", request.getWorkflowDirection());
        requestMap.put("useOrg", useOrg);
        requestMap.put("candidateAccounts",
                request.getCandidateAccounts() != null ? request.getCandidateAccounts() : "未提供");

        // 使用新格式记录请求参数
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        log.info("【{}-getRecommendation-START-{}】", request.getUserAccount(), timestamp);
        log.info("请求参数信息\n{}", JsonUtil.toJsonPretty(requestMap));

        // 检查数据加载情况 - 使用DEBUG级别记录详细信息
        if (log.isDebugEnabled()) {
            log.debug("【系统状态】");
            log.debug("组织数量: {}", organizationService.getAllOrganizations().size());
            log.debug("领导数量: {}", leaderService.getAllLeaders().size());
            log.debug("职责领域数量: {}", domainService.getAllDomains().size());

            // 检查关键词匹配情况
            log.debug("\n【关键词匹配详情】");
            for (ResponsibilityDomain domain : domainService.getAllDomains()) {
                double score = domain.calculateMatchScore(request.getTaskTitle());
                List<String> matchedKeywords = domain.getMatchedKeywords(request.getTaskTitle());
                log.debug("{} - 匹配分数: {}, 匹配关键词: {}", domain.getDomainName(), score, matchedKeywords);

                // 打印关键词列表
                log.debug("  关键词列表: {}", domain.getKeywords());

                // 检查分词结果
                List<String> textTokens = ChineseTokenizer.tokenize(request.getTaskTitle());
                log.debug("  分词结果: {}", textTokens);

                // 检查直接匹配
                for (String keyword : domain.getKeywords()) {
                    boolean directMatch = request.getTaskTitle().contains(keyword);
                    if (directMatch) {
                        log.debug("  直接匹配关键词: {}", keyword);
                    }
                }
            }

            // 检查动态阈值
            double threshold = recommendationService.calculateDynamicThreshold(request.getTaskTitle());
            log.debug("动态阈值: {}", threshold);
        }

        // 记录开始处理时间
        long startTime = System.currentTimeMillis();

        // 调用推荐服务
        RecommendationResult result = recommendationService.recommendLeader(
                request.getUserAccount(),
                request.getOrgId() != null ? request.getOrgId() : "",
                request.getTaskTitle(),
                request.getWorkflowDirection(),
                useOrg,
                request.getCandidateAccounts());

        // 计算处理时间
        long processingTime = System.currentTimeMillis() - startTime;

        // 不再需要创建输入输出对照结构，因为我们已经在开始时打印了请求参数

        // 打印请求结果
        if (result != null) {
            // 生成详细的AI分析报告
            String detailedReport = result.generateDetailedAIReport();

            // 创建结果Map用于日志记录
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("leaderAccount", result.getLeaderAccount());
            resultMap.put("leaderName", result.getLeaderName());
            resultMap.put("score", result.getScore());
            resultMap.put("confidenceLevel", result.getConfidenceLevel());
            resultMap.put("recommendationType", result.getRecommendationType());
            resultMap.put("reason", result.getReason());
            resultMap.put("aiMetrics", result.getAiMetrics());
            resultMap.put("processingTimeMs", processingTime);

            // 打印请求结果信息
            log.info("请求结果信息\n{}", JsonUtil.toJsonPretty(resultMap));

            // 如果启用了DEBUG日志，打印详细的AI分析报告
            if (log.isDebugEnabled()) {
                log.debug("详细AI分析报告\n{}", detailedReport);
            }
        } else {
            // 打印无推荐结果的信息
            log.info("请求结果信息: 无匹配的推荐结果, 处理时间: {}毫秒", processingTime);
        }

        // 使用新格式记录请求结束
        String endTimestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        log.info("【{}-getRecommendation-END-{}】", request.getUserAccount(), endTimestamp);
        return JsonResponse.success(result, MSG_SUCCESS);
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
        info.put("name", systemConfig.getSystemName());
        info.put("version", systemConfig.getSystemVersion());
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
        for (ResponsibilityDomain domain : domainService.getAllDomains()) {
            Map<String, Object> domainResult = new HashMap<>();
            double score = domain.calculateMatchScore(text);
            List<String> matchedKeywords = domain.getMatchedKeywords(text);

            domainResult.put("score", score);
            domainResult.put("matchedKeywords", matchedKeywords);
            domainResult.put("keywords", domain.getKeywords());
            domainResult.put("domainName", domain.getDomainName());
            domainResult.put("responsiblePerson", domain.getResponsiblePerson());
            domainResult.put("leaderAccount", domain.getLeaderAccount());

            matchResults.put(domain.getDomainId(), domainResult);
        }
        result.put("matchResults", matchResults);

        // 动态阈值
        double threshold = recommendationService.calculateDynamicThreshold(text);
        result.put("threshold", threshold);

        // 最佳匹配结果
        AbstractMap.SimpleEntry<String, Double> bestMatch = domainService.getBestMatchDomain(text);
        if (bestMatch != null) {
            result.put("bestMatchDomainId", bestMatch.getKey());
            result.put("bestMatchScore", bestMatch.getValue());
            result.put("aboveThreshold", bestMatch.getValue() >= threshold);

            ResponsibilityDomain domain = domainService.getDomainById(bestMatch.getKey());
            if (domain != null) {
                result.put("bestMatchDomainName", domain.getDomainName());
                result.put("bestMatchLeaderAccount", domain.getLeaderAccount());
            }
        } else {
            result.put("bestMatchDomainId", null);
            result.put("bestMatchScore", 0.0);
            result.put("aboveThreshold", false);
        }

        return result;
    }
}
