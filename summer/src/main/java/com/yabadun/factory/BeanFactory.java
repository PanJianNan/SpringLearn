package com.yabadun.factory;

/**
 * BeanFactory
 *
 * @author panjn
 * @date 2019/2/5
 */
public interface BeanFactory {

    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

    void preInstantiateSingletons();

    Object getBean(String name) throws Exception;
}
