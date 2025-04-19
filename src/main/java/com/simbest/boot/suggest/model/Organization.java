package com.simbest.boot.suggest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 组织模型类
 * 表示一个组织及其分管领导
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organization {
    private String orgId; // 组织ID
    private String orgName; // 组织名称
    private String parentOrgId; // 父组织ID
    private String leaderAccount; // 分管领导账号

}
