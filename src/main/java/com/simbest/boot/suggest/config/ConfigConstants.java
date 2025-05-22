package com.simbest.boot.suggest.config;

/**
 * 配置常量类
 * 定义所有配置项的键名，避免硬编码字符串
 */
public class ConfigConstants {

    /**
     * 默认租户代码
     */
    public static final String DEFAULT_TENANT = "default";

    /**
     * 配置类别
     */
    public static class Category {
        public static final String THRESHOLD = "threshold";
        public static final String ALGORITHM = "algorithm";
        public static final String AI_ANALYSIS = "ai-analysis";
        public static final String RECOMMENDATION = "recommendation";
        public static final String SYSTEM = "system";
        public static final String DISPLAY = "display";
    }

    /**
     * 阈值配置
     */
    public static class Threshold {
        public static final String BASE_THRESHOLD = "baseThreshold";
        public static final String LENGTH_THRESHOLDS = "lengthThresholds";
        public static final String CONTENT_ADJUSTMENTS = "contentAdjustments";
    }

    /**
     * 算法权重配置
     */
    public static class Algorithm {
        public static final String TEXT_SIMILARITY = "textSimilarity";
        public static final String KEYWORD_MATCHING = "keywordMatching";
        public static final String TOKENIZER = "tokenizer";
        public static final String DOMAIN_ID = "domainId";

        /**
         * 文本相似度配置
         */
        public static class TextSimilarity {
            public static final String CHARACTER_LEVEL = "characterLevel";
            public static final String TOKEN_LEVEL = "tokenLevel";
            public static final String FINAL_COMBINATION = "finalCombination";

            public static final String JACCARD_WEIGHT = "jaccardWeight";
            public static final String COSINE_WEIGHT = "cosineWeight";
            public static final String LEVENSHTEIN_WEIGHT = "levenshteinWeight";

            public static final String CHARACTER_LEVEL_WEIGHT = "characterLevelWeight";
            public static final String TOKEN_LEVEL_WEIGHT = "tokenLevelWeight";
        }

        /**
         * 关键词匹配配置
         */
        public static class KeywordMatching {
            public static final String DIRECT_MATCH_MULTIPLIER = "directMatchMultiplier";
            public static final String TOKEN_MATCH_MULTIPLIER = "tokenMatchMultiplier";
            public static final String SYNONYM_MATCH_MULTIPLIER = "synonymMatchMultiplier";
            public static final String KEYWORD_COUNT_RATIO_WEIGHT = "keywordCountRatioWeight";
            public static final String WEIGHTED_RATIO_WEIGHT = "weightedRatioWeight";
            public static final String KEYWORD_WEIGHT_BASE = "keywordWeightBase";
            public static final String KEYWORD_LENGTH_FACTOR = "keywordLengthFactor";
            public static final String KEYWORD_MAX_LENGTH = "keywordMaxLength";
        }

        /**
         * 分词器配置
         */
        public static class Tokenizer {
            public static final String MAX_WORD_LENGTH = "maxWordLength";
        }

        /**
         * 领域ID配置
         */
        public static class DomainId {
            public static final String PREFIX = "prefix";
        }
    }

    /**
     * AI分析配置
     */
    public static class AIAnalysis {
        public static final String SEMANTIC_COMPLEXITY = "semanticComplexity";
        public static final String SENTIMENT = "sentiment";
        public static final String LEADER_MATCH = "leaderMatch";
        public static final String HISTORICAL_EFFICIENCY = "historicalEfficiency";
        public static final String DOMAIN_EXPERTISE = "domainExpertise";

        /**
         * 语义复杂度配置
         */
        public static class SemanticComplexity {
            public static final String BASE_VALUE = "baseValue";
            public static final String WORD_LENGTH_FACTOR = "wordLengthFactor";
            public static final String WORD_COUNT_FACTOR = "wordCountFactor";
            public static final String MAX_COMPLEXITY = "maxComplexity";
        }

        /**
         * 情感分析配置
         */
        public static class Sentiment {
            public static final String POSITIVE = "positive";
            public static final String NEUTRAL = "neutral";
            public static final String NEGATIVE = "negative";
            public static final String URGENT = "urgent";

            public static final String BASE_VALUE = "baseValue";
            public static final String RANDOM_RANGE = "randomRange";
            public static final String KEYWORDS = "keywords";
            public static final String MATCHED_BASE_VALUE = "matchedBaseValue";
        }
    }

    /**
     * 推荐配置
     */
    public static class Recommendation {
        public static final String ORGANIZATION_MATCH = "organizationMatch";
        public static final String DOMAIN_MATCH = "domainMatch";
        public static final String HISTORY_MATCH = "historyMatch";
        public static final String TEXT_SIMILARITY_MATCH = "textSimilarityMatch";
        public static final String RECOMMENDATION_REASONS = "recommendationReasons";

        /**
         * 组织匹配配置
         */
        public static class OrganizationMatch {
            public static final String MAIN_LEADER_TO_DEPUTY_SCORE = "mainLeaderToDeputyScore";
            public static final String DEPUTY_LEADER_TO_MAIN_SCORE = "deputyLeaderToMainScore";
            public static final String PEER_LEADER_SCORE = "peerLeaderScore";
            public static final String DEFAULT_SCORE = "defaultScore";
        }

        /**
         * 推荐原因模板配置
         */
        public static class RecommendationReasons {
            public static final String ORGANIZATION_REASON = "organizationReason";
            public static final String DOMAIN_REASON = "domainReason";
            public static final String HISTORY_REASON = "historyReason";
            public static final String TEXT_SIMILARITY_REASON = "textSimilarityReason";
        }
    }

    /**
     * 系统配置
     */
    public static class System {
        public static final String NAME = "name";
        public static final String VERSION = "version";
        public static final String DOMAIN_ID_PREFIX = "domainIdPrefix";
    }

    /**
     * 显示配置
     */
    public static class Display {
        public static final String SHOW_CONFIDENCE = "showConfidence";
        public static final String SHOW_METRICS = "showMetrics";
        public static final String USE_ADVANCED_TERMINOLOGY = "useAdvancedTerminology";
        public static final String SHOW_RECOMMENDATION_TYPE = "showRecommendationType";
        public static final String USE_DETAILED_REASON = "useDetailedReason";
        public static final String SHOW_SCORE = "showScore";
        public static final String USE_PERCENTAGE_FORMAT = "usePercentageFormat";
        public static final String USE_COLORED_OUTPUT = "useColoredOutput";
    }
}
