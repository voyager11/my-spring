package com.cjp.spring.beans.config;

public class EricBeanDefinition {


    private String factoryBeanName;

    //If it is an instance, it is the full class name of the instance
    //If it is an interface,  it is the full class name of the instance of the interface
    private String beanClassName;

    public String getBeanClassName() {
        return beanClassName;
    }

    private boolean lazyInit = false;

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    @Override
    public String toString() {
        return "EricBeanDefinition{" +
                "factoryBeanName='" + factoryBeanName + '\'' +
                ", beanClassName='" + beanClassName + '\'' +
                ", lazyInit=" + lazyInit +
                '}';
    }
}
