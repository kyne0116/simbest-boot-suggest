package com.simbest.boot.suggest.model;

/**
 * 组织模型类
 * 表示一个组织及其分管领导
 */
public class Organization {
    private String orgId;          // 组织ID
    private String orgName;        // 组织名称
    private String parentOrgId;    // 父组织ID
    private String leaderAccount;  // 分管领导账号

    /**
     * 构造函数
     * 
     * @param orgId 组织ID
     * @param orgName 组织名称
     * @param parentOrgId 父组织ID
     * @param leaderAccount 分管领导账号
     */
    public Organization(String orgId, String orgName, String parentOrgId, String leaderAccount) {
        this.orgId = orgId;
        this.orgName = orgName;
        this.parentOrgId = parentOrgId;
        this.leaderAccount = leaderAccount;
    }

    /**
     * 获取组织ID
     * 
     * @return 组织ID
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * 设置组织ID
     * 
     * @param orgId 组织ID
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取组织名称
     * 
     * @return 组织名称
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * 设置组织名称
     * 
     * @param orgName 组织名称
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    /**
     * 获取父组织ID
     * 
     * @return 父组织ID
     */
    public String getParentOrgId() {
        return parentOrgId;
    }

    /**
     * 设置父组织ID
     * 
     * @param parentOrgId 父组织ID
     */
    public void setParentOrgId(String parentOrgId) {
        this.parentOrgId = parentOrgId;
    }

    /**
     * 获取分管领导账号
     * 
     * @return 分管领导账号
     */
    public String getLeaderAccount() {
        return leaderAccount;
    }

    /**
     * 设置分管领导账号
     * 
     * @param leaderAccount 分管领导账号
     */
    public void setLeaderAccount(String leaderAccount) {
        this.leaderAccount = leaderAccount;
    }

    @Override
    public String toString() {
        return "Organization{" +
                "orgId='" + orgId + '\'' +
                ", orgName='" + orgName + '\'' +
                ", parentOrgId='" + parentOrgId + '\'' +
                ", leaderAccount='" + leaderAccount + '\'' +
                '}';
    }
}
