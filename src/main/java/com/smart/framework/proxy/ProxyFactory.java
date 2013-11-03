package com.smart.framework.proxy;

import java.lang.reflect.Method;
import java.util.List;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ProxyFactory {

    private Class<?> targetClass;
    private List<Proxy> proxyList;

    public ProxyFactory(Class<?> targetClass, List<Proxy> proxyList) {
        this.targetClass = targetClass;
        this.proxyList = proxyList;
    }

    @SuppressWarnings("unchecked")
    public <T> T createProxy() {
        return (T) Enhancer.create(targetClass, new MethodInterceptor() {
            @Override
            public Object intercept(Object target, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                ProxyChain proxyChain = new ProxyChain(targetClass, target, method, args, proxy, proxyList);
                return proxyChain.doProxyChain();
            }
        });
    }
}
