package com.simbest.boot.suggest.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simbest.boot.suggest.model.Leader;
import com.simbest.boot.suggest.model.Organization;
import com.simbest.boot.suggest.model.ResponsibilityDomain;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
                return objectMapper.readValue(is, new TypeReference<List<Organization>>() {});
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
                return objectMapper.readValue(is, new TypeReference<List<Leader>>() {});
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
                return objectMapper.readValue(is, new TypeReference<List<ResponsibilityDomain>>() {});
            }
        } catch (IOException e) {
            System.err.println("加载职责领域数据失败: " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
