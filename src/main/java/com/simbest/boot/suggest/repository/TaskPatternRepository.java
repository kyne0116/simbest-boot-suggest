package com.simbest.boot.suggest.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.simbest.boot.suggest.model.TaskPattern;
import com.simbest.boot.suggest.model.WorkflowDirection;

/**
 * 任务模式Repository接口
 * 提供对任务模式实体的数据库操作
 */
@Repository
public interface TaskPatternRepository extends JpaRepository<TaskPattern, Long> {
    
    /**
     * 根据租户编码查找任务模式
     * 
     * @param tenantCode 租户编码
     * @return 任务模式列表
     */
    List<TaskPattern> findByTenantCode(String tenantCode);
    
    /**
     * 根据租户编码和工作流方向查找任务模式
     * 
     * @param tenantCode 租户编码
     * @param workflowDirection 工作流方向
     * @return 任务模式列表
     */
    List<TaskPattern> findByTenantCodeAndWorkflowDirection(String tenantCode, WorkflowDirection workflowDirection);
    
    /**
     * 根据租户编码和模式名称查找任务模式
     * 
     * @param tenantCode 租户编码
     * @param patternName 模式名称
     * @return 任务模式列表
     */
    List<TaskPattern> findByTenantCodeAndPatternName(String tenantCode, String patternName);
    
    /**
     * 根据租户编码和关键词查找任务模式
     * 
     * @param tenantCode 租户编码
     * @param keyword 关键词
     * @return 任务模式列表
     */
    @Query("SELECT p FROM TaskPattern p JOIN p.keywords k WHERE p.tenantCode = :tenantCode AND k = :keyword")
    List<TaskPattern> findByTenantCodeAndKeyword(@Param("tenantCode") String tenantCode, @Param("keyword") String keyword);
    
    /**
     * 根据租户编码和置信度阈值查找任务模式
     * 
     * @param tenantCode 租户编码
     * @param confidenceThreshold 置信度阈值
     * @return 任务模式列表
     */
    @Query("SELECT p FROM TaskPattern p WHERE p.tenantCode = :tenantCode AND p.confidence >= :confidenceThreshold")
    List<TaskPattern> findByTenantCodeAndConfidenceGreaterThanEqual(
            @Param("tenantCode") String tenantCode, 
            @Param("confidenceThreshold") double confidenceThreshold);
    
    /**
     * 根据租户编码和工作流方向查找任务模式，并按置信度降序排序
     * 
     * @param tenantCode 租户编码
     * @param workflowDirection 工作流方向
     * @param pageable 分页参数
     * @return 任务模式分页结果
     */
    Page<TaskPattern> findByTenantCodeAndWorkflowDirectionOrderByConfidenceDesc(
            String tenantCode, WorkflowDirection workflowDirection, Pageable pageable);
    
    /**
     * 根据租户编码查找最常用的任务模式
     * 
     * @param tenantCode 租户编码
     * @param pageable 分页参数
     * @return 任务模式分页结果
     */
    Page<TaskPattern> findByTenantCodeOrderByMatchCountDesc(String tenantCode, Pageable pageable);
}
