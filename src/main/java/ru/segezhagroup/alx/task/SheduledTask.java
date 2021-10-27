package ru.segezhagroup.alx.task;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.scheduler.*;
import com.atlassian.scheduler.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.TimeUnit;

@ExportAsService
@Named
public class SheduledTask implements JobRunner, InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(SheduledTask.class);

    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of(SheduledTask.class.getName());
    private static final long EVERY_MINUTE = TimeUnit.MINUTES.toMillis(1);
    private static final JobId JOB_ID = JobId.of(SheduledTask.class.getName());
    final JobConfig jobConfig = JobConfig.forJobRunnerKey(JOB_RUNNER_KEY).withRunMode(RunMode.RUN_LOCALLY).withSchedule(Schedule.forInterval(EVERY_MINUTE, null));


    private final SchedulerService scheduler;
    
    @Inject
    public SheduledTask(@ComponentImport SchedulerService scheduler) {
        this.scheduler = scheduler;
    }

    @Nullable
    @Override
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {

        System.out.println("run at " + jobRunnerRequest.getStartTime());
        log.warn("run at " + jobRunnerRequest.getStartTime());
        return JobRunnerResponse.success();
//        return null;
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("Stopping...");
        log.warn("Stopping...");

        scheduler.unscheduleJob(JOB_ID);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // примеры выражений крон
        // https://support.atlassian.com/jira-software-cloud/docs/construct-cron-expressions-for-a-filter-subscription/
        // 0 0 8 * * ? - каждый день в 8.00

        System.out.println("Starting...");
        log.warn("Starting ..... ");

        scheduler.registerJobRunner(JOB_RUNNER_KEY, this);
//        final JobConfig jobConfig = JobConfig.forJobRunnerKey(JOB_RUNNER_KEY)
//                                            .withRunMode(RunMode.RUN_LOCALLY)
//                                            .withSchedule(Schedule.forInterval(EVERY_MINUTE, null));
        final JobConfig jobConfig = JobConfig.forJobRunnerKey(JOB_RUNNER_KEY)
                .withRunMode(RunMode.RUN_LOCALLY)
                .withSchedule(Schedule.forCronExpression("0 17 12 * * ?"));


        try {
            scheduler.scheduleJob(JOB_ID, jobConfig);
        } catch (SchedulerServiceException e) {
            e.printStackTrace();
        }
    }
}
