package com.simbest.boot.suggest.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simbest.boot.suggest.entity.OrganizationDomainEntity;
import com.simbest.boot.suggest.entity.OrganizationEntity;
import com.simbest.boot.suggest.entity.ResponsibilityDomainEntity;
import com.simbest.boot.suggest.model.Leader;
import com.simbest.boot.suggest.model.Organization;
import com.simbest.boot.suggest.model.ResponsibilityDomain;
import com.simbest.boot.suggest.repository.OrganizationDomainRepository;
import com.simbest.boot.suggest.repository.OrganizationRepository;
import com.simbest.boot.suggest.repository.ResponsibilityDomainRepository;
import com.simbest.boot.suggest.util.DatabaseDataLoader;

import lombok.extern.slf4j.Slf4j;

/**
 * 组织管理服务
 * 提供组织相关的操作
 * 整合了OrganizationService和OrganizationEntityService的功能
 */
@Service
@Slf4j
public class OrganizationService {
    private Map<String, Organization> organizations; // 组织ID到组织的映射
    private Map<String, List<String>> mainLeaderOrgMap; // 主管领导账号到组织ID的映射
    private Map<String, List<String>> deputyLeaderOrgMap; // 分管领导账号到组织ID的映射
    private Map<String, List<String>> superiorLeaderOrgMap; // 上级领导账号到组织ID的映射

    @Autowired
    private LeaderService leaderService; // 领导服务

    @Autowired
    private DatabaseDataLoader databaseDataLoader;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationDomainRepository organizationDomainRepository;

    @Autowired
    private ResponsibilityDomainRepository responsibilityDomainRepository;

    @Value("${app.tenantDefaultCode:default}")
    private String tenantCode; // 默认租户代码

    /**
     * 构造函数
     */
    public OrganizationService() {
        this.organizations = new HashMap<>();
        this.mainLeaderOrgMap = new HashMap<>();
        this.deputyLeaderOrgMap = new HashMap<>();
        this.superiorLeaderOrgMap = new HashMap<>();
    }

    /**
     * 初始化方法，由Spring在注入依赖后调用
     */
    @Autowired
    public void init() {
        initOrganizations();
    }

    /**
     * 初始化组织数据
     */
    public void initOrganizations() {
        // 从数据库加载组织数据
        List<Organization> orgList = databaseDataLoader.loadOrganizations(tenantCode);
        for (Organization org : orgList) {
            addOrganization(org);
        }

        log.info("已初始化 {} 个组织数据", organizations.size());
    }

    /**
     * 添加组织
     *
     * @param organization 组织对象
     */
    public void addOrganization(Organization organization) {
        organizations.put(organization.getOrgId(), organization);

        // 更新主管领导到组织的映射
        String mainLeaderAccount = organization.getMainLeaderAccount();
        if (mainLeaderAccount != null && !mainLeaderAccount.isEmpty()) {
            mainLeaderOrgMap.computeIfAbsent(mainLeaderAccount, k -> new ArrayList<>()).add(organization.getOrgId());
        }

        // 更新分管领导到组织的映射
        List<String> deputyLeaderAccounts = organization.getDeputyLeaderAccounts();
        if (deputyLeaderAccounts != null) {
            for (String deputyLeaderAccount : deputyLeaderAccounts) {
                if (deputyLeaderAccount != null && !deputyLeaderAccount.isEmpty()) {
                    deputyLeaderOrgMap.computeIfAbsent(deputyLeaderAccount, k -> new ArrayList<>())
                            .add(organization.getOrgId());
                }
            }
        }

        // 更新上级领导到组织的映射
        String superiorLeaderAccount = organization.getSuperiorLeaderAccount();
        if (superiorLeaderAccount != null && !superiorLeaderAccount.isEmpty()) {
            superiorLeaderOrgMap.computeIfAbsent(superiorLeaderAccount, k -> new ArrayList<>())
                    .add(organization.getOrgId());
        }
    }

    /**
     * 根据组织ID获取组织
     *
     * @param orgId 组织ID
     * @return 组织对象
     */
    public Organization getOrganizationById(String orgId) {
        return organizations.get(orgId);
    }

    /**
     * 根据组织ID获取主管领导账号
     *
     * @param orgId 组织ID
     * @return 主管领导账号
     */
    public String getMainLeaderAccountByOrgId(String orgId) {
        Organization org = organizations.get(orgId);
        return org != null ? org.getMainLeaderAccount() : null;
    }

    /**
     * 根据组织ID获取分管领导账号列表
     *
     * @param orgId 组织ID
     * @return 分管领导账号列表
     */
    public List<String> getDeputyLeaderAccountsByOrgId(String orgId) {
        Organization org = organizations.get(orgId);
        return org != null ? org.getDeputyLeaderAccounts() : new ArrayList<>();
    }

