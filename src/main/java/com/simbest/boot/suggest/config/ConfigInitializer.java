package com.simbest.boot.suggest.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.simbest.boot.suggest.service.ConfigService;
import com.simbest.boot.suggest.util.DataLoader;

import lombok.extern.slf4j.Slf4j;

/**
 * 配置初始化器
 * 负责在应用启动时初始化配置数据
 */
@Component
@Slf4j
public class ConfigInitializer {

        private final ConfigService configService;
        private final AppConfig appConfig;

        public ConfigInitializer(ConfigService configService, AppConfig appConfig) {
                this.configService = configService;
                this.appConfig = appConfig;
        }

        /**
         * 初始化配置数据
         *
         * @throws IOException 如果初始化配置数据失败
         */
        @PostConstruct
        public void init() throws IOException {
                log.info("初始化配置数据...");

                // 初始化系统配置
                initializeSystemConfig();

                // 初始化显示配置
                initializeDisplayConfig();

                // 初始化算法配置
                initializeAlgorithmConfig();

                log.info("配置数据初始化完成");
        }

        /**
         * 初始化系统配置
         */
        private void initializeSystemConfig() {
                log.info("初始化系统配置...");

                // 创建系统配置类别
                configService.initializeConfigCategory(
                                ConfigConstants.Category.SYSTEM,
                                "系统配置",
                                "系统基本配置参数",
                                ConfigConstants.DEFAULT_TENANT);

                // 创建系统配置映射
                Map<String, Object> systemConfig = new HashMap<>();
                systemConfig.put(ConfigConstants.System.NAME, appConfig.getName());
                systemConfig.put(ConfigConstants.System.VERSION, appConfig.getVersion());
                systemConfig.put(ConfigConstants.System.DOMAIN_ID_PREFIX, appConfig.getDomainIdPrefix());

                // 保存系统配置
                configService.saveConfigMap(
                                ConfigConstants.Category.SYSTEM,
                                systemConfig,
                                ConfigConstants.DEFAULT_TENANT);

                log.info("系统配置初始化完成");
        }

        /**
         * 初始化显示配置
         */
        private void initializeDisplayConfig() {
                log.info("初始化显示配置...");

                // 创建显示配置类别
                configService.initializeConfigCategory(
                                ConfigConstants.Category.DISPLAY,
                                "显示配置",
                                "系统显示相关配置参数",
                                ConfigConstants.DEFAULT_TENANT);

                // 创建显示配置映射
                Map<String, Object> displayConfig = new HashMap<>();
                displayConfig.put(ConfigConstants.Display.SHOW_CONFIDENCE, appConfig.isDisplayShowConfidence());
                displayConfig.put(ConfigConstants.Display.SHOW_METRICS, appConfig.isDisplayShowMetrics());
                displayConfig.put(ConfigConstants.Display.USE_ADVANCED_TERMINOLOGY,
                                appConfig.isDisplayUseAdvancedTerminology());
                displayConfig.put(ConfigConstants.Display.SHOW_RECOMMENDATION_TYPE,
                                appConfig.isDisplayShowRecommendationType());
                displayConfig.put(ConfigConstants.Display.USE_DETAILED_REASON, appConfig.isDisplayUseDetailedReason());
                displayConfig.put(ConfigConstants.Display.SHOW_SCORE, appConfig.isDisplayShowScore());
                displayConfig.put(ConfigConstants.Display.USE_PERCENTAGE_FORMAT,
                                appConfig.isDisplayUsePercentageFormat());
                displayConfig.put(ConfigConstants.Display.USE_COLORED_OUTPUT, appConfig.isDisplayUseColoredOutput());

                // 保存显示配置
                configService.saveConfigMap(
                                ConfigConstants.Category.DISPLAY,
                                displayConfig,
                                ConfigConstants.DEFAULT_TENANT);

                log.info("显示配置初始化完成");
        }

