package com.simbest.boot.suggest.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simbest.boot.suggest.entity.LeaderInfoEntity;
import com.simbest.boot.suggest.repository.LeaderInfoRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 领导信息服务类
 * 提供领导信息相关的业务逻辑处理
 */
@Service
@Slf4j
public class LeaderInfoService {

    @Autowired
    private LeaderInfoRepository leaderInfoRepository;

    /**
     * 获取所有领导信息
     *
     * @return 领导信息列表
     */
    public List<LeaderInfoEntity> getAllLeaderInfos() {
        return leaderInfoRepository.findAll();
    }

    /**
     * 根据ID获取领导信息
     *
     * @param id 领导信息ID
     * @return 领导信息（可选）
     */
    public Optional<LeaderInfoEntity> getLeaderInfoById(Long id) {
        return leaderInfoRepository.findById(id);
    }

    /**
     * 根据租户代码获取领导信息列表
     *
     * @param tenantCode 租户代码
     * @return 领导信息列表
     */
    public List<LeaderInfoEntity> getLeaderInfosByTenantCode(String tenantCode) {
        return leaderInfoRepository.findByTenantCode(tenantCode);
    }

    /**
     * 根据账号获取领导信息
     *
     * @param account 领导账号
     * @return 领导信息（可选）
     */
    public Optional<LeaderInfoEntity> getLeaderInfoByAccount(String account) {
        return leaderInfoRepository.findByAccount(account);
    }

    /**
     * 根据账号和租户代码获取领导信息
     *
     * @param account    领导账号
     * @param tenantCode 租户代码
     * @return 领导信息（可选）
     */
    public Optional<LeaderInfoEntity> getLeaderInfoByAccountAndTenantCode(String account, String tenantCode) {
        return leaderInfoRepository.findByAccountAndTenantCode(account, tenantCode);
    }

    /**
     * 保存领导信息
     *
     * @param leaderInfo 领导信息实体
     * @return 保存后的领导信息实体
     */
    @Transactional
    public LeaderInfoEntity saveLeaderInfo(LeaderInfoEntity leaderInfo) {
        if (leaderInfo.getCreateTime() == null) {
            leaderInfo.setCreateTime(new Date());
        }
        return leaderInfoRepository.save(leaderInfo);
    }

    /**
     * 更新领导信息
     *
     * @param leaderInfo 领导信息实体
     * @return 更新后的领导信息实体
     */
    @Transactional
    public LeaderInfoEntity updateLeaderInfo(LeaderInfoEntity leaderInfo) {
        leaderInfo.setUpdateTime(new Date());
        return leaderInfoRepository.save(leaderInfo);
    }

    /**
     * 删除领导信息
     *
     * @param id 领导信息ID
     */
    @Transactional
    public void deleteLeaderInfo(Long id) {
        leaderInfoRepository.deleteById(id);
    }

    /**
     * 初始化领导信息
     * 注意：系统不再硬编码默认领导信息，而是依赖数据库初始化脚本
     */
    @Transactional
    public void initLeaderInfo() {
        // 检查是否有领导信息数据
        if (leaderInfoRepository.count() == 0) {
            log.info("未检测到领导信息数据，请确保数据库初始化脚本已正确执行");
        } else {
            log.info("领导信息数据已存在，共 {} 条记录", leaderInfoRepository.count());
        }
    }
}
