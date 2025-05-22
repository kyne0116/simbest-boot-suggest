package com.simbest.boot.suggest.util;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * 组织数据初始化工具类
 * 用于执行组织人员相关的初始化SQL脚本
 */
@Component
@Slf4j
public class OrganizationDataInitializer {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SqlScriptExecutor sqlScriptExecutor;

    /**
     * 初始化组织人员数据
     * 执行组织人员相关的初始化SQL脚本
     */
    @Transactional
    public void initializeOrganizationData() {
        log.info("开始执行组织人员数据初始化脚本...");

        try {
            // 执行组织人员初始化脚本
            sqlScriptExecutor.executeScript("db/init/init-organization-from-doc.sql");
            log.info("组织人员数据初始化脚本执行完成");

            // 执行职责领域初始化脚本
            sqlScriptExecutor.executeScript("db/init/init-responsibility-domain.sql");
            log.info("职责领域数据初始化脚本执行完成（包含组织-领域关联数据）");

            log.info("组织人员数据初始化完成");
        } catch (Exception e) {
            log.error("组织人员数据初始化失败", e);
            throw new RuntimeException("组织人员数据初始化失败", e);
        }
    }

    /**
     * 使用ResourceDatabasePopulator执行SQL脚本
     *
     * @param scriptPath SQL脚本路径
     */
    public void executeScript(String scriptPath) {
        log.info("执行SQL脚本: {}", scriptPath);
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource(scriptPath));
        populator.execute(dataSource);
        log.info("SQL脚本执行完成: {}", scriptPath);
    }
}
