package com.simbest.boot.suggest.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.simbest.boot.suggest.model.ApprovalHistory;
import com.simbest.boot.suggest.model.JsonResponse;
import com.simbest.boot.suggest.model.WorkflowDirection;
import com.simbest.boot.suggest.service.ApprovalHistoryService;

import lombok.extern.slf4j.Slf4j;

/**
 * 批复历史记录控制器
 * 提供批复历史记录相关的API接口
 */
@RestController
@RequestMapping("/approval-history")
@Slf4j
public class ApprovalHistoryController {
    
    @Autowired
    private ApprovalHistoryService approvalHistoryService;
    
    /**
     * 保存批复历史记录
     * 
     * @param approvalHistory 批复历史记录对象
     * @return 保存结果
     */
    @PostMapping
    public JsonResponse<ApprovalHistory> saveApprovalHistory(@RequestBody ApprovalHistory approvalHistory) {
        try {
            ApprovalHistory savedHistory = approvalHistoryService.saveApprovalHistory(approvalHistory);
            return JsonResponse.success(savedHistory, "保存批复历史记录成功");
        } catch (Exception e) {
            log.error("保存批复历史记录失败: {}", e.getMessage(), e);
            return JsonResponse.fail("保存批复历史记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量保存批复历史记录
     * 
     * @param approvalHistories 批复历史记录列表
     * @return 保存结果
     */
    @PostMapping("/batch")
    public JsonResponse<List<ApprovalHistory>> saveAllApprovalHistories(@RequestBody List<ApprovalHistory> approvalHistories) {
        try {
            List<ApprovalHistory> savedHistories = approvalHistoryService.saveAllApprovalHistories(approvalHistories);
            return JsonResponse.success(savedHistories, "批量保存批复历史记录成功");
        } catch (Exception e) {
            log.error("批量保存批复历史记录失败: {}", e.getMessage(), e);
            return JsonResponse.fail("批量保存批复历史记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取批复历史记录
     * 
     * @param id 批复历史记录ID
     * @return 批复历史记录对象
     */
    @GetMapping("/{id}")
    public JsonResponse<ApprovalHistory> getApprovalHistoryById(@PathVariable Long id) {
        try {
            Optional<ApprovalHistory> historyOpt = approvalHistoryService.getApprovalHistoryById(id);
            if (historyOpt.isPresent()) {
                return JsonResponse.success(historyOpt.get(), "获取批复历史记录成功");
            } else {
                return JsonResponse.fail("批复历史记录不存在: " + id);
            }
        } catch (Exception e) {
            log.error("获取批复历史记录失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取批复历史记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据租户编码获取批复历史记录
     * 
     * @param tenantCode 租户编码
     * @return 批复历史记录列表
     */
    @GetMapping("/tenant/{tenantCode}")
    public JsonResponse<List<ApprovalHistory>> getApprovalHistoriesByTenantCode(@PathVariable String tenantCode) {
        try {
            List<ApprovalHistory> histories = approvalHistoryService.getApprovalHistoriesByTenantCode(tenantCode);
            return JsonResponse.success(histories, "获取批复历史记录成功");
        } catch (Exception e) {
            log.error("获取批复历史记录失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取批复历史记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据租户编码和任务标题相似度查找批复历史记录
     * 
     * @param tenantCode 租户编码
     * @param taskTitle 任务标题关键词
     * @return 批复历史记录列表
     */
    @GetMapping("/search")
    public JsonResponse<List<ApprovalHistory>> searchApprovalHistories(
            @RequestParam String tenantCode, 
            @RequestParam String taskTitle) {
        try {
            List<ApprovalHistory> histories = approvalHistoryService.getApprovalHistoriesByTaskTitle(tenantCode, taskTitle);
            return JsonResponse.success(histories, "搜索批复历史记录成功");
        } catch (Exception e) {
            log.error("搜索批复历史记录失败: {}", e.getMessage(), e);
            return JsonResponse.fail("搜索批复历史记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据租户编码和任务标题相似度查找批复历史记录，并分页
     * 
     * @param tenantCode 租户编码
     * @param taskTitle 任务标题关键词
     * @param page 页码
     * @param size 每页大小
     * @return 批复历史记录分页结果
     */
    @GetMapping("/search/page")
    public JsonResponse<Page<ApprovalHistory>> searchApprovalHistoriesWithPaging(
            @RequestParam String tenantCode, 
            @RequestParam String taskTitle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ApprovalHistory> histories = approvalHistoryService.getApprovalHistoriesByTaskTitle(
                    tenantCode, taskTitle, page, size);
            return JsonResponse.success(histories, "分页搜索批复历史记录成功");
        } catch (Exception e) {
            log.error("分页搜索批复历史记录失败: {}", e.getMessage(), e);
            return JsonResponse.fail("分页搜索批复历史记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据租户编码和发起人账号查找批复历史记录
     * 
     * @param tenantCode 租户编码
     * @param initiatorAccount 发起人账号
     * @return 批复历史记录列表
     */
    @GetMapping("/initiator")
    public JsonResponse<List<ApprovalHistory>> getApprovalHistoriesByInitiator(
            @RequestParam String tenantCode, 
            @RequestParam String initiatorAccount) {
        try {
            List<ApprovalHistory> histories = approvalHistoryService.getApprovalHistoriesByInitiator(
                    tenantCode, initiatorAccount);
            return JsonResponse.success(histories, "获取批复历史记录成功");
        } catch (Exception e) {
            log.error("获取批复历史记录失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取批复历史记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据租户编码和审批人账号查找批复历史记录
     * 
     * @param tenantCode 租户编码
     * @param approverAccount 审批人账号
     * @return 批复历史记录列表
     */
    @GetMapping("/approver")
    public JsonResponse<List<ApprovalHistory>> getApprovalHistoriesByApprover(
            @RequestParam String tenantCode, 
            @RequestParam String approverAccount) {
        try {
            List<ApprovalHistory> histories = approvalHistoryService.getApprovalHistoriesByApprover(
                    tenantCode, approverAccount);
            return JsonResponse.success(histories, "获取批复历史记录成功");
        } catch (Exception e) {
            log.error("获取批复历史记录失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取批复历史记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据租户编码、发起人账号和工作流方向查找批复历史记录
     * 
     * @param tenantCode 租户编码
     * @param initiatorAccount 发起人账号
     * @param workflowDirection 工作流方向
     * @return 批复历史记录列表
     */
    @GetMapping("/initiator-direction")
    public JsonResponse<List<ApprovalHistory>> getApprovalHistoriesByInitiatorAndDirection(
            @RequestParam String tenantCode, 
            @RequestParam String initiatorAccount,
            @RequestParam WorkflowDirection workflowDirection) {
        try {
            List<ApprovalHistory> histories = approvalHistoryService.getApprovalHistoriesByInitiatorAndDirection(
                    tenantCode, initiatorAccount, workflowDirection);
            return JsonResponse.success(histories, "获取批复历史记录成功");
        } catch (Exception e) {
            log.error("获取批复历史记录失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取批复历史记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据租户编码和时间范围查找批复历史记录
     * 
     * @param tenantCode 租户编码
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 批复历史记录列表
     */
    @GetMapping("/time-range")
    public JsonResponse<List<ApprovalHistory>> getApprovalHistoriesByTimeRange(
            @RequestParam String tenantCode, 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDate) {
        try {
            List<ApprovalHistory> histories = approvalHistoryService.getApprovalHistoriesByTimeRange(
                    tenantCode, startDate, endDate);
            return JsonResponse.success(histories, "获取批复历史记录成功");
        } catch (Exception e) {
            log.error("获取批复历史记录失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取批复历史记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 查找指定租户中最常用的审批人
     * 
     * @param tenantCode 租户编码
     * @param limit 返回结果数量限制
     * @return 审批人账号和使用次数的映射
     */
    @GetMapping("/most-frequent-approvers")
    public JsonResponse<Map<String, Long>> getMostFrequentApprovers(
            @RequestParam String tenantCode, 
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Map<String, Long> approverCounts = approvalHistoryService.getMostFrequentApprovers(tenantCode, limit);
            return JsonResponse.success(approverCounts, "获取最常用审批人成功");
        } catch (Exception e) {
            log.error("获取最常用审批人失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取最常用审批人失败: " + e.getMessage());
        }
    }
    
    /**
     * 查找指定租户和发起人最常用的审批人
     * 
     * @param tenantCode 租户编码
     * @param initiatorAccount 发起人账号
     * @param limit 返回结果数量限制
     * @return 审批人账号和使用次数的映射
     */
    @GetMapping("/most-frequent-approvers-by-initiator")
    public JsonResponse<Map<String, Long>> getMostFrequentApproversByInitiator(
            @RequestParam String tenantCode, 
            @RequestParam String initiatorAccount,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Map<String, Long> approverCounts = approvalHistoryService.getMostFrequentApproversByInitiator(
                    tenantCode, initiatorAccount, limit);
            return JsonResponse.success(approverCounts, "获取最常用审批人成功");
        } catch (Exception e) {
            log.error("获取最常用审批人失败: {}", e.getMessage(), e);
            return JsonResponse.fail("获取最常用审批人失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除批复历史记录
     * 
     * @param id 批复历史记录ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public JsonResponse<Void> deleteApprovalHistory(@PathVariable Long id) {
        try {
            approvalHistoryService.deleteApprovalHistory(id);
            return JsonResponse.success(null, "删除批复历史记录成功");
        } catch (Exception e) {
            log.error("删除批复历史记录失败: {}", e.getMessage(), e);
            return JsonResponse.fail("删除批复历史记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 清空指定租户的所有批复历史记录
     * 
     * @param tenantCode 租户编码
     * @return 删除结果
     */
    @DeleteMapping("/tenant/{tenantCode}")
    public JsonResponse<Long> clearApprovalHistories(@PathVariable String tenantCode) {
        try {
            long count = approvalHistoryService.clearApprovalHistories(tenantCode);
            return JsonResponse.success(count, "清空批复历史记录成功，共删除 " + count + " 条记录");
        } catch (Exception e) {
            log.error("清空批复历史记录失败: {}", e.getMessage(), e);
            return JsonResponse.fail("清空批复历史记录失败: " + e.getMessage());
        }
    }
}
