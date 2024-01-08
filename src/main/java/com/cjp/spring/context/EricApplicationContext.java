package com.cjp.spring.context;

import com.cjp.spring.annotation.EricAutowired;
import com.cjp.spring.annotation.EricController;
import com.cjp.spring.annotation.EricService;
import com.cjp.spring.beans.EricBeanFactory;
import com.cjp.spring.beans.EricBeanWrapper;
import com.cjp.spring.beans.config.EricBeanDefinition;
import com.cjp.spring.beans.config.EricBeanPostProcessor;
import com.cjp.spring.beans.support.EricBeanDefinitionReader;
import com.cjp.spring.beans.support.EricDefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IOC implement
 * EricDefaultListableBeanFactory: default container implementation
 * EricBeanFactory: top container standards
 */
public class EricApplicationContext
        extends EricDefaultListableBeanFactory
        implements EricBeanFactory {

    private String[] configLocations;

    private EricBeanDefinitionReader reader;

    public EricApplicationContext(String... configLocations){
        this.configLocations = configLocations;
        refresh();
    }

    private Map<String,Object> singletonObjects = new ConcurrentHashMap<>();

    //common IOC
    //simple name ： wrapper
    //full class name ： wrapper
    private Map<String,EricBeanWrapper> factoryBeanInstanceCache = new HashMap<>();

    private boolean isSingleton = true;

    @Override
    public void refresh()  {
        //1 load configurations
        System.out.println("########################## loading configurations #####################");
        reader = new EricBeanDefinitionReader(this.configLocations);

        //2 scan classes
        System.out.println("########################## load BeanDefinitions ########################## ");
        List<EricBeanDefinition> ericBeanDefinitions = reader.loadBeanDefinitions();

        System.out.println("##########################  register bean info ########################## ");
        //3 add configuration into IOC (fake IOC)
        doRegisterBeanDefinition(ericBeanDefinitions);

        System.out.println("########################## dependency injection  ########################## ");

        //4 Classes that are not lazy to load should be initialized early
        try {
            doAutowired();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("##########################  Container initialization is complete ########################## ");
    }

    private void doRegisterBeanDefinition(List<EricBeanDefinition> beanDefinitions) {
        //superclasses
        for (EricBeanDefinition beanDefinition : beanDefinitions){
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
        System.out.println("--beanDefinitionMap--"+beanDefinitionMap);

    }


    private void doAutowired() throws Exception {
        for (Map.Entry<String, EricBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()){
//                getBean(beanName);
                putWrapperBenToFactory(beanName);
            }
        }

        for (Map.Entry<String, EricBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            EricBeanWrapper beanWrapper = this.factoryBeanInstanceCache.get(beanName);
            populateBean(beanName,beanWrapper);

        }

    }

    private void putWrapperBenToFactory(String beanName) throws Exception {
        EricBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        //1 full class path
        String className = beanDefinition.getBeanClassName();
        Object instance = null;


        //todo why don't use one function to handle these 2 steps
        // Cus we need to handle circular dependency
        // regardless dependency relations, initializing them all at first

        //2 initializing
        //2 .1 before and after aware
        EricBeanPostProcessor postProcessor = new EricBeanPostProcessor();
        postProcessor.postProcessBeforeInitialization(instance,beanName);

        instance = instantiateBean(className,beanDefinition);

        postProcessor.postProcessAfterInitialization(instance,beanName);


        //3  beanWrapper
        EricBeanWrapper beanWrapper = new EricBeanWrapper(instance);

        //4
        // singletonObjects
        // factoryBeanInstanceCache
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);
        this.factoryBeanInstanceCache.put(className,beanWrapper);

        System.out.println("--singletonObjects--"+singletonObjects.size()+"--"+singletonObjects.toString());
        System.out.println("--factoryBeanInstanceCache--"+factoryBeanInstanceCache.size()+"--"+factoryBeanInstanceCache.toString());

    }


    //AbstractAutowiredCapableBeanFactory
    @Override
    public Object getBean(String beanName) {

        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    //
    public Object getBean(Class<?> beanClass){
        return getBean(beanClass.getName());
    }

    public String [] getBeanDefinitionNames(){
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }



    private Object instantiateBean(String className,
                                 EricBeanDefinition beanDefinition) {

        System.out.println("-- wrapper beanName-- "+className);
        System.out.println(beanDefinition);

        Object instance = null;
        //2 reflection
        try {

            if (this.singletonObjects.containsKey(className)){
                System.out.println("--containsKey--"+className);
                instance = this.singletonObjects.get(className);
            }else {

                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                System.out.println("--instance--"+instance);
                this.singletonObjects.put(className,instance);
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return instance;
    }




    private void populateBean(String beanName
//                              EricBeanDefinition beanDefinition
                              ,EricBeanWrapper beanWrapper) {

        Object instance = beanWrapper.getWrappedInstance(); //ActionDemo

        Class<?> clazz = beanWrapper.getWrappedClass();
        if (clazz.isAnnotationPresent(EricController.class) || clazz.isAnnotationPresent(EricService.class)){
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {//e.g: will get: actionService
                if (field.isAnnotationPresent(EricAutowired.class)){
                    EricAutowired autowired = field.getAnnotation(EricAutowired.class);
                    String autoWiredBeanName = autowired.value().trim();

                    if ("".equals(autoWiredBeanName)){
                        // field.getType().getName()
                        autoWiredBeanName = field.getType().getName();
                        //System.out.println("--EricAutowired--value--no--");
                    }

                    field.setAccessible(true);

                    try {
                        //reflection
                        Object o = this.factoryBeanInstanceCache.get(autoWiredBeanName).getWrappedInstance();
                        System.out.println("--  --"+instance);
                        System.out.println("-- be autowired instance ----"+o);
                        System.out.println("--"+autoWiredBeanName);
                        field.set(instance,o);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

        }else if (clazz.isAnnotationPresent(EricService.class)){


         }else {
            return;
        }
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }

    
}
