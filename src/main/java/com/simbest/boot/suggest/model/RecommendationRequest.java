package com.simbest.boot.suggest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 领导推荐请求DTO
 * 用于封装推荐接口的请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {
    /**
     * 租户编码（必填）
     * 用于多租户隔离和历史批复记录匹配
     */
    private String tenantCode;

    /**
     * 当前办理人账号（必填，如果未提供orgId）
     * 系统将根据userAccount查找用户所在组织
     */
    private String userAccount;

    /**
     * 当前办理人组织ID（必填，如果未提供userAccount）
     * 直接使用提供的组织ID进行匹配
     */
    private String orgId;

    /**
     * 任务标题（必填）
     */
    private String taskTitle;

    /**
     * 任务ID（可选）
     * 用于关联历史批复记录
     */
    private String taskId;

    /**
     * 工作流方向（必填）：向下指派(DOWNWARD)、向上请示(UPWARD)或同级协办(PARALLEL)
     */
    private WorkflowDirection workflowDirection;

    /**
     * 候选账号列表（可选），如果提供，则推荐结果必须在此列表中
     */
    private String[] candidateAccounts;

    /**
     * 推荐类型（可选，默认为单选推荐人）
     * 单选推荐人：只返回一个推荐命中率最高最匹配的推荐领导
     * 多选推荐人：按照推荐命中率倒排，可以推荐一个或多个领导
     */
    private RecommendationType recommendationType = RecommendationType.SINGLE;
}
