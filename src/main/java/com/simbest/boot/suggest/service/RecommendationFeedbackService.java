package com.simbest.boot.suggest.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simbest.boot.suggest.model.RecommendationFeedback;
import com.simbest.boot.suggest.model.WorkflowDirection;
import com.simbest.boot.suggest.repository.RecommendationFeedbackRepository;
import com.simbest.boot.suggest.util.TenantValidator;

import lombok.extern.slf4j.Slf4j;

/**
 * 推荐反馈服务类
 * 提供推荐反馈相关的业务逻辑
 */
@Service
@Slf4j
public class RecommendationFeedbackService {

    @Autowired
    private RecommendationFeedbackRepository feedbackRepository;

    @Autowired
    private TenantValidator tenantValidator;

    /**
     * 保存推荐反馈
     *
     * @param feedback 推荐反馈对象
     * @return 保存后的推荐反馈对象
     */
    @Transactional
    public RecommendationFeedback saveFeedback(RecommendationFeedback feedback) {
        // 验证租户
        tenantValidator.validateTenant(feedback.getTenantCode());

        // 设置创建时间和更新时间
        if (feedback.getCreateTime() == null) {
            feedback.setCreateTime(new Date());
        }
        feedback.setUpdateTime(new Date());

        // 如果是更新反馈，设置反馈时间
        if (feedback.getId() != null && feedback.getFeedbackTime() == null) {
            feedback.setFeedbackTime(new Date());
        }

        RecommendationFeedback savedFeedback = feedbackRepository.save(feedback);
        log.info("保存推荐反馈成功: {}", savedFeedback.getId());
        return savedFeedback;
    }

    /**
     * 批量保存推荐反馈
     *
     * @param feedbacks 推荐反馈列表
     * @return 保存后的推荐反馈列表
     */
    @Transactional
    public List<RecommendationFeedback> saveAllFeedbacks(List<RecommendationFeedback> feedbacks) {
        // 设置创建时间和更新时间
        Date now = new Date();
        for (RecommendationFeedback feedback : feedbacks) {
            // 验证租户
            tenantValidator.validateTenant(feedback.getTenantCode());

            if (feedback.getCreateTime() == null) {
                feedback.setCreateTime(now);
            }
            feedback.setUpdateTime(now);

            // 如果是更新反馈，设置反馈时间
            if (feedback.getId() != null && feedback.getFeedbackTime() == null) {
                feedback.setFeedbackTime(now);
            }
        }

        List<RecommendationFeedback> savedFeedbacks = feedbackRepository.saveAll(feedbacks);
        log.info("批量保存推荐反馈成功: {} 条记录", savedFeedbacks.size());
        return savedFeedbacks;
    }

    /**
     * 更新推荐反馈
     *
     * @param feedback 推荐反馈对象
     * @return 更新后的推荐反馈对象
     */
    @Transactional
    public RecommendationFeedback updateFeedback(RecommendationFeedback feedback) {
        // 验证租户
        tenantValidator.validateTenant(feedback.getTenantCode());

        // 检查反馈是否存在
        if (!feedbackRepository.existsById(feedback.getId())) {
            throw new IllegalArgumentException("推荐反馈不存在: " + feedback.getId());
        }

        // 设置更新时间和反馈时间
        feedback.setUpdateTime(new Date());
        if (feedback.getFeedbackTime() == null) {
            feedback.setFeedbackTime(new Date());
        }

        RecommendationFeedback updatedFeedback = feedbackRepository.save(feedback);
        log.info("更新推荐反馈成功: {}", updatedFeedback.getId());
        return updatedFeedback;
    }

    /**
     * 根据ID获取推荐反馈
     *
     * @param id 推荐反馈ID
     * @return 推荐反馈对象
     */
    public RecommendationFeedback getFeedbackById(Long id) {
        return feedbackRepository.findById(id).orElse(null);
    }

