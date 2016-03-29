package com.yabadun.mall.controllers;

import com.yabadun.mall.Service.SecurityService;
import com.yabadun.annotation.RequestMapping;

/**
 * 登录Controller
 *
 * @author panjn
 * @date 2016/2/24
 */
@RequestMapping(value = "/login")
public class LoginController {
    public SecurityService securityService;

    @RequestMapping
    public String login () {
        if (securityService.userAuth()) {
            System.out.println("welcome!");
        }
        return "welcome";
    }
}
