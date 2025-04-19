package com.simbest.boot.suggest.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 领导模型类
 * 表示一个领导及其分管组织和职责领域
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Leader {
    private String account; // 领导账号
    private String name; // 领导姓名
    private List<String> orgIds = new ArrayList<>(); // 分管组织ID列表
    private List<String> domainIds = new ArrayList<>(); // 负责的职责领域ID列表

    /**
     * 构造函数
     *
     * @param account 领导账号
     * @param name    领导姓名
     */
    public Leader(String account, String name) {
        this.account = account;
        this.name = name;
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
     * 添加负责的职责领域ID
     *
     * @param domainId 职责领域ID
     */
    public void addDomainId(String domainId) {
        if (!domainIds.contains(domainId)) {
            domainIds.add(domainId);
        }
    }
}
