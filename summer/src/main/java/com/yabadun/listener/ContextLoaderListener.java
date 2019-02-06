package com.yabadun.listener;

import com.yabadun.context.WebApplicationContext;
import com.yabadun.context.XmlWebApplicationContext;
import com.yabadun.servlet.DispatcherServlet;
import com.yabadun.support.PathMatchingResourcePatternResolver;
import com.yabadun.util.ConstantUtil;
import net.sf.cglib.beans.BeanCopier;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * 自定义上下文载入监听器
 *
 * @author panjn
 * @date 2016/2/24
 */
@WebListener
public class ContextLoaderListener implements ServletContextListener,
        HttpSessionListener, HttpSessionAttributeListener {

    /**
     * The root WebApplicationContext instance that this loader manages.
     */
    private WebApplicationContext context;

    // Public constructor is required by servlet spec
    public ContextLoaderListener() {
    }

    // -------------------------------------------------------
    // ServletContextListener implementation
    // -------------------------------------------------------
    public void contextInitialized(ServletContextEvent event) {
        //壹
        initWebApplicationContext(event.getServletContext());
    }

    private void initWebApplicationContext(ServletContext servletContext) {
        if (this.context == null) {
            //贰
            this.context = createWebApplicationContext(servletContext);
        }

        XmlWebApplicationContext xwac = (XmlWebApplicationContext) this.context;
        //叁
        configureAndRefreshWebApplicationContext(xwac, servletContext);
        //肆
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.context);
    }

    private void configureAndRefreshWebApplicationContext(XmlWebApplicationContext xwac, ServletContext servletContext) {
        xwac.setServletContext(servletContext);

        String contextConfigLocation = servletContext.getInitParameter("contextConfigLocation");
        if (contextConfigLocation == null) {
            contextConfigLocation = "summer/summer-application.xml";
        }
        xwac.setConfigLocation(contextConfigLocation);
        xwac.setConfigLocations(contextConfigLocation.split(","));

        ////非懒加载的bean就在这加载的
        xwac.refresh();
    }

    private WebApplicationContext createWebApplicationContext(ServletContext servletContext) {
        XmlWebApplicationContext xmlWebApplicationContext = new XmlWebApplicationContext();

        //fixme
//        BeanCopier beanCopier = BeanCopier.create(servletContext.getClass(), XmlWebApplicationContext.class, false);
//        beanCopier.copy(servletContext, xmlWebApplicationContext,null);

        return xmlWebApplicationContext;
    }

    public void contextDestroyed(ServletContextEvent sce) {
      /* This method is invoked when the Servlet Context 
         (the Web application) is undeployed or 
         Application Server shuts down.
      */
    }

    // -------------------------------------------------------
    // HttpSessionListener implementation
    // -------------------------------------------------------
    public void sessionCreated(HttpSessionEvent se) {
      /* Session is created. */
    }

    public void sessionDestroyed(HttpSessionEvent se) {
      /* Session is destroyed. */
    }

    // -------------------------------------------------------
    // HttpSessionAttributeListener implementation
    // -------------------------------------------------------

    public void attributeAdded(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute 
         is added to a session.
      */
    }

    public void attributeRemoved(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute
         is removed from a session.
      */
    }

    public void attributeReplaced(HttpSessionBindingEvent sbe) {
      /* This method is invoked when an attibute
         is replaced in a session.
      */
    }

}
