package com.cjp.app.test;

import com.cjp.app.service.ActionService;
import com.cjp.spring.context.EricApplicationContext;

public class TestSpring {

    public static void main(String[] args) {

        tess1();
    }


    //ioc test
    private static void tess1(){
        EricApplicationContext context = new EricApplicationContext("classpath:application.properties");


        System.out.println("------ test result -----");
        Object actionDemo = context.getBean("actionDemo");
        System.out.println(actionDemo);

        System.out.println("-- eachWired --");
        Object o2 = context.getBean("eachWiredA");
        System.out.println(o2);

        ActionService actionService = (ActionService) context.getBean("service1");
        actionService.hh();
    }


}
