package com.simbest.boot.suggest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.simbest.boot.suggest.entity.DomainLeaderMappingEntity;

/**
 * 领域到领导映射数据访问接口
 */
@Repository
public interface DomainLeaderMappingRepository extends JpaRepository<DomainLeaderMappingEntity, Long> {

    /**
     * 根据租户代码查找领域到领导映射列表
     *
     * @param tenantCode 租户代码
     * @return 领域到领导映射实体列表
     */
    List<DomainLeaderMappingEntity> findByTenantCode(String tenantCode);

    /**
     * 根据领域名称和租户代码查找领域到领导映射
     *
     * @param domainName 领域名称
     * @param tenantCode 租户代码
     * @return 领域到领导映射实体（可选）
     */
    Optional<DomainLeaderMappingEntity> findByDomainNameAndTenantCode(String domainName, String tenantCode);

    /**
     * 根据领导账号查找领域到领导映射列表
     *
     * @param leaderAccount 领导账号
     * @return 领域到领导映射实体列表
     */
    List<DomainLeaderMappingEntity> findByLeaderAccount(String leaderAccount);

    /**
     * 根据领导账号和租户代码查找领域到领导映射列表
     *
     * @param leaderAccount 领导账号
     * @param tenantCode    租户代码
     * @return 领域到领导映射实体列表
     */
    List<DomainLeaderMappingEntity> findByLeaderAccountAndTenantCode(String leaderAccount, String tenantCode);
}
