package com.smart.plugin.search;

import com.smart.framework.Plugin;

public class SearchPlugin implements Plugin {

    @Override
    public void init() {
        new IndexThread().start();
    }

    @Override
    public void destroy() {
    }
}
