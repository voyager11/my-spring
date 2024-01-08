package com.cjp.app.service;

import com.cjp.spring.annotation.EricService;

//@EricService
@EricService("service1")
public class ActionServiceImpl implements ActionService {
    @Override
    public void hh()  {

        System.out.println("ActionServiceImpl----hhhhhh");

    }

    @Override
    public void add() throws Exception {

        throw new Exception("a execption--");

    }


}
