package com.simbest.boot.suggest.test;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * 简单测试类
 * 用于验证TestNG测试框架是否可用
 */
public class SimpleTest {
    
    @Test
    public void testFramework() {
        System.out.println("执行简单测试...");
        Assert.assertTrue(true, "测试框架可用");
    }
}