    /**
     * 根据组织ID获取上级领导账号
     *
     * @param orgId 组织ID
     * @return 上级领导账号
     */
    public String getSuperiorLeaderAccountByOrgId(String orgId) {
        Organization org = organizations.get(orgId);
        return org != null ? org.getSuperiorLeaderAccount() : null;
    }

    /**
     * 获取组织的上级组织
     *
     * @param orgId 组织ID
     * @return 上级组织
     */
    public Organization getParentOrganization(String orgId) {
        Organization org = getOrganizationById(orgId);
        if (org != null && org.getParentOrgId() != null) {
            return getOrganizationById(org.getParentOrgId());
        }
        return null;
    }

    /**
     * 根据领导账号获取其作为主管的组织ID列表
     *
     * @param leaderAccount 领导账号
     * @return 组织ID列表
     */
    public List<String> getOrgIdsAsMainLeader(String leaderAccount) {
        return mainLeaderOrgMap.getOrDefault(leaderAccount, new ArrayList<>());
    }

    /**
     * 根据领导账号获取其作为分管的组织ID列表
     *
     * @param leaderAccount 领导账号
     * @return 组织ID列表
     */
    public List<String> getOrgIdsAsDeputyLeader(String leaderAccount) {
        return deputyLeaderOrgMap.getOrDefault(leaderAccount, new ArrayList<>());
    }

    /**
     * 根据领导账号获取其作为上级领导的组织ID列表
     *
     * @param leaderAccount 领导账号
     * @return 组织ID列表
     */
    public List<String> getOrgIdsAsSuperiorLeader(String leaderAccount) {
        return superiorLeaderOrgMap.getOrDefault(leaderAccount, new ArrayList<>());
    }

    /**
     * 获取领导作为主管的所有组织
     *
     * @param leaderAccount 领导账号
     * @return 领导作为主管的组织列表
     */
    public List<Organization> getOrganizationsAsMainLeader(String leaderAccount) {
        List<Organization> result = new ArrayList<>();
        List<String> orgIds = getOrgIdsAsMainLeader(leaderAccount);
        for (String orgId : orgIds) {
            Organization org = getOrganizationById(orgId);
            if (org != null) {
                result.add(org);
            }
        }
        return result;
    }

    /**
     * 获取领导作为分管的所有组织
     *
     * @param leaderAccount 领导账号
     * @return 领导作为分管的组织列表
     */
    public List<Organization> getOrganizationsAsDeputyLeader(String leaderAccount) {
        List<Organization> result = new ArrayList<>();
        List<String> orgIds = getOrgIdsAsDeputyLeader(leaderAccount);
        for (String orgId : orgIds) {
            Organization org = getOrganizationById(orgId);
            if (org != null) {
                result.add(org);
            }
        }
        return result;
    }

    /**
     * 获取领导作为上级领导的所有组织
     *
     * @param leaderAccount 领导账号
     * @return 领导作为上级领导的组织列表
     */
    public List<Organization> getOrganizationsAsSuperiorLeader(String leaderAccount) {
        List<Organization> result = new ArrayList<>();
        List<String> orgIds = getOrgIdsAsSuperiorLeader(leaderAccount);
        for (String orgId : orgIds) {
            Organization org = getOrganizationById(orgId);
            if (org != null) {
                result.add(org);
            }
        }
        return result;
    }

    /**
     * 获取领导所有相关的组织（包括作为主管、分管和上级的）
     *
     * @param leaderAccount 领导账号
     * @return 领导相关的所有组织列表
     */
    public List<Organization> getAllOrganizationsForLeader(String leaderAccount) {
        List<Organization> result = new ArrayList<>();
        result.addAll(getOrganizationsAsMainLeader(leaderAccount));
        result.addAll(getOrganizationsAsDeputyLeader(leaderAccount));
        result.addAll(getOrganizationsAsSuperiorLeader(leaderAccount));
        return result;
    }

    /**
     * 判断领导是否是某个组织的主管
     *
     * @param leaderAccount 领导账号
     * @param orgId         组织ID
     * @return 是否是主管
     */
    public boolean isMainLeaderOfOrganization(String leaderAccount, String orgId) {
        Organization org = getOrganizationById(orgId);
        return org != null && leaderAccount.equals(org.getMainLeaderAccount());
    }

    /**
     * 判断领导是否是某个组织的分管
     *
     * @param leaderAccount 领导账号
     * @param orgId         组织ID
     * @return 是否是分管
     */
    public boolean isDeputyLeaderOfOrganization(String leaderAccount, String orgId) {
        Organization org = getOrganizationById(orgId);
        return org != null && org.getDeputyLeaderAccounts() != null &&
                org.getDeputyLeaderAccounts().contains(leaderAccount);
    }

