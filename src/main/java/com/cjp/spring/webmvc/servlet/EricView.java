package com.cjp.spring.webmvc.servlet;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EricView {

    private final String DEFAULT_CONTENT_TYPE="text/html;charset=utf-8";

    private File viewFile;

    public EricView(File vieFile) {
        this.viewFile = vieFile;
    }

    public void render(Map<String,?> model,
                       HttpServletRequest request,
                       HttpServletResponse response) throws Exception{


        StringBuilder sb = new StringBuilder();

        RandomAccessFile ra = new RandomAccessFile(this.viewFile,"r");

        String line = null;

        while (null != (line = ra.readLine())){
            line = new String(line.getBytes("ISO-8859-1"),"utf-8");
            //replace html
            Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()){
                String paraName = matcher.group();
                paraName = paraName.replaceAll("￥\\{|\\}","");
                Object paramValue = model.get(paraName);
                if (paramValue == null) continue;
                line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                matcher = pattern.matcher(line);

            }
            sb.append(line);
        }

        response.setCharacterEncoding("utf-8");
//        response.setContentType(DEFAULT_CONTENT_TYPE);
        response.getWriter().write(sb.toString());


    }

     private static String makeStringForRegExp(String str){
        return str
                .replace("\\","\\\\")
                .replace("*","\\*")
                .replace("+","\\+")
                .replace("{","\\{")
                .replace("}","\\}")
                .replace("(","\\(")
                .replace("^","\\^")
                .replace("[","\\[")
                .replace("?","\\?")
                .replace("|","\\|")
                .replace(")","\\)")
                .replace("$","\\$")
                .replace(".","\\.")
                .replace("&","\\&");

     }
}
