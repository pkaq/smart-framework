package org.smart4j.sso.init;

import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@HandlesTypes(SmartInitializer.class)
public class SmartServletContainerInitializer implements ServletContainerInitializer {

    private static final Logger logger = LoggerFactory.getLogger(SmartServletContainerInitializer.class);

    @Override
    public void onStartup(Set<Class<?>> webApplicationInitializerClassSet, ServletContext servletContext) throws ServletException {
        try {
            for (Class<?> webApplicationInitializerClass : webApplicationInitializerClassSet) {
                SmartInitializer smartInitializer = (SmartInitializer) webApplicationInitializerClass.newInstance();
                smartInitializer.init(servletContext);
            }
        } catch (Exception e) {
            logger.error("初始化出错！", e);
        }
    }
}