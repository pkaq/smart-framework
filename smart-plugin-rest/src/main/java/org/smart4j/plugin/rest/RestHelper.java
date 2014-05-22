package org.smart4j.plugin.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.provider.jsonp.JsonpInInterceptor;
import org.apache.cxf.jaxrs.provider.jsonp.JsonpPostStreamInterceptor;
import org.apache.cxf.jaxrs.provider.jsonp.JsonpPreStreamInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharingFilter;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.smart4j.framework.core.ConfigHelper;
import org.smart4j.framework.ioc.BeanHelper;
import org.smart4j.framework.util.StringUtil;

public class RestHelper {

    private static final List<Object> providerList = new ArrayList<Object>();
    private static final List<Interceptor<? extends Message>> inInterceptorList = new ArrayList<Interceptor<? extends Message>>();
    private static final List<Interceptor<? extends Message>> outInterceptorList = new ArrayList<Interceptor<? extends Message>>();

    static {
        // 添加 JSON Provider
        JacksonJsonProvider jsonProvider = new JacksonJsonProvider();
        providerList.add(jsonProvider);
        // 添加 Logging Interceptor
        boolean log = ConfigHelper.getBoolean("smart.plugin.rest.log");
        if (log) {
            LoggingInInterceptor loggingInInterceptor = new LoggingInInterceptor();
            inInterceptorList.add(loggingInInterceptor);
            LoggingOutInterceptor loggingOutInterceptor = new LoggingOutInterceptor();
            outInterceptorList.add(loggingOutInterceptor);
        }
        // 添加 JSONP Interceptor
        boolean jsonp = ConfigHelper.getBoolean("smart.plugin.rest.jsonp");
        if (jsonp) {
            JsonpInInterceptor jsonpInInterceptor = new JsonpInInterceptor();
            String jsonpFunction = ConfigHelper.getString("smart.plugin.rest.jsonp.function");
            jsonpInInterceptor.setCallbackParam(jsonpFunction);
            inInterceptorList.add(jsonpInInterceptor);
            JsonpPreStreamInterceptor jsonpPreStreamInterceptor = new JsonpPreStreamInterceptor();
            outInterceptorList.add(jsonpPreStreamInterceptor);
            JsonpPostStreamInterceptor jsonpPostStreamInterceptor = new JsonpPostStreamInterceptor();
            outInterceptorList.add(jsonpPostStreamInterceptor);
        }
        // 添加 CORS Provider
        boolean cors = ConfigHelper.getBoolean("smart.plugin.rest.cors");
        if (cors) {
            CrossOriginResourceSharingFilter corsProvider = new CrossOriginResourceSharingFilter();
            String corsOrigin = ConfigHelper.getString("smart.plugin.rest.cors.origin");
            corsProvider.setAllowOrigins(Arrays.asList(StringUtil.splitString(corsOrigin, ",")));
            providerList.add(corsProvider);
        }
    }

    // 发布 REST 服务
    public static void publishService(String wadl, Class<?> resourceClass) {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setAddress(wadl);
        factory.setResourceClasses(resourceClass);
        factory.setResourceProvider(resourceClass, new SingletonResourceProvider(BeanHelper.getBean(resourceClass)));
        factory.setProviders(providerList);
        factory.setInInterceptors(inInterceptorList);
        factory.setOutInterceptors(outInterceptorList);
        factory.create();
    }

    // 创建 REST 客户端
    public static <T> T createClient(String wadl, Class<? extends T> resourceClass) {
        return JAXRSClientFactory.create(wadl, resourceClass, providerList);
    }
}
