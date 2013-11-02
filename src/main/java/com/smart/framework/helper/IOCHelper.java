package com.smart.framework.helper;

import com.smart.framework.annotation.Impl;
import com.smart.framework.annotation.Inject;
import com.smart.framework.util.ArrayUtil;
import com.smart.framework.util.CollectionUtil;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class IOCHelper {

    private static final Logger logger = Logger.getLogger(IOCHelper.class);

    private static final IOCHelper instance = new IOCHelper();

    private IOCHelper() {
    }

    public static IOCHelper getInstance() {
        return instance;
    }

    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("初始化 IOCHelper");
        }
        try {
            // 获取并遍历所有的 Bean 类
            Map<Class<?>, Object> beanMap = BeanHelper.getInstance().getBeanMap();
            for (Map.Entry<Class<?>, Object> beanEntry : beanMap.entrySet()) {
                // 获取 Bean 类与 Bean 实例
                Class<?> beanClass = beanEntry.getKey();
                Object beanInstance = beanEntry.getValue();
                // 获取 Bean 类中所有的字段（不包括父类中的方法）
                Field[] beanFields = beanClass.getDeclaredFields();
                if (ArrayUtil.isNotEmpty(beanFields)) {
                    // 遍历所有的 Bean 字段
                    for (Field beanField : beanFields) {
                        // 判断当前 Bean 字段是否带有 @Inject 注解
                        if (beanField.isAnnotationPresent(Inject.class)) {
                            // 获取 Bean 字段对应的实现类
                            Class<?> implementClass = getImplementClass(beanField);
                            // 若存在实现类，则执行以下代码
                            if (implementClass != null) {
                                // 从 Bean Map 中获取该实现类对应的实现类实例
                                Object implementInstance = beanMap.get(implementClass);
                                // 设置该 Bean 字段的值
                                if (implementInstance != null) {
                                    beanField.setAccessible(true); // 取消类型安全检测（可提高反射性能）
                                    beanField.set(beanInstance, implementInstance); // beanInstance 是普通实例，或 CGLib 动态代理实例（不能使 JDK 动态代理实例）
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("初始化 IOCHelper 出错！", e);
        }
    }

    private Class<?> getImplementClass(Field beanField) {
        // 定义实现类对象
        Class<?> implementClass = null;
        // 获取 Bean 字段对应的接口
        Class<?> interfaceClass = beanField.getType();
        // 判断接口上是否标注了 @Impl 注解
        if (interfaceClass.isAnnotationPresent(Impl.class)) {
            // 获取强制指定的实现类
            implementClass = interfaceClass.getAnnotation(Impl.class).value();
        } else {
            // 获取该接口所有的实现类
            List<Class<?>> implementClassList = ClassHelper.getInstance().getClassListByInterface(interfaceClass);
            if (CollectionUtil.isNotEmpty(implementClassList)) {
                // 获取第一个实现类
                implementClass = implementClassList.get(0);
            }
        }
        // 返回实现类对象
        return implementClass;
    }
}
