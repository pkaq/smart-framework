package org.smart4j.framework.plugin;

import java.util.List;
import org.smart4j.framework.aop.proxy.Proxy;

public abstract class PluginProxy implements Proxy {

    public abstract List<Class<?>> getTargetClassList();
}
