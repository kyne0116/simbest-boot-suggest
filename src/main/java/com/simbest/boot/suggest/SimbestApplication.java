package com.simbest.boot.suggest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.simbest.boot.suggest.service.DomainService;
import com.simbest.boot.suggest.service.LeaderService;
import com.simbest.boot.suggest.service.OrganizationService;
import com.simbest.boot.suggest.service.RecommendationService;
import com.simbest.boot.suggest.util.ChineseTokenizer;
import com.simbest.boot.suggest.util.DataLoader;
import com.simbest.boot.suggest.util.SynonymManager;

import lombok.extern.slf4j.Slf4j;

/**
 * Simbest Boot Application Main Class
 */
@SpringBootApplication
@Slf4j
public class SimbestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimbestApplication.class, args);
    }

    /**
     * 创建组织服务Bean
     */
    @Bean
    public OrganizationService organizationService(LeaderService leaderService) {
        OrganizationService organizationService = new OrganizationService();
        organizationService.setLeaderService(leaderService);
        return organizationService;
    }

    /**
     * 创建领导服务Bean
     */
    @Bean
    public LeaderService leaderService(DomainService domainService) {
        LeaderService leaderService = new LeaderService();
        leaderService.setDomainService(domainService);
        return leaderService;
    }

    /**
     * 创建职责领域服务Bean
     */
    @Bean
    public DomainService domainService() {
        return new DomainService();
    }

    /**
     * 创建推荐服务Bean
     */
    @Bean
    public RecommendationService recommendationService(
            OrganizationService organizationService,
            LeaderService leaderService,
            DomainService domainService) {
        return new RecommendationService(organizationService, leaderService, domainService);
    }

    /**
     * 应用启动时执行初始化
     */
    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            log.info("======== 初始化推荐系统数据 ========");

            // 预加载配置文件
            log.info("正在加载配置文件...");
            log.info("1. 领域到领导映射: {} 条记录", DataLoader.loadDomainLeaderMapping().size());
            log.info("2. 常用词列表: {} 个词语", DataLoader.loadCommonWords().size());
            log.info("3. 同义词组: {} 个组", DataLoader.loadCommonSynonyms().size());
            log.info("4. 阈值配置: {} 个参数", DataLoader.loadThresholdConfig().size());
            log.info("5. 算法权重配置: {} 个部分", DataLoader.loadAlgorithmWeights().size());

            // 初始化中文分词词典
            log.info("正在初始化中文分词词典...");
            ChineseTokenizer.initialize();

            // 初始化同义词表
            log.info("正在初始化同义词表...");
            SynonymManager.initialize();

            log.info("======== 初始化完成 ========");
        };
    }
}
