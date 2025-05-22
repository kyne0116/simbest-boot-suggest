package com.simbest.boot.suggest.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * 同义词管理工具类
 * 提供同义词查询和管理功能
 */
@Slf4j
public class SynonymManager {
    private static final Map<String, Set<String>> synonyms = new HashMap<>();
    private static boolean isInitialized = false;

    /**
     * 初始化同义词表
     *
     * @throws IOException 如果加载同义词文件失败
     */
    public static synchronized void initialize() throws IOException {
        if (isInitialized) {
            return;
        }

        // 从资源文件加载同义词
        InputStream is = SynonymManager.class.getResourceAsStream("/synonyms.txt");
        if (is != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        String[] words = line.split(",");
                        if (words.length > 1) {
                            for (String word : words) {
                                word = word.trim();
                                if (!word.isEmpty()) {
                                    Set<String> wordSynonyms = new HashSet<>();
                                    for (String other : words) {
                                        other = other.trim();
                                        if (!other.isEmpty() && !other.equals(word)) {
                                            wordSynonyms.add(other);
                                        }
                                    }
                                    synonyms.put(word, wordSynonyms);
                                }
                            }
                        }
                    }
                }
            }
        }

        // 添加一些常用同义词
        addCommonSynonyms();

        isInitialized = true;
    }

    /**
     * 添加常用同义词
     *
     * 注意：同义词组已迁移到数据库，该方法已不再从配置文件加载
     * 同义词组应通过DatabaseDataLoader.loadCommonSynonyms()方法加载
     */
    private static void addCommonSynonyms() {
        // 同义词组已迁移到数据库，该方法不再从配置文件加载
        log.info("同义词组已迁移到数据库，请使用DatabaseDataLoader.loadCommonSynonyms()方法加载");
    }

    /**
     * 添加同义词组
     *
     * @param synonymGroup 同义词组，格式为"word1,word2,word3"
     */
    public static void addSynonyms(String synonymGroup) {
        if (synonymGroup == null || synonymGroup.isEmpty()) {
            return;
        }

        String[] words = synonymGroup.split(",");
        if (words.length > 1) {
            for (String word : words) {
                word = word.trim();
                if (!word.isEmpty()) {
                    Set<String> wordSynonyms = synonyms.computeIfAbsent(word, k -> new HashSet<>());
                    for (String other : words) {
                        other = other.trim();
                        if (!other.isEmpty() && !other.equals(word)) {
                            wordSynonyms.add(other);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取词的所有同义词
     *
     * @param word 词语
     * @return 同义词集合
     * @throws IOException 如果初始化同义词表失败
     */
    public static Set<String> getSynonyms(String word) throws IOException {
        if (!isInitialized) {
            initialize();
        }
        return synonyms.getOrDefault(word, Collections.emptySet());
    }

    /**
     * 判断两个词是否是同义词
     *
     * @param word1 词语1
     * @param word2 词语2
     * @return 是否是同义词
     * @throws IOException 如果初始化同义词表失败
     */
    public static boolean areSynonyms(String word1, String word2) throws IOException {
        if (word1 == null || word2 == null || word1.isEmpty() || word2.isEmpty()) {
            return false;
        }

        if (word1.equals(word2)) {
            return true;
        }

        Set<String> word1Synonyms = getSynonyms(word1);
        return word1Synonyms.contains(word2);
    }
}
