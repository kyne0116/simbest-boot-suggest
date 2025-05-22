package com.simbest.boot.suggest.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simbest.boot.suggest.model.ApprovalHistory;
import com.simbest.boot.suggest.model.WorkflowDirection;
import com.simbest.boot.suggest.repository.ApprovalHistoryRepository;
import com.simbest.boot.suggest.util.TenantValidator;

import lombok.extern.slf4j.Slf4j;

/**
 * 批复历史记录服务类
 * 提供批复历史记录相关的业务逻辑
 */
@Service
@Slf4j
public class ApprovalHistoryService {

    @Autowired
    private ApprovalHistoryRepository approvalHistoryRepository;

    @Autowired
    private TenantValidator tenantValidator;

    /**
     * 保存批复历史记录
     *
     * @param approvalHistory 批复历史记录对象
     * @return 保存后的批复历史记录对象
     */
    @Transactional
    public ApprovalHistory saveApprovalHistory(ApprovalHistory approvalHistory) {
        // 验证租户
        tenantValidator.validateTenant(approvalHistory.getTenantCode());

        // 设置创建时间和更新时间
        if (approvalHistory.getCreateTime() == null) {
            approvalHistory.setCreateTime(new Date());
        }
        approvalHistory.setUpdateTime(new Date());

        ApprovalHistory savedHistory = approvalHistoryRepository.save(approvalHistory);
        log.info("保存批复历史记录成功: {}", savedHistory.getId());
        return savedHistory;
    }

    /**
     * 批量保存批复历史记录
     *
     * @param approvalHistories 批复历史记录列表
     * @return 保存后的批复历史记录列表
     */
    @Transactional
    public List<ApprovalHistory> saveAllApprovalHistories(List<ApprovalHistory> approvalHistories) {
        // 设置创建时间和更新时间
        Date now = new Date();
        for (ApprovalHistory history : approvalHistories) {
            // 验证租户
            tenantValidator.validateTenant(history.getTenantCode());

            if (history.getCreateTime() == null) {
                history.setCreateTime(now);
            }
            history.setUpdateTime(now);
        }

        List<ApprovalHistory> savedHistories = approvalHistoryRepository.saveAll(approvalHistories);
        log.info("批量保存批复历史记录成功: {} 条记录", savedHistories.size());
        return savedHistories;
    }

    /**
     * 根据ID查找批复历史记录
     *
     * @param id 批复历史记录ID
     * @return 批复历史记录对象
     */
    public Optional<ApprovalHistory> getApprovalHistoryById(Long id) {
        return approvalHistoryRepository.findById(id);
    }

