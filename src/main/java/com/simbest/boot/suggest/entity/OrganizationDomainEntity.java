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
 * 组织领域关联实体类
 * 存储组织与职责领域的映射关系
 *
 * 该实体类映射到数据库中的organization_domains表，用于建立组织与职责领域之间的多对多关系，包括：
 * 1. 组织ID
 * 2. 职责领域ID
 * 3. 该组织对该领域的权重
 *
 * 该映射关系用于推荐系统根据任务内容匹配相关领域后，找到负责该领域的组织
 */
@Entity
@Table(name = "organization_domains")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDomainEntity implements TenantAwareEntity {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 组织ID
     */
    @Column(name = "organization_id", nullable = false, length = 50)
    private String organizationId;
    
    /**
     * 职责领域ID
     */
    @Column(name = "domain_id", nullable = false, length = 50)
    private String domainId;
    
    /**
     * 该组织对该领域的权重
     */
    @Column(name = "domain_weight")
    private Double domainWeight = 1.0;
    
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
