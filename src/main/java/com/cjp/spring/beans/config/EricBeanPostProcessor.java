package com.cjp.spring.beans.config;

public class EricBeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean,String beanName) throws Exception{
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean,String beanName) throws Exception{
        return bean;
    }
}