    /**
     * 根据租户编码查找批复历史记录
     *
     * @param tenantCode 租户编码
     * @return 批复历史记录列表
     */
    public List<ApprovalHistory> getApprovalHistoriesByTenantCode(String tenantCode) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return approvalHistoryRepository.findByTenantCode(tenantCode);
    }

    /**
     * 根据租户编码和任务标题相似度查找批复历史记录
     *
     * @param tenantCode 租户编码
     * @param taskTitle  任务标题关键词
     * @return 批复历史记录列表
     */
    public List<ApprovalHistory> getApprovalHistoriesByTaskTitle(String tenantCode, String taskTitle) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return approvalHistoryRepository.findByTenantCodeAndTaskTitleContaining(tenantCode, taskTitle);
    }

    /**
     * 根据租户编码和任务标题相似度查找批复历史记录，并分页
     *
     * @param tenantCode 租户编码
     * @param taskTitle  任务标题关键词
     * @param page       页码
     * @param size       每页大小
     * @return 批复历史记录分页结果
     */
    public Page<ApprovalHistory> getApprovalHistoriesByTaskTitle(String tenantCode, String taskTitle, int page,
            int size) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "approvalTime"));
        return approvalHistoryRepository.findByTenantCodeAndTaskTitleContaining(tenantCode, taskTitle, pageable);
    }

    /**
     * 根据租户编码和发起人账号查找批复历史记录
     *
     * @param tenantCode       租户编码
     * @param initiatorAccount 发起人账号
     * @return 批复历史记录列表
     */
    public List<ApprovalHistory> getApprovalHistoriesByInitiator(String tenantCode, String initiatorAccount) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return approvalHistoryRepository.findByTenantCodeAndInitiatorAccount(tenantCode, initiatorAccount);
    }

    /**
     * 根据租户编码和审批人账号查找批复历史记录
     *
     * @param tenantCode      租户编码
     * @param approverAccount 审批人账号
     * @return 批复历史记录列表
     */
    public List<ApprovalHistory> getApprovalHistoriesByApprover(String tenantCode, String approverAccount) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return approvalHistoryRepository.findByTenantCodeAndApproverAccount(tenantCode, approverAccount);
    }

    /**
     * 根据租户编码、发起人账号和工作流方向查找批复历史记录
     *
     * @param tenantCode        租户编码
     * @param initiatorAccount  发起人账号
     * @param workflowDirection 工作流方向
     * @return 批复历史记录列表
     */
    public List<ApprovalHistory> getApprovalHistoriesByInitiatorAndDirection(
            String tenantCode, String initiatorAccount, WorkflowDirection workflowDirection) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return approvalHistoryRepository.findByTenantCodeAndInitiatorAccountAndWorkflowDirection(
                tenantCode, initiatorAccount, workflowDirection);
    }

    /**
     * 根据租户编码和工作流方向查找批复历史记录
     *
     * @param tenantCode        租户编码
     * @param workflowDirection 工作流方向
     * @return 批复历史记录列表
     */
    public List<ApprovalHistory> getApprovalHistoriesByWorkflowDirection(
            String tenantCode, WorkflowDirection workflowDirection) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return approvalHistoryRepository.findByTenantCodeAndWorkflowDirection(
                tenantCode, workflowDirection);
    }

    /**
     * 根据租户编码和时间范围查找批复历史记录
     *
     * @param tenantCode 租户编码
     * @param startDate  开始时间
     * @param endDate    结束时间
     * @return 批复历史记录列表
     */
    public List<ApprovalHistory> getApprovalHistoriesByTimeRange(String tenantCode, Date startDate, Date endDate) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return approvalHistoryRepository.findByTenantCodeAndApprovalTimeBetween(tenantCode, startDate, endDate);
    }

    /**
     * 根据租户编码、发起人账号和任务标题相似度查找批复历史记录
     *
     * @param tenantCode       租户编码
     * @param initiatorAccount 发起人账号
     * @param taskTitle        任务标题关键词
     * @return 批复历史记录列表
     */
    public List<ApprovalHistory> getApprovalHistoriesByInitiatorAndTaskTitle(
            String tenantCode, String initiatorAccount, String taskTitle) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return approvalHistoryRepository.findByTenantCodeAndInitiatorAccountAndTaskTitleContaining(
                tenantCode, initiatorAccount, taskTitle);
    }

    /**
     * 查找指定租户中最常用的审批人
     *
     * @param tenantCode 租户编码
     * @param limit      返回结果数量限制
     * @return 审批人账号和使用次数的映射
     */
    public Map<String, Long> getMostFrequentApprovers(String tenantCode, int limit) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = approvalHistoryRepository.findMostFrequentApprovers(tenantCode, pageable);

        Map<String, Long> approverCounts = new HashMap<>();
        for (Object[] result : results) {
            String approverAccount = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            approverCounts.put(approverAccount, count);
        }

        return approverCounts;
    }

    /**
     * 查找指定租户和发起人最常用的审批人
     *
     * @param tenantCode       租户编码
     * @param initiatorAccount 发起人账号
     * @param limit            返回结果数量限制
     * @return 审批人账号和使用次数的映射
     */
    public Map<String, Long> getMostFrequentApproversByInitiator(String tenantCode, String initiatorAccount,
            int limit) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = approvalHistoryRepository.findMostFrequentApproversByInitiator(
                tenantCode, initiatorAccount, pageable);

        Map<String, Long> approverCounts = new HashMap<>();
        for (Object[] result : results) {
            String approverAccount = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            approverCounts.put(approverAccount, count);
        }

        return approverCounts;
    }

    /**
     * 删除批复历史记录
     *
     * @param id 批复历史记录ID
     */
    @Transactional
    public void deleteApprovalHistory(Long id) {
        approvalHistoryRepository.deleteById(id);
        log.info("删除批复历史记录成功: {}", id);
    }

    /**
     * 清空指定租户的所有批复历史记录
     *
     * @param tenantCode 租户编码
     * @return 删除的记录数量
     */
    @Transactional
    public long clearApprovalHistories(String tenantCode) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        List<ApprovalHistory> histories = approvalHistoryRepository.findByTenantCode(tenantCode);
        long count = histories.size();

        approvalHistoryRepository.deleteAll(histories);
        log.info("清空租户 {} 的批复历史记录成功: {} 条记录", tenantCode, count);

        return count;
    }
}
