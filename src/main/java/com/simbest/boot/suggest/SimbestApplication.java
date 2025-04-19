package com.simbest.boot.suggest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.simbest.boot.suggest.service.DomainService;
import com.simbest.boot.suggest.service.LeaderService;
import com.simbest.boot.suggest.service.OrganizationService;
import com.simbest.boot.suggest.service.RecommendationService;

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
}
