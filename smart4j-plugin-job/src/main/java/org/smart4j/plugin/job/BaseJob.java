package org.smart4j.plugin.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(BaseJob.class);

    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            execute();
        } catch (Exception e) {
            logger.error("执行 Job 出错！", e);
        }
    }

    public abstract void execute();
}
