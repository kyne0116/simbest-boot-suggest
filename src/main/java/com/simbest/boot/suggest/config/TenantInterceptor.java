package com.simbest.boot.suggest.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

/**
 * 租户拦截器
 * 用于在请求处理前设置租户上下文
 */
@Component
@Slf4j
public class TenantInterceptor implements HandlerInterceptor {

    /**
     * 在请求处理前设置租户上下文
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String tenantCode = request.getParameter("tenantCode");

        // 如果请求中没有租户代码，则从请求头中获取
        if (tenantCode == null || tenantCode.isEmpty()) {
            tenantCode = request.getHeader("X-Tenant-Code");
        }

        // 如果请求中和请求头中都没有租户代码，则使用默认租户代码
        if (tenantCode == null || tenantCode.isEmpty()) {
            tenantCode = DefaultValueConstants.getDefaultTenantCode();
        }

        // 设置当前租户上下文
        TenantContext.setCurrentTenant(tenantCode);
        log.debug("设置当前租户上下文: {}", tenantCode);

        return true;
    }

    /**
     * 在请求处理后清除租户上下文
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // 不做任何处理
    }

    /**
     * 在请求完成后清除租户上下文
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 清除当前租户上下文
        TenantContext.clear();
        log.debug("清除当前租户上下文");
    }
}
