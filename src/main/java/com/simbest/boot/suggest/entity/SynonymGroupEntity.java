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
 * 同义词组实体类
 * 存储系统中使用的同义词组
 *
 * 该实体类映射到数据库中的synonym_group表，存储同义词组信息，包括：
 * 1. 同义词组的类别
 * 2. 同义词组的词语列表（以逗号分隔的字符串形式存储）
 *
 * 同义词组用于推荐系统的文本匹配，可以识别不同表达方式的相同概念，
 * 提高推荐的准确性和召回率
 */
@Entity
@Table(name = "synonym_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SynonymGroupEntity {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 同义词组类别，用于对同义词组进行分类
     */
    @Column(name = "category", length = 50)
    private String category;

    /**
     * 同义词组词语列表，以逗号分隔的字符串形式存储
     */
    @Column(name = "synonym_words", nullable = false, length = 500)
    private String synonymWords;

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
