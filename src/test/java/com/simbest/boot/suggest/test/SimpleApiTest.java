package com.simbest.boot.suggest.test;

import static io.restassured.RestAssured.given;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * 简单API测试类
 * 用于验证测试技术是否可用
 */
public class SimpleApiTest {

    @BeforeClass
    public void setup() {
        // 设置基础URL和端口
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 12345;
        RestAssured.basePath = "/suggest";

        System.out.println("测试环境配置:");
        System.out.println("- 基础URL: " + RestAssured.baseURI);
        System.out.println("- 端口: " + RestAssured.port);
        System.out.println("- 基础路径: " + RestAssured.basePath);
    }

    @Test
    public void testHealthCheck() {
        // 测试健康检查接口
        System.out.println("执行健康检查测试...");

        try {
            Response response = given()
                    .when()
                    .get("/hello/hi")
                    .then()
                    .extract().response();

            System.out.println("健康检查响应状态码: " + response.statusCode());
            System.out.println("健康检查响应内容: " + response.asString());
        } catch (Exception e) {
            System.out.println("健康检查测试异常: " + e.getMessage());
            throw e; // 重新抛出异常，让测试失败
        }
    }

    @Test
    public void testSimpleRecommendation() {
        // 测试简单推荐接口
        System.out.println("执行简单推荐测试...");

        try {
            String requestBody = "{\n" +
                    "  \"taskTitle\": \"测试\",\n" +
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
                    .extract().response();

            System.out.println("推荐响应状态码: " + response.statusCode());
            System.out.println("推荐响应内容: " + response.asString());
        } catch (Exception e) {
            System.out.println("推荐测试异常: " + e.getMessage());
            throw e; // 重新抛出异常，让测试失败
        }
    }
}
