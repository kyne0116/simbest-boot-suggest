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
 * 常用词实体类
 * 存储系统中使用的常用词汇
 *
 * 该实体类映射到数据库中的common_word表，存储常用词信息，包括：
 * 1. 常用词的词语
 * 2. 常用词的类别
 *
 * 常用词用于推荐系统的文本分析和关键词提取，
 * 帮助系统识别任务中的重要概念和术语
 */
@Entity
@Table(name = "common_word")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonWordEntity {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 常用词词语
     */
    @Column(name = "word", nullable = false, length = 100)
    private String word;

    /**
     * 常用词类别，用于对常用词进行分类
     */
    @Column(name = "category", length = 50)
    private String category;

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
