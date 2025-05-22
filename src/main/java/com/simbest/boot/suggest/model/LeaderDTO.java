package com.simbest.boot.suggest.model;

import com.simbest.boot.suggest.util.JsonUtil;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 领导DTO类
 * 用于封装领导的账号、姓名信息以及推荐相关信息
 */
@Data
@NoArgsConstructor
public class LeaderDTO {
    /**
     * 领导账号
     */
    private String suggestAccount;

    /**
     * 领导姓名
     */
    private String suggestTruename;

    /**
     * 推荐理由
     */
    @Getter
    private String reason;

    /**
     * 匹配分数
     */
    private double score;

    /**
     * AI置信度
     */
    private double confidenceLevel;

    /**
     * 推荐类型
     */
    private String recommendationType;

    /**
     * 构造函数 - 仅包含账号和姓名
     *
     * @param suggestAccount  领导账号
     * @param suggestTruename 领导姓名
     */
    public LeaderDTO(String suggestAccount, String suggestTruename) {
        this.suggestAccount = suggestAccount;
        this.suggestTruename = suggestTruename;
    }

    /**
     * 构造函数 - 包含所有字段
     *
     * @param suggestAccount     领导账号
     * @param suggestTruename    领导姓名
     * @param reason             推荐理由
     * @param score              匹配分数
     * @param confidenceLevel    AI置信度
     * @param recommendationType 推荐类型
     */
    public LeaderDTO(String suggestAccount, String suggestTruename, String reason, double score, double confidenceLevel,
            String recommendationType) {
        this.suggestAccount = suggestAccount;
        this.suggestTruename = suggestTruename;
        setReason(reason);
        this.score = score;
        this.confidenceLevel = confidenceLevel;
        this.recommendationType = recommendationType;
    }

    /**
     * 设置推荐理由，确保使用UTF-8编码
     *
     * @param reason 推荐理由
     */
    public void setReason(String reason) {
        try {
            this.reason = JsonUtil.ensureUtf8Encoding(reason);
        } catch (Exception e) {
            this.reason = reason;
        }
    }
}
