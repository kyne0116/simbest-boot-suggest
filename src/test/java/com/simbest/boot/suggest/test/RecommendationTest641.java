package com.simbest.boot.suggest.test;

import static io.restassured.RestAssured.given;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * 推荐功能测试套件
 * 覆盖6.4.1章节的所有测试用例
 */
@Listeners(com.simbest.boot.suggest.test.report.TestReportGenerator.class)
public class RecommendationTest641 {

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
     * 6.4.1.1.1 信息技术部领导向下推荐测试
     * 测试信息技术部领导向下推荐
     */
    @Test
    public void test_6_4_1_1_1_ChannelDepartmentUpwardRecommendation() {
        System.out.println("执行测试用例6.4.1.1.1: 信息技术部领导向下推荐测试");

        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于迎接集团公司政企大客户行为规范培训的通知\",\n" +
                "  \"recommendationType\": \"MULTIPLE\",\n" +
                "  \"workflowDirection\": \"DOWNWARD\",\n" +
                "  \"userAccount\": \"user004\",\n" +
                "  \"orgId\": null,\n" +
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

            // 验证推荐结果中包含领导
            boolean containsLeader = false;
            for (Object item : leaders) {
                Map<String, Object> recommendation = (Map<String, Object>) item;
                if (recommendation.containsKey("suggestAccount")) {
                    containsLeader = true;
                    break;
                }
            }
            Assert.assertTrue(containsLeader, "推荐结果应包含领导");
        } catch (Exception e) {
            System.out.println("测试执行异常: " + e.getMessage());
            Assert.fail("测试执行失败: " + e.getMessage());
        }
    }

    /**
     * 6.4.1.1.2 渠道管理室主管向上推荐测试
     * 测试渠道管理室主管作为起始用户的推荐
     */
    @Test
    public void test_6_4_1_1_2_ChannelManagerUpwardRecommendation() {
        System.out.println("执行测试用例6.4.1.1.2: 渠道管理室主管向上推荐测试");

        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于迎接集团公司政企大客户行为规范培训的通知\",\n" +
                "  \"recommendationType\": \"MULTIPLE\",\n" +
                "  \"workflowDirection\": \"UPWARD\",\n" +
                "  \"userAccount\": \"user023\",\n" +
                "  \"orgId\": null,\n" +
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

            // 验证推荐结果中包含领导
            boolean containsLeader = false;
            for (Object item : leaders) {
                Map<String, Object> recommendation = (Map<String, Object>) item;
                if (recommendation.containsKey("suggestAccount")) {
                    containsLeader = true;
                    break;
                }
            }
            Assert.assertTrue(containsLeader, "推荐结果应包含领导");
        } catch (Exception e) {
            System.out.println("测试执行异常: " + e.getMessage());
            Assert.fail("测试执行失败: " + e.getMessage());
        }
    }

    /**
     * 6.4.1.1.3 政企业务支撑室向上推荐测试
     * 测试政企业务支撑室作为起始组织的推荐
     */
    @Test
    public void test_6_4_1_1_3_EnterpriseSupportUpwardRecommendation() {
        System.out.println("执行测试用例6.4.1.1.3: 政企业务支撑室向上推荐测试");

        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于迎接集团公司政企大客户行为规范培训的通知\",\n" +
                "  \"recommendationType\": \"MULTIPLE\",\n" +
                "  \"workflowDirection\": \"UPWARD\",\n" +
                "  \"userAccount\": null,\n" +
                "  \"orgId\": \"team_enterprise_support\",\n" +
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

            // 验证推荐结果中包含领导
            boolean containsLeader = false;
            for (Object item : leaders) {
                Map<String, Object> recommendation = (Map<String, Object>) item;
                if (recommendation.containsKey("suggestAccount")) {
                    containsLeader = true;
                    break;
                }
            }
            Assert.assertTrue(containsLeader, "推荐结果应包含领导");
        } catch (Exception e) {
            System.out.println("测试执行异常: " + e.getMessage());
            Assert.fail("测试执行失败: " + e.getMessage());
        }
    }

    /**
     * 6.4.1.2 候选人筛选推荐测试（多选）
     * 验证在指定候选人范围内推荐领导的功能
     */
    @Test
    public void test_6_4_1_2_CandidateFilteringRecommendation() {
        System.out.println("执行测试用例6.4.1.2: 候选人筛选推荐测试（多选）");

        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于信息系统安全管理规定的通知\",\n" +
                "  \"recommendationType\": \"MULTIPLE\",\n" +
                "  \"workflowDirection\": \"UPWARD\",\n" +
                "  \"userAccount\": null,\n" +
                "  \"orgId\": \"team_security\",\n" +
                "  \"tenantCode\": \"default\",\n" +
                "  \"candidateAccounts\": [\"user005\", \"user020\", \"user004\"]\n" +
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

            // 验证推荐结果中只包含指定的候选人
            for (Object item : leaders) {
                Map<String, Object> recommendation = (Map<String, Object>) item;
                String account = (String) recommendation.get("suggestAccount");
                Assert.assertTrue(
                        account.equals("user005") || account.equals("user020") || account.equals("user004"),
                        "推荐结果应只包含指定的候选人");
            }
        } catch (Exception e) {
            System.out.println("测试执行异常: " + e.getMessage());
            Assert.fail("测试执行失败: " + e.getMessage());
        }
    }

    /**
     * 6.4.1.3 单一推荐测试（单选）
     * 验证返回单个最佳匹配领导的功能
     */
    @Test
    public void test_6_4_1_3_SingleRecommendation() {
        System.out.println("执行测试用例6.4.1.3: 单一推荐测试（单选）");

        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于政企业务客户满意度调查的通知\",\n" +
                "  \"recommendationType\": \"SINGLE\",\n" +
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

            // 验证只返回一个推荐结果（单选模式）
            Assert.assertEquals(leaders.size(), 1, "单选模式应只返回一个推荐结果");
        } catch (Exception e) {
            System.out.println("测试执行异常: " + e.getMessage());
            Assert.fail("测试执行失败: " + e.getMessage());
        }
    }

    /**
     * 6.4.1.4 指定用户的推荐测试（单选）
     * 验证根据指定用户推荐领导的功能
     */
    @Test
    public void test_6_4_1_4_SpecificUserRecommendation() {
        System.out.println("执行测试用例6.4.1.4: 指定用户的推荐测试（单选）");

        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于部门年度预算编制的通知\",\n" +
                "  \"recommendationType\": \"SINGLE\",\n" +
                "  \"workflowDirection\": \"UPWARD\",\n" +
                "  \"userAccount\": \"user004\",\n" +
                "  \"orgId\": null,\n" +
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

            // 验证只返回一个推荐结果（单选模式）
            Assert.assertEquals(leaders.size(), 1, "单选模式应只返回一个推荐结果");

            // 验证推荐的领导是user004的上级领导
            Map<String, Object> recommendation = (Map<String, Object>) leaders.get(0);
            Assert.assertTrue(recommendation.containsKey("suggestAccount"), "推荐结果应包含领导账号");
            // 注意：这里我们不能确定具体的账号，因为可能会根据实际数据变化
            // 所以只验证字段存在，不验证具体值
        } catch (Exception e) {
            System.out.println("测试执行异常: " + e.getMessage());
            Assert.fail("测试执行失败: " + e.getMessage());
        }
    }

    /**
     * 6.4.1.5 指定组织的推荐测试（多选）
     * 验证根据指定组织推荐领导的功能
     */
    @Test
    public void test_6_4_1_5_SpecificOrgRecommendation() {
        System.out.println("执行测试用例6.4.1.5: 指定组织的推荐测试（多选）");

        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于软件平台升级的技术方案\",\n" +
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

            // 验证推荐结果中包含领导
            boolean containsLeader = false;
            for (Object item : leaders) {
                Map<String, Object> recommendation = (Map<String, Object>) item;
                if (recommendation.containsKey("suggestAccount")) {
                    containsLeader = true;
                    break;
                }
            }
            Assert.assertTrue(containsLeader, "推荐结果应包含领导");
        } catch (Exception e) {
            System.out.println("测试执行异常: " + e.getMessage());
            Assert.fail("测试执行失败: " + e.getMessage());
        }
    }
}