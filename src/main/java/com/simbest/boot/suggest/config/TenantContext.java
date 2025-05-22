package com.simbest.boot.suggest.config;

import org.springframework.stereotype.Component;

/**
 * 租户上下文
 * 用于在线程中存储当前租户信息
 */
@Component
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    /**
     * 设置当前租户代码
     * 
     * @param tenantCode 租户代码
     */
    public static void setCurrentTenant(String tenantCode) {
        CURRENT_TENANT.set(tenantCode);
    }

    /**
     * 获取当前租户代码
     * 
     * @return 当前租户代码，如果未设置则返回默认租户代码
     */
    public static String getCurrentTenant() {
        String tenantCode = CURRENT_TENANT.get();
        return tenantCode != null ? tenantCode : DefaultValueConstants.getDefaultTenantCode();
    }

    /**
     * 清除当前租户代码
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
