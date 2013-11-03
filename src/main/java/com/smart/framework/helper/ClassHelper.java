package com.smart.framework.helper;

import com.smart.framework.util.ClassUtil;
import java.lang.annotation.Annotation;
import java.util.List;

public class ClassHelper {

    private static final ClassHelper instance = new ClassHelper();

    private final String packageName = ConfigHelper.getInstance().getStringProperty("package");

    public static ClassHelper getInstance() {
        return instance;
    }

    public List<Class<?>> getClassListByPackage(String pkg) {
        return ClassUtil.getClassList(pkg, true);
    }

    public List<Class<?>> getClassListBySuper(Class<?> superClass) {
        return ClassUtil.getClassListBySuper(packageName, superClass);
    }

    public List<Class<?>> getClassListByAnnotation(Class<? extends Annotation> annotationClass) {
        return ClassUtil.getClassListByAnnotation(packageName, annotationClass);
    }
}
