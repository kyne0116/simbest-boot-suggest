package com.simbest.boot.suggest.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.simbest.boot.suggest.model.JsonResponse;
import com.simbest.boot.suggest.model.RecommendationFeedback;
import com.simbest.boot.suggest.model.WorkflowDirection;
import com.simbest.boot.suggest.service.RecommendationFeedbackService;

import lombok.extern.slf4j.Slf4j;

/**
 * 反馈控制器
 * 提供推荐反馈相关的API接口
 */
@RestController
@RequestMapping("/feedback")
@Slf4j
public class FeedbackController {

    @Autowired
    private RecommendationFeedbackService feedbackService;

    /**
     * 保存推荐反馈
     *
     * @param feedback 推荐反馈对象
     * @return 保存结果
     */
    @PostMapping
    public JsonResponse<RecommendationFeedback> saveFeedback(@RequestBody RecommendationFeedback feedback) {
        try {
            RecommendationFeedback savedFeedback = feedbackService.saveFeedback(feedback);
            return JsonResponse.success(savedFeedback, "保存推荐反馈成功");
        } catch (Exception e) {
            log.error("保存推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("保存推荐反馈失败: " + e.getMessage());
        }
    }

    /**
     * 批量保存推荐反馈
     *
     * @param feedbacks 推荐反馈列表
     * @return 保存结果
     */
    @PostMapping("/batch")
    public JsonResponse<List<RecommendationFeedback>> saveAllFeedbacks(
            @RequestBody List<RecommendationFeedback> feedbacks) {
        try {
            List<RecommendationFeedback> savedFeedbacks = feedbackService.saveAllFeedbacks(feedbacks);
            return JsonResponse.success(savedFeedbacks, "批量保存推荐反馈成功");
        } catch (Exception e) {
            log.error("批量保存推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("批量保存推荐反馈失败: " + e.getMessage());
        }
    }

    /**
     * 更新推荐反馈
     *
     * @param feedback 推荐反馈对象
     * @return 更新结果
     */
    @PutMapping
    public JsonResponse<RecommendationFeedback> updateFeedback(@RequestBody RecommendationFeedback feedback) {
        try {
            RecommendationFeedback updatedFeedback = feedbackService.updateFeedback(feedback);
            return JsonResponse.success(updatedFeedback, "更新推荐反馈成功");
        } catch (Exception e) {
            log.error("更新推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("更新推荐反馈失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取推荐反馈
     *
     * @param id 推荐反馈ID
     * @return 推荐反馈对象
     */
    @GetMapping("/{id}")
    public JsonResponse<RecommendationFeedback> getFeedbackById(@PathVariable Long id) {
        try {
            RecommendationFeedback feedback = feedbackService.getFeedbackById(id);
            if (feedback != null) {
                return JsonResponse.success(feedback, "获取推荐反馈成功");
            } else {
                return JsonResponse.fail("推荐反馈不存在: " + id);
            }
        } catch (Exception e) {
            log.error("获取推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取推荐反馈失败: " + e.getMessage());
        }
    }

    /**
     * 根据租户编码获取推荐反馈
     *
     * @param tenantCode 租户编码
     * @return 推荐反馈列表
     */
    @GetMapping("/tenant/{tenantCode}")
    public JsonResponse<List<RecommendationFeedback>> getFeedbacksByTenantCode(@PathVariable String tenantCode) {
        try {
            List<RecommendationFeedback> feedbacks = feedbackService.getAllFeedbacks(tenantCode);
            return JsonResponse.success(feedbacks, "获取推荐反馈成功");
        } catch (Exception e) {
            log.error("获取推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取推荐反馈失败: " + e.getMessage());
        }
    }

    /**
     * 根据租户编码获取推荐反馈，并分页
     *
     * @param tenantCode 租户编码
     * @param page       页码
     * @param size       每页大小
     * @return 推荐反馈分页结果
     */
    @GetMapping("/tenant/{tenantCode}/page")
    public JsonResponse<Page<RecommendationFeedback>> getFeedbacksByTenantCodeWithPaging(
            @PathVariable String tenantCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<RecommendationFeedback> feedbacks = feedbackService.getAllFeedbacks(tenantCode, pageable);
            return JsonResponse.success(feedbacks, "获取推荐反馈成功");
        } catch (Exception e) {
            log.error("获取推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取推荐反馈失败: " + e.getMessage());
        }
    }

    /**
     * 根据租户编码和任务标题相似度查找推荐反馈
     *
     * @param tenantCode 租户编码
     * @param taskTitle  任务标题关键词
     * @return 推荐反馈列表
     */
    @GetMapping("/search")
    public JsonResponse<List<RecommendationFeedback>> searchFeedbacks(
            @RequestParam String tenantCode,
            @RequestParam String taskTitle) {
        try {
            List<RecommendationFeedback> feedbacks = feedbackService.getFeedbacksByTaskTitle(tenantCode, taskTitle);
            return JsonResponse.success(feedbacks, "搜索推荐反馈成功");
        } catch (Exception e) {
            log.error("搜索推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("搜索推荐反馈失败: " + e.getMessage());
        }
    }

    /**
     * 根据租户编码和用户账号查找推荐反馈
     *
     * @param tenantCode  租户编码
     * @param userAccount 用户账号
     * @return 推荐反馈列表
     */
    @GetMapping("/user")
    public JsonResponse<List<RecommendationFeedback>> getFeedbacksByUserAccount(
            @RequestParam String tenantCode,
            @RequestParam String userAccount) {
        try {
            List<RecommendationFeedback> feedbacks = feedbackService.getFeedbacksByUserAccount(tenantCode, userAccount);
            return JsonResponse.success(feedbacks, "获取推荐反馈成功");
        } catch (Exception e) {
            log.error("获取推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取推荐反馈失败: " + e.getMessage());
        }
    }

    /**
     * 根据租户编码和推荐的领导账号查找推荐反馈
     *
     * @param tenantCode               租户编码
     * @param recommendedLeaderAccount 推荐的领导账号
     * @return 推荐反馈列表
     */
    @GetMapping("/leader")
    public JsonResponse<List<RecommendationFeedback>> getFeedbacksByRecommendedLeader(
            @RequestParam String tenantCode,
            @RequestParam String recommendedLeaderAccount) {
        try {
            List<RecommendationFeedback> feedbacks = feedbackService.getFeedbacksByRecommendedLeader(
                    tenantCode, recommendedLeaderAccount);
            return JsonResponse.success(feedbacks, "获取推荐反馈成功");
        } catch (Exception e) {
            log.error("获取推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取推荐反馈失败: " + e.getMessage());
        }
    }

    /**
     * 根据租户编码和用户是否接受推荐查找推荐反馈
     *
     * @param tenantCode 租户编码
     * @param isAccepted 是否接受推荐
     * @return 推荐反馈列表
     */
    @GetMapping("/acceptance")
    public JsonResponse<List<RecommendationFeedback>> getFeedbacksByAcceptance(
            @RequestParam String tenantCode,
            @RequestParam Boolean isAccepted) {
        try {
            List<RecommendationFeedback> feedbacks = feedbackService.getFeedbacksByAcceptance(tenantCode, isAccepted);
            return JsonResponse.success(feedbacks, "获取推荐反馈成功");
        } catch (Exception e) {
            log.error("获取推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取推荐反馈失败: " + e.getMessage());
        }
    }

    /**
     * 根据租户编码和工作流方向查找推荐反馈
     *
     * @param tenantCode        租户编码
     * @param workflowDirection 工作流方向
     * @return 推荐反馈列表
     */
    @GetMapping("/direction")
    public JsonResponse<List<RecommendationFeedback>> getFeedbacksByWorkflowDirection(
            @RequestParam String tenantCode,
            @RequestParam WorkflowDirection workflowDirection) {
        try {
            List<RecommendationFeedback> feedbacks = feedbackService.getFeedbacksByWorkflowDirection(
                    tenantCode, workflowDirection);
            return JsonResponse.success(feedbacks, "获取推荐反馈成功");
        } catch (Exception e) {
            log.error("获取推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取推荐反馈失败: " + e.getMessage());
        }
    }

    /**
     * 根据租户编码和反馈时间范围查找推荐反馈
     *
     * @param tenantCode 租户编码
     * @param startDate  开始时间
     * @param endDate    结束时间
     * @return 推荐反馈列表
     */
    @GetMapping("/time-range")
    public JsonResponse<List<RecommendationFeedback>> getFeedbacksByTimeRange(
            @RequestParam String tenantCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDate) {
        try {
            List<RecommendationFeedback> feedbacks = feedbackService.getFeedbacksByTimeRange(
                    tenantCode, startDate, endDate);
            return JsonResponse.success(feedbacks, "获取推荐反馈成功");
        } catch (Exception e) {
            log.error("获取推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取推荐反馈失败: " + e.getMessage());
        }
    }

    /**
     * 根据租户编码和推荐类型查找推荐反馈
     *
     * @param tenantCode         租户编码
     * @param recommendationType 推荐类型
     * @return 推荐反馈列表
     */
    @GetMapping("/type")
    public JsonResponse<List<RecommendationFeedback>> getFeedbacksByRecommendationType(
            @RequestParam String tenantCode,
            @RequestParam String recommendationType) {
        try {
            List<RecommendationFeedback> feedbacks = feedbackService.getFeedbacksByRecommendationType(
                    tenantCode, recommendationType);
            return JsonResponse.success(feedbacks, "获取推荐反馈成功");
        } catch (Exception e) {
            log.error("获取推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取推荐反馈失败: " + e.getMessage());
        }
    }

    /**
     * 根据租户编码和用户反馈评分查找推荐反馈
     *
     * @param tenantCode 租户编码
     * @param rating     用户反馈评分
     * @return 推荐反馈列表
     */
    @GetMapping("/rating")
    public JsonResponse<List<RecommendationFeedback>> getFeedbacksByRating(
            @RequestParam String tenantCode,
            @RequestParam Integer rating) {
        try {
            List<RecommendationFeedback> feedbacks = feedbackService.getFeedbacksByRating(tenantCode, rating);
            return JsonResponse.success(feedbacks, "获取推荐反馈成功");
        } catch (Exception e) {
            log.error("获取推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取推荐反馈失败: " + e.getMessage());
        }
    }

    /**
     * 计算指定租户的推荐接受率
     *
     * @param tenantCode 租户编码
     * @return 接受率
     */
    @GetMapping("/acceptance-rate")
    public JsonResponse<Double> calculateAcceptanceRate(@RequestParam String tenantCode) {
        try {
            double acceptanceRate = feedbackService.calculateAcceptanceRate(tenantCode);
            return JsonResponse.success(acceptanceRate, "计算推荐接受率成功");
        } catch (Exception e) {
            log.error("计算推荐接受率失败: {}", e.getMessage(), e);
            return JsonResponse.fail("计算推荐接受率失败: " + e.getMessage());
        }
    }

    /**
     * 计算指定租户和推荐类型的推荐接受率
     *
     * @param tenantCode         租户编码
     * @param recommendationType 推荐类型
     * @return 接受率
     */
    @GetMapping("/acceptance-rate-by-type")
    public JsonResponse<Double> calculateAcceptanceRateByType(
            @RequestParam String tenantCode,
            @RequestParam String recommendationType) {
        try {
            double acceptanceRate = feedbackService.calculateAcceptanceRateByType(tenantCode, recommendationType);
            return JsonResponse.success(acceptanceRate, "计算推荐接受率成功");
        } catch (Exception e) {
            log.error("计算推荐接受率失败: {}", e.getMessage(), e);
            return JsonResponse.fail("计算推荐接受率失败: " + e.getMessage());
        }
    }

    /**
     * 计算指定租户的平均评分
     *
     * @param tenantCode 租户编码
     * @return 平均评分
     */
    @GetMapping("/average-rating")
    public JsonResponse<Double> calculateAverageRating(@RequestParam String tenantCode) {
        try {
            double avgRating = feedbackService.calculateAverageRating(tenantCode);
            return JsonResponse.success(avgRating, "计算平均评分成功");
        } catch (Exception e) {
            log.error("计算平均评分失败: {}", e.getMessage(), e);
            return JsonResponse.fail("计算平均评分失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定租户的推荐统计信息
     *
     * @param tenantCode 租户编码
     * @return 统计信息
     */
    @GetMapping("/stats")
    public JsonResponse<Map<String, Object>> getRecommendationStats(@RequestParam String tenantCode) {
        try {
            Map<String, Object> stats = feedbackService.getRecommendationStats(tenantCode);
            return JsonResponse.success(stats, "获取推荐统计信息成功");
        } catch (Exception e) {
            log.error("获取推荐统计信息失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取推荐统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 删除推荐反馈
     *
     * @param id 推荐反馈ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public JsonResponse<Void> deleteFeedback(@PathVariable Long id) {
        try {
            feedbackService.deleteFeedback(id);
            return JsonResponse.success(null, "删除推荐反馈成功");
        } catch (Exception e) {
            log.error("删除推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("删除推荐反馈失败: " + e.getMessage());
        }
    }

    /**
     * 清空指定租户的所有推荐反馈
     *
     * @param tenantCode 租户编码
     * @return 删除结果
     */
    @DeleteMapping("/tenant/{tenantCode}")
    public JsonResponse<Long> clearFeedbacks(@PathVariable String tenantCode) {
        try {
            long count = feedbackService.clearFeedbacks(tenantCode);
            return JsonResponse.success(count, "清空推荐反馈成功，共删除 " + count + " 条记录");
        } catch (Exception e) {
            log.error("清空推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("清空推荐反馈失败: " + e.getMessage());
        }
    }
}
