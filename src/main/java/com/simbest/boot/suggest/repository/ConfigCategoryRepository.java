package com.simbest.boot.suggest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.simbest.boot.suggest.entity.ConfigCategoryEntity;

/**
 * 配置类别存储库
 * 用于操作配置类别数据
 */
@Repository
public interface ConfigCategoryRepository extends JpaRepository<ConfigCategoryEntity, Long> {

    /**
     * 根据类别代码和租户代码查找配置类别
     * 
     * @param categoryCode 类别代码
     * @param tenantCode   租户代码
     * @return 配置类别
     */
    Optional<ConfigCategoryEntity> findByCategoryCodeAndTenantCode(String categoryCode, String tenantCode);

    /**
     * 根据租户代码查找所有配置类别
     * 
     * @param tenantCode 租户代码
     * @return 配置类别列表
     */
    List<ConfigCategoryEntity> findByTenantCode(String tenantCode);
}
