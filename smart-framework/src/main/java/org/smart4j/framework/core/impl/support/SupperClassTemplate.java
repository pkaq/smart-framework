package org.smart4j.framework.core.impl.support;

/**
 * 用于获取子类的模板类
 *
 * @author huangyong
 * @since 2.3
 */
public abstract class SupperClassTemplate extends ClassTemplate {

    protected final Class<?> superClass;

    protected SupperClassTemplate(String packageName, Class<?> superClass) {
        super(packageName);
        this.superClass = superClass;
    }
}
