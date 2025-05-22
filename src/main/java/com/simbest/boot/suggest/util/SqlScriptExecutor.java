package com.simbest.boot.suggest.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.simbest.boot.suggest.config.AppConfig;
import com.simbest.boot.suggest.config.DefaultValueConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * SQL脚本执行器
 * 用于在应用启动时执行SQL脚本
 */
@Component
@Slf4j
public class SqlScriptExecutor {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AppConfig appConfig;

    /**
     * 执行SQL脚本
     *
     * @param scriptPath 脚本路径
     */
    public void executeScript(String scriptPath) {
        log.info("执行SQL脚本: {}", scriptPath);
        Resource resource = new ClassPathResource(scriptPath);

        if (!resource.exists()) {
            log.warn("SQL脚本不存在: {}", scriptPath);
            return;
        }

        InputStream is = null;
        BufferedReader reader = null;
        Connection connection = null;
        Statement stmt = null;

        try {
            is = resource.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            StringBuilder sb = new StringBuilder();
            String line;
            List<String> statements = new ArrayList<>();
            Map<String, String> variables = new HashMap<>();
            boolean hasError = false;

            while ((line = reader.readLine()) != null) {
                // 忽略注释和空行
                if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                    continue;
                }

                // 处理SOURCE命令
                if (line.trim().toUpperCase().startsWith("SOURCE ")) {
                    // 将这个语句添加到语句列表中
                    if (sb.length() > 0) {
                        statements.add(sb.toString());
                        sb.setLength(0);
                    }

                    // 提取脚本路径
                    String sourcePath = line.trim().substring("SOURCE ".length()).trim();
                    if (sourcePath.endsWith(";")) {
                        sourcePath = sourcePath.substring(0, sourcePath.length() - 1);
                    }

                    log.info("发现SOURCE命令，执行脚本: {}", sourcePath);
                    executeScript(sourcePath);

                    continue;
                }

                // 处理MySQL变量设置
                if (line.trim().toUpperCase().startsWith("SET @")) {
                    // 将这个语句添加到语句列表中
                    if (sb.length() > 0) {
                        statements.add(sb.toString());
                        sb.setLength(0);
                    }
                    statements.add(line);

                    // 提取变量名和值以便后续替换
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        String varName = parts[0].trim();
                        String varValue = parts[1].trim();
                        // 处理变量值中的分号
                        if (varValue.endsWith(";")) {
                            varValue = varValue.substring(0, varValue.length() - 1);
                        }
                        // 如果变量值是一个查询，则不将其存储在变量映射中
                        if (!varValue.trim().toUpperCase().startsWith("(SELECT")) {
                            variables.put(varName, varValue);
                            log.debug("设置变量: {} = {}", varName, varValue);
                        }
                    }
                    continue;
                }

                // 替换变量
                String processedLine = line;
                for (Map.Entry<String, String> entry : variables.entrySet()) {
                    processedLine = processedLine.replace(entry.getKey(), entry.getValue());
                }

                // 添加空格确保语法正确
                if (sb.length() > 0 && !sb.toString().endsWith(" ") && !processedLine.startsWith(" ")) {
                    sb.append(" ");
                }
                sb.append(processedLine);

                // 如果行以分号结尾，则认为是一个完整的SQL语句
                if (processedLine.trim().endsWith(";")) {
                    statements.add(sb.toString());
                    sb.setLength(0);
                }
            }

            stmt = connection.createStatement();
            int statementCount = 0;
            try {
                for (String sql : statements) {
                    // 只在debug级别输出详细SQL语句
                    if (log.isDebugEnabled()) {
                        log.debug("执行SQL: {}", sql);
                    } else {
                        log.info("执行SQL: {}", sql.length() > 100 ? sql.substring(0, 100) + "..." : sql);
                    }

                    // H2数据库不支持INSERT IGNORE语法，所以不进行修改
                    // 如果需要处理重复键，应该在SQL脚本中使用MERGE或ON DUPLICATE KEY UPDATE语法

                    stmt.execute(sql);
                    statementCount++;
                }
                connection.commit();
                log.info("SQL脚本执行成功: {}, 执行了 {} 条SQL语句", scriptPath, statementCount);
            } catch (SQLException e) {
                hasError = true;
                connection.rollback();
                log.error("执行SQL脚本时发生SQL异常: {}", e.getMessage());
                throw new RuntimeException("执行SQL脚本时发生SQL异常: " + e.getMessage(), e);
            }

            // 如果是init-all.sql脚本，输出英文提示
            if (scriptPath.equals(DefaultValueConstants.SqlScriptPaths.INIT_ALL)) {
                // 获取应用端口和上下文路径
                String port = System.getProperty("server.port");
                if (port == null) {
                    port = appConfig.getDefaultServerPort(); // 使用配置的默认端口
                }
                String contextPath = System.getProperty("server.servlet.context-path");
                if (contextPath == null) {
                    contextPath = appConfig.getDefaultContextPath(); // 使用配置的默认上下文路径
                }

                log.warn("Data initialization completed successfully, lets go and have fun......");
                log.warn("Application is accessible at: http://localhost:{}{}", port, contextPath);
            }
        } catch (IOException e) {
            log.error("执行SQL脚本时发生IO异常: {}", e.getMessage());
            throw new RuntimeException("执行SQL脚本时发生IO异常: " + scriptPath, e);
        } catch (SQLException e) {
            log.error("执行SQL脚本时发生SQL异常: {}", e.getMessage());
            throw new RuntimeException("执行SQL脚本时发生SQL异常: " + scriptPath, e);
        } finally {
            // 关闭资源
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    log.error("关闭Statement时发生异常: {}", e.getMessage());
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("关闭Connection时发生异常: {}", e.getMessage());
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("关闭BufferedReader时发生异常: {}", e.getMessage());
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("关闭InputStream时发生异常: {}", e.getMessage());
                }
            }
        }
    }
}
