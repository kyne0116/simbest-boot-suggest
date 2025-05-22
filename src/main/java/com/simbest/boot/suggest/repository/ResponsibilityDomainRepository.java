package com.simbest.boot.suggest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.simbest.boot.suggest.entity.ResponsibilityDomainEntity;

/**
 * 职责领域数据访问接口
 */
@Repository
public interface ResponsibilityDomainRepository extends JpaRepository<ResponsibilityDomainEntity, Long> {

    /**
     * 根据领域ID查找职责领域
     *
     * @param domainId 领域ID
     * @return 职责领域实体（可选）
     */
    Optional<ResponsibilityDomainEntity> findByDomainId(String domainId);

    /**
     * 根据租户代码查找职责领域列表
     *
     * @param tenantCode 租户代码
     * @return 职责领域实体列表
     */
    List<ResponsibilityDomainEntity> findByTenantCode(String tenantCode);

}
