package com.simbest.boot.suggest.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simbest.boot.suggest.model.Leader;
import com.simbest.boot.suggest.model.ResponsibilityDomain;
import com.simbest.boot.suggest.util.DataLoader;

/**
 * 领导服务
 * 提供领导相关的操作
 */
public class LeaderService {
    private Map<String, Leader> leaders; // 领导账号到领导的映射
    private DomainService domainService; // 职责领域服务

    /**
     * 构造函数
     */
    public LeaderService() {
        this.leaders = new HashMap<>();
        initLeaders();
    }

    /**
     * 设置职责领域服务
     *
     * @param domainService 职责领域服务
     */
    public void setDomainService(DomainService domainService) {
        this.domainService = domainService;
    }

    /**
     * 初始化领导数据
     */
    public void initLeaders() {
        // 从资源文件加载领导数据
        List<Leader> leaderList = DataLoader.loadLeaders();
        for (Leader leader : leaderList) {
            addLeader(leader);
        }

        System.out.println("已初始化 " + leaders.size() + " 个领导数据");
    }

    /**
     * 添加领导
     *
     * @param leader 领导对象
     */
    public void addLeader(Leader leader) {
        leaders.put(leader.getAccount(), leader);
    }

    /**
     * 根据账号获取领导
     *
     * @param account 领导账号
     * @return 领导对象
     */
    public Leader getLeaderByAccount(String account) {
        return leaders.get(account);
    }

    /**
     * 获取所有领导
     *
     * @return 所有领导的列表
     */
    public List<Leader> getAllLeaders() {
        return new ArrayList<>(leaders.values());
    }

    /**
     * 获取所有领导账号
     *
     * @return 所有领导账号的列表
     */
    public List<String> getAllLeaderAccounts() {
        return new ArrayList<>(leaders.keySet());
    }

    /**
     * 计算领导的职责领域与任务标题的匹配度
     *
     * @param leader    领导对象
     * @param taskTitle 任务标题
     * @return 匹配度分数
     */
    public double calculateDomainMatchScore(Leader leader, String taskTitle) {
        if (leader == null || taskTitle == null || taskTitle.isEmpty() ||
                leader.getDomainIds() == null || leader.getDomainIds().isEmpty() ||
                domainService == null) {
            return 0.0;
        }

        double totalScore = 0.0;
        int count = 0;

        for (String domainId : leader.getDomainIds()) {
            ResponsibilityDomain domain = domainService.getDomainById(domainId);
            if (domain != null) {
                double score = domain.calculateMatchScore(taskTitle);
                totalScore += score;
                count++;
            }
        }

        // 计算平均匹配度
        return count > 0 ? totalScore / count : 0.0;
    }
}
