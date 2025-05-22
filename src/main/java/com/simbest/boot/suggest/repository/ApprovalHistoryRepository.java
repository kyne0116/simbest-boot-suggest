package com.simbest.boot.suggest.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.simbest.boot.suggest.model.ApprovalHistory;
import com.simbest.boot.suggest.model.WorkflowDirection;

/**
 * 批复历史记录Repository接口
 * 提供对批复历史记录实体的数据库操作
 */
@Repository
public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {

        /**
         * 根据租户编码查找批复历史记录
         *
         * @param tenantCode 租户编码
         * @return 批复历史记录列表
         */
        List<ApprovalHistory> findByTenantCode(String tenantCode);

        /**
         * 根据租户编码和任务标题相似度查找批复历史记录
         * 使用LIKE查询匹配任务标题
         *
         * @param tenantCode 租户编码
         * @param taskTitle  任务标题关键词
         * @return 批复历史记录列表
         */
        @Query("SELECT a FROM ApprovalHistory a WHERE a.tenantCode = :tenantCode AND a.taskTitle LIKE %:taskTitle%")
        List<ApprovalHistory> findByTenantCodeAndTaskTitleContaining(
                        @Param("tenantCode") String tenantCode,
                        @Param("taskTitle") String taskTitle);

        /**
         * 根据租户编码和发起人账号查找批复历史记录
         *
         * @param tenantCode       租户编码
         * @param initiatorAccount 发起人账号
         * @return 批复历史记录列表
         */
        List<ApprovalHistory> findByTenantCodeAndInitiatorAccount(String tenantCode, String initiatorAccount);

        /**
         * 根据租户编码和审批人账号查找批复历史记录
         *
         * @param tenantCode      租户编码
         * @param approverAccount 审批人账号
         * @return 批复历史记录列表
         */
        List<ApprovalHistory> findByTenantCodeAndApproverAccount(String tenantCode, String approverAccount);

        /**
         * 根据租户编码、发起人账号和工作流方向查找批复历史记录
         *
         * @param tenantCode        租户编码
         * @param initiatorAccount  发起人账号
         * @param workflowDirection 工作流方向
         * @return 批复历史记录列表
         */
        List<ApprovalHistory> findByTenantCodeAndInitiatorAccountAndWorkflowDirection(
                        String tenantCode, String initiatorAccount, WorkflowDirection workflowDirection);

        /**
         * 根据租户编码和工作流方向查找批复历史记录
         *
         * @param tenantCode        租户编码
         * @param workflowDirection 工作流方向
         * @return 批复历史记录列表
         */
        List<ApprovalHistory> findByTenantCodeAndWorkflowDirection(
                        String tenantCode, WorkflowDirection workflowDirection);

        /**
         * 根据租户编码和时间范围查找批复历史记录
         *
         * @param tenantCode 租户编码
         * @param startDate  开始时间
         * @param endDate    结束时间
         * @return 批复历史记录列表
         */
        List<ApprovalHistory> findByTenantCodeAndApprovalTimeBetween(
                        String tenantCode, Date startDate, Date endDate);

        /**
         * 根据租户编码和任务标题相似度查找批复历史记录，并分页
         *
         * @param tenantCode 租户编码
         * @param taskTitle  任务标题关键词
         * @param pageable   分页参数
         * @return 批复历史记录分页结果
         */
        @Query("SELECT a FROM ApprovalHistory a WHERE a.tenantCode = :tenantCode AND a.taskTitle LIKE %:taskTitle%")
        Page<ApprovalHistory> findByTenantCodeAndTaskTitleContaining(
                        @Param("tenantCode") String tenantCode,
                        @Param("taskTitle") String taskTitle,
                        Pageable pageable);

        /**
         * 根据租户编码、发起人账号和任务标题相似度查找批复历史记录
         *
         * @param tenantCode       租户编码
         * @param initiatorAccount 发起人账号
         * @param taskTitle        任务标题关键词
         * @return 批复历史记录列表
         */
        @Query("SELECT a FROM ApprovalHistory a WHERE a.tenantCode = :tenantCode AND a.initiatorAccount = :initiatorAccount AND a.taskTitle LIKE %:taskTitle%")
        List<ApprovalHistory> findByTenantCodeAndInitiatorAccountAndTaskTitleContaining(
                        @Param("tenantCode") String tenantCode,
                        @Param("initiatorAccount") String initiatorAccount,
                        @Param("taskTitle") String taskTitle);

        /**
         * 查找指定租户中最常用的审批人
         *
         * @param tenantCode 租户编码
         * @param limit      返回结果数量限制
         * @return 审批人账号和使用次数的列表
         */
        @Query("SELECT a.approverAccount, COUNT(a) FROM ApprovalHistory a WHERE a.tenantCode = :tenantCode GROUP BY a.approverAccount ORDER BY COUNT(a) DESC")
        List<Object[]> findMostFrequentApprovers(@Param("tenantCode") String tenantCode, Pageable limit);

        /**
         * 查找指定租户和发起人最常用的审批人
         *
         * @param tenantCode       租户编码
         * @param initiatorAccount 发起人账号
         * @param limit            返回结果数量限制
         * @return 审批人账号和使用次数的列表
         */
        @Query("SELECT a.approverAccount, COUNT(a) FROM ApprovalHistory a WHERE a.tenantCode = :tenantCode AND a.initiatorAccount = :initiatorAccount GROUP BY a.approverAccount ORDER BY COUNT(a) DESC")
        List<Object[]> findMostFrequentApproversByInitiator(
                        @Param("tenantCode") String tenantCode,
                        @Param("initiatorAccount") String initiatorAccount,
                        Pageable limit);
}
