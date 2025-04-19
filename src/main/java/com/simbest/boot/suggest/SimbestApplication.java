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

/**
 * Simbest Boot Application Main Class
 */
@SpringBootApplication
public class SimbestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimbestApplication.class, args);
    }

    /**
     * 创建组织服务Bean
     */
    @Bean
    public OrganizationService organizationService() {
        return new OrganizationService();
    }

    /**
     * 创建领导服务Bean
     */
    @Bean
    public LeaderService leaderService() {
        return new LeaderService();
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
            System.out.println("======== 初始化推荐系统数据 ========");

            // 预加载配置文件
            System.out.println("正在加载配置文件...");
            System.out.println("1. 领域到领导映射: " + DataLoader.loadDomainLeaderMapping().size() + " 条记录");
            System.out.println("2. 常用词列表: " + DataLoader.loadCommonWords().size() + " 个词语");
            System.out.println("3. 同义词组: " + DataLoader.loadCommonSynonyms().size() + " 个组");
            System.out.println("4. 阈值配置: " + DataLoader.loadThresholdConfig().size() + " 个参数");

            // 初始化中文分词词典
            System.out.println("正在初始化中文分词词典...");
            ChineseTokenizer.initialize();

            // 初始化同义词表
            System.out.println("正在初始化同义词表...");
            SynonymManager.initialize();

            System.out.println("======== 初始化完成 ========");
        };
    }
}
