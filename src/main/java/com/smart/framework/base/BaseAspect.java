package com.smart.framework.base;

import com.smart.framework.proxy.Proxy;
import com.smart.framework.proxy.ProxyChain;
import java.lang.reflect.Method;

public abstract class BaseAspect implements Proxy {

    @Override
    public final void doProxy(ProxyChain proxyChain) throws Exception {
        Class<?> cls = proxyChain.getTargetClass();
        Method method = proxyChain.getTargetMethod();
        Object[] params = proxyChain.getMethodParams();

        begin();
        try {
            if (filter(cls, method, params)) {
                before(cls, method, params);
                Object result = proxyChain.doProxyChain();
                after(cls, method, params, result);
            } else {
                proxyChain.doProxyChain();
            }
        } catch (Exception e) {
            error(cls, method, params, e);
            throw e;
        } finally {
            end();
        }
    }

    public void begin() {
    }

    public boolean filter(Class<?> cls, Method method, Object[] params) {
        return true;
    }

    public void before(Class<?> cls, Method method, Object[] params) {
    }

    public void after(Class<?> cls, Method method, Object[] params, Object result) {
    }

    public void error(Class<?> cls, Method method, Object[] params, Exception e) {
    }

    public void end() {
    }
}
