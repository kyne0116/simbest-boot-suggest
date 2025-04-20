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
     * 当前办理人账号（必填）
     */
    private String userAccount;
    
    /**
     * 当前办理人组织ID（可选）
     */
    private String orgId;
    
    /**
     * 任务标题（必填）
     */
    private String taskTitle;
    
    /**
     * 工作流方向（必填）：向下指派(DOWNWARD)、向上请示(UPWARD)或同级协办(PARALLEL)
     */
    private WorkflowDirection workflowDirection;
    
    /**
     * 是否使用组织关系（可选，默认为true）
     */
    private Boolean useOrg = true;
    
    /**
     * 候选账号列表（可选），如果提供，则推荐结果必须在此列表中
     */
    private String[] candidateAccounts;
}
