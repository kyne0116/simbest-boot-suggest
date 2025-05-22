package com.simbest.boot.suggest.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.simbest.boot.suggest.model.TenantAwareEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 领域到领导映射实体类
 * 存储领域名称与领导账号的映射关系
 *
 * 该实体类映射到数据库中的domain_leader_mapping表，用于建立职责领域与领导之间的多对多关系，包括：
 * 1. 职责领域名称
 * 2. 负责该领域的领导账号
 *
 * 该映射关系用于推荐系统根据任务内容匹配相关领域后，找到负责该领域的领导
 */
@Entity
@Table(name = "domain_leader_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DomainLeaderMappingEntity implements TenantAwareEntity {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 职责领域名称
     */
    @Column(name = "domain_name", nullable = false, length = 100)
    private String domainName;

    /**
     * 领导账号
     */
    @Column(name = "leader_account", nullable = false, length = 100)
    private String leaderAccount;

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
