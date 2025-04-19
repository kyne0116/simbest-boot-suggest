package com.simbest.boot.suggest.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.simbest.boot.suggest.util.ChineseTokenizer;
import com.simbest.boot.suggest.util.SynonymManager;

/**
 * 职责领域类
 * 表示一个特定的业务领域及其关键词和负责人
 */
public class ResponsibilityDomain {
    private String domainId; // 领域ID
    private String domainName; // 领域名称
    private String responsiblePerson; // 负责人
    private List<String> keywords; // 关键词列表
    private String description; // 领域描述

    /**
     * 构造函数
     *
     * @param domainId          领域ID
     * @param domainName        领域名称
     * @param responsiblePerson 负责人
     * @param description       领域描述
     */
    public ResponsibilityDomain(String domainId, String domainName, String responsiblePerson, String description) {
        this.domainId = domainId;
        this.domainName = domainName;
        this.responsiblePerson = responsiblePerson;
        this.description = description;
        this.keywords = new ArrayList<>();
    }

    /**
     * 构造函数
     *
     * @param domainName        领域名称
     * @param responsiblePerson 负责人
     * @param description       领域描述
     */
    public ResponsibilityDomain(String domainName, String responsiblePerson, String description) {
        this.domainId = "domain_" + System.currentTimeMillis(); // 生成一个临时ID
        this.domainName = domainName;
        this.responsiblePerson = responsiblePerson;
        this.description = description;
        this.keywords = new ArrayList<>();
    }

    /**
     * 添加关键词
     *
     * @param keyword 关键词
     */
    public void addKeyword(String keyword) {
        keywords.add(keyword);
    }

    /**
     * 添加多个关键词
     *
     * @param keywords 关键词列表
     */
    public void addKeywords(List<String> keywords) {
        this.keywords.addAll(keywords);
    }

    /**
     * 获取领域ID
     *
     * @return 领域ID
     */
    public String getDomainId() {
        return domainId;
    }

    /**
     * 设置领域ID
     *
     * @param domainId 领域ID
     */
    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    /**
     * 获取领域名称
     *
     * @return 领域名称
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * 设置领域名称
     *
     * @param domainName 领域名称
     */
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    /**
     * 获取负责人
     *
     * @return 负责人
     */
    public String getResponsiblePerson() {
        return responsiblePerson;
    }

    /**
     * 设置负责人
     *
     * @param responsiblePerson 负责人
     */
    public void setResponsiblePerson(String responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    /**
     * 获取关键词列表
     *
     * @return 关键词列表
     */
    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * 设置关键词列表
     *
     * @param keywords 关键词列表
     */
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * 获取领域描述
     *
     * @return 领域描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置领域描述
     *
     * @param description 领域描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 计算文本与该领域的匹配度
     * 使用改进的关键词匹配算法
     *
     * @param text 输入文本
     * @return 匹配度分数（0-1之间）
     */
    public double calculateMatchScore(String text) {
        if (text == null || text.isEmpty() || keywords.isEmpty()) {
            return 0.0;
        }

        // 分词处理输入文本
        List<String> textTokens = ChineseTokenizer.tokenize(text);

        // 初始化关键词权重
        Map<String, Double> keywordWeights = new HashMap<>();
        for (String keyword : keywords) {
            // 关键词长度越长，权重越高
            double weight = 1.0 + (0.1 * Math.min(keyword.length(), 10));
            keywordWeights.put(keyword, weight);
        }

        int matchedKeywordsCount = 0;
        double weightedMatchScore = 0.0;

        // 计算匹配的关键词
        for (String keyword : keywords) {
            // 直接匹配
            boolean directMatch = text.contains(keyword);

            // 分词匹配
            boolean tokenMatch = textTokens.contains(keyword);

            // 同义词匹配
            boolean synonymMatch = false;
            Set<String> synonyms = SynonymManager.getSynonyms(keyword);
            for (String token : textTokens) {
                if (synonyms.contains(token)) {
                    synonymMatch = true;
                    break;
                }
            }

            // 如果任一方式匹配成功
            if (directMatch || tokenMatch || synonymMatch) {
                matchedKeywordsCount++;
                double matchWeight = keywordWeights.get(keyword);

                // 如果是精确匹配，给予更高权重
                if (tokenMatch) {
                    matchWeight *= 1.2;
                }

                // 如果是同义词匹配，给予较低权重
                if (synonymMatch && !directMatch && !tokenMatch) {
                    matchWeight *= 0.8;
                }

                weightedMatchScore += matchWeight;
            }
        }

        // 计算匹配度
        double keywordCountRatio = (double) matchedKeywordsCount / keywords.size();

        // 计算加权分数
        double totalWeight = 0.0;
        for (double weight : keywordWeights.values()) {
            totalWeight += weight;
        }
        double weightedRatio = weightedMatchScore / totalWeight;

        // 综合评分：60%基于匹配关键词数量，40%基于加权分数
        return 0.6 * keywordCountRatio + 0.4 * weightedRatio;
    }

    /**
     * 获取匹配的关键词列表
     *
     * @param text 输入文本
     * @return 匹配的关键词列表
     */
    public List<String> getMatchedKeywords(String text) {
        List<String> matchedKeywords = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return matchedKeywords;
        }

        // 分词处理输入文本
        List<String> textTokens = ChineseTokenizer.tokenize(text);

        for (String keyword : keywords) {
            // 直接匹配
            boolean directMatch = text.contains(keyword);

            // 分词匹配
            boolean tokenMatch = textTokens.contains(keyword);

            // 同义词匹配
            boolean synonymMatch = false;
            Set<String> synonyms = SynonymManager.getSynonyms(keyword);
            for (String token : textTokens) {
                if (synonyms.contains(token)) {
                    synonymMatch = true;
                    break;
                }
            }

            // 如果任一方式匹配成功
            if (directMatch || tokenMatch || synonymMatch) {
                matchedKeywords.add(keyword);

                // 如果是同义词匹配，添加匹配的同义词信息
                if (synonymMatch && !directMatch && !tokenMatch) {
                    for (String token : textTokens) {
                        if (synonyms.contains(token)) {
                            matchedKeywords.add(keyword + "(同义词: " + token + ")");
                            break;
                        }
                    }
                }
            }
        }

        return matchedKeywords;
    }

    @Override
    public String toString() {
        return "ResponsibilityDomain{" +
                "domainId='" + domainId + '\'' +
                ", domainName='" + domainName + '\'' +
                ", responsiblePerson='" + responsiblePerson + '\'' +
                ", keywords=" + keywords +
                ", description='" + description + '\'' +
                '}';
    }
}
