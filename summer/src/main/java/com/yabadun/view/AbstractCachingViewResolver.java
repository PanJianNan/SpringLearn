package com.yabadun.view;

import com.yabadun.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * 自定义AbstractCachingViewResolver视图解析器
 *
 * @author panjn
 * @date 2016/4/6
 */
public abstract class AbstractCachingViewResolver {
    public abstract void resolveView(ModelAndView modelAndView, ServletRequest request, ServletResponse response) throws ServletException, IOException;
}
