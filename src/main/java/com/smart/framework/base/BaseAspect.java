package com.smart.framework.base;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.log4j.Logger;

public abstract class BaseAspect implements MethodInterceptor {

    private static final Logger logger = Logger.getLogger(BaseAspect.class);

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> cls) {
        return (T) Enhancer.create(cls, this);
    }

    @Override
    public Object intercept(Object proxy, Method methodTarget, Object[] args, MethodProxy methodProxy) throws Throwable {
        begin(methodTarget, args);
        Object result = null;
        try {
            if (filter(methodTarget, args)) {
                before(methodTarget, args);
                result = methodProxy.invokeSuper(proxy, args);
                after(methodTarget, args);
            } else {
                result = methodProxy.invokeSuper(proxy, args);
            }
        } catch (Exception e) {
            error(methodTarget, args, e);
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            end(methodTarget, args);
        }
        return result;
    }

    protected void begin(Method method, Object[] args) {
    }

    protected boolean filter(Method method, Object[] args) {
        return true;
    }

    protected void before(Method method, Object[] args) {
    }

    protected void after(Method method, Object[] args) {
    }

    protected void error(Method method, Object[] args, Exception e) {
    }

    protected void end(Method method, Object[] args) {
    }
}
