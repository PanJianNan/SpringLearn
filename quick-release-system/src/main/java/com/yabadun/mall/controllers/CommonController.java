package com.yabadun.mall.controllers;

import com.yabadun.ModelAndView;
import com.yabadun.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * CommonController
 *
 * @author panjn
 * @date 2016/5/4
 */
@RequestMapping(value = "index")
public class CommonController {

    @RequestMapping
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView("/WEB-INF/views/index.jsp");
        return  modelAndView;
    }
}
