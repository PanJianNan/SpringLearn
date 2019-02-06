package com.yabadun.factory;

import com.yabadun.util.ConstantUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DefaultListableBeanFactory
 *
 * @author panjn
 * @date 2019/2/5
 */
public class DefaultListableBeanFactory implements BeanFactory {

    private BeanFactory parentBeanFactory;
    /** Map of bean definition objects, keyed by bean name */
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    /** List of bean definition names, in registration order */
    private volatile List<String> beanDefinitionNames = new ArrayList<>(256);
    /** Cache of singleton factories: bean name --> ObjectFactory */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
    /** Cache of early singleton objects: bean name --> bean instance */
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);
    /** Cache of singleton objects: bean name --> bean instance */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
    /** Set of registered singletons, containing the bean names in registration order */
    private final Set<String> registeredSingletons = new LinkedHashSet<>(256);
    /** Cache of singleton objects created by FactoryBeans: FactoryBean name --> object */
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>(16);


    /**
     * Internal marker for a null singleton object:
     * used as marker value for concurrent Maps (which don't support null values).
     */
    protected static final Object NULL_OBJECT = new Object();

    /** Names of beans that are currently in creation */
    private final Set<String> singletonsCurrentlyInCreation =
            Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(16));

    public DefaultListableBeanFactory(BeanFactory parentBeanFactory) {
        this.parentBeanFactory = parentBeanFactory;
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(beanName, beanDefinition);
        this.beanDefinitionNames.add(beanName);
    }

    public Map<String, Object> getSingletonObjects() {
        return singletonObjects;
    }

    @Override
    public void preInstantiateSingletons() {
        // Iterate over a copy to allow for init methods which in turn register new bean definitions.
        // While this may not be part of the regular factory bootstrap, it does otherwise work fine.
        List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);
        // Trigger initialization of all non-lazy singleton beans...
        for (String beanName : beanNames) {
            GenericBeanDefinition beanDefinition = (GenericBeanDefinition)getBeanDefinitionMap(beanName);
            if (beanDefinition.isSingleton() && !beanDefinition.isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        Object bean = null;

        // Eagerly check singleton cache for manually registered singletons.
        Object sharedInstance = getSingleton(beanName);

        if (sharedInstance != null) {
            bean = sharedInstance;
        } else {
            // Check if bean definition exists in this factory.
            BeanFactory parentBeanFactory = getParentBeanFactory();
            // 如果bean的beanDefinition不存在当前上下文的beanFactory中，则尝试从父上下文的beanFactory匹配bean
            if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
                // 尝试从父上下文的beanFactory匹配bean
                return parentBeanFactory.getBean(beanName);
            }

            //当父上下文的beanFactory无法匹配到bean，则在当前上下文创建bean的单例
            GenericBeanDefinition beanDefinition = (GenericBeanDefinition) getBeanDefinitionMap(beanName);
            String[] dependsOn = beanDefinition.getDependsOn();
            if (dependsOn != null) {
                for (String dep : dependsOn) {
                    getBean(dep);
                }
            }

            if (beanDefinition.isSingleton()) {
                sharedInstance = getSingleton(beanName, new ObjectFactory<Object>() {
                    @Override
                    public Object getObject() throws Exception {
                        try {
                            Object[] args = null;
                            return createBean(beanName, beanDefinition, args);
                        } catch (Exception ex) {
                            // Explicitly remove instance from singleton cache: It might have been put there
                            // eagerly by the creation process, to allow for circular reference resolution.
                            // Also remove any beans that received a temporary reference to the bean.
//                        destroySingleton(beanName);
                            throw ex;
                        }
                    }
                });
                bean = sharedInstance;
            }
        }

        return bean;
    }

    private boolean containsBeanDefinition(String beanName) {
        return this.beanDefinitionMap.containsKey(beanName);
    }

    private Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) throws Exception {
        synchronized (this.singletonObjects) {
            Object singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
                beforeSingletonCreation(beanName);
                boolean newSingleton = false;

                try {
                    singletonObject = singletonFactory.getObject();
                    newSingleton = true;
                } catch (Exception e) {
                    singletonObject = this.singletonObjects.get(beanName);
                    if (singletonObject == null) {
                        throw e;
                    }
                } finally {
                    afterSingletonCreation(beanName);
                }
                if (newSingleton) {
                    addSingleton(beanName, singletonObject);
                }
            }
            return (singletonObject != NULL_OBJECT ? singletonObject : null);
        }
    }

    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, (singletonObject != null ? singletonObject : NULL_OBJECT));
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }

    private void beforeSingletonCreation(String beanName) {
        this.singletonsCurrentlyInCreation.add(beanName);
    }

    protected void afterSingletonCreation(String beanName) {
        this.singletonsCurrentlyInCreation.remove(beanName);
    }

    private Object createBean(String beanName, GenericBeanDefinition beanDefinition, Object[] args) throws Exception {

        // Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
        Object bean = resolveBeforeInstantiation(beanName);
        if (bean != null) {
            return bean;
        }

        //生成一个简单的bean
        Object beanInstance = doCreateBean(beanName, args);
        //为bean设置属性
        populateBean(beanName, beanInstance);
        return beanInstance;
    }

    private void populateBean(String beanName, Object beanInstance) {
        GenericBeanDefinition beanDefinition = (GenericBeanDefinition)getBeanDefinitionMap(beanName);
        Map<String, Object> propertyMap = beanDefinition.getPropertyMap();
        for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
            String propertyName = entry.getKey();
            Object propertyValue = entry.getValue();
            try {
                Object refBean = this.singletonObjects.get(propertyValue);
                Field field = beanInstance.getClass().getDeclaredField(propertyName);
                field.setAccessible(true);
                field.set(beanInstance, refBean);
            } catch (NoSuchFieldException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

    private Object doCreateBean(String beanName, Object[] args) {
        Object bean = null;
        // Instantiate the bean.
        GenericBeanDefinition beanDefinition = (GenericBeanDefinition)getBeanDefinitionMap(beanName);
        String beanClassName = beanDefinition.getBeanClassName();
        try {
            bean = Class.forName(beanClassName).newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return bean;
    }


    private Object resolveBeforeInstantiation(String beanName) {
        Object bean = null;
//        bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
//        if (bean != null) {
//            bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
//        }
        return bean;
    }

    private Object getSingleton(String beanName) throws Exception {
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
            synchronized (this.singletonObjects) {
                singletonObject = this.earlySingletonObjects.get(beanName);
                if (singletonObject == null) {
                    ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                    if (singletonFactory != null) {
                        singletonObject = singletonFactory.getObject();
                        this.earlySingletonObjects.put(beanName, singletonObject);
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }
        return (singletonObject != NULL_OBJECT ? singletonObject : null);
    }

    private BeanDefinition getBeanDefinitionMap(String beanName) {
        return beanDefinitionMap.get(beanName);
    }

    /**
     * Return whether the specified singleton bean is currently in creation
     * (within the entire factory).
     * @param beanName the name of the bean
     */
    public boolean isSingletonCurrentlyInCreation(String beanName) {
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    /**
     * Obtain an object to expose from the given FactoryBean, if available
     * in cached form. Quick check for minimal synchronization.
     * @param beanName the name of the bean
     * @return the object obtained from the FactoryBean,
     * or {@code null} if not available
     */
    protected Object getCachedObjectForFactoryBean(String beanName) {
        Object object = this.factoryBeanObjectCache.get(beanName);
        return (object != NULL_OBJECT ? object : null);
    }

    public BeanFactory getParentBeanFactory() {
        return parentBeanFactory;
    }
}
