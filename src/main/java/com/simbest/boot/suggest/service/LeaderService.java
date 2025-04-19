package com.simbest.boot.suggest.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simbest.boot.suggest.model.Leader;
import com.simbest.boot.suggest.util.DataLoader;

/**
 * 领导服务
 * 提供领导相关的操作
 */
public class LeaderService {
    private Map<String, Leader> leaders; // 领导账号到领导的映射

    /**
     * 构造函数
     */
    public LeaderService() {
        this.leaders = new HashMap<>();
        initLeaders();
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
}
