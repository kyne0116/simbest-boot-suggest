package com.simbest.boot.suggest.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simbest.boot.suggest.config.DefaultValueConstants;
import com.simbest.boot.suggest.service.TenantService;

import lombok.extern.slf4j.Slf4j;

/**
 * 租户验证工具类
 * 用于验证租户是否有效
 */
@Component
@Slf4j
public class TenantValidator {

    @Autowired
    private TenantService tenantService;

    /**
     * 验证租户是否有效
     *
     * @param tenantCode 租户编码
     * @return 是否有效
     */
    public boolean isValidTenant(String tenantCode) {
        if (tenantCode == null || tenantCode.isEmpty()) {
            log.warn("租户编码为空");
            return false;
        }

        // 特殊处理默认租户
        if (DefaultValueConstants.getDefaultTenantCode().equals(tenantCode)) {
            log.debug("默认租户验证通过");
            return true;
        }

        boolean valid = tenantService.validateTenant(tenantCode);
        if (!valid) {
            log.warn("租户无效或不存在: {}", tenantCode);
        }

        return valid;
    }

    /**
     * 验证租户是否有效，如果无效则抛出异常
     *
     * @param tenantCode 租户编码
     * @throws IllegalArgumentException 如果租户无效
     */
    public void validateTenant(String tenantCode) {
        if (!isValidTenant(tenantCode)) {
            throw new IllegalArgumentException("租户无效或不存在: " + tenantCode);
        }
    }
}
