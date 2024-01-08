package com.cjp.spring.beans.support;

import com.cjp.spring.beans.config.EricBeanDefinition;
import com.cjp.spring.context.support.EricAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EricDefaultListableBeanFactory extends EricAbstractApplicationContext {

    protected final Map<String, EricBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

}
