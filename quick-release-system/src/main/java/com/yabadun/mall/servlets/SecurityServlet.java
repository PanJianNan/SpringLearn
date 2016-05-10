package com.yabadun.mall.servlets;

import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * SecurityServlet
 *
 * @author panjn
 * @date 2016/5/4
 */
public class SecurityServlet extends HttpServlet implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(true);
        String userCode = (String) request.getRemoteUser();// 登录人
        String userRole = (String) session.getAttribute("role");//登录人角色
        String isLogin = (String) session.getAttribute("isLogin");//登录人角色
        String url = request.getRequestURI();

        if (url.endsWith("*.jpg") || url.endsWith(".png") || url.endsWith(".gif") || url.endsWith(".js")
                || url.endsWith(".css") || url.endsWith(".ico") || url.endsWith(".woff")
                || url.endsWith(".woff2") || url.endsWith(".map")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (StringUtils.isBlank(isLogin)) {
            //判断获取的路径不为空且不是访问登录页面或执行登录操作时跳转
            if (StringUtils.isNotBlank(url) && (url.indexOf("Login") < 0 && url.indexOf("login") < 0)) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
