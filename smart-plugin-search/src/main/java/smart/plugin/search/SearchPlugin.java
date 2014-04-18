package smart.plugin.search;

import smart.framework.Plugin;

public class SearchPlugin implements Plugin {

    @Override
    public void init() {
        new IndexThread().start();
    }

    @Override
    public void destroy() {
    }
}
