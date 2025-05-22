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
 * 反馈机制测试套件
 * 覆盖6.4.5章节的所有测试用例
 */
public class FeedbackTest645 {

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
     * 6.4.5.1 推荐反馈保存测试
     * 验证保存用户对推荐结果反馈的功能
     */
    @Test
    public void test_6_4_5_1_RecommendationFeedbackSave() {
        System.out.println("执行测试用例6.4.5.1: 推荐反馈保存测试");

        // 第一步：获取推荐结果
        String recommendRequestBody = "{\n" +
                "  \"taskTitle\": \"关于市场营销策略调整的通知\",\n" +
                "  \"recommendationType\": \"MULTIPLE\",\n" +
                "  \"workflowDirection\": \"UPWARD\",\n" +
                "  \"userAccount\": \"lixuanang\",\n" +
                "  \"orgId\": null,\n" +
                "  \"tenantCode\": \"default\",\n" +
                "  \"candidateAccounts\": null\n" +
                "}";

        try {
            // 获取推荐结果
            Response recommendResponse = given()
                    .contentType(ContentType.JSON)
                    .body(recommendRequestBody)
                    .when()
                    .post("/recommend/getRecommendation")
                    .then()
                    .extract().response();

            System.out.println("推荐响应状态码: " + recommendResponse.statusCode());
            System.out.println("推荐响应内容: " + recommendResponse.asString());

            // 验证推荐响应状态码为200
            Assert.assertEquals(recommendResponse.getStatusCode(), 200, "推荐响应状态码应为200");
            Assert.assertEquals(recommendResponse.jsonPath().getInt("errcode"), 0, "推荐响应中errcode字段应为0");

            // 获取推荐ID和推荐的领导账号
            String recommendId = recommendResponse.jsonPath().getString("data.recommendId");
            List<?> leaders = recommendResponse.jsonPath().getList("data.leaders");
            Assert.assertNotNull(leaders, "推荐结果中应包含领导列表");
            Assert.assertFalse(leaders.isEmpty(), "推荐结果不应为空");

            Map<String, Object> leader = (Map<String, Object>) leaders.get(0);
            String leaderAccount = (String) leader.get("suggestAccount");

            // 第二步：提交反馈
            String feedbackRequestBody = "{\n" +
                    "  \"recommendId\": \"" + recommendId + "\",\n" +
                    "  \"taskId\": \"test-task-002\",\n" +
                    "  \"taskTitle\": \"关于市场营销策略调整的通知\",\n" +
                    "  \"selectedAccount\": \"" + leaderAccount + "\",\n" +
                    "  \"userAccount\": \"lixuanang\",\n" +
                    "  \"feedbackType\": \"ACCEPT\",\n" +
                    "  \"feedbackComment\": \"推荐结果很准确\",\n" +
                    "  \"tenantCode\": \"default\"\n" +
                    "}";

            Response feedbackResponse = given()
                    .contentType(ContentType.JSON)
                    .body(feedbackRequestBody)
                    .when()
                    .post("/feedback/saveRecommendationFeedback")
                    .then()
                    .extract().response();

            System.out.println("反馈响应状态码: " + feedbackResponse.statusCode());
            System.out.println("反馈响应内容: " + feedbackResponse.asString());

            // 验证反馈响应状态码为200
            Assert.assertEquals(feedbackResponse.getStatusCode(), 200, "反馈响应状态码应为200");
            Assert.assertEquals(feedbackResponse.jsonPath().getInt("errcode"), 0, "反馈响应中errcode字段应为0");

            // 第三步：查询反馈记录
            Response queryResponse = given()
                    .contentType(ContentType.JSON)
                    .queryParam("recommendId", recommendId)
                    .queryParam("tenantCode", "default")
                    .when()
                    .get("/feedback/getRecommendationFeedback")
                    .then()
                    .extract().response();

            System.out.println("查询反馈响应状态码: " + queryResponse.statusCode());
            System.out.println("查询反馈响应内容: " + queryResponse.asString());

            // 验证查询响应状态码为200
            Assert.assertEquals(queryResponse.getStatusCode(), 200, "查询反馈响应状态码应为200");
            Assert.assertEquals(queryResponse.jsonPath().getInt("errcode"), 0, "查询反馈响应中errcode字段应为0");

            // 验证返回结果包含反馈记录
            Assert.assertNotNull(queryResponse.jsonPath().get("data"), "响应中data字段不应为null");
            Assert.assertEquals(queryResponse.jsonPath().getString("data.recommendId"), recommendId, "反馈记录的recommendId应与提交的一致");
            Assert.assertEquals(queryResponse.jsonPath().getString("data.selectedAccount"), leaderAccount, "反馈记录的selectedAccount应与提交的一致");
            Assert.assertEquals(queryResponse.jsonPath().getString("data.feedbackType"), "ACCEPT", "反馈记录的feedbackType应与提交的一致");
        } catch (Exception e) {
            System.out.println("测试执行异常: " + e.getMessage());
            Assert.fail("测试执行失败: " + e.getMessage());
        }
    }

    /**
     * 6.4.5.2 推荐接受率计算测试
     * 验证计算推荐接受率的功能
     */
    @Test
    public void test_6_4_5_2_RecommendationAcceptanceRateCalculation() {
        System.out.println("执行测试用例6.4.5.2: 推荐接受率计算测试");

        // 首先确保有反馈记录
        test_6_4_5_1_RecommendationFeedbackSave();

        try {
            // 查询推荐接受率统计
            Response response = given()
                    .contentType(ContentType.JSON)
                    .queryParam("tenantCode", "default")
                    .when()
                    .get("/feedback/getRecommendationAcceptanceRate")
                    .then()
                    .extract().response();

            System.out.println("接受率统计响应状态码: " + response.statusCode());
            System.out.println("接受率统计响应内容: " + response.asString());

            // 验证响应状态码为200
            Assert.assertEquals(response.getStatusCode(), 200, "接受率统计响应状态码应为200");
            Assert.assertEquals(response.jsonPath().getInt("errcode"), 0, "接受率统计响应中errcode字段应为0");

            // 验证返回结果包含统计数据
            Assert.assertNotNull(response.jsonPath().get("data"), "响应中data字段不应为null");
            
            // 验证统计数据包含总数、接受数和接受率
            Assert.assertTrue(response.jsonPath().getInt("data.totalCount") > 0, "总推荐数应大于0");
            Assert.assertTrue(response.jsonPath().getInt("data.acceptCount") > 0, "接受数应大于0");
            Assert.assertTrue(response.jsonPath().getFloat("data.acceptanceRate") > 0, "接受率应大于0");
            
            // 验证接受率计算正确
            float acceptanceRate = response.jsonPath().getFloat("data.acceptanceRate");
            int totalCount = response.jsonPath().getInt("data.totalCount");
            int acceptCount = response.jsonPath().getInt("data.acceptCount");
            
            float expectedRate = (float) acceptCount / totalCount;
            Assert.assertEquals(acceptanceRate, expectedRate, 0.01, "接受率计算应正确");
        } catch (Exception e) {
            System.out.println("测试执行异常: " + e.getMessage());
            Assert.fail("测试执行失败: " + e.getMessage());
        }
    }
}
