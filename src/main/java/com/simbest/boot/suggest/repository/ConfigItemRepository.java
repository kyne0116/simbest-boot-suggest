package com.simbest.boot.suggest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.simbest.boot.suggest.entity.ConfigCategoryEntity;
import com.simbest.boot.suggest.entity.ConfigItemEntity;

/**
 * 配置项存储库
 * 用于操作配置项数据
 */
@Repository
public interface ConfigItemRepository extends JpaRepository<ConfigItemEntity, Long> {

    /**
     * 根据类别和键查找配置项
     * 
     * @param category   配置类别
     * @param itemKey    配置项键
     * @param tenantCode 租户代码
     * @return 配置项
     */
    Optional<ConfigItemEntity> findByCategoryAndItemKeyAndTenantCode(ConfigCategoryEntity category, String itemKey,
            String tenantCode);

    /**
     * 根据类别代码和键查找配置项
     * 
     * @param categoryCode 类别代码
     * @param itemKey      配置项键
     * @param tenantCode   租户代码
     * @return 配置项
     */
    @Query("SELECT ci FROM ConfigItemEntity ci JOIN ci.category cc WHERE cc.categoryCode = :categoryCode AND ci.itemKey = :itemKey AND ci.tenantCode = :tenantCode")
    Optional<ConfigItemEntity> findByCategoryCodeAndItemKeyAndTenantCode(@Param("categoryCode") String categoryCode,
            @Param("itemKey") String itemKey, @Param("tenantCode") String tenantCode);

    /**
     * 根据类别查找所有配置项
     * 
     * @param category   配置类别
     * @param tenantCode 租户代码
     * @return 配置项列表
     */
    List<ConfigItemEntity> findByCategoryAndTenantCode(ConfigCategoryEntity category, String tenantCode);

    /**
     * 根据类别代码查找所有配置项
     * 
     * @param categoryCode 类别代码
     * @param tenantCode   租户代码
     * @return 配置项列表
     */
    @Query("SELECT ci FROM ConfigItemEntity ci JOIN ci.category cc WHERE cc.categoryCode = :categoryCode AND ci.tenantCode = :tenantCode")
    List<ConfigItemEntity> findByCategoryCodeAndTenantCode(@Param("categoryCode") String categoryCode,
            @Param("tenantCode") String tenantCode);
}
