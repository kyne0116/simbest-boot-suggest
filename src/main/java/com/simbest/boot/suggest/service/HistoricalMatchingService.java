package com.simbest.boot.suggest.service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.simbest.boot.suggest.config.AppConfig;
import com.simbest.boot.suggest.model.ApprovalHistory;
import com.simbest.boot.suggest.model.Leader;
import com.simbest.boot.suggest.model.RecommendationResult;
import com.simbest.boot.suggest.model.RecommendationType;
import com.simbest.boot.suggest.model.TaskPattern;
import com.simbest.boot.suggest.model.TextSimilarityUtil;
import com.simbest.boot.suggest.model.WorkflowDirection;
import com.simbest.boot.suggest.util.ChineseTokenizer;
import com.simbest.boot.suggest.util.TenantValidator;

import lombok.extern.slf4j.Slf4j;

/**
 * 历史批复匹配服务类
 * 提供基于历史批复记录的推荐功能
 */
@Service
@Slf4j
public class HistoricalMatchingService {

    @Autowired
    private ApprovalHistoryService approvalHistoryService;

    @Autowired
    private TaskPatternService taskPatternService;

    @Autowired
    private LeaderService leaderService;

    @Autowired
    private TenantValidator tenantValidator;

    @Autowired
    private AppConfig appConfig;

    // 使用AppConfig中的配置替代@Value注解
    private double similarityThreshold;
    private int maxResults;
    private double confidenceThreshold;
    private boolean usePatterns;

    /**
     * 初始化方法，从AppConfig中获取配置值
     */
    @Autowired
    public void init() {
        this.similarityThreshold = appConfig.getHistoricalMatchingSimilarityThreshold();
        this.maxResults = appConfig.getHistoricalMatchingMaxResults();
        this.confidenceThreshold = appConfig.getHistoricalMatchingConfidenceThreshold();
        this.usePatterns = appConfig.isHistoricalMatchingUsePatterns();

        log.info("历史匹配服务初始化完成，配置参数：similarityThreshold={}, maxResults={}, confidenceThreshold={}, usePatterns={}",
                similarityThreshold, maxResults, confidenceThreshold, usePatterns);
    }

