package com.smart.framework.util;

import java.lang.reflect.Field;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

public class ObjectUtil {

    private static final Logger logger = Logger.getLogger(ObjectUtil.class);

    // 设置字段值
    public static void setField(Object obj, String fieldName, Object fieldValue) {
        try {
            if (PropertyUtils.isWriteable(obj, fieldName)) {
                PropertyUtils.setProperty(obj, fieldName, fieldValue);
            }
        } catch (Exception e) {
            logger.error("设置字段出错！", e);
            throw new RuntimeException(e);
        }
    }

    // 获取字段值
    public static Object getFieldValue(Object obj, String fieldName) {
        Object propertyValue = null;
        try {
            if (PropertyUtils.isReadable(obj, fieldName)) {
                propertyValue = PropertyUtils.getProperty(obj, fieldName);
            }
        } catch (Exception e) {
            logger.error("获取字段出错！", e);
            throw new RuntimeException(e);
        }
        return propertyValue;
    }

    // 复制所有字段
    public static void copyFields(Object source, Object target) {
        try {
            for (Field field : source.getClass().getDeclaredFields()) {
                field.setAccessible(true); // 可操作私有字段
                field.set(target, field.get(source));
            }
        } catch (Exception e) {
            logger.error("复制字段出错！", e);
            throw new RuntimeException(e);
        }
    }

    // 通过反射创建实例
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className) {
        T instance;
        try {
            Class<?> commandClass = Class.forName(className);
            instance = (T) commandClass.newInstance();
        } catch (Exception e) {
            logger.error("创建实例出错！", e);
            throw new RuntimeException(e);
        }
        return instance;
    }
}
