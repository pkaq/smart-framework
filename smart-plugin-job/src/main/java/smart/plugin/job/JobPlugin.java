package smart.plugin.job;

import smart.framework.plugin.Plugin;

public class JobPlugin implements Plugin {

    @Override
    public void init() {
        JobHelper.startJobAll();
    }

    @Override
    public void destroy() {
        JobHelper.stopJobAll();
    }
}
