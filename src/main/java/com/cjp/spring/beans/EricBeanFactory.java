package com.cjp.spring.beans;

/**
 * singleton factory
 */
public interface EricBeanFactory {

    /**
     *
     * @param beanName
     * @return
     */
    Object getBean(String beanName);

    Object getBean(Class<?> beanClass);
}
