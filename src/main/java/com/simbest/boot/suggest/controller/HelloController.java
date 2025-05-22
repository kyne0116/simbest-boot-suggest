package com.simbest.boot.suggest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.simbest.boot.suggest.config.AppConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * Hello控制器
 * 提供基本的系统信息和健康检查端点
 */
@Slf4j
@RestController
@RequestMapping("/hello")
public class HelloController {

    private final String applicationName;
    private final String systemName;
    private final String systemVersion;

    @Autowired
    public HelloController(Environment environment, AppConfig appConfig) {
        this.applicationName = environment.getProperty("spring.application.name", "suggest");
        this.systemName = appConfig.getName();
        this.systemVersion = appConfig.getVersion();
    }

    /**
     * 健康检查端点
     *
     * @return 系统状态信息
     */
    @GetMapping(value = "/hi")
    public String hi() {
        log.info("健康检查请求");
        return "系统正常运行中 - " + applicationName;
    }

    /**
     * 测试端点
     *
     * @return 测试信息
     */
    @GetMapping(value = "/test")
    public String test() {
        log.info("测试请求");
        return "系统测试端点 - " + systemName;
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    @GetMapping(value = "/time")
    public String time() {
        log.info("时间请求");
        return "当前时间：" + new java.util.Date();
    }

    /**
     * 回显消息
     *
     * @param message 要回显的消息
     * @return 回显的消息
     */
    @GetMapping(value = "/echo")
    public String echo(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        log.info("回显请求，参数：{}", message);
        return "Echo: " + message;
    }

    /**
     * 获取系统信息
     *
     * @return 系统信息
     */
    @GetMapping(value = "/info")
    public String info() {
        log.info("系统信息请求");
        return String.format("系统名称：%s，版本：%s，运行环境：%s %s",
                systemName,
                systemVersion,
                System.getProperty("os.name"),
                System.getProperty("os.version"));
    }
}
