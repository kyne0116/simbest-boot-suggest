package com.simbest.boot.suggest.demo;

import java.util.List;
import java.util.Scanner;

import com.simbest.boot.suggest.model.Leader;
import com.simbest.boot.suggest.model.Organization;
import com.simbest.boot.suggest.model.RecommendationResult;
import com.simbest.boot.suggest.model.ResponsibilityDomain;
import com.simbest.boot.suggest.service.DomainService;
import com.simbest.boot.suggest.service.LeaderService;
import com.simbest.boot.suggest.service.OrganizationService;
import com.simbest.boot.suggest.service.RecommendationService;

/**
 * 领导推荐演示程序
 */
public class LeaderRecommendationDemo {

    public static void main(String[] args) {
        // 创建服务
        OrganizationService organizationService = new OrganizationService();
        LeaderService leaderService = new LeaderService();
        DomainService domainService = new DomainService();
        RecommendationService recommendationService = new RecommendationService(
                organizationService, leaderService, domainService);

        // 打印欢迎信息
        System.out.println("=== 领导推荐系统演示 ===");
        System.out.println("本系统根据当前办理人账号、组织和任务标题，推荐最合适的领导账号");
        System.out.println("输入'exit'退出程序");
        System.out.println();

        // 打印组织信息
        System.out.println("=== 组织信息 ===");
        List<Organization> organizations = organizationService.getAllOrganizations();
        for (Organization org : organizations) {
            System.out.println(org.getOrgId() + ": " + org.getOrgName() +
                    " (主管领导: " + org.getMainLeaderAccount() + ", 上级领导: " +
                    org.getSuperiorLeaderAccount() + ")");
        }
        System.out.println();

        // 打印领导信息
        System.out.println("=== 领导信息 ===");
        List<Leader> leaders = leaderService.getAllLeaders();
        for (Leader leader : leaders) {
            System.out.println(leader.getAccount() + ": " + leader.getName());

            // 打印主管组织
            System.out.print("  主管组织: ");
            List<Organization> mainOrgs = organizationService.getOrganizationsAsMainLeader(leader.getAccount());
            for (Organization org : mainOrgs) {
                System.out.print(org.getOrgName() + "(" + org.getOrgId() + ") ");
            }
            System.out.println();

            // 打印分管组织
            System.out.print("  分管组织: ");
            List<Organization> deputyOrgs = organizationService.getOrganizationsAsDeputyLeader(leader.getAccount());
            for (Organization org : deputyOrgs) {
                System.out.print(org.getOrgName() + "(" + org.getOrgId() + ") ");
            }
            System.out.println();

            // 打印职责领域
            System.out.print("  职责领域: ");
            for (String domainId : leader.getDomainIds()) {
                ResponsibilityDomain domain = domainService.getDomainById(domainId);
                if (domain != null) {
                    System.out.print(domain.getDomainName() + "(" + domainId + ") ");
                }
            }
            System.out.println();
            System.out.println();
        }

        // 创建扫描器读取用户输入
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // 获取当前办理人账号
            System.out.print("请输入当前办理人账号 (例如: user001): ");
            String currentUserAccount = scanner.nextLine().trim();

            // 检查是否退出
            if ("exit".equalsIgnoreCase(currentUserAccount)) {
                break;
            }

            // 获取当前办理人组织ID
            System.out.print("请输入当前办理人组织ID (例如: org001): ");
            String currentUserOrgId = scanner.nextLine().trim();

            // 检查是否退出
            if ("exit".equalsIgnoreCase(currentUserOrgId)) {
                break;
            }

            // 获取任务标题
            System.out.print("请输入任务标题: ");
            String taskTitle = scanner.nextLine().trim();

            // 检查是否退出
            if ("exit".equalsIgnoreCase(taskTitle)) {
                break;
            }

            // 如果输入为空，继续下一次循环
            if (currentUserAccount.isEmpty() || currentUserOrgId.isEmpty() || taskTitle.isEmpty()) {
                System.out.println("输入不能为空，请重新输入");
                continue;
            }

            System.out.println("\n=== 推荐结果 ===");

            // 获取推荐结果
            RecommendationResult result = recommendationService.recommendLeader(
                    currentUserAccount, currentUserOrgId, taskTitle);

            // 显示推荐结果
            if (result != null) {
                System.out.println(result);
            } else {
                System.out.println("没有找到推荐结果");
            }

            System.out.println("\n");
        }

        scanner.close();
        System.out.println("程序已退出");
    }
}
