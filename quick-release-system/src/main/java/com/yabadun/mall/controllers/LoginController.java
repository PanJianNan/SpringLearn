package com.yabadun.mall.controllers;

import com.yabadun.ModelAndView;
import com.yabadun.mall.bean.User;
import com.yabadun.mall.service.SecurityService;
import com.yabadun.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录Controller
 *
 * @author panjn
 * @date 2016/2/24
 */
@RequestMapping(value = "login")
public class LoginController {
    public SecurityService securityService;

    @RequestMapping
    public ModelAndView index (HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView("/WEB-INF/views/login.jsp");
        return modelAndView;
    }

    @RequestMapping(value = "/auth")
    public ModelAndView auth (HttpServletRequest request) {
        User user = new User();
        user.setAccount(request.getParameter("account"));
        user.setPassword(request.getParameter("password"));
        ModelAndView modelAndView = new ModelAndView();
        if (securityService.userAuth(user)) {
            modelAndView.setView("/WEB-INF/views/success.jsp");
            modelAndView.addObject("user", user);
        } else {
            modelAndView.setView("/WEB-INF/views/login/login.jsp");
            modelAndView.addObject("message", "账号或密码错误！");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/login")
    public ModelAndView login (HttpServletRequest request) {
        User user = new User();
        user.setAccount(request.getParameter("account"));
        user.setPassword(request.getParameter("password"));
        ModelAndView modelAndView = new ModelAndView();
        if (securityService.userAuth(user)) {
            modelAndView.setView("/WEB-INF/views/login/login_success.jsp");
            modelAndView.addObject("user", user);
        } else {
            modelAndView.setView("/WEB-INF/views/login/login.jsp");
            modelAndView.addObject("message", "账号或密码错误！");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/test")
    public void voidTest (HttpServletRequest request) {
        System.out.println("welcome!");
    }

    @RequestMapping(value = "/test_str")
    public String strTest (HttpServletRequest request) {
        return "test string success !";
    }

    @RequestMapping(value = "/param_test")
    public String parametersTest (HttpServletRequest request, String account, String password) {
        return "test string success !";
    }
}
