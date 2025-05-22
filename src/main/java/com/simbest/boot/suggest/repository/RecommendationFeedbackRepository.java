package com.simbest.boot.suggest.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.simbest.boot.suggest.model.RecommendationFeedback;
import com.simbest.boot.suggest.model.WorkflowDirection;

/**
 * 推荐反馈Repository接口
 * 提供对推荐反馈实体的数据库操作
 */
@Repository
public interface RecommendationFeedbackRepository extends JpaRepository<RecommendationFeedback, Long> {
    
    /**
     * 根据租户编码查找推荐反馈
     * 
     * @param tenantCode 租户编码
     * @return 推荐反馈列表
     */
    List<RecommendationFeedback> findByTenantCode(String tenantCode);
    
    /**
     * 根据租户编码和任务标题相似度查找推荐反馈
     * 
     * @param tenantCode 租户编码
     * @param taskTitle 任务标题关键词
     * @return 推荐反馈列表
     */
    @Query("SELECT f FROM RecommendationFeedback f WHERE f.tenantCode = :tenantCode AND f.taskTitle LIKE %:taskTitle%")
    List<RecommendationFeedback> findByTenantCodeAndTaskTitleContaining(
            @Param("tenantCode") String tenantCode, 
            @Param("taskTitle") String taskTitle);
    
    /**
     * 根据租户编码和用户账号查找推荐反馈
     * 
     * @param tenantCode 租户编码
     * @param userAccount 用户账号
     * @return 推荐反馈列表
     */
    List<RecommendationFeedback> findByTenantCodeAndUserAccount(String tenantCode, String userAccount);
    
    /**
     * 根据租户编码和推荐的领导账号查找推荐反馈
     * 
     * @param tenantCode 租户编码
     * @param recommendedLeaderAccount 推荐的领导账号
     * @return 推荐反馈列表
     */
    List<RecommendationFeedback> findByTenantCodeAndRecommendedLeaderAccount(
            String tenantCode, String recommendedLeaderAccount);
    
    /**
     * 根据租户编码和用户是否接受推荐查找推荐反馈
     * 
     * @param tenantCode 租户编码
     * @param isAccepted 是否接受推荐
     * @return 推荐反馈列表
     */
    List<RecommendationFeedback> findByTenantCodeAndIsAccepted(String tenantCode, Boolean isAccepted);
    
    /**
     * 根据租户编码和工作流方向查找推荐反馈
     * 
     * @param tenantCode 租户编码
     * @param workflowDirection 工作流方向
     * @return 推荐反馈列表
     */
    List<RecommendationFeedback> findByTenantCodeAndWorkflowDirection(
            String tenantCode, WorkflowDirection workflowDirection);
    
    /**
     * 根据租户编码和反馈时间范围查找推荐反馈
     * 
     * @param tenantCode 租户编码
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 推荐反馈列表
     */
    List<RecommendationFeedback> findByTenantCodeAndFeedbackTimeBetween(
            String tenantCode, Date startDate, Date endDate);
    
    /**
     * 根据租户编码和推荐类型查找推荐反馈
     * 
     * @param tenantCode 租户编码
     * @param recommendationType 推荐类型
     * @return 推荐反馈列表
     */
    List<RecommendationFeedback> findByTenantCodeAndRecommendationType(String tenantCode, String recommendationType);
    
    /**
     * 根据租户编码和用户反馈评分查找推荐反馈
     * 
     * @param tenantCode 租户编码
     * @param rating 用户反馈评分
     * @return 推荐反馈列表
     */
    List<RecommendationFeedback> findByTenantCodeAndRating(String tenantCode, Integer rating);
    
    /**
     * 根据租户编码查找推荐反馈，并分页
     * 
     * @param tenantCode 租户编码
     * @param pageable 分页参数
     * @return 推荐反馈分页结果
     */
    Page<RecommendationFeedback> findByTenantCode(String tenantCode, Pageable pageable);
    
    /**
     * 计算指定租户的推荐接受率
     * 
     * @param tenantCode 租户编码
     * @return 接受率（接受的反馈数 / 总反馈数）
     */
    @Query("SELECT COUNT(f) FROM RecommendationFeedback f WHERE f.tenantCode = :tenantCode AND f.isAccepted = true") 
    long countAcceptedByTenantCode(@Param("tenantCode") String tenantCode);
    
    /**
     * 计算指定租户的总反馈数
     * 
     * @param tenantCode 租户编码
     * @return 总反馈数
     */
    @Query("SELECT COUNT(f) FROM RecommendationFeedback f WHERE f.tenantCode = :tenantCode") 
    long countByTenantCode(@Param("tenantCode") String tenantCode);
    
    /**
     * 计算指定租户和推荐类型的推荐接受率
     * 
     * @param tenantCode 租户编码
     * @param recommendationType 推荐类型
     * @return 接受率（接受的反馈数 / 总反馈数）
     */
    @Query("SELECT COUNT(f) FROM RecommendationFeedback f WHERE f.tenantCode = :tenantCode AND f.recommendationType = :recommendationType AND f.isAccepted = true") 
    long countAcceptedByTenantCodeAndRecommendationType(
            @Param("tenantCode") String tenantCode, 
            @Param("recommendationType") String recommendationType);
    
    /**
     * 计算指定租户和推荐类型的总反馈数
     * 
     * @param tenantCode 租户编码
     * @param recommendationType 推荐类型
     * @return 总反馈数
     */
    @Query("SELECT COUNT(f) FROM RecommendationFeedback f WHERE f.tenantCode = :tenantCode AND f.recommendationType = :recommendationType") 
    long countByTenantCodeAndRecommendationType(
            @Param("tenantCode") String tenantCode, 
            @Param("recommendationType") String recommendationType);
    
    /**
     * 计算指定租户的平均评分
     * 
     * @param tenantCode 租户编码
     * @return 平均评分
     */
    @Query("SELECT AVG(f.rating) FROM RecommendationFeedback f WHERE f.tenantCode = :tenantCode AND f.rating IS NOT NULL") 
    Double calculateAverageRatingByTenantCode(@Param("tenantCode") String tenantCode);
}
