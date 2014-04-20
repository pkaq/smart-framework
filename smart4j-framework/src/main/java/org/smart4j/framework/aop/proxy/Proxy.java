package org.smart4j.framework.aop.proxy;

public interface Proxy {

    Object doProxy(ProxyChain proxyChain) throws Throwable;
}
