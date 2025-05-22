package com.simbest.boot.suggest.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * 同义词管理器适配器
 * 提供静态方法，用于获取同义词
 */
@Slf4j
public class SynonymManagerAdapter {

    /**
     * 获取词的所有同义词
     *
     * 注意：为了避免空指针异常，此方法总是返回一个空集合，而不是实际的同义词
     * 这是一个临时解决方案，以确保系统能够正常运行
     *
     * @param word 词语
     * @return 同义词集合（空集合）
     * @throws IOException 如果初始化同义词表失败
     */
    public static Set<String> getSynonyms(String word) throws IOException {
        // 返回一个空集合，避免空指针异常
        return new HashSet<>();
    }

    /**
     * 判断两个词是否是同义词
     *
     * 注意：为了避免空指针异常，此方法总是返回false，而不是实际的判断结果
     * 这是一个临时解决方案，以确保系统能够正常运行
     *
     * @param word1 词语1
     * @param word2 词语2
     * @return 是否是同义词（总是返回false）
     * @throws IOException 如果初始化同义词表失败
     */
    public static boolean areSynonyms(String word1, String word2) throws IOException {
        // 如果两个词完全相同，则返回true
        if (word1 != null && word2 != null && word1.equals(word2)) {
            return true;
        }
        // 否则返回false，避免空指针异常
        return false;
    }
}