    /**
     * 判断领导是否是某个组织的上级领导
     *
     * @param leaderAccount 领导账号
     * @param orgId         组织ID
     * @return 是否是上级领导
     */
    public boolean isSuperiorLeaderOfOrganization(String leaderAccount, String orgId) {
        Organization org = getOrganizationById(orgId);
        return org != null && leaderAccount.equals(org.getSuperiorLeaderAccount());
    }

    /**
     * 获取所有组织
     *
     * @return 所有组织的列表
     */
    public List<Organization> getAllOrganizations() {
        return new ArrayList<>(organizations.values());
    }

    /**
     * 获取所有主管领导账号
     *
     * @return 所有主管领导账号的列表
     */
    public List<String> getAllMainLeaderAccounts() {
        return new ArrayList<>(mainLeaderOrgMap.keySet());
    }

    /**
     * 获取所有分管领导账号
     *
     * @return 所有分管领导账号的列表
     */
    public List<String> getAllDeputyLeaderAccounts() {
        return new ArrayList<>(deputyLeaderOrgMap.keySet());
    }

    /**
     * 获取所有上级领导账号
     *
     * @return 所有上级领导账号的列表
     */
    public List<String> getAllSuperiorLeaderAccounts() {
        return new ArrayList<>(superiorLeaderOrgMap.keySet());
    }

    /**
     * 获取所有领导账号
     *
     * @return 所有领导账号的列表
     */
    public List<String> getAllLeaderAccounts() {
        List<String> result = new ArrayList<>();
        result.addAll(getAllMainLeaderAccounts());
        result.addAll(getAllDeputyLeaderAccounts());
        result.addAll(getAllSuperiorLeaderAccounts());
        return result;
    }

    /**
     * 根据任务标题找到最合适的分管领导
     *
     * @param org       组织
     * @param taskTitle 任务标题
     * @return 最合适的分管领导账号
     * @throws java.io.IOException 如果初始化分词器或同义词表失败
     */
    public String findBestDeputyLeaderByTaskTitle(Organization org, String taskTitle) throws java.io.IOException {
        if (taskTitle == null || taskTitle.isEmpty() ||
                org.getDeputyLeaderAccounts() == null || org.getDeputyLeaderAccounts().isEmpty() ||
                leaderService == null) {
            return null;
        }

        String bestLeaderAccount = null;
        double bestScore = 0.0;

        // 遍历所有分管领导
        for (String leaderAccount : org.getDeputyLeaderAccounts()) {
            Leader leader = leaderService.getLeaderByAccountModel(leaderAccount);
            if (leader == null) {
                continue;
            }

            // 计算该领导负责的领域与任务标题的匹配度
            double personalScore = leaderService.calculateDomainMatchScore(leader, taskTitle);

            // 计算该领导所在组织与任务标题的匹配度
            double organizationScore = 0.0;
            List<Organization> leaderOrgs = getOrganizationsAsDeputyLeader(leaderAccount);
            for (Organization leaderOrg : leaderOrgs) {
                double orgScore = calculateOrganizationMatchScore(leaderOrg, taskTitle);
                organizationScore = Math.max(organizationScore, orgScore);
            }

            // 组织分数占70%，个人分数占30%
            double finalScore = organizationScore * 0.7 + personalScore * 0.3;

            // 更新最佳匹配
            if (finalScore > bestScore) {
                bestScore = finalScore;
                bestLeaderAccount = leaderAccount;
            }
        }

        return bestLeaderAccount;
    }

    // ========== 组织领域关联相关方法 ==========

    /**
     * 获取组织关联的所有职责领域ID
     *
     * @param organizationId 组织ID
     * @return 职责领域ID列表
     */
    public List<String> getOrganizationDomainIds(String organizationId) {
        List<OrganizationDomainEntity> domainEntities = organizationDomainRepository
                .findByOrganizationId(organizationId);
        return domainEntities.stream()
                .map(OrganizationDomainEntity::getDomainId)
                .collect(Collectors.toList());
    }

    /**
     * 获取组织-领域关联的权重
     *
     * @param organizationId 组织ID
     * @param domainId       领域ID
     * @return 权重值，如果不存在则返回默认值1.0
     */
    public double getOrganizationDomainWeight(String organizationId, String domainId) {
        Optional<OrganizationDomainEntity> entity = organizationDomainRepository
                .findByOrganizationIdAndDomainId(organizationId, domainId);
        return entity.map(OrganizationDomainEntity::getDomainWeight).orElse(1.0);
    }

