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
 * 批复历史测试套件
 * 覆盖6.4.4章节的所有测试用例
 */
public class HistoryTest644 {

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
     * 6.4.4.1 批复历史记录测试
     * 验证批复历史记录功能
     */
    @Test
    public void test_6_4_4_1_ApprovalHistoryRecord() {
        System.out.println("执行测试用例6.4.4.1: 批复历史记录测试");

        // 第一步：创建一条批复记录
        String createHistoryBody = "{\n" +
                "  \"taskId\": \"test-task-001\",\n" +
                "  \"taskTitle\": \"关于市场营销策略调整的通知\",\n" +
                "  \"approverAccount\": \"zqf\",\n" +
                "  \"initiatorAccount\": \"lixuanang\",\n" +
                "  \"initiatorOrgId\": \"team_marketing\",\n" +
                "  \"tenantCode\": \"default\"\n" +
                "}";

        try {
            // 创建批复历史记录
            Response createResponse = given()
                    .contentType(ContentType.JSON)
                    .body(createHistoryBody)
                    .when()
                    .post("/history/saveApprovalHistory")
                    .then()
                    .extract().response();

            System.out.println("创建批复历史响应状态码: " + createResponse.statusCode());
            System.out.println("创建批复历史响应内容: " + createResponse.asString());

            // 验证创建响应状态码为200
            Assert.assertEquals(createResponse.getStatusCode(), 200, "创建批复历史响应状态码应为200");
            Assert.assertEquals(createResponse.jsonPath().getInt("errcode"), 0, "创建批复历史响应中errcode字段应为0");

            // 第二步：查询批复历史记录
            Response queryResponse = given()
                    .contentType(ContentType.JSON)
                    .queryParam("initiatorAccount", "lixuanang")
                    .queryParam("tenantCode", "default")
                    .when()
                    .get("/history/getApprovalHistory")
                    .then()
                    .extract().response();

            System.out.println("查询批复历史响应状态码: " + queryResponse.statusCode());
            System.out.println("查询批复历史响应内容: " + queryResponse.asString());

            // 验证查询响应状态码为200
            Assert.assertEquals(queryResponse.getStatusCode(), 200, "查询批复历史响应状态码应为200");
            Assert.assertEquals(queryResponse.jsonPath().getInt("errcode"), 0, "查询批复历史响应中errcode字段应为0");

            // 验证返回结果包含历史记录
            Assert.assertNotNull(queryResponse.jsonPath().get("data"), "响应中data字段不应为null");

            List<?> historyRecords = queryResponse.jsonPath().getList("data");
            Assert.assertNotNull(historyRecords, "响应中data字段不应为null");
            Assert.assertFalse(historyRecords.isEmpty(), "批复历史记录不应为空");

            // 验证历史记录中包含刚才创建的记录
            boolean containsNewRecord = false;
            for (Object item : historyRecords) {
                Map<String, Object> record = (Map<String, Object>) item;
                if ("test-task-001".equals(record.get("taskId"))) {
                    containsNewRecord = true;
                    break;
                }
            }
            Assert.assertTrue(containsNewRecord, "批复历史记录应包含刚才创建的记录");
        } catch (Exception e) {
            System.out.println("测试执行异常: " + e.getMessage());
            Assert.fail("测试执行失败: " + e.getMessage());
        }
    }

    /**
     * 6.4.4.2 基于历史的推荐测试
     * 验证基于历史批复记录进行推荐的功能
     */
    @Test
    public void test_6_4_4_2_HistoryBasedRecommendation() {
        System.out.println("执行测试用例6.4.4.2: 基于历史的推荐测试");

        // 首先确保有历史记录
        test_6_4_4_1_ApprovalHistoryRecord();

        // 然后基于历史进行推荐
        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于市场营销策略调整的通知\",\n" +
                "  \"recommendationType\": \"MULTIPLE\",\n" +
                "  \"workflowDirection\": \"UPWARD\",\n" +
                "  \"userAccount\": \"lixuanang\",\n" +
                "  \"orgId\": null,\n" +
                "  \"tenantCode\": \"default\",\n" +
                "  \"useHistoryData\": true,\n" +
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

            // 验证推荐结果中包含历史批复的领导
            boolean containsHistoryLeader = false;
            for (Object item : leaders) {
                Map<String, Object> recommendation = (Map<String, Object>) item;
                if ("zqf".equals(recommendation.get("suggestAccount"))) {
                    String reason = (String) recommendation.get("reason");
                    if (reason != null && reason.contains("历史")) {
                        containsHistoryLeader = true;
                        break;
                    }
                }
            }
            Assert.assertTrue(containsHistoryLeader, "推荐结果应包含基于历史批复的领导");
        } catch (Exception e) {
            System.out.println("测试执行异常: " + e.getMessage());
            Assert.fail("测试执行失败: " + e.getMessage());
        }
    }
}
