package com.yabadun.listeners;

/**
 * SpringLearn
 *
 * @author panjn
 * @date 2016/2/24
 */

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
import javax.xml.stream.events.Attribute;
import java.io.*;
import java.util.List;

@WebListener()
public class ContextLoaderListener implements ServletContextListener,
        HttpSessionListener, HttpSessionAttributeListener {

    // Public constructor is required by servlet spec
    public ContextLoaderListener() {
    }

    // -------------------------------------------------------
    // ServletContextListener implementation
    // -------------------------------------------------------
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        System.out.println(sc.getInitParameter("contextConfigLocation"));
//        File file = new File("summer/summer-web.xml");


        try {
            SAXReader reader = new SAXReader();
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("summer/summer-web.xml");
            Document doc = reader.read(in);
            Element root = doc.getRootElement();
            initBean(root, "");
        } catch (DocumentException e) {
            e.printStackTrace();
        }

      /*  byte[] bytes = new byte[1024];
        try {
            fis.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(new String(bytes));*/
      /* This method is called when the servlet context is
         initialized(when the Web application is deployed). 
         You can initialize servlet context related data here.
      */
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

    public static void initBean(Element root, String prefix) {
        if (root == null) return;
        // 获取属性
        List<Attribute> attrs = root.attributes();
        if (attrs != null && attrs.size() > 0) {
            System.out.println(prefix);
            for (Attribute attr : attrs) {
                System.out.println(attr.getValue() + " ");
            }
        }
        // 获取他的子节点
        List<Element> childNodes = root.elements();
        prefix += "\t";
        for (Element e : childNodes) {
            initBean(e, prefix);
        }
    }
}
