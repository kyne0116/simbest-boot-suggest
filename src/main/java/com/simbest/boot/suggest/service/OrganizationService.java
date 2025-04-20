package com.simbest.boot.suggest.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simbest.boot.suggest.model.Leader;
import com.simbest.boot.suggest.model.Organization;
import com.simbest.boot.suggest.util.DataLoader;

/**
 * 组织管理服务
 * 提供组织相关的操作
 */
public class OrganizationService {
    private Map<String, Organization> organizations; // 组织ID到组织的映射
    private Map<String, List<String>> mainLeaderOrgMap; // 主管领导账号到组织ID的映射
    private Map<String, List<String>> deputyLeaderOrgMap; // 分管领导账号到组织ID的映射
    private Map<String, List<String>> superiorLeaderOrgMap; // 上级领导账号到组织ID的映射
    private LeaderService leaderService; // 领导服务

    /**
     * 构造函数
     */
    public OrganizationService() {
        this.organizations = new HashMap<>();
        this.mainLeaderOrgMap = new HashMap<>();
        this.deputyLeaderOrgMap = new HashMap<>();
        this.superiorLeaderOrgMap = new HashMap<>();
        initOrganizations();
    }

    /**
     * 设置领导服务
     *
     * @param leaderService 领导服务
     */
    public void setLeaderService(LeaderService leaderService) {
        this.leaderService = leaderService;
    }

    /**
     * 初始化组织数据
     */
    public void initOrganizations() {
        // 从资源文件加载组织数据
        List<Organization> orgList = DataLoader.loadOrganizations();
        for (Organization org : orgList) {
            addOrganization(org);
        }

        System.out.println("已初始化 " + organizations.size() + " 个组织数据");
    }

    /**
     * 添加组织
     *
     * @param organization 组织对象
     */
    public void addOrganization(Organization organization) {
        organizations.put(organization.getOrgId(), organization);

        // 更新主管领导到组织的映射
        String mainLeaderAccount = organization.getMainLeaderAccount();
        if (mainLeaderAccount != null && !mainLeaderAccount.isEmpty()) {
            mainLeaderOrgMap.computeIfAbsent(mainLeaderAccount, k -> new ArrayList<>()).add(organization.getOrgId());
        }

        // 更新分管领导到组织的映射
        List<String> deputyLeaderAccounts = organization.getDeputyLeaderAccounts();
        if (deputyLeaderAccounts != null) {
            for (String deputyLeaderAccount : deputyLeaderAccounts) {
                if (deputyLeaderAccount != null && !deputyLeaderAccount.isEmpty()) {
                    deputyLeaderOrgMap.computeIfAbsent(deputyLeaderAccount, k -> new ArrayList<>())
                            .add(organization.getOrgId());
                }
            }
        }

        // 更新上级领导到组织的映射
        String superiorLeaderAccount = organization.getSuperiorLeaderAccount();
        if (superiorLeaderAccount != null && !superiorLeaderAccount.isEmpty()) {
            superiorLeaderOrgMap.computeIfAbsent(superiorLeaderAccount, k -> new ArrayList<>())
                    .add(organization.getOrgId());
        }
    }

    /**
     * 根据组织ID获取组织
     *
     * @param orgId 组织ID
     * @return 组织对象
     */
    public Organization getOrganizationById(String orgId) {
        return organizations.get(orgId);
    }

    /**
     * 根据组织ID获取主管领导账号
     *
     * @param orgId 组织ID
     * @return 主管领导账号
     */
    public String getMainLeaderAccountByOrgId(String orgId) {
        Organization org = organizations.get(orgId);
        return org != null ? org.getMainLeaderAccount() : null;
    }

    /**
     * 根据组织ID获取分管领导账号列表
     *
     * @param orgId 组织ID
     * @return 分管领导账号列表
     */
    public List<String> getDeputyLeaderAccountsByOrgId(String orgId) {
        Organization org = organizations.get(orgId);
        return org != null ? org.getDeputyLeaderAccounts() : new ArrayList<>();
    }

