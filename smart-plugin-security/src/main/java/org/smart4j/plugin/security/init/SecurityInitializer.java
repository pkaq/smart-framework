package org.smart4j.plugin.security.init;

import java.util.Set;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.shiro.web.env.EnvironmentLoaderListener;

/**
 * Security 初始化器
 * <br/>
 * 用于注册 Shiro 所需要的 Listener 与 Filter
 *
 * @author huangyong
 * @since 2.3
 */
public class SecurityInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> handlesTypes, ServletContext servletContext) throws ServletException {
        // 注册 Listener
        servletContext.addListener(EnvironmentLoaderListener.class);
        // 注册 Filter
        FilterRegistration.Dynamic shiroFilter = servletContext.addFilter("ShiroFilter", SecurityFilter.class);
        shiroFilter.addMappingForUrlPatterns(null, false, "/*");
    }
}
