package com.simbest.boot.suggest.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 领导模型类
 * 表示一个领导及其分管组织和职责领域
 */
public class Leader {
    private String account;        // 领导账号
    private String name;           // 领导姓名
    private List<String> orgIds;   // 分管组织ID列表
    private List<String> domainIds; // 负责的职责领域ID列表

    /**
     * 构造函数
     * 
     * @param account 领导账号
     * @param name 领导姓名
     */
    public Leader(String account, String name) {
        this.account = account;
        this.name = name;
        this.orgIds = new ArrayList<>();
        this.domainIds = new ArrayList<>();
    }

    /**
     * 构造函数
     * 
     * @param account 领导账号
     * @param name 领导姓名
     * @param orgIds 分管组织ID列表
     * @param domainIds 负责的职责领域ID列表
     */
    public Leader(String account, String name, List<String> orgIds, List<String> domainIds) {
        this.account = account;
        this.name = name;
        this.orgIds = orgIds;
        this.domainIds = domainIds;
    }

    /**
     * 获取领导账号
     * 
     * @return 领导账号
     */
    public String getAccount() {
        return account;
    }

    /**
     * 设置领导账号
     * 
     * @param account 领导账号
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * 获取领导姓名
     * 
     * @return 领导姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置领导姓名
     * 
     * @param name 领导姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取分管组织ID列表
     * 
     * @return 组织ID列表
     */
    public List<String> getOrgIds() {
        return orgIds;
    }

    /**
     * 设置分管组织ID列表
     * 
     * @param orgIds 组织ID列表
     */
    public void setOrgIds(List<String> orgIds) {
        this.orgIds = orgIds;
    }

    /**
     * 添加分管组织ID
     * 
     * @param orgId 组织ID
     */
    public void addOrgId(String orgId) {
        if (!orgIds.contains(orgId)) {
            orgIds.add(orgId);
        }
    }

    /**
     * 获取负责的职责领域ID列表
     * 
     * @return 职责领域ID列表
     */
    public List<String> getDomainIds() {
        return domainIds;
    }

    /**
     * 设置负责的职责领域ID列表
     * 
     * @param domainIds 职责领域ID列表
     */
    public void setDomainIds(List<String> domainIds) {
        this.domainIds = domainIds;
    }

    /**
     * 添加负责的职责领域ID
     * 
     * @param domainId 职责领域ID
     */
    public void addDomainId(String domainId) {
        if (!domainIds.contains(domainId)) {
            domainIds.add(domainId);
        }
    }

    @Override
    public String toString() {
        return "Leader{" +
                "account='" + account + '\'' +
                ", name='" + name + '\'' +
                ", orgIds=" + orgIds +
                ", domainIds=" + domainIds +
                '}';
    }
}
