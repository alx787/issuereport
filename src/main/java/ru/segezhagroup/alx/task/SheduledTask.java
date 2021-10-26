package ru.segezhagroup.alx.task;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.TimeUnit;

@ExportAsService
@Named
public class SheduledTask implements JobRunner, InitializingBean, DisposableBean {

    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of(SheduledTask.class.getName());
    private static final long EVERY_MINUTE = TimeUnit.MINUTES.toMillis(1);
    final JobConfig jobConfig = JobConfig.forJobRunnerKey(JOB_RUNNER_KEY).withRunMode(RunMode.RUN_LOCALLY).withSchedule(Schedule.forInterval(EVERY_MINUTE, null));
    private static final JobId JOB_ID = JobId.of(SheduledTask.class.getName());


    private final SchedulerService scheduler;
    
    @Inject
    public SheduledTask(@ComponentImport SchedulerService scheduler) {
        this.scheduler = scheduler;
    }

    @Nullable
    @Override
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {

        System.out.println("run at " + jobRunnerRequest.getStartTime());
        return JobRunnerResponse.success();
//        return null;
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
