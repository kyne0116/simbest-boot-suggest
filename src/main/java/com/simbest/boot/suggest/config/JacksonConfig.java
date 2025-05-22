package com.simbest.boot.suggest.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.nio.charset.StandardCharsets;
import java.util.TimeZone;

/**
 * Jackson配置类
 * 配置全局的Jackson ObjectMapper，确保正确处理中文字符
 */
@Configuration
public class JacksonConfig {

    /**
     * 创建并配置ObjectMapper
     * 
     * @param builder Jackson2ObjectMapperBuilder
     * @return 配置好的ObjectMapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();
        
        // 配置序列化特性
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        
        // 配置反序列化特性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // 设置时区
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        
        // 确保使用UTF-8编码
        objectMapper.getFactory().setCharacterEscapes(null);
        
        return objectMapper;
    }
}
