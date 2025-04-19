package com.simbest.boot.suggest.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

        double jaccardSimilarity = calculateJaccardSimilarity(text1, text2);
        double cosineSimilarity = calculateCosineSimilarity(text1, text2);
        double levenshteinSimilarity = calculateLevenshteinSimilarity(text1, text2);

        // 加权平均
        return 0.3 * jaccardSimilarity + 0.4 * cosineSimilarity + 0.3 * levenshteinSimilarity;
    }
}
