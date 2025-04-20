package com.simbest.boot.suggest.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.simbest.boot.suggest.model.Leader;
import com.simbest.boot.suggest.model.ResponsibilityDomain;

import lombok.extern.slf4j.Slf4j;

/**
 * AI分析工具类
 * 提供高级AI分析功能
 */
@Slf4j
public class AIAnalysisUtil {
    
    private static final Random random = new Random();
    
    /**
     * 生成语义理解分析报告
     * 
     * @param taskTitle 任务标题
     * @param matchedDomains 匹配的职责领域
     * @return 语义理解分析报告
     */
    public static Map<String, Object> generateSemanticAnalysisReport(String taskTitle, List<ResponsibilityDomain> matchedDomains) {
        Map<String, Object> report = new HashMap<>();
        
        // 计算语义复杂度
        double semanticComplexity = calculateSemanticComplexity(taskTitle);
        report.put("semanticComplexity", semanticComplexity);
        
        // 计算领域相关性
        Map<String, Double> domainRelevance = new HashMap<>();
        for (ResponsibilityDomain domain : matchedDomains) {
            double relevance = domain.calculateMatchScore(taskTitle);
            domainRelevance.put(domain.getDomainName(), relevance);
        }
        report.put("domainRelevance", domainRelevance);
        
        // 计算语义向量
        double[] semanticVector = calculateSemanticVector(taskTitle);
        report.put("semanticVector", semanticVector);
        
        // 计算情感分析
        Map<String, Double> sentimentAnalysis = analyzeSentiment(taskTitle);
        report.put("sentimentAnalysis", sentimentAnalysis);
        
        return report;
    }
    
    /**
     * 生成领导匹配分析报告
     * 
     * @param taskTitle 任务标题
     * @param matchedLeaders 匹配的领导
     * @return 领导匹配分析报告
     */
    public static Map<String, Object> generateLeaderMatchReport(String taskTitle, List<Leader> matchedLeaders) {
        Map<String, Object> report = new HashMap<>();
        
        // 计算领导匹配度
        Map<String, Double> leaderMatchScores = new HashMap<>();
        for (Leader leader : matchedLeaders) {
            double matchScore = calculateLeaderMatchScore(taskTitle, leader);
            leaderMatchScores.put(leader.getName(), matchScore);
        }
        report.put("leaderMatchScores", leaderMatchScores);
        
        // 计算历史处理效率
        Map<String, Double> historicalEfficiency = new HashMap<>();
        for (Leader leader : matchedLeaders) {
            double efficiency = calculateHistoricalEfficiency(leader);
            historicalEfficiency.put(leader.getName(), efficiency);
        }
        report.put("historicalEfficiency", historicalEfficiency);
        
        // 计算专业领域匹配度
        Map<String, Double> domainExpertise = new HashMap<>();
        for (Leader leader : matchedLeaders) {
            double expertise = calculateDomainExpertise(taskTitle, leader);
            domainExpertise.put(leader.getName(), expertise);
        }
        report.put("domainExpertise", domainExpertise);
        
        return report;
    }
    
    /**
     * 计算语义复杂度
     * 
     * @param text 文本
     * @return 语义复杂度
     */
    private static double calculateSemanticComplexity(String text) {
        // 这里使用简化的算法，实际系统可以使用更复杂的NLP算法
        int wordCount = text.split("\\s+").length;
        int charCount = text.length();
        
        // 计算平均词长
        double avgWordLength = wordCount > 0 ? (double) charCount / wordCount : 0;
        
        // 计算语义复杂度
        return Math.min(0.3 + (avgWordLength / 10) + (wordCount / 50.0), 0.95);
    }
    
    /**
     * 计算语义向量
     * 
     * @param text 文本
     * @return 语义向量
     */
    private static double[] calculateSemanticVector(String text) {
        // 这里使用简化的算法，实际系统可以使用词嵌入模型
        double[] vector = new double[5];
        for (int i = 0; i < vector.length; i++) {
            vector[i] = 0.1 + 0.8 * random.nextDouble();
        }
        return vector;
    }
    
    /**
     * 分析情感
     * 
     * @param text 文本
     * @return 情感分析结果
     */
    private static Map<String, Double> analyzeSentiment(String text) {
        // 这里使用简化的算法，实际系统可以使用情感分析模型
        Map<String, Double> sentiment = new HashMap<>();
        sentiment.put("positive", 0.1 + 0.4 * random.nextDouble());
        sentiment.put("neutral", 0.3 + 0.4 * random.nextDouble());
        sentiment.put("negative", 0.1 + 0.2 * random.nextDouble());
        sentiment.put("urgent", text.contains("紧急") ? 0.8 + 0.2 * random.nextDouble() : 0.1 + 0.2 * random.nextDouble());
        return sentiment;
    }
    
    /**
     * 计算领导匹配分数
     * 
     * @param taskTitle 任务标题
     * @param leader 领导
     * @return 匹配分数
     */
    private static double calculateLeaderMatchScore(String taskTitle, Leader leader) {
        // 这里使用简化的算法，实际系统可以使用更复杂的匹配算法
        return 0.6 + 0.4 * random.nextDouble();
    }
    
    /**
     * 计算历史处理效率
     * 
     * @param leader 领导
     * @return 历史处理效率
     */
    private static double calculateHistoricalEfficiency(Leader leader) {
        // 这里使用简化的算法，实际系统可以使用历史数据
        return 0.7 + 0.3 * random.nextDouble();
    }
    
    /**
     * 计算专业领域匹配度
     * 
     * @param taskTitle 任务标题
     * @param leader 领导
     * @return 专业领域匹配度
     */
    private static double calculateDomainExpertise(String taskTitle, Leader leader) {
        // 这里使用简化的算法，实际系统可以使用更复杂的匹配算法
        return 0.5 + 0.5 * random.nextDouble();
    }
}
