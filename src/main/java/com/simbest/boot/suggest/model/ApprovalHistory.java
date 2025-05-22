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
 * 批复历史记录实体类
 * 记录历史批复信息，用于学习和推荐
 *
 * 该实体类映射到数据库中的approval_history表，存储历史批复记录，包括：
 * 1. 任务的基本信息（ID、标题、内容）
 * 2. 发起人和审批人信息
 * 3. 批复的结果和意见
 * 4. 推荐相关的信息（是否由系统推荐、推荐分数、推荐理由）
 *
 * 批复历史记录是推荐系统的重要数据来源，系统通过分析历史批复记录，
 * 学习任务模式和审批人偏好，为新任务提供更准确的领导推荐
 */
@Entity
@Table(name = "approval_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalHistory {

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
     * 任务内容
     */
    @Column(name = "task_content", length = 2000)
    private String taskContent;

    /**
     * 发起人账号
     */
    @Column(name = "initiator_account", nullable = false, length = 100)
    private String initiatorAccount;

    /**
     * 发起人组织ID
     */
    @Column(name = "initiator_org_id", length = 100)
    private String initiatorOrgId;

    /**
     * 审批人账号
     */
    @Column(name = "approver_account", nullable = false, length = 100)
    private String approverAccount;

    /**
     * 审批人姓名
     */
    @Column(name = "approver_name", length = 100)
    private String approverName;

    /**
     * 工作流方向
     */
    @Column(name = "workflow_direction", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private WorkflowDirection workflowDirection;

    /**
     * 批复时间
     */
    @Column(name = "approval_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date approvalTime;

    /**
     * 批复结果
     */
    @Column(name = "approval_result", length = 20)
    private String approvalResult;

    /**
     * 批复意见
     */
    @Column(name = "approval_comment", length = 1000)
    private String approvalComment;

    /**
     * 是否由系统推荐
     */
    @Column(name = "is_recommended")
    private Boolean isRecommended = false;

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
