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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 领导信息实体类
 * 对应leader_info表
 *
 * 该实体类映射到数据库中的leader_info表，存储领导的扩展信息，包括：
 * 1. 领导的账号
 * 2. 领导的真实姓名
 *
 * 领导信息实体是对领导实体的补充，提供更多的领导个人信息
 */
@Entity
@Table(name = "leader_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderInfoEntity {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 领导账号，用于唯一标识领导，与leader表的account字段对应
     */
    @Column(name = "account", nullable = false, length = 100)
    private String account;

    /**
     * 领导真实姓名
     */
    @Column(name = "true_name", nullable = false, length = 100)
    private String truename;

    /**
     * 租户代码，用于多租户隔离
     */
    @Column(name = "tenant_code", nullable = false, length = 50)
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
