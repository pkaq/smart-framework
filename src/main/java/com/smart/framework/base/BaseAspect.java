package com.smart.framework.base;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.log4j.Logger;

public abstract class BaseAspect implements MethodInterceptor {

    private static final Logger logger = Logger.getLogger(BaseAspect.class);

    private Class<?> cls;

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> cls) {
        this.cls = cls;
        return (T) Enhancer.create(cls, this);
    }

    @Override
    public final Object intercept(Object proxy, Method methodTarget, Object[] args, MethodProxy methodProxy) throws Throwable {
        begin(methodTarget, args);
        Object result = null;
        try {
            if (filter(cls, methodTarget, args)) {
                before(methodTarget, args);
                result = methodProxy.invokeSuper(proxy, args);
                after(methodTarget, args);
            } else {
                result = methodProxy.invokeSuper(proxy, args);
            }
        } catch (Exception e) {
            error(methodTarget, args, e);
            throw e;
        } finally {
            end(methodTarget, args);
        }
        return result;
    }

    public void begin(Method method, Object[] args) {
    }

    public boolean filter(Class<?> cls, Method method, Object[] args) {
        return true;
    }

    public void before(Method method, Object[] args) {
    }

    public void after(Method method, Object[] args) {
    }

    public void error(Method method, Object[] args, Exception e) {
    }

    public void end(Method method, Object[] args) {
    }
}
