package com.smart.framework;

import com.smart.framework.helper.ConfigHelper;
import com.smart.framework.helper.PluginHelper;
import com.smart.framework.util.StringUtil;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContainerListener implements ServletContextListener {

    private static final String wwwPath = ConfigHelper.getConfigString(FrameworkConstant.APP_WWW_PATH);
    private static final String jspPath = ConfigHelper.getConfigString(FrameworkConstant.APP_JSP_PATH);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 初始化相关 Helper 类
        HelperLoader.init();
        // 添加 Servlet 映射
        addServletMapping(sce.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 销毁插件
        destroyPlugin();
    }

    private void addServletMapping(ServletContext context) {
        // 用 DefaultServlet 映射所有静态资源
        registerDefaultServlet(context);
        // 用 JspServlet 映射所有 JSP 请求
        registerJspServlet(context);
    }

    private void registerDefaultServlet(ServletContext context) {
        ServletRegistration defaultServlet = context.getServletRegistration(FrameworkConstant.DEFAULT_SERVLET_NAME);
        defaultServlet.addMapping("/index.html");
        defaultServlet.addMapping("/favicon.ico");
        if (StringUtil.isNotEmpty(wwwPath)) {
            defaultServlet.addMapping(wwwPath + "*");
        }
    }

    private void registerJspServlet(ServletContext context) {
        ServletRegistration jspServlet = context.getServletRegistration(FrameworkConstant.JSP_SERVLET_NAME);
        if (StringUtil.isNotEmpty(jspPath)) {
            jspServlet.addMapping(jspPath + "*");
        }
    }

    private void destroyPlugin() {
        List<Plugin> pluginList = PluginHelper.getPluginList();
        for (Plugin plugin : pluginList) {
            plugin.destroy();
        }
    }
}
