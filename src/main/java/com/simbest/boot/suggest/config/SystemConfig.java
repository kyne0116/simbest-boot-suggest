package com.simbest.boot.suggest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

/**
 * 系统配置类
 * 用于读取application.properties中的自定义配置
 */
@Configuration
@Getter
public class SystemConfig {

    /**
     * 系统名称
     */
    @Value("${system.name:领导推荐系统}")
    private String systemName;

    /**
     * 系统版本
     */
    @Value("${system.version:1.0.0}")
    private String systemVersion;

    /**
     * 领域ID前缀
     */
    @Value("${domain.id.prefix:domain_}")
    private String domainIdPrefix;
}
