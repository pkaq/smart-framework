package com.smart.framework.proxy;

import java.lang.reflect.Method;
import java.util.List;
import net.sf.cglib.proxy.MethodProxy;

public class ProxyChain {

    private Class<?> targetClass;
    private Object targetObject;
    private Method targetMethod;
    private Object[] methodParams;
    private MethodProxy methodProxy;

    private List<Proxy> proxyList;
    private int currentProxyIndex = 0;

    public ProxyChain(Class<?> targetClass, Object targetObject, Method targetMethod, Object[] methodParams, MethodProxy methodProxy, List<Proxy> proxyList) {
        this.targetClass = targetClass;
        this.targetObject = targetObject;
        this.targetMethod = targetMethod;
        this.methodParams = methodParams;
        this.methodProxy = methodProxy;
        this.proxyList = proxyList;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public Object[] getMethodParams() {
        return methodParams;
    }

    public Object doProxyChain() throws Exception {
        Object methodResult;
        if (currentProxyIndex < proxyList.size()) {
            methodResult = proxyList.get(currentProxyIndex++).doProxy(this);
        } else {
            try {
                methodResult = methodProxy.invokeSuper(targetObject, methodParams);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
        return methodResult;
    }
}
