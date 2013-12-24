package com.smart.framework.base;

import com.smart.framework.proxy.Proxy;
import com.smart.framework.proxy.ProxyChain;
import java.lang.reflect.Method;

public abstract class BaseAspect implements Proxy {

    @Override
    public final Object doProxy(ProxyChain proxyChain) throws Exception {
        Object result = null;

        Class<?> cls = proxyChain.getTargetClass();
        Method method = proxyChain.getTargetMethod();
        Object[] params = proxyChain.getMethodParams();

        begin();
        try {
            if (intercept(cls, method, params)) {
                before(cls, method, params);
                result = proxyChain.doProxyChain();
                after(cls, method, params, result);
            } else {
                result = proxyChain.doProxyChain();
            }
        } catch (Exception e) {
            error(cls, method, params, e);
            throw e;
        } finally {
            end();
        }

        return result;
    }

    public void begin() {
    }

    public boolean intercept(Class<?> cls, Method method, Object[] params) throws Exception {
        return true;
    }

    public void before(Class<?> cls, Method method, Object[] params) throws Exception {
    }

    public void after(Class<?> cls, Method method, Object[] params, Object result) throws Exception {
    }

    public void error(Class<?> cls, Method method, Object[] params, Exception e) {
    }

    public void end() {
    }
}
