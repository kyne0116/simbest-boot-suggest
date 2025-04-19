package com.simbest.boot.suggest.controller;

import com.simbest.boot.suggest.model.Leader;
import com.simbest.boot.suggest.model.Organization;
import com.simbest.boot.suggest.model.RecommendationResult;
import com.simbest.boot.suggest.service.LeaderService;
import com.simbest.boot.suggest.service.OrganizationService;
import com.simbest.boot.suggest.service.RecommendationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 领导推荐控制器
 * 提供领导推荐相关的REST API
 */
@RestController
@RequestMapping("/api/recommendation")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;
    
    @Autowired
    private OrganizationService organizationService;
    
    @Autowired
    private LeaderService leaderService;

    /**
     * 推荐领导
     * 
     * @param userAccount 当前办理人账号
     * @param orgId 当前办理人组织ID
     * @param taskTitle 任务标题
     * @return 推荐结果
     */
    @GetMapping("/recommend")
    public RecommendationResult recommendLeader(
            @RequestParam String userAccount,
            @RequestParam String orgId,
            @RequestParam String taskTitle) {
        return recommendationService.recommendLeader(userAccount, orgId, taskTitle);
    }
    
    /**
     * 获取所有可能的推荐结果
     * 
     * @param userAccount 当前办理人账号
     * @param orgId 当前办理人组织ID
     * @param taskTitle 任务标题
     * @return 所有可能的推荐结果列表
     */
    @GetMapping("/recommend/all")
    public List<RecommendationResult> getAllRecommendations(
            @RequestParam String userAccount,
            @RequestParam String orgId,
            @RequestParam String taskTitle) {
        return recommendationService.getAllPossibleRecommendations(userAccount, orgId, taskTitle);
    }
    
    /**
     * 获取所有组织
     * 
     * @return 所有组织的列表
     */
    @GetMapping("/organizations")
    public List<Organization> getAllOrganizations() {
        return organizationService.getAllOrganizations();
    }
    
    /**
     * 获取所有领导
     * 
     * @return 所有领导的列表
     */
    @GetMapping("/leaders")
    public List<Leader> getAllLeaders() {
        return leaderService.getAllLeaders();
    }
    
    /**
     * 获取系统信息
     * 
     * @return 系统信息
     */
    @GetMapping("/info")
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "领导推荐系统");
        info.put("version", "1.0.0");
        info.put("organizationCount", organizationService.getAllOrganizations().size());
        info.put("leaderCount", leaderService.getAllLeaders().size());
        return info;
    }
}
