package org.smart4j.plugin.cache;

import org.smart4j.framework.plugin.Plugin;

public class CachePlugin implements Plugin {

    @Override
    public void init() {
        new CacheThread().start();
    }

    @Override
    public void destroy() {
    }
}
