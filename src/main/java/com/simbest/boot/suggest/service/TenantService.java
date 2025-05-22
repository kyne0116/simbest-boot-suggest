package com.simbest.boot.suggest.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simbest.boot.suggest.model.Tenant;
import com.simbest.boot.suggest.repository.TenantRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 租户服务类
 * 提供租户相关的业务逻辑
 */
@Service
@Slf4j
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    /**
     * 创建新租户
     *
     * @param tenant 租户对象
     * @return 创建后的租户对象
     */
    @Transactional
    public Tenant createTenant(Tenant tenant) {
        // 检查租户编码是否已存在
        if (tenantRepository.existsByTenantCode(tenant.getTenantCode())) {
            throw new IllegalArgumentException("租户编码已存在: " + tenant.getTenantCode());
        }

        // 设置创建时间
        tenant.setCreateTime(new Date());
        tenant.setUpdateTime(new Date());

        // 默认激活
        if (tenant.getStatus() == null) {
            tenant.setStatus(1);
        }

        Tenant savedTenant = tenantRepository.save(tenant);
        log.info("创建租户成功: {}", savedTenant.getTenantName());
        return savedTenant;
    }

    /**
     * 更新租户信息
     *
     * @param tenant 租户对象
     * @return 更新后的租户对象
     */
    @Transactional
    public Tenant updateTenant(Tenant tenant) {
        // 检查租户是否存在
        if (!tenantRepository.existsById(tenant.getId())) {
            throw new IllegalArgumentException("租户不存在: " + tenant.getId());
        }

        // 设置更新时间
        tenant.setUpdateTime(new Date());

        Tenant updatedTenant = tenantRepository.save(tenant);
        log.info("更新租户成功: {}", updatedTenant.getTenantName());
        return updatedTenant;
    }

    /**
     * 根据ID查找租户
     *
     * @param id 租户ID
     * @return 租户对象
     */
    public Optional<Tenant> getTenantById(Long id) {
        return tenantRepository.findById(id);
    }

    /**
     * 根据租户编码查找租户
     *
     * @param tenantCode 租户编码
     * @return 租户对象
     */
    public Optional<Tenant> getTenantByCode(String tenantCode) {
        return tenantRepository.findByTenantCode(tenantCode);
    }

    /**
     * 获取所有租户
     *
     * @return 所有租户列表
     */
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    /**
     * 获取所有激活的租户
     *
     * @return 所有激活的租户列表
     */
    public List<Tenant> getAllActiveTenants() {
        return tenantRepository.findByStatus(1);
    }

    /**
     * 激活租户
     *
     * @param id 租户ID
     * @return 更新后的租户对象
     */
    @Transactional
    public Tenant activateTenant(Long id) {
        Optional<Tenant> tenantOpt = tenantRepository.findById(id);
        if (!tenantOpt.isPresent()) {
            throw new IllegalArgumentException("租户不存在: " + id);
        }

        Tenant tenant = tenantOpt.get();
        tenant.setStatus(1);
        tenant.setUpdateTime(new Date());

        Tenant updatedTenant = tenantRepository.save(tenant);
        log.info("激活租户成功: {}", updatedTenant.getTenantName());
        return updatedTenant;
    }

    /**
     * 停用租户
     *
     * @param id 租户ID
     * @return 更新后的租户对象
     */
    @Transactional
    public Tenant deactivateTenant(Long id) {
        Optional<Tenant> tenantOpt = tenantRepository.findById(id);
        if (!tenantOpt.isPresent()) {
            throw new IllegalArgumentException("租户不存在: " + id);
        }

        Tenant tenant = tenantOpt.get();
        tenant.setStatus(0);
        tenant.setUpdateTime(new Date());

        Tenant updatedTenant = tenantRepository.save(tenant);
        log.info("停用租户成功: {}", updatedTenant.getTenantName());
        return updatedTenant;
    }

    /**
     * 验证租户是否有效
     *
     * @param tenantCode 租户编码
     * @return 是否有效
     */
    public boolean validateTenant(String tenantCode) {
        if (tenantCode == null || tenantCode.isEmpty()) {
            return false;
        }

        Optional<Tenant> tenantOpt = tenantRepository.findByTenantCode(tenantCode);
        return tenantOpt.isPresent() && tenantOpt.get().isActive();
    }
}
