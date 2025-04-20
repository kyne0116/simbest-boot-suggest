package com.simbest.boot.suggest.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 组织模型类
 * 表示一个组织及其领导关系
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organization {
    private String orgId; // 组织ID
    private String orgName; // 组织名称
    private String parentOrgId; // 父组织ID
    private String mainLeaderAccount; // 主管领导账号
    private List<String> deputyLeaderAccounts = new ArrayList<>(); // 分管领导账号列表
    private String superiorLeaderAccount; // 上级领导账号（该组织直接向谁汇报）
    private String orgType; // 组织类型：DEPARTMENT(部门)、DIVISION(处室)等
}
