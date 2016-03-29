package com.yabadun.servlet;

import com.yabadun.annotation.RequestMapping;
import com.yabadun.util.ClassMethod;
import com.yabadun.util.ConstantUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 请求分发控制
 *
 * @author panjn
 * @date 2016/3/4
 */
public class DispatcherServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        super.init();
        for (Map.Entry<String, Object> entry : ConstantUtil.beanMap.entrySet()) {
            Class clazz = entry.getValue().getClass();
            RequestMapping controllerMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
            if (controllerMapping == null) {
                continue;
            }
            String preURI = this.dealPreStr(controllerMapping.value());
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
                if (methodMapping == null) {
                    continue;
                }
                String sufStr = this.dealPreStr(methodMapping.value());
                ClassMethod classMethod = new ClassMethod(entry.getValue(), method);
                ConstantUtil.simpleURIMapping.put(preURI + sufStr, classMethod);
            }
        }
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        String requestURI = ((HttpServletRequest) req).getRequestURI();
        ClassMethod classMethod = ConstantUtil.simpleURIMapping.get(requestURI);
        if (classMethod != null) {
            try {
                classMethod.getMethod().invoke(classMethod.getObj());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            ServletOutputStream outputStream = res.getOutputStream();
            outputStream.print("404 !");
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    /**
     * uri前缀 "/"
     * @param str
     * @return
     */
    private String dealPreStr(String str) {
        if (!str.startsWith("/")) {
            return "/" + str;
        }
        return str;
    }
}
