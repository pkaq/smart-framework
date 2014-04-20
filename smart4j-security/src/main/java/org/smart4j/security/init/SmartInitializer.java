package org.smart4j.security.init;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import org.apache.shiro.web.env.EnvironmentLoaderListener;

public class SmartInitializer implements ISmartInitializer {

    @Override
    public void init(ServletContext servletContext) {
        servletContext.addListener(EnvironmentLoaderListener.class);
        FilterRegistration.Dynamic shiroFilter = servletContext.addFilter("ShiroFilter", SmartShiroFilter.class);
        shiroFilter.addMappingForUrlPatterns(null, false, "/*");
    }
}