    /**
     * 根据租户编码获取所有推荐反馈
     *
     * @param tenantCode 租户编码
     * @return 推荐反馈列表
     */
    public List<RecommendationFeedback> getAllFeedbacks(String tenantCode) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return feedbackRepository.findByTenantCode(tenantCode);
    }

    /**
     * 根据租户编码获取所有推荐反馈，并分页
     *
     * @param tenantCode 租户编码
     * @param pageable   分页参数
     * @return 推荐反馈分页结果
     */
    public Page<RecommendationFeedback> getAllFeedbacks(String tenantCode, Pageable pageable) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return feedbackRepository.findByTenantCode(tenantCode, pageable);
    }

    /**
     * 根据租户编码和任务标题相似度查找推荐反馈
     *
     * @param tenantCode 租户编码
     * @param taskTitle  任务标题关键词
     * @return 推荐反馈列表
     */
    public List<RecommendationFeedback> getFeedbacksByTaskTitle(String tenantCode, String taskTitle) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return feedbackRepository.findByTenantCodeAndTaskTitleContaining(tenantCode, taskTitle);
    }

    /**
     * 根据租户编码和用户账号查找推荐反馈
     *
     * @param tenantCode  租户编码
     * @param userAccount 用户账号
     * @return 推荐反馈列表
     */
    public List<RecommendationFeedback> getFeedbacksByUserAccount(String tenantCode, String userAccount) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return feedbackRepository.findByTenantCodeAndUserAccount(tenantCode, userAccount);
    }

    /**
     * 根据租户编码和推荐的领导账号查找推荐反馈
     *
     * @param tenantCode               租户编码
     * @param recommendedLeaderAccount 推荐的领导账号
     * @return 推荐反馈列表
     */
    public List<RecommendationFeedback> getFeedbacksByRecommendedLeader(String tenantCode,
            String recommendedLeaderAccount) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return feedbackRepository.findByTenantCodeAndRecommendedLeaderAccount(tenantCode, recommendedLeaderAccount);
    }

    /**
     * 根据租户编码和用户是否接受推荐查找推荐反馈
     *
     * @param tenantCode 租户编码
     * @param isAccepted 是否接受推荐
     * @return 推荐反馈列表
     */
    public List<RecommendationFeedback> getFeedbacksByAcceptance(String tenantCode, Boolean isAccepted) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return feedbackRepository.findByTenantCodeAndIsAccepted(tenantCode, isAccepted);
    }

    /**
     * 根据租户编码和工作流方向查找推荐反馈
     *
     * @param tenantCode        租户编码
     * @param workflowDirection 工作流方向
     * @return 推荐反馈列表
     */
    public List<RecommendationFeedback> getFeedbacksByWorkflowDirection(String tenantCode,
            WorkflowDirection workflowDirection) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return feedbackRepository.findByTenantCodeAndWorkflowDirection(tenantCode, workflowDirection);
    }

    /**
     * 根据租户编码和反馈时间范围查找推荐反馈
     *
     * @param tenantCode 租户编码
     * @param startDate  开始时间
     * @param endDate    结束时间
     * @return 推荐反馈列表
     */
    public List<RecommendationFeedback> getFeedbacksByTimeRange(String tenantCode, Date startDate, Date endDate) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return feedbackRepository.findByTenantCodeAndFeedbackTimeBetween(tenantCode, startDate, endDate);
    }

    /**
     * 根据租户编码和推荐类型查找推荐反馈
     *
     * @param tenantCode         租户编码
     * @param recommendationType 推荐类型
     * @return 推荐反馈列表
     */
    public List<RecommendationFeedback> getFeedbacksByRecommendationType(String tenantCode, String recommendationType) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return feedbackRepository.findByTenantCodeAndRecommendationType(tenantCode, recommendationType);
    }

    /**
     * 根据租户编码和用户反馈评分查找推荐反馈
     *
     * @param tenantCode 租户编码
     * @param rating     用户反馈评分
     * @return 推荐反馈列表
     */
    public List<RecommendationFeedback> getFeedbacksByRating(String tenantCode, Integer rating) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return feedbackRepository.findByTenantCodeAndRating(tenantCode, rating);
    }

    /**
     * 计算指定租户的推荐接受率
     *
     * @param tenantCode 租户编码
     * @return 接受率（接受的反馈数 / 总反馈数）
     */
    public double calculateAcceptanceRate(String tenantCode) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        long acceptedCount = feedbackRepository.countAcceptedByTenantCode(tenantCode);
        long totalCount = feedbackRepository.countByTenantCode(tenantCode);

        return totalCount > 0 ? (double) acceptedCount / totalCount : 0.0;
    }

    /**
     * 计算指定租户和推荐类型的推荐接受率
     *
     * @param tenantCode         租户编码
     * @param recommendationType 推荐类型
     * @return 接受率（接受的反馈数 / 总反馈数）
     */
    public double calculateAcceptanceRateByType(String tenantCode, String recommendationType) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        long acceptedCount = feedbackRepository.countAcceptedByTenantCodeAndRecommendationType(tenantCode,
                recommendationType);
        long totalCount = feedbackRepository.countByTenantCodeAndRecommendationType(tenantCode, recommendationType);

        return totalCount > 0 ? (double) acceptedCount / totalCount : 0.0;
    }

    /**
     * 计算指定租户的平均评分
     *
     * @param tenantCode 租户编码
     * @return 平均评分
     */
    public double calculateAverageRating(String tenantCode) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        Double avgRating = feedbackRepository.calculateAverageRatingByTenantCode(tenantCode);
        return avgRating != null ? avgRating : 0.0;
    }

    /**
     * 删除推荐反馈
     *
     * @param id 推荐反馈ID
     */
    @Transactional
    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
        log.info("删除推荐反馈成功: {}", id);
    }

    /**
     * 删除租户的所有推荐反馈
     *
     * @param tenantCode 租户编码
     */
    @Transactional
    public void deleteAllFeedbacks(String tenantCode) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        List<RecommendationFeedback> feedbacks = feedbackRepository.findByTenantCode(tenantCode);
        feedbackRepository.deleteAll(feedbacks);
        log.info("删除租户 {} 的所有推荐反馈成功: {} 条记录", tenantCode, feedbacks.size());
    }

    /**
     * 清空指定租户的所有推荐反馈
     *
     * @param tenantCode 租户编码
     * @return 删除的记录数量
     */
    @Transactional
    public long clearFeedbacks(String tenantCode) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        List<RecommendationFeedback> feedbacks = feedbackRepository.findByTenantCode(tenantCode);
        long count = feedbacks.size();

        feedbackRepository.deleteAll(feedbacks);
        log.info("清空租户 {} 的推荐反馈成功: {} 条记录", tenantCode, count);

        return count;
    }

    /**
     * 获取指定租户的推荐统计信息
     *
     * @param tenantCode 租户编码
     * @return 统计信息
     */
    public Map<String, Object> getRecommendationStats(String tenantCode) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        Map<String, Object> stats = new HashMap<>();

        // 总反馈数
        long totalCount = feedbackRepository.countByTenantCode(tenantCode);
        stats.put("totalFeedbacks", totalCount);

        // 接受的反馈数
        long acceptedCount = feedbackRepository.countAcceptedByTenantCode(tenantCode);
        stats.put("acceptedFeedbacks", acceptedCount);

        // 接受率
        double acceptanceRate = totalCount > 0 ? (double) acceptedCount / totalCount : 0.0;
        stats.put("acceptanceRate", acceptanceRate);

        // 平均评分
        Double avgRating = feedbackRepository.calculateAverageRatingByTenantCode(tenantCode);
        stats.put("averageRating", avgRating != null ? avgRating : 0.0);

        return stats;
    }
}
