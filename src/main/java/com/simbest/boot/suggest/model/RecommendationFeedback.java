package com.simbest.boot.suggest.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 推荐反馈实体类
 * 记录用户对推荐结果的反馈信息
 */
@Entity
@Table(name = "recommendation_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationFeedback {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 租户编码
     */
    @Column(name = "tenant_code", nullable = false, length = 50)
    private String tenantCode;

    /**
     * 任务ID
     */
    @Column(name = "task_id", length = 100)
    private String taskId;

    /**
     * 任务标题
     */
    @Column(name = "task_title", nullable = false, length = 500)
    private String taskTitle;

    /**
     * 用户账号
     */
    @Column(name = "user_account", nullable = false, length = 100)
    private String userAccount;

    /**
     * 用户组织ID
     */
    @Column(name = "user_org_id", length = 100)
    private String userOrgId;

    /**
     * 推荐的领导账号
     */
    @Column(name = "recommended_leader_account", nullable = false, length = 100)
    private String recommendedLeaderAccount;

    /**
     * 推荐的领导姓名
     */
    @Column(name = "recommended_leader_name", length = 100)
    private String recommendedLeaderName;

    /**
     * 工作流方向
     */
    @Column(name = "workflow_direction", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private WorkflowDirection workflowDirection;

    /**
     * 推荐分数
     */
    @Column(name = "recommendation_score")
    private Double recommendationScore;

    /**
     * 推荐理由
     */
    @Column(name = "recommendation_reason", length = 500)
    private String recommendationReason;

    /**
     * 推荐类型
     */
    @Column(name = "recommendation_type", length = 50)
    private String recommendationType;

    /**
     * 用户是否接受推荐
     */
    @Column(name = "is_accepted")
    private Boolean isAccepted;

    /**
     * 用户选择的实际领导账号
     */
    @Column(name = "actual_leader_account", length = 100)
    private String actualLeaderAccount;

    /**
     * 用户选择的实际领导姓名
     */
    @Column(name = "actual_leader_name", length = 100)
    private String actualLeaderName;

    /**
     * 用户反馈评分（1-5）
     */
    @Column(name = "rating")
    private Integer rating;

    /**
     * 用户反馈意见
     */
    @Column(name = "feedback_comment", length = 1000)
    private String feedbackComment;

    /**
     * 反馈时间
     */
    @Column(name = "feedback_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date feedbackTime;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    /**
     * 创建人
     */
    @Column(name = "created_by", length = 50)
    private String createdBy;

    /**
     * 更新人
     */
    @Column(name = "updated_by", length = 50)
    private String updatedBy;
}
