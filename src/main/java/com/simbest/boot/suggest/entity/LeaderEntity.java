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

import com.simbest.boot.suggest.model.TenantAwareEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 领导实体类
 * 对应当前的Leader模型类，但添加JPA注解以支持数据库持久化
 *
 * 该实体类映射到数据库中的leader表，存储领导的基本信息，包括：
 * 1. 领导的账号和姓名
 * 2. 领导负责的职责领域
 *
 * 领导实体是推荐系统的核心实体之一，用于构建组织的领导关系网络
 */
@Entity
@Table(name = "leader")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderEntity implements TenantAwareEntity {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 领导账号，用于唯一标识领导
     */
    @Column(name = "account", nullable = false, length = 100)
    private String account;

    /**
     * 领导姓名
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 领导负责的职责领域ID列表
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "leader_domains", joinColumns = @JoinColumn(name = "leader_id", nullable = false))
    @Column(name = "domain_id")
    @OrderColumn(name = "domain_order")
    private List<String> domainIds = new ArrayList<>();

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
}
