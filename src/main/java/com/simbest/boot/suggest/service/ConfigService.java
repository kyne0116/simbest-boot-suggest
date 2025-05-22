package com.simbest.boot.suggest.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simbest.boot.suggest.entity.ConfigCategoryEntity;
import com.simbest.boot.suggest.entity.ConfigItemEntity;
import com.simbest.boot.suggest.entity.ConfigItemEntity.ValueType;
import com.simbest.boot.suggest.repository.ConfigCategoryRepository;
import com.simbest.boot.suggest.repository.ConfigItemRepository;
import com.simbest.boot.suggest.util.DataLoader;

import lombok.extern.slf4j.Slf4j;

/**
 * 配置服务类
 * 提供配置项的管理和访问功能
 */
@Service
@Slf4j
public class ConfigService {

    @Autowired
    private ConfigCategoryRepository categoryRepository;

    @Autowired
    private ConfigItemRepository itemRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 配置缓存，按租户和类别分组
    private final Map<String, Map<String, Map<String, Object>>> configCache = new ConcurrentHashMap<>();

    /**
     * 初始化方法，加载配置数据
     */
    @PostConstruct
    public void init() {
        log.info("初始化配置服务...");
        // 检查是否需要初始化配置数据
        if (categoryRepository.count() == 0) {
            log.info("配置类别表为空，开始初始化配置数据...");
            initializeConfigData();
        } else {
            log.info("配置数据已存在，跳过初始化");
        }

        // 加载配置数据到缓存
        refreshCache();
        log.info("配置服务初始化完成");
    }

    /**
     * 初始化配置数据
     * 将JSON配置文件中的数据导入到数据库
     */
    @Transactional
    public void initializeConfigData() {
        log.info("开始初始化配置数据...");
        String defaultTenant = "default";

        // 1. 初始化阈值配置
        initializeConfigCategory("threshold", "阈值配置", "系统阈值相关配置", defaultTenant);
        Map<String, Object> thresholdConfig = DataLoader.loadThresholdConfig();
        saveConfigMap("threshold", thresholdConfig, defaultTenant);

        // 2. 初始化算法权重配置
        initializeConfigCategory("algorithm", "算法权重配置", "文本相似度和关键词匹配算法的权重配置", defaultTenant);
        Map<String, Object> algorithmWeights = DataLoader.loadAlgorithmWeights();
        saveConfigMap("algorithm", algorithmWeights, defaultTenant);

        // 3. 初始化AI分析配置
        initializeConfigCategory("ai-analysis", "AI分析配置", "AI分析相关的配置参数", defaultTenant);
        Map<String, Object> aiAnalysisConfig = DataLoader.loadAIAnalysisConfig();
        saveConfigMap("ai-analysis", aiAnalysisConfig, defaultTenant);

        // 4. 初始化推荐配置
        initializeConfigCategory("recommendation", "推荐配置", "推荐算法相关的配置参数", defaultTenant);
        Map<String, Object> recommendationConfig = DataLoader.loadRecommendationConfig();
        saveConfigMap("recommendation", recommendationConfig, defaultTenant);

        log.info("配置数据初始化完成");
    }

    /**
     * 初始化配置类别
     *
     * @param categoryCode 类别代码
     * @param categoryName 类别名称
     * @param description  类别描述
     * @param tenantCode   租户代码
     * @return 配置类别实体
     */
    @Transactional
    public ConfigCategoryEntity initializeConfigCategory(String categoryCode, String categoryName, String description,
            String tenantCode) {
        Optional<ConfigCategoryEntity> existingCategory = categoryRepository.findByCategoryCodeAndTenantCode(
                categoryCode, tenantCode);
        if (existingCategory.isPresent()) {
            log.debug("配置类别 {} 已存在，跳过创建", categoryCode);
            return existingCategory.get();
        }

        ConfigCategoryEntity category = new ConfigCategoryEntity(categoryCode, categoryName, description, tenantCode);
        category = categoryRepository.save(category);
        log.info("创建配置类别: {}", categoryCode);
        return category;
    }

