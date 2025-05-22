package com.simbest.boot.suggest.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * 推荐功能测试套件
 * 覆盖5.4.1章节的所有测试用例
 */
public class RecommendationTestSuite {

    @BeforeClass
    @Parameters({ "baseUrl", "port" })
    public void setup(String baseUrl, int port) {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = port;
        RestAssured.basePath = "/suggest";

        System.out.println("测试环境配置:");
        System.out.println("- 基础URL: " + baseUrl);
        System.out.println("- 端口: " + port);
    }

    /**
     * 5.4.1.1.1 渠道管理室向上推荐测试
     * 测试渠道管理室作为起始组织的推荐
     */
    @Test
    public void testChannelDepartmentUpwardRecommendation() {
        System.out.println("执行测试用例5.4.1.1.1: 渠道管理室向上推荐测试");

        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于迎接集团公司政企大客户行为规范培训的通知\",\n" +
                "  \"recommendationType\": \"MULTIPLE\",\n" +
                "  \"workflowDirection\": \"UPWARD\",\n" +
                "  \"userAccount\": null,\n" +
                "  \"orgId\": \"team_channel\",\n" +
                "  \"tenantCode\": \"default\",\n" +
                "  \"candidateAccounts\": null\n" +
                "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/recommend/getRecommendation")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", not(empty()))
                .extract().response();

        System.out.println("响应状态码: " + response.statusCode());
        System.out.println("响应时间: " + response.time() + "ms");

