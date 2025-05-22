package com.simbest.boot.suggest.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.simbest.boot.suggest.model.Organization;
import com.simbest.boot.suggest.util.ChineseTokenizer;
import com.simbest.boot.suggest.util.SynonymManagerAdapter;

import lombok.extern.slf4j.Slf4j;

/**
 * 组织关键字匹配服务
 * 用于计算组织与任务标题的匹配度
 */
@Service
@Slf4j
public class OrganizationKeywordMatchService {

    @Autowired
    private OrganizationService organizationService;

    /**
     * 计算组织与任务标题的匹配度
     *
     * @param organization 组织
     * @param taskTitle    任务标题
     * @return 匹配度分数
     * @throws IOException 如果分词器初始化失败
     */
    public double calculateOrganizationMatchScore(Organization organization, String taskTitle) throws IOException {
        if (organization == null || taskTitle == null || taskTitle.isEmpty() ||
                organization.getKeywords() == null || organization.getKeywords().isEmpty()) {
            return 0.0;
        }

        // 分词处理任务标题
        List<String> taskTokens = ChineseTokenizer.tokenize(taskTitle);

        // 计算匹配分数
        double score = calculateKeywordMatchScore(organization.getKeywords(), organization.getKeywordWeights(),
                taskTokens,
                taskTitle);

        // 应用组织层级权重
        // 部门级别权重高于科室级别
        if ("DEPARTMENT".equals(organization.getOrgType())) {
            score *= 1.2; // 部门级别加权20%
        } else if ("TEAM".equals(organization.getOrgType()) || "OFFICE".equals(organization.getOrgType())) {
            score *= 0.9; // 科室级别减权10%
        }

        // 确保分数不超过1.0
        return Math.min(score, 1.0);
    }

    /**
     * 计算关键字匹配分数
     *
     * @param keywords       关键字列表
     * @param keywordWeights 关键字权重列表
     * @param taskTokens     任务标题分词结果
     * @param taskTitle      原始任务标题
     * @return 匹配分数
     */
    private double calculateKeywordMatchScore(List<String> keywords, List<Double> keywordWeights,
            List<String> taskTokens, String taskTitle) {
        if (keywords == null || keywords.isEmpty() || taskTokens == null || taskTokens.isEmpty()) {
            return 0.0;
        }

        int matchedKeywordsCount = 0;
        double weightedMatchScore = 0.0;
        double totalWeight = 0.0;

        // 创建权重映射
        Map<String, Double> weightMap = new HashMap<>();
        for (int i = 0; i < keywords.size(); i++) {
            double weight = (i < keywordWeights.size()) ? keywordWeights.get(i) : 1.0;
            weightMap.put(keywords.get(i), weight);
            totalWeight += weight;
        }

        // 计算匹配的关键词
        for (String keyword : keywords) {
            if (keyword == null) {
                continue;
            }

            // 直接匹配
            boolean directMatch = taskTitle.contains(keyword);

            // 分词匹配
            boolean tokenMatch = taskTokens.contains(keyword);

            // 同义词匹配
            boolean synonymMatch = false;
            try {
                for (String token : taskTokens) {
                    if (SynonymManagerAdapter.areSynonyms(keyword, token)) {
                        synonymMatch = true;
                        break;
                    }
                }
            } catch (Exception e) {
                log.warn("获取同义词时出错: {}", e.getMessage());
            }

            // 如果任一方式匹配成功
            if (directMatch || tokenMatch || synonymMatch) {
                matchedKeywordsCount++;
                double weight = weightMap.getOrDefault(keyword, 1.0);
                weightedMatchScore += weight;
            }
        }

        // 计算匹配率
        double keywordCountRatio = (double) matchedKeywordsCount / keywords.size();

        // 计算加权匹配率
        double weightedRatio = (totalWeight > 0.0) ? weightedMatchScore / totalWeight : 0.0;

        // 综合评分 (70% 关键词匹配率 + 30% 加权匹配率)
        double finalScore = 0.7 * keywordCountRatio + 0.3 * weightedRatio;

        // 注意：由于方法参数中没有organization对象，此处不再应用组织层级权重
        // 组织层级权重将在calculateOrganizationMatchScore方法中应用

        // 确保分数不超过1.0
        return Math.min(finalScore, 1.0);
    }

    /**
     * 根据任务标题找到最匹配的组织
     *
     * @param taskTitle 任务标题
     * @return 最匹配的组织
     * @throws IOException 如果分词器初始化失败
     */
    public Organization findBestMatchingOrganization(String taskTitle) throws IOException {
        if (taskTitle == null || taskTitle.isEmpty()) {
            return null;
        }

        List<Organization> allOrganizations = organizationService.getAllOrganizations();
        Organization bestOrganization = null;
        double bestScore = 0.0;

        for (Organization org : allOrganizations) {
            double score = calculateOrganizationMatchScore(org, taskTitle);
            if (score > bestScore) {
                bestScore = score;
                bestOrganization = org;
            }
        }

        // 如果最佳匹配分数太低，则返回null
        if (bestScore < 0.3) {
            return null;
        }

        return bestOrganization;
    }

    /**
     * 获取所有组织的匹配分数
     *
     * @param taskTitle 任务标题
     * @return 组织ID到匹配分数的映射
     * @throws IOException 如果分词器初始化失败
     */
    public Map<String, Double> getAllOrganizationMatchScores(String taskTitle) throws IOException {
        if (taskTitle == null || taskTitle.isEmpty()) {
            return new HashMap<>();
        }

        List<Organization> allOrganizations = organizationService.getAllOrganizations();
        Map<String, Double> scoreMap = new HashMap<>();

        for (Organization org : allOrganizations) {
            double score = calculateOrganizationMatchScore(org, taskTitle);
            scoreMap.put(org.getOrgId(), score);
        }

        return scoreMap;
    }
}
