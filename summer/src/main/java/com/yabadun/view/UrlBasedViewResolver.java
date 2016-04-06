package com.yabadun.view;

import com.yabadun.ModelAndView;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 自定义UrlBasedViewResolver视图解析器
 *
 * @author panjn
 * @date 2016/4/6
 */
public class UrlBasedViewResolver extends AbstractCachingViewResolver {
    @Override
    public void resolveView(ModelAndView modelAndView, ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (StringUtils.isBlank(modelAndView.getView())) {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/views/error/404.jsp");
            requestDispatcher.forward(request, response);
        } else {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(modelAndView.getView());
            for (Map.Entry<String, Object> entry:  modelAndView.getModel().entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
            requestDispatcher.forward(request, response);
        }
    }
}
