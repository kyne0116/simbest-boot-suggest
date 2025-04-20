package com.simbest.boot.suggest.model;

/**
 * 工作流方向枚举
 * 用于指定推荐的方向
 */
public enum WorkflowDirection {
    /**
     * 向下指派：推荐同级组织分管领导或人员
     */
    DOWNWARD,
    
    /**
     * 向上请示：直接推荐该组织的上级领导账号
     */
    UPWARD,
    
    /**
     * 同级协办：推荐同级组织的其他领导或人员
     */
    PARALLEL
}
