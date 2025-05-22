package com.simbest.boot.suggest.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务模式实体类
 * 用于存储从历史批复记录中学习到的任务模式
 *
 * 该实体类映射到数据库中的task_pattern表，存储任务模式信息，包括：
 * 1. 任务模式的名称和关键词列表
 * 2. 工作流方向（上行、下行）
 * 3. 批复人账号及其权重映射
 * 4. 模式的匹配次数和置信度
 *
 * 任务模式是推荐系统的核心概念之一，系统通过分析历史批复记录，
 * 提取出常见的任务模式，并根据模式匹配为新任务推荐合适的审批人
 */
@Entity
@Table(name = "task_pattern")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskPattern {

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
     * 模式名称
     */
    @Column(name = "pattern_name", length = 100)
    private String patternName;

    /**
     * 关键词列表
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task_pattern_keywords", joinColumns = @JoinColumn(name = "task_pattern_id", nullable = false))
    @OrderColumn(name = "keyword_order", nullable = false)
    @Column(name = "keyword")
    private List<String> keywords;

    /**
     * 工作流方向
     */
    @Column(name = "workflow_direction", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private WorkflowDirection workflowDirection;

    /**
     * 批复人账号权重映射
     * 存储批复人账号和对应的权重
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task_pattern_approver_weights", joinColumns = @JoinColumn(name = "task_pattern_id", nullable = false))
    @MapKeyColumn(name = "approver_account", nullable = false)
    @Column(name = "weight")
    private Map<String, Double> approverWeights;

    /**
     * 最后更新时间
     */
    @Column(name = "last_update_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateTime;

    /**
     * 匹配次数
     */
    @Column(name = "match_count")
    private Integer matchCount = 0;

    /**
     * 置信度
     */
    @Column(name = "confidence")
    private Double confidence = 0.0;

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

    /**
     * 增加匹配次数
     */
    public void incrementMatchCount() {
        this.matchCount = this.matchCount == null ? 1 : this.matchCount + 1;
    }

    /**
     * 更新批复人权重
     *
     * @param approverAccount 批复人账号
     * @param weight          权重增量
     */
    public void updateApproverWeight(String approverAccount, double weight) {
        if (approverWeights.containsKey(approverAccount)) {
            double currentWeight = approverWeights.get(approverAccount);
            approverWeights.put(approverAccount, currentWeight + weight);
        } else {
            approverWeights.put(approverAccount, weight);
        }
    }

    /**
     * 获取最佳批复人账号
     *
     * @return 权重最高的批复人账号
     */
    public String getBestApproverAccount() {
        if (approverWeights == null || approverWeights.isEmpty()) {
            return null;
        }

        String bestApprover = null;
        double maxWeight = Double.MIN_VALUE;

        for (Map.Entry<String, Double> entry : approverWeights.entrySet()) {
            if (entry.getValue() > maxWeight) {
                maxWeight = entry.getValue();
                bestApprover = entry.getKey();
            }
        }

        return bestApprover;
    }

    /**
     * 获取批复人权重
     *
     * @param approverAccount 批复人账号
     * @return 权重值，如果不存在则返回0
     */
    public double getApproverWeight(String approverAccount) {
        return approverWeights.getOrDefault(approverAccount, 0.0);
    }
}
