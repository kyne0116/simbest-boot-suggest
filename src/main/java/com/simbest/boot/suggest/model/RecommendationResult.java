package com.simbest.boot.suggest.model;

/**
 * 推荐结果模型类
 * 表示一个推荐结果，包含推荐的领导账号、推荐理由和匹配分数
 */
public class RecommendationResult {
    private String leaderAccount;  // 推荐的领导账号
    private String leaderName;     // 推荐的领导姓名
    private String reason;         // 推荐理由
    private double score;          // 匹配分数

    /**
     * 构造函数
     * 
     * @param leaderAccount 推荐的领导账号
     * @param reason 推荐理由
     * @param score 匹配分数
     */
    public RecommendationResult(String leaderAccount, String reason, double score) {
        this.leaderAccount = leaderAccount;
        this.reason = reason;
        this.score = score;
    }

    /**
     * 构造函数
     * 
     * @param leaderAccount 推荐的领导账号
     * @param leaderName 推荐的领导姓名
     * @param reason 推荐理由
     * @param score 匹配分数
     */
    public RecommendationResult(String leaderAccount, String leaderName, String reason, double score) {
        this.leaderAccount = leaderAccount;
        this.leaderName = leaderName;
        this.reason = reason;
        this.score = score;
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

    @Override
    public String toString() {
        return "推荐领导: " + (leaderName != null ? leaderName + " (" + leaderAccount + ")" : leaderAccount) +
                "\n推荐理由: " + reason +
                "\n匹配分数: " + String.format("%.2f", score);
    }
}
