package org.smart4j.plugin.job;

import org.smart4j.framework.plugin.Plugin;

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