    /**
     * 基于历史数据进行推荐
     *
     * @param taskTitle          任务标题
     * @param workflowDirection  工作流方向
     * @param userAccount        用户账号
     * @param candidateAccounts  候选账号列表
     * @param tenantCode         租户编码
     * @param recommendationType 推荐类型
     * @return 推荐结果
     * @throws java.io.IOException 如果初始化分词器或同义词表失败
     */
    public RecommendationResult recommendBasedOnHistory(
            String taskTitle,
            WorkflowDirection workflowDirection,
            String userAccount,
            String[] candidateAccounts,
            String tenantCode,
            RecommendationType recommendationType) throws java.io.IOException {

        log.debug(
                "基于历史数据进行推荐: taskTitle={}, workflowDirection={}, userAccount={}, tenantCode={}, recommendationType={}",
                taskTitle, workflowDirection, userAccount, tenantCode, recommendationType);

        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        // 首先尝试使用任务模式匹配
        if (usePatterns) {
            TaskPattern bestPattern = taskPatternService.findBestMatchingPattern(
                    tenantCode, taskTitle, workflowDirection, confidenceThreshold);

            if (bestPattern != null) {
                // 获取任务模式中的批复人权重
                Map<String, Double> approverWeights = bestPattern.getApproverWeights();

                // 过滤候选账号
                Map<String, Double> filteredWeights = new HashMap<>();
                for (Map.Entry<String, Double> entry : approverWeights.entrySet()) {
                    if (isInCandidateAccounts(entry.getKey(), candidateAccounts)) {
                        filteredWeights.put(entry.getKey(), entry.getValue());
                    }
                }

                if (!filteredWeights.isEmpty()) {
                    // 创建推荐结果
                    List<com.simbest.boot.suggest.model.LeaderDTO> leaders = new ArrayList<>();
                    double highestScore = 0.0;

                    for (Map.Entry<String, Double> entry : filteredWeights.entrySet()) {
                        String approverAccount = entry.getKey();
                        double weight = entry.getValue();

                        Leader leader = leaderService.getLeaderByAccountModel(approverAccount);
                        if (leader != null) {
                            com.simbest.boot.suggest.model.LeaderDTO leaderDTO = new com.simbest.boot.suggest.model.LeaderDTO();
                            leaderDTO.setSuggestAccount(approverAccount);
                            leaderDTO.setSuggestTruename(leader.getName());

                            String reason = "【历史批复模式匹配】系统通过分析历史批复记录，发现该任务与" +
                                    bestPattern.getPatternName() + "模式高度匹配。该模式下，此领导是最常处理此类任务的审批人";

                            leaderDTO.setReason(reason);
                            leaderDTO.setScore(weight);
                            leaderDTO.setConfidenceLevel(bestPattern.getConfidence());

                            leaders.add(leaderDTO);

                            if (weight > highestScore) {
                                highestScore = weight;
                            }
                        }
                    }

                    // 如果是单选模式，只保留得分最高的领导
                    if (recommendationType == RecommendationType.SINGLE && leaders.size() > 1) {
                        // 找出得分最高的领导
                        com.simbest.boot.suggest.model.LeaderDTO bestLeader = null;
                        double bestScore = 0.0;

                        for (com.simbest.boot.suggest.model.LeaderDTO leader : leaders) {
                            if (leader.getScore() > bestScore) {
                                bestScore = leader.getScore();
                                bestLeader = leader;
                            }
                        }

                        if (bestLeader != null) {
                            leaders.clear();
                            leaders.add(bestLeader);
                        }
                    }

                    if (!leaders.isEmpty()) {
                        return new RecommendationResult(leaders, "基于历史批复模式的推荐结果", highestScore);
                    }
                }
            }
        }

        // 如果任务模式匹配失败，尝试使用历史批复记录直接匹配
        return recommendByHistoryDirectly(tenantCode, taskTitle, userAccount, workflowDirection, candidateAccounts,
                recommendationType);
    }

