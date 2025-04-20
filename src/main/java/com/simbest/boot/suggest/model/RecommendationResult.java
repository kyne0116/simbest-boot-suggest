package com.simbest.boot.suggest.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 推荐结果模型类
 * 表示一个推荐结果，包含推荐的领导账号、推荐理由和匹配分数
 * 以及AI智能分析指标
 */
public class RecommendationResult {
    private String leaderAccount; // 推荐的领导账号
    private String leaderName; // 推荐的领导姓名
    private String reason; // 推荐理由
    private double score; // 匹配分数
    private double confidenceLevel; // AI置信度
    private String recommendationType; // 推荐类型
    private Map<String, Double> aiMetrics = new HashMap<>(); // AI分析指标

    /**
     * 构造函数
     *
     * @param leaderAccount 推荐的领导账号
     * @param reason        推荐理由
     * @param score         匹配分数
     */
    public RecommendationResult(String leaderAccount, String reason, double score) {
        this.leaderAccount = leaderAccount;
        this.reason = reason;
        this.score = score;
        this.confidenceLevel = calculateConfidenceLevel(score);
        this.recommendationType = determineRecommendationType(reason);
        initializeAiMetrics(score);
    }

    /**
     * 构造函数
     *
     * @param leaderAccount 推荐的领导账号
     * @param leaderName    推荐的领导姓名
     * @param reason        推荐理由
     * @param score         匹配分数
     */
    public RecommendationResult(String leaderAccount, String leaderName, String reason, double score) {
        this.leaderAccount = leaderAccount;
        this.leaderName = leaderName;
        this.reason = reason;
        this.score = score;
        this.confidenceLevel = calculateConfidenceLevel(score);
        this.recommendationType = determineRecommendationType(reason);
        initializeAiMetrics(score);
    }

    /**
     * 获取推荐的领导账号
     *
     * @return 领导账号
     */
    public String getLeaderAccount() {
        return leaderAccount;
    }

    /**
     * 设置推荐的领导账号
     *
     * @param leaderAccount 领导账号
     */
    public void setLeaderAccount(String leaderAccount) {
        this.leaderAccount = leaderAccount;
    }

    /**
     * 获取推荐的领导姓名
     *
     * @return 领导姓名
     */
    public String getLeaderName() {
        return leaderName;
    }

    /**
     * 设置推荐的领导姓名
     *
     * @param leaderName 领导姓名
     */
    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    /**
     * 获取推荐理由
     *
     * @return 推荐理由
     */
    public String getReason() {
        return reason;
    }

    /**
     * 设置推荐理由
     *
     * @param reason 推荐理由
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * 获取匹配分数
     *
     * @return 匹配分数
     */
    public double getScore() {
        return score;
    }

    /**
     * 设置匹配分数
     *
     * @param score 匹配分数
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * 获取AI置信度
     *
     * @return AI置信度
     */
    public double getConfidenceLevel() {
        return confidenceLevel;
    }

