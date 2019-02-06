package com.yabadun.web;

import com.yabadun.context.WebApplicationContext;

import javax.servlet.ServletContext;

/**
 * WebApplicationContextUtils
 *
 * @author panjn
 * @date 2019/2/6
 */
public class WebApplicationContextUtils {
    public static WebApplicationContext getWebApplicationContext(ServletContext servletContext) {
        Object obj = servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (obj == null) {
            return null;
        }
        return (WebApplicationContext) obj;
    }
}