    /**
     * 保存配置映射
     *
     * @param categoryCode 类别代码
     * @param configMap    配置映射
     * @param tenantCode   租户代码
     */
    @Transactional
    public void saveConfigMap(String categoryCode, Map<String, Object> configMap, String tenantCode) {
        Optional<ConfigCategoryEntity> categoryOpt = categoryRepository.findByCategoryCodeAndTenantCode(
                categoryCode, tenantCode);
        if (!categoryOpt.isPresent()) {
            log.error("配置类别 {} 不存在，无法保存配置映射", categoryCode);
            return;
        }

        ConfigCategoryEntity category = categoryOpt.get();
        saveConfigMapRecursive(category, "", configMap, tenantCode);
    }

    /**
     * 递归保存配置映射
     *
     * @param category   配置类别
     * @param prefix     键前缀
     * @param configMap  配置映射
     * @param tenantCode 租户代码
     */
    @SuppressWarnings("unchecked")
    private void saveConfigMapRecursive(ConfigCategoryEntity category, String prefix, Map<String, Object> configMap,
            String tenantCode) {
        for (Map.Entry<String, Object> entry : configMap.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                // 递归处理嵌套的Map
                saveConfigMapRecursive(category, key, (Map<String, Object>) value, tenantCode);
            } else {
                // 保存叶子节点
                String stringValue;
                ValueType valueType;

                if (value instanceof String) {
                    stringValue = (String) value;
                    valueType = ValueType.STRING;
                } else if (value instanceof Number) {
                    stringValue = value.toString();
                    valueType = ValueType.NUMBER;
                } else if (value instanceof Boolean) {
                    stringValue = value.toString();
                    valueType = ValueType.BOOLEAN;
                } else {
                    try {
                        stringValue = objectMapper.writeValueAsString(value);
                        valueType = ValueType.JSON;
                    } catch (JsonProcessingException e) {
                        log.error("序列化配置值失败: {}", e.getMessage(), e);
                        stringValue = value.toString();
                        valueType = ValueType.STRING;
                    }
                }

                // 检查是否已存在
                Optional<ConfigItemEntity> existingItem = itemRepository.findByCategoryAndItemKeyAndTenantCode(
                        category, key, tenantCode);
                if (existingItem.isPresent()) {
                    // 更新现有配置项
                    ConfigItemEntity item = existingItem.get();
                    item.setItemValue(stringValue);
                    item.setValueType(valueType);
                    item.setUpdateTime(new Date());
                    item.setUpdatedBy("system");
                    itemRepository.save(item);
                    log.debug("更新配置项: {}.{}", category.getCategoryCode(), key);
                } else {
                    // 创建新配置项
                    ConfigItemEntity item = new ConfigItemEntity(category, key, stringValue, valueType,
                            "从配置文件导入的配置项", tenantCode);
                    itemRepository.save(item);
                    log.debug("创建配置项: {}.{}", category.getCategoryCode(), key);
                }
            }
        }
    }

    /**
     * 刷新配置缓存
     */
    public void refreshCache() {
        log.info("刷新配置缓存...");
        configCache.clear();

        // 获取所有租户的配置类别
        List<ConfigCategoryEntity> allCategories = categoryRepository.findAll();
        for (ConfigCategoryEntity category : allCategories) {
            String tenantCode = category.getTenantCode();
            String categoryCode = category.getCategoryCode();

            // 获取该类别下的所有配置项
            List<ConfigItemEntity> items = itemRepository.findByCategoryAndTenantCode(category, tenantCode);
            if (!items.isEmpty()) {
                // 构建配置映射
                Map<String, Object> categoryConfig = buildConfigMap(items);

                // 添加到缓存
                configCache.computeIfAbsent(tenantCode, k -> new ConcurrentHashMap<>())
                        .put(categoryCode, categoryConfig);
            }
        }

        log.info("配置缓存刷新完成，共加载 {} 个租户的配置", configCache.size());
    }

    /**
     * 构建配置映射
     *
     * @param items 配置项列表
     * @return 配置映射
     */
    private Map<String, Object> buildConfigMap(List<ConfigItemEntity> items) {
        Map<String, Object> result = new HashMap<>();

        for (ConfigItemEntity item : items) {
            String key = item.getItemKey();
            String value = item.getItemValue();
            ValueType valueType = item.getValueType();

            // 解析键路径
            String[] keyParts = key.split("\\.");
            Map<String, Object> currentMap = result;

            // 处理嵌套路径
            for (int i = 0; i < keyParts.length - 1; i++) {
                String part = keyParts[i];
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) currentMap.computeIfAbsent(part,
                        k -> new HashMap<>());
                currentMap = nestedMap;
            }

            // 设置最终值
            String lastKey = keyParts[keyParts.length - 1];
            try {
                Object finalValue = parseValue(value, valueType);
                currentMap.put(lastKey, finalValue);
            } catch (JsonProcessingException | NumberFormatException e) {
                log.error("解析配置值失败: {} ({}), 使用原始字符串", value, valueType, e);
                currentMap.put(lastKey, value);
            }
        }

        return result;
    }

    /**
     * 解析配置值
     *
     * @param value     值字符串
     * @param valueType 值类型
     * @return 解析后的值
     * @throws NumberFormatException   如果数字解析失败
     * @throws JsonProcessingException 如果JSON解析失败
     */
    private Object parseValue(String value, ValueType valueType) throws JsonProcessingException {
        switch (valueType) {
            case NUMBER:
                // 尝试解析为整数或浮点数
                if (value.contains(".")) {
                    // 尝试解析为浮点数，如果失败则抛出异常
                    return Double.parseDouble(value);
                } else {
                    // 尝试解析为整数，如果失败则尝试解析为浮点数，如果仍然失败则抛出异常
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        // 尝试解析为浮点数
                        return Double.parseDouble(value);
                    }
                }
            case BOOLEAN:
                return Boolean.parseBoolean(value);
            case JSON:
                // 解析JSON，如果失败则抛出异常
                return objectMapper.readValue(value, new TypeReference<Map<String, Object>>() {
                });
            case STRING:
            default:
                return value;
        }
    }

    /**
     * 获取配置项
     *
     * @param categoryCode 类别代码
     * @param key          配置键
     * @param tenantCode   租户代码
     * @return 配置值
     */
    public Object getConfigItem(String categoryCode, String key, String tenantCode) {
        // 从缓存中获取
        Map<String, Map<String, Object>> tenantConfig = configCache.get(tenantCode);
        if (tenantConfig == null) {
            log.debug("租户 {} 的配置缓存不存在", tenantCode);
            return null;
        }

        Map<String, Object> categoryConfig = tenantConfig.get(categoryCode);
        if (categoryConfig == null) {
            log.debug("租户 {} 的类别 {} 配置缓存不存在", tenantCode, categoryCode);
            return null;
        }

        // 处理嵌套键
        String[] keyParts = key.split("\\.");
        Object currentValue = categoryConfig;

        for (String part : keyParts) {
            if (currentValue instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> currentMap = (Map<String, Object>) currentValue;
                currentValue = currentMap.get(part);
                if (currentValue == null) {
                    log.debug("配置项 {}.{} 不存在", categoryCode, key);
                    return null;
                }
            } else {
                log.debug("配置项 {}.{} 的路径无效", categoryCode, key);
                return null;
            }
        }

        return currentValue;
    }

    /**
     * 获取配置项，带默认值
     *
     * @param categoryCode 类别代码
     * @param key          配置键
     * @param tenantCode   租户代码
     * @param defaultValue 默认值
     * @return 配置值
     */
    public Object getConfigItem(String categoryCode, String key, String tenantCode, Object defaultValue) {
        Object value = getConfigItem(categoryCode, key, tenantCode);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取配置类别
     *
     * @param categoryCode 类别代码
     * @param tenantCode   租户代码
     * @return 配置映射
     */
    public Map<String, Object> getConfigCategory(String categoryCode, String tenantCode) {
        // 从缓存中获取
        Map<String, Map<String, Object>> tenantConfig = configCache.get(tenantCode);
        if (tenantConfig == null) {
            log.debug("租户 {} 的配置缓存不存在", tenantCode);
            return new HashMap<>();
        }

        Map<String, Object> categoryConfig = tenantConfig.get(categoryCode);
        if (categoryConfig == null) {
            log.debug("租户 {} 的类别 {} 配置缓存不存在", tenantCode, categoryCode);
            return new HashMap<>();
        }

        return new HashMap<>(categoryConfig);
    }

    /**
     * 获取配置类别的子部分
     *
     * @param categoryCode 类别代码
     * @param section      部分名称
     * @param tenantCode   租户代码
     * @return 配置映射
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getConfigSection(String categoryCode, String section, String tenantCode) {
        Object sectionObj = getConfigItem(categoryCode, section, tenantCode);
        if (sectionObj instanceof Map) {
            return (Map<String, Object>) sectionObj;
        }
        return new HashMap<>();
    }

    /**
     * 更新配置项
     *
     * @param categoryCode 类别代码
     * @param key          配置键
     * @param value        配置值
     * @param tenantCode   租户代码
     * @return 是否更新成功
     */
    @Transactional
    public boolean updateConfigItem(String categoryCode, String key, Object value, String tenantCode) {
        Optional<ConfigCategoryEntity> categoryOpt = categoryRepository.findByCategoryCodeAndTenantCode(
                categoryCode, tenantCode);
        if (!categoryOpt.isPresent()) {
            log.error("配置类别 {} 不存在，无法更新配置项", categoryCode);
            return false;
        }

        ConfigCategoryEntity category = categoryOpt.get();
        Optional<ConfigItemEntity> itemOpt = itemRepository.findByCategoryAndItemKeyAndTenantCode(
                category, key, tenantCode);

        String stringValue;
        ValueType valueType;

        if (value instanceof String) {
            stringValue = (String) value;
            valueType = ValueType.STRING;
        } else if (value instanceof Number) {
            stringValue = value.toString();
            valueType = ValueType.NUMBER;
        } else if (value instanceof Boolean) {
            stringValue = value.toString();
            valueType = ValueType.BOOLEAN;
        } else {
            try {
                stringValue = objectMapper.writeValueAsString(value);
                valueType = ValueType.JSON;
            } catch (JsonProcessingException e) {
                log.error("序列化配置值失败: {}", e.getMessage(), e);
                stringValue = value.toString();
                valueType = ValueType.STRING;
            }
        }

        if (itemOpt.isPresent()) {
            // 更新现有配置项
            ConfigItemEntity item = itemOpt.get();
            item.setItemValue(stringValue);
            item.setValueType(valueType);
            item.setUpdateTime(new Date());
            item.setUpdatedBy("system");
            itemRepository.save(item);
            log.info("更新配置项: {}.{}", categoryCode, key);
        } else {
            // 创建新配置项
            ConfigItemEntity item = new ConfigItemEntity(category, key, stringValue, valueType,
                    "通过API创建的配置项", tenantCode);
            itemRepository.save(item);
            log.info("创建配置项: {}.{}", categoryCode, key);
        }

        // 刷新缓存
        refreshCache();
        return true;
    }

    /**
     * 删除配置项
     *
     * @param categoryCode 类别代码
     * @param key          配置键
     * @param tenantCode   租户代码
     * @return 是否删除成功
     */
    @Transactional
    public boolean deleteConfigItem(String categoryCode, String key, String tenantCode) {
        Optional<ConfigCategoryEntity> categoryOpt = categoryRepository.findByCategoryCodeAndTenantCode(
                categoryCode, tenantCode);
        if (!categoryOpt.isPresent()) {
            log.error("配置类别 {} 不存在，无法删除配置项", categoryCode);
            return false;
        }

        ConfigCategoryEntity category = categoryOpt.get();
        Optional<ConfigItemEntity> itemOpt = itemRepository.findByCategoryAndItemKeyAndTenantCode(
                category, key, tenantCode);

        if (itemOpt.isPresent()) {
            itemRepository.delete(itemOpt.get());
            log.info("删除配置项: {}.{}", categoryCode, key);

            // 刷新缓存
            refreshCache();
            return true;
        } else {
            log.warn("配置项 {}.{} 不存在，无法删除", categoryCode, key);
            return false;
        }
    }

    /**
     * 获取所有配置类别
     *
     * @param tenantCode 租户代码
     * @return 配置类别列表
     */
    public List<ConfigCategoryEntity> getAllCategories(String tenantCode) {
        return categoryRepository.findByTenantCode(tenantCode);
    }

    /**
     * 获取配置类别下的所有配置项
     *
     * @param categoryCode 类别代码
     * @param tenantCode   租户代码
     * @return 配置项列表
     */
    public List<ConfigItemEntity> getCategoryItems(String categoryCode, String tenantCode) {
        return itemRepository.findByCategoryCodeAndTenantCode(categoryCode, tenantCode);
    }
}
