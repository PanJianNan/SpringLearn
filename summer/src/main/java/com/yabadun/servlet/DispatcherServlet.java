package com.yabadun.servlet;

import com.yabadun.ModelAndView;
import com.yabadun.annotation.RequestMapping;
import com.yabadun.context.WebApplicationContext;
import com.yabadun.context.XmlWebApplicationContext;
import com.yabadun.factory.DefaultListableBeanFactory;
import com.yabadun.util.ClassMethod;
import com.yabadun.util.ConstantUtil;
import com.yabadun.view.AbstractCachingViewResolver;
import com.yabadun.web.WebApplicationContextUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    WebApplicationContext webApplicationContext;

    @Override
    public void init() throws ServletException {
        super.init();
        initServletBean();

        XmlWebApplicationContext xwac = (XmlWebApplicationContext)  webApplicationContext;
        DefaultListableBeanFactory factory = (DefaultListableBeanFactory) xwac.getBeanFactory();
        Map<String, Object> singletonObjects = factory.getSingletonObjects();
        for (Map.Entry<String, Object> entry : singletonObjects.entrySet()) {
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

    private void initServletBean() {
        //创建Web应用上下文
        this.webApplicationContext = initWebApplicationContext();
    }

    private WebApplicationContext initWebApplicationContext() {
        WebApplicationContext rootContext =
                WebApplicationContextUtils.getWebApplicationContext(getServletContext());

        WebApplicationContext wac = null;
        if (this.webApplicationContext != null) {
            // A context instance was injected at construction time -> use it
            wac = this.webApplicationContext;
            /*if (wac instanceof ConfigurableWebApplicationContext) {
                ConfigurableWebApplicationContext cwac = (ConfigurableWebApplicationContext) wac;
                if (!cwac.isActive()) {
                    // The context has not yet been refreshed -> provide services such as
                    // setting the parent context, setting the application context id, etc
                    if (cwac.getParent() == null) {
                        // The context instance was injected without an explicit parent -> set
                        // the root application context (if any; may be null) as the parent
                        cwac.setParent(rootContext);
                    }
                    configureAndRefreshWebApplicationContext(cwac);
                }
            }*/
        }

        if (wac == null) {
            // No context instance is defined for this servlet -> create a local one
            wac = createWebApplicationContext(rootContext);
        }
        return wac;
    }

    private WebApplicationContext createWebApplicationContext(WebApplicationContext rootContext) {
//        Class<?> contextClass = this.getContextClass();

        XmlWebApplicationContext xwac = new XmlWebApplicationContext();
        xwac.setParent(rootContext);
        this.configureAndRefreshWebApplicationContext(xwac);
        return xwac;
    }

    private void configureAndRefreshWebApplicationContext(XmlWebApplicationContext xwac) {
        ServletContext servletContext = this.getServletContext();
        xwac.setServletContext(servletContext);
//        xwac.setServletConfig(this.getServletConfig());

        String contextConfigLocation = servletContext.getInitParameter("contextConfigLocation");
        if (contextConfigLocation == null) {
            contextConfigLocation = "summer/summer-web.xml";
        }
        xwac.setConfigLocation(contextConfigLocation);
        xwac.setConfigLocations(contextConfigLocation.split(","));
        xwac.refresh();
    }


    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        ClassMethod classMethod = ConstantUtil.simpleURIMapping.get(requestURI);
        if (classMethod != null) {
            try {
                Method method = classMethod.getMethod();
                method.getParameterTypes();
                Object returnValue = classMethod.getMethod().invoke(classMethod.getObj(), req);
                    if (returnValue != null) {
                    if (returnValue instanceof ModelAndView) {//若方法调用结果是ModelAndView则返回相应视图
                        AbstractCachingViewResolver viewResolver = (AbstractCachingViewResolver) webApplicationContext.getBean("defaultView");
                        viewResolver.resolveView((ModelAndView) returnValue, req, res);
                    } else if (returnValue instanceof String) {
                        String value = (String) returnValue;
                        if (value.startsWith("forward:")) {
                            //todo
                        } else if (value.startsWith("redict:")) {
                            res.sendRedirect(value.replace("redict:", ""));
                        } else {
                            AbstractCachingViewResolver viewResolver = (AbstractCachingViewResolver) webApplicationContext.getBean("defaultView");
                            viewResolver.resolveView(new ModelAndView(value), req, res);
                        }
                    } else {//否则直接返回结果 todo 可使用fastjson等第三方库加工结果再返回
                        res.getWriter().print(returnValue);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/WEB-INF/views/error/404.jsp");
            requestDispatcher.forward(req, res);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    /**
     * uri前缀 "/"
     *
     * @param str
     * @return
     */
    private String dealPreStr(String str) {
        if (StringUtils.isNotBlank(str) && !str.startsWith("/")) {
            return "/" + str;
        }
        return str;
    }
}
