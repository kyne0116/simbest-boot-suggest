package com.simbest.boot.suggest.test.generated;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.Parameters;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * 职责领域测试类
 * 自动生成的测试类，基于测试用例文档
 */
public class DomainTest {

    @BeforeClass
    @Parameters({"baseUrl", "port"})
    public void setup(String baseUrl, int port) {
        RestAssured.baseURI = baseUrl;
        RestAssured.port = port;
        RestAssured.basePath = "/suggest";
    }

}
