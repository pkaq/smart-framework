package org.smart4j.framework.core;

import java.lang.annotation.Annotation;
import java.util.List;
import org.smart4j.framework.util.ClassUtil;

public class ClassHelper {

    private static final String BASE_PACKAGE = ConfigHelper.getConfigString("app.base_package");

    public static List<Class<?>> getClassList() {
        return ClassUtil.getClassList(BASE_PACKAGE);
    }

    public static List<Class<?>> getClassListBySuper(Class<?> superClass) {
        return ClassUtil.getClassListBySuper(BASE_PACKAGE, superClass);
    }

    public static List<Class<?>> getClassListByAnnotation(Class<? extends Annotation> annotationClass) {
        return ClassUtil.getClassListByAnnotation(BASE_PACKAGE, annotationClass);
    }
}
