package com.simbest.boot.suggest.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 配置项实体类
 * 用于存储系统配置的具体项目
 *
 * 该实体类映射到数据库中的config_item表，存储系统的各种配置项，包括：
 * 1. 配置项所属的类别
 * 2. 配置项的键值对
 * 3. 配置项的值类型（字符串、数字、布尔值、JSON）
 *
 * 配置项用于系统的各种参数设置，如算法权重、阈值、显示选项等
 */
@Entity
@Table(name = "config_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigItemEntity {

    /**
     * 配置项值类型枚举
     */
    public enum ValueType {
        STRING, NUMBER, BOOLEAN, JSON
    }

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 配置项所属类别，多对一关系
     */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ConfigCategoryEntity category;

    /**
     * 配置项键名
     */
    @Column(name = "item_key", nullable = false, length = 100)
    private String itemKey;

    /**
     * 配置项值，存储为文本类型
     */
    @Column(name = "item_value", nullable = false, columnDefinition = "TEXT")
    private String itemValue;

    /**
     * 配置项值类型：STRING(字符串)、NUMBER(数字)、BOOLEAN(布尔值)、JSON(JSON格式)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "value_type", nullable = false, length = 20)
    private ValueType valueType;

    /**
     * 配置项描述
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
     * @param category    配置类别
     * @param itemKey     配置项键
     * @param itemValue   配置项值
     * @param valueType   值类型
     * @param description 配置项描述
     * @param tenantCode  租户代码
     */
    public ConfigItemEntity(ConfigCategoryEntity category, String itemKey, String itemValue, ValueType valueType,
            String description, String tenantCode) {
        this.category = category;
        this.itemKey = itemKey;
        this.itemValue = itemValue;
        this.valueType = valueType;
        this.description = description;
        this.tenantCode = tenantCode;
        this.createTime = new Date();
        this.createdBy = "system";
    }
}
