package com.simbest.boot.suggest.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 租户实体类
 * 表示系统中的一个租户，用于多租户隔离
 *
 * 该实体类映射到数据库中的tenant表，存储租户信息，包括：
 * 1. 租户的编码和名称
 * 2. 租户的状态（启用/禁用）
 *
 * 租户是系统的基础概念，用于实现多租户架构，
 * 系统中的所有业务数据都通过租户代码进行隔离
 */
@Entity
@Table(name = "tenant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant implements TenantAwareEntity {

    /**
     * 租户ID，主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 租户编码，唯一标识
     */
    @Column(name = "tenant_code", unique = true, nullable = false, length = 50)
    private String tenantCode;

    /**
     * 租户名称
     */
    @Column(name = "tenant_name", nullable = false, length = 100)
    private String tenantName;

    /**
     * 状态：0-禁用，1-启用
     */
    @Column(name = "status", nullable = false)
    private Integer status = 1;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    /**
     * 创建人
     */
    @Column(name = "created_by", length = 50)
    private String createdBy;

    /**
     * 更新人
     */
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    /**
     * 判断租户是否激活
     *
     * @return 是否激活
     */
    public boolean isActive() {
        return status != null && status == 1;
    }

    /**
     * 设置租户激活状态
     *
     * @param active 是否激活
     */
    public void setActive(boolean active) {
        this.status = active ? 1 : 0;
    }
}
