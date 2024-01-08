package com.cjp.app.service;

import com.cjp.spring.annotation.EricService;

@EricService
public class DemoServiceImpl implements DemoService{
    @Override
    public void hh() {
        System.out.println(this.getClass().getName() + ",,hhhhh");
    }
}
