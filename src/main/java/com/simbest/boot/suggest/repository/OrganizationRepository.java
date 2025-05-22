package com.simbest.boot.suggest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.simbest.boot.suggest.entity.OrganizationEntity;

/**
 * 组织数据访问接口
 */
@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Long> {

    /**
     * 根据组织ID查找组织
     * 
     * @param orgId 组织ID
     * @return 组织实体（可选）
     */
    Optional<OrganizationEntity> findByOrgId(String orgId);

    /**
     * 根据租户代码查找组织列表
     * 
     * @param tenantCode 租户代码
     * @return 组织实体列表
     */
    List<OrganizationEntity> findByTenantCode(String tenantCode);

    /**
     * 根据父组织ID查找组织列表
     * 
     * @param parentOrgId 父组织ID
     * @return 组织实体列表
     */
    List<OrganizationEntity> findByParentOrgId(String parentOrgId);

    /**
     * 根据主管领导账号查找组织列表
     * 
     * @param mainLeaderAccount 主管领导账号
     * @return 组织实体列表
     */
    List<OrganizationEntity> findByMainLeaderAccount(String mainLeaderAccount);

    /**
     * 根据租户代码和组织类型查找组织列表
     * 
     * @param tenantCode 租户代码
     * @param orgType 组织类型
     * @return 组织实体列表
     */
    List<OrganizationEntity> findByTenantCodeAndOrgType(String tenantCode, String orgType);
}
