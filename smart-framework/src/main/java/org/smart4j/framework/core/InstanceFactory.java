package org.smart4j.framework.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.smart4j.framework.ds.DataSourceFactory;
import org.smart4j.framework.ds.impl.DefaultDataSourceFactory;
import org.smart4j.framework.mvc.HandlerInvoker;
import org.smart4j.framework.mvc.HandlerMapping;
import org.smart4j.framework.mvc.ViewResolver;
import org.smart4j.framework.mvc.impl.DefaultHandlerInvoker;
import org.smart4j.framework.mvc.impl.DefaultHandlerMapping;
import org.smart4j.framework.mvc.impl.DefaultViewResolver;
import org.smart4j.framework.util.ObjectUtil;
import org.smart4j.framework.util.StringUtil;

/**
 * 实例工厂
 *
 * @author huangyong
 * @since 2.3
 */
public class InstanceFactory {

    // 用于缓存对应的实例
    private static final Map<String, Object> cache = new ConcurrentHashMap<String, Object>();

    private static final String DS_FACTORY = "smart.ds_factory";
    private static final String ACTION_HANDLER = "smart.action_handler";
    private static final String HANDLER_INVOKER = "smart.handler_invoker";
    private static final String VIEW_RESOLVER = "smart.view_resolver";

    public static DataSourceFactory createDataSourceFactory() {
        return createInstance(DS_FACTORY, DefaultDataSourceFactory.class);
    }

    public static HandlerMapping createActionHandler() {
        return createInstance(ACTION_HANDLER, DefaultHandlerMapping.class);
    }

    public static HandlerInvoker createHandlerInvoker() {
        return createInstance(HANDLER_INVOKER, DefaultHandlerInvoker.class);
    }

    public static ViewResolver createViewResolver() {
        return createInstance(VIEW_RESOLVER, DefaultViewResolver.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createInstance(String cacheKey, Class<T> defaultImplClass) {
        // 若缓存中存在对应的实例，则返回该实例
        if (cache.containsKey(cacheKey)) {
            return (T) cache.get(cacheKey);
        }
        // 从配置文件中获取相应的接口实现类配置
        String implClassName = ConfigHelper.getConfigString(cacheKey);
        // 若实现类配置不存在，则使用默认实现类
        if (StringUtil.isEmpty(implClassName)) {
            implClassName = defaultImplClass.getName();
        }
        // 通过反射创建该实现类对应的实例
        T instance = ObjectUtil.newInstance(implClassName);
        // 若该实例不为空，则将其放入缓存
        if (instance != null) {
            cache.put(cacheKey, instance);
        }
        // 返回该实例
        return instance;
    }
}
