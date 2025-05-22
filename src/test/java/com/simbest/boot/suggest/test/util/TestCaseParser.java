package com.simbest.boot.suggest.test.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 测试用例文档解析器
 * 用于从Markdown格式的测试用例文档中提取测试用例信息
 */
public class TestCaseParser {

    private String markdownFilePath;
    private List<TestCase> testCases = new ArrayList<>();

    public TestCaseParser(String markdownFilePath) {
        this.markdownFilePath = markdownFilePath;
    }

    /**
     * 解析测试用例文档
     *
     * @return 测试用例列表
     */
    public List<TestCase> parseTestCases() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(markdownFilePath)), StandardCharsets.UTF_8);

        // 使用正则表达式匹配测试用例
        // 匹配格式：##### 5.4.1.1.1 渠道管理室向上推荐测试
        Pattern testCasePattern = Pattern.compile("#{5}\\s+(\\d+\\.\\d+\\.\\d+\\.\\d+(?:\\.\\d+)?)\\s+(.+?)(?=#{5}|$)",
                Pattern.DOTALL);
        Matcher matcher = testCasePattern.matcher(content);

        while (matcher.find()) {
            String testCaseContent = matcher.group(0);
            TestCase testCase = parseTestCase(testCaseContent);
            if (testCase != null) {
                testCases.add(testCase);
            }
        }

        return testCases;
    }

    /**
     * 解析单个测试用例
     */
    private TestCase parseTestCase(String testCaseContent) {
        TestCase testCase = new TestCase();

        // 提取测试用例ID和名称
        Pattern idPattern = Pattern.compile("#{5}\\s+(\\d+\\.\\d+\\.\\d+\\.\\d+(?:\\.\\d+)?)\\s+(.+?)\\s*\\n");
        Matcher idMatcher = idPattern.matcher(testCaseContent);
        if (idMatcher.find()) {
            String id = idMatcher.group(1).trim();
            String name = idMatcher.group(2).trim();
            // 将5.4.1.1.1格式转换为TC_REC_54111格式
            String methodId = id.replace(".", "");
            testCase.setId("TC_REC_" + methodId);
            testCase.setOriginalId(id); // 保存原始ID，用于报告显示
            testCase.setName(name);
            System.out.println("解析到测试用例: " + id + " -> " + testCase.getId() + " - " + testCase.getName());
        }

        // 提取测试目的
        Pattern purposePattern = Pattern.compile("\\*\\*目的\\*\\*：(.+?)(?=\\*\\*前置条件|$)", Pattern.DOTALL);
        Matcher purposeMatcher = purposePattern.matcher(testCaseContent);
        if (purposeMatcher.find()) {
            testCase.setPurpose(purposeMatcher.group(1).trim());
        }

        // 提取测试步骤
        Pattern stepsPattern = Pattern.compile("\\*\\*测试步骤\\*\\*：(.*?)\\*\\*预期结果\\*\\*", Pattern.DOTALL);
        Matcher stepsMatcher = stepsPattern.matcher(testCaseContent);
        if (stepsMatcher.find()) {
            String stepsContent = stepsMatcher.group(1);
            testCase.setSteps(parseSteps(stepsContent));
        }

        // 提取API请求
        Pattern apiPattern = Pattern.compile("```\\s*\\n(POST|GET|PUT|DELETE)\\s+([^\\n]+)\\s*\\n(.*?)```",
                Pattern.DOTALL);
        Matcher apiMatcher = apiPattern.matcher(testCaseContent);
        if (apiMatcher.find()) {
            testCase.setHttpMethod(apiMatcher.group(1).trim());
            testCase.setEndpoint(apiMatcher.group(2).trim());
            testCase.setRequestBody(apiMatcher.group(3).trim());
            System.out.println("解析到API请求: " + testCase.getHttpMethod() + " " + testCase.getEndpoint());
        } else {
            // 尝试另一种格式的API请求
            Pattern altApiPattern = Pattern.compile(
                    "调用(.+?)API接口.*?```\\s*\\n(POST|GET|PUT|DELETE)\\s+([^\\n]+)\\s*\\n(.*?)```",
                    Pattern.DOTALL);
            Matcher altApiMatcher = altApiPattern.matcher(testCaseContent);
            if (altApiMatcher.find()) {
                testCase.setHttpMethod(altApiMatcher.group(2).trim());
                testCase.setEndpoint(altApiMatcher.group(3).trim());
                testCase.setRequestBody(altApiMatcher.group(4).trim());
                System.out.println("解析到API请求(替代格式): " + testCase.getHttpMethod() + " " + testCase.getEndpoint());
            }
        }

        // 提取预期结果
        Pattern expectedPattern = Pattern.compile("\\*\\*预期结果\\*\\*：(.*?)(?=\\*\\*测试结果依据|$)", Pattern.DOTALL);
        Matcher expectedMatcher = expectedPattern.matcher(testCaseContent);
        if (expectedMatcher.find()) {
            testCase.setExpectedResults(parseExpectedResults(expectedMatcher.group(1)));
        }

        return testCase;
    }

    private List<String> parseSteps(String stepsContent) {
        List<String> steps = new ArrayList<>();
        Pattern stepPattern = Pattern.compile("\\d+\\.\\s+(.+?)(?=\\d+\\.\\s+|$)", Pattern.DOTALL);
        Matcher stepMatcher = stepPattern.matcher(stepsContent);

        while (stepMatcher.find()) {
            steps.add(stepMatcher.group(1).trim());
        }

        return steps;
    }

    private Map<String, Object> parseExpectedResults(String expectedContent) {
        Map<String, Object> expected = new HashMap<>();
        Pattern resultPattern = Pattern.compile("\\d+\\.\\s+(.+?)(?=\\d+\\.\\s+|$)", Pattern.DOTALL);
        Matcher resultMatcher = resultPattern.matcher(expectedContent);

        int index = 1;
        while (resultMatcher.find()) {
            expected.put("result" + index, resultMatcher.group(1).trim());
            index++;
        }

        return expected;
    }
}
