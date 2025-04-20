package com.simbest.boot.suggest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

/**
 * AI显示配置类
 * 用于配置AI相关的显示参数
 */
@Configuration
@Getter
public class AIDisplayConfig {

    /**
     * 是否显示AI置信度
     */
    @Value("${ai.display.showConfidence:true}")
    private boolean showConfidence;

    /**
     * 是否显示AI分析指标
     */
    @Value("${ai.display.showMetrics:true}")
    private boolean showMetrics;

    /**
     * 是否使用高级AI术语
     */
    @Value("${ai.display.useAdvancedTerminology:true}")
    private boolean useAdvancedTerminology;

    /**
     * 是否显示推荐类型
     */
    @Value("${ai.display.showRecommendationType:true}")
    private boolean showRecommendationType;

    /**
     * 是否使用详细的推荐理由
     */
    @Value("${ai.display.useDetailedReason:true}")
    private boolean useDetailedReason;

    /**
     * 是否显示匹配分数
     */
    @Value("${ai.display.showScore:true}")
    private boolean showScore;

    /**
     * 是否使用百分比格式显示分数
     */
    @Value("${ai.display.usePercentageFormat:true}")
    private boolean usePercentageFormat;

    /**
     * 是否使用彩色显示（仅在支持的终端中有效）
     */
    @Value("${ai.display.useColoredOutput:false}")
    private boolean useColoredOutput;
}
