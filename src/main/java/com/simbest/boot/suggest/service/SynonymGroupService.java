package com.simbest.boot.suggest.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simbest.boot.suggest.entity.SynonymGroupEntity;
import com.simbest.boot.suggest.repository.SynonymGroupRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 同义词组服务类
 * 提供同义词组相关的业务逻辑处理
 */
@Service
@Slf4j
public class SynonymGroupService {

    @Autowired
    private SynonymGroupRepository synonymGroupRepository;

    /**
     * 获取所有同义词组
     *
     * @return 同义词组列表
     */
    public List<SynonymGroupEntity> getAllSynonymGroups() {
        return synonymGroupRepository.findAll();
    }

    /**
     * 根据ID获取同义词组
     *
     * @param id 同义词组ID
     * @return 同义词组（可选）
     */
    public Optional<SynonymGroupEntity> getSynonymGroupById(Long id) {
        return synonymGroupRepository.findById(id);
    }

    /**
     * 根据租户代码获取同义词组列表
     *
     * @param tenantCode 租户代码
     * @return 同义词组列表
     */
    public List<SynonymGroupEntity> getSynonymGroupsByTenantCode(String tenantCode) {
        return synonymGroupRepository.findByTenantCode(tenantCode);
    }

    /**
     * 根据类别和租户代码获取同义词组列表
     *
     * @param category   类别
     * @param tenantCode 租户代码
     * @return 同义词组列表
     */
    public List<SynonymGroupEntity> getSynonymGroupsByCategoryAndTenantCode(String category, String tenantCode) {
        return synonymGroupRepository.findByCategoryAndTenantCode(category, tenantCode);
    }

    /**
     * 创建同义词组
     *
     * @param synonymGroup 同义词组实体
     * @return 创建后的同义词组实体
     */
    @Transactional
    public SynonymGroupEntity createSynonymGroup(SynonymGroupEntity synonymGroup) {
        synonymGroup.setCreateTime(new Date());
        synonymGroup.setUpdateTime(new Date());
        return synonymGroupRepository.save(synonymGroup);
    }

    /**
     * 更新同义词组
     *
     * @param synonymGroup 同义词组实体
     * @return 更新后的同义词组实体
     */
    @Transactional
    public SynonymGroupEntity updateSynonymGroup(SynonymGroupEntity synonymGroup) {
        synonymGroup.setUpdateTime(new Date());
        return synonymGroupRepository.save(synonymGroup);
    }

    /**
     * 删除同义词组
     *
     * @param id 同义词组ID
     */
    @Transactional
    public void deleteSynonymGroup(Long id) {
        synonymGroupRepository.deleteById(id);
    }

    /**
     * 获取同义词组列表（仅同义词组字符串）
     *
     * @param tenantCode 租户代码
     * @return 同义词组列表
     */
    public List<String> getSynonymGroupList(String tenantCode) {
        List<SynonymGroupEntity> entities = getSynonymGroupsByTenantCode(tenantCode);
        return entities.stream()
                .map(SynonymGroupEntity::getSynonymWords)
                .collect(Collectors.toList());
    }

    /**
     * 解析同义词组字符串为同义词列表
     *
     * @param synonymWords 同义词组字符串，格式为"词1,词2,词3"
     * @return 同义词列表
     */
    public List<String> parseSynonymWords(String synonymWords) {
        if (synonymWords == null || synonymWords.isEmpty()) {
            return new ArrayList<>();
        }

        String[] words = synonymWords.split(",");
        List<String> result = new ArrayList<>();

        for (String word : words) {
            String trimmed = word.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }

        return result;
    }
}
