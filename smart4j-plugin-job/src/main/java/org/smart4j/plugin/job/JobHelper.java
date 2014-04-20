package org.smart4j.plugin.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.core.ClassHelper;
import org.smart4j.framework.util.CollectionUtil;
import org.smart4j.framework.util.DateUtil;
import org.smart4j.framework.util.StringUtil;

public class JobHelper {

    private static final Logger logger = LoggerFactory.getLogger(JobHelper.class);

    private static final Map<Class<?>, Scheduler> jobMap = new HashMap<Class<?>, Scheduler>();

    private static final JobFactory jobFactory = new SmartJobFactory();

    public static void startJob(Class<?> jobClass, String cron) {
        JobDetail jobDetail = createJobDetail(jobClass);
        Trigger trigger = createTrigger(jobClass, cron);
        doStartJob(jobClass, jobDetail, trigger);
    }

    public static void startJob(Class<?> jobClass, int second) {
        startJob(jobClass, second, 0, "", "");
    }

    public static void startJob(Class<?> jobClass, int second, int count) {
        startJob(jobClass, second, count, "", "");
    }

    public static void startJob(Class<?> jobClass, int second, int count, String start, String end) {
        JobDetail jobDetail = createJobDetail(jobClass);
        Trigger trigger = createTrigger(jobClass, second, count, start, end);
        doStartJob(jobClass, jobDetail, trigger);
    }

    private static void doStartJob(Class<?> jobClass, JobDetail jobDetail, Trigger trigger) {
        try {
            Scheduler scheduler = createScheduler(jobDetail, trigger);
            scheduler.start();
            jobMap.put(jobClass, scheduler);
            logger.debug("[Smart] start job: " + jobClass.getName());
        } catch (SchedulerException e) {
            logger.error("启动 Job 出错！", e);
        }
    }

    public static void startJobAll() {
        List<Class<?>> jobClassList = ClassHelper.getClassListBySuper(BaseJob.class);
        if (CollectionUtil.isNotEmpty(jobClassList)) {
            for (Class<?> jobClass : jobClassList) {
                if (jobClass.isAnnotationPresent(Job.class)) {
                    Job job = jobClass.getAnnotation(Job.class);
                    Job.Type type = job.type();
                    if (type == Job.Type.CRON) {
                        String cron = job.value();
                        startJob(jobClass, cron);
                    } else if (type == Job.Type.TIMER) {
                        int second = job.second();
                        int count = job.count();
                        String start = job.start();
                        String end = job.end();
                        startJob(jobClass, second, count, start, end);
                    }
                }
            }
        }
    }

    public static void stopJob(Class<?> jobClass) {
        try {
            Scheduler scheduler = getScheduler(jobClass);
            scheduler.shutdown(true);
            jobMap.remove(jobClass); // 从 jobMap 中移除该 Job
            logger.debug("[Smart] stop job: " + jobClass.getName());
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
            logger.debug("[Smart] pause job: " + jobClass.getName());
        } catch (SchedulerException e) {
            logger.error("暂停 Job 出错！", e);
        }
    }

    public static void resumeJob(Class<?> jobClass) {
        try {
            Scheduler scheduler = getScheduler(jobClass);
            scheduler.resumeJob(new JobKey(jobClass.getName()));
            logger.debug("[Smart] resume job: " + jobClass.getName());
        } catch (SchedulerException e) {
            logger.error("恢复 Job 出错！", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static JobDetail createJobDetail(Class<?> jobClass) {
        return JobBuilder.newJob((Class<? extends org.quartz.Job>) jobClass)
            .withIdentity(jobClass.getName())
            .build();
    }

    private static CronTrigger createTrigger(Class<?> jobClass, String cron) {
        return TriggerBuilder.newTrigger()
            .withIdentity(jobClass.getName())
            .withSchedule(CronScheduleBuilder.cronSchedule(cron))
            .build();
    }

    private static SimpleTrigger createTrigger(Class<?> jobClass, int second) {
        return TriggerBuilder.newTrigger()
            .withIdentity(jobClass.getName())
            .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(second))
            .build();
    }

    private static SimpleTrigger createTrigger(Class<?> jobClass, int second, int count) {
        return createTrigger(jobClass, second, count, null, null);
    }

    private static SimpleTrigger createTrigger(Class<?> jobClass, int second, int count, String start, String end) {
        TriggerBuilder<SimpleTrigger> triggerBuilder;
        if (count > 0) {
            triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(jobClass.getName())
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForTotalCount(count, second));
        } else {
            triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(jobClass.getName())
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(second));
        }
        return doCreateTrigger(triggerBuilder, start, end);
    }

    private static SimpleTrigger doCreateTrigger(TriggerBuilder<SimpleTrigger> triggerBuilder, String start, String end) {
        if (StringUtil.isNotEmpty(start)) {
            triggerBuilder.startAt(DateUtil.parseDatetime(start));
        }
        if (StringUtil.isNotEmpty(end)) {
            triggerBuilder.endAt(DateUtil.parseDatetime(end));
        }
        return triggerBuilder.build();
    }

    private static Scheduler createScheduler(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.setJobFactory(jobFactory); // 从 Smart IOC 容器中获取 Job 实例
        scheduler.scheduleJob(jobDetail, trigger);
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
