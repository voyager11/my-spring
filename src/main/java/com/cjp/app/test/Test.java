package com.cjp.app.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    public static void main(String[] args) throws Exception {

//        Test test = new Test();
//        test.test2();
    }

    private static void test1(){
        Class<?> beanClass = Car.class;
        System.out.println(beanClass);
        System.out.println(beanClass.getName());
        System.out.println(beanClass.getSimpleName());

        Map<String,String> map = new HashMap<>();
        map.put("aa","aac");
        if (map.containsKey("aa")){
            System.out.println("xxxx");
        }
    }

    //file stream test
    private  void test2() throws Exception{
        String location = "classpath:application.properties";
        InputStream fis = this.getClass()
                .getClassLoader()
                .getResourceAsStream(location.replace("classpath:",""));

        Properties config = new Properties();
        try {
            config.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(config);

        String scanPackage = config.getProperty("scanPackage");
        System.out.println("/"+scanPackage.replaceAll("\\.","/"));

        System.out.println(this.getClass().getResource("/"));

        System.out.println(this.getClass().getResource("/").getPath());




        URL url = this.getClass().
                getResource("/"+scanPackage.replaceAll("\\.","/"));
        System.out.println(url);

        File classPath = new File(url.getFile());
        System.out.println("--classPath--"+classPath);

        doScanner(scanPackage);

        System.out.println("------- viewResolver ---------");

        String templateRoot = config.getProperty("templateRoot");

        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        System.out.println(templateRootPath);

        File templateRootDir = new File(templateRootPath);

        File templateFile = new File((templateRootDir.getPath() + "/" +"500.html").replaceAll("/+","/"));

        System.out.println(templateFile);

        RandomAccessFile ra = new RandomAccessFile(templateFile,"r");

        String line = "";
        while (null != (line = ra.readLine())){
            line = new String(line.getBytes("ISO-8859-1"),"utf-8");
            System.out.println(line);
            //替换html中的 为
            //replace start with ￥ in {}, and not } letters, caps not sensitive 大小写不敏感
            Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()){
                String paraName = matcher.group();
                System.out.println(paraName);
                paraName = paraName.replaceAll("￥\\{|\\}","");

                line = matcher.replaceFirst("*");

            }
        }

    }




    private void doScanner(String scanPackage) {

        URL url = this.getClass().
                getResource("/"+scanPackage.replaceAll("\\.","/"));


        File classPath = new File(url.getFile());

        for (File file : Objects.requireNonNull(classPath.listFiles())){
            if (file.isDirectory()){
                System.out.println(scanPackage + "." + file.getName());
                doScanner(scanPackage + "." + file.getName());
            }else {
                if (!file.getName().endsWith(".class")){ continue;}

                String className = (scanPackage + "." +file.getName().replace(".class",""));
                System.out.println((className));
            }
        }

        //System.out.println("--scanned classes--"+registryBeanClasses);
    }


}
