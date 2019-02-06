package com.yabadun.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * GenericBeanDefinition
 *
 * @author panjn
 * @date 2019/2/5
 */
public class GenericBeanDefinition implements BeanDefinition {

    private String parentName;

    private volatile Object beanClass;

    private String beanClassName;

    private String scope = SCOPE_DEFAULT;

    private boolean lazyInit = false;

    private String[] dependsOn;

    private String initMethodName;

    private String destroyMethodName;
    private Map<String, Object> propertyMap = new HashMap<>(16);

//    private Resource resource;


    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Object getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Object beanClass) {
        this.beanClass = beanClass;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String[] getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(String[] dependsOn) {
        this.dependsOn = dependsOn;
    }

    public String getInitMethodName() {
        return initMethodName;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public String getDestroyMethodName() {
        return destroyMethodName;
    }

    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }

    public Map<String, Object> getPropertyMap() {
        return propertyMap;
    }

    public void setPropertyMap(Map<String, Object> propertyMap) {
        this.propertyMap = propertyMap;
    }

    @Override
    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(scope) || SCOPE_DEFAULT.equals(scope);
    }
}
