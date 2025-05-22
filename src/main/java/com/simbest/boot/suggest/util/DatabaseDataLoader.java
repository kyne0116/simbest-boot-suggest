package com.simbest.boot.suggest.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simbest.boot.suggest.entity.OrganizationDomainEntity;
import com.simbest.boot.suggest.model.Leader;
import com.simbest.boot.suggest.model.Organization;
import com.simbest.boot.suggest.model.ResponsibilityDomain;
import com.simbest.boot.suggest.repository.OrganizationDomainRepository;
import com.simbest.boot.suggest.service.CommonWordService;
import com.simbest.boot.suggest.service.ConfigService;
import com.simbest.boot.suggest.service.DomainLeaderMappingService;
import com.simbest.boot.suggest.service.LeaderService;
import com.simbest.boot.suggest.service.OrganizationEntityService;
import com.simbest.boot.suggest.service.ResponsibilityDomainService;
import com.simbest.boot.suggest.service.SynonymGroupService;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据库数据加载器
 * 负责从数据库加载数据，替代原来从资源文件加载的方式
 */
@Component
@Slf4j
public class DatabaseDataLoader {

    @Autowired
    private ResponsibilityDomainService responsibilityDomainService;

    @Autowired
    private OrganizationEntityService organizationEntityService;

    @Autowired
    private LeaderService leaderService;

    @Autowired
    private DomainLeaderMappingService domainLeaderMappingService;

    @Autowired
    private CommonWordService commonWordService;

    @Autowired
    private SynonymGroupService synonymGroupService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private OrganizationDomainRepository organizationDomainRepository;

    /**
     * 加载所有租户代码
     *
     * @return 租户代码列表
     */
    public List<String> loadAllTenantCodes() {
        log.info("从数据库加载所有租户代码");
        // 目前只有default租户，后续可以从数据库中加载
        List<String> tenantCodes = new ArrayList<>();
        tenantCodes.add("default");
        return tenantCodes;
    }

    /**
     * 加载组织数据
     *
     * @param tenantCode 租户代码
     * @return 组织列表
     */
    public List<Organization> loadOrganizations(String tenantCode) {
        log.info("从数据库加载组织数据，租户代码: {}", tenantCode);
        return organizationEntityService.getOrganizationsByTenantCode(tenantCode)
                .stream()
                .map(organizationEntityService::convertToModel)
                .collect(Collectors.toList());
    }

    /**
     * 加载领导数据
     *
     * @param tenantCode 租户代码
     * @return 领导列表
     */
    public List<Leader> loadLeaders(String tenantCode) {
        log.info("从数据库加载领导数据，租户代码: {}", tenantCode);
        return leaderService.getLeadersByTenantCode(tenantCode)
                .stream()
                .map(leaderService::convertToModel)
                .collect(Collectors.toList());
    }

    /**
     * 加载职责领域数据
     *
     * @param tenantCode 租户代码
     * @return 职责领域列表
     */
    public List<ResponsibilityDomain> loadDomains(String tenantCode) {
        log.info("从数据库加载职责领域数据，租户代码: {}", tenantCode);
        return responsibilityDomainService.getDomainsByTenantCode(tenantCode)
                .stream()
                .map(responsibilityDomainService::convertToModel)
                .collect(Collectors.toList());
    }

    /**
     * 加载领域到领导账号的映射
     *
     * @param tenantCode 租户代码
     * @return 领域名称到领导账号的映射
     */
    public Map<String, String> loadDomainLeaderMapping(String tenantCode) {
        log.info("从数据库加载领域到领导映射，租户代码: {}", tenantCode);
        return domainLeaderMappingService.getDomainLeaderMapping(tenantCode);
    }

    /**
     * 加载常用词列表
     *
     * @param tenantCode 租户代码
     * @return 常用词列表
     */
    public List<String> loadCommonWords(String tenantCode) {
        log.info("从数据库加载常用词，租户代码: {}", tenantCode);
        return commonWordService.getCommonWordList(tenantCode);
    }

    /**
     * 加载常用同义词组
     *
     * @param tenantCode 租户代码
     * @return 同义词组列表
     */
    public List<String> loadCommonSynonyms(String tenantCode) {
        log.info("从数据库加载同义词组，租户代码: {}", tenantCode);
        return synonymGroupService.getSynonymGroupList(tenantCode);
    }

    /**
     * 加载阈值配置
     *
     * @return 阈值配置
     */
    public Map<String, Object> loadThresholdConfig() {
        log.info("从数据库加载阈值配置");
        return configService.getConfigCategory("threshold", "default");
    }

    /**
     * 加载算法权重配置
     *
     * @return 算法权重配置
     */
    public Map<String, Object> loadAlgorithmWeights() {
        log.info("从数据库加载算法权重配置");
        return configService.getConfigCategory("algorithm", "default");
    }

    /**
     * 获取算法权重配置中的特定部分
     *
     * @param section 配置部分名称
     * @return 特定部分的配置
     */
    public Map<String, Object> getAlgorithmWeightSection(String section) {
        log.debug("从数据库加载算法权重配置部分: {}", section);
        return configService.getConfigSection("algorithm", section, "default");
    }

    /**
     * 加载AI分析配置
     *
     * @return AI分析配置
     */
    public Map<String, Object> loadAIAnalysisConfig() {
        log.info("从数据库加载AI分析配置");
        return configService.getConfigCategory("ai-analysis", "default");
    }

    /**
     * 获取AI分析配置中的特定部分
     *
     * @param section 配置部分名称
     * @return 特定部分的配置
     */
    public Map<String, Object> getAIAnalysisConfigSection(String section) {
        log.debug("从数据库加载AI分析配置部分: {}", section);
        return configService.getConfigSection("ai-analysis", section, "default");
    }

    /**
     * 加载推荐配置
     *
     * @return 推荐配置
     */
    public Map<String, Object> loadRecommendationConfig() {
        log.info("从数据库加载推荐配置");
        return configService.getConfigCategory("recommendation", "default");
    }

    /**
     * 获取推荐配置中的特定部分
     *
     * @param section 配置部分名称
     * @return 特定部分的配置
     */
    public Map<String, Object> getRecommendationConfigSection(String section) {
        log.debug("从数据库加载推荐配置部分: {}", section);
        return configService.getConfigSection("recommendation", section, "default");
    }

    /**
     * 加载组织-领域关联数据
     *
     * @param tenantCode 租户代码
     * @return 组织-领域关联列表
     */
    public List<OrganizationDomainEntity> loadOrganizationDomains(String tenantCode) {
        log.info("从数据库加载组织-领域关联数据，租户代码: {}", tenantCode);
        return organizationDomainRepository.findByTenantCode(tenantCode);
    }

    /**
     * 根据组织ID加载组织-领域关联数据
     *
     * @param organizationId 组织ID
     * @return 组织-领域关联列表
     */
    public List<OrganizationDomainEntity> loadOrganizationDomainsByOrganizationId(String organizationId) {
        log.info("从数据库加载组织-领域关联数据，组织ID: {}", organizationId);
        return organizationDomainRepository.findByOrganizationId(organizationId);
    }

    /**
     * 根据领域ID加载组织-领域关联数据
     *
     * @param domainId 领域ID
     * @return 组织-领域关联列表
     */
    public List<OrganizationDomainEntity> loadOrganizationDomainsByDomainId(String domainId) {
        log.info("从数据库加载组织-领域关联数据，领域ID: {}", domainId);
        return organizationDomainRepository.findByDomainId(domainId);
    }
}
