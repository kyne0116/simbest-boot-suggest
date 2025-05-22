package com.simbest.boot.suggest.model;

/**
 * 推荐类型枚举
 * 用于指定推荐结果的返回方式
 */
public enum RecommendationType {
    /**
     * 单选推荐人：只返回一个推荐命中率最高最匹配的推荐领导
     */
    SINGLE,
    
    /**
     * 多选推荐人：按照推荐命中率倒排，可以推荐一个或多个领导
     */
    MULTIPLE
}
