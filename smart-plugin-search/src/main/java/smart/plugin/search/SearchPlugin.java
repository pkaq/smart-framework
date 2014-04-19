package smart.plugin.search;

import smart.framework.plugin.Plugin;

public class SearchPlugin implements Plugin {

    @Override
    public void init() {
        new IndexThread().start();
    }

    @Override
    public void destroy() {
    }
}
