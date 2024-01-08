package com.cjp.spring.webmvc.servlet;

import com.cjp.spring.annotation.EricRequestParam;
import com.cjp.spring.annotation.EricRequestParam2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EricHandlerAdapter {

    public boolean support(Object handler){
        return (handler instanceof EricHandlerMapping);

    }


    public EricModelAndView handle(HttpServletRequest request,
                                   HttpServletResponse response,
                                   Object handler) throws Exception{

        EricHandlerMapping handlerMapping = (EricHandlerMapping) handler;

        Map<String,Integer> paramIndexMapping = new HashMap<>();

        Method method = handlerMapping.getMethod();

        Annotation[][] pa = method.getParameterAnnotations();

        System.out.println("--method--"+method);
//        System.out.println("--Annotation[][] pa--"+ Arrays.toString(pa));
        //pa.length
        for (int i=0; i<pa.length; i++){

            for (Annotation a :pa[i]){
                if (a instanceof EricRequestParam){
                    // get parameter name
                    String paramName = ((EricRequestParam)a).value();
                    if (!"".equals(paramName.trim())){
                        paramIndexMapping.put(paramName,i);
                        System.out.println("--get param name-- "+paramName);
                    }
                }else if (a instanceof EricRequestParam2){
                    String paramName = ((EricRequestParam2)a).value();
                    if (!"".equals(paramName.trim())){
                        paramIndexMapping.put(paramName,i);
                        System.out.println("--get param name-- "+paramName);
                    }
                }
            }

        }


        //formal parameters list
        Class<?> [] paramsTypes = method.getParameterTypes();

        //get http servlet req and resp
        for (int i=0; i<paramsTypes.length; i++){
            Class<?> type = paramsTypes[i];
            if (type == HttpServletRequest.class || type == HttpServletResponse.class){
                paramIndexMapping.put(type.getName(),i);
            }
        }


        //actual parameters list
        Object [] paramValues = new Object[paramsTypes.length];

        //get parameter value, e.g.: name=sdf, get the "sdf"
        Map<String,String[]> reqparamMap = request.getParameterMap();
        for (Map.Entry<String,String[]> param : reqparamMap.entrySet()){
            String value = Arrays.toString(param.getValue())
                    .replaceAll("\\[|\\]","")
                    .replaceAll("\\s",",");
            System.out.println("--set real param--"+value);

            if (!paramIndexMapping.containsKey(param.getKey())){
                continue;
            }

            int index = paramIndexMapping.get(param.getKey());
            paramValues[index] = convert(paramsTypes[index],value);
        }

        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())){
            int reqIndex = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = request;
        }

        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())){
            int respIndex = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = response;
        }


        System.out.println("--handlerMapping.controller--"+handlerMapping.getController());
        System.out.println("--handlerMapping.method--"+handlerMapping.getMethod().getName());
        System.out.println("--paramValues--"+Arrays.toString(paramValues));
        //--paramValues--[(GET /demo/say?name=eric)@751792209 org.eclipse.jetty.server.Request@2ccf7051, (GET /demo/say?name=eric)@751792209 org.eclipse.jetty.server.Request@2ccf7051, eric]
        Method handlerMethod = handlerMapping.getMethod();
        Object controller = handlerMapping.getController();
        Object returnValue = handlerMethod.invoke(controller,paramValues);

        //
        if (returnValue ==null || returnValue instanceof Void) {return null;}

        boolean isModelAndView = handlerMethod.getReturnType() == EricModelAndView.class;
        if (isModelAndView) {
            return (EricModelAndView) returnValue;
        }


        return null;
    }



    //parameters from url are all String, we need to convert into other type if happens
    private Object convert(Class<?> type, String value){

        if (Integer.class == type){
            return Integer.valueOf(value);
        }else if (Double.class == type){
            return Double.valueOf(value);
        }

        return value;
    }
}
