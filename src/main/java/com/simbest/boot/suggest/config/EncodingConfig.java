package com.simbest.boot.suggest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 编码配置类
 * 专门处理中文编码问题
 */
@Configuration
public class EncodingConfig implements WebMvcConfigurer {

    /**
     * 创建StringHttpMessageConverter Bean，确保使用UTF-8编码
     */
    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converter.setWriteAcceptCharset(false); // 避免在Content-Type中添加charset参数
        return converter;
    }

    /**
     * 配置消息转换器，确保使用UTF-8编码
     * @param converters 消息转换器列表
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 添加String消息转换器，使用UTF-8编码
        converters.add(0, stringHttpMessageConverter());
    }
}
