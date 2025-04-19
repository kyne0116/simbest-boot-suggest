package com.simbest.boot.suggest.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simbest.boot.suggest.model.Organization;
import com.simbest.boot.suggest.util.DataLoader;

/**
 * 组织管理服务
 * 提供组织相关的操作
 */
public class OrganizationService {
    private Map<String, Organization> organizations; // 组织ID到组织的映射
    private Map<String, List<String>> leaderOrgMap; // 领导账号到分管组织ID的映射

    /**
     * 构造函数
     */
    public OrganizationService() {
        this.organizations = new HashMap<>();
        this.leaderOrgMap = new HashMap<>();
        initOrganizations();
    }

    /**
     * 初始化组织数据
     */
    public void initOrganizations() {
        // 从资源文件加载组织数据
        List<Organization> orgList = DataLoader.loadOrganizations();
        for (Organization org : orgList) {
            addOrganization(org);
        }

        System.out.println("已初始化 " + organizations.size() + " 个组织数据");
    }

    /**
     * 添加组织
     *
     * @param organization 组织对象
     */
    public void addOrganization(Organization organization) {
        organizations.put(organization.getOrgId(), organization);

        // 更新领导到组织的映射
        String leaderAccount = organization.getLeaderAccount();
        if (leaderAccount != null && !leaderAccount.isEmpty()) {
            leaderOrgMap.computeIfAbsent(leaderAccount, k -> new ArrayList<>()).add(organization.getOrgId());
        }
    }

    /**
     * 根据组织ID获取组织
     *
     * @param orgId 组织ID
     * @return 组织对象
     */
    public Organization getOrganizationById(String orgId) {
        return organizations.get(orgId);
    }

    /**
     * 根据组织ID获取分管领导账号
     *
     * @param orgId 组织ID
     * @return 分管领导账号
     */
    public String getLeaderAccountByOrgId(String orgId) {
        Organization org = organizations.get(orgId);
        return org != null ? org.getLeaderAccount() : null;
    }

    /**
     * 根据领导账号获取分管组织ID列表
     *
     * @param leaderAccount 领导账号
     * @return 分管组织ID列表
     */
    public List<String> getOrgIdsByLeaderAccount(String leaderAccount) {
        return leaderOrgMap.getOrDefault(leaderAccount, new ArrayList<>());
    }

    /**
     * 获取所有组织
     *
     * @return 所有组织的列表
     */
    public List<Organization> getAllOrganizations() {
        return new ArrayList<>(organizations.values());
    }

    /**
     * 获取所有领导账号
     *
     * @return 所有领导账号的列表
     */
    public List<String> getAllLeaderAccounts() {
        return new ArrayList<>(leaderOrgMap.keySet());
    }
}
