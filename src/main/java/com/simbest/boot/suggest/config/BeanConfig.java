package com.simbest.boot.suggest.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.simbest.boot.suggest.service.LeaderInfoService;
import com.simbest.boot.suggest.util.ChineseTokenizer;
import com.simbest.boot.suggest.util.DatabaseDataLoader;
import com.simbest.boot.suggest.util.DatabaseSynonymManager;
import com.simbest.boot.suggest.util.SqlScriptExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * Bean配置类
 * 用于集中管理应用中的Bean定义
 */
@Configuration
@Slf4j
public class BeanConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AppConfig appConfig;

    // RecommendationService已经使用@Service注解，不需要在这里定义Bean

    /**
     * 应用启动时执行初始化
     */
    @Bean
    public CommandLineRunner initializeData(DatabaseDataLoader databaseDataLoader,
            ApplicationContext applicationContext,
            SqlScriptExecutor sqlScriptExecutor,
            LeaderInfoService leaderInfoService) {
        return args -> {
            log.info("======== 初始化推荐系统数据 ========");

            // 预加载配置文件
            log.info("正在加载配置文件...");
            log.info("4. 阈值配置: {} 个参数", databaseDataLoader.loadThresholdConfig().size());
            log.info("5. 算法权重配置: {} 个部分", databaseDataLoader.loadAlgorithmWeights().size());
            log.info("6. AI分析配置: {} 个部分", databaseDataLoader.loadAIAnalysisConfig().size());
            log.info("7. 推荐配置: {} 个部分", databaseDataLoader.loadRecommendationConfig().size());

            // 执行数据库初始化脚本
            log.info("开始执行数据库初始化脚本...");

            // 不需要执行表结构创建脚本，因为JPA会自动创建表结构
            log.info("JPA将自动创建表结构，无需执行init-schema.sql脚本");

            // 等待表结构创建完成
            Thread.sleep(2000);

            // 执行核心初始化脚本
            sqlScriptExecutor.executeScript("db/core/core-data.sql");
            log.info("成功执行核心数据初始化脚本");

            // 汇总数据库初始化情况
            summarizeDataInitialization();

            log.info("数据库初始化脚本执行完成");
            log.warn("Data initialization completed successfully, lets go and have fun......");

            // 获取所有租户代码
            List<String> tenantCodes = new ArrayList<>();
            tenantCodes.add(DefaultValueConstants.getDefaultTenantCode());

            // 为每个租户加载数据
            for (String tenantCode : tenantCodes) {
                log.info("======== 正在加载租户 {} 的数据 ========", tenantCode);
                log.info("1. 领域到领导映射: {} 条记录", databaseDataLoader.loadDomainLeaderMapping(tenantCode).size());
                log.info("2. 常用词列表: {} 个词语", databaseDataLoader.loadCommonWords(tenantCode).size());
                log.info("3. 同义词组: {} 个组", databaseDataLoader.loadCommonSynonyms(tenantCode).size());
            }

            // 初始化leader_info表
            log.info("正在初始化leader_info表...");
            leaderInfoService.initLeaderInfo();

            // 初始化中文分词词典
            log.info("正在初始化中文分词词典...");
            ChineseTokenizer.initialize();

            // 初始化同义词表
            log.info("正在初始化同义词表...");
            // 使用Spring管理的DatabaseSynonymManager Bean
            DatabaseSynonymManager databaseSynonymManager = applicationContext.getBean(DatabaseSynonymManager.class);
            databaseSynonymManager.initialize();

            log.info("======== 初始化完成 ========");
            log.warn("Data initialization completed successfully, lets go and have fun......");

            // 输出应用访问地址
            int port = Integer.parseInt(
                    applicationContext.getEnvironment().getProperty("server.port", appConfig.getDefaultServerPort()));
            String contextPath = applicationContext.getEnvironment().getProperty("server.servlet.context-path",
                    appConfig.getDefaultContextPath());
            log.warn("\n---------------------------------------------------------\n" +
                    "\t数据初始化完成，应用访问地址：\n" +
                    "\tLocal:\t\thttp://localhost:{}{}" +
                    "\n\tAPI文档:\thttp://localhost:{}{}/swagger-ui.html" +
                    "\n\tH2控制台:\thttp://localhost:{}/h2-console" +
                    "\n---------------------------------------------------------\n",
                    port, contextPath, port, contextPath, port);
        };
    }

    /**
     * 汇总数据库初始化情况
     * 统计各个表的数据量
     */
    private void summarizeDataInitialization() {
        log.info("=== 数据库初始化汇总 ====");

        Connection conn = null;
        Statement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();

            // 查询各个表的数据量
            Map<String, Integer> tableCounts = new HashMap<>();

            // 使用安全的查询方法
            tableCounts.put("leader", safeCountTable(stmt, "leader"));
            tableCounts.put("organization", safeCountTable(stmt, "organization"));
            tableCounts.put("responsibility_domain", safeCountTable(stmt, "responsibility_domain"));
            tableCounts.put("domain_leader_mapping", safeCountTable(stmt, "domain_leader_mapping"));
            tableCounts.put("common_word", safeCountTable(stmt, "common_word"));
            tableCounts.put("synonym_group", safeCountTable(stmt, "synonym_group"));
            tableCounts.put("task_pattern", safeCountTable(stmt, "task_pattern"));
            tableCounts.put("approval_history", safeCountTable(stmt, "approval_history"));

            // 尝试查询leader_info表，但不影响整体执行
            tableCounts.put("leader_info", safeCountTable(stmt, "leader_info"));

            // 按租户统计
            log.info("按租户统计数据量");
            String tenant = DefaultValueConstants.getDefaultTenantCode();
            int tenantLeaderCount = safeCountTableWithCondition(stmt, "leader", "tenant_code", tenant);
            int tenantOrgCount = safeCountTableWithCondition(stmt, "organization", "tenant_code", tenant);
            int tenantDomainCount = safeCountTableWithCondition(stmt, "responsibility_domain", "tenant_code", tenant);
            int tenantTaskPatternCount = safeCountTableWithCondition(stmt, "task_pattern", "tenant_code", tenant);
            int tenantApprovalHistoryCount = safeCountTableWithCondition(stmt, "approval_history", "tenant_code",
                    tenant);

            log.info("租户 {}: 领导 {} 条, 组织 {} 条, 职责领域 {} 条, 任务模式 {} 条, 批复历史 {} 条",
                    tenant, tenantLeaderCount, tenantOrgCount, tenantDomainCount, tenantTaskPatternCount,
                    tenantApprovalHistoryCount);

            // 总体数据统计
            log.info("总体数据统计: 领导 {} 条, 组织 {} 条, 职责领域 {} 条, 领域映射 {} 条",
                    tableCounts.get("leader"), tableCounts.get("organization"),
                    tableCounts.get("responsibility_domain"), tableCounts.get("domain_leader_mapping"));
            log.info("总体数据统计: 常用词 {} 条, 同义词组 {} 条, 任务模式 {} 条, 批复历史 {} 条",
                    tableCounts.get("common_word"), tableCounts.get("synonym_group"),
                    tableCounts.get("task_pattern"), tableCounts.get("approval_history"));
        } catch (SQLException e) {
            log.error("汇总数据库初始化情况时出错: {}", e.getMessage());
            throw new RuntimeException("数据库初始化汇总失败", e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    log.error("关闭Statement时出错: {}", e.getMessage());
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("关闭Connection时出错: {}", e.getMessage());
                }
            }
        }

        log.info("=== 数据库初始化汇总完成 ====");
    }

    /**
     * 统计表中的记录数
     *
     * @param stmt      SQL语句执行器
     * @param tableName 表名
     * @return 记录数
     */
    private int safeCountTable(Statement stmt, String tableName) {
        try {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("统计表 {} 记录数失败: {}", tableName, e.getMessage());
        }
        return 0;
    }

    /**
     * 统计表中满足条件的记录数
     *
     * @param stmt       SQL语句执行器
     * @param tableName  表名
     * @param columnName 条件列名
     * @param value      条件值
     * @return 记录数
     */
    private int safeCountTableWithCondition(Statement stmt, String tableName, String columnName, String value) {
        try {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName +
                    " WHERE " + columnName + " = '" + value + "'");
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("统计表 {} 中 {} = {} 的记录数失败: {}", tableName, columnName, value, e.getMessage());
        }
        return 0;
    }
}