    /**
     * 根据领域ID获取职责领域
     *
     * @param domainId 领域ID
     * @return 职责领域对象
     */
    public ResponsibilityDomain getDomainById(String domainId) {
        Optional<ResponsibilityDomainEntity> entityOpt = responsibilityDomainRepository.findByDomainId(domainId);
        if (!entityOpt.isPresent()) {
            return null;
        }

        ResponsibilityDomainEntity entity = entityOpt.get();
        ResponsibilityDomain domain = new ResponsibilityDomain();
        domain.setDomainId(entity.getDomainId());
        domain.setDomainName(entity.getDomainName());
        domain.setDescription(entity.getDescription());
        domain.setKeywords(entity.getKeywords());

        return domain;
    }

    /**
     * 计算组织与任务标题的匹配度
     *
     * @param organization 组织
     * @param taskTitle    任务标题
     * @return 匹配度分数
     */
    public double calculateOrganizationMatchScore(Organization organization, String taskTitle)
            throws java.io.IOException {
        if (organization == null || taskTitle == null || taskTitle.isEmpty()) {
            return 0.0;
        }

        // 1. 基于组织关键词的匹配
        double keywordMatchScore = 0.0;
        if (organization.getKeywords() != null && !organization.getKeywords().isEmpty()) {
            // 这里可以实现关键词匹配算法，或者调用现有的匹配方法
            // 简单实现：检查任务标题是否包含组织关键词
            for (int i = 0; i < organization.getKeywords().size(); i++) {
                String keyword = organization.getKeywords().get(i);
                double weight = (organization.getKeywordWeights() != null
                        && i < organization.getKeywordWeights().size())
                                ? organization.getKeywordWeights().get(i)
                                : 1.0;

                if (taskTitle.contains(keyword)) {
                    keywordMatchScore += weight;
                }
            }

            // 归一化分数
            if (!organization.getKeywords().isEmpty()) {
                keywordMatchScore = keywordMatchScore / organization.getKeywords().size();
            }
        }

        // 2. 基于组织关联的职责领域的匹配
        double domainMatchScore = 0.0;
        List<String> domainIds = getOrganizationDomainIds(organization.getOrgId());
        if (domainIds != null && !domainIds.isEmpty()) {
            for (String domainId : domainIds) {
                ResponsibilityDomain domain = getDomainById(domainId);
                if (domain != null) {
                    double score = domain.calculateMatchScore(taskTitle);
                    // 获取该组织-领域的权重
                    double weight = getOrganizationDomainWeight(organization.getOrgId(), domainId);
                    domainMatchScore = Math.max(domainMatchScore, score * weight);
                }
            }
        }

        // 组织关键词匹配占30%，职责领域匹配占70%
        return keywordMatchScore * 0.3 + domainMatchScore * 0.7;
    }

    // ========== 以下是从OrganizationEntityService整合的方法 ==========

    /**
     * 获取所有组织实体
     *
     * @return 组织实体列表
     */
    public List<OrganizationEntity> getAllOrganizationsEntities() {
        return organizationRepository.findAll();
    }

    /**
     * 根据ID获取组织实体
     *
     * @param id 组织ID
     * @return 组织实体（可选）
     */
    public Optional<OrganizationEntity> getOrganizationEntityById(Long id) {
        return organizationRepository.findById(id);
    }

    /**
     * 根据租户代码获取组织实体列表
     *
     * @param tenantCode 租户代码
     * @return 组织实体列表
     */
    public List<OrganizationEntity> getOrganizationEntitiesByTenantCode(String tenantCode) {
        return organizationRepository.findByTenantCode(tenantCode);
    }

    /**
     * 根据组织ID获取组织实体
     *
     * @param orgId 组织ID
     * @return 组织实体（可选）
     */
    public Optional<OrganizationEntity> getOrganizationEntityByOrgId(String orgId) {
        return organizationRepository.findByOrgId(orgId);
    }

    /**
     * 根据父组织ID获取组织实体列表
     *
     * @param parentOrgId 父组织ID
     * @return 组织实体列表
     */
    public List<OrganizationEntity> getOrganizationEntitiesByParentOrgId(String parentOrgId) {
        return organizationRepository.findByParentOrgId(parentOrgId);
    }

    /**
     * 根据主管领导账号获取组织实体列表
     *
     * @param mainLeaderAccount 主管领导账号
     * @return 组织实体列表
     */
    public List<OrganizationEntity> getOrganizationEntitiesByMainLeaderAccount(String mainLeaderAccount) {
        return organizationRepository.findByMainLeaderAccount(mainLeaderAccount);
    }

    /**
     * 根据租户代码和组织类型获取组织实体列表
     *
     * @param tenantCode 租户代码
     * @param orgType    组织类型
     * @return 组织实体列表
     */
    public List<OrganizationEntity> getOrganizationEntitiesByTenantCodeAndOrgType(String tenantCode, String orgType) {
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
        if (entities == null) {
            return new ArrayList<>();
        }

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
