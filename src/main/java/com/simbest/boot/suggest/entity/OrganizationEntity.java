package com.simbest.boot.suggest.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.simbest.boot.suggest.model.TenantAwareEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 组织实体类
 * 对应当前的Organization模型类，但添加JPA注解以支持数据库持久化
 *
 * 该实体类映射到数据库中的organization表，存储组织的基本信息，包括：
 * 1. 组织的唯一标识和名称
 * 2. 组织的层级关系（父组织ID）
 * 3. 组织的领导关系（主管领导、副领导、上级分管领导）
 * 4. 组织的类型（公司、部门、处室、团队等）
 * 5. 组织的关键词及权重，用于关键词匹配
 *
 * 组织实体是推荐系统的核心实体之一，用于构建组织结构树和领导关系网络
 */
@Entity
@Table(name = "organization")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationEntity implements TenantAwareEntity {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 组织唯一业务ID，用于业务标识
     */
    @Column(name = "org_id", nullable = false, length = 50)
    private String orgId;

    /**
     * 组织名称
     */
    @Column(name = "org_name", nullable = false, length = 100)
    private String orgName;

    /**
     * 父组织ID，用于构建组织层级关系
     */
    @Column(name = "parent_org_id", length = 50)
    private String parentOrgId;

    /**
     * 组织主管领导账号
     */
    @Column(name = "main_leader_account", length = 100)
    private String mainLeaderAccount;

    /**
     * 组织副领导账号列表
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "organization_deputy_leaders", joinColumns = @JoinColumn(name = "organization_id", nullable = false))
    @Column(name = "deputy_leader_account")
    @OrderColumn(name = "deputy_leader_order")
    private List<String> deputyLeaderAccounts = new ArrayList<>();

    /**
     * 组织上级分管领导账号（该组织直接向谁汇报）
     */
    @Column(name = "superior_leader_account", length = 100)
    private String superiorLeaderAccount;

    /**
     * 组织类型：COMPANY(公司)、DEPARTMENT(部门)、OFFICE(处室)、TEAM(团队)等
     */
    @Column(name = "org_type", length = 50)
    private String orgType;

    /**
     * 租户代码，用于多租户隔离
     */
    @Column(name = "tenant_code", length = 50)
    private String tenantCode;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    /**
     * 创建人账号
     */
    @Column(name = "created_by", length = 50)
    private String createdBy;

    /**
     * 更新人账号
     */
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    /**
     * 组织关键词列表，用于关键词匹配
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "organization_keywords", joinColumns = @JoinColumn(name = "organization_id", nullable = false))
    @Column(name = "keyword")
    @OrderColumn(name = "keyword_order")
    private List<String> keywords = new ArrayList<>();

    /**
     * 组织关键词权重列表，与关键词列表一一对应
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "organization_keywords", joinColumns = @JoinColumn(name = "organization_id", nullable = false))
    @Column(name = "keyword_weight")
    @OrderColumn(name = "keyword_order")
    private List<Double> keywordWeights = new ArrayList<>();

    /**
     * 组织关联的职责领域ID列表
     * 非持久化字段，用于运行时计算
     */
    @Transient
    private List<String> domainIds = new ArrayList<>();

    /**
     * 组织关联的职责领域权重列表，与domainIds列表一一对应
     * 非持久化字段，用于运行时计算
     */
    @Transient
    private List<Double> domainWeights = new ArrayList<>();
}
