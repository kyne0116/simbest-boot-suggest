package com.simbest.boot.suggest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

/**
 * AI算法配置类
 * 用于配置AI相关的算法参数
 */
@Configuration
@Getter
public class AIAlgorithmConfig {

    /**
     * 是否使用语义分析
     */
    @Value("${ai.algorithm.useSemantic:true}")
    private boolean useSemantic;

    /**
     * 是否使用深度学习
     */
    @Value("${ai.algorithm.useDeepLearning:true}")
    private boolean useDeepLearning;

    /**
     * 是否使用上下文感知
     */
    @Value("${ai.algorithm.useContextAwareness:true}")
    private boolean useContextAwareness;

    /**
     * 置信度阈值
     */
    @Value("${ai.algorithm.confidenceThreshold:0.6}")
    private double confidenceThreshold;

    /**
     * 语义权重
     */
    @Value("${ai.algorithm.semanticWeight:0.7}")
    private double semanticWeight;

    /**
     * 组织关系权重
     */
    @Value("${ai.algorithm.organizationalWeight:0.8}")
    private double organizationalWeight;

    /**
     * 历史数据权重
     */
    @Value("${ai.algorithm.historicalWeight:0.5}")
    private double historicalWeight;
}
