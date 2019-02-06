package com.yabadun.context;

import com.yabadun.factory.*;
import com.yabadun.support.PathMatchingResourcePatternResolver;
import com.yabadun.util.ConstantUtil;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

/**
 * XmlWebApplicationContext
 *
 * @author panjn
 * @date 2019/2/4
 */
public class XmlWebApplicationContext implements WebApplicationContext {

    private ServletContext servletContext;
    private String configLocation;
    private String[] configLocations;
    private BeanFactory beanFactory;

    private WebApplicationContext parent;

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    public String[] getConfigLocations() {
        return configLocations;
    }

    public void setConfigLocations(String[] configLocations) {
        this.configLocations = configLocations;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setParent(WebApplicationContext parent) {
        this.parent = parent;
    }

    @Override
    public void refresh() {

        // Tell the subclass to refresh the internal bean factory.
        BeanFactory beanFactory = obtainFreshBeanFactory();

        // Invoke factory processors registered as beans in the context.
        //todo myBatis 就是进入这里执行的，就是实例化postProcessor，之后AOP会用到
//        invokeBeanFactoryPostProcessors(beanFactory);

        //todo 重头戏，实例化所有非懒加载的bean （AOP也会在这一步体现）
        finishBeanFactoryInitialization(beanFactory);

    }

    private void finishBeanFactoryInitialization(BeanFactory beanFactory) {
        //todo 重头戏，实例化所有非懒加载的bean （AOP也会在这一步体现）
        beanFactory.preInstantiateSingletons();
    }

    protected BeanFactory obtainFreshBeanFactory() {
        refreshBeanFactory();
        BeanFactory beanFactory = getBeanFactory();
        return beanFactory;
    }

    private void refreshBeanFactory() {
//        if (hasBeanFactory()) {
//            destroyBeans();
//            closeBeanFactory();
//        }
        try {
            DefaultListableBeanFactory beanFactory = createBeanFactory();
//            beanFactory.setSerializationId(getId());
            //todo 将当前上下文ApplicationContext的bean配置信息加载到beanFactory中
            //具体过程是，将bean配置信息解析成一个个BeanDefinition
            //将它们设置到beanFactory的beanDefinitionMap中（Map<String, BeanDefinition> beanDefinitionMap，key-beanName，value-BeanDefinition）
            loadBeanDefinitions(beanFactory);

            this.beanFactory = beanFactory;
        } catch (IOException ex) {
            throw new RuntimeException("I/O error parsing bean definition source for ", ex);
        }
    }

    private void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws IOException {
        String[] configLocations = getConfigLocations();
        if (configLocations != null) {
            loadBeanDefinitions(beanFactory, configLocations);
        }
    }

    private void loadBeanDefinitions(DefaultListableBeanFactory beanFactory, String[] configLocations) {
        for (String configLocation : configLocations) {
            loadBeanDefinitions(beanFactory, configLocation);
        }
    }

    private void loadBeanDefinitions(DefaultListableBeanFactory beanFactory, String configLocation) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        URL[] urls = new URL[0];
        try {
            urls = resolver.getResources(configLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (URL url : urls) {
            loadBeanDefinitions(beanFactory, url);
        }
    }

    private void loadBeanDefinitions(DefaultListableBeanFactory beanFactory, URL url) {
        InputStream in = null;
        try {
            SAXReader reader = new SAXReader();
            in = url.openStream();
            Document doc = reader.read(in);
            Element root = doc.getRootElement();
            parseBeanDefinitions(root, beanFactory);
//            initBean(root, beanFactory);
//            initRef(ConstantUtil.refBeanSettingMap);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void parseBeanDefinitions(Element root, DefaultListableBeanFactory beanFactory) {
        if (root == null) {
            return;
        }
        // 获取他的子节点
        List<Element> childNodes = root.elements();
        for (Element e : childNodes) {
            BeanDefinitionHolder beanDefinitionHolder = parseCustomElement(e);
            if (beanDefinitionHolder != null) {
                beanFactory.registerBeanDefinition(beanDefinitionHolder.getBeanName(), beanDefinitionHolder.getBeanDefinition());
            }
        }
    }

    private BeanDefinitionHolder parseCustomElement(Element element) {
        if ("bean".equals(element.getName())) {
            return parseBeanElement(element);
        }
        return null;
    }

    private BeanDefinitionHolder parseBeanElement(Element e) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition);

        // 获取属性
        List<Attribute> attrs = e.attributes();
        for (Attribute attr : attrs) {
            if ("id".equals(attr.getName())) {
                beanDefinitionHolder.setBeanName(attr.getValue());
            }
            if ("class".equals(attr.getName())) {
                beanDefinition.setBeanClassName(attr.getValue());
            }
        }

        Map<String, Object> propertyMap = new HashMap<>(16);
        List<String> dependOnBeanNameList = new ArrayList<>();
        //获取property
        List<Element> childNodes = e.elements();
        for (Element property : childNodes) {
            if ("property".equals(property.getName())) {
                String propertyName = property.attributeValue("name");
                Object propertyValue = null;
                List<Element> valueElements = property.elements();

                for (Element valueElement : valueElements) {
                    if ("ref".equals(valueElement.getName())) {
                        String refBeanName = valueElement.attributeValue("bean");
                        propertyValue = refBeanName;
                        dependOnBeanNameList.add(refBeanName);
                    }
                }

                propertyMap.put(propertyName, propertyValue);
            }
        }

        beanDefinition.setPropertyMap(propertyMap);

//        for (String dependOnBeanName : dependOnBeanNameList) {
//
//        }
        beanDefinition.setDependsOn(dependOnBeanNameList.toArray(new String[dependOnBeanNameList.size()]));

        return beanDefinitionHolder;
    }


    protected DefaultListableBeanFactory createBeanFactory() {
        //TODO 生成默认beanfactory时，如果当前ApplicationContext存在父ApplicationContext，将父ApplicationContext的beanfactory作为parentBeanFactory设置到新生beanfactory
        //TODO 这样就实现了，子ApplicationContext可的beanFactory可以加载父ApplicationContext范围的bean，但是反之则不行
        // 这也就是SpringMVC，在加载dispatchServlet的ApplicationContext上下文时，其父beanFactory是spring root ApplicationContext的beanfactory
        return new DefaultListableBeanFactory(getInternalParentBeanFactory());
    }

    protected BeanFactory getInternalParentBeanFactory() {
        XmlWebApplicationContext pwac = (XmlWebApplicationContext) getParent();
        if (pwac == null) {
            return null;
        }
        return pwac.getBeanFactory();
    }

    public WebApplicationContext getParent() {
        return this.parent;
    }

    @Override
    public Object getBean(String name) throws Exception {
        return getBeanFactory().getBean(name);
    }

}
