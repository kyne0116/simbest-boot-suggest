package com.simbest.boot.suggest.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simbest.boot.suggest.service.ConfigService;

import lombok.extern.slf4j.Slf4j;

/**
 * 配置管理器
 * 统一管理配置的访问，提供类型安全的配置获取方法
 */
@Component
@Slf4j
public class ConfigManager {

    @Autowired
    private ConfigService configService;

    /**
     * 获取字符串配置项
     * 
     * @param categoryCode 类别代码
     * @param key          配置键
     * @param tenantCode   租户代码
     * @param defaultValue 默认值
     * @return 配置值
     */
    public String getString(String categoryCode, String key, String tenantCode, String defaultValue) {
        Object value = configService.getConfigItem(categoryCode, key, tenantCode);
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }

    /**
     * 获取字符串配置项（使用默认租户）
     * 
     * @param categoryCode 类别代码
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public String getString(String categoryCode, String key, String defaultValue) {
        return getString(categoryCode, key, ConfigConstants.DEFAULT_TENANT, defaultValue);
    }

    /**
     * 获取整数配置项
     * 
     * @param categoryCode 类别代码
     * @param key          配置键
     * @param tenantCode   租户代码
     * @param defaultValue 默认值
     * @return 配置值
     */
    public int getInt(String categoryCode, String key, String tenantCode, int defaultValue) {
        Object value = configService.getConfigItem(categoryCode, key, tenantCode);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            log.warn("配置项 {}.{} 的值 {} 不是有效的整数，使用默认值 {}", categoryCode, key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * 获取整数配置项（使用默认租户）
     * 
     * @param categoryCode 类别代码
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public int getInt(String categoryCode, String key, int defaultValue) {
        return getInt(categoryCode, key, ConfigConstants.DEFAULT_TENANT, defaultValue);
    }

    /**
     * 获取长整数配置项
     * 
     * @param categoryCode 类别代码
     * @param key          配置键
     * @param tenantCode   租户代码
     * @param defaultValue 默认值
     * @return 配置值
     */
    public long getLong(String categoryCode, String key, String tenantCode, long defaultValue) {
        Object value = configService.getConfigItem(categoryCode, key, tenantCode);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            log.warn("配置项 {}.{} 的值 {} 不是有效的长整数，使用默认值 {}", categoryCode, key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * 获取长整数配置项（使用默认租户）
     * 
     * @param categoryCode 类别代码
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public long getLong(String categoryCode, String key, long defaultValue) {
        return getLong(categoryCode, key, ConfigConstants.DEFAULT_TENANT, defaultValue);
    }

    /**
     * 获取双精度浮点数配置项
     * 
     * @param categoryCode 类别代码
     * @param key          配置键
     * @param tenantCode   租户代码
     * @param defaultValue 默认值
     * @return 配置值
     */
    public double getDouble(String categoryCode, String key, String tenantCode, double defaultValue) {
        Object value = configService.getConfigItem(categoryCode, key, tenantCode);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            log.warn("配置项 {}.{} 的值 {} 不是有效的双精度浮点数，使用默认值 {}", categoryCode, key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * 获取双精度浮点数配置项（使用默认租户）
     * 
     * @param categoryCode 类别代码
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public double getDouble(String categoryCode, String key, double defaultValue) {
        return getDouble(categoryCode, key, ConfigConstants.DEFAULT_TENANT, defaultValue);
    }

    /**
     * 获取布尔配置项
     * 
     * @param categoryCode 类别代码
     * @param key          配置键
     * @param tenantCode   租户代码
     * @param defaultValue 默认值
     * @return 配置值
     */
    public boolean getBoolean(String categoryCode, String key, String tenantCode, boolean defaultValue) {
        Object value = configService.getConfigItem(categoryCode, key, tenantCode);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        String strValue = value.toString().toLowerCase();
        return "true".equals(strValue) || "yes".equals(strValue) || "1".equals(strValue);
    }

    /**
     * 获取布尔配置项（使用默认租户）
     * 
     * @param categoryCode 类别代码
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public boolean getBoolean(String categoryCode, String key, boolean defaultValue) {
        return getBoolean(categoryCode, key, ConfigConstants.DEFAULT_TENANT, defaultValue);
    }

    /**
     * 获取Map配置项
     * 
     * @param categoryCode 类别代码
     * @param key          配置键
     * @param tenantCode   租户代码
     * @return 配置值
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMap(String categoryCode, String key, String tenantCode) {
        Object value = configService.getConfigItem(categoryCode, key, tenantCode);
        if (value == null) {
            return new HashMap<>();
        }
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        log.warn("配置项 {}.{} 的值 {} 不是有效的Map，返回空Map", categoryCode, key, value);
        return new HashMap<>();
    }

    /**
     * 获取Map配置项（使用默认租户）
     * 
     * @param categoryCode 类别代码
     * @param key          配置键
     * @return 配置值
     */
    public Map<String, Object> getMap(String categoryCode, String key) {
        return getMap(categoryCode, key, ConfigConstants.DEFAULT_TENANT);
    }

    /**
     * 获取配置类别
     * 
     * @param categoryCode 类别代码
     * @param tenantCode   租户代码
     * @return 配置映射
     */
    public Map<String, Object> getCategory(String categoryCode, String tenantCode) {
        return configService.getConfigCategory(categoryCode, tenantCode);
    }

    /**
     * 获取配置类别（使用默认租户）
     * 
     * @param categoryCode 类别代码
     * @return 配置映射
     */
    public Map<String, Object> getCategory(String categoryCode) {
        return getCategory(categoryCode, ConfigConstants.DEFAULT_TENANT);
    }

    /**
     * 获取配置类别的子部分
     * 
     * @param categoryCode 类别代码
     * @param section      部分名称
     * @param tenantCode   租户代码
     * @return 配置映射
     */
    public Map<String, Object> getSection(String categoryCode, String section, String tenantCode) {
        return configService.getConfigSection(categoryCode, section, tenantCode);
    }

    /**
     * 获取配置类别的子部分（使用默认租户）
     * 
     * @param categoryCode 类别代码
     * @param section      部分名称
     * @return 配置映射
     */
    public Map<String, Object> getSection(String categoryCode, String section) {
        return getSection(categoryCode, section, ConfigConstants.DEFAULT_TENANT);
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
    public boolean updateConfigItem(String categoryCode, String key, Object value, String tenantCode) {
        return configService.updateConfigItem(categoryCode, key, value, tenantCode);
    }

    /**
     * 更新配置项（使用默认租户）
     * 
     * @param categoryCode 类别代码
     * @param key          配置键
     * @param value        配置值
     * @return 是否更新成功
     */
    public boolean updateConfigItem(String categoryCode, String key, Object value) {
        return updateConfigItem(categoryCode, key, value, ConfigConstants.DEFAULT_TENANT);
    }
}
