package com.simbest.boot.suggest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.simbest.boot.suggest.entity.OrganizationDomainEntity;

/**
 * 组织领域关联数据访问接口
 */
@Repository
public interface OrganizationDomainRepository extends JpaRepository<OrganizationDomainEntity, Long> {

    /**
     * 根据组织ID查找组织领域关联
     *
     * @param organizationId 组织ID
     * @return 组织领域关联列表
     */
    List<OrganizationDomainEntity> findByOrganizationId(String organizationId);

    /**
     * 根据领域ID查找组织领域关联
     *
     * @param domainId 领域ID
     * @return 组织领域关联列表
     */
    List<OrganizationDomainEntity> findByDomainId(String domainId);

    /**
     * 根据组织ID和领域ID查找组织领域关联
     *
     * @param organizationId 组织ID
     * @param domainId 领域ID
     * @return 组织领域关联（可选）
     */
    Optional<OrganizationDomainEntity> findByOrganizationIdAndDomainId(String organizationId, String domainId);

    /**
     * 根据租户代码查找组织领域关联列表
     *
     * @param tenantCode 租户代码
     * @return 组织领域关联列表
     */
    List<OrganizationDomainEntity> findByTenantCode(String tenantCode);

}
