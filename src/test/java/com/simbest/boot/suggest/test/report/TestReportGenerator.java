package com.simbest.boot.suggest.test.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

/**
 * 测试报告生成器
 * 实现TestNG的IReporter接口，生成自定义HTML测试报告
 */
public class TestReportGenerator implements IReporter {

    private static final String REPORT_TEMPLATE = "src/test/resources/report-template.html";
    private static final String REPORT_OUTPUT = "test-output/custom-report.html";

    // 测试用例ID和名称的映射表
    private Map<String, String> testCaseIdMap = new HashMap<>();
    private Map<String, String> testCaseNameMap = new HashMap<>();

    // 测试用例顺序映射表 - 用于按文档顺序排序测试用例
    private Map<String, Integer> testCaseOrderMap = new HashMap<>();

    // 测试结果信息类，用于存储测试结果并排序
    private static class TestResultInfo {
        String testCaseId;
        String testCaseName;
        String status;
        long executionTime;
        String exceptionMessage;
        String cssClass;
    }

    // 测试用例章节结构
    private static final String[][] TEST_CASE_STRUCTURE = {
            { "6.4.1.1.1", "渠道管理室向上推荐测试" },
            { "6.4.1.1.2", "渠道管理室主管向上推荐测试" },
            { "6.4.1.1.3", "政企业务支撑室向上推荐测试" },
            { "6.4.1.2", "候选人筛选推荐测试（多选）" },
            { "6.4.1.3", "单一推荐测试（单选）" },
            { "6.4.1.4", "指定用户的推荐测试（单选）" },
            { "6.4.1.5", "指定组织的推荐测试（多选）" }
    };