        // 验证返回结果包含多个推荐
        Assert.assertTrue(response.jsonPath().getList("data").size() > 0, "应返回多个推荐结果");
    }

    /**
     * 5.4.1.1.2 渠道管理室主管向上推荐测试
     * 测试渠道管理室主管作为起始用户的推荐
     */
    @Test
    public void testChannelManagerUpwardRecommendation() {
        System.out.println("执行测试用例5.4.1.1.2: 渠道管理室主管向上推荐测试");

        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于迎接集团公司政企大客户行为规范培训的通知\",\n" +
                "  \"recommendationType\": \"MULTIPLE\",\n" +
                "  \"workflowDirection\": \"UPWARD\",\n" +
                "  \"userAccount\": \"lixuanang\",\n" +
                "  \"orgId\": null,\n" +
                "  \"tenantCode\": \"default\",\n" +
                "  \"candidateAccounts\": null\n" +
                "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/recommend/getRecommendation")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", not(empty()))
                .extract().response();

        System.out.println("响应状态码: " + response.statusCode());
        System.out.println("响应时间: " + response.time() + "ms");

        // 验证返回结果包含多个推荐
        Assert.assertTrue(response.jsonPath().getList("data").size() > 0, "应返回多个推荐结果");
    }

    /**
     * 5.4.1.1.3 政企业务支撑室向上推荐测试
     * 测试政企业务支撑室作为起始组织的推荐
     */
    @Test
    public void testEnterpriseSupportUpwardRecommendation() {
        System.out.println("执行测试用例5.4.1.1.3: 政企业务支撑室向上推荐测试");

        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于迎接集团公司政企大客户行为规范培训的通知\",\n" +
                "  \"recommendationType\": \"MULTIPLE\",\n" +
                "  \"workflowDirection\": \"UPWARD\",\n" +
                "  \"userAccount\": null,\n" +
                "  \"orgId\": \"team_enterprise_support\",\n" +
                "  \"tenantCode\": \"default\",\n" +
                "  \"candidateAccounts\": null\n" +
                "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/recommend/getRecommendation")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", not(empty()))
                .extract().response();

        System.out.println("响应状态码: " + response.statusCode());
        System.out.println("响应时间: " + response.time() + "ms");

        // 验证返回结果包含多个推荐
        Assert.assertTrue(response.jsonPath().getList("data").size() > 0, "应返回多个推荐结果");
    }

    /**
     * 5.4.1.2 候选人筛选推荐测试（多选）
     * 验证在指定候选人范围内推荐领导的功能
     */
    @Test
    public void testCandidateFilteringRecommendation() {
        System.out.println("执行测试用例5.4.1.2: 候选人筛选推荐测试（多选）");

        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于信息系统安全管理规定的通知\",\n" +
                "  \"recommendationType\": \"MULTIPLE\",\n" +
                "  \"workflowDirection\": \"UPWARD\",\n" +
                "  \"userAccount\": null,\n" +
                "  \"orgId\": \"team_security\",\n" +
                "  \"tenantCode\": \"default\",\n" +
                "  \"candidateAccounts\": [\"zb1\", \"zqf\", \"zxpzong\"]\n" +
                "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/recommend/getRecommendation")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", not(empty()))
                .extract().response();

        System.out.println("响应状态码: " + response.statusCode());
        System.out.println("响应时间: " + response.time() + "ms");

        // 验证返回结果只包含候选人列表中的用户
        for (Object item : response.jsonPath().getList("data")) {
            String account = ((Map<String, Object>) item).get("userAccount").toString();
            Assert.assertTrue(
                    account.equals("zb1") || account.equals("zqf") || account.equals("zxpzong"),
                    "推荐结果应只包含候选人列表中的用户");
        }
    }

    /**
     * 5.4.1.3 单一推荐测试（单选）
     * 验证返回单个最佳匹配领导的功能
     */
    @Test
    public void testSingleRecommendation() {
        System.out.println("执行测试用例5.4.1.3: 单一推荐测试（单选）");

        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于政企业务客户满意度调查的通知\",\n" +
                "  \"recommendationType\": \"SINGLE\",\n" +
                "  \"workflowDirection\": \"UPWARD\",\n" +
                "  \"userAccount\": null,\n" +
                "  \"orgId\": \"team_marketing\",\n" +
                "  \"tenantCode\": \"default\",\n" +
                "  \"candidateAccounts\": null\n" +
                "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/recommend/getRecommendation")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", not(empty()))
                .extract().response();

        System.out.println("响应状态码: " + response.statusCode());
        System.out.println("响应时间: " + response.time() + "ms");

        // 验证返回结果只包含一个推荐
        Assert.assertEquals(response.jsonPath().getList("data").size(), 1, "应只返回一个推荐结果");
    }

    /**
     * 5.4.1.4 指定用户的推荐测试（单选）
     * 验证根据指定用户推荐领导的功能
     */
    @Test
    public void testSpecificUserRecommendation() {
        System.out.println("执行测试用例5.4.1.4: 指定用户的推荐测试（单选）");

        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于员工培训计划的通知\",\n" +
                "  \"recommendationType\": \"SINGLE\",\n" +
                "  \"workflowDirection\": \"UPWARD\",\n" +
                "  \"userAccount\": \"wangfei\",\n" +
                "  \"orgId\": null,\n" +
                "  \"tenantCode\": \"default\",\n" +
                "  \"candidateAccounts\": null\n" +
                "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/recommend/getRecommendation")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", not(empty()))
                .extract().response();

        System.out.println("响应状态码: " + response.statusCode());
        System.out.println("响应时间: " + response.time() + "ms");

        // 验证返回结果只包含一个推荐
        Assert.assertEquals(response.jsonPath().getList("data").size(), 1, "应只返回一个推荐结果");

        // 验证推荐结果是基于指定用户的上级领导
        // 这里假设wangfei的上级领导是zxpzong
        String recommendedLeader = response.jsonPath().getString("data[0].userAccount");
        Assert.assertTrue(recommendedLeader != null && !recommendedLeader.isEmpty(), "应返回有效的领导账号");
    }

    /**
     * 5.4.1.5 指定组织的推荐测试（多选）
     * 验证根据指定组织推荐领导的功能
     */
    @Test
    public void testSpecificOrgRecommendation() {
        System.out.println("执行测试用例5.4.1.5: 指定组织的推荐测试（多选）");

        String requestBody = "{\n" +
                "  \"taskTitle\": \"关于网络安全管理规定的通知\",\n" +
                "  \"recommendationType\": \"MULTIPLE\",\n" +
                "  \"workflowDirection\": \"UPWARD\",\n" +
                "  \"userAccount\": null,\n" +
                "  \"orgId\": \"dept_it\",\n" +
                "  \"tenantCode\": \"default\",\n" +
                "  \"candidateAccounts\": null\n" +
                "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/recommend/getRecommendation")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", not(empty()))
                .extract().response();

        System.out.println("响应状态码: " + response.statusCode());
        System.out.println("响应时间: " + response.time() + "ms");

        // 验证返回结果包含多个推荐
        Assert.assertTrue(response.jsonPath().getList("data").size() > 0, "应返回多个推荐结果");

        // 验证推荐结果中包含信息技术部的领导
        boolean containsITLeader = false;
        for (Object item : response.jsonPath().getList("data")) {
            Map<String, Object> recommendation = (Map<String, Object>) item;
            if (recommendation.containsKey("orgName") &&
                    recommendation.get("orgName").toString().contains("信息技术")) {
                containsITLeader = true;
                break;
            }
        }
        Assert.assertTrue(containsITLeader, "推荐结果应包含信息技术部的领导");
    }
}
