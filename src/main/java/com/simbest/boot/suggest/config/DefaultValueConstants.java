package com.simbest.boot.suggest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 默认值常量类
 * 从AppConfig获取配置值，避免硬编码
 */
@Component
public class DefaultValueConstants {

    private static AppConfig appConfig;

    @Autowired
    public void setAppConfig(AppConfig appConfig) {
        DefaultValueConstants.appConfig = appConfig;
    }

    /**
     * 获取默认租户代码
     *
     * @return 默认租户代码
     */
    public static String getDefaultTenantCode() {
        return appConfig != null ? appConfig.getTenantDefaultCode() : "default";
    }

    /**
     * 获取系统创建者
     *
     * @return 系统创建者
     */
    public static String getSystemCreator() {
        return appConfig != null ? appConfig.getSystemCreator() : "system";
    }

    /**
     * 获取匿名用户
     *
     * @return 匿名用户
     */
    public static String getAnonymousUser() {
        return appConfig != null ? appConfig.getAnonymousUser() : "anonymous";
    }

    /**
     * 推荐原因模板
     */
    public static class RecommendationReasons {
        /**
         * 获取基于组织关系匹配的推荐原因模板
         *
         * @return 推荐原因模板
         */
        public static String getOrganizationReason() {
            return appConfig != null ? appConfig.getRecommendationReasonOrganization()
                    : "【规则1-基于组织关系匹配】当前用户是{orgName}的{userRole}，推荐该组织的{targetRole}，负责与任务相关的业务领域";
        }

        /**
         * 获取职责领域匹配的推荐原因模板
         *
         * @return 推荐原因模板
         */
        public static String getDomainReason() {
            return appConfig != null ? appConfig.getRecommendationReasonDomain()
                    : "【职责领域匹配】系统通过关键词匹配，发现该任务与此领导的职责领域{domainName}高度相关，匹配关键词：{keywords}";
        }

        /**
         * 获取文本相似度匹配的推荐原因模板
         *
         * @return 推荐原因模板
         */
        public static String getTextSimilarityReason() {
            return appConfig != null ? appConfig.getRecommendationReasonTextSimilarity()
                    : "【AI文本语义分析】系统通过自然语言处理和语义向量计算，发现该任务与此领导的职责领域存在高度语义关联。推荐基于深度学习的文本理解模型和多维相似度算法";
        }

        /**
         * 获取历史批复匹配的推荐原因模板
         *
         * @return 推荐原因模板
         */
        public static String getHistoryReason() {
            return appConfig != null ? appConfig.getRecommendationReasonHistory()
                    : "【历史批复模式匹配】系统通过分析历史批复记录，发现该任务与{patternName}模式高度匹配。该模式下，此领导是最常处理此类任务的审批人";
        }
    }

    /**
     * 组织匹配分数
     */
    public static class OrganizationMatchScores {
        /**
         * 获取主管领导到分管领导的匹配分数
         *
         * @return 匹配分数
         */
        public static double getMainLeaderToDeputyScore() {
            return appConfig != null ? appConfig.getOrganizationMatchMainLeaderToDeputyScore() : 0.9;
        }

        /**
         * 获取分管领导到主管领导的匹配分数
         *
         * @return 匹配分数
         */
        public static double getDeputyLeaderToMainScore() {
            return appConfig != null ? appConfig.getOrganizationMatchDeputyLeaderToMainScore() : 0.85;
        }

        /**
         * 获取同级领导的匹配分数
         *
         * @return 匹配分数
         */
        public static double getPeerLeaderScore() {
            return appConfig != null ? appConfig.getOrganizationMatchPeerLeaderScore() : 0.8;
        }

        /**
         * 获取默认匹配分数
         *
         * @return 匹配分数
         */
        public static double getDefaultScore() {
            return appConfig != null ? appConfig.getOrganizationMatchDefaultScore() : 0.75;
        }
    }

    /**
     * 文本相似度匹配分数
     */
    public static class TextSimilarityScores {
        /**
         * 获取高相似度分数
         *
         * @return 匹配分数
         */
        public static double getHighSimilarityScore() {
            return appConfig != null ? appConfig.getTextSimilarityHighScore() : 0.8;
        }

        /**
         * 获取中等相似度分数
         *
         * @return 匹配分数
         */
        public static double getMediumSimilarityScore() {
            return appConfig != null ? appConfig.getTextSimilarityMediumScore() : 0.7;
        }

        /**
         * 获取低相似度分数
         *
         * @return 匹配分数
         */
        public static double getLowSimilarityScore() {
            return appConfig != null ? appConfig.getTextSimilarityLowScore() : 0.6;
        }
    }

    /**
     * 阈值配置默认值
     */
    public static class ThresholdValues {
        /**
         * 获取基础阈值
         *
         * @return 阈值
         */
        public static double getBaseThreshold() {
            return appConfig != null ? appConfig.getThresholdBaseThreshold() : 0.01;
        }

