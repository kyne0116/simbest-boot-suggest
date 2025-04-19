package com.simbest.boot.suggest.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.simbest.boot.suggest.util.ChineseTokenizer;
import com.simbest.boot.suggest.util.DataLoader;

/**
 * 文本相似度工具类
 * 提供计算文本相似度的方法
 */
public class TextSimilarityUtil {

    /**
     * 计算Jaccard相似度
     * Jaccard相似度 = 交集大小 / 并集大小
     *
     * @param text1 文本1
     * @param text2 文本2
     * @return Jaccard相似度（0-1之间）
     */
    public static double calculateJaccardSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null || text1.isEmpty() || text2.isEmpty()) {
            return 0.0;
        }

        // 将文本转换为字符集合
        Set<Character> set1 = new HashSet<>();
        Set<Character> set2 = new HashSet<>();

        for (char c : text1.toCharArray()) {
            set1.add(c);
        }

        for (char c : text2.toCharArray()) {
            set2.add(c);
        }

        // 计算交集大小
        Set<Character> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        // 计算并集大小
        Set<Character> union = new HashSet<>(set1);
        union.addAll(set2);

        // 计算Jaccard相似度
        return (double) intersection.size() / union.size();
    }

    /**
     * 计算余弦相似度
     * 余弦相似度 = 向量点积 / (向量1范数 * 向量2范数)
     *
     * @param text1 文本1
     * @param text2 文本2
     * @return 余弦相似度（0-1之间）
     */
    public static double calculateCosineSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null || text1.isEmpty() || text2.isEmpty()) {
            return 0.0;
        }

        // 将文本转换为词频向量
        Map<Character, Integer> vector1 = new HashMap<>();
        Map<Character, Integer> vector2 = new HashMap<>();

        for (char c : text1.toCharArray()) {
            vector1.put(c, vector1.getOrDefault(c, 0) + 1);
        }

        for (char c : text2.toCharArray()) {
            vector2.put(c, vector2.getOrDefault(c, 0) + 1);
        }

        // 计算向量点积
        double dotProduct = 0.0;
        for (char c : vector1.keySet()) {
            if (vector2.containsKey(c)) {
                dotProduct += vector1.get(c) * vector2.get(c);
            }
        }

        // 计算向量范数
        double norm1 = 0.0;
        for (int count : vector1.values()) {
            norm1 += count * count;
        }
        norm1 = Math.sqrt(norm1);

        double norm2 = 0.0;
        for (int count : vector2.values()) {
            norm2 += count * count;
        }
        norm2 = Math.sqrt(norm2);

        // 计算余弦相似度
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        return dotProduct / (norm1 * norm2);
    }

    /**
     * 计算Levenshtein距离相似度
     * Levenshtein距离是两个字符串之间的最小编辑距离
     * 相似度 = 1 - 距离 / max(text1.length, text2.length)
     *
     * @param text1 文本1
     * @param text2 文本2
     * @return Levenshtein相似度（0-1之间）
     */
    public static double calculateLevenshteinSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null) {
            return 0.0;
        }

        if (text1.equals(text2)) {
            return 1.0;
        }

        if (text1.isEmpty()) {
            return 0.0;
        }

        if (text2.isEmpty()) {
            return 0.0;
        }

        // 计算Levenshtein距离
        int[][] distance = new int[text1.length() + 1][text2.length() + 1];

        for (int i = 0; i <= text1.length(); i++) {
            distance[i][0] = i;
        }

        for (int j = 0; j <= text2.length(); j++) {
            distance[0][j] = j;
        }

        for (int i = 1; i <= text1.length(); i++) {
            for (int j = 1; j <= text2.length(); j++) {
                int cost = (text1.charAt(i - 1) == text2.charAt(j - 1)) ? 0 : 1;
                distance[i][j] = Math.min(
                        Math.min(distance[i - 1][j] + 1, distance[i][j - 1] + 1),
                        distance[i - 1][j - 1] + cost);
            }
        }

        // 计算相似度
        int maxLength = Math.max(text1.length(), text2.length());
        return 1.0 - (double) distance[text1.length()][text2.length()] / maxLength;
    }

    /**
     * 计算综合相似度
     * 结合多种相似度算法，取加权平均值
     *
     * @param text1 文本1
     * @param text2 文本2
     * @return 综合相似度（0-1之间）
     */
    public static double calculateOverallSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null || text1.isEmpty() || text2.isEmpty()) {
            return 0.0;
        }

        // 从配置文件中加载字符级相似度算法的权重
        Map<String, Object> characterLevelConfig = DataLoader.getAlgorithmWeightSection("textSimilarity");
        @SuppressWarnings("unchecked")
        Map<String, Object> characterWeights = characterLevelConfig.containsKey("characterLevel")
                ? (Map<String, Object>) characterLevelConfig.get("characterLevel")
                : new HashMap<>();

        // 获取各算法权重
        double jaccardWeight = characterWeights.containsKey("jaccardWeight")
                ? ((Number) characterWeights.get("jaccardWeight")).doubleValue()
                : 0.3;
        double cosineWeight = characterWeights.containsKey("cosineWeight")
                ? ((Number) characterWeights.get("cosineWeight")).doubleValue()
                : 0.4;
        double levenshteinWeight = characterWeights.containsKey("levenshteinWeight")
                ? ((Number) characterWeights.get("levenshteinWeight")).doubleValue()
                : 0.3;

        double jaccardSimilarity = calculateJaccardSimilarity(text1, text2);
        double cosineSimilarity = calculateCosineSimilarity(text1, text2);
        double levenshteinSimilarity = calculateLevenshteinSimilarity(text1, text2);

        // 加权平均
        return jaccardWeight * jaccardSimilarity + cosineWeight * cosineSimilarity
                + levenshteinWeight * levenshteinSimilarity;
    }

    /**
     * 计算基于分词的Jaccard相似度
     *
     * @param text1 文本1
     * @param text2 文本2
     * @return 基于分词的Jaccard相似度（0-1之间）
     */
    public static double calculateTokenJaccardSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null || text1.isEmpty() || text2.isEmpty()) {
            return 0.0;
        }

        // 分词
        List<String> tokens1 = ChineseTokenizer.tokenize(text1);
        List<String> tokens2 = ChineseTokenizer.tokenize(text2);

        // 转换为集合
        Set<String> set1 = new HashSet<>(tokens1);
        Set<String> set2 = new HashSet<>(tokens2);

        // 计算交集大小
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        // 计算并集大小
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        // 计算Jaccard相似度
        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    /**
     * 计算基于分词的余弦相似度
     *
     * @param text1 文本1
     * @param text2 文本2
     * @return 基于分词的余弦相似度（0-1之间）
     */
    public static double calculateTokenCosineSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null || text1.isEmpty() || text2.isEmpty()) {
            return 0.0;
        }

        // 分词
        List<String> tokens1 = ChineseTokenizer.tokenize(text1);
        List<String> tokens2 = ChineseTokenizer.tokenize(text2);

        // 计算词频
        Map<String, Integer> vector1 = new HashMap<>();
        for (String token : tokens1) {
            vector1.put(token, vector1.getOrDefault(token, 0) + 1);
        }

        Map<String, Integer> vector2 = new HashMap<>();
        for (String token : tokens2) {
            vector2.put(token, vector2.getOrDefault(token, 0) + 1);
        }

        // 计算向量点积
        double dotProduct = 0.0;
        for (String token : vector1.keySet()) {
            if (vector2.containsKey(token)) {
                dotProduct += vector1.get(token) * vector2.get(token);
            }
        }

        // 计算向量范数
        double norm1 = 0.0;
        for (int count : vector1.values()) {
            norm1 += count * count;
        }
        norm1 = Math.sqrt(norm1);

        double norm2 = 0.0;
        for (int count : vector2.values()) {
            norm2 += count * count;
        }
        norm2 = Math.sqrt(norm2);

        // 计算余弦相似度
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        return dotProduct / (norm1 * norm2);
    }

    /**
     * 计算基于分词的综合相似度
     * 结合多种基于分词的相似度算法，取加权平均值
     *
     * @param text1 文本1
     * @param text2 文本2
     * @return 基于分词的综合相似度（0-1之间）
     */
    public static double calculateTokenOverallSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null || text1.isEmpty() || text2.isEmpty()) {
            return 0.0;
        }

        // 从配置文件中加载词语级相似度算法的权重
        Map<String, Object> textSimilarityConfig = DataLoader.getAlgorithmWeightSection("textSimilarity");
        @SuppressWarnings("unchecked")
        Map<String, Object> tokenWeights = textSimilarityConfig.containsKey("tokenLevel")
                ? (Map<String, Object>) textSimilarityConfig.get("tokenLevel")
                : new HashMap<>();

        // 获取各算法权重
        double jaccardWeight = tokenWeights.containsKey("jaccardWeight")
                ? ((Number) tokenWeights.get("jaccardWeight")).doubleValue()
                : 0.35;
        double cosineWeight = tokenWeights.containsKey("cosineWeight")
                ? ((Number) tokenWeights.get("cosineWeight")).doubleValue()
                : 0.45;
        double levenshteinWeight = tokenWeights.containsKey("levenshteinWeight")
                ? ((Number) tokenWeights.get("levenshteinWeight")).doubleValue()
                : 0.2;

        double tokenJaccardSimilarity = calculateTokenJaccardSimilarity(text1, text2);
        double tokenCosineSimilarity = calculateTokenCosineSimilarity(text1, text2);
        double levenshteinSimilarity = calculateLevenshteinSimilarity(text1, text2);

        // 加权平均
        return jaccardWeight * tokenJaccardSimilarity + cosineWeight * tokenCosineSimilarity
                + levenshteinWeight * levenshteinSimilarity;
    }

    /**
     * 计算最终综合相似度
     * 结合字符级和词语级相似度，取加权平均值
     *
     * @param text1 文本1
     * @param text2 文本2
     * @return 最终综合相似度（0-1之间）
     */
    public static double calculateFinalSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null || text1.isEmpty() || text2.isEmpty()) {
            return 0.0;
        }

        // 从配置文件中加载最终综合相似度的权重
        Map<String, Object> textSimilarityConfig = DataLoader.getAlgorithmWeightSection("textSimilarity");
        @SuppressWarnings("unchecked")
        Map<String, Object> finalWeights = textSimilarityConfig.containsKey("finalCombination")
                ? (Map<String, Object>) textSimilarityConfig.get("finalCombination")
                : new HashMap<>();

        // 获取各级别相似度的权重
        double characterLevelWeight = finalWeights.containsKey("characterLevelWeight")
                ? ((Number) finalWeights.get("characterLevelWeight")).doubleValue()
                : 0.4;
        double tokenLevelWeight = finalWeights.containsKey("tokenLevelWeight")
                ? ((Number) finalWeights.get("tokenLevelWeight")).doubleValue()
                : 0.6;

        double overallSimilarity = calculateOverallSimilarity(text1, text2);
        double tokenOverallSimilarity = calculateTokenOverallSimilarity(text1, text2);

        // 加权平均
        return characterLevelWeight * overallSimilarity + tokenLevelWeight * tokenOverallSimilarity;
    }
}
