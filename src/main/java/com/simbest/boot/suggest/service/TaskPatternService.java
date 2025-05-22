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

import com.simbest.boot.suggest.model.TaskPattern;
import com.simbest.boot.suggest.model.WorkflowDirection;
import com.simbest.boot.suggest.repository.TaskPatternRepository;
import com.simbest.boot.suggest.util.ChineseTokenizer;
import com.simbest.boot.suggest.util.TenantValidator;

import lombok.extern.slf4j.Slf4j;

/**
 * 任务模式服务类
 * 提供任务模式相关的业务逻辑
 */
@Service
@Slf4j
public class TaskPatternService {

    @Autowired
    private TaskPatternRepository taskPatternRepository;

    @Autowired
    private TenantValidator tenantValidator;

    /**
     * 创建任务模式
     *
     * @param taskPattern 任务模式对象
     * @return 创建后的任务模式对象
     */
    @Transactional
    public TaskPattern createTaskPattern(TaskPattern taskPattern) {
        // 验证租户
        tenantValidator.validateTenant(taskPattern.getTenantCode());

        // 设置创建时间和更新时间
        Date now = new Date();
        taskPattern.setCreateTime(now);
        taskPattern.setUpdateTime(now);
        taskPattern.setLastUpdateTime(now);

        // 初始化匹配次数和置信度
        if (taskPattern.getMatchCount() == null) {
            taskPattern.setMatchCount(0);
        }
        if (taskPattern.getConfidence() == null) {
            taskPattern.setConfidence(0.0);
        }

        // 如果没有设置批复人权重映射，则初始化为空映射
        if (taskPattern.getApproverWeights() == null) {
            taskPattern.setApproverWeights(new HashMap<>());
        }

        TaskPattern savedPattern = taskPatternRepository.save(taskPattern);
        log.info("创建任务模式成功: {}", savedPattern.getId());
        return savedPattern;
    }

    /**
     * 更新任务模式
     *
     * @param taskPattern 任务模式对象
     * @return 更新后的任务模式对象
     */
    @Transactional
    public TaskPattern updateTaskPattern(TaskPattern taskPattern) {
        // 验证租户
        tenantValidator.validateTenant(taskPattern.getTenantCode());

        // 检查任务模式是否存在
        if (!taskPatternRepository.existsById(taskPattern.getId())) {
            throw new IllegalArgumentException("任务模式不存在: " + taskPattern.getId());
        }

        // 设置更新时间
        Date now = new Date();
        taskPattern.setUpdateTime(now);
        taskPattern.setLastUpdateTime(now);

        TaskPattern updatedPattern = taskPatternRepository.save(taskPattern);
        log.info("更新任务模式成功: {}", updatedPattern.getId());
        return updatedPattern;
    }

    /**
     * 根据ID查找任务模式
     *
     * @param id 任务模式ID
     * @return 任务模式对象
     */
    public Optional<TaskPattern> getTaskPatternById(Long id) {
        return taskPatternRepository.findById(id);
    }

