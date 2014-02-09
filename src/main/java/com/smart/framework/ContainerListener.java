package com.smart.framework;

import com.smart.framework.helper.ConfigHelper;
import com.smart.framework.helper.ModuleHelper;
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
        ServletContext context = sce.getServletContext();
        // 初始化相关 Helper 类
        HelperLoader.init();
        // 添加 Servlet 映射
        addServletMapping(context);
        // 安装模块
        installModule(context);
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
        ServletRegistration defaultServletReg = context.getServletRegistration(FrameworkConstant.DEFAULT_SERVLET_NAME);
        defaultServletReg.addMapping("/favicon.ico");
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

    private void installModule(ServletContext context) {
        List<Module> moduleList = ModuleHelper.getModuleList();
        for (Module module : moduleList) {
            module.install(context);
        }
    }

    private void destroyPlugin() {
        List<Plugin> pluginList = PluginHelper.getPluginList();
        for (Plugin plugin : pluginList) {
            plugin.destroy();
        }
    }
}
