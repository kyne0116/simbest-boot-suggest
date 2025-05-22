package com.simbest.boot.suggest.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
 * 推荐反馈控制器
 * 提供推荐反馈相关的API接口
 */
@RestController
@RequestMapping("/recommendation-feedback")
@Slf4j
public class RecommendationFeedbackController {

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
     * 更新推荐反馈
     *
     * @param id       推荐反馈ID
     * @param feedback 推荐反馈对象
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public JsonResponse<RecommendationFeedback> updateFeedback(@PathVariable Long id,
            @RequestBody RecommendationFeedback feedback) {
        try {
            feedback.setId(id);
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
                return JsonResponse.fail("推荐反馈不存在");
            }
        } catch (Exception e) {
            log.error("获取推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取推荐反馈失败: " + e.getMessage());
        }
    }

    /**
     * 根据租户编码获取所有推荐反馈
     *
     * @param tenantCode 租户编码
     * @param page       页码
     * @param size       每页大小
     * @return 推荐反馈列表
     */
    @GetMapping
    public JsonResponse<Page<RecommendationFeedback>> getAllFeedbacks(
            @RequestParam String tenantCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
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
    @GetMapping("/task")
    public JsonResponse<List<RecommendationFeedback>> getFeedbacksByTaskTitle(
            @RequestParam String tenantCode,
            @RequestParam String taskTitle) {
        try {
            List<RecommendationFeedback> feedbacks = feedbackService.getFeedbacksByTaskTitle(tenantCode, taskTitle);
            return JsonResponse.success(feedbacks, "获取推荐反馈成功");
        } catch (Exception e) {
            log.error("获取推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取推荐反馈失败: " + e.getMessage());
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
    @GetMapping("/workflow")
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
    @GetMapping("/time")
    public JsonResponse<List<RecommendationFeedback>> getFeedbacksByTimeRange(
            @RequestParam String tenantCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
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
     * 获取推荐反馈统计信息
     *
     * @param tenantCode 租户编码
     * @return 统计信息
     */
    @GetMapping("/stats")
    public JsonResponse<Map<String, Object>> getFeedbackStats(@RequestParam String tenantCode) {
        try {
            double acceptanceRate = feedbackService.calculateAcceptanceRate(tenantCode);
            double singleAcceptanceRate = feedbackService.calculateAcceptanceRateByType(tenantCode, "SINGLE");
            double multipleAcceptanceRate = feedbackService.calculateAcceptanceRateByType(tenantCode, "MULTIPLE");
            double averageRating = feedbackService.calculateAverageRating(tenantCode);

            Map<String, Object> stats = new HashMap<>();
            stats.put("acceptanceRate", acceptanceRate);
            stats.put("singleAcceptanceRate", singleAcceptanceRate);
            stats.put("multipleAcceptanceRate", multipleAcceptanceRate);
            stats.put("averageRating", averageRating);

            return JsonResponse.success(stats, "获取推荐反馈统计信息成功");
        } catch (Exception e) {
            log.error("获取推荐反馈统计信息失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取推荐反馈统计信息失败: " + e.getMessage());
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
     * 删除租户的所有推荐反馈
     *
     * @param tenantCode 租户编码
     * @return 删除结果
     */
    @DeleteMapping("/tenant/{tenantCode}")
    public JsonResponse<Void> deleteAllFeedbacks(@PathVariable String tenantCode) {
        try {
            feedbackService.deleteAllFeedbacks(tenantCode);
            return JsonResponse.success(null, "删除租户的所有推荐反馈成功");
        } catch (Exception e) {
            log.error("删除租户的所有推荐反馈失败: {}", e.getMessage(), e);
            return JsonResponse.fail("删除租户的所有推荐反馈失败: " + e.getMessage());
        }
    }
}
