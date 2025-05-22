package com.simbest.boot.suggest.config;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * 自定义字符编码过滤器配置
 * 确保所有请求和响应都使用UTF-8编码
 */
@Configuration
public class CustomCharacterEncodingFilter {

    /**
     * 创建字符编码过滤器
     *
     * @return 字符编码过滤器注册Bean
     */
    @Bean
    public FilterRegistrationBean<CharacterEncodingFilter> utf8CharacterEncodingFilterRegistration() {
        FilterRegistrationBean<CharacterEncodingFilter> registrationBean = new FilterRegistrationBean<>();

        // 创建字符编码过滤器
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding(StandardCharsets.UTF_8.name());
        characterEncodingFilter.setForceEncoding(true); // 强制使用指定的编码

        registrationBean.setFilter(characterEncodingFilter);
        registrationBean.addUrlPatterns("/*"); // 应用到所有URL
        registrationBean.setOrder(0); // 设置为最高优先级

        return registrationBean;
    }
}
