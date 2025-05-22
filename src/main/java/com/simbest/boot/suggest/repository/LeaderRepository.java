package com.simbest.boot.suggest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.simbest.boot.suggest.entity.LeaderEntity;

/**
 * 领导数据访问接口
 */
@Repository
public interface LeaderRepository extends JpaRepository<LeaderEntity, Long> {

    /**
     * 根据账号查找领导
     * 
     * @param account 领导账号
     * @return 领导实体（可选）
     */
    Optional<LeaderEntity> findByAccount(String account);

    /**
     * 根据租户代码查找领导列表
     * 
     * @param tenantCode 租户代码
     * @return 领导实体列表
     */
    List<LeaderEntity> findByTenantCode(String tenantCode);

    /**
     * 根据领域ID查找负责该领域的领导列表
     * 
     * @param domainId 领域ID
     * @return 领导实体列表
     */
    @Query("SELECT l FROM LeaderEntity l JOIN l.domainIds d WHERE d = :domainId")
    List<LeaderEntity> findByDomainId(@Param("domainId") String domainId);

    /**
     * 根据租户代码和领域ID查找负责该领域的领导列表
     * 
     * @param tenantCode 租户代码
     * @param domainId 领域ID
     * @return 领导实体列表
     */
    @Query("SELECT l FROM LeaderEntity l JOIN l.domainIds d WHERE l.tenantCode = :tenantCode AND d = :domainId")
    List<LeaderEntity> findByTenantCodeAndDomainId(
            @Param("tenantCode") String tenantCode,
            @Param("domainId") String domainId);
}
