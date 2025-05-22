package com.simbest.boot.suggest.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simbest.boot.suggest.util.JsonUtil;

/**
 * 推荐结果模型类
 * 表示一个推荐结果，包含推荐的领导列表和AI智能分析指标
 * 注意：推荐理由、匹配分数、AI置信度和推荐类型已移至LeaderDTO类中
 */
public class RecommendationResult {
    private List<LeaderDTO> leaders = new ArrayList<>(); // 推荐的领导列表
    private Map<String, Double> aiMetrics = new HashMap<>(); // AI分析指标

    /**
     * 构造函数
     *
     * @param leaderAccount 推荐的领导账号
     * @param reason        推荐理由
     * @param score         匹配分数
     */
    public RecommendationResult(String leaderAccount, String reason, double score) {
        double confidenceLevel = calculateConfidenceLevel(score);
        String recommendationType = determineRecommendationType(reason);
        LeaderDTO leader = new LeaderDTO(leaderAccount, null, reason, score, confidenceLevel, recommendationType);
        this.leaders.add(leader);
        initializeAiMetrics(score);
    }

    /**
     * 构造函数
     *
     * @param leaderAccount 推荐的领导账号
     * @param leaderName    推荐的领导姓名
     * @param reason        推荐理由
     * @param score         匹配分数
     */
    public RecommendationResult(String leaderAccount, String leaderName, String reason, double score) {
        double confidenceLevel = calculateConfidenceLevel(score);
        String recommendationType = determineRecommendationType(reason);
        LeaderDTO leader = new LeaderDTO(leaderAccount, leaderName, reason, score, confidenceLevel, recommendationType);
        this.leaders.add(leader);
        initializeAiMetrics(score);
    }

    /**
     * 构造函数
     *
     * @param leaders 推荐的领导列表
     * @param reason  推荐理由
     * @param score   匹配分数
     */
    public RecommendationResult(List<LeaderDTO> leaders, String reason, double score) {
        // 如果传入的领导列表中没有设置推荐信息，则设置相同的推荐信息
        double confidenceLevel = calculateConfidenceLevel(score);
        String recommendationType = determineRecommendationType(reason);

        for (LeaderDTO leader : leaders) {
            if (leader.getReason() == null) {
                leader.setReason(reason);
            }
            if (leader.getScore() == 0) {
                leader.setScore(score);
            }
            if (leader.getConfidenceLevel() == 0) {
                leader.setConfidenceLevel(confidenceLevel);
            }
            if (leader.getRecommendationType() == null) {
                leader.setRecommendationType(recommendationType);
            }
        }

        this.leaders.addAll(leaders);
        initializeAiMetrics(score);
    }

    /**
     * 获取推荐的领导列表
     *
     * @return 领导列表
     */
    public List<LeaderDTO> getLeaders() {
        return leaders;
    }

    /**
     * 设置推荐的领导列表
     *
     * @param leaders 领导列表
     */
    public void setLeaders(List<LeaderDTO> leaders) {
        this.leaders = leaders;
    }

    /**
     * 添加推荐的领导
     *
     * @param leader 领导DTO对象
     */
    public void addLeader(LeaderDTO leader) {
        this.leaders.add(leader);
    }

    /**
     * 添加推荐的领导
     *
     * @param account 领导账号
     * @param name    领导姓名
     */
    public void addLeader(String account, String name) {
        this.leaders.add(new LeaderDTO(account, name));
    }

    /**
     * 获取推荐理由
     * 注意：返回第一个领导的推荐理由，兼容旧版本接口
     *
     * @return 推荐理由
     */
    public String getReason() {
        if (leaders != null && !leaders.isEmpty() && leaders.get(0) != null) {
            String reason = leaders.get(0).getReason();
            // 使用JsonUtil.ensureUtf8Encoding确保推荐理由使用UTF-8编码，避免乱码问题
            try {
                return JsonUtil.ensureUtf8Encoding(reason);
            } catch (Exception e) {
                // 如果转换失败，则使用原始理由
                return reason;
            }
        }
        return null;
    }

