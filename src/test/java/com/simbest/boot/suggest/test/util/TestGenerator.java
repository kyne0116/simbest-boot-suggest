package com.simbest.boot.suggest.test.util;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 测试代码生成器
 * 用于根据解析的测试用例生成TestNG测试类
 */
public class TestGenerator {

    private List<TestCase> testCases;
    private String outputDir;
    private String basePackage;

    public TestGenerator(List<TestCase> testCases, String outputDir, String basePackage) {
        this.testCases = testCases;
        this.outputDir = outputDir;
        this.basePackage = basePackage;
    }

    /**
     * 生成测试类
     */
    public void generateTestClasses() throws IOException {
        // 确保输出目录存在
        Path dirPath = Paths.get(outputDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // 按测试用例类型分组生成测试类
        generateRecommendationTests();
        generateDomainTests();
        generateOrganizationTests();
        generateHistoryTests();
        generateFeedbackTests();
    }

    /**
     * 生成推荐功能测试类
     */
    private void generateRecommendationTests() throws IOException {
        StringBuilder classContent = new StringBuilder();

        // 生成类头
        classContent.append("package ").append(basePackage).append(";\n\n");
        classContent.append("import io.restassured.RestAssured;\n");
        classContent.append("import io.restassured.http.ContentType;\n");
        classContent.append("import org.testng.annotations.BeforeClass;\n");
        classContent.append("import org.testng.annotations.Test;\n");
        classContent.append("import org.testng.annotations.Parameters;\n");
        classContent.append("import static io.restassured.RestAssured.*;\n");
        classContent.append("import static org.hamcrest.Matchers.*;\n\n");

        classContent.append("/**\n");
        classContent.append(" * 推荐功能测试类\n");
        classContent.append(" * 自动生成的测试类，基于测试用例文档\n");
        classContent.append(" */\n");
        classContent.append("public class RecommendationTest {\n\n");

        // 生成setup方法
        classContent.append("    @BeforeClass\n");
        classContent.append("    @Parameters({\"baseUrl\", \"port\"})\n");
        classContent.append("    public void setup(String baseUrl, int port) {\n");
        classContent.append("        RestAssured.baseURI = baseUrl;\n");
        classContent.append("        RestAssured.port = port;\n");
        classContent.append("        RestAssured.basePath = \"/suggest\";\n");
        classContent.append("    }\n\n");

        // 为每个推荐相关的测试用例生成测试方法
        for (TestCase testCase : testCases) {
            if (testCase.getId().startsWith("TC_REC_")) {
                classContent.append(generateTestMethod(testCase));
            }
        }

        // 生成类尾
        classContent.append("}\n");

        // 写入文件
        try (FileWriter writer = new FileWriter(outputDir + "/RecommendationTest.java")) {
            writer.write(classContent.toString());
        }
    }

    /**
     * 生成职责领域测试类
     */
    private void generateDomainTests() throws IOException {
        StringBuilder classContent = new StringBuilder();

        // 生成类头
        classContent.append("package ").append(basePackage).append(";\n\n");
        classContent.append("import io.restassured.RestAssured;\n");
        classContent.append("import io.restassured.http.ContentType;\n");
        classContent.append("import org.testng.annotations.BeforeClass;\n");
        classContent.append("import org.testng.annotations.Test;\n");
        classContent.append("import org.testng.annotations.Parameters;\n");
        classContent.append("import static io.restassured.RestAssured.*;\n");
        classContent.append("import static org.hamcrest.Matchers.*;\n\n");

        classContent.append("/**\n");
        classContent.append(" * 职责领域测试类\n");
        classContent.append(" * 自动生成的测试类，基于测试用例文档\n");
        classContent.append(" */\n");
        classContent.append("public class DomainTest {\n\n");

        // 生成setup方法
        classContent.append("    @BeforeClass\n");
        classContent.append("    @Parameters({\"baseUrl\", \"port\"})\n");
        classContent.append("    public void setup(String baseUrl, int port) {\n");
        classContent.append("        RestAssured.baseURI = baseUrl;\n");
        classContent.append("        RestAssured.port = port;\n");
        classContent.append("        RestAssured.basePath = \"/suggest\";\n");
        classContent.append("    }\n\n");

        // 为每个领域相关的测试用例生成测试方法
        for (TestCase testCase : testCases) {
            if (testCase.getId().startsWith("TC_DOM_")) {
                classContent.append(generateTestMethod(testCase));
            }
        }

        // 生成类尾
        classContent.append("}\n");

        // 写入文件
        try (FileWriter writer = new FileWriter(outputDir + "/DomainTest.java")) {
            writer.write(classContent.toString());
        }
    }

    // 其他测试类生成方法（组织关系、批复历史、反馈机制）类似，此处省略

    private void generateOrganizationTests() throws IOException {
        // 实现类似推荐测试的生成逻辑
    }

    private void generateHistoryTests() throws IOException {
        // 实现类似推荐测试的生成逻辑
    }

    private void generateFeedbackTests() throws IOException {
        // 实现类似推荐测试的生成逻辑
    }

    /**
     * 为单个测试用例生成测试方法
     */
    private String generateTestMethod(TestCase testCase) {
        StringBuilder methodContent = new StringBuilder();

        // 生成方法注释
        methodContent.append("    /**\n");
        methodContent.append("     * ").append(testCase.getName()).append("\n");
        methodContent.append("     * 目的: ").append(testCase.getPurpose()).append("\n");
        methodContent.append("     */\n");

        // 生成测试方法
        methodContent.append("    @Test\n");
        methodContent.append("    public void ").append(testCase.getId()).append("() {\n");

        // 根据HTTP方法生成不同的测试代码
        if ("POST".equals(testCase.getHttpMethod())) {
            methodContent.append("        given()\n");
            methodContent.append("            .contentType(ContentType.JSON)\n");
            methodContent.append("            .body(").append(formatJsonString(testCase.getRequestBody()))
                    .append(")\n");
            methodContent.append("        .when()\n");
            methodContent.append("            .post(\"").append(testCase.getEndpoint()).append("\")\n");
            methodContent.append("        .then()\n");
            methodContent.append("            .statusCode(200)\n");
            methodContent.append("            .body(\"success\", equalTo(true));\n");
        } else if ("GET".equals(testCase.getHttpMethod())) {
            methodContent.append("        given()\n");
            methodContent.append("        .when()\n");
            methodContent.append("            .get(\"").append(testCase.getEndpoint()).append("\")\n");
            methodContent.append("        .then()\n");
            methodContent.append("            .statusCode(200)\n");
            methodContent.append("            .body(\"success\", equalTo(true));\n");
        }

        methodContent.append("    }\n\n");

        return methodContent.toString();
    }

    /**
     * 格式化JSON字符串为Java代码中的字符串常量
     */
    private String formatJsonString(String json) {
        if (json == null || json.trim().isEmpty()) {
            return "\"{}\"";
        }

        // 简单处理，实际应用中可能需要更复杂的JSON处理
        return "\"" + json.replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }
}
