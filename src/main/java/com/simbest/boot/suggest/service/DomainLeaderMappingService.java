package com.simbest.boot.suggest.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simbest.boot.suggest.entity.DomainLeaderMappingEntity;
import com.simbest.boot.suggest.repository.DomainLeaderMappingRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 领域到领导映射服务类
 * 提供领域到领导映射相关的业务逻辑处理
 */
@Service
@Slf4j
public class DomainLeaderMappingService {

    @Autowired
    private DomainLeaderMappingRepository domainLeaderMappingRepository;

    /**
     * 获取所有领域到领导映射
     *
     * @return 领域到领导映射列表
     */
    public List<DomainLeaderMappingEntity> getAllMappings() {
        return domainLeaderMappingRepository.findAll();
    }

    /**
     * 根据ID获取领域到领导映射
     *
     * @param id 领域到领导映射ID
     * @return 领域到领导映射（可选）
     */
    public Optional<DomainLeaderMappingEntity> getMappingById(Long id) {
        return domainLeaderMappingRepository.findById(id);
    }

    /**
     * 根据租户代码获取领域到领导映射列表
     *
     * @param tenantCode 租户代码
     * @return 领域到领导映射列表
     */
    public List<DomainLeaderMappingEntity> getMappingsByTenantCode(String tenantCode) {
        return domainLeaderMappingRepository.findByTenantCode(tenantCode);
    }

    /**
     * 根据领域名称和租户代码获取领域到领导映射
     *
     * @param domainName 领域名称
     * @param tenantCode 租户代码
     * @return 领域到领导映射（可选）
     */
    public Optional<DomainLeaderMappingEntity> getMappingByDomainNameAndTenantCode(String domainName,
            String tenantCode) {
        return domainLeaderMappingRepository.findByDomainNameAndTenantCode(domainName, tenantCode);
    }

    /**
     * 根据领导账号和租户代码获取领域到领导映射列表
     *
     * @param leaderAccount 领导账号
     * @param tenantCode    租户代码
     * @return 领域到领导映射列表
     */
    public List<DomainLeaderMappingEntity> getMappingsByLeaderAccountAndTenantCode(String leaderAccount,
            String tenantCode) {
        return domainLeaderMappingRepository.findByLeaderAccountAndTenantCode(leaderAccount, tenantCode);
    }

    /**
     * 创建领域到领导映射
     *
     * @param mapping 领域到领导映射实体
     * @return 创建后的领域到领导映射实体
     */
    @Transactional
    public DomainLeaderMappingEntity createMapping(DomainLeaderMappingEntity mapping) {
        mapping.setCreateTime(new Date());
        mapping.setUpdateTime(new Date());
        return domainLeaderMappingRepository.save(mapping);
    }

    /**
     * 更新领域到领导映射
     *
     * @param mapping 领域到领导映射实体
     * @return 更新后的领域到领导映射实体
     */
    @Transactional
    public DomainLeaderMappingEntity updateMapping(DomainLeaderMappingEntity mapping) {
        mapping.setUpdateTime(new Date());
        return domainLeaderMappingRepository.save(mapping);
    }

    /**
     * 删除领域到领导映射
     *
     * @param id 领域到领导映射ID
     */
    @Transactional
    public void deleteMapping(Long id) {
        domainLeaderMappingRepository.deleteById(id);
    }

    /**
     * 获取领域名称到领导账号的映射
     *
     * @param tenantCode 租户代码
     * @return 领域名称到领导账号的映射
     */
    public Map<String, String> getDomainLeaderMapping(String tenantCode) {
        List<DomainLeaderMappingEntity> mappings = getMappingsByTenantCode(tenantCode);
        Map<String, String> result = new HashMap<>();

        for (DomainLeaderMappingEntity mapping : mappings) {
            result.put(mapping.getDomainName(), mapping.getLeaderAccount());
        }

        return result;
    }

    /**
     * 根据领导账号获取职责领域名称列表
     *
     * @param leaderAccount 领导账号
     * @return 职责领域名称列表
     */
    public List<String> getDomainNamesByLeaderAccount(String leaderAccount) {
        List<DomainLeaderMappingEntity> mappings = domainLeaderMappingRepository.findByLeaderAccount(leaderAccount);
        return mappings.stream()
                .map(DomainLeaderMappingEntity::getDomainName)
                .collect(Collectors.toList());
    }

    /**
     * 根据领导账号和租户代码获取职责领域名称列表
     *
     * @param leaderAccount 领导账号
     * @param tenantCode    租户代码
     * @return 职责领域名称列表
     */
    public List<String> getDomainNamesByLeaderAccountAndTenantCode(String leaderAccount, String tenantCode) {
        List<DomainLeaderMappingEntity> mappings = domainLeaderMappingRepository
                .findByLeaderAccountAndTenantCode(leaderAccount, tenantCode);
        return mappings.stream()
                .map(DomainLeaderMappingEntity::getDomainName)
                .collect(Collectors.toList());
    }
}
