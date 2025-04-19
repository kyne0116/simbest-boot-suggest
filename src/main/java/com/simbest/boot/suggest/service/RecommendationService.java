package com.simbest.boot.suggest.service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.simbest.boot.suggest.model.Leader;
import com.simbest.boot.suggest.model.Organization;
import com.simbest.boot.suggest.model.RecommendationResult;
import com.simbest.boot.suggest.model.ResponsibilityDomain;
import com.simbest.boot.suggest.model.TextSimilarityUtil;
import com.simbest.boot.suggest.util.DataLoader;

/**
 * 推荐服务
 * 提供领导推荐相关的操作
 */
public class RecommendationService {
    private OrganizationService organizationService;
    private LeaderService leaderService;
    private DomainService domainService;

    /**
     * 构造函数
     *
     * @param organizationService 组织服务
     * @param leaderService       领导服务
     * @param domainService       职责领域服务
     */
    public RecommendationService(OrganizationService organizationService,
            LeaderService leaderService,
            DomainService domainService) {
        this.organizationService = organizationService;
        this.leaderService = leaderService;
        this.domainService = domainService;
    }

    /**
     * 推荐领导账号
     *
     * @param currentUserAccount 当前办理人账号
     * @param currentUserOrgId   当前办理人组织ID
     * @param taskTitle          任务标题
     * @param useOrg             是否使用基于组织关系的匹配，默认为true
     * @return 推荐结果
     */
    public RecommendationResult recommendLeader(String currentUserAccount,
            String currentUserOrgId,
            String taskTitle,
            boolean useOrg) {
        // 1. 基于组织关系的匹配（如果useOrg为true）
        if (useOrg) {
            RecommendationResult orgResult = recommendLeaderByOrganization(currentUserOrgId);
            if (orgResult != null) {
                return orgResult;
            }
        }

        // 2. 基于职责领域的匹配
        RecommendationResult domainResult = recommendLeaderByDomain(taskTitle);
        if (domainResult != null) {
            return domainResult;
        }

        // 3. 基于文本相似度的匹配
        return recommendLeaderBySimilarity(taskTitle);
    }

    /**
     * 推荐领导账号（兼容旧版本接口）
     *
     * @param currentUserAccount 当前办理人账号
     * @param currentUserOrgId   当前办理人组织ID
     * @param taskTitle          任务标题
     * @return 推荐结果
     */
    public RecommendationResult recommendLeader(String currentUserAccount,
            String currentUserOrgId,
            String taskTitle) {
        // 默认使用基于组织关系的匹配
        return recommendLeader(currentUserAccount, currentUserOrgId, taskTitle, true);
    }

    /**
     * 基于组织关系推荐领导账号
     *
     * @param orgId 组织ID
     * @return 推荐结果
     */
    public RecommendationResult recommendLeaderByOrganization(String orgId) {
        if (orgId == null || orgId.isEmpty()) {
            return null;
        }

        // 获取组织的分管领导
        String leaderAccount = organizationService.getLeaderAccountByOrgId(orgId);
        if (leaderAccount == null || leaderAccount.isEmpty()) {
            return null;
        }

        // 获取领导信息
        Leader leader = leaderService.getLeaderByAccount(leaderAccount);
        if (leader == null) {
            return null;
        }

        // 获取组织信息
        Organization org = organizationService.getOrganizationById(orgId);
        String orgName = org != null ? org.getOrgName() : orgId;

        // 创建推荐结果
        String reason = "基于组织关系匹配：该领导是 " + orgName + " 的分管领导";
        return new RecommendationResult(leaderAccount, leader.getName(), reason, 1.0);
    }

    /**
     * 基于职责领域推荐领导账号
     *
     * @param taskTitle 任务标题
     * @return 推荐结果
     */
    public RecommendationResult recommendLeaderByDomain(String taskTitle) {
        if (taskTitle == null || taskTitle.isEmpty()) {
            return null;
        }

        // 获取最佳匹配的职责领域
        AbstractMap.SimpleEntry<String, Double> bestMatch = domainService.getBestMatchDomain(taskTitle);
        if (bestMatch == null) {
            return null;
        }

        String domainId = bestMatch.getKey();
        double score = bestMatch.getValue();

        // 计算动态阈值
        double threshold = calculateDynamicThreshold(taskTitle);

        // 如果匹配分数低于阈值，则不推荐
        if (score < threshold) {
            return null;
        }

        // 获取职责领域信息
        ResponsibilityDomain domain = domainService.getDomainById(domainId);
        if (domain == null) {
            return null;
        }

        // 获取负责该领域的领导账号
        String leaderAccount = null;
        for (Leader leader : leaderService.getAllLeaders()) {
            if (leader.getDomainIds().contains(domainId)) {
                leaderAccount = leader.getAccount();
                break;
            }
        }

        if (leaderAccount == null) {
            return null;
        }

        // 获取领导信息
        Leader leader = leaderService.getLeaderByAccount(leaderAccount);
        if (leader == null) {
            return null;
        }

        // 获取匹配的关键词
        List<String> matchedKeywords = domain.getMatchedKeywords(taskTitle);
        String keywordsStr = String.join("、", matchedKeywords);

        // 创建推荐结果
        String reason = "基于职责领域匹配：该领导负责 " + domain.getDomainName() + " 领域，匹配关键词：" + keywordsStr;
        return new RecommendationResult(leaderAccount, leader.getName(), reason, score);
    }

