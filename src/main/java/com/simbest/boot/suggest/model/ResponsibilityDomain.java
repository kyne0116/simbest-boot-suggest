package com.simbest.boot.suggest.model;

import java.util.ArrayList;
import java.util.List;

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

        int matchCount = 0;
        int matchedKeywordsCount = 0;

        // 计算匹配的关键词数量和长度
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                // 关键词越长，权重越大
                matchCount += keyword.length();
                matchedKeywordsCount++;
            }
        }

        // 计算匹配度：结合匹配关键词数量比例和长度比例
        double keywordCountRatio = (double) matchedKeywordsCount / keywords.size();
        double keywordLengthRatio = (double) matchCount / Math.min(text.length(), 50); // 限制文本长度影响

        // 综合评分：70%基于匹配关键词数量，30%基于匹配关键词长度
        return 0.7 * keywordCountRatio + 0.3 * keywordLengthRatio;
    }

    /**
     * 获取匹配的关键词列表
     *
     * @param text 输入文本
     * @return 匹配的关键词列表
     */
    public List<String> getMatchedKeywords(String text) {
        List<String> matchedKeywords = new ArrayList<>();

        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                matchedKeywords.add(keyword);
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
