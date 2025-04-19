package com.simbest.boot.suggest.service;

import com.simbest.boot.suggest.model.Organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组织管理服务
 * 提供组织相关的操作
 */
public class OrganizationService {
    private Map<String, Organization> organizations; // 组织ID到组织的映射
    private Map<String, List<String>> leaderOrgMap;  // 领导账号到分管组织ID的映射

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
        // 创建组织
        addOrganization(new Organization("org001", "信息安全部", "org000", "xuhyun"));
        addOrganization(new Organization("org002", "计费账务部", "org000", "zhangyk"));
        addOrganization(new Organization("org003", "系统管理部", "org000", "zhaobin"));
        addOrganization(new Organization("org004", "数据治理部", "org000", "zhaobin"));
        addOrganization(new Organization("org005", "网络安全部", "org001", "xuhyun"));
        addOrganization(new Organization("org006", "信息安全室", "org001", "xuhyun"));
        addOrganization(new Organization("org007", "计费系统部", "org002", "zhangyk"));
        addOrganization(new Organization("org008", "账务结算部", "org002", "zhangyk"));
        addOrganization(new Organization("org009", "短信营销部", "org002", "zhangyk"));
        addOrganization(new Organization("org010", "系统建设部", "org003", "zhaobin"));
        addOrganization(new Organization("org011", "系统运维部", "org003", "zhaobin"));
        addOrganization(new Organization("org012", "数据分析部", "org004", "zhaobin"));
        addOrganization(new Organization("org013", "AI研发部", "org004", "zhaobin"));
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
