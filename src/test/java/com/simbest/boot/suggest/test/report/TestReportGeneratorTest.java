package com.simbest.boot.suggest.test.report;

import java.util.ArrayList;
import java.util.List;

import org.testng.ISuite;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlSuite.ParallelMode;

public class TestReportGeneratorTest {

    public static void main(String[] args) {
        TestReportGenerator generator = new TestReportGenerator();

        // 创建空的XmlSuite列表和ISuite列表用于测试
        List<XmlSuite> xmlSuites = new ArrayList<>();
        List<ISuite> suites = new ArrayList<>();

        // 这里可以根据需要构造模拟的ISuite对象，当前为空列表测试生成报告的基本流程

        // 指定输出目录
        String outputDirectory = "test-output";

        // 调用生成报告方法
        generator.generateReport(xmlSuites, suites, outputDirectory);

        System.out.println("TestReportGeneratorTest: 生成报告测试完成，请检查 " + outputDirectory + " 目录");
    }
}
