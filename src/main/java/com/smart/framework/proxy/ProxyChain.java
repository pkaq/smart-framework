package com.smart.framework.proxy;

import com.smart.framework.util.CollectionUtil;
import java.lang.reflect.Method;
import java.util.List;
import net.sf.cglib.proxy.MethodProxy;

public class ProxyChain {

    private List<Proxy> proxyList;
    private int currentProxyIndex;

    private Class<?> targetClass;
    private Object targetObject;
    private Method targetMethod;
    private Object[] methodParams;
    private MethodProxy methodProxy;
    private Object methodResult;

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

    public Object getTargetObject() {
        return targetObject;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public Object[] getMethodParams() {
        return methodParams;
    }

    public MethodProxy getMethodProxy() {
        return methodProxy;
    }

    public Object getMethodResult() {
        return methodResult;
    }

    public void doProxyChain() throws Exception {
        if (CollectionUtil.isNotEmpty(proxyList) && currentProxyIndex < proxyList.size()) {
            proxyList.get(currentProxyIndex++).doProxy(this);
        } else {
            try {
                methodResult = methodProxy.invokeSuper(targetObject, methodParams);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
    }
}
