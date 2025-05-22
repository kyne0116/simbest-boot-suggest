package com.simbest.boot.suggest.test.util;

import java.util.List;
import java.util.Map;

/**
 * 测试用例模型类
 * 用于存储从测试用例文档中解析出的测试用例信息
 */
public class TestCase {
    private String id;
    private String originalId; // 原始ID，如5.4.1.1.1
    private String name;
    private String purpose;
    private List<String> steps;
    private String httpMethod;
    private String endpoint;
    private String requestBody;
    private Map<String, Object> expectedResults;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalId() {
        return originalId;
    }

    public void setOriginalId(String originalId) {
        this.originalId = originalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public Map<String, Object> getExpectedResults() {
        return expectedResults;
    }

    public void setExpectedResults(Map<String, Object> expectedResults) {
        this.expectedResults = expectedResults;
    }

    @Override
    public String toString() {
        return "TestCase{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", endpoint='" + endpoint + '\'' +
                '}';
    }
}
