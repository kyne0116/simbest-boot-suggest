package com.simbest.boot.suggest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.simbest.boot.suggest.service.OrganizationService;
import com.simbest.boot.suggest.util.OrganizationDataInitializer;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据初始化控制器
 * 提供数据初始化相关的API
 */
@Slf4j
@RestController
@RequestMapping("/api/init")
public class DataInitController {

    @Autowired
    private OrganizationDataInitializer organizationDataInitializer;
    
    @Autowired
    private OrganizationService organizationService;

    /**
     * 初始化组织人员数据
     * 
     * @return 初始化结果
     */
    @GetMapping("/organization")
    public String initOrganization() {
        log.info("开始初始化组织人员数据...");
        try {
            // 执行组织人员数据初始化
            organizationDataInitializer.initializeOrganizationData();
            
            // 重新加载组织数据到内存
            organizationService.initOrganizations();
            
            return "组织人员数据初始化成功";
        } catch (Exception e) {
            log.error("组织人员数据初始化失败", e);
            return "组织人员数据初始化失败: " + e.getMessage();
        }
    }
}
