package com.yabadun.factory;

/**
 * BeanDefinitionHolder
 *
 * @author panjn
 * @date 2019/2/5
 */
public class BeanDefinitionHolder {

    private String beanName;
    private BeanDefinition beanDefinition;

    public BeanDefinitionHolder(GenericBeanDefinition beanDefinition) {
        this.beanDefinition = beanDefinition;
    }

    public String getBeanName() {
        return beanName;
    }

    public BeanDefinition getBeanDefinition() {
        return beanDefinition;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

}
