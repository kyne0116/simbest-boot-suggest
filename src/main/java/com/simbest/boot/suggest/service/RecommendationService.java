package com.simbest.boot.suggest.service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import com.simbest.boot.suggest.model.Leader;
import com.simbest.boot.suggest.model.Organization;
import com.simbest.boot.suggest.model.RecommendationResult;
import com.simbest.boot.suggest.model.ResponsibilityDomain;
import com.simbest.boot.suggest.model.TextSimilarityUtil;
import com.simbest.boot.suggest.model.WorkflowDirection;
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
     * @param workflowDirection  工作流方向：向下指派(DOWNWARD)、向上请示(UPWARD)或同级协办(PARALLEL)
     * @param useOrg             是否使用组织关系
     * @param candidateAccounts  候选账号列表，如果提供，则推荐结果必须在此列表中
     * @return 推荐结果
     */
    public RecommendationResult recommendLeader(String currentUserAccount,
            String currentUserOrgId,
            String taskTitle,
            WorkflowDirection workflowDirection,
            boolean useOrg,
            String[] candidateAccounts) {
        // 如果没有传入orgId，但传入了userAccount，尝试根据userAccount查找组织
        if (useOrg && (currentUserOrgId == null || currentUserOrgId.isEmpty()) &&
                currentUserAccount != null && !currentUserAccount.isEmpty()) {
            // 查找用户作为主管的组织
            List<Organization> userOrgs = organizationService.getOrganizationsAsMainLeader(currentUserAccount);
            if (!userOrgs.isEmpty()) {
                currentUserOrgId = userOrgs.get(0).getOrgId();
                System.out.println("根据用户账号" + currentUserAccount + "找到组织ID: " + currentUserOrgId);
            }
        }

        System.out.println("工作流方向: " + workflowDirection);

        // 基于工作流方向进行推荐
        RecommendationResult result = null;

        // 1. 基于组织关系的匹配（如果useOrg为true且orgId有值时）
        if (useOrg && currentUserOrgId != null && !currentUserOrgId.isEmpty()) {
            Organization org = organizationService.getOrganizationById(currentUserOrgId);
            if (org != null) {
                switch (workflowDirection) {
                    case DOWNWARD: // 向下指派：推荐同级组织分管领导
                        if (currentUserAccount != null && !currentUserAccount.isEmpty() &&
                                currentUserAccount.equals(org.getMainLeaderAccount()) &&
                                org.getDeputyLeaderAccounts() != null && !org.getDeputyLeaderAccounts().isEmpty()) {
                            // 如果当前用户是主管领导，则根据任务标题匹配最合适的分管领导
                            String bestDeputyLeaderAccount = organizationService.findBestDeputyLeaderByTaskTitle(org,
                                    taskTitle);
                            if (bestDeputyLeaderAccount != null) {
                                Leader deputyLeader = leaderService.getLeaderByAccount(bestDeputyLeaderAccount);
                                if (deputyLeader != null) {
                                    String reason = "向下指派：当前用户是" + org.getOrgName() +
                                            "的主管领导，推荐该组织的分管领导，负责与任务相关的业务领域";
                                    result = new RecommendationResult(deputyLeader.getAccount(),
                                            deputyLeader.getName(), reason, 0.9);
                                }
                            }
                        }
                        break;

                    case UPWARD: // 向上请示：直接推荐该组织的上级领导账号
                        String superiorLeaderAccount = org.getSuperiorLeaderAccount();
                        if (superiorLeaderAccount != null && !superiorLeaderAccount.isEmpty()) {
                            Leader superiorLeader = leaderService.getLeaderByAccount(superiorLeaderAccount);
                            if (superiorLeader != null) {
                                String reason = "向上请示：直接推荐" + org.getOrgName() + "的上级领导";
                                result = new RecommendationResult(superiorLeader.getAccount(),
                                        superiorLeader.getName(), reason, 1.0);
                            }
                        }
                        break;

                    case PARALLEL: // 同级协办：推荐同级组织的其他领导
                        // 如果当前用户是分管领导，则推荐其他分管领导
                        if (currentUserAccount != null && !currentUserAccount.isEmpty() &&
                                org.getDeputyLeaderAccounts() != null &&
                                org.getDeputyLeaderAccounts().contains(currentUserAccount) &&
                                org.getDeputyLeaderAccounts().size() > 1) {

                            // 找到除当前用户以外的其他分管领导
                            for (String deputyAccount : org.getDeputyLeaderAccounts()) {
                                if (!deputyAccount.equals(currentUserAccount)) {
                                    Leader deputyLeader = leaderService.getLeaderByAccount(deputyAccount);
                                    if (deputyLeader != null) {
                                        String reason = "同级协办：当前用户是" + org.getOrgName() +
                                                "的分管领导，推荐该组织的其他分管领导";
                                        result = new RecommendationResult(deputyAccount,
                                                deputyLeader.getName(), reason, 0.8);
                                        break;
                                    }
                                }
                            }
                        }
                        // 如果没有找到其他分管领导，则推荐主管领导
                        if (result == null) {
                            String mainLeaderAccount = org.getMainLeaderAccount();
                            if (mainLeaderAccount != null && !mainLeaderAccount.isEmpty() &&
                                    !mainLeaderAccount.equals(currentUserAccount)) {
                                Leader mainLeader = leaderService.getLeaderByAccount(mainLeaderAccount);
                                if (mainLeader != null) {
                                    String reason = "同级协办：推荐" + org.getOrgName() + "的主管领导";
                                    result = new RecommendationResult(mainLeader.getAccount(),
                                            mainLeader.getName(), reason, 0.7);
                                }
                            }
                        }
                        break;
                }
            }
        }

        // 如果基于组织关系找到了推荐结果
        if (result != null) {
            // 检查是否在候选账号列表中
            if (isInCandidateAccounts(result.getLeaderAccount(), candidateAccounts)) {
                return result;
            } else {
                System.out.println("基于组织关系的推荐结果不在候选账号列表中，继续匹配");
            }
        }

        // 2. 基于职责领域的匹配
        RecommendationResult domainResult = recommendLeaderByDomain(taskTitle, currentUserAccount);
        if (domainResult != null) {
            // 检查是否在候选账号列表中
            if (isInCandidateAccounts(domainResult.getLeaderAccount(), candidateAccounts)) {
                return domainResult;
            } else {
                System.out.println("基于职责领域的推荐结果不在候选账号列表中，继续匹配");
            }
        }

        // 3. 基于文本相似度的匹配
        RecommendationResult similarityResult = recommendLeaderBySimilarity(taskTitle, currentUserAccount);
        if (similarityResult != null) {
            // 检查是否在候选账号列表中
            if (isInCandidateAccounts(similarityResult.getLeaderAccount(), candidateAccounts)) {
                return similarityResult;
            } else {
                System.out.println("基于文本相似度的推荐结果不在候选账号列表中");
                return null; // 所有推荐结果都不在候选账号列表中，返回null
            }
        }

        return null;
    }

    /**
     * 推荐领导账号
     *
     * @param currentUserAccount 当前办理人账号
     * @param currentUserOrgId   当前办理人组织ID
     * @param taskTitle          任务标题
     * @param workflowDirection  工作流方向：向下指派(DOWNWARD)、向上请示(UPWARD)或同级协办(PARALLEL)
     * @param useOrg             是否使用组织关系
     * @return 推荐结果
     */
    public RecommendationResult recommendLeader(String currentUserAccount,
            String currentUserOrgId,
            String taskTitle,
            WorkflowDirection workflowDirection,
            boolean useOrg) {
        // 调用带候选账号的方法，传入null表示不限制候选账号
        return recommendLeader(currentUserAccount, currentUserOrgId, taskTitle, workflowDirection, useOrg, null);
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
        // 默认使用组织关系，不限制候选账号，默认向上请示
        return recommendLeader(currentUserAccount, currentUserOrgId, taskTitle, WorkflowDirection.UPWARD, true, null);
    }

    /**
     * 基于组织关系推荐领导账号
     *
     * @param orgId       组织ID
     * @param userAccount 当前用户账号
     * @param taskTitle   任务标题
     * @return 推荐结果
     */
    public RecommendationResult recommendLeaderByOrganization(String orgId, String userAccount, String taskTitle) {
        if (orgId == null || orgId.isEmpty()) {
            return null;
        }

        // 获取组织信息
        Organization org = organizationService.getOrganizationById(orgId);
        if (org == null) {
            return null;
        }

        // 情况1: 如果当前用户是组织的主管领导，则根据任务标题匹配最合适的分管领导
        if (userAccount != null && !userAccount.isEmpty() &&
                userAccount.equals(org.getMainLeaderAccount()) &&
                org.getDeputyLeaderAccounts() != null && !org.getDeputyLeaderAccounts().isEmpty()) {

            // 根据任务标题找到最合适的分管领导
            String bestDeputyLeaderAccount = organizationService.findBestDeputyLeaderByTaskTitle(org, taskTitle);
            if (bestDeputyLeaderAccount != null) {
                Leader deputyLeader = leaderService.getLeaderByAccount(bestDeputyLeaderAccount);
                if (deputyLeader != null) {
                    String reason = "基于组织关系和任务内容匹配：当前用户是" + org.getOrgName() +
                            "的主管领导，推荐该组织的分管领导，负责与任务相关的业务领域";
                    return new RecommendationResult(deputyLeader.getAccount(),
                            deputyLeader.getName(), reason, 0.9);
                }
            }

            // 如果没有找到合适的分管领导，则推荐上级领导
            String superiorLeaderAccount = org.getSuperiorLeaderAccount();
            if (superiorLeaderAccount != null && !superiorLeaderAccount.isEmpty()) {
                Leader superiorLeader = leaderService.getLeaderByAccount(superiorLeaderAccount);
                if (superiorLeader != null) {
                    String reason = "基于组织关系匹配：当前用户是" + org.getOrgName() +
                            "的主管领导，推荐该组织的上级领导";
                    return new RecommendationResult(superiorLeader.getAccount(),
                            superiorLeader.getName(), reason, 0.8);
                }
            }
            return null;
        }

        // 情况2: 如果当前用户是组织的分管领导之一，则推荐该组织的主管领导
        if (userAccount != null && !userAccount.isEmpty() &&
                org.getDeputyLeaderAccounts() != null &&
                org.getDeputyLeaderAccounts().contains(userAccount)) {

            String mainLeaderAccount = org.getMainLeaderAccount();
            if (mainLeaderAccount != null && !mainLeaderAccount.isEmpty()) {
                Leader mainLeader = leaderService.getLeaderByAccount(mainLeaderAccount);
                if (mainLeader != null) {
                    String reason = "基于组织关系匹配：当前用户是" + org.getOrgName() +
                            "的分管领导，推荐该组织的主管领导";
                    return new RecommendationResult(mainLeader.getAccount(),
                            mainLeader.getName(), reason, 1.0);
                }
            }
            return null;
        }

        // 情况3: 推荐主管领导
        String mainLeaderAccount = org.getMainLeaderAccount();
        if (mainLeaderAccount != null && !mainLeaderAccount.isEmpty()) {
            Leader mainLeader = leaderService.getLeaderByAccount(mainLeaderAccount);
            if (mainLeader != null) {
                String reason = "基于组织关系匹配：推荐" + org.getOrgName() + "的主管领导";
                return new RecommendationResult(mainLeader.getAccount(),
                        mainLeader.getName(), reason, 1.0);
            }
        }

        return null;
    }

    /**
     * 基于组织关系推荐领导账号（兼容旧版本接口）
     *
     * @param orgId 组织ID
     * @return 推荐结果
     */
    public RecommendationResult recommendLeaderByOrganization(String orgId) {
        return recommendLeaderByOrganization(orgId, null, null);
    }

    /**
     * 基于组织关系推荐领导账号（兼容旧版本接口）
     *
     * @param orgId       组织ID
     * @param userAccount 当前用户账号
     * @return 推荐结果
     */
    public RecommendationResult recommendLeaderByOrganization(String orgId, String userAccount) {
        return recommendLeaderByOrganization(orgId, userAccount, null);
    }

    /**
     * 基于职责领域推荐领导账号
     *
     * @param taskTitle   任务标题
     * @param userAccount 当前用户账号
     * @return 推荐结果
     */
    public RecommendationResult recommendLeaderByDomain(String taskTitle, String userAccount) {
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

        // 如果当前用户就是该领导，则需要找到该领导的上级
        if (userAccount != null && !userAccount.isEmpty() && userAccount.equals(leaderAccount)) {
            // 获取该领导作为主管的所有组织
            List<Organization> leaderOrgs = organizationService.getOrganizationsAsMainLeader(leaderAccount);
            if (!leaderOrgs.isEmpty()) {
                // 获取第一个组织的上级领导
                String superiorLeaderAccount = leaderOrgs.get(0).getSuperiorLeaderAccount();
                if (superiorLeaderAccount != null && !superiorLeaderAccount.isEmpty()) {
                    leaderAccount = superiorLeaderAccount;
                }
            }
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
     * 基于职责领域推荐领导账号（兼容旧版本接口）
     *
     * @param taskTitle 任务标题
     * @return 推荐结果
     */
    public RecommendationResult recommendLeaderByDomain(String taskTitle) {
        return recommendLeaderByDomain(taskTitle, null);
    }

    /**
     * 基于文本相似度推荐领导账号
     *
     * @param taskTitle   任务标题
     * @param userAccount 当前用户账号
     * @return 推荐结果
     */
    public RecommendationResult recommendLeaderBySimilarity(String taskTitle, String userAccount) {
        if (taskTitle == null || taskTitle.isEmpty()) {
            return null;
        }

        String bestLeaderAccount = null;
        double bestScore = 0.0;

        // 遍历所有领导，计算文本相似度
        for (Leader leader : leaderService.getAllLeaders()) {
            // 如果当前用户就是该领导，则跳过
            if (userAccount != null && !userAccount.isEmpty() &&
                    userAccount.equals(leader.getAccount())) {
                continue;
            }

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
     * 基于文本相似度推荐领导账号（兼容旧版本接口）
     *
     * @param taskTitle 任务标题
     * @return 推荐结果
     */
    public RecommendationResult recommendLeaderBySimilarity(String taskTitle) {
        return recommendLeaderBySimilarity(taskTitle, null);
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
     * 检查账号是否在候选账号列表中
     *
     * @param account           要检查的账号
     * @param candidateAccounts 候选账号列表
     * @return 如果在候选账号列表中或候选账号列表为空，返回true；否则返回false
     */
    private boolean isInCandidateAccounts(String account, String[] candidateAccounts) {
        // 如果候选账号列表为空，则不限制
        if (candidateAccounts == null || candidateAccounts.length == 0) {
            return true;
        }

        // 检查账号是否在候选账号列表中
        for (String candidateAccount : candidateAccounts) {
            if (candidateAccount.equals(account)) {
                return true;
            }
        }

        return false;
    }

}
