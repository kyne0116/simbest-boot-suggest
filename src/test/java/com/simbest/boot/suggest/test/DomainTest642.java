package com.simbest.boot.suggest.test;

import static io.restassured.RestAssured.given;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * 职责领域测试套件
 * 覆盖6.4.2章节的所有测试用例
 */
public class DomainTest642 {

    @BeforeClass
    @Parameters({ "baseUrl", "port" })
    public void setup(String baseUrl, int port) {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = port;
        RestAssured.basePath = "/suggest";

        System.out.println("测试环境配置:");
        System.out.println("- 基础URL: " + baseUrl);
        System.out.println("- 端口: " + port);
        System.out.println("- 基础路径: " + RestAssured.basePath);
    }

    /**
     * 6.4.2.1 领域关键词匹配测试
     * 验证任务标题与职责领域关键词匹配功能
     */
    @Test
    public void test_6_4_2_1_DomainKeywordMatching() {
        System.out.println("执行测试用例6.4.2.1: 领域关键词匹配测试");

        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于网络安全管理规定的通知\",\n" +
                "  \"recommendationType\": \"MULTIPLE\",\n" +
                "  \"workflowDirection\": \"UPWARD\",\n" +
                "  \"userAccount\": null,\n" +
                "  \"orgId\": \"team_security\",\n" +
                "  \"tenantCode\": \"default\",\n" +
                "  \"candidateAccounts\": null\n" +
                "}";

        try {
            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when()
                    .post("/recommend/getRecommendation")
                    .then()
                    .extract().response();

            System.out.println("响应状态码: " + response.statusCode());
            System.out.println("响应时间: " + response.time() + "ms");
            System.out.println("响应内容: " + response.asString());

            // 验证响应状态码为200
            Assert.assertEquals(response.getStatusCode(), 200, "响应状态码应为200");

            // 验证响应中包含errcode字段
            Assert.assertEquals(response.jsonPath().getInt("errcode"), 0, "响应中errcode字段应为0");

            // 验证返回结果包含推荐
            Assert.assertNotNull(response.jsonPath().get("data"), "响应中data字段不应为null");

            List<?> leaders = response.jsonPath().getList("data.leaders");
            Assert.assertNotNull(leaders, "响应中data.leaders字段不应为null");
            Assert.assertFalse(leaders.isEmpty(), "推荐结果不应为空");

            // 验证推荐结果中包含与网络安全相关的领导
            boolean containsSecurityLeader = false;
            for (Object item : leaders) {
                Map<String, Object> recommendation = (Map<String, Object>) item;
                String reason = (String) recommendation.get("reason");
                if (reason != null && reason.contains("网络安全")) {
                    containsSecurityLeader = true;
                    break;
                }
            }
            Assert.assertTrue(containsSecurityLeader, "推荐结果应包含与网络安全相关的领导");
        } catch (Exception e) {
            System.out.println("测试执行异常: " + e.getMessage());
            Assert.fail("测试执行失败: " + e.getMessage());
        }
    }

    /**
     * 6.4.2.2 同义词扩展匹配测试
     * 验证通过同义词扩展提高匹配准确性功能
     */
    @Test
    public void test_6_4_2_2_SynonymExpansionMatching() {
        System.out.println("执行测试用例6.4.2.2: 同义词扩展匹配测试");

        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于信息系统防护措施的通知\",\n" +
                "  \"recommendationType\": \"MULTIPLE\",\n" +
                "  \"workflowDirection\": \"UPWARD\",\n" +
                "  \"userAccount\": null,\n" +
                "  \"orgId\": \"team_security\",\n" +
                "  \"tenantCode\": \"default\",\n" +
                "  \"candidateAccounts\": null\n" +
                "}";

        try {
            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when()
                    .post("/recommend/getRecommendation")
                    .then()
                    .extract().response();

            System.out.println("响应状态码: " + response.statusCode());
            System.out.println("响应时间: " + response.time() + "ms");
            System.out.println("响应内容: " + response.asString());

            // 验证响应状态码为200
            Assert.assertEquals(response.getStatusCode(), 200, "响应状态码应为200");

            // 验证响应中包含errcode字段
            Assert.assertEquals(response.jsonPath().getInt("errcode"), 0, "响应中errcode字段应为0");

            // 验证返回结果包含推荐
            Assert.assertNotNull(response.jsonPath().get("data"), "响应中data字段不应为null");

            List<?> leaders = response.jsonPath().getList("data.leaders");
            Assert.assertNotNull(leaders, "响应中data.leaders字段不应为null");
            Assert.assertFalse(leaders.isEmpty(), "推荐结果不应为空");

            // 验证推荐结果中包含与网络安全相关的领导（通过同义词匹配）
            boolean containsSecurityLeader = false;
            for (Object item : leaders) {
                Map<String, Object> recommendation = (Map<String, Object>) item;
                String reason = (String) recommendation.get("reason");
                if (reason != null && (reason.contains("安全") || reason.contains("防护"))) {
                    containsSecurityLeader = true;
                    break;
                }
            }
            Assert.assertTrue(containsSecurityLeader, "推荐结果应包含与安全防护相关的领导");
        } catch (Exception e) {
            System.out.println("测试执行异常: " + e.getMessage());
            Assert.fail("测试执行失败: " + e.getMessage());
        }
    }
}
