package com.cjp.spring.beans;


/**
 * implement instances - full class name
 */
public class EricBeanWrapper {

    //wrapped instance
    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public EricBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }

    //return proxy instance
    //maybe: $Proxy0
    public Class<?> getWrappedClass(){
        return this.wrappedInstance.getClass();
    }

    @Override
    public String toString() {
        return "EricBeanWrapper{" +
                "wrappedInstance=" + wrappedInstance +
                '}';
    }
}
