package com.simbest.boot.suggest.test;

import org.testng.TestNG;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 5.4.1推荐功能测试运行器
 * 用于直接从Java代码中运行测试
 */
public class RecommendationTest541Runner {

    private static final String TESTNG_XML = "src/test/resources/testng-recommendation-541.xml";

    public static void main(String[] args) {
        try {
            // 解析命令行参数
            String baseUrl = "http://localhost";
            int port = 12345;

            for (int i = 0; i < args.length; i++) {
                if ("-baseUrl".equals(args[i]) && i + 1 < args.length) {
                    baseUrl = args[++i];
                } else if ("-port".equals(args[i]) && i + 1 < args.length) {
                    port = Integer.parseInt(args[++i]);
                }
            }

            System.out.println("===== 智能领导推荐系统测试 =====");
            System.out.println("测试配置:");
            System.out.println("- 基础URL: " + baseUrl);
            System.out.println("- 端口: " + port);

            // 更新TestNG配置文件
            updateTestNGConfig(baseUrl, port);

            System.out.println("已更新测试配置文件: " + TESTNG_XML);

            // 执行测试
            System.out.println("开始执行测试...");
            TestNG testng = new TestNG();
            List<String> suites = new ArrayList<>();
            suites.add(TESTNG_XML);
            testng.setTestSuites(suites);
            testng.run();

            System.out.println("测试执行完成，请查看测试报告: test-output/custom-report.html");

        } catch (Exception e) {
            System.err.println("测试执行过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 更新TestNG配置文件中的参数
     */
    private static void updateTestNGConfig(String baseUrl, int port) throws IOException {
        File file = new File(TESTNG_XML);
        String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));

        // 更新参数
        content = content.replaceAll("<parameter name=\"baseUrl\" value=\".*\"/>",
                "<parameter name=\"baseUrl\" value=\"" + baseUrl + "\"/>");
        content = content.replaceAll("<parameter name=\"port\" value=\".*\"/>",
                "<parameter name=\"port\" value=\"" + port + "\"/>");

        // 写回文件
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }
}
