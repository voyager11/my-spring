package com.cjp.spring.webmvc.servlet;

import java.io.File;
import java.util.Locale;

public class EricViewResolver {

    private File templateRootDir;

    private final String DEFAULT_TEMPLATE_SUFFX=".html";

    public EricViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader()
                .getResource(templateRoot).getFile();

        templateRootDir = new File(templateRootPath);

    }

    public EricView resolveViewName(String viewName, Locale locale) throws Exception{


        if (null == viewName || "".equals(viewName.trim())) return null;

        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFX) ? viewName: (viewName + DEFAULT_TEMPLATE_SUFFX);

        File templateFile = new File((templateRootDir.getPath() + "/" +viewName).replaceAll("/+","/"));

        return new EricView(templateFile);
    }


}