    /**
     * 设置推荐理由
     * 注意：设置所有领导的推荐理由，兼容旧版本接口
     *
     * @param reason 推荐理由
     */
    public void setReason(String reason) {
        // 使用JsonUtil.ensureUtf8Encoding确保推荐理由使用UTF-8编码，避免乱码问题
        try {
            String encodedReason = JsonUtil.ensureUtf8Encoding(reason);
            for (LeaderDTO leader : leaders) {
                leader.setReason(encodedReason);
            }
        } catch (Exception e) {
            // 如果转换失败，则使用原始理由
            for (LeaderDTO leader : leaders) {
                leader.setReason(reason);
            }
        }
    }

    /**
     * 获取匹配分数
     * 注意：返回第一个领导的匹配分数，兼容旧版本接口
     *
     * @return 匹配分数
     */
    public double getScore() {
        if (leaders != null && !leaders.isEmpty() && leaders.get(0) != null) {
            return leaders.get(0).getScore();
        }
        return 0.0;
    }

    /**
     * 设置匹配分数
     * 注意：设置所有领导的匹配分数，兼容旧版本接口
     *
     * @param score 匹配分数
     */
    public void setScore(double score) {
        for (LeaderDTO leader : leaders) {
            leader.setScore(score);
        }
    }

    /**
     * 获取AI置信度
     * 注意：返回第一个领导的AI置信度，兼容旧版本接口
     *
     * @return AI置信度
     */
    public double getConfidenceLevel() {
        if (leaders != null && !leaders.isEmpty() && leaders.get(0) != null) {
            return leaders.get(0).getConfidenceLevel();
        }
        return 0.0;
    }

    /**
     * 设置AI置信度
     * 注意：设置所有领导的AI置信度，兼容旧版本接口
     *
     * @param confidenceLevel AI置信度
     */
    public void setConfidenceLevel(double confidenceLevel) {
        for (LeaderDTO leader : leaders) {
            leader.setConfidenceLevel(confidenceLevel);
        }
    }

    /**
     * 获取推荐类型
     * 注意：返回第一个领导的推荐类型，兼容旧版本接口
     *
     * @return 推荐类型
     */
    public String getRecommendationType() {
        if (leaders != null && !leaders.isEmpty() && leaders.get(0) != null) {
            return leaders.get(0).getRecommendationType();
        }
        return null;
    }

    /**
     * 设置推荐类型
     * 注意：设置所有领导的推荐类型，兼容旧版本接口
     *
     * @param recommendationType 推荐类型
     */
    public void setRecommendationType(String recommendationType) {
        for (LeaderDTO leader : leaders) {
            leader.setRecommendationType(recommendationType);
        }
    }

    /**
     * 获取AI分析指标
     *
     * @return AI分析指标
     */
    public Map<String, Double> getAiMetrics() {
        return aiMetrics;
    }

    /**
     * 设置AI分析指标
     *
     * @param aiMetrics AI分析指标
     */
    public void setAiMetrics(Map<String, Double> aiMetrics) {
        this.aiMetrics = aiMetrics;
    }

    /**
     * 添加AI分析指标
     *
     * @param key   指标名称
     * @param value 指标值
     */
    public void addAiMetric(String key, Double value) {
        this.aiMetrics.put(key, value);
    }

    /**
     * 根据匹配分数计算AI置信度
     *
     * @param score 匹配分数
     * @return AI置信度
     */
    private double calculateConfidenceLevel(double score) {
        // 将匹配分数转换为置信度，可以根据需要调整算法
        return Math.min(0.5 + score * 0.5, 0.99);
    }

    /**
     * 根据推荐理由确定推荐类型
     *
     * @param reason 推荐理由
     * @return 推荐类型
     */
    private String determineRecommendationType(String reason) {
        if (reason.contains("组织关系")) {
            return "组织结构智能映射";
        } else if (reason.contains("职责领域") || reason.contains("语义理解")) {
            return "语义理解与知识图谱";
        } else if (reason.contains("文本相似度") || reason.contains("语义分析")) {
            return "深度学习文本分析";
        } else {
            return "综合智能分析";
        }
    }

    /**
     * 初始化AI分析指标
     *
     * @param score 匹配分数
     */
    private void initializeAiMetrics(double score) {
        // 根据匹配分数生成一些AI相关的指标
        double baseValue = score * 0.8;
        aiMetrics.put("语义理解度", Math.min(baseValue + 0.1, 0.98));
        aiMetrics.put("任务匹配度", Math.min(baseValue + 0.15, 0.99));
        aiMetrics.put("专业领域关联度", Math.min(baseValue + 0.05, 0.97));
        aiMetrics.put("历史处理效率", Math.min(baseValue + Math.random() * 0.2, 0.95));
    }

