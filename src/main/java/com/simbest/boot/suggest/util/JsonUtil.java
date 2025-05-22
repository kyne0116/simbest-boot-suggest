package com.simbest.boot.suggest.util;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import lombok.extern.slf4j.Slf4j;

/**
 * JSON工具类
 * 提供JSON格式化和转换功能
 */
@Slf4j
public class JsonUtil {
    private static final ObjectMapper objectMapper;

    static {
        // 初始化并配置ObjectMapper
        objectMapper = new ObjectMapper();

        // 配置ObjectMapper，使JSON输出更美观
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // 配置ObjectMapper以正确处理中文字符
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // 忽略未知属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 允许空对象
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // 添加自定义的字符串序列化器，确保中文字符正确编码
        SimpleModule module = new SimpleModule();
        module.addSerializer(String.class, new JsonSerializer<String>() {
            @Override
            public void serialize(String value, JsonGenerator gen, SerializerProvider serializers)
                    throws java.io.IOException {
                if (value != null) {
                    // 确保字符串使用UTF-8编码
                    byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
                    String utf8String = new String(bytes, StandardCharsets.UTF_8);
                    gen.writeString(utf8String);
                } else {
                    gen.writeNull();
                }
            }
        });
        objectMapper.registerModule(module);
    }

    /**
     * 将对象转换为格式化的JSON字符串
     *
     * @param obj 要转换的对象
     * @return 格式化的JSON字符串，如果转换失败则返回对象的toString()结果
     */
    public static String toJsonPretty(Object obj) {
        if (obj == null) {
            return "null";
        }
        try {
            // 使用UTF-8编码确保中文字符正确显示
            byte[] jsonBytes = objectMapper.writeValueAsBytes(obj);
            return new String(jsonBytes, StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            log.error("转换对象到JSON失败: {}", e.getMessage(), e);
            return obj.toString();
        } catch (Exception e) {
            log.error("转换对象到JSON失败: {}", e.getMessage(), e);
            return obj.toString();
        }
    }

    /**
     * 将对象转换为单行JSON字符串
     *
     * @param obj 要转换的对象
     * @return 单行JSON字符串，如果转换失败则返回对象的toString()结果
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        try {
            ObjectMapper compactMapper = new ObjectMapper();

            // 配置ObjectMapper以正确处理中文字符
            compactMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            // 忽略未知属性
            compactMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // 允许空对象
            compactMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

            // 添加自定义的字符串序列化器，确保中文字符正确编码
            SimpleModule module = new SimpleModule();
            module.addSerializer(String.class, new JsonSerializer<String>() {
                @Override
                public void serialize(String value, JsonGenerator gen, SerializerProvider serializers)
                        throws java.io.IOException {
                    if (value != null) {
                        // 确保字符串使用UTF-8编码
                        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
                        String utf8String = new String(bytes, StandardCharsets.UTF_8);
                        gen.writeString(utf8String);
                    } else {
                        gen.writeNull();
                    }
                }
            });
            compactMapper.registerModule(module);

            // 使用UTF-8编码确保中文字符正确显示
            byte[] jsonBytes = compactMapper.writeValueAsBytes(obj);
            return new String(jsonBytes, StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            log.error("转换对象到JSON失败: {}", e.getMessage(), e);
            return obj.toString();
        } catch (Exception e) {
            log.error("转换对象到JSON失败: {}", e.getMessage(), e);
            return obj.toString();
        }
    }

    /**
     * 确保字符串使用UTF-8编码，解决中文乱码问题
     *
     * @param text 原始文本
     * @return 确保UTF-8编码的文本
     */
    public static String ensureUtf8Encoding(String text) {
        if (text == null) {
            return null;
        }
        try {
            // 将字符串转换为UTF-8字节数组，然后再转回字符串，确保编码正确
            return new String(text.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("转换字符串编码失败: {}", e.getMessage(), e);
            return text;
        }
    }

    /**
     * 创建包含请求参数的日志友好的Map
     *
     * @param userAccount       用户账号
     * @param orgId             组织ID
     * @param taskTitle         任务标题
     * @param workflowDirection 工作流方向
     * @param candidateAccounts 候选账号列表
     * @return 包含请求参数的Map
     */
    public static Map<String, Object> createRequestMap(String userAccount, String orgId, String taskTitle,
            String workflowDirection, String[] candidateAccounts) {
        Map<String, Object> requestMap = new java.util.HashMap<>();
        requestMap.put("userAccount", userAccount);
        requestMap.put("orgId", orgId != null ? orgId : "未提供");
        requestMap.put("taskTitle", taskTitle);
        requestMap.put("workflowDirection", workflowDirection);
        requestMap.put("candidateAccounts", candidateAccounts != null ? candidateAccounts : "未提供");
        return requestMap;
    }
}
