package com.yabadun.mall.controllers;

import com.yabadun.mall.Service.SecurityService;

/**
 * 登录Controller
 *
 * @author panjn
 * @date 2016/2/24
 */
public class LoginController {
    public SecurityService securityService;

    public String login () {
        if (securityService.userAuth()) {
            System.out.println("welcome!");
        }
        return "welcome";
    }
}
