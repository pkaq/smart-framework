package org.smart4j.plugin.search;

import org.smart4j.framework.plugin.Plugin;

public class SearchPlugin implements Plugin {

    @Override
    public void init() {
        new IndexThread().start();
    }

    @Override
    public void destroy() {
    }
}
