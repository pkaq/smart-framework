package com.smart.framework.base;

import com.smart.framework.proxy.Proxy;
import com.smart.framework.proxy.ProxyChain;
import org.apache.log4j.Logger;

public abstract class BaseAspect implements Proxy {

    private static final Logger logger = Logger.getLogger(BaseAspect.class);

    @Override
    public final void doProxy(ProxyChain proxyChain) {
        String className = proxyChain.getTargetClass().getName();
        String methodName = proxyChain.getTargetMethod().getName();

        begin(className, methodName);
        try {
            if (filter(className, methodName)) {
                before(className, methodName);
                proxyChain.doProxyChain();
                after(className, methodName);
            } else {
                proxyChain.doProxyChain();
            }
        } catch (Exception e) {
            error(className, methodName, e);
            throw new RuntimeException(e);
        } finally {
            end(className, methodName);
        }
    }

    public void begin(String className, String methodName) {
    }

    public boolean filter(String className, String methodName) {
        return true;
    }

    public void before(String className, String methodName) {
    }

    public void after(String className, String methodName) {
    }

    public void error(String className, String methodName, Exception e) {
    }

    public void end(String className, String methodName) {
    }
}
