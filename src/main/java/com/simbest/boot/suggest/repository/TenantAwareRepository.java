package com.simbest.boot.suggest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 租户感知仓库接口
 * 用于实现租户隔离的数据访问
 * @param <T> 实体类型
 * @param <ID> 主键类型
 */
@NoRepositoryBean
public interface TenantAwareRepository<T, ID> extends JpaRepository<T, ID> {

    /**
     * 根据租户代码查询所有实体
     * @param tenantCode 租户代码
     * @return 实体列表
     */
    List<T> findAllByTenantCode(String tenantCode);

    /**
     * 根据ID和租户代码查询实体
     * @param id 实体ID
     * @param tenantCode 租户代码
     * @return 实体
     */
    Optional<T> findByIdAndTenantCode(ID id, String tenantCode);

    /**
     * 根据租户代码删除所有实体
     * @param tenantCode 租户代码
     */
    void deleteAllByTenantCode(String tenantCode);

    /**
     * 根据ID和租户代码删除实体
     * @param id 实体ID
     * @param tenantCode 租户代码
     */
    void deleteByIdAndTenantCode(ID id, String tenantCode);

    /**
     * 根据租户代码统计实体数量
     * @param tenantCode 租户代码
     * @return 实体数量
     */
    long countByTenantCode(String tenantCode);
}
