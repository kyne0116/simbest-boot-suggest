package com.simbest.boot.suggest.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simbest.boot.suggest.entity.OrganizationEntity;
import com.simbest.boot.suggest.model.Organization;
import com.simbest.boot.suggest.repository.OrganizationRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 组织服务类
 * 提供组织相关的业务逻辑处理
 */
@Service
@Slf4j
public class OrganizationEntityService {

    @Autowired
    private OrganizationRepository organizationRepository;

    /**
     * 获取所有组织
     *
     * @return 组织列表
     */
    public List<OrganizationEntity> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    /**
     * 根据ID获取组织
     *
     * @param id 组织ID
     * @return 组织（可选）
     */
    public Optional<OrganizationEntity> getOrganizationById(Long id) {
        return organizationRepository.findById(id);
    }

    /**
     * 根据租户代码获取组织列表
     *
     * @param tenantCode 租户代码
     * @return 组织列表
     */
    public List<OrganizationEntity> getOrganizationsByTenantCode(String tenantCode) {
        return organizationRepository.findByTenantCode(tenantCode);
    }

    /**
     * 根据组织ID获取组织
     *
     * @param orgId 组织ID
     * @return 组织（可选）
     */
    public Optional<OrganizationEntity> getOrganizationByOrgId(String orgId) {
        return organizationRepository.findByOrgId(orgId);
    }

    /**
     * 根据父组织ID获取组织列表
     *
     * @param parentOrgId 父组织ID
     * @return 组织列表
     */
    public List<OrganizationEntity> getOrganizationsByParentOrgId(String parentOrgId) {
        return organizationRepository.findByParentOrgId(parentOrgId);
    }

    /**
     * 根据主管领导账号获取组织列表
     *
     * @param mainLeaderAccount 主管领导账号
     * @return 组织列表
     */
    public List<OrganizationEntity> getOrganizationsByMainLeaderAccount(String mainLeaderAccount) {
        return organizationRepository.findByMainLeaderAccount(mainLeaderAccount);
    }

    /**
     * 根据租户代码和组织类型获取组织列表
     *
     * @param tenantCode 租户代码
     * @param orgType    组织类型
     * @return 组织列表
     */
    public List<OrganizationEntity> getOrganizationsByTenantCodeAndOrgType(String tenantCode, String orgType) {
        return organizationRepository.findByTenantCodeAndOrgType(tenantCode, orgType);
    }

    /**
     * 创建组织
     *
     * @param organization 组织实体
     * @return 创建后的组织实体
     */
    @Transactional
    public OrganizationEntity createOrganization(OrganizationEntity organization) {
        organization.setCreateTime(new Date());
        organization.setUpdateTime(new Date());
        return organizationRepository.save(organization);
    }

    /**
     * 更新组织
     *
     * @param organization 组织实体
     * @return 更新后的组织实体
     */
    @Transactional
    public OrganizationEntity updateOrganization(OrganizationEntity organization) {
        organization.setUpdateTime(new Date());
        return organizationRepository.save(organization);
    }

    /**
     * 删除组织
     *
     * @param id 组织ID
     */
    @Transactional
    public void deleteOrganization(Long id) {
        organizationRepository.deleteById(id);
    }

    /**
     * 将实体对象转换为模型对象
     *
     * @param entity 组织实体
     * @return 组织模型
     */
    public Organization convertToModel(OrganizationEntity entity) {
        if (entity == null) {
            return null;
        }

        Organization model = new Organization();
        model.setOrgId(entity.getOrgId());
        model.setOrgName(entity.getOrgName());
        model.setParentOrgId(entity.getParentOrgId());
        model.setMainLeaderAccount(entity.getMainLeaderAccount());
        model.setDeputyLeaderAccounts(entity.getDeputyLeaderAccounts());
        model.setSuperiorLeaderAccount(entity.getSuperiorLeaderAccount());
        model.setOrgType(entity.getOrgType());

        return model;
    }

    /**
     * 将实体对象列表转换为模型对象列表
     *
     * @param entities 组织实体列表
     * @return 组织模型列表
     */
    public List<Organization> convertToModelList(List<OrganizationEntity> entities) {
        return entities.stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    /**
     * 将模型对象转换为实体对象
     *
     * @param model      组织模型
     * @param tenantCode 租户代码
     * @return 组织实体
     */
    public OrganizationEntity convertToEntity(Organization model, String tenantCode) {
        if (model == null) {
            return null;
        }

        OrganizationEntity entity = new OrganizationEntity();
        entity.setOrgId(model.getOrgId());
        entity.setOrgName(model.getOrgName());
        entity.setParentOrgId(model.getParentOrgId());
        entity.setMainLeaderAccount(model.getMainLeaderAccount());
        entity.setDeputyLeaderAccounts(model.getDeputyLeaderAccounts());
        entity.setSuperiorLeaderAccount(model.getSuperiorLeaderAccount());
        entity.setOrgType(model.getOrgType());
        entity.setTenantCode(tenantCode);

        return entity;
    }
}
