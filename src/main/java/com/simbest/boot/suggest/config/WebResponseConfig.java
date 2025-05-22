package com.simbest.boot.suggest.config;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Web响应配置类
 * 配置HTTP消息转换器，确保正确处理中文字符
 */
@Configuration
public class WebResponseConfig implements WebMvcConfigurer {

    /**
     * 配置消息转换器，确保使用UTF-8编码
     * @param converters 消息转换器列表
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 添加String消息转换器，使用UTF-8编码
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringConverter.setWriteAcceptCharset(false);  // 避免在Content-Type中添加charset参数
        converters.add(0, stringConverter);  // 添加到最前面，优先使用
        
        // 添加JSON消息转换器，使用UTF-8编码
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        jsonConverter.setObjectMapper(objectMapper);
        jsonConverter.setDefaultCharset(StandardCharsets.UTF_8);
        converters.add(1, jsonConverter);  // 添加到第二位
    }
    
    /**
     * 创建StringHttpMessageConverter Bean，确保使用UTF-8编码
     */
    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }
    
    /**
     * 创建MappingJackson2HttpMessageConverter Bean，确保使用UTF-8编码
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        return converter;
    }
}