    /**
     * 检查领导账号是否在候选账号列表中
     *
     * @param leaderAccount     领导账号
     * @param candidateAccounts 候选账号列表
     * @return 是否在候选账号列表中
     */
    private boolean isInCandidateAccounts(String leaderAccount, String[] candidateAccounts) {
        // 如果候选账号列表为空，则认为所有领导账号都在候选账号列表中
        if (candidateAccounts == null || candidateAccounts.length == 0) {
            return true;
        }

        // 如果领导账号为空，则认为不在候选账号列表中
        if (leaderAccount == null || leaderAccount.isEmpty()) {
            return false;
        }

        // 检查领导账号是否在候选账号列表中
        for (String candidateAccount : candidateAccounts) {
            if (leaderAccount.equals(candidateAccount)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 基于历史批复记录推荐领导
     *
     * @param tenantCode        租户编码
     * @param taskTitle         任务标题
     * @param initiatorAccount  发起人账号
     * @param workflowDirection 工作流方向
     * @return 推荐结果
     * @throws java.io.IOException 如果初始化分词器或同义词表失败
     */
    public RecommendationResult recommendLeaderByHistory(
            String tenantCode, String taskTitle, String initiatorAccount, WorkflowDirection workflowDirection)
            throws java.io.IOException {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        // 首先尝试使用任务模式匹配
        if (usePatterns) {
            RecommendationResult patternResult = recommendByTaskPattern(
                    tenantCode, taskTitle, workflowDirection);
            if (patternResult != null && !patternResult.getLeaders().isEmpty()) {
                log.debug("【匹配成功】基于任务模式匹配成功，推荐领导: {}", patternResult.getLeaders().get(0).getSuggestAccount());
                return patternResult;
            }
        }

        // 如果任务模式匹配失败，尝试使用历史批复记录直接匹配
        RecommendationResult historyResult = recommendByHistoryDirectly(
                tenantCode, taskTitle, initiatorAccount, workflowDirection, null, RecommendationType.SINGLE);
        if (historyResult != null && !historyResult.getLeaders().isEmpty()) {
            log.debug("【匹配成功】基于历史批复记录直接匹配成功，推荐领导: {}", historyResult.getLeaders().get(0).getSuggestAccount());
            return historyResult;
        }

        log.debug("【匹配失败】基于历史批复记录匹配失败，没有找到合适的推荐结果");
        return null;
    }

    /**
     * 基于任务模式推荐领导
     *
     * @param tenantCode        租户编码
     * @param taskTitle         任务标题
     * @param workflowDirection 工作流方向
     * @return 推荐结果
     * @throws java.io.IOException 如果初始化分词器或同义词表失败
     */
    private RecommendationResult recommendByTaskPattern(
            String tenantCode, String taskTitle, WorkflowDirection workflowDirection) throws java.io.IOException {
        try {
            // 查找最匹配的任务模式
            TaskPattern bestPattern = taskPatternService.findBestMatchingPattern(
                    tenantCode, taskTitle, workflowDirection, confidenceThreshold);

            if (bestPattern == null) {
                log.debug("【匹配失败】未找到匹配的任务模式");
                return null;
            }

            // 检查批复人权重映射是否为null
            Map<String, Double> approverWeights = bestPattern.getApproverWeights();
            if (approverWeights == null || approverWeights.isEmpty()) {
                log.debug("【匹配失败】任务模式 {} 的批复人权重映射为null或为空", bestPattern.getId());
                return null;
            }

            // 获取最佳批复人账号
            String bestApproverAccount = bestPattern.getBestApproverAccount();
            if (bestApproverAccount == null) {
                log.debug("【匹配失败】任务模式中没有批复人信息");
                return null;
            }

            // 获取领导信息
            Leader leader = leaderService.getLeaderByAccountModel(bestApproverAccount);
            if (leader == null) {
                log.debug("【匹配失败】未找到批复人对应的领导信息: {}", bestApproverAccount);
                return null;
            }

            // 更新任务模式的匹配次数
            taskPatternService.updatePatternMatchingInfo(bestPattern.getId(), bestApproverAccount, 0.1);

            // 创建推荐结果
            double score = bestPattern.getConfidence();
            String reason = "【历史批复模式匹配】系统通过分析历史批复记录，发现该任务与" +
                    bestPattern.getPatternName() + "模式高度匹配。该模式下，此领导是最常处理此类任务的审批人";

            // 创建领导DTO对象
            com.simbest.boot.suggest.model.LeaderDTO leaderDTO = new com.simbest.boot.suggest.model.LeaderDTO();
            leaderDTO.setSuggestAccount(bestApproverAccount);
            leaderDTO.setSuggestTruename(leader.getName());
            leaderDTO.setReason(reason);
            leaderDTO.setScore(score);
            leaderDTO.setConfidenceLevel(score);

            // 创建包含单个领导的推荐结果
            List<com.simbest.boot.suggest.model.LeaderDTO> leaders = new ArrayList<>();
            leaders.add(leaderDTO);

            return new RecommendationResult(leaders, reason, score);

        } catch (Exception e) {
            log.error("【匹配失败】基于任务模式推荐领导时发生异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 基于历史批复记录直接推荐领导
     *
     * @param tenantCode         租户编码
     * @param taskTitle          任务标题
     * @param initiatorAccount   发起人账号
     * @param workflowDirection  工作流方向
     * @param candidateAccounts  候选账号列表
     * @param recommendationType 推荐类型
     * @return 推荐结果
     * @throws java.io.IOException 如果初始化分词器或同义词表失败
     */
    private RecommendationResult recommendByHistoryDirectly(
            String tenantCode, String taskTitle, String initiatorAccount, WorkflowDirection workflowDirection,
            String[] candidateAccounts, RecommendationType recommendationType) throws java.io.IOException {
        // 对任务标题进行分词
        List<String> taskTokens = ChineseTokenizer.tokenize(taskTitle);

        // 查找相关的历史批复记录
        List<ApprovalHistory> histories;

        // 如果有发起人信息，优先考虑相同发起人的历史记录
        if (initiatorAccount != null && !initiatorAccount.isEmpty()) {
            // 查找相同发起人和工作流方向的历史记录
            histories = approvalHistoryService.getApprovalHistoriesByInitiatorAndDirection(
                    tenantCode, initiatorAccount, workflowDirection);

            // 如果找到了足够的记录，则使用这些记录
            if (histories.size() >= 3) {
                RecommendationResult result = processHistoricalRecords(
                        histories, taskTitle, taskTokens, true, candidateAccounts, recommendationType);
                if (result != null) {
                    return result;
                }
            }
        }

        // 如果没有找到足够的相同发起人的记录，则查找相同工作流方向的所有记录
        histories = approvalHistoryService.getApprovalHistoriesByWorkflowDirection(
                tenantCode, workflowDirection);

        return processHistoricalRecords(histories, taskTitle, taskTokens, false, candidateAccounts, recommendationType);
    }

    /**
     * 处理历史批复记录，计算相似度并生成推荐结果
     *
     * @param histories          历史批复记录列表
     * @param taskTitle          任务标题
     * @param taskTokens         任务标题分词结果
     * @param isSameInitiator    是否是相同发起人
     * @param candidateAccounts  候选账号列表
     * @param recommendationType 推荐类型
     * @return 推荐结果
     * @throws java.io.IOException 如果初始化分词器或同义词表失败
     */
    private RecommendationResult processHistoricalRecords(
            List<ApprovalHistory> histories, String taskTitle, List<String> taskTokens, boolean isSameInitiator,
            String[] candidateAccounts, RecommendationType recommendationType) throws java.io.IOException {
        if (histories.isEmpty()) {
            return null;
        }

        // 计算每条历史记录与当前任务的相似度
        List<Map.Entry<ApprovalHistory, Double>> similarityScores = new ArrayList<>();

        for (ApprovalHistory history : histories) {
            double similarity = calculateSimilarity(taskTitle, taskTokens, history.getTaskTitle());
            if (similarity >= similarityThreshold) {
                similarityScores.add(new AbstractMap.SimpleEntry<>(history, similarity));
            }
        }

        // 如果没有找到相似度足够高的记录，则返回null
        if (similarityScores.isEmpty()) {
            return null;
        }

        // 按相似度降序排序
        Collections.sort(similarityScores,
                (e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

        // 取前N条记录
        List<Map.Entry<ApprovalHistory, Double>> topResults = similarityScores.stream()
                .limit(maxResults)
                .collect(Collectors.toList());

        // 统计每个审批人的出现次数和平均相似度
        Map<String, Integer> approverCounts = new HashMap<>();
        Map<String, Double> approverScores = new HashMap<>();

        for (Map.Entry<ApprovalHistory, Double> entry : topResults) {
            ApprovalHistory history = entry.getKey();
            double similarity = entry.getValue();

            String approverAccount = history.getApproverAccount();

            // 检查是否在候选账号列表中
            if (isInCandidateAccounts(approverAccount, candidateAccounts)) {
                approverCounts.put(approverAccount, approverCounts.getOrDefault(approverAccount, 0) + 1);

                double currentScore = approverScores.getOrDefault(approverAccount, 0.0);
                approverScores.put(approverAccount, currentScore + similarity);
            }
        }

        // 计算每个审批人的综合得分
        Map<String, Double> approverFinalScores = new HashMap<>();

        for (String approverAccount : approverCounts.keySet()) {
            int count = approverCounts.get(approverAccount);
            double totalScore = approverScores.get(approverAccount);
            double avgScore = totalScore / count;

            // 综合得分 = 平均相似度 * (出现次数 / 最大可能出现次数)
            double countFactor = (double) count / Math.min(topResults.size(), 5);
            double finalScore = avgScore * (0.7 + 0.3 * countFactor);

            approverFinalScores.put(approverAccount, finalScore);
        }

        // 找出得分最高的审批人
        String bestApproverAccount = null;
        double bestScore = 0.0;

        for (Map.Entry<String, Double> entry : approverFinalScores.entrySet()) {
            if (entry.getValue() > bestScore) {
                bestScore = entry.getValue();
                bestApproverAccount = entry.getKey();
            }
        }

        // 如果没有找到最佳审批人，则返回null
        if (bestApproverAccount == null) {
            return null;
        }

        // 获取领导信息
        Leader leader = leaderService.getLeaderByAccountModel(bestApproverAccount);
        if (leader == null) {
            return null;
        }

        // 创建推荐结果
        String reason;
        if (isSameInitiator) {
            reason = "【历史批复记录匹配】系统分析了您过去的审批历史，发现该任务与您之前提交的任务相似，" +
                    "该领导曾多次处理过类似的任务";
        } else {
            reason = "【历史批复记录匹配】系统分析了历史审批记录，发现该任务与过去处理过的任务相似，" +
                    "该领导在处理此类任务时具有丰富经验";
        }

        // 创建领导DTO对象
        com.simbest.boot.suggest.model.LeaderDTO leaderDTO = new com.simbest.boot.suggest.model.LeaderDTO();
        leaderDTO.setSuggestAccount(bestApproverAccount);
        leaderDTO.setSuggestTruename(leader.getName());
        leaderDTO.setReason(reason);
        leaderDTO.setScore(bestScore);
        leaderDTO.setConfidenceLevel(bestScore);

        // 创建包含单个领导的推荐结果
        List<com.simbest.boot.suggest.model.LeaderDTO> leaders = new ArrayList<>();
        leaders.add(leaderDTO);

        // 如果是多选模式，并且有其他得分较高的审批人，也添加到推荐结果中
        if (recommendationType == RecommendationType.MULTIPLE) {
            for (Map.Entry<String, Double> entry : approverFinalScores.entrySet()) {
                String approverAccount = entry.getKey();
                double score = entry.getValue();

                // 如果不是最佳审批人，但得分足够高，也添加到推荐结果中
                if (!approverAccount.equals(bestApproverAccount) && score >= bestScore * 0.8) {
                    Leader otherLeader = leaderService.getLeaderByAccountModel(approverAccount);
                    if (otherLeader != null) {
                        com.simbest.boot.suggest.model.LeaderDTO otherLeaderDTO = new com.simbest.boot.suggest.model.LeaderDTO();
                        otherLeaderDTO.setSuggestAccount(approverAccount);
                        otherLeaderDTO.setSuggestTruename(otherLeader.getName());
                        otherLeaderDTO.setReason(reason);
                        otherLeaderDTO.setScore(score);
                        otherLeaderDTO.setConfidenceLevel(score);
                        leaders.add(otherLeaderDTO);
                    }
                }
            }
        }

        return new RecommendationResult(leaders, reason, bestScore);
    }

    /**
     * 计算两个任务标题的相似度
     *
     * @param taskTitle1  任务标题1
     * @param taskTokens1 任务标题1的分词结果
     * @param taskTitle2  任务标题2
     * @return 相似度（0-1之间）
     * @throws java.io.IOException 如果初始化分词器或同义词表失败
     */
    private double calculateSimilarity(String taskTitle1, List<String> taskTokens1, String taskTitle2)
            throws java.io.IOException {
        // 使用文本相似度工具计算相似度
        double textSimilarity = TextSimilarityUtil.calculateFinalSimilarity(taskTitle1, taskTitle2);

        // 分词相似度
        List<String> taskTokens2 = ChineseTokenizer.tokenize(taskTitle2);
        double tokenSimilarity = calculateTokenSimilarity(taskTokens1, taskTokens2);

        // 综合相似度
        return 0.7 * textSimilarity + 0.3 * tokenSimilarity;
    }

    /**
     * 计算两个分词结果的相似度
     *
     * @param tokens1 分词结果1
     * @param tokens2 分词结果2
     * @return 相似度（0-1之间）
     */
    private double calculateTokenSimilarity(List<String> tokens1, List<String> tokens2) {
        if (tokens1.isEmpty() || tokens2.isEmpty()) {
            return 0.0;
        }

        // 计算交集大小
        int intersection = 0;
        for (String token : tokens1) {
            if (tokens2.contains(token)) {
                intersection++;
            }
        }

        // 计算Jaccard相似度
        int union = tokens1.size() + tokens2.size() - intersection;
        return (double) intersection / union;
    }
}
