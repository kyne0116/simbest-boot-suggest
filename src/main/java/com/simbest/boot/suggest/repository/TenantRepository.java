package com.simbest.boot.suggest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.simbest.boot.suggest.model.Tenant;

/**
 * 租户Repository接口
 * 提供对租户实体的数据库操作
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    /**
     * 根据租户编码查找租户
     *
     * @param tenantCode 租户编码
     * @return 租户对象
     */
    Optional<Tenant> findByTenantCode(String tenantCode);

    /**
     * 根据租户名称查找租户
     *
     * @param tenantName 租户名称
     * @return 租户对象
     */
    Optional<Tenant> findByTenantName(String tenantName);

    /**
     * 查找所有激活的租户
     *
     * @param status 状态值
     * @return 激活的租户列表
     */
    List<Tenant> findByStatus(Integer status);

    /**
     * 检查租户编码是否存在
     *
     * @param tenantCode 租户编码
     * @return 是否存在
     */
    boolean existsByTenantCode(String tenantCode);
}
