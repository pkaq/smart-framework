package org.smart4j.framework.core.impl;

import java.lang.annotation.Annotation;
import java.util.List;
import org.smart4j.framework.core.ClassScanner;

/**
 * 抽象类扫描器
 *
 * @author huangyong
 * @since 2.3
 */
public class DefaultClassScanner implements ClassScanner {

    @Override
    public List<Class<?>> getClassList(String packageName) {
        return new ScannerTemplate(packageName) {
            @Override
            public boolean checkAddClass(Class<?> cls) {
                return cls.getName().substring(0, cls.getName().lastIndexOf(".")).equals(packageName);
            }
        }.getClassList();
    }

    @Override
    public List<Class<?>> getClassListByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
        return new ScannerTemplate(packageName) {
            @Override
            public boolean checkAddClass(Class<?> cls) {
                return cls.isAnnotationPresent(annotationClass);
            }
        }.addAnnotationClass(annotationClass).getClassList();
    }

    @Override
    public List<Class<?>> getClassListBySuper(String packageName, Class<?> superClass) {
        return new ScannerTemplate(packageName) {
            @Override
            public boolean checkAddClass(Class<?> cls) {
                return superClass.isAssignableFrom(cls) && !superClass.equals(cls);
            }
        }.addSupperClass(superClass).getClassList();
    }
}
