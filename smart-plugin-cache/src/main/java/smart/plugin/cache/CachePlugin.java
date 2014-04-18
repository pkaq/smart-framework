package smart.plugin.cache;

import smart.framework.Plugin;

public class CachePlugin implements Plugin {

    @Override
    public void init() {
        new CacheThread().start();
    }

    @Override
    public void destroy() {
    }
}
