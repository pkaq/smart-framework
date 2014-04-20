package org.smart4j.framework.core;

import java.lang.annotation.Annotation;
import java.util.List;
import org.smart4j.framework.util.ClassUtil;

public class ClassHelper {

    private static final String packageName = ConfigHelper.getConfigString("app.package");

    public static List<Class<?>> getClassList() {
        return ClassUtil.getClassList(packageName);
    }

    public static List<Class<?>> getClassListBySuper(Class<?> superClass) {
        return ClassUtil.getClassListBySuper(packageName, superClass);
    }

    public static List<Class<?>> getClassListByAnnotation(Class<? extends Annotation> annotationClass) {
        return ClassUtil.getClassListByAnnotation(packageName, annotationClass);
    }
}
