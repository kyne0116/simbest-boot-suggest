package com.simbest.boot.suggest.config;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据库初始化器
 * 用于在应用程序启动时执行初始化 SQL 脚本
 */
@Component
@Slf4j
public class DatabaseInitializer {

    @Autowired
    private DataSource dataSource;

    /**
     * 初始化数据库
     * 注意：表结构由JPA自动创建，这里只执行必要的初始化数据脚本
     */
    @PostConstruct
    public void initialize() {
        log.info("开始执行数据库初始化脚本...");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();

        // 初始化租户数据
        populator.addScript(new ClassPathResource(DefaultValueConstants.SqlScriptPaths.INIT_TENANT));

        // 执行脚本
        populator.execute(dataSource);
        log.info("数据库初始化脚本执行完成");
    }
}