    /**
     * 根据组织ID获取上级领导账号
     *
     * @param orgId 组织ID
     * @return 上级领导账号
     */
    public String getSuperiorLeaderAccountByOrgId(String orgId) {
        Organization org = organizations.get(orgId);
        return org != null ? org.getSuperiorLeaderAccount() : null;
    }

    /**
     * 获取组织的上级组织
     *
     * @param orgId 组织ID
     * @return 上级组织
     */
    public Organization getParentOrganization(String orgId) {
        Organization org = getOrganizationById(orgId);
        if (org != null && org.getParentOrgId() != null) {
            return getOrganizationById(org.getParentOrgId());
        }
        return null;
    }

    /**
     * 根据领导账号获取其作为主管的组织ID列表
     *
     * @param leaderAccount 领导账号
     * @return 组织ID列表
     */
    public List<String> getOrgIdsAsMainLeader(String leaderAccount) {
        return mainLeaderOrgMap.getOrDefault(leaderAccount, new ArrayList<>());
    }

    /**
     * 根据领导账号获取其作为分管的组织ID列表
     *
     * @param leaderAccount 领导账号
     * @return 组织ID列表
     */
    public List<String> getOrgIdsAsDeputyLeader(String leaderAccount) {
        return deputyLeaderOrgMap.getOrDefault(leaderAccount, new ArrayList<>());
    }

    /**
     * 根据领导账号获取其作为上级领导的组织ID列表
     *
     * @param leaderAccount 领导账号
     * @return 组织ID列表
     */
    public List<String> getOrgIdsAsSuperiorLeader(String leaderAccount) {
        return superiorLeaderOrgMap.getOrDefault(leaderAccount, new ArrayList<>());
    }

    /**
     * 获取领导作为主管的所有组织
     *
     * @param leaderAccount 领导账号
     * @return 领导作为主管的组织列表
     */
    public List<Organization> getOrganizationsAsMainLeader(String leaderAccount) {
        List<Organization> result = new ArrayList<>();
        List<String> orgIds = getOrgIdsAsMainLeader(leaderAccount);
        for (String orgId : orgIds) {
            Organization org = getOrganizationById(orgId);
            if (org != null) {
                result.add(org);
            }
        }
        return result;
    }

    /**
     * 获取领导作为分管的所有组织
     *
     * @param leaderAccount 领导账号
     * @return 领导作为分管的组织列表
     */
    public List<Organization> getOrganizationsAsDeputyLeader(String leaderAccount) {
        List<Organization> result = new ArrayList<>();
        List<String> orgIds = getOrgIdsAsDeputyLeader(leaderAccount);
        for (String orgId : orgIds) {
            Organization org = getOrganizationById(orgId);
            if (org != null) {
                result.add(org);
            }
        }
        return result;
    }

    /**
     * 获取领导作为上级领导的所有组织
     *
     * @param leaderAccount 领导账号
     * @return 领导作为上级领导的组织列表
     */
    public List<Organization> getOrganizationsAsSuperiorLeader(String leaderAccount) {
        List<Organization> result = new ArrayList<>();
        List<String> orgIds = getOrgIdsAsSuperiorLeader(leaderAccount);
        for (String orgId : orgIds) {
            Organization org = getOrganizationById(orgId);
            if (org != null) {
                result.add(org);
            }
        }
        return result;
    }

    /**
     * 获取领导所有相关的组织（包括作为主管、分管和上级的）
     *
     * @param leaderAccount 领导账号
     * @return 领导相关的所有组织列表
     */
    public List<Organization> getAllOrganizationsForLeader(String leaderAccount) {
        List<Organization> result = new ArrayList<>();
        result.addAll(getOrganizationsAsMainLeader(leaderAccount));
        result.addAll(getOrganizationsAsDeputyLeader(leaderAccount));
        result.addAll(getOrganizationsAsSuperiorLeader(leaderAccount));
        return result;
    }

