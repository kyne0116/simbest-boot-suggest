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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 职责领域实体类
 * 对应当前的ResponsibilityDomain模型类，但添加JPA注解以支持数据库持久化
 *
 * 该实体类映射到数据库中的responsibility_domain表，存储职责领域的基本信息，包括：
 * 1. 职责领域的唯一标识和名称
 * 2. 职责领域的描述信息
 * 3. 职责领域的关键词列表
 *
 * 职责领域是推荐系统的核心概念之一，用于描述业务范围，
 * 系统会根据任务内容与职责领域的匹配度进行领导推荐
 * 职责领域与领导的关系通过domain_leader_mapping表维护
 */
@Entity
@Table(name = "responsibility_domain")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsibilityDomainEntity {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 职责领域唯一业务ID，用于业务标识
     */
    @Column(name = "domain_id", unique = true, nullable = false, length = 50)
    private String domainId;

    /**
     * 职责领域名称
     */
    @Column(name = "domain_name", nullable = false, length = 100)
    private String domainName;

    /**
     * 职责领域描述
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 租户代码，用于多租户隔离
     */
    @Column(name = "tenant_code", length = 50)
    private String tenantCode;

    /**
     * 职责领域关键词列表，用于关键词匹配
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "domain_keywords", joinColumns = @JoinColumn(name = "responsibility_domain_id", nullable = false))
    @Column(name = "keyword")
    @OrderColumn(name = "keyword_order")
    private List<String> keywords = new ArrayList<>();

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
}