        /**
         * 初始化算法配置
         *
         * @throws IOException 如果加载配置文件失败
         */
        private void initializeAlgorithmConfig() throws IOException {
                log.info("初始化算法配置...");

                // 创建算法配置类别（如果不存在）
                if (configService.getConfigCategory(ConfigConstants.Category.ALGORITHM, ConfigConstants.DEFAULT_TENANT)
                                .isEmpty()) {
                        // 从JSON文件加载算法配置
                        Map<String, Object> algorithmConfig = DataLoader
                                        .loadConfigFromJson("/config/algorithm-weights.json");

                        // 创建算法配置类别
                        configService.initializeConfigCategory(
                                        ConfigConstants.Category.ALGORITHM,
                                        "算法权重配置",
                                        "文本相似度和关键词匹配算法的权重配置",
                                        ConfigConstants.DEFAULT_TENANT);

                        // 保存算法配置
                        configService.saveConfigMap(
                                        ConfigConstants.Category.ALGORITHM,
                                        algorithmConfig,
                                        ConfigConstants.DEFAULT_TENANT);

                        log.info("算法配置初始化完成");
                } else {
                        log.info("算法配置已存在，跳过初始化");
                }

                // 创建阈值配置类别（如果不存在）
                if (configService.getConfigCategory(ConfigConstants.Category.THRESHOLD, ConfigConstants.DEFAULT_TENANT)
                                .isEmpty()) {
                        // 从JSON文件加载阈值配置
                        Map<String, Object> thresholdConfig = DataLoader
                                        .loadConfigFromJson("/config/threshold-config.json");

                        // 创建阈值配置类别
                        configService.initializeConfigCategory(
                                        ConfigConstants.Category.THRESHOLD,
                                        "阈值配置",
                                        "系统阈值相关配置",
                                        ConfigConstants.DEFAULT_TENANT);

                        // 保存阈值配置
                        configService.saveConfigMap(
                                        ConfigConstants.Category.THRESHOLD,
                                        thresholdConfig,
                                        ConfigConstants.DEFAULT_TENANT);

                        log.info("阈值配置初始化完成");
                } else {
                        log.info("阈值配置已存在，跳过初始化");
                }

                // 创建AI分析配置类别（如果不存在）
                if (configService
                                .getConfigCategory(ConfigConstants.Category.AI_ANALYSIS, ConfigConstants.DEFAULT_TENANT)
                                .isEmpty()) {
                        // 从JSON文件加载AI分析配置
                        Map<String, Object> aiAnalysisConfig = DataLoader
                                        .loadConfigFromJson("/config/ai-analysis-config.json");

                        // 创建AI分析配置类别
                        configService.initializeConfigCategory(
                                        ConfigConstants.Category.AI_ANALYSIS,
                                        "AI分析配置",
                                        "AI分析相关的配置参数",
                                        ConfigConstants.DEFAULT_TENANT);

                        // 保存AI分析配置
                        configService.saveConfigMap(
                                        ConfigConstants.Category.AI_ANALYSIS,
                                        aiAnalysisConfig,
                                        ConfigConstants.DEFAULT_TENANT);

                        log.info("AI分析配置初始化完成");
                } else {
                        log.info("AI分析配置已存在，跳过初始化");
                }

                // 创建推荐配置类别（如果不存在）
                if (configService
                                .getConfigCategory(ConfigConstants.Category.RECOMMENDATION,
                                                ConfigConstants.DEFAULT_TENANT)
                                .isEmpty()) {
                        // 从JSON文件加载推荐配置
                        Map<String, Object> recommendationConfig = DataLoader
                                        .loadConfigFromJson("/config/recommendation-config.json");

                        // 创建推荐配置类别
                        configService.initializeConfigCategory(
                                        ConfigConstants.Category.RECOMMENDATION,
                                        "推荐配置",
                                        "推荐算法相关的配置参数",
                                        ConfigConstants.DEFAULT_TENANT);

                        // 保存推荐配置
                        configService.saveConfigMap(
                                        ConfigConstants.Category.RECOMMENDATION,
                                        recommendationConfig,
                                        ConfigConstants.DEFAULT_TENANT);

                        log.info("推荐配置初始化完成");
                } else {
                        log.info("推荐配置已存在，跳过初始化");
                }
        }
}
