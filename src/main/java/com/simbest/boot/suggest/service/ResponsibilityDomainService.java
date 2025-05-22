package com.simbest.boot.suggest.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simbest.boot.suggest.entity.ResponsibilityDomainEntity;
import com.simbest.boot.suggest.model.ResponsibilityDomain;
import com.simbest.boot.suggest.repository.ResponsibilityDomainRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 职责领域服务类
 * 提供职责领域相关的业务逻辑处理
 */
@Service
@Slf4j
public class ResponsibilityDomainService {

    @Autowired
    private ResponsibilityDomainRepository responsibilityDomainRepository;

    /**
     * 获取所有职责领域
     *
     * @return 职责领域列表
     */
    public List<ResponsibilityDomainEntity> getAllDomains() {
        return responsibilityDomainRepository.findAll();
    }

    /**
     * 根据ID获取职责领域
     *
     * @param id 职责领域ID
     * @return 职责领域（可选）
     */
    public Optional<ResponsibilityDomainEntity> getDomainById(Long id) {
        return responsibilityDomainRepository.findById(id);
    }

    /**
     * 根据租户代码获取职责领域列表
     *
     * @param tenantCode 租户代码
     * @return 职责领域列表
     */
    public List<ResponsibilityDomainEntity> getDomainsByTenantCode(String tenantCode) {
        return responsibilityDomainRepository.findByTenantCode(tenantCode);
    }

    /**
     * 根据领域ID获取职责领域
     *
     * @param domainId 领域ID
     * @return 职责领域（可选）
     */
    public Optional<ResponsibilityDomainEntity> getDomainById(String domainId) {
        return responsibilityDomainRepository.findByDomainId(domainId);
    }

    /**
     * 创建职责领域
     *
     * @param domain 职责领域实体
     * @return 创建后的职责领域实体
     */
    @Transactional
    public ResponsibilityDomainEntity createDomain(ResponsibilityDomainEntity domain) {
        domain.setCreateTime(new Date());
        domain.setUpdateTime(new Date());
        return responsibilityDomainRepository.save(domain);
    }

    /**
     * 更新职责领域
     *
     * @param domain 职责领域实体
     * @return 更新后的职责领域实体
     */
    @Transactional
    public ResponsibilityDomainEntity updateDomain(ResponsibilityDomainEntity domain) {
        domain.setUpdateTime(new Date());
        return responsibilityDomainRepository.save(domain);
    }

    /**
     * 删除职责领域
     *
     * @param id 职责领域ID
     */
    @Transactional
    public void deleteDomain(Long id) {
        responsibilityDomainRepository.deleteById(id);
    }

    /**
     * 将实体对象转换为模型对象
     *
     * @param entity 职责领域实体
     * @return 职责领域模型
     */
    public ResponsibilityDomain convertToModel(ResponsibilityDomainEntity entity) {
        if (entity == null) {
            return null;
        }

        ResponsibilityDomain model = new ResponsibilityDomain();
        model.setDomainId(entity.getDomainId());
        model.setDomainName(entity.getDomainName());
        model.setDescription(entity.getDescription());

        model.setKeywords(entity.getKeywords());

        return model;
    }

    /**
     * 将实体对象列表转换为模型对象列表
     *
     * @param entities 职责领域实体列表
     * @return 职责领域模型列表
     */
    public List<ResponsibilityDomain> convertToModelList(List<ResponsibilityDomainEntity> entities) {
        return entities.stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    /**
     * 将模型对象转换为实体对象
     *
     * @param model      职责领域模型
     * @param tenantCode 租户代码
     * @return 职责领域实体
     */
    public ResponsibilityDomainEntity convertToEntity(ResponsibilityDomain model, String tenantCode) {
        if (model == null) {
            return null;
        }

        ResponsibilityDomainEntity entity = new ResponsibilityDomainEntity();
        entity.setDomainId(model.getDomainId());
        entity.setDomainName(model.getDomainName());
        entity.setDescription(model.getDescription());

        entity.setKeywords(model.getKeywords());
        entity.setTenantCode(tenantCode);

        return entity;
    }
}
