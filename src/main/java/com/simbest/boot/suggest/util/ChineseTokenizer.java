package com.simbest.boot.suggest.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * 简单中文分词工具类
 * 使用最大正向匹配算法进行中文分词
 */
@Slf4j
public class ChineseTokenizer {
    private static final Set<String> dictionary = new HashSet<>();
    private static int maxWordLength = 10; // 默认最大词长
    private static boolean isInitialized = false;

    /**
     * 初始化词典
     */
    public static synchronized void initialize() {
        if (isInitialized) {
            return;
        }

        // 从配置文件中加载分词器配置
        Map<String, Object> tokenizerConfig = DataLoader.getAlgorithmWeightSection("tokenizer");
        if (tokenizerConfig.containsKey("maxWordLength")) {
            maxWordLength = ((Number) tokenizerConfig.get("maxWordLength")).intValue();
            log.info("已加载分词器最大词长配置: {}", maxWordLength);
        }

        // 从资源文件加载词典
        InputStream is = ChineseTokenizer.class.getResourceAsStream("/dictionary.txt");
        if (is != null) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        dictionary.add(line);
                    }
                }
            } catch (IOException e) {
                log.error("加载词典文件失败: {}", e.getMessage());
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        log.error("关闭词典文件读取器失败: {}", e.getMessage());
                    }
                }
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("关闭词典文件输入流失败: {}", e.getMessage());
                }
            }
        }

        // 添加一些常用词
        addCommonWords();

        isInitialized = true;
    }

    /**
     * 添加常用词
     *
     * 注意：常用词已迁移到数据库，该方法已不再从配置文件加载
     * 常用词应通过DatabaseDataLoader.loadCommonWords()方法加载
     */
    private static void addCommonWords() {
        // 常用词已迁移到数据库，该方法不再从配置文件加载
        log.info("常用词已迁移到数据库，请使用DatabaseDataLoader.loadCommonWords()方法加载");
    }

    /**
     * 添加词语到词典
     *
     * @param word 词语
     */
    public static void addWord(String word) {
        if (word != null && !word.isEmpty()) {
            dictionary.add(word);
        }
    }

    /**
     * 添加多个词语到词典
     *
     * @param words 词语列表
     */
    public static void addWords(List<String> words) {
        if (words != null) {
            for (String word : words) {
                addWord(word);
            }
        }
    }

    /**
     * 分词
     *
     * @param text 输入文本
     * @return 分词结果
     */
    public static List<String> tokenize(String text) {
        if (!isInitialized) {
            initialize();
        }

        List<String> result = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return result;
        }

        // 最大正向匹配算法
        int start = 0;
        while (start < text.length()) {
            // 尝试最长匹配
            boolean found = false;
            for (int end = Math.min(start + maxWordLength, text.length()); end > start; end--) {
                String word = text.substring(start, end);
                if (dictionary.contains(word)) {
                    result.add(word);
                    start = end;
                    found = true;
                    break;
                }
            }

            // 如果没有匹配到词，则按字符处理
            if (!found) {
                result.add(String.valueOf(text.charAt(start)));
                start++;
            }
        }

        return result;
    }

    /**
     * 判断文本是否包含指定词语（精确匹配）
     *
     * @param text 文本
     * @param word 词语
     * @return 是否包含
     */
    public static boolean containsWord(String text, String word) {
        if (text == null || word == null || text.isEmpty() || word.isEmpty()) {
            return false;
        }

        List<String> tokens = tokenize(text);
        return tokens.contains(word);
    }
}
