package com.cjp.spring.webmvc;

import com.cjp.spring.annotation.EricController;
import com.cjp.spring.annotation.EricRequestMapping;
import com.cjp.spring.context.EricApplicationContext;
import com.cjp.spring.webmvc.servlet.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EricDispatchServlet extends HttpServlet {
    private final String CONTEXT_CONFIG_LOCATION="contextConfigLocation";

    private EricApplicationContext context;

    //EricHandlerMapping url controller method
    private List<EricHandlerMapping> handlerMappings = new ArrayList<>();

    private Map<EricHandlerMapping, EricHandlerAdapter> handlerAdapterMap = new HashMap<>();

    private List<EricViewResolver> viewResolvers = new ArrayList<>();


    @Override
    public void init(ServletConfig config) throws ServletException {
        //1 initializing ApplicationContext
        context = new EricApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));

        //2 initializing spring mvc
        initStrategies(context);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDispath(req,resp);
        }catch (Exception e){
            resp.getWriter().write("500 : \r\n" + Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        }

    }


    private void doDispath(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1 get req from url, matching a handlermapping
        EricHandlerMapping handler = getHandler(req);
        System.out.println("--handler--"+handler);

        if (handler==null){

            System.out.println("--404--");
            EricModelAndView modelAndView = new EricModelAndView("404");
            processDispatchResult(req,resp,modelAndView);
            return;
        }

        //2 get read of parameters
        // get an adapter
        EricHandlerAdapter ha = getHandlerAdapter(handler);

        //3 handle
        EricModelAndView mv  = ha.handle(req,resp,handler);

        //4 return
        processDispatchResult(req, resp, mv);


    }

    protected void initStrategies(EricApplicationContext context) {
        initMultipartResolver(context);
        initLocaleResolver(context);
        initThemeResolver(context);

        initHandlerMappings(context);

        initHandlerAdapters(context);

        initHandlerExceptionResolvers(context);

        initRequestToViewNameTranslator(context);

        initViewResolvers(context);

        initFlashMapManager(context);
    }



    private void processDispatchResult(HttpServletRequest req,
                                       HttpServletResponse resp,
                                       EricModelAndView mv) throws Exception {
        //mv convert to html / output stream / json / freemark
        //contextType
        if (mv == null) return;

        if (this.viewResolvers.isEmpty()) return;

        for (EricViewResolver viewResolver : this.viewResolvers) {

            EricView view = viewResolver.resolveViewName(mv.getViewName(),null);

            view.render(mv.getModel(),req,resp);
            return;
        }

    }



    private EricHandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) return null;

        //absolute path convert to relative path
        String url =  req.getRequestURI();
        System.out.println("--req.getRequestURI()--"+req.getRequestURI());

        String contextPath = req.getContextPath();
        System.out.println("--req.getContextPath()--"+req.getContextPath());

        //contextPath: pom, <contextPath>/</contextPath>，
//        url.replaceAll(contextPath,"").replaceAll("/+","/");

        url.replaceAll("/+","/");

        System.out.println("--doDispatch--"+url);

        for (EricHandlerMapping handler : this.handlerMappings) {
            try {
                Matcher matcher = handler.getPattern().matcher(url.replaceAll(contextPath,""));
                if (!matcher.matches()) continue;

                return handler;
            }catch (Exception e){

            }

        }
        return null;

    }

    //handler, is a handlerMapping
    private EricHandlerAdapter getHandlerAdapter(EricHandlerMapping handler) {
        if (this.handlerAdapterMap.isEmpty()) return null;

        EricHandlerAdapter ha = this.handlerAdapterMap.get(handler);

        if (ha.support(handler)){
            return ha;
        }

        return null;
    }




    private void initMultipartResolver(EricApplicationContext context) {
    }

    private void initFlashMapManager(EricApplicationContext context) {
    }



    private void initViewResolvers(EricApplicationContext context) {
        //get the path of the template
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);
        for (File template : templateRootDir.listFiles()) {
            //The main purpose here is to imitate the spring list save
            //   in order to be compatible with multiple templates
            //simplized here, just need one
            this.viewResolvers.add(new EricViewResolver(templateRoot));
        }
    }


    private void initRequestToViewNameTranslator(EricApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(EricApplicationContext context) {



    }

    private void initHandlerAdapters(EricApplicationContext context) {
        //convert request to a handler
        for (EricHandlerMapping handlerMapping : handlerMappings){
            this.handlerAdapterMap.put(handlerMapping,new EricHandlerAdapter());
        }

    }

    //a controller and its annotations and methods
    private void initHandlerMappings(EricApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        System.out.println("--beanNames--"+Arrays.toString(beanNames));
        try {
            for (String beanName : beanNames) {
                Object controller = context.getBean(beanName);
                Class<?> clazz = controller.getClass();
                if (!clazz.isAnnotationPresent(EricController.class)){
                    continue;
                }

                String basUrl="";
                if (clazz.isAnnotationPresent(EricRequestMapping.class)){
                    EricRequestMapping requestMapping = clazz.getAnnotation(EricRequestMapping.class);
                    basUrl = requestMapping.value();
                }

                //default: get all public methods
                for (Method method :clazz.getMethods()){
                    if (!method.isAnnotationPresent(EricRequestMapping.class)) continue;

                    EricRequestMapping requestMapping = method.getAnnotation(EricRequestMapping.class);
                    String regex = ("/"+basUrl + "/"+requestMapping.value().replaceAll("\\*",".*"))
                            .replaceAll("/+","/");
                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new   EricHandlerMapping(pattern,controller,method));
                    System.out.println("--mapped :"+pattern + ","+method.getName());

                }



            }
        }catch (Exception e){

        }

    }

    private void initThemeResolver(EricApplicationContext context) {
    }

    private void initLocaleResolver(EricApplicationContext context) {
    }



}
