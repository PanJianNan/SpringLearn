package com.yabadun.listener;

import com.yabadun.util.ConstantUtil;
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
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * ContextLoaderListener
 *
 * @author panjn
 * @date 2016/2/24
 */
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
            initBean(root);
            initRef(ConstantUtil.refBeanSettingMap);
            System.out.println();
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

    /**
     * 初始化所有bean
     *
     * @param root
     */
    public static void initBean(Element root) {
        if (root == null) return;
        // 获取他的子节点
        List<Element> childNodes = root.elements();
        for (Element e : childNodes) {
            if ("bean".equals(e.getName())) {
                initAttribute(e);
            }
        }
    }

    public static void initAttribute(Element e) {
        // 获取属性
        List<Attribute> attrs = e.attributes();
        String beanName = null;
        String classObjName = null;
        for (Attribute attr : attrs) {
            if ("id".equals(attr.getName())) {
                beanName = attr.getValue();
            }
            if ("class".equals(attr.getName())) {
                classObjName = attr.getValue();
            }
        }

        //初始化对象
        if (StringUtils.isNotBlank(beanName) && StringUtils.isNotBlank(classObjName)) {
            if (ConstantUtil.beanMap.get(beanName) == null) {
                try {
                    Object obj = Class.forName(classObjName).newInstance();
                    ConstantUtil.beanMap.put(beanName, obj);

                    //获取property
                    List<Element> childNodes = e.elements();
                    for (Element property : childNodes) {
                        if ("property".equals(property.getName())) {
                            List<Element> refBeans = property.elements();
                            for (Element refBean : refBeans) {
                                // 获取属性
                                List<Attribute> refAttrs = refBean.attributes();
                                    String refBeanName = null;
                                String refClassObj = null;
                                for (Attribute attr : refAttrs) {
                                    if ("bean".equals(attr.getName())) {
                                        refBeanName = attr.getValue();
                                        ConstantUtil.refBeanSettingMap.put(beanName, refBeanName);
                                        /*Object refBeanObj = beanMap.get(refBeanName);
                                        if (refBeanObj != null) {
                                            try {
                                                Field field=obj.getClass().getDeclaredField(refBeanName);
                                                field.setAccessible(true);
                                                field.set(refBeanName, refBeanObj);
                                            } catch (NoSuchFieldException e1) {
                                                e1.printStackTrace();
                                            }
                                        } else {
                                            initBean(e.getParent());
                                        }*/
                                    }
                                }
                            }
                        }
                    }
                } catch (InstantiationException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    /**
     * 处理bean依赖
     *
     * @param refBeanSettingMap
     */
    private void initRef(Map<String, String> refBeanSettingMap) {
        for (Map.Entry<String, String> entry : refBeanSettingMap.entrySet()) {
            String beanName = entry.getKey();
            String refBeanName = entry.getValue();
            try {
                Object bean = ConstantUtil.beanMap.get(beanName);
                Object refBean = ConstantUtil.beanMap.get(refBeanName);
                Field field = bean.getClass().getDeclaredField(refBeanName);
                field.setAccessible(true);
                field.set(bean, refBean);
            } catch (NoSuchFieldException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void initProperty(Element e) {
        // 获取属性
        /*List<Attribute> attrs = e.attributes();
        for (Attribute attr : attrs) {
            if (attr.getName()) {

            }
            System.out.println(attr.getValue() + " ");
        }
        //获取property
        List<Element> childNodes = e.elements();
        for (Element e : childNodes) {
            if ("bean".equals(e.getName())) {
                initAttribute(e);
            }
        }*/

    }
}
