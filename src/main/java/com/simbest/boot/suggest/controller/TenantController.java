package com.simbest.boot.suggest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.simbest.boot.suggest.model.JsonResponse;
import com.simbest.boot.suggest.model.Tenant;
import com.simbest.boot.suggest.service.TenantService;

import lombok.extern.slf4j.Slf4j;

/**
 * 租户管理控制器
 * 提供租户管理相关的API接口
 */
@RestController
@RequestMapping("/tenant")
@Slf4j
public class TenantController {
    
    @Autowired
    private TenantService tenantService;
    
    /**
     * 创建租户
     * 
     * @param tenant 租户对象
     * @return 创建结果
     */
    @PostMapping
    public JsonResponse<Tenant> createTenant(@RequestBody Tenant tenant) {
        try {
            Tenant createdTenant = tenantService.createTenant(tenant);
            return JsonResponse.success(createdTenant, "创建租户成功");
        } catch (Exception e) {
            log.error("创建租户失败: {}", e.getMessage(), e);
            return JsonResponse.fail("创建租户失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新租户
     * 
     * @param tenant 租户对象
     * @return 更新结果
     */
    @PutMapping
    public JsonResponse<Tenant> updateTenant(@RequestBody Tenant tenant) {
        try {
            Tenant updatedTenant = tenantService.updateTenant(tenant);
            return JsonResponse.success(updatedTenant, "更新租户成功");
        } catch (Exception e) {
            log.error("更新租户失败: {}", e.getMessage(), e);
            return JsonResponse.fail("更新租户失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有租户
     * 
     * @param activeOnly 是否只返回激活的租户
     * @return 租户列表
     */
    @GetMapping
    public JsonResponse<List<Tenant>> getAllTenants(@RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        try {
            List<Tenant> tenants;
            if (activeOnly) {
                tenants = tenantService.getAllActiveTenants();
            } else {
                tenants = tenantService.getAllTenants();
            }
            return JsonResponse.success(tenants, "获取租户列表成功");
        } catch (Exception e) {
            log.error("获取租户列表失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取租户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取租户
     * 
     * @param id 租户ID
     * @return 租户对象
     */
    @GetMapping("/{id}")
    public JsonResponse<Tenant> getTenantById(@PathVariable Long id) {
        try {
            Optional<Tenant> tenantOpt = tenantService.getTenantById(id);
            if (tenantOpt.isPresent()) {
                return JsonResponse.success(tenantOpt.get(), "获取租户成功");
            } else {
                return JsonResponse.fail("租户不存在: " + id);
            }
        } catch (Exception e) {
            log.error("获取租户失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取租户失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据租户编码获取租户
     * 
     * @param tenantCode 租户编码
     * @return 租户对象
     */
    @GetMapping("/code/{tenantCode}")
    public JsonResponse<Tenant> getTenantByCode(@PathVariable String tenantCode) {
        try {
            Optional<Tenant> tenantOpt = tenantService.getTenantByCode(tenantCode);
            if (tenantOpt.isPresent()) {
                return JsonResponse.success(tenantOpt.get(), "获取租户成功");
            } else {
                return JsonResponse.fail("租户不存在: " + tenantCode);
            }
        } catch (Exception e) {
            log.error("获取租户失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取租户失败: " + e.getMessage());
        }
    }
    
    /**
     * 激活租户
     * 
     * @param id 租户ID
     * @return 激活结果
     */
    @PutMapping("/{id}/activate")
    public JsonResponse<Tenant> activateTenant(@PathVariable Long id) {
        try {
            Tenant activatedTenant = tenantService.activateTenant(id);
            return JsonResponse.success(activatedTenant, "激活租户成功");
        } catch (Exception e) {
            log.error("激活租户失败: {}", e.getMessage(), e);
            return JsonResponse.fail("激活租户失败: " + e.getMessage());
        }
    }
    
    /**
     * 停用租户
     * 
     * @param id 租户ID
     * @return 停用结果
     */
    @PutMapping("/{id}/deactivate")
    public JsonResponse<Tenant> deactivateTenant(@PathVariable Long id) {
        try {
            Tenant deactivatedTenant = tenantService.deactivateTenant(id);
            return JsonResponse.success(deactivatedTenant, "停用租户成功");
        } catch (Exception e) {
            log.error("停用租户失败: {}", e.getMessage(), e);
            return JsonResponse.fail("停用租户失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证租户
     * 
     * @param tenantCode 租户编码
     * @return 验证结果
     */
    @GetMapping("/validate")
    public JsonResponse<Boolean> validateTenant(@RequestParam String tenantCode) {
        try {
            boolean valid = tenantService.validateTenant(tenantCode);
            if (valid) {
                return JsonResponse.success(true, "租户有效");
            } else {
                return JsonResponse.fail("租户无效或不存在");
            }
        } catch (Exception e) {
            log.error("验证租户失败: {}", e.getMessage(), e);
            return JsonResponse.fail("验证租户失败: " + e.getMessage());
        }
    }
}
