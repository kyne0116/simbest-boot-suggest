package com.simbest.boot.suggest.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simbest.boot.suggest.entity.CommonWordEntity;
import com.simbest.boot.suggest.repository.CommonWordRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 常用词服务类
 * 提供常用词相关的业务逻辑处理
 */
@Service
@Slf4j
public class CommonWordService {

    @Autowired
    private CommonWordRepository commonWordRepository;

    /**
     * 获取所有常用词
     *
     * @return 常用词列表
     */
    public List<CommonWordEntity> getAllCommonWords() {
        return commonWordRepository.findAll();
    }

    /**
     * 根据ID获取常用词
     *
     * @param id 常用词ID
     * @return 常用词（可选）
     */
    public Optional<CommonWordEntity> getCommonWordById(Long id) {
        return commonWordRepository.findById(id);
    }

    /**
     * 根据租户代码获取常用词列表
     *
     * @param tenantCode 租户代码
     * @return 常用词列表
     */
    public List<CommonWordEntity> getCommonWordsByTenantCode(String tenantCode) {
        return commonWordRepository.findByTenantCode(tenantCode);
    }

    /**
     * 根据类别和租户代码获取常用词列表
     *
     * @param category   类别
     * @param tenantCode 租户代码
     * @return 常用词列表
     */
    public List<CommonWordEntity> getCommonWordsByCategoryAndTenantCode(String category, String tenantCode) {
        return commonWordRepository.findByCategoryAndTenantCode(category, tenantCode);
    }

    /**
     * 创建常用词
     *
     * @param commonWord 常用词实体
     * @return 创建后的常用词实体
     */
    @Transactional
    public CommonWordEntity createCommonWord(CommonWordEntity commonWord) {
        commonWord.setCreateTime(new Date());
        commonWord.setUpdateTime(new Date());
        return commonWordRepository.save(commonWord);
    }

    /**
     * 更新常用词
     *
     * @param commonWord 常用词实体
     * @return 更新后的常用词实体
     */
    @Transactional
    public CommonWordEntity updateCommonWord(CommonWordEntity commonWord) {
        commonWord.setUpdateTime(new Date());
        return commonWordRepository.save(commonWord);
    }

    /**
     * 删除常用词
     *
     * @param id 常用词ID
     */
    @Transactional
    public void deleteCommonWord(Long id) {
        commonWordRepository.deleteById(id);
    }

    /**
     * 获取常用词列表（仅词语）
     *
     * @param tenantCode 租户代码
     * @return 常用词列表
     */
    public List<String> getCommonWordList(String tenantCode) {
        List<CommonWordEntity> entities = getCommonWordsByTenantCode(tenantCode);
        return entities.stream()
                .map(CommonWordEntity::getWord)
                .collect(Collectors.toList());
    }
}
