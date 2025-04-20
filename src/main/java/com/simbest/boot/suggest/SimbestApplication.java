package com.simbest.boot.suggest;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

import com.simbest.boot.suggest.service.DomainService;
import com.simbest.boot.suggest.service.LeaderService;
import com.simbest.boot.suggest.service.OrganizationService;
import com.simbest.boot.suggest.service.RecommendationService;
import com.simbest.boot.suggest.util.ChineseTokenizer;
import com.simbest.boot.suggest.util.DataLoader;
import com.simbest.boot.suggest.util.SynonymManager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.net.InetAddress;

/**
 * Simbest Boot Application Main Class
 */
@SpringBootApplication
@Slf4j
public class SimbestApplication implements ApplicationListener<WebServerInitializedEvent>  {

    @Autowired
    private ApplicationContext appContext;

    public static void main(String[] args) {
        SpringApplication.run(SimbestApplication.class, args);
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        String[] activeProfiles = appContext.getEnvironment().getActiveProfiles();
        for (String profile : activeProfiles) {
            log.warn("加载环境信息为: 【{}】", profile);
            log.warn("Application started successfully, lets go and have fun......");
        }

        WebServer server = event.getWebServer();
        WebServerApplicationContext context = event.getApplicationContext();
        Environment env = context.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        int port = server.getPort();
        String contextPath = env.getProperty("server.servlet.context-path");
        contextPath = StringUtils.isEmpty(contextPath) ? "":contextPath;
        log.warn("\n---------------------------------------------------------\n" +
                "\t应用已成功启动，运行地址如下：:\n" +
                "\tLocal:\t\thttp://localhost:{}{}" +
                "\n\tExternal:\thttp://{}:{}{}" +
                "\nAplication started successfully, lets go and have fun......" +
                "\n---------------------------------------------------------\n", port, contextPath, ip, port, contextPath);
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
