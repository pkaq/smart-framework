package com.smart.plugin.cache;

import com.smart.framework.Plugin;

public class CachePlugin implements Plugin {

    @Override
    public void init() {
        new CacheThread().start();
    }

    @Override
    public void destroy() {
    }
}
