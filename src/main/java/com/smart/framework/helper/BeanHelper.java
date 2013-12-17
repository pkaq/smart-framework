package com.smart.framework.helper;

import com.smart.framework.annotation.Action;
import com.smart.framework.annotation.Aspect;
import com.smart.framework.annotation.Bean;
import com.smart.framework.annotation.Service;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanHelper {

    private static final Logger logger = LoggerFactory.getLogger(BeanHelper.class);

    private static final Map<Class<?>, Object> beanMap = new HashMap<Class<?>, Object>(); // Bean 类 => Bean 实例

    static {
        try {
            // 获取并遍历所有的 Bean（带有 @Bean/@Service/@Action/@Aspect 注解的类）
            Set<Class<?>> beanClassSet = getBeanClassSet();
            for (Class<?> beanClass : beanClassSet) {
                // 创建 Bean 实例
                Object beanInstance = beanClass.newInstance();
                // 将 Bean 实例放入 Bean Map 中（键为 Bean 类，值为 Bean 实例）
                beanMap.put(beanClass, beanInstance);
            }
        } catch (Exception e) {
            logger.error("初始化 BeanHelper 出错！", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Set<Class<?>> getBeanClassSet() {
        Set<Class<?>> beanClassSet = new HashSet<Class<?>>();
        Class[] annotationClassArray = {Bean.class, Service.class, Action.class, Aspect.class};
        for (Class annotationClass : annotationClassArray) {
            beanClassSet.addAll(ClassHelper.getClassListByAnnotation(annotationClass));
        }
        return beanClassSet;
    }

    public static Map<Class<?>, Object> getBeanMap() {
        return beanMap;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> cls) {
        if (!beanMap.containsKey(cls)) {
            throw new RuntimeException("无法根据类名获取实例！" + cls);
        }
        return (T) beanMap.get(cls);
    }
}