    // 初始化测试用例映射表
    {
        // 为每个测试用例分配顺序号，用于排序
        for (int i = 0; i < TEST_CASE_STRUCTURE.length; i++) {
            String testCaseId = TEST_CASE_STRUCTURE[i][0];
            String testCaseName = TEST_CASE_STRUCTURE[i][1];

            testCaseOrderMap.put(testCaseId, i);
            testCaseNameMap.put(testCaseId, testCaseName);
        }

        // 6.4.1.1.1 渠道管理室向上推荐测试
        testCaseIdMap.put("TC_REC_54111", "6.4.1.1.1");
        testCaseIdMap.put("test_5_4_1_1_1_ChannelDepartmentUpwardRecommendation", "6.4.1.1.1");
        testCaseIdMap.put("test_6_4_1_1_1_ChannelDepartmentUpwardRecommendation", "6.4.1.1.1");

        // 6.4.1.1.2 渠道管理室主管向上推荐测试
        testCaseIdMap.put("TC_REC_54112", "6.4.1.1.2");
        testCaseIdMap.put("test_5_4_1_1_2_ChannelManagerUpwardRecommendation", "6.4.1.1.2");
        testCaseIdMap.put("test_6_4_1_1_2_ChannelManagerUpwardRecommendation", "6.4.1.1.2");

        // 6.4.1.1.3 政企业务支撑室向上推荐测试
        testCaseIdMap.put("TC_REC_54113", "6.4.1.1.3");
        testCaseIdMap.put("test_5_4_1_1_3_EnterpriseSupportUpwardRecommendation", "6.4.1.1.3");
        testCaseIdMap.put("test_6_4_1_1_3_EnterpriseSupportUpwardRecommendation", "6.4.1.1.3");

        // 6.4.1.2 候选人筛选推荐测试（多选）
        testCaseIdMap.put("TC_REC_5412", "6.4.1.2");
        testCaseIdMap.put("test_5_4_1_2_CandidateFilteringRecommendation", "6.4.1.2");
        testCaseIdMap.put("test_6_4_1_2_CandidateFilteringRecommendation", "6.4.1.2");

        // 6.4.1.3 单一推荐测试（单选）
        testCaseIdMap.put("TC_REC_5413", "6.4.1.3");
        testCaseIdMap.put("test_5_4_1_3_SingleRecommendation", "6.4.1.3");
        testCaseIdMap.put("test_6_4_1_3_SingleRecommendation", "6.4.1.3");

        // 6.4.1.4 指定用户的推荐测试（单选）
        testCaseIdMap.put("TC_REC_5414", "6.4.1.4");
        testCaseIdMap.put("test_5_4_1_4_SpecificUserRecommendation", "6.4.1.4");
        testCaseIdMap.put("test_6_4_1_4_SpecificUserRecommendation", "6.4.1.4");

        // 6.4.1.5 指定组织的推荐测试（多选）
        testCaseIdMap.put("TC_REC_5415", "6.4.1.5");
        testCaseIdMap.put("test_5_4_1_5_SpecificOrgRecommendation", "6.4.1.5");
        testCaseIdMap.put("test_6_4_1_5_SpecificOrgRecommendation", "6.4.1.5");

        // 兼容旧的方法名映射
        testCaseIdMap.put("testChannelDepartmentUpwardRecommendation", "6.4.1.1.1");
        testCaseIdMap.put("testChannelManagerUpwardRecommendation", "6.4.1.1.2");
        testCaseIdMap.put("testEnterpriseSupportUpwardRecommendation", "6.4.1.1.3");
        testCaseIdMap.put("testCandidateFilteringRecommendation", "6.4.1.2");
        testCaseIdMap.put("testSingleRecommendation", "6.4.1.3");
        testCaseIdMap.put("testSpecificUserRecommendation", "6.4.1.4");
        testCaseIdMap.put("testSpecificOrgRecommendation", "6.4.1.5");
    }

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        System.out.println("TestReportGenerator: generateReport 方法开始执行");
        try {
            // 创建输出目录
            File outputDir = new File(outputDirectory);
            if (!outputDir.exists()) {
                System.out.println("TestReportGenerator: 输出目录不存在，创建目录 " + outputDirectory);
                outputDir.mkdirs();
            } else {
                System.out.println("TestReportGenerator: 输出目录已存在 " + outputDirectory);
            }

            // 读取模板文件
            String templateContent = new String(Files.readAllBytes(Paths.get(REPORT_TEMPLATE)), StandardCharsets.UTF_8);
            System.out.println("TestReportGenerator: 模板文件读取成功");

            // 生成测试详情内容
            StringBuilder testDetailsBuilder = new StringBuilder();

            // 测试摘要
            int totalTests = 0;
            int passedTests = 0;
            int failedTests = 0;
            int skippedTests = 0;

            // 处理每个测试套件
            for (ISuite suite : suites) {
                System.out.println("TestReportGenerator: 处理测试套件 " + suite.getName());
                testDetailsBuilder.append("<h2>测试套件: ").append(suite.getName()).append("</h2>");

                Map<String, ISuiteResult> suiteResults = suite.getResults();
                for (ISuiteResult suiteResult : suiteResults.values()) {
                    ITestContext testContext = suiteResult.getTestContext();

                    // 统计测试结果
                    Set<ITestResult> passedTestResults = testContext.getPassedTests().getAllResults();
                    Set<ITestResult> failedTestResults = testContext.getFailedTests().getAllResults();
                    Set<ITestResult> skippedTestResults = testContext.getSkippedTests().getAllResults();

                    totalTests += passedTestResults.size() + failedTestResults.size() + skippedTestResults.size();
                    passedTests += passedTestResults.size();
                    failedTests += failedTestResults.size();
                    skippedTests += skippedTestResults.size();

                    // 测试摘要
                    testDetailsBuilder.append("<div class='summary'>");
                    testDetailsBuilder.append("<h3>测试: ").append(testContext.getName()).append("</h3>");
                    testDetailsBuilder.append("<p>总测试数: ")
                            .append(passedTestResults.size() + failedTestResults.size() + skippedTestResults.size())
                            .append("</p>");
                    testDetailsBuilder.append("<p>通过: ").append(passedTestResults.size()).append("</p>");
                    testDetailsBuilder.append("<p>失败: ").append(failedTestResults.size()).append("</p>");
                    testDetailsBuilder.append("<p>跳过: ").append(skippedTestResults.size()).append("</p>");
                    testDetailsBuilder.append("</div>");

                    // 收集所有测试结果并按文档顺序排序
                    List<TestResultInfo> allTestResults = new ArrayList<>();

                    // 收集通过的测试
                    for (ITestResult result : passedTestResults) {
                        String methodName = result.getMethod().getMethodName();
                        String testCaseId = getTestCaseIdFromMethodName(methodName);
                        String testCaseName = getTestCaseNameFromMethodName(methodName);

                        TestResultInfo testInfo = new TestResultInfo();
                        testInfo.testCaseId = testCaseId;
                        testInfo.testCaseName = testCaseName;
                        testInfo.status = "通过";
                        testInfo.executionTime = result.getEndMillis() - result.getStartMillis();
                        testInfo.exceptionMessage = "";
                        testInfo.cssClass = "pass";

                        allTestResults.add(testInfo);
                    }

                    // 收集失败的测试
                    for (ITestResult result : failedTestResults) {
                        String methodName = result.getMethod().getMethodName();
                        String testCaseId = getTestCaseIdFromMethodName(methodName);
                        String testCaseName = getTestCaseNameFromMethodName(methodName);

                        TestResultInfo testInfo = new TestResultInfo();
                        testInfo.testCaseId = testCaseId;
                        testInfo.testCaseName = testCaseName;
                        testInfo.status = "失败";
                        testInfo.executionTime = result.getEndMillis() - result.getStartMillis();
                        testInfo.exceptionMessage = result.getThrowable() != null ? result.getThrowable().getMessage()
                                : "";
                        testInfo.cssClass = "fail";

                        allTestResults.add(testInfo);

                        // 输出详细的失败信息到控制台
                        System.out.println(
                                "测试失败: " + testCaseId + " " + testCaseName + ", 执行时间: " + testInfo.executionTime
                                        + "ms");
                        System.out.println("异常信息: " + testInfo.exceptionMessage);
                        if (result.getThrowable() != null) {
                            result.getThrowable().printStackTrace();
                        }
                    }

                    // 收集跳过的测试
                    for (ITestResult result : skippedTestResults) {
                        String methodName = result.getMethod().getMethodName();
                        String testCaseId = getTestCaseIdFromMethodName(methodName);
                        String testCaseName = getTestCaseNameFromMethodName(methodName);

                        TestResultInfo testInfo = new TestResultInfo();
                        testInfo.testCaseId = testCaseId;
                        testInfo.testCaseName = testCaseName;
                        testInfo.status = "跳过";
                        testInfo.executionTime = 0;
                        testInfo.exceptionMessage = result.getThrowable() != null ? result.getThrowable().getMessage()
                                : "";
                        testInfo.cssClass = "skip";

                        allTestResults.add(testInfo);
                    }

                    // 按照文档中的顺序排序测试用例
                    allTestResults.sort(new Comparator<TestResultInfo>() {
                        @Override
                        public int compare(TestResultInfo o1, TestResultInfo o2) {
                            Integer order1 = testCaseOrderMap.getOrDefault(o1.testCaseId, Integer.MAX_VALUE);
                            Integer order2 = testCaseOrderMap.getOrDefault(o2.testCaseId, Integer.MAX_VALUE);
                            return order1.compareTo(order2);
                        }
                    });

                    // 测试详情表格
                    testDetailsBuilder.append("<table>");
                    testDetailsBuilder
                            .append("<tr><th>测试用例编号</th><th>测试用例名称</th><th>状态</th><th>执行时间(ms)</th><th>异常信息</th></tr>");

                    // 按排序后的顺序输出测试结果
                    for (TestResultInfo testInfo : allTestResults) {
                        testDetailsBuilder.append("<tr class='").append(testInfo.cssClass).append("'>");
                        testDetailsBuilder.append("<td>").append(testInfo.testCaseId).append("</td>");
                        testDetailsBuilder.append("<td>").append(testInfo.testCaseName).append("</td>");
                        testDetailsBuilder.append("<td>").append(testInfo.status).append("</td>");
                        testDetailsBuilder.append("<td>").append(testInfo.executionTime).append("</td>");
                        testDetailsBuilder.append("<td>").append(testInfo.exceptionMessage).append("</td>");
                        testDetailsBuilder.append("</tr>");
                    }

                    testDetailsBuilder.append("</table>");
                }
            }

            // 替换模板中的占位符
            String reportContent = templateContent
                    .replace("${timestamp}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                    .replace("${testSuiteName}", "智能领导推荐系统测试套件")
                    .replace("${totalTests}", String.valueOf(totalTests))
                    .replace("${passedTests}", String.valueOf(passedTests))
                    .replace("${failedTests}", String.valueOf(failedTests))
                    .replace("${skippedTests}", String.valueOf(skippedTests))
                    .replace("${testDetails}", testDetailsBuilder.toString());

            // 写入报告文件，使用UTF-8编码
            File reportFile = new File(outputDirectory, "custom-report.html");
            try (FileOutputStream fos = new FileOutputStream(reportFile);
                    OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
                writer.write(reportContent);
            }

            System.out.println("TestReportGenerator: 自定义测试报告已生成: "
                    + new File(outputDirectory, "custom-report.html").getAbsolutePath());

        } catch (Exception e) {
            System.err.println("TestReportGenerator: 生成测试报告时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 从测试方法名中提取测试用例编号
     * 例如：从TC_REC_54111提取为5.4.1.1.1
     */
    private String getTestCaseIdFromMethodName(String methodName) {
        // 首先检查映射表
        if (testCaseIdMap.containsKey(methodName)) {
            return testCaseIdMap.get(methodName);
        }

        // 如果映射表中没有，尝试从方法名中提取
        // 例如：test_5_4_1_1_1_ChannelDepartmentUpwardRecommendation -> 5.4.1.1.1
        if (methodName.startsWith("test_")) {
            // 尝试提取格式为test_5_4_1_1_1_XXX的测试用例ID
            String[] parts = methodName.split("_");
            if (parts.length >= 6) {
                return parts[1] + "." + parts[2] + "." + parts[3] +
                        (parts.length > 4 ? "." + parts[4] : "") +
                        (parts.length > 5 ? "." + parts[5] : "");
            }
        }
        // 例如：TC_REC_54111 -> 5.4.1.1.1
        else if (methodName.startsWith("TC_REC_")) {
            String numPart = methodName.substring(7); // 去掉"TC_REC_"前缀

            // 对于5位数字的情况（如54111），转换为5.4.1.1.1格式
            if (numPart.length() == 5) {
                return numPart.charAt(0) + "." +
                        numPart.charAt(1) + "." +
                        numPart.charAt(2) + "." +
                        numPart.charAt(3) + "." +
                        numPart.charAt(4);
            }
            // 对于4位数字的情况（如5411），转换为5.4.1.1格式
            else if (numPart.length() == 4) {
                return numPart.charAt(0) + "." +
                        numPart.charAt(1) + "." +
                        numPart.charAt(2) + "." +
                        numPart.charAt(3);
            }
            // 对于3位数字的情况（如541），转换为5.4.1格式
            else if (numPart.length() == 3) {
                return numPart.charAt(0) + "." +
                        numPart.charAt(1) + "." +
                        numPart.charAt(2);
            }
        }

        return methodName; // 如果无法提取，返回原方法名
    }

    /**
     * 从测试方法名中提取测试用例名称
     */
    private String getTestCaseNameFromMethodName(String methodName) {
        // 首先检查映射表中是否有直接的方法名映射
        if (testCaseNameMap.containsKey(methodName)) {
            return testCaseNameMap.get(methodName);
        }

        // 尝试获取测试用例ID
        String testCaseId = getTestCaseIdFromMethodName(methodName);

        // 如果测试用例ID与方法名不同，说明成功提取了ID，尝试通过ID查找名称
        if (!testCaseId.equals(methodName) && testCaseNameMap.containsKey(testCaseId)) {
            return testCaseNameMap.get(testCaseId);
        }

        // 如果都没有找到，尝试从方法名中提取有意义的名称
        if (methodName.contains("_")) {
            String[] parts = methodName.split("_");
            if (parts.length > 0) {
                // 取最后一部分作为名称，通常是最有描述性的部分
                String lastPart = parts[parts.length - 1];

                // 将驼峰命名转换为更可读的格式
                StringBuilder readableName = new StringBuilder();
                for (int i = 0; i < lastPart.length(); i++) {
                    char c = lastPart.charAt(i);
                    if (i > 0 && Character.isUpperCase(c)) {
                        readableName.append(' ');
                    }
                    readableName.append(c);
                }

                return readableName.toString();
            }
        }

        // 如果映射表中没有，返回方法名
        return methodName;
    }
}
