package com.simbest.boot.suggest.service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simbest.boot.suggest.model.ResponsibilityDomain;
import com.simbest.boot.suggest.util.DataLoader;

/**
 * 职责领域服务
 * 提供职责领域相关的操作
 */
public class DomainService {
    private Map<String, ResponsibilityDomain> domains; // 领域ID到领域的映射
    private Map<String, List<String>> leaderDomainMap; // 领导账号到负责领域ID的映射

    /**
     * 构造函数
     */
    public DomainService() {
        this.domains = new HashMap<>();
        this.leaderDomainMap = new HashMap<>();
        initDomains();
    }

    /**
     * 初始化职责领域数据
     */
    public void initDomains() {
        // 从资源文件加载职责领域数据
        List<ResponsibilityDomain> domainList = DataLoader.loadDomains();
        for (ResponsibilityDomain domain : domainList) {
            // 从领域名称确定负责人账号
            String leaderAccount = null;
            if (domain.getDomainName().equals("网络安全")) {
                leaderAccount = "xuhyun";
            } else if (domain.getDomainName().equals("计费账务")) {
                leaderAccount = "zhangyk";
            } else if (domain.getDomainName().equals("系统管理") || domain.getDomainName().equals("数据治理")) {
                leaderAccount = "zhaobin";
            }

            addDomain(domain, leaderAccount);
        }

        System.out.println("已初始化 " + domains.size() + " 个职责领域数据");
    }

    /*
     * ResponsibilityDomain billingDomain = new ResponsibilityDomain(
     * "domain002",
     * "计费账务",
     * "张耀华",
     * "负责计费、账务、系统规划、中台运营等相关工作");billingDomain.addKeywords(Arrays.asList("计费","账务",
     * "结算","短信","营销","在线营销","短信平台","端口","扩展号码","10086","账单","计费系统","账务系统","账务调整",
     * "计费账务","软件维护","平台维护","系统维护","中台","规划","网间结算","省内结算"));
     * 
     * ResponsibilityDomain systemDomain = new ResponsibilityDomain(
     * "domain003",
     * "系统管理",
     * "赵斌",
     * "负责运营管理、管理信息系统、政企业务等相关工作");systemDomain.addKeywords(Arrays.asList("运营管理",
     * "管理信息系统","政企业务","支撑","系统建设","支撑需求","业务支撑","政企","管理系统","信息系统","系统管理","运营","建设"
     * ,"管理协调","生产经营","精益管理","协作配合","管理工作","反馈","协调","管理","协作","配合","工作","台账","管控",
     * "事项"));
     * 
     * ResponsibilityDomain dataDomain = new ResponsibilityDomain(
     * "domain004",
     * "数据治理",
     * "赵斌",
     * "负责数据治理、智能体、智算资源等相关工作");dataDomain.addKeywords(Arrays.asList("数据治理","智能体",
     * "智算资源","数智化","自主可控","AI","人工智能","灵犀","数据","治理","统计局","研发","科研档案","年报","统计",
     * "研发项目","佐证材料"));
     * 
     * // 添加职责领域
     * addDomain(securityDomain, "xuhyun");
     * addDomain(billingDomain, "zhangyk");
     * addDomain(systemDomain, "zhaobin");
     * addDomain(dataDomain, "zhaobin");
     * }
     * 
     * /**
     * 添加职责领域
     *
     * @param domain 职责领域对象
     * 
     * @param leaderAccount 负责人账号
     */
    public void addDomain(ResponsibilityDomain domain, String leaderAccount) {
        domains.put(domain.getDomainId(), domain);

        // 更新领导到职责领域的映射
        if (leaderAccount != null && !leaderAccount.isEmpty()) {
            leaderDomainMap.computeIfAbsent(leaderAccount, k -> new ArrayList<>()).add(domain.getDomainId());
        }
    }

    /**
     * 根据领域ID获取职责领域
     *
     * @param domainId 领域ID
     * @return 职责领域对象
     */
    public ResponsibilityDomain getDomainById(String domainId) {
        return domains.get(domainId);
    }

    /**
     * 根据领导账号获取负责的职责领域ID列表
     *
     * @param leaderAccount 领导账号
     * @return 职责领域ID列表
     */
    public List<String> getDomainIdsByLeaderAccount(String leaderAccount) {
        return leaderDomainMap.getOrDefault(leaderAccount, new ArrayList<>());
    }

    /**
     * 获取所有职责领域
     *
     * @return 所有职责领域的列表
     */
    public List<ResponsibilityDomain> getAllDomains() {
        return new ArrayList<>(domains.values());
    }

    /**
     * 根据文本内容匹配最佳职责领域
     *
     * @param text 文本内容
     * @return 最佳匹配的职责领域ID和匹配分数的映射
     */
    public AbstractMap.SimpleEntry<String, Double> getBestMatchDomain(String text) {
        String bestMatchDomainId = null;
        double bestMatchScore = 0.0;

        for (ResponsibilityDomain domain : domains.values()) {
            double score = domain.calculateMatchScore(text);
            if (score > bestMatchScore) {
                bestMatchScore = score;
                bestMatchDomainId = domain.getDomainId();
            }
        }

        if (bestMatchDomainId != null && bestMatchScore > 0.0) {
            return new AbstractMap.SimpleEntry<>(bestMatchDomainId, bestMatchScore);
        }

        return null;
    }

    /**
     * 获取所有匹配的职责领域
     *
     * @param text      文本内容
     * @param threshold 匹配阈值（0-1之间）
     * @return 匹配的职责领域ID和匹配分数的映射
     */
    public Map<String, Double> getAllMatchDomains(String text, double threshold) {
        Map<String, Double> matchDomains = new HashMap<>();

        for (ResponsibilityDomain domain : domains.values()) {
            double score = domain.calculateMatchScore(text);
            if (score >= threshold) {
                matchDomains.put(domain.getDomainId(), score);
            }
        }

        return matchDomains;
    }
}
