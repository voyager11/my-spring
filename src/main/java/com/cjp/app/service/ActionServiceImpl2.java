package com.cjp.app.service;

import com.cjp.spring.annotation.EricService;

@EricService(value = "actionServiceImpl2")
public class ActionServiceImpl2 implements ActionService {
    @Override
    public void hh() {

        System.out.println("ActionServiceImpl222----hhhhhh");

    }

    @Override
    public void add() throws Exception {

    }
}
