package com.cjp.spring.beans.support;

import com.cjp.spring.annotation.EricController;
import com.cjp.spring.annotation.EricRequestMapping;
import com.cjp.spring.annotation.EricService;
import com.cjp.spring.beans.config.EricBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;

public class EricBeanDefinitionReader {

    private Properties config = new Properties();

    private final String SCAN_PACKAGE="scanPackage";

    List<String> registryBeanClasses = new ArrayList<>();



    public EricBeanDefinitionReader(String... locations) {

        InputStream fis = this.getClass()
                .getClassLoader()
                .getResourceAsStream(locations[0].replace("classpath:",""));
        try {
            config.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {
        //scanPackage = com.cjp.mvc , package path
        //convert to file path
        // file:/../../myspring/target/classes/com/cjp/app
        URL url = this.getClass().
                getResource("/"+scanPackage.replaceAll("\\.","/"));

        File classPath = new File(url.getFile());

        for (File file : classPath.listFiles()){
            if (file.isDirectory()){
                doScanner(scanPackage + "." + file.getName());
            }else {
                if (!file.getName().endsWith(".class")){ continue;}
                String className = (scanPackage + "." +file.getName().replace(".class",""));
                registryBeanClasses.add(className);
            }
        }

        //System.out.println("--scanned classes--"+registryBeanClasses);
    }


    //
    public List<EricBeanDefinition> loadBeanDefinitions(){
        List<EricBeanDefinition> result = new ArrayList<>();
        try {
            for (String className: registryBeanClasses){
                System.out.println("--String className: registryBeanClasses--"+className);

                Class<?> beanClass = Class.forName(className);

                //if it is an interface, let it pass here
                if (beanClass.isInterface()){
                    continue;
                }

                boolean a = beanClass.isAnnotationPresent(EricService.class);
                boolean b = beanClass.isAnnotationPresent(EricRequestMapping.class);
                boolean c = beanClass.isAnnotationPresent(EricController.class);
//                System.out.println(a);
                if ((!a) && (!b) && (!c))
                {
                    continue;
                }


                //beanName has 3 situations:
                //1 default simple name: starts with a lowercase letter, ManageBook --> manageBook
                // customised the bean name in the container
                String factoryBenName = toLowerFirstCase(beanClass.getSimpleName());
                if (beanClass.isAnnotationPresent(EricService.class)){
                    if (!beanClass.getAnnotation(EricService.class).value().trim().equals("")){
                        factoryBenName = beanClass.getAnnotation(EricService.class).value().trim();
                    }
                }


                String beanClassName = beanClass.getName();

                result.add(doCreateBeanDefinition(factoryBenName,beanClassName));

                //an instance may implement various interfaces
                // define its interfaces

                Class<?>[] interfaces = beanClass.getInterfaces();
                Set<String> factoryNameSet = new HashSet<>();
                for (Class<?> i : interfaces) {
                    if (factoryNameSet.contains(i.getName())) continue;
                    result.add(doCreateBeanDefinition(i.getName(),beanClass.getName()));
                    factoryNameSet.add(i.getName());

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return result;
    }


    private EricBeanDefinition doCreateBeanDefinition(String factoryBenName, String beanClassName){

        try {

            EricBeanDefinition beanDefinition = new EricBeanDefinition();
            //full class name
            beanDefinition.setBeanClassName(beanClassName);

            //with customised name: customised name
            //without: simple name
            beanDefinition.setFactoryBeanName(factoryBenName);
            System.out.println("--doCreateBeanDefinition--"+beanDefinition.toString());
            return beanDefinition;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;

    }

    private String toLowerFirstCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }


    public Properties getConfig(){
        return this.config;
    }
}