    /**
     * 判断领导是否是某个组织的主管
     *
     * @param leaderAccount 领导账号
     * @param orgId         组织ID
     * @return 是否是主管
     */
    public boolean isMainLeaderOfOrganization(String leaderAccount, String orgId) {
        Organization org = getOrganizationById(orgId);
        return org != null && leaderAccount.equals(org.getMainLeaderAccount());
    }

    /**
     * 判断领导是否是某个组织的分管
     *
     * @param leaderAccount 领导账号
     * @param orgId         组织ID
     * @return 是否是分管
     */
    public boolean isDeputyLeaderOfOrganization(String leaderAccount, String orgId) {
        Organization org = getOrganizationById(orgId);
        return org != null && org.getDeputyLeaderAccounts() != null &&
                org.getDeputyLeaderAccounts().contains(leaderAccount);
    }

    /**
     * 判断领导是否是某个组织的上级领导
     *
     * @param leaderAccount 领导账号
     * @param orgId         组织ID
     * @return 是否是上级领导
     */
    public boolean isSuperiorLeaderOfOrganization(String leaderAccount, String orgId) {
        Organization org = getOrganizationById(orgId);
        return org != null && leaderAccount.equals(org.getSuperiorLeaderAccount());
    }

    /**
     * 获取所有组织
     *
     * @return 所有组织的列表
     */
    public List<Organization> getAllOrganizations() {
        return new ArrayList<>(organizations.values());
    }

    /**
     * 获取所有主管领导账号
     *
     * @return 所有主管领导账号的列表
     */
    public List<String> getAllMainLeaderAccounts() {
        return new ArrayList<>(mainLeaderOrgMap.keySet());
    }

    /**
     * 获取所有分管领导账号
     *
     * @return 所有分管领导账号的列表
     */
    public List<String> getAllDeputyLeaderAccounts() {
        return new ArrayList<>(deputyLeaderOrgMap.keySet());
    }

    /**
     * 获取所有上级领导账号
     *
     * @return 所有上级领导账号的列表
     */
    public List<String> getAllSuperiorLeaderAccounts() {
        return new ArrayList<>(superiorLeaderOrgMap.keySet());
    }

    /**
     * 获取所有领导账号
     *
     * @return 所有领导账号的列表
     */
    public List<String> getAllLeaderAccounts() {
        List<String> result = new ArrayList<>();
        result.addAll(getAllMainLeaderAccounts());
        result.addAll(getAllDeputyLeaderAccounts());
        result.addAll(getAllSuperiorLeaderAccounts());
        return result;
    }

    /**
     * 根据任务标题找到最合适的分管领导
     *
     * @param org       组织
     * @param taskTitle 任务标题
     * @return 最合适的分管领导账号
     */
    public String findBestDeputyLeaderByTaskTitle(Organization org, String taskTitle) {
        if (taskTitle == null || taskTitle.isEmpty() ||
                org.getDeputyLeaderAccounts() == null || org.getDeputyLeaderAccounts().isEmpty() ||
                leaderService == null) {
            return null;
        }

        String bestLeaderAccount = null;
        double bestScore = 0.0;

        // 遍历所有分管领导
        for (String leaderAccount : org.getDeputyLeaderAccounts()) {
            Leader leader = leaderService.getLeaderByAccount(leaderAccount);
            if (leader == null || leader.getDomainIds() == null || leader.getDomainIds().isEmpty()) {
                continue;
            }

            // 计算该领导负责的领域与任务标题的匹配度
            double score = leaderService.calculateDomainMatchScore(leader, taskTitle);

            // 更新最佳匹配
            if (score > bestScore) {
                bestScore = score;
                bestLeaderAccount = leaderAccount;
            }
        }

        return bestLeaderAccount;
    }
}
