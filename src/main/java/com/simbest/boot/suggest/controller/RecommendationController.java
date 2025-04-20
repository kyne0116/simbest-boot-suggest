package com.simbest.boot.suggest.controller;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.simbest.boot.suggest.model.JsonResponse;
import com.simbest.boot.suggest.model.Leader;
import com.simbest.boot.suggest.model.Organization;
import com.simbest.boot.suggest.model.RecommendationResult;
import com.simbest.boot.suggest.model.ResponsibilityDomain;
import com.simbest.boot.suggest.service.DomainService;
import com.simbest.boot.suggest.service.LeaderService;
import com.simbest.boot.suggest.service.OrganizationService;
import com.simbest.boot.suggest.service.RecommendationService;
import com.simbest.boot.suggest.util.ChineseTokenizer;

/**
 * 领导推荐控制器
 * 提供领导推荐相关的REST API
 */
@RestController
@RequestMapping("/api/recommendation")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private LeaderService leaderService;

    @Autowired
    private DomainService domainService;

    /**
     * 推荐领导
     *
     * @param userAccount       当前办理人账号（必填）
     * @param orgId             当前办理人组织ID（可选）
     * @param taskTitle         任务标题（必填）
     * @param useOrg            是否使用组织关系（可选，默认为true）
     * @param candidateAccounts 候选账号列表（可选），如果提供，则推荐结果必须在此列表中
     * @return 推荐结果
     */
    @GetMapping("/recommend")
    public JsonResponse<RecommendationResult> recommendLeader(
            @RequestParam(required = true) String userAccount,
            @RequestParam(required = false) String orgId,
            @RequestParam String taskTitle,
            @RequestParam(required = false, defaultValue = "true") boolean useOrg,
            @RequestParam(required = false) String[] candidateAccounts) {
        System.out.println("\n\n=== 推荐请求 ===\n");
        System.out.println("用户账号: " + userAccount);
        System.out.println("组织ID: " + (orgId != null ? orgId : "未提供"));
        System.out.println("任务标题: " + taskTitle);
        System.out.println("使用组织关系: " + useOrg);
        System.out.println("候选账号列表: " + (candidateAccounts != null ? String.join(", ", candidateAccounts) : "未提供"));

        // 检查数据加载情况
        System.out.println("\n组织数量: " + organizationService.getAllOrganizations().size());
        System.out.println("领导数量: " + leaderService.getAllLeaders().size());
        System.out.println("职责领域数量: " + domainService.getAllDomains().size());

        // 检查关键词匹配情况
        System.out.println("\n关键词匹配情况:");
        for (ResponsibilityDomain domain : domainService.getAllDomains()) {
            double score = domain.calculateMatchScore(taskTitle);
            List<String> matchedKeywords = domain.getMatchedKeywords(taskTitle);
            System.out.println(domain.getDomainName() + " - 匹配分数: " + score + ", 匹配关键词: " + matchedKeywords);

            // 打印关键词列表
            System.out.println("  关键词列表: " + domain.getKeywords());

            // 检查分词结果
            List<String> textTokens = ChineseTokenizer.tokenize(taskTitle);
            System.out.println("  分词结果: " + textTokens);

            // 检查直接匹配
            for (String keyword : domain.getKeywords()) {
                boolean directMatch = taskTitle.contains(keyword);
                if (directMatch) {
                    System.out.println("  直接匹配关键词: " + keyword);
                }
            }
        }

        // 检查动态阈值
        double threshold = recommendationService.calculateDynamicThreshold(taskTitle);
        System.out.println("\n动态阈值: " + threshold);

        RecommendationResult result = recommendationService.recommendLeader(
                userAccount,
                orgId != null ? orgId : "",
                taskTitle,
                useOrg,
                candidateAccounts);
        System.out.println("\n推荐结果: " + (result != null ? result : "无推荐结果"));
        System.out.println("\n=== 推荐结束 ===\n\n");
        return JsonResponse.success(result, taskTitle + "的推荐结果");
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
        info.put("name", "领导推荐系统");
        info.put("version", "1.0.0");
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
