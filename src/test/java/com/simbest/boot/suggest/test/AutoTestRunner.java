package com.simbest.boot.suggest.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.simbest.boot.suggest.test.util.TestCase;
import com.simbest.boot.suggest.test.util.TestCaseParser;
import com.simbest.boot.suggest.test.util.TestGenerator;

/**
 * 自动化测试运行器
 * 解析测试用例文档，生成测试代码，并执行测试
 */
public class AutoTestRunner {

    private static String TEST_CASE_DOC = "docs/6-测试用例设计.md";
    private static final String OUTPUT_DIR = "src/test/java/com/simbest/boot/suggest/test/generated";
    private static final String BASE_PACKAGE = "com.simbest.boot.suggest.test.generated";
    private static final String TESTNG_XML = "src/test/resources/testng.xml";

    public static void main(String[] args) {
        try {
            // 解析命令行参数
            String baseUrl = "http://localhost";
            int port = 8080;
            boolean generateOnly = false;

            for (int i = 0; i < args.length; i++) {
                if ("-baseUrl".equals(args[i]) && i + 1 < args.length) {
                    baseUrl = args[++i];
                } else if ("-port".equals(args[i]) && i + 1 < args.length) {
                    port = Integer.parseInt(args[++i]);
                } else if ("-generateOnly".equals(args[i])) {
                    generateOnly = true;
                } else if ("-testCaseFile".equals(args[i]) && i + 1 < args.length) {
                    TEST_CASE_DOC = args[++i];
                }
            }

            System.out.println("开始解析测试用例文档: " + TEST_CASE_DOC);

            // 解析测试用例文档
            TestCaseParser parser = new TestCaseParser(TEST_CASE_DOC);
            List<TestCase> testCases = parser.parseTestCases();

            System.out.println("共解析到 " + testCases.size() + " 个测试用例");

            // 生成测试代码
            TestGenerator generator = new TestGenerator(testCases, OUTPUT_DIR, BASE_PACKAGE);
            generator.generateTestClasses();

            System.out.println("测试代码生成完成，输出目录: " + OUTPUT_DIR);

            if (generateOnly) {
                System.out.println("仅生成测试代码，不执行测试");
                return;
            }

            // 生成TestNG配置文件
            generateTestNGXml(baseUrl, port);

            System.out.println("TestNG配置文件生成完成: " + TESTNG_XML);

            // 执行测试
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
     * 生成TestNG配置文件
     */
    private static void generateTestNGXml(String baseUrl, int port) throws IOException {
        // 创建TestNG配置
        XmlSuite suite = new XmlSuite();
        suite.setName("智能领导推荐系统测试套件");

        // 设置参数
        Map<String, String> parameters = new HashMap<>();
        parameters.put("baseUrl", baseUrl);
        parameters.put("port", String.valueOf(port));
        suite.setParameters(parameters);

        // 添加监听器
        List<String> listeners = new ArrayList<>();
        listeners.add("com.simbest.boot.suggest.test.report.TestReportGenerator");
        suite.setListeners(listeners);

        // 创建测试
        XmlTest test = new XmlTest(suite);
        test.setName("API测试");

        // 添加测试类
        List<XmlClass> classes = new ArrayList<>();

        // 获取生成的测试类
        File generatedDir = new File(OUTPUT_DIR);
        if (generatedDir.exists() && generatedDir.isDirectory()) {
            File[] files = generatedDir.listFiles((dir, name) -> name.endsWith("Test.java"));
            if (files != null) {
                for (File file : files) {
                    String className = file.getName().replace(".java", "");
                    classes.add(new XmlClass(BASE_PACKAGE + "." + className));
                }
            }
        }

        test.setXmlClasses(classes);

        // 写入配置文件
        try (FileWriter writer = new FileWriter(TESTNG_XML)) {
            writer.write(suite.toXml());
        }
    }
}
