package com.smart.plugin.job;

import com.smart.framework.helper.ClassHelper;
import com.smart.framework.util.CollectionUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobHelper {

    private static final Logger logger = LoggerFactory.getLogger(JobHelper.class);

    private static final Map<Class<?>, Scheduler> jobMap = new HashMap<Class<?>, Scheduler>();

    private static final JobFactory jobFactory = new SmartJobFactory();

    public static void startJob(Class<?> jobClass, String cron) {
        try {
            Scheduler scheduler = createScheduler(jobClass, cron);
            scheduler.start();
            jobMap.put(jobClass, scheduler);
            if (logger.isDebugEnabled()) {
                logger.debug("[Smart] start job: " + jobClass.getName());
            }
        } catch (SchedulerException e) {
            logger.error("启动 Job 出错！", e);
        }
    }

    public static void startJobAll() {
        List<Class<?>> jobClassList = ClassHelper.getClassListBySuper(BaseJob.class);
        if (CollectionUtil.isNotEmpty(jobClassList)) {
            for (Class<?> jobClass : jobClassList) {
                if (jobClass.isAnnotationPresent(Job.class)) {
                    String cron = jobClass.getAnnotation(Job.class).value();
                    startJob(jobClass, cron);
                }
            }
        }
    }

    public static void stopJob(Class<?> jobClass) {
        try {
            Scheduler scheduler = getScheduler(jobClass);
            scheduler.shutdown(true);
            jobMap.remove(jobClass); // 从 jobMap 中移除该 Job
            if (logger.isDebugEnabled()) {
                logger.debug("[Smart] stop job: " + jobClass.getName());
            }
        } catch (SchedulerException e) {
            logger.error("停止 Job 出错！", e);
        }
    }

    public static void stopJobAll() {
        for (Class<?> jobClass : jobMap.keySet()) {
            stopJob(jobClass);
        }
    }

    public static void pauseJob(Class<?> jobClass) {
        try {
            Scheduler scheduler = getScheduler(jobClass);
            scheduler.pauseJob(new JobKey(jobClass.getName()));
            if (logger.isDebugEnabled()) {
                logger.debug("[Smart] pause job: " + jobClass.getName());
            }
        } catch (SchedulerException e) {
            logger.error("暂停 Job 出错！", e);
        }
    }

    public static void resumeJob(Class<?> jobClass) {
        try {
            Scheduler scheduler = getScheduler(jobClass);
            scheduler.resumeJob(new JobKey(jobClass.getName()));
            if (logger.isDebugEnabled()) {
                logger.debug("[Smart] resume job: " + jobClass.getName());
            }
        } catch (SchedulerException e) {
            logger.error("恢复 Job 出错！", e);
        }
    }

    private static Scheduler createScheduler(Class<?> jobClass, String cron) {
        Scheduler scheduler = null;
        try {
            @SuppressWarnings("unchecked")
            JobDetail jobDetail = JobBuilder.newJob((Class<? extends org.quartz.Job>) jobClass)
                .withIdentity(jobClass.getName())
                .build();
            Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobClass.getName())
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.setJobFactory(jobFactory); // 从 Smart IOC 容器中获取 Job 实例
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            logger.error("创建 Scheduler 出错！", e);
        }
        return scheduler;
    }

    private static Scheduler getScheduler(Class<?> jobClass) {
        Scheduler scheduler = null;
        if (jobMap.containsKey(jobClass)) {
            scheduler = jobMap.get(jobClass);
        }
        return scheduler;
    }
}
