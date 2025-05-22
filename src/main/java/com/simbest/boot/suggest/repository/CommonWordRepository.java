package com.simbest.boot.suggest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.simbest.boot.suggest.entity.CommonWordEntity;

/**
 * 常用词数据访问接口
 */
@Repository
public interface CommonWordRepository extends JpaRepository<CommonWordEntity, Long> {

    /**
     * 根据租户代码查找常用词列表
     * 
     * @param tenantCode 租户代码
     * @return 常用词实体列表
     */
    List<CommonWordEntity> findByTenantCode(String tenantCode);

    /**
     * 根据类别和租户代码查找常用词列表
     * 
     * @param category 类别
     * @param tenantCode 租户代码
     * @return 常用词实体列表
     */
    List<CommonWordEntity> findByCategoryAndTenantCode(String category, String tenantCode);
}
