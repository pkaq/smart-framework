package com.smart.plugin.job;

import com.smart.framework.Plugin;

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
