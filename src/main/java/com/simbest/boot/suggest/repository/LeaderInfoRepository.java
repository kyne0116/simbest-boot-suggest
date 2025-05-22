package com.simbest.boot.suggest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.simbest.boot.suggest.entity.LeaderInfoEntity;

/**
 * 领导信息数据访问接口
 */
@Repository
public interface LeaderInfoRepository extends JpaRepository<LeaderInfoEntity, Long> {

    /**
     * 根据账号查找领导信息
     * 
     * @param account 领导账号
     * @return 领导信息实体（可选）
     */
    Optional<LeaderInfoEntity> findByAccount(String account);

    /**
     * 根据租户代码查找领导信息列表
     * 
     * @param tenantCode 租户代码
     * @return 领导信息实体列表
     */
    List<LeaderInfoEntity> findByTenantCode(String tenantCode);

    /**
     * 根据账号和租户代码查找领导信息
     * 
     * @param account 领导账号
     * @param tenantCode 租户代码
     * @return 领导信息实体（可选）
     */
    Optional<LeaderInfoEntity> findByAccountAndTenantCode(String account, String tenantCode);
}
