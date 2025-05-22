package com.simbest.boot.suggest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.simbest.boot.suggest.entity.SynonymGroupEntity;

/**
 * 同义词组数据访问接口
 */
@Repository
public interface SynonymGroupRepository extends JpaRepository<SynonymGroupEntity, Long> {

    /**
     * 根据租户代码查找同义词组列表
     * 
     * @param tenantCode 租户代码
     * @return 同义词组实体列表
     */
    List<SynonymGroupEntity> findByTenantCode(String tenantCode);

    /**
     * 根据类别和租户代码查找同义词组列表
     * 
     * @param category 类别
     * @param tenantCode 租户代码
     * @return 同义词组实体列表
     */
    List<SynonymGroupEntity> findByCategoryAndTenantCode(String category, String tenantCode);
}
