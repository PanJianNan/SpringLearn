package com.yabadun.mall.controllers;

import com.yabadun.ModelAndView;
import com.yabadun.annotation.RequestMapping;

/**
 * CommonController
 *
 * @author panjn
 * @date 2016/5/4
 */
@RequestMapping(value = "index")
public class CommonController {
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView("/WEB-INF/views/success.jsp");
        return  modelAndView;
    }
}
