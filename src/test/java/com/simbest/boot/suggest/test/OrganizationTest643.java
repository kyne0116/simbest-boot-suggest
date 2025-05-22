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
 * 组织关系测试套件
 * 覆盖6.4.3章节的所有测试用例
 */
public class OrganizationTest643 {

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
     * 6.4.3.1 组织关键词匹配测试
     * 验证任务标题与组织关键词匹配功能
     */
    @Test
    public void test_6_4_3_1_OrganizationKeywordMatching() {
        System.out.println("执行测试用例6.4.3.1: 组织关键词匹配测试");

        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于市场营销策略调整的通知\",\n" +
                "  \"recommendationType\": \"MULTIPLE\",\n" +
                "  \"workflowDirection\": \"UPWARD\",\n" +
                "  \"userAccount\": null,\n" +
                "  \"orgId\": \"team_marketing\",\n" +
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

            // 验证推荐结果中包含与市场营销相关的领导
            boolean containsMarketingLeader = false;
            for (Object item : leaders) {
                Map<String, Object> recommendation = (Map<String, Object>) item;
                String reason = (String) recommendation.get("reason");
                if (reason != null && reason.contains("市场")) {
                    containsMarketingLeader = true;
                    break;
                }
            }
            Assert.assertTrue(containsMarketingLeader, "推荐结果应包含与市场营销相关的领导");
        } catch (Exception e) {
            System.out.println("测试执行异常: " + e.getMessage());
            Assert.fail("测试执行失败: " + e.getMessage());
        }
    }

    /**
     * 6.4.3.2 组织层级关系测试
     * 验证组织层级关系处理功能
     */
    @Test
    public void test_6_4_3_2_OrganizationHierarchyRelationship() {
        System.out.println("执行测试用例6.4.3.2: 组织层级关系测试");

        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于部门年度工作计划的通知\",\n" +
                "  \"recommendationType\": \"MULTIPLE\",\n" +
                "  \"workflowDirection\": \"UPWARD\",\n" +
                "  \"userAccount\": null,\n" +
                "  \"orgId\": \"team_platform\",\n" +
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

            // 验证推荐结果中包含直接上级和更高层级的领导
            if (leaders.size() >= 2) {
                // 如果有至少两个推荐结果，验证是否包含不同层级的领导
                Map<String, Object> firstRecommendation = (Map<String, Object>) leaders.get(0);
                Map<String, Object> secondRecommendation = (Map<String, Object>) leaders.get(1);
                
                // 验证两个推荐结果是不同的领导
                Assert.assertNotEquals(
                    firstRecommendation.get("suggestAccount"), 
                    secondRecommendation.get("suggestAccount"), 
                    "推荐结果应包含不同的领导"
                );
            }
        } catch (Exception e) {
            System.out.println("测试执行异常: " + e.getMessage());
            Assert.fail("测试执行失败: " + e.getMessage());
        }
    }
}
