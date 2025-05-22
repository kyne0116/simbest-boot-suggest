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
 * 配置类别实体类
 * 用于存储系统配置的类别信息
 *
 * 该实体类映射到数据库中的config_category表，存储配置类别信息，包括：
 * 1. 配置类别的代码和名称
 * 2. 配置类别的描述
 *
 * 配置类别用于对系统配置项进行分组管理，如系统配置、显示配置、算法权重配置等
 */
@Entity
@Table(name = "config_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigCategoryEntity {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 配置类别代码，用于唯一标识配置类别
     */
    @Column(name = "category_code", nullable = false, length = 50)
    private String categoryCode;

    /**
     * 配置类别名称
     */
    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    /**
     * 配置类别描述
     */
    @Column(name = "description", length = 500)
    private String description;

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

    /**
     * 构造函数
     *
     * @param categoryCode 类别代码
     * @param categoryName 类别名称
     * @param description  类别描述
     * @param tenantCode   租户代码
     */
    public ConfigCategoryEntity(String categoryCode, String categoryName, String description, String tenantCode) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.description = description;
        this.tenantCode = tenantCode;
        this.createTime = new Date();
        this.createdBy = "system";
    }
}
