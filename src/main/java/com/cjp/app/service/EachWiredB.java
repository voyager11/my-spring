package com.cjp.app.service;

import com.cjp.spring.annotation.EricAutowired;
import com.cjp.spring.annotation.EricService;

@EricService
public class EachWiredB {

    @EricAutowired
    EachWiredA eachWiredA;

    
}
