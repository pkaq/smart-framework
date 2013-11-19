package com.smart.framework.aspect;

import com.smart.framework.proxy.Proxy;
import java.util.List;

public abstract class PluginAspect implements Proxy {

    public abstract void initPlugin();

    public abstract List<Class<?>> getTargetClassList();
}