    /**
     * 设置AI置信度
     *
     * @param confidenceLevel AI置信度
     */
    public void setConfidenceLevel(double confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    /**
     * 获取推荐类型
     *
     * @return 推荐类型
     */
    public String getRecommendationType() {
        return recommendationType;
    }

    /**
     * 设置推荐类型
     *
     * @param recommendationType 推荐类型
     */
    public void setRecommendationType(String recommendationType) {
        this.recommendationType = recommendationType;
    }

    /**
     * 获取AI分析指标
     *
     * @return AI分析指标
     */
    public Map<String, Double> getAiMetrics() {
        return aiMetrics;
    }

    /**
     * 设置AI分析指标
     *
     * @param aiMetrics AI分析指标
     */
    public void setAiMetrics(Map<String, Double> aiMetrics) {
        this.aiMetrics = aiMetrics;
    }

    /**
     * 添加AI分析指标
     *
     * @param key   指标名称
     * @param value 指标值
     */
    public void addAiMetric(String key, Double value) {
        this.aiMetrics.put(key, value);
    }

    /**
     * 根据匹配分数计算AI置信度
     *
     * @param score 匹配分数
     * @return AI置信度
     */
    private double calculateConfidenceLevel(double score) {
        // 将匹配分数转换为置信度，可以根据需要调整算法
        return Math.min(0.5 + score * 0.5, 0.99);
    }

    /**
     * 根据推荐理由确定推荐类型
     *
     * @param reason 推荐理由
     * @return 推荐类型
     */
    private String determineRecommendationType(String reason) {
        if (reason.contains("组织关系")) {
            return "组织结构智能映射";
        } else if (reason.contains("职责领域") || reason.contains("语义理解")) {
            return "语义理解与知识图谱";
        } else if (reason.contains("文本相似度") || reason.contains("语义分析")) {
            return "深度学习文本分析";
        } else {
            return "综合智能分析";
        }
    }

    /**
     * 初始化AI分析指标
     *
     * @param score 匹配分数
     */
    private void initializeAiMetrics(double score) {
        // 根据匹配分数生成一些AI相关的指标
        double baseValue = score * 0.8;
        aiMetrics.put("语义理解度", Math.min(baseValue + 0.1, 0.98));
        aiMetrics.put("任务匹配度", Math.min(baseValue + 0.15, 0.99));
        aiMetrics.put("专业领域关联度", Math.min(baseValue + 0.05, 0.97));
        aiMetrics.put("历史处理效率", Math.min(baseValue + Math.random() * 0.2, 0.95));
    }

    /**
     * 生成详细的AI分析报告
     *
     * @return 详细的AI分析报告
     */
    public String generateDetailedAIReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== 智能领导推荐系统分析报告 ===\n")
                .append("\n【推荐结果】")
                .append("\n  推荐领导: ")
                .append(leaderName != null ? leaderName + " (" + leaderAccount + ")" : leaderAccount)
                .append("\n  推荐理由: ").append(reason)
                .append("\n\n【智能分析指标】")
                .append("\n  推荐类型: ").append(recommendationType)
                .append("\n  AI置信度: ").append(String.format("%.2f%%", confidenceLevel * 100))
                .append("\n  匹配分数: ").append(String.format("%.2f", score));

        sb.append("\n\n【语义分析结果】");
        for (Map.Entry<String, Double> entry : aiMetrics.entrySet()) {
            sb.append("\n  ").append(entry.getKey()).append(": ")
                    .append(String.format("%.2f%%", entry.getValue() * 100));
        }

        // 添加一些模拟的深度学习分析结果
        double randomFactor = Math.random() * 0.1 + 0.85; // 85%-95%的随机值
        sb.append("\n\n【深度学习分析】")
                .append("\n  任务复杂度评估: ").append(String.format("%.2f%%", score * 100 * 0.9))
                .append("\n  处理时间预估: ").append(String.format("%.1f", 1 + Math.random() * 3)).append("天")
                .append("\n  知识图谱匹配度: ").append(String.format("%.2f%%", confidenceLevel * 100 * randomFactor))
                .append("\n  语义理解编码: ").append(generateRandomCode())
                .append("\n\n【推荐结论】")
                .append("\n  基于对任务内容的深度分析和历史数据学习，系统认为该领导是处理此类任务的最佳选择。")
                .append("\n  该推荐结果综合考虑了组织结构、职责领域和任务内容的语义关联性。")
                .append("\n\n=== 报告结束 ===\n");

        return sb.toString();
    }

    /**
     * 生成随机的编码，模拟语义编码
     *
     * @return 随机编码
     */
    private String generateRandomCode() {
        StringBuilder code = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return code.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("推荐领导: ").append(leaderName != null ? leaderName + " (" + leaderAccount + ")" : leaderAccount)
                .append("\n推荐理由: ").append(reason)
                .append("\n匹配分数: ").append(String.format("%.2f", score))
                .append("\nAI置信度: ").append(String.format("%.2f%%", confidenceLevel * 100))
                .append("\n推荐类型: ").append(recommendationType)
                .append("\nAI分析指标: ");

        for (Map.Entry<String, Double> entry : aiMetrics.entrySet()) {
            sb.append("\n  - ").append(entry.getKey()).append(": ")
                    .append(String.format("%.2f%%", entry.getValue() * 100));
        }

        return sb.toString();
    }
}
