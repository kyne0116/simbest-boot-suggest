package com.simbest.boot.suggest.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simbest.boot.suggest.entity.LeaderEntity;
import com.simbest.boot.suggest.entity.ResponsibilityDomainEntity;
import com.simbest.boot.suggest.model.Leader;
import com.simbest.boot.suggest.model.ResponsibilityDomain;
import com.simbest.boot.suggest.repository.LeaderRepository;
import com.simbest.boot.suggest.repository.ResponsibilityDomainRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 领导服务类
 * 提供领导相关的业务逻辑处理
 */
@Service
@Slf4j
public class LeaderService {

    @Autowired
    private LeaderRepository leaderRepository;

    @Autowired
    private ResponsibilityDomainRepository responsibilityDomainRepository;

    /**
     * 获取所有领导
     *
     * @return 领导列表
     */
    public List<LeaderEntity> getAllLeadersEntities() {
        return leaderRepository.findAll();
    }

    /**
     * 获取所有领导
     *
     * @return 领导列表
     */
    public List<Leader> getAllLeaders() {
        return convertToModelList(getAllLeadersEntities());
    }

    /**
     * 根据ID获取领导
     *
     * @param id 领导ID
     * @return 领导（可选）
     */
    public Optional<LeaderEntity> getLeaderById(Long id) {
        return leaderRepository.findById(id);
    }

    /**
     * 根据租户代码获取领导列表
     *
     * @param tenantCode 租户代码
     * @return 领导列表
     */
    public List<LeaderEntity> getLeadersByTenantCode(String tenantCode) {
        return leaderRepository.findByTenantCode(tenantCode);
    }

    /**
     * 根据账号获取领导
     *
     * @param account 领导账号
     * @return 领导（可选）
     */
    public Optional<LeaderEntity> getLeaderByAccount(String account) {
        return leaderRepository.findByAccount(account);
    }

    /**
     * 根据账号获取领导
     *
     * @param account 领导账号
     * @return 领导
     */
    public Leader getLeaderByAccountModel(String account) {
        Optional<LeaderEntity> leaderOpt = getLeaderByAccount(account);
        return leaderOpt.map(this::convertToModel).orElse(null);
    }

    /**
     * 根据领域ID获取负责该领域的领导列表
     *
     * @param domainId 领域ID
     * @return 领导列表
     */
    public List<LeaderEntity> getLeadersByDomainId(String domainId) {
        return leaderRepository.findByDomainId(domainId);
    }

    /**
     * 根据租户代码和领域ID获取负责该领域的领导列表
     *
     * @param tenantCode 租户代码
     * @param domainId   领域ID
     * @return 领导列表
     */
    public List<LeaderEntity> getLeadersByTenantCodeAndDomainId(String tenantCode, String domainId) {
        return leaderRepository.findByTenantCodeAndDomainId(tenantCode, domainId);
    }

    /**
     * 创建领导
     *
     * @param leader 领导实体
     * @return 创建后的领导实体
     */
    @Transactional
    public LeaderEntity createLeader(LeaderEntity leader) {
        leader.setCreateTime(new Date());
        leader.setUpdateTime(new Date());
        return leaderRepository.save(leader);
    }

    /**
     * 更新领导
     *
     * @param leader 领导实体
     * @return 更新后的领导实体
     */
    @Transactional
    public LeaderEntity updateLeader(LeaderEntity leader) {
        leader.setUpdateTime(new Date());
        return leaderRepository.save(leader);
    }

    /**
     * 删除领导
     *
     * @param id 领导ID
     */
    @Transactional
    public void deleteLeader(Long id) {
        leaderRepository.deleteById(id);
    }

    /**
     * 将实体对象转换为模型对象
     *
     * @param entity 领导实体
     * @return 领导模型
     */
    public Leader convertToModel(LeaderEntity entity) {
        if (entity == null) {
            return null;
        }

        Leader model = new Leader();
        model.setAccount(entity.getAccount());
        model.setName(entity.getName());
        model.setDomainIds(entity.getDomainIds());

        return model;
    }

    /**
     * 将实体对象列表转换为模型对象列表
     *
     * @param entities 领导实体列表
     * @return 领导模型列表
     */
    public List<Leader> convertToModelList(List<LeaderEntity> entities) {
        return entities.stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    /**
     * 将模型对象转换为实体对象
     *
     * @param model      领导模型
     * @param tenantCode 租户代码
     * @return 领导实体
     */
    public LeaderEntity convertToEntity(Leader model, String tenantCode) {
        if (model == null) {
            return null;
        }

        LeaderEntity entity = new LeaderEntity();
        entity.setAccount(model.getAccount());
        entity.setName(model.getName());
        entity.setDomainIds(model.getDomainIds());
        entity.setTenantCode(tenantCode);

        return entity;
    }

    /**
     * 计算领导的职责领域与任务标题的匹配度
     *
     * @param leader    领导对象
     * @param taskTitle 任务标题
     * @return 匹配度分数
     * @throws java.io.IOException 如果初始化分词器或同义词表失败
     */
    public double calculateDomainMatchScore(Leader leader, String taskTitle) throws java.io.IOException {
        if (leader == null || taskTitle == null || taskTitle.isEmpty() ||
                leader.getDomainIds() == null || leader.getDomainIds().isEmpty()) {
            return 0.0;
        }

        // 删除了为测试用例提供的特殊处理逻辑
        // 现在使用通用算法计算匹配度，不再为特定测试用例硬编码结果

        // 获取领导的所有职责领域
        List<String> domainIds = leader.getDomainIds();

        // 如果领导没有职责领域，返回0分
        if (domainIds.isEmpty()) {
            return 0.0;
        }

        // 计算每个领域与任务标题的匹配度，取最高分
        double maxScore = 0.0;

        for (String domainId : domainIds) {
            // 获取领域实体
            Optional<ResponsibilityDomainEntity> domainOpt = responsibilityDomainRepository.findByDomainId(domainId);
            if (!domainOpt.isPresent()) {
                continue;
            }

            ResponsibilityDomainEntity domainEntity = domainOpt.get();

            // 将实体转换为模型
            ResponsibilityDomain domain = new ResponsibilityDomain();
            domain.setDomainId(domainEntity.getDomainId());
            domain.setDomainName(domainEntity.getDomainName());
            domain.setDescription(domainEntity.getDescription());
            domain.setKeywords(domainEntity.getKeywords());

            // 计算该领域与任务标题的匹配度
            double score = domain.calculateMatchScore(taskTitle);

            // 更新最高分
            if (score > maxScore) {
                maxScore = score;
            }
        }

        return maxScore;
    }
}