    /**
     * 基于文本相似度推荐领导账号
     *
     * @param taskTitle 任务标题
     * @return 推荐结果
     */
    public RecommendationResult recommendLeaderBySimilarity(String taskTitle) {
        if (taskTitle == null || taskTitle.isEmpty()) {
            return null;
        }

        String bestLeaderAccount = null;
        double bestScore = 0.0;

        // 遍历所有领导，计算文本相似度
        for (Leader leader : leaderService.getAllLeaders()) {
            // 获取领导负责的所有职责领域
            List<String> domainIds = leader.getDomainIds();

            // 计算任务标题与每个职责领域描述的相似度
            double totalScore = 0.0;
            int count = 0;

            for (String domainId : domainIds) {
                ResponsibilityDomain domain = domainService.getDomainById(domainId);
                if (domain != null) {
                    double similarity = TextSimilarityUtil.calculateFinalSimilarity(
                            taskTitle, domain.getDescription());
                    totalScore += similarity;
                    count++;
                }
            }

            // 计算平均相似度
            double avgScore = count > 0 ? totalScore / count : 0.0;

            // 更新最佳匹配
            if (avgScore > bestScore) {
                bestScore = avgScore;
                bestLeaderAccount = leader.getAccount();
            }
        }

        // 计算动态阈值
        double threshold = calculateDynamicThreshold(taskTitle);

        // 如果匹配分数低于阈值，则不推荐
        if (bestScore < threshold || bestLeaderAccount == null) {
            return null;
        }

        // 获取领导信息
        Leader leader = leaderService.getLeaderByAccount(bestLeaderAccount);
        if (leader == null) {
            return null;
        }

        // 创建推荐结果
        String reason = "基于文本相似度匹配：该领导的职责领域与任务标题具有较高的相似度";
        return new RecommendationResult(bestLeaderAccount, leader.getName(), reason, bestScore);
    }

    /**
     * 计算动态阈值
     * 根据任务标题的特征动态调整阈值
     *
     * @param taskTitle 任务标题
     * @return 动态阈值
     */
    @SuppressWarnings("unchecked")
    public double calculateDynamicThreshold(String taskTitle) {
        // 从配置文件加载阈值配置
        Map<String, Object> config = DataLoader.loadThresholdConfig();

        // 基础阈值
        double baseThreshold = config.containsKey("baseThreshold")
                ? ((Number) config.get("baseThreshold")).doubleValue()
                : 0.01;

        // 根据标题长度调整
        int length = taskTitle.length();
        double lengthFactor = 1.0;

        if (config.containsKey("lengthThresholds")) {
            List<Map<String, Object>> lengthThresholds = (List<Map<String, Object>>) config.get("lengthThresholds");
            for (Map<String, Object> threshold : lengthThresholds) {
                int thresholdLength = ((Number) threshold.get("length")).intValue();
                double factor = ((Number) threshold.get("factor")).doubleValue();
                if (length > thresholdLength) {
                    lengthFactor = factor;
                }
            }
        }

        // 根据标题中特定词语调整
        double contentFactor = 1.0;

        if (config.containsKey("contentAdjustments")) {
            List<Map<String, Object>> contentAdjustments = (List<Map<String, Object>>) config.get("contentAdjustments");
            for (Map<String, Object> adjustment : contentAdjustments) {
                List<String> keywords = (List<String>) adjustment.get("keywords");
                double factor = ((Number) adjustment.get("factor")).doubleValue();

                for (String keyword : keywords) {
                    if (taskTitle.contains(keyword)) {
                        contentFactor = factor;
                        break;
                    }
                }
            }
        }

        return baseThreshold * lengthFactor * contentFactor;
    }

    /**
     * 获取所有可能的推荐结果
     *
     * @param currentUserAccount 当前办理人账号
     * @param currentUserOrgId   当前办理人组织ID
     * @param taskTitle          任务标题
     * @param useOrg             是否使用基于组织关系的匹配，默认为true
     * @return 所有可能的推荐结果列表
     */
    public List<RecommendationResult> getAllPossibleRecommendations(String currentUserAccount,
            String currentUserOrgId,
            String taskTitle,
            boolean useOrg) {
        List<RecommendationResult> results = new ArrayList<>();

        // 1. 基于组织关系的匹配（如果useOrg为true）
        if (useOrg) {
            RecommendationResult orgResult = recommendLeaderByOrganization(currentUserOrgId);
            if (orgResult != null) {
                results.add(orgResult);
            }
        }

        // 2. 基于职责领域的匹配
        RecommendationResult domainResult = recommendLeaderByDomain(taskTitle);
        if (domainResult != null) {
            results.add(domainResult);
        }

        // 3. 基于文本相似度的匹配
        RecommendationResult similarityResult = recommendLeaderBySimilarity(taskTitle);
        if (similarityResult != null) {
            results.add(similarityResult);
        }

        return results;
    }

    /**
     * 获取所有可能的推荐结果（兼容旧版本接口）
     *
     * @param currentUserAccount 当前办理人账号
     * @param currentUserOrgId   当前办理人组织ID
     * @param taskTitle          任务标题
     * @return 所有可能的推荐结果列表
     */
    public List<RecommendationResult> getAllPossibleRecommendations(String currentUserAccount,
            String currentUserOrgId,
            String taskTitle) {
        // 默认使用基于组织关系的匹配
        return getAllPossibleRecommendations(currentUserAccount, currentUserOrgId, taskTitle, true);
    }
}
