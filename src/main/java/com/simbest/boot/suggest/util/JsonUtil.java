package com.simbest.boot.suggest.util;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

/**
 * JSON工具类
 * 提供JSON格式化和转换功能
 */
@Slf4j
public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 配置ObjectMapper，使JSON输出更美观
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * 将对象转换为格式化的JSON字符串
     *
     * @param obj 要转换的对象
     * @return 格式化的JSON字符串，如果转换失败则返回对象的toString()结果
     */
    public static String toJsonPretty(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
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
        try {
            ObjectMapper compactMapper = new ObjectMapper();
            return compactMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("转换对象到JSON失败: {}", e.getMessage(), e);
            return obj.toString();
        }
    }

    /**
     * 创建包含请求参数的日志友好的Map
     *
     * @param userAccount 用户账号
     * @param orgId 组织ID
     * @param taskTitle 任务标题
     * @param workflowDirection 工作流方向
     * @param useOrg 是否使用组织关系
     * @param candidateAccounts 候选账号列表
     * @return 包含请求参数的Map
     */
    public static Map<String, Object> createRequestMap(String userAccount, String orgId, String taskTitle,
            String workflowDirection, boolean useOrg, String[] candidateAccounts) {
        Map<String, Object> requestMap = new java.util.HashMap<>();
        requestMap.put("userAccount", userAccount);
        requestMap.put("orgId", orgId != null ? orgId : "未提供");
        requestMap.put("taskTitle", taskTitle);
        requestMap.put("workflowDirection", workflowDirection);
        requestMap.put("useOrg", useOrg);
        requestMap.put("candidateAccounts", candidateAccounts != null ? candidateAccounts : "未提供");
        return requestMap;
    }
}
