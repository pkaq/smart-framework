package smart.framework.plugin;

import java.util.List;
import smart.framework.aop.proxy.Proxy;

public abstract class PluginProxy implements Proxy {

    public abstract List<Class<?>> getTargetClassList();
}
