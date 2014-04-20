package org.smart4j.plugin.job.test;

import org.junit.Test;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzJobTest {

    @Test
    public void test() {
        try {
            JobDetail jobDetail = JobBuilder.newJob(QuartzHelloJob.class).build();

//            ScheduleBuilder<?> builder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5).repeatForever();
            ScheduleBuilder<?> builder = CronScheduleBuilder.cronSchedule("0/1 * * * * ?");

            Trigger trigger = TriggerBuilder.newTrigger().withSchedule(builder).build();

            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();

            sleep(3000);

            scheduler.shutdown(true);

            sleep(3000);
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}