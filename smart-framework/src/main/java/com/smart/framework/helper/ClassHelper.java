package com.smart.framework.helper;

import com.smart.framework.util.ClassUtil;
import java.lang.annotation.Annotation;
import java.util.List;

public class ClassHelper {

    private static final String packageName = ConfigHelper.getConfigString("app.package");

    public static List<Class<?>> getClassList() {
        return ClassUtil.getClassList(packageName, true);
    }

    public static List<Class<?>> getClassListBySuper(Class<?> superClass) {
        return ClassUtil.getClassListBySuper(packageName, superClass);
    }

    public static List<Class<?>> getClassListByAnnotation(Class<? extends Annotation> annotationClass) {
        return ClassUtil.getClassListByAnnotation(packageName, annotationClass);
    }
}
