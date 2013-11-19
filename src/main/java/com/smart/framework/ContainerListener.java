package com.smart.framework;

import com.smart.framework.helper.ConfigHelper;
import com.smart.framework.util.StringUtil;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContainerListener implements ServletContextListener {

    private final String wwwPath = ConfigHelper.getStringProperty(Constant.APP_WWW_PATH);
    private final String jspPath = ConfigHelper.getStringProperty(Constant.APP_JSP_PATH);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 初始化 Helper 类
        Smart.init();
        // 添加 Servlet 映射
        addServletMapping(sce.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    private void addServletMapping(ServletContext context) {
        // 用 DefaultServlet 映射所有静态资源
        registerDefaultServlet(context);
        // 用 JspServlet 映射所有 JSP 请求
        registerJspServlet(context);
        // 用 UploadServlet 映射 /upload.do 请求
        registerUploadServlet(context);
    }

    private void registerDefaultServlet(ServletContext context) {
        ServletRegistration defaultServletRegistration = context.getServletRegistration(Constant.SERVLET_DEFAULT);
        defaultServletRegistration.addMapping(Constant.REQUEST_FAVICON);
        if (StringUtil.isNotEmpty(wwwPath)) {
            defaultServletRegistration.addMapping(wwwPath + "*");
        }
    }

    private void registerJspServlet(ServletContext context) {
        if (StringUtil.isNotEmpty(jspPath)) {
            ServletRegistration jspServletRegistration = context.getServletRegistration(Constant.SERVLET_JSP);
            jspServletRegistration.addMapping(jspPath + "*");
        }
    }

    private void registerUploadServlet(ServletContext context) {
        ServletRegistration uploadServletRegistration = context.getServletRegistration(Constant.SERVLET_UPLOAD);
        uploadServletRegistration.addMapping(Constant.REQUEST_UPLOAD);
    }
}
