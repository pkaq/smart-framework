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

    private static final String wwwPath = ConfigHelper.getStringProperty(FrameworkConstant.APP_WWW_PATH);
    private static final String jspPath = ConfigHelper.getStringProperty(FrameworkConstant.APP_JSP_PATH);

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
        ServletRegistration defaultServletReg = context.getServletRegistration(FrameworkConstant.DEFAULT_SERVLET_NAME);
        defaultServletReg.addMapping(FrameworkConstant.FAVICON_ICO_URL);
        if (StringUtil.isNotEmpty(wwwPath)) {
            defaultServletReg.addMapping(wwwPath + "*");
        }
    }

    private void registerJspServlet(ServletContext context) {
        if (StringUtil.isNotEmpty(jspPath)) {
            ServletRegistration jspServletReg = context.getServletRegistration(FrameworkConstant.JSP_SERVLET_NAME);
            jspServletReg.addMapping(jspPath + "*");
        }
    }

    private void registerUploadServlet(ServletContext context) {
        ServletRegistration uploadServletReg = context.getServletRegistration(FrameworkConstant.UPLOAD_SERVLET_NAME);
        uploadServletReg.addMapping(FrameworkConstant.UPLOAD_SERVLET_URL);
    }
}
