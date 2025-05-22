package com.simbest.boot.suggest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 应用程序统一配置类
 * 集中管理所有配置项，避免散落的@Value注解
 */
@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
@ToString
@Validated
public class AppConfig {

    // 系统配置
    private String name = "领导推荐系统";
    private String version = "1.0.0";
    private String domainIdPrefix = "domain_";
    private String systemCreator = "system";
    private String anonymousUser = "anonymous";
    private String defaultServerPort = "12345";
    private String defaultContextPath = "/suggest";

    // 显示配置
    private boolean displayShowConfidence = true;
    private boolean displayShowMetrics = true;
    private boolean displayUseAdvancedTerminology = true;
    private boolean displayShowRecommendationType = true;
    private boolean displayUseDetailedReason = true;
    private boolean displayShowScore = true;
    private boolean displayUsePercentageFormat = true;
    private boolean displayUseColoredOutput = false;

    // AI算法配置
    private boolean algorithmUseSemantic = true;
    private boolean algorithmUseDeepLearning = true;
    private boolean algorithmUseContextAwareness = true;
    private double algorithmConfidenceThreshold = 0.6;
    private double algorithmSemanticWeight = 0.7;
    private double algorithmOrganizationalWeight = 0.8;

    // 算法配置
    private double algorithmTextSimilarityWeight = 0.4;
    private double algorithmKeywordMatchWeight = 0.6;
    private double algorithmOrganizationWeight = 0.8;
    private double algorithmHistoryWeight = 0.7;
    private double algorithmDomainWeight = 0.6;

    // 阈值配置
    private double thresholdBaseThreshold = 0.01;
    private double thresholdShortTextThreshold = 0.3;
    private double thresholdMediumTextThreshold = 0.2;
    private double thresholdLongTextThreshold = 0.1;
    private int thresholdShortTextLength = 10;
    private int thresholdMediumTextLength = 50;

    // 历史匹配配置
    private double historicalMatchingSimilarityThreshold = 0.6;
    private int historicalMatchingMaxResults = 10;
    private double historicalMatchingConfidenceThreshold = 0.5;
    private boolean historicalMatchingUsePatterns = true;

    // 租户配置
    private String tenantDefaultCode = "default";
    private boolean tenantEnableMultiTenant = true;

    // 推荐配置
    private int recommendationOrganizationPriority = 1;
    private int recommendationHistoryPriority = 2;
    private int recommendationDomainPriority = 3;
    private int recommendationTextSimilarityPriority = 4;

    // 推荐原因模板
    private String recommendationReasonOrganization = "【规则1-基于组织关系匹配】当前用户是{orgName}的{userRole}，推荐该组织的{targetRole}，负责与任务相关的业务领域";
    private String recommendationReasonDomain = "【职责领域匹配】系统通过关键词匹配，发现该任务与此领导的职责领域{domainName}高度相关，匹配关键词：{keywords}";
    private String recommendationReasonTextSimilarity = "【AI文本语义分析】系统通过自然语言处理和语义向量计算，发现该任务与此领导的职责领域存在高度语义关联。推荐基于深度学习的文本理解模型和多维相似度算法";
    private String recommendationReasonHistory = "【历史批复模式匹配】系统通过分析历史批复记录，发现该任务与{patternName}模式高度匹配。该模式下，此领导是最常处理此类任务的审批人";

    // 通用推荐分数
    private double scoreOrganizationBased = 0.7;
    private double scoreKeywordBased = 0.8;
    private double scoreCandidateBased = 0.5;
    private double scoreSystemAnalysis = 0.8;

    // 组织匹配分数
    private double organizationMatchMainLeaderToDeputyScore = 0.9;
    private double organizationMatchDeputyLeaderToMainScore = 0.85;
    private double organizationMatchPeerLeaderScore = 0.8;
    private double organizationMatchDefaultScore = 0.75;

    // 文本相似度匹配分数
    private double textSimilarityHighScore = 0.8;
    private double textSimilarityMediumScore = 0.7;
    private double textSimilarityLowScore = 0.6;

    // 领域匹配分数
    private double domainMatchDirectMatchScore = 0.95;
    private double domainMatchKeywordMatchScore = 0.85;
    private double domainMatchDefaultScore = 0.7;

    // 历史匹配分数
    private double historyMatchDirectMatchScore = 0.9;
    private double historyMatchPatternMatchScore = 0.85;
    private double historyMatchDefaultScore = 0.7;
}