    /**
     * 根据租户编码查找任务模式
     *
     * @param tenantCode 租户编码
     * @return 任务模式列表
     */
    public List<TaskPattern> getTaskPatternsByTenantCode(String tenantCode) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return taskPatternRepository.findByTenantCode(tenantCode);
    }

    /**
     * 根据租户编码和工作流方向查找任务模式
     *
     * @param tenantCode        租户编码
     * @param workflowDirection 工作流方向
     * @return 任务模式列表
     */
    public List<TaskPattern> getTaskPatternsByWorkflowDirection(String tenantCode,
            WorkflowDirection workflowDirection) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return taskPatternRepository.findByTenantCodeAndWorkflowDirection(tenantCode, workflowDirection);
    }

    /**
     * 根据租户编码和模式名称查找任务模式
     *
     * @param tenantCode  租户编码
     * @param patternName 模式名称
     * @return 任务模式列表
     */
    public List<TaskPattern> getTaskPatternsByPatternName(String tenantCode, String patternName) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return taskPatternRepository.findByTenantCodeAndPatternName(tenantCode, patternName);
    }

    /**
     * 根据租户编码和关键词查找任务模式
     *
     * @param tenantCode 租户编码
     * @param keyword    关键词
     * @return 任务模式列表
     */
    public List<TaskPattern> getTaskPatternsByKeyword(String tenantCode, String keyword) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return taskPatternRepository.findByTenantCodeAndKeyword(tenantCode, keyword);
    }

    /**
     * 根据租户编码和置信度阈值查找任务模式
     *
     * @param tenantCode          租户编码
     * @param confidenceThreshold 置信度阈值
     * @return 任务模式列表
     */
    public List<TaskPattern> getTaskPatternsByConfidenceThreshold(String tenantCode, double confidenceThreshold) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        return taskPatternRepository.findByTenantCodeAndConfidenceGreaterThanEqual(tenantCode, confidenceThreshold);
    }

    /**
     * 根据租户编码和工作流方向查找任务模式，并按置信度降序排序
     *
     * @param tenantCode        租户编码
     * @param workflowDirection 工作流方向
     * @param page              页码
     * @param size              每页大小
     * @return 任务模式分页结果
     */
    public Page<TaskPattern> getTaskPatternsByWorkflowDirectionSortByConfidence(
            String tenantCode, WorkflowDirection workflowDirection, int page, int size) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        Pageable pageable = PageRequest.of(page, size);
        return taskPatternRepository.findByTenantCodeAndWorkflowDirectionOrderByConfidenceDesc(
                tenantCode, workflowDirection, pageable);
    }

    /**
     * 根据租户编码查找最常用的任务模式
     *
     * @param tenantCode 租户编码
     * @param limit      返回结果数量限制
     * @return 任务模式列表
     */
    public List<TaskPattern> getMostUsedTaskPatterns(String tenantCode, int limit) {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "matchCount"));
        Page<TaskPattern> page = taskPatternRepository.findByTenantCodeOrderByMatchCountDesc(tenantCode, pageable);
        return page.getContent();
    }

    /**
     * 根据任务标题和工作流方向查找最匹配的任务模式
     *
     * @param tenantCode          租户编码
     * @param taskTitle           任务标题
     * @param workflowDirection   工作流方向
     * @param confidenceThreshold 置信度阈值
     * @return 最匹配的任务模式
     * @throws java.io.IOException 如果初始化分词器失败
     */
    public TaskPattern findBestMatchingPattern(
            String tenantCode, String taskTitle, WorkflowDirection workflowDirection, double confidenceThreshold)
            throws java.io.IOException {
        // 验证租户
        tenantValidator.validateTenant(tenantCode);

        // 获取指定工作流方向的所有任务模式
        List<TaskPattern> patterns = taskPatternRepository.findByTenantCodeAndWorkflowDirection(
                tenantCode, workflowDirection);

        if (patterns.isEmpty()) {
            return null;
        }

        // 对任务标题进行分词
        List<String> taskTokens = ChineseTokenizer.tokenize(taskTitle);

        // 计算每个模式的匹配分数
        TaskPattern bestPattern = null;
        double bestScore = 0.0;

        for (TaskPattern pattern : patterns) {
            // 如果模式的置信度为null或低于阈值，则跳过
            Double confidence = pattern.getConfidence();
            if (confidence == null) {
                log.warn("任务模式 {} 的置信度为null，使用默认值0.0", pattern.getId());
                confidence = 0.0;
                pattern.setConfidence(confidence);
            }
            if (confidence < confidenceThreshold) {
                continue;
            }

            // 检查关键词列表是否为null
            List<String> keywords = pattern.getKeywords();
            if (keywords == null) {
                log.warn("任务模式 {} 的关键词列表为null", pattern.getId());
                continue;
            }

            // 计算关键词匹配分数
            double score = calculateKeywordMatchScore(keywords, taskTokens, taskTitle);

            // 更新最佳匹配
            if (score > bestScore) {
                bestScore = score;
                bestPattern = pattern;
            }
        }

        // 如果最佳匹配分数太低，则返回null
        if (bestScore < 0.3) {
            return null;
        }

        return bestPattern;
    }

    /**
     * 计算关键词匹配分数
     *
     * @param patternKeywords 模式关键词列表
     * @param taskTokens      任务标题分词结果
     * @param taskTitle       原始任务标题
     * @return 匹配分数（0-1之间）
     */
    private double calculateKeywordMatchScore(List<String> patternKeywords, List<String> taskTokens, String taskTitle) {
        if (patternKeywords == null || patternKeywords.isEmpty() ||
                taskTokens == null || taskTokens.isEmpty()) {
            return 0.0;
        }

        int matchCount = 0;

        for (String keyword : patternKeywords) {
            // 直接匹配
            if (taskTitle.contains(keyword)) {
                matchCount++;
                continue;
            }

            // 分词匹配
            if (taskTokens.contains(keyword)) {
                matchCount++;
            }
        }

        // 计算匹配比例
        return (double) matchCount / patternKeywords.size();
    }

    /**
     * 更新任务模式的匹配次数和批复人权重
     *
     * @param patternId       任务模式ID
     * @param approverAccount 批复人账号
     * @param weightIncrement 权重增量
     * @return 更新后的任务模式
     */
    @Transactional
    public TaskPattern updatePatternMatchingInfo(Long patternId, String approverAccount, double weightIncrement) {
        Optional<TaskPattern> patternOpt = taskPatternRepository.findById(patternId);
        if (!patternOpt.isPresent()) {
            throw new IllegalArgumentException("任务模式不存在: " + patternId);
        }

        TaskPattern pattern = patternOpt.get();

        // 增加匹配次数
        pattern.incrementMatchCount();

        // 更新批复人权重
        if (approverAccount != null && !approverAccount.isEmpty()) {
            Map<String, Double> approverWeights = pattern.getApproverWeights();
            if (approverWeights == null) {
                approverWeights = new HashMap<>();
                pattern.setApproverWeights(approverWeights);
            }

            double currentWeight = approverWeights.getOrDefault(approverAccount, 0.0);
            approverWeights.put(approverAccount, currentWeight + weightIncrement);
        }

        // 更新最后更新时间
        pattern.setLastUpdateTime(new Date());
        pattern.setUpdateTime(new Date());

        // 重新计算置信度
        recalculateConfidence(pattern);

        return taskPatternRepository.save(pattern);
    }

    /**
     * 重新计算任务模式的置信度
     *
     * @param pattern 任务模式
     */
    private void recalculateConfidence(TaskPattern pattern) {
        // 基于匹配次数和批复人权重计算置信度
        int matchCount = pattern.getMatchCount();
        Map<String, Double> approverWeights = pattern.getApproverWeights();

        // 计算批复人权重总和
        double totalWeight = 0.0;
        for (Double weight : approverWeights.values()) {
            totalWeight += weight;
        }

        // 计算置信度
        // 匹配次数越多，置信度越高
        // 批复人权重越集中，置信度越高
        double matchCountFactor = Math.min(1.0, matchCount / 10.0); // 最多10次匹配达到最大值

        double weightConcentrationFactor = 0.0;
        if (!approverWeights.isEmpty()) {
            // 计算权重集中度
            double maxWeight = 0.0;
            for (Double weight : approverWeights.values()) {
                maxWeight = Math.max(maxWeight, weight);
            }

            weightConcentrationFactor = totalWeight > 0 ? maxWeight / totalWeight : 0.0;
        }

        // 综合计算置信度
        double confidence = 0.4 * matchCountFactor + 0.6 * weightConcentrationFactor;
        pattern.setConfidence(confidence);
    }
}
