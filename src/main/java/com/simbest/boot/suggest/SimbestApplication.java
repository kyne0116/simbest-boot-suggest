package com.simbest.boot.suggest;

import java.net.InetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.util.StringUtils;

import com.simbest.boot.suggest.repository.impl.TenantAwareRepositoryFactoryBean;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Simbest Boot Application Main Class
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.simbest.boot.suggest.repository", repositoryFactoryBeanClass = TenantAwareRepositoryFactoryBean.class)
@Slf4j
public class SimbestApplication implements ApplicationListener<WebServerInitializedEvent> {

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
        contextPath = StringUtils.isEmpty(contextPath) ? "" : contextPath;
        log.warn("\n---------------------------------------------------------\n" +
                "\t应用已成功启动，运行地址如下：:\n" +
                "\tLocal:\t\thttp://localhost:{}{}" +
                "\n\tExternal:\thttp://{}:{}{}" +
                "\nApplication started successfully, lets go and have fun......" +
                "\n---------------------------------------------------------\n", port, contextPath, ip, port,
                contextPath);
    }
}
