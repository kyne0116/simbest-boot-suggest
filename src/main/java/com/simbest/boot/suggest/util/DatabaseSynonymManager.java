package com.simbest.boot.suggest.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simbest.boot.suggest.config.DefaultValueConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据库同义词管理工具类
 * 提供从数据库加载同义词的功能
 */
@Component
@Slf4j
public class DatabaseSynonymManager {
    private final Map<String, Set<String>> synonyms = new HashMap<>();
    private boolean isInitialized = false;

    @Autowired
    private DatabaseDataLoader databaseDataLoader;

    private String tenantCode = DefaultValueConstants.getDefaultTenantCode(); // 默认租户代码

    /**
     * 初始化同义词表
     */
    public synchronized void initialize() {
        if (isInitialized) {
            return;
        }

        // 从资源文件加载同义词
        InputStream is = getClass().getResourceAsStream("/synonyms.txt");
        if (is != null) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
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
            } catch (IOException e) {
                log.error("加载同义词文件失败: {}", e.getMessage());
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        log.error("关闭同义词文件读取器失败: {}", e.getMessage());
                    }
                }
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("关闭同义词文件输入流失败: {}", e.getMessage());
                }
            }
        }

        // 添加从数据库加载的同义词
        addDatabaseSynonyms();

        isInitialized = true;
        log.info("同义词表初始化完成，共加载 {} 个词条", synonyms.size());
    }

    /**
     * 添加从数据库加载的同义词
     */
    private void addDatabaseSynonyms() {
        // 从数据库加载同义词组
        List<String> synonymGroups = databaseDataLoader.loadCommonSynonyms(tenantCode);
        log.info("从数据库加载了 {} 个同义词组", synonymGroups.size());

        // 添加同义词组
        for (String group : synonymGroups) {
            addSynonyms(group);
        }
    }

    /**
     * 添加同义词组
     *
     * @param synonymGroup 同义词组，格式为"word1,word2,word3"
     */
    public void addSynonyms(String synonymGroup) {
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
     */
    public Set<String> getSynonyms(String word) {
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
     */
    public boolean areSynonyms(String word1, String word2) {
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
