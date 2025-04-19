package com.simbest.boot.suggest.service;

import com.simbest.boot.suggest.model.Leader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // 创建领导
        Leader xuhyun = new Leader("xuhyun", "许慧云");
        xuhyun.setOrgIds(Arrays.asList("org001", "org005", "org006"));
        xuhyun.setDomainIds(Arrays.asList("domain001"));
        
        Leader zhangyk = new Leader("zhangyk", "张耀华");
        zhangyk.setOrgIds(Arrays.asList("org002", "org007", "org008", "org009"));
        zhangyk.setDomainIds(Arrays.asList("domain002"));
        
        Leader zhaobin = new Leader("zhaobin", "赵斌");
        zhaobin.setOrgIds(Arrays.asList("org003", "org004", "org010", "org011", "org012", "org013"));
        zhaobin.setDomainIds(Arrays.asList("domain003", "domain004"));
        
        // 添加领导
        addLeader(xuhyun);
        addLeader(zhangyk);
        addLeader(zhaobin);
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
