package com.simbest.boot.suggest.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simbest.boot.suggest.config.ConfigConstants;
import com.simbest.boot.suggest.config.ConfigManager;
import com.simbest.boot.suggest.service.ConfigService;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据加载器
 * 负责从资源文件加载配置数据
 * 注意：基础数据已迁移到数据库，此类仅用于加载配置数据
 *
 * 此类现在是一个包装类，将调用转发到ConfigManager
 * 为了保持兼容性，保留了静态方法，但内部使用ConfigManager实现
 */
@Component
@Slf4j
public class DataLoader {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 静态引用，用于在静态方法中访问ConfigService和ConfigManager
    private static ConfigService configService;
    private static ConfigManager configManager;

    /**
     * 构造函数，注入ConfigService和ConfigManager
     *
     * @param configService 配置服务
     * @param configManager 配置管理器
     */
    @Autowired
    public DataLoader(ConfigService configService, ConfigManager configManager) {
        DataLoader.configService = configService;
        DataLoader.configManager = configManager;
        log.info("DataLoader初始化完成，已注入ConfigService和ConfigManager");
    }

    /**
     * 从JSON文件加载配置（用于初始化ConfigService）
     *
     * @param resourcePath 资源路径
     * @return 配置映射
     * @throws IOException 如果加载配置文件失败
     */
    public static Map<String, Object> loadConfigFromJson(String resourcePath) throws IOException {
        InputStream is = DataLoader.class.getResourceAsStream(resourcePath);
        if (is != null) {
            Map<String, Object> config = objectMapper.readValue(is, new TypeReference<Map<String, Object>>() {
            });
            log.info("已从JSON文件加载配置: {}", resourcePath);
            return config;
        }
        log.warn("配置文件不存在: {}", resourcePath);
        return new HashMap<>();
    }

    /**
     * 加载阈值配置
     *
     * @return 阈值配置
     */
    public static Map<String, Object> loadThresholdConfig() {
        if (configManager != null) {
            return configManager.getCategory(ConfigConstants.Category.THRESHOLD);
        } else if (configService != null) {
            return configService.getConfigCategory(ConfigConstants.Category.THRESHOLD, ConfigConstants.DEFAULT_TENANT);
        } else {
            log.error("ConfigManager和ConfigService未初始化，无法加载阈值配置");
            return new HashMap<>();
        }
    }

    /**
     * 加载算法权重配置
     *
     * @return 算法权重配置
     */
    public static Map<String, Object> loadAlgorithmWeights() {
        if (configManager != null) {
            return configManager.getCategory(ConfigConstants.Category.ALGORITHM);
        } else if (configService != null) {
            return configService.getConfigCategory(ConfigConstants.Category.ALGORITHM, ConfigConstants.DEFAULT_TENANT);
        } else {
            log.error("ConfigManager和ConfigService未初始化，无法加载算法权重配置");
            return new HashMap<>();
        }
    }

    /**
     * 获取算法权重配置中的特定部分
     *
     * @param section 配置部分名称
     * @return 特定部分的配置
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getAlgorithmWeightSection(String section) {
        if (configManager != null) {
            return configManager.getSection(ConfigConstants.Category.ALGORITHM, section);
        } else if (configService != null) {
            return configService.getConfigSection(ConfigConstants.Category.ALGORITHM, section,
                    ConfigConstants.DEFAULT_TENANT);
        } else {
            log.error("ConfigManager和ConfigService未初始化，无法获取算法权重配置部分: {}", section);
            return new HashMap<>();
        }
    }

    /**
     * 加载AI分析配置
     *
     * @return AI分析配置
     */
    public static Map<String, Object> loadAIAnalysisConfig() {
        if (configManager != null) {
            return configManager.getCategory(ConfigConstants.Category.AI_ANALYSIS);
        } else if (configService != null) {
            return configService.getConfigCategory(ConfigConstants.Category.AI_ANALYSIS,
                    ConfigConstants.DEFAULT_TENANT);
        } else {
            log.error("ConfigManager和ConfigService未初始化，无法加载AI分析配置");
            return new HashMap<>();
        }
    }

    /**
     * 获取AI分析配置中的特定部分
     *
     * @param section 配置部分名称
     * @return 特定部分的配置
     */
    public static Map<String, Object> getAIAnalysisConfigSection(String section) {
        if (configManager != null) {
            return configManager.getSection(ConfigConstants.Category.AI_ANALYSIS, section);
        } else if (configService != null) {
            return configService.getConfigSection(ConfigConstants.Category.AI_ANALYSIS, section,
                    ConfigConstants.DEFAULT_TENANT);
        } else {
            log.error("ConfigManager和ConfigService未初始化，无法获取AI分析配置部分: {}", section);
            return new HashMap<>();
        }
    }

    /**
     * 加载推荐配置
     *
     * @return 推荐配置
     */
    public static Map<String, Object> loadRecommendationConfig() {
        if (configManager != null) {
            return configManager.getCategory(ConfigConstants.Category.RECOMMENDATION);
        } else if (configService != null) {
            return configService.getConfigCategory(ConfigConstants.Category.RECOMMENDATION,
                    ConfigConstants.DEFAULT_TENANT);
        } else {
            log.error("ConfigManager和ConfigService未初始化，无法加载推荐配置");
            return new HashMap<>();
        }
    }

    /**
     * 获取推荐配置中的特定部分
     *
     * @param section 配置部分名称
     * @return 特定部分的配置
     */
    public static Map<String, Object> getRecommendationConfigSection(String section) {
        if (configManager != null) {
            return configManager.getSection(ConfigConstants.Category.RECOMMENDATION, section);
        } else if (configService != null) {
            return configService.getConfigSection(ConfigConstants.Category.RECOMMENDATION, section,
                    ConfigConstants.DEFAULT_TENANT);
        } else {
            log.error("ConfigManager和ConfigService未初始化，无法获取推荐配置部分: {}", section);
            return new HashMap<>();
        }
    }
}
