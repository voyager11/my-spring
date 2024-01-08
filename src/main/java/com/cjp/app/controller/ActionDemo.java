package com.cjp.app.controller;


import com.cjp.app.service.ActionService;
import com.cjp.app.service.ActionServiceImpl;
import com.cjp.spring.annotation.*;
import com.cjp.spring.webmvc.servlet.EricModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@EricController
@EricRequestMapping("demo")
public class ActionDemo {


    @EricAutowired("service1")
    ActionService actionService;

    @EricAutowired
    ActionServiceImpl actionServiceImpl;


    // http://localhost:8014/test/demo/say?name=sdf
    @EricRequestMapping("say")
    public void say(HttpServletRequest req, HttpServletResponse resp,
                    @EricRequestParam("name") String name){

        String result = "say my name is " + name;

        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println("sss");
        actionService.hh();
    }

    // http://localhost:8014/test/demo/say2?name=eric&passwd=ooooo
    @EricRequestMapping("say2")
    public void say2(HttpServletRequest req, HttpServletResponse resp,
                    @EricRequestParam("name") String name,
                     @EricRequestParam("passwd") String passwd){

        String result = "say2 my name is" + name + passwd;

        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println("sss");
        actionService.hh();
    }


    //  http://localhost:8014/test/demo/say3?name=eric&num=2
    @EricRequestMapping("say3")
    public void say3(HttpServletRequest req, HttpServletResponse resp,
                     @EricRequestParam("name") String name,
                     @EricRequestParam("num") Integer num){

        String result = "say3 my name is" + name + num;

        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println("sss");
        actionService.hh();
    }

    //  http://localhost:8014/test/demo/say4?name=eric&num=2
    @EricRequestMapping("say4")
    public void say4(HttpServletRequest req,
                     @EricRequestParam("name") String name,
                     @EricRequestParam("num") Integer num,
                     HttpServletResponse resp){

        String result = "say4 my name is" + name + num;

        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println("sss");
        actionService.hh();
    }

    // test two-dimensional array
    @EricRequestMapping("say5")
    public void say5(HttpServletRequest req,
                     @EricRequestParam("name") @EricRequestParam2("aaa") String name,
                     @EricRequestParam("num") Integer num,
                     HttpServletResponse resp){

        String result = "say5 my name is" + name + num;

        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println("sss");
        actionService.hh();
    }


    // http://localhost:8014/demo/say6?name=sdf
    @EricRequestMapping("say6")
    public String remove(@EricRequestParam("name") String name){
        return "say6 "+name;
    }

    @Override
    public String toString() {
        return "ActionDemo{" +
                "actionService=" + actionService +
                ", actionServiceImpl=" + actionServiceImpl +
                '}';
    }


    // http://localhost:8014/demo/say?name=sdf
    @EricRequestMapping("say7")
    public EricModelAndView say7(HttpServletRequest req, HttpServletResponse resp,
                                 @EricRequestParam("name") String name){

        String result = "say7 my name is " + name;

        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println("sss");
        actionService.hh();

        return out(resp,result);
    }

    // http://localhost:8017/test/demo/say8?name=sdf
    //error test
    @EricRequestMapping("say8")
    public EricModelAndView say8(HttpServletRequest req, HttpServletResponse resp,
                                 @EricRequestParam("name") String name){

        String result = "say8 my name is " + name;

        try {
            System.out.println(actionService);
            actionService.add();
            return out(resp,result);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String,Object> map = new HashMap<>();
            map.put("detail",e.getMessage());
            System.out.println("---------------------");
            System.out.println(Arrays.toString(e.getStackTrace()));
            map.put("stackTrace", Arrays.toString(e.getStackTrace()));
            return new EricModelAndView("500",map);
        }

    }



    private EricModelAndView out(HttpServletResponse resp,String str){
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
