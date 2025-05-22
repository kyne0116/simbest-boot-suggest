package com.simbest.boot.suggest.model;

/**
 * 租户感知实体接口
 * 用于标记实体类是否支持租户隔离
 */
public interface TenantAwareEntity {

    /**
     * 获取租户代码
     * @return 租户代码
     */
    String getTenantCode();

    /**
     * 设置租户代码
     * @param tenantCode 租户代码
     */
    void setTenantCode(String tenantCode);
}
