package com.simbest.boot.suggest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.simbest.boot.suggest.repository.CommonWordRepository;
import com.simbest.boot.suggest.repository.DomainLeaderMappingRepository;
import com.simbest.boot.suggest.repository.LeaderRepository;
import com.simbest.boot.suggest.repository.OrganizationRepository;
import com.simbest.boot.suggest.repository.ResponsibilityDomainRepository;
import com.simbest.boot.suggest.repository.SynonymGroupRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据初始化日志记录组件
 * 用于在应用启动时记录数据初始化的日志
 */
@Component
@Slf4j
public class DataInitializationLogger implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ResponsibilityDomainRepository responsibilityDomainRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private LeaderRepository leaderRepository;

    @Autowired
    private DomainLeaderMappingRepository domainLeaderMappingRepository;

    @Autowired
    private CommonWordRepository commonWordRepository;

    @Autowired
    private SynonymGroupRepository synonymGroupRepository;

    // 删除特殊规则仓库引用

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 避免重复执行
        if (event.getApplicationContext().getParent() == null) {
            logInitializedData();
        }
    }

    /**
     * 记录初始化数据的日志
     */
    private void logInitializedData() {
        log.info("=== 数据初始化完成，当前数据统计 ===");
        log.info("职责领域数据: {} 条", responsibilityDomainRepository.count());
        log.info("组织数据: {} 条", organizationRepository.count());
        log.info("领导数据: {} 条", leaderRepository.count());
        log.info("领域领导映射数据: {} 条", domainLeaderMappingRepository.count());
        log.info("常用词数据: {} 条", commonWordRepository.count());
        log.info("同义词组数据: {} 条", synonymGroupRepository.count());
        // 删除特殊规则数据统计
        log.info("=== 数据初始化统计完成 ===");
    }
}