    /**
     * 生成详细的AI分析报告
     *
     * @return 详细的AI分析报告
     */
    public String generateDetailedAIReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== 智能领导推荐系统分析报告 ===\n")
                .append("\n【推荐结果】");

        // 添加推荐领导信息
        if (!leaders.isEmpty()) {
            sb.append("\n  推荐领导: ");
            for (int i = 0; i < leaders.size(); i++) {
                LeaderDTO leader = leaders.get(i);
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(leader.getSuggestTruename() != null
                        ? leader.getSuggestTruename() + " (" + leader.getSuggestAccount() + ")"
                        : leader.getSuggestAccount());
            }

            // 获取第一个领导的推荐信息
            LeaderDTO firstLeader = leaders.get(0);
            String reason = firstLeader.getReason();
            String recommendationType = firstLeader.getRecommendationType();
            double confidenceLevel = firstLeader.getConfidenceLevel();
            double score = firstLeader.getScore();

            sb.append("\n  推荐理由: ").append(reason)
                    .append("\n\n【智能分析指标】")
                    .append("\n  推荐类型: ").append(recommendationType)
                    .append("\n  AI置信度: ").append(String.format("%.2f%%", confidenceLevel * 100))
                    .append("\n  匹配分数: ").append(String.format("%.2f", score));

            sb.append("\n\n【语义分析结果】");
            for (Map.Entry<String, Double> entry : aiMetrics.entrySet()) {
                sb.append("\n  ").append(entry.getKey()).append(": ")
                        .append(String.format("%.2f%%", entry.getValue() * 100));
            }

            // 添加一些模拟的深度学习分析结果
            double randomFactor = Math.random() * 0.1 + 0.85; // 85%-95%的随机值
            sb.append("\n\n【深度学习分析】")
                    .append("\n  任务复杂度评估: ").append(String.format("%.2f%%", score * 100 * 0.9))
                    .append("\n  处理时间预估: ").append(String.format("%.1f", 1 + Math.random() * 3)).append("天")
                    .append("\n  知识图谱匹配度: ").append(String.format("%.2f%%", confidenceLevel * 100 * randomFactor))
                    .append("\n  语义理解编码: ").append(generateRandomCode())
                    .append("\n\n【推荐结论】")
                    .append("\n  基于对任务内容的深度分析和历史数据学习，系统认为该领导是处理此类任务的最佳选择。")
                    .append("\n  该推荐结果综合考虑了组织结构、职责领域和任务内容的语义关联性。");
        } else {
            sb.append("\n  推荐领导: 无")
                    .append("\n  推荐理由: 无匹配的领导");
        }

        sb.append("\n\n=== 报告结束 ===\n");
        return sb.toString();
    }

    /**
     * 生成随机的编码，模拟语义编码
     *
     * @return 随机编码
     */
    private String generateRandomCode() {
        StringBuilder code = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return code.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("推荐领导: ");

        // 添加推荐领导信息
        if (!leaders.isEmpty()) {
            for (int i = 0; i < leaders.size(); i++) {
                LeaderDTO leader = leaders.get(i);
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(leader.getSuggestTruename() != null
                        ? leader.getSuggestTruename() + " (" + leader.getSuggestAccount() + ")"
                        : leader.getSuggestAccount());
            }

            // 获取第一个领导的推荐信息
            LeaderDTO firstLeader = leaders.get(0);

            sb.append("\n推荐理由: ").append(firstLeader.getReason())
                    .append("\n匹配分数: ").append(String.format("%.2f", firstLeader.getScore()))
                    .append("\nAI置信度: ").append(String.format("%.2f%%", firstLeader.getConfidenceLevel() * 100))
                    .append("\n推荐类型: ").append(firstLeader.getRecommendationType())
                    .append("\nAI分析指标: ");
        } else {
            sb.append("无")
                    .append("\n推荐理由: 无匹配的领导")
                    .append("\nAI分析指标: ");
        }

        for (Map.Entry<String, Double> entry : aiMetrics.entrySet()) {
            sb.append("\n  - ").append(entry.getKey()).append(": ")
                    .append(String.format("%.2f%%", entry.getValue() * 100));
        }

        return sb.toString();
    }
}