        /**
         * 获取短文本阈值
         *
         * @return 阈值
         */
        public static double getShortTextThreshold() {
            return appConfig != null ? appConfig.getThresholdShortTextThreshold() : 0.3;
        }

        /**
         * 获取中等长度文本阈值
         *
         * @return 阈值
         */
        public static double getMediumTextThreshold() {
            return appConfig != null ? appConfig.getThresholdMediumTextThreshold() : 0.2;
        }

        /**
         * 获取长文本阈值
         *
         * @return 阈值
         */
        public static double getLongTextThreshold() {
            return appConfig != null ? appConfig.getThresholdLongTextThreshold() : 0.1;
        }
    }

    /**
     * 领域匹配默认分数
     */
    public static class DomainMatchScores {
        /**
         * 获取直接匹配分数
         *
         * @return 匹配分数
         */
        public static double getDirectMatchScore() {
            return appConfig != null ? appConfig.getDomainMatchDirectMatchScore() : 0.95;
        }

        /**
         * 获取关键词匹配分数
         *
         * @return 匹配分数
         */
        public static double getKeywordMatchScore() {
            return appConfig != null ? appConfig.getDomainMatchKeywordMatchScore() : 0.85;
        }

        /**
         * 获取默认匹配分数
         *
         * @return 匹配分数
         */
        public static double getDefaultScore() {
            return appConfig != null ? appConfig.getDomainMatchDefaultScore() : 0.7;
        }
    }

    /**
     * 历史匹配默认分数
     */
    public static class HistoryMatchScores {
        /**
         * 获取直接匹配分数
         *
         * @return 匹配分数
         */
        public static double getDirectMatchScore() {
            return appConfig != null ? appConfig.getHistoryMatchDirectMatchScore() : 0.9;
        }

        /**
         * 获取模式匹配分数
         *
         * @return 匹配分数
         */
        public static double getPatternMatchScore() {
            return appConfig != null ? appConfig.getHistoryMatchPatternMatchScore() : 0.85;
        }

        /**
         * 获取默认匹配分数
         *
         * @return 匹配分数
         */
        public static double getDefaultScore() {
            return appConfig != null ? appConfig.getHistoryMatchDefaultScore() : 0.7;
        }
    }

    /**
     * SQL脚本路径
     * 注意：这些路径是固定的，不需要从配置中获取
     */
    public static class SqlScriptPaths {
        /**
         * 数据库脚本根目录
         */
        public static final String DB_DIRECTORY = "db/";

        /**
         * 初始化脚本目录
         */
        public static final String INIT_DIRECTORY = DB_DIRECTORY + "init/";

        /**
         * 默认租户脚本目录
         */
        public static final String DEFAULT_DIRECTORY = DB_DIRECTORY + "default/";

        /**
         * 主初始化脚本
         */
        public static final String INIT_ALL = DB_DIRECTORY + "init-all.sql";

        /**
         * 租户初始化脚本
         */
        public static final String INIT_TENANT = INIT_DIRECTORY + "init-tenant.sql";

        /**
         * 基础数据初始化脚本
         */
        public static final String INIT_BASE_DATA = INIT_DIRECTORY + "init-base-data.sql";

        /**
         * 默认租户组织结构初始化脚本
         */
        public static final String DEFAULT_ORGANIZATION_STRUCTURE = DEFAULT_DIRECTORY
                + "init-new-organization-structure.sql";

        /**
         * 默认租户领域数据脚本
         */
        public static final String DEFAULT_DOMAINS = DEFAULT_DIRECTORY + "default-domains.sql";

        /**
         * 默认租户同义词数据脚本
         */
        public static final String DEFAULT_SYNONYMS = DEFAULT_DIRECTORY + "default-synonyms.sql";

        /**
         * 默认租户历史数据脚本
         */
        public static final String DEFAULT_HISTORY = DEFAULT_DIRECTORY + "default-history.sql";

    }

    /**
     * 通用推荐分数
     */
    public static class GenericScores {
        /**
         * 获取基于组织关系推荐的默认分数
         *
         * @return 推荐分数
         */
        public static double getOrganizationBased() {
            return appConfig != null ? appConfig.getScoreOrganizationBased() : 0.7;
        }

        /**
         * 获取基于关键字匹配的默认分数
         *
         * @return 推荐分数
         */
        public static double getKeywordBased() {
            return appConfig != null ? appConfig.getScoreKeywordBased() : 0.8;
        }

        /**
         * 获取候选账号列表推荐的默认分数
         *
         * @return 推荐分数
         */
        public static double getCandidateBased() {
            return appConfig != null ? appConfig.getScoreCandidateBased() : 0.5;
        }

        /**
         * 获取系统分析推荐的默认分数
         *
         * @return 推荐分数
         */
        public static double getSystemAnalysis() {
            return appConfig != null ? appConfig.getScoreSystemAnalysis() : 0.8;
        }
    }
}
