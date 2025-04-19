package com.simbest.boot.suggest.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simbest.boot.suggest.model.Leader;
import com.simbest.boot.suggest.model.Organization;
import com.simbest.boot.suggest.model.ResponsibilityDomain;

/**
 * 数据加载器
 * 负责从资源文件加载初始化数据
 */
public class DataLoader {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 加载组织数据
     *
     * @return 组织列表
     */
    public static List<Organization> loadOrganizations() {
        try {
            InputStream is = DataLoader.class.getResourceAsStream("/data/organizations.json");
            if (is != null) {
                return objectMapper.readValue(is, new TypeReference<List<Organization>>() {
                });
            }
        } catch (IOException e) {
            System.err.println("加载组织数据失败: " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 加载领导数据
     *
     * @return 领导列表
     */
    public static List<Leader> loadLeaders() {
        try {
            InputStream is = DataLoader.class.getResourceAsStream("/data/leaders.json");
            if (is != null) {
                return objectMapper.readValue(is, new TypeReference<List<Leader>>() {
                });
            }
        } catch (IOException e) {
            System.err.println("加载领导数据失败: " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 加载职责领域数据
     *
     * @return 职责领域列表
     */
    public static List<ResponsibilityDomain> loadDomains() {
        try {
            InputStream is = DataLoader.class.getResourceAsStream("/data/domains.json");
            if (is != null) {
                return objectMapper.readValue(is, new TypeReference<List<ResponsibilityDomain>>() {
                });
            }
        } catch (IOException e) {
            System.err.println("加载职责领域数据失败: " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 加载领域到领导账号的映射
     *
     * @return 领域名称到领导账号的映射
     */
    public static Map<String, String> loadDomainLeaderMapping() {
        try {
            InputStream is = DataLoader.class.getResourceAsStream("/config/domain-leader-mapping.json");
            if (is != null) {
                List<Map<String, String>> mappingList = objectMapper.readValue(is,
                        new TypeReference<List<Map<String, String>>>() {
                        });
                Map<String, String> result = new HashMap<>();
                for (Map<String, String> mapping : mappingList) {
                    result.put(mapping.get("domainName"), mapping.get("leaderAccount"));
                }
                System.out.println("已加载 " + result.size() + " 个领域到领导的映射");
                return result;
            }
        } catch (IOException e) {
            System.err.println("加载领域到领导映射失败: " + e.getMessage());
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    /**
     * 加载常用词列表
     *
     * @return 常用词列表
     */
    public static List<String> loadCommonWords() {
        try {
            InputStream is = DataLoader.class.getResourceAsStream("/config/common-words.json");
            if (is != null) {
                List<String> words = objectMapper.readValue(is, new TypeReference<List<String>>() {
                });
                System.out.println("已加载 " + words.size() + " 个常用词");
                return words;
            }
        } catch (IOException e) {
            System.err.println("加载常用词失败: " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 加载常用同义词组
     *
     * @return 同义词组列表
     */
    public static List<String> loadCommonSynonyms() {
        try {
            InputStream is = DataLoader.class.getResourceAsStream("/config/common-synonyms.json");
            if (is != null) {
                List<Map<String, Object>> synonymsData = objectMapper.readValue(is,
                        new TypeReference<List<Map<String, Object>>>() {
                        });
                List<String> result = new ArrayList<>();

                for (Map<String, Object> category : synonymsData) {
                    @SuppressWarnings("unchecked")
                    List<String> synonymGroups = (List<String>) category.get("synonymGroups");
                    if (synonymGroups != null) {
                        result.addAll(synonymGroups);
                    }
                }

                System.out.println("已加载 " + result.size() + " 个同义词组");
                return result;
            }
        } catch (IOException e) {
            System.err.println("加载同义词组失败: " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 加载阈值配置
     *
     * @return 阈值配置
     */
    public static Map<String, Object> loadThresholdConfig() {
        try {
            InputStream is = DataLoader.class.getResourceAsStream("/config/threshold-config.json");
            if (is != null) {
                Map<String, Object> config = objectMapper.readValue(is, new TypeReference<Map<String, Object>>() {
                });
                System.out.println("已加载阈值配置");
                return config;
            }
        } catch (IOException e) {
            System.err.println("加载阈值配置失败: " + e.getMessage());
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
