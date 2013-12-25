package com.smart.framework.proxy;

import java.util.List;

public abstract class PluginProxy implements Proxy {

    public abstract List<Class<?>> getTargetClassList();
}
