package ru.segezhagroup.alx.task;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.scheduler.*;
import com.atlassian.scheduler.config.*;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import ru.segezhagroup.alx.ao.ReportTask;
import ru.segezhagroup.alx.ao.ReportTaskDao;
import ru.segezhagroup.alx.settings.PluginSettingsService;
import ru.segezhagroup.alx.settings.PluginSettingsServiceTools;
import ru.segezhagroup.alx.tools.JobsProcedures;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
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
    private final PluginSettingsService pluginSettingsService;
    private final ReportTaskDao reportTaskDao;
    
    @Inject
    public SheduledTask(@ComponentImport SchedulerService scheduler, PluginSettingsService pluginSettingsService, ReportTaskDao reportTaskDao) {
        this.scheduler = scheduler;
        this.pluginSettingsService = pluginSettingsService;
        this.reportTaskDao = reportTaskDao;
    }

    @Nullable
    @Override
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {

//        System.out.println("run at " + jobRunnerRequest.getStartTime());
//        log.warn("run at " + jobRunnerRequest.getStartTime());

        JobsProcedures.ExecReporting(reportTaskDao);


        return JobRunnerResponse.success();
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("Stopping...");
        log.warn("Stopping...");

        scheduler.unscheduleJob(JOB_ID);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        String shedulerString = null;
        try {
            shedulerString = PluginSettingsServiceTools.getValueFromSettingsCfg(pluginSettingsService.getConfigJson(), "sheduler");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (shedulerString == null || shedulerString.equals("")) {
            shedulerString = "0 0 8 * * ?"; // 8 утра каждый день по умолчанию

            JsonObject params = new JsonObject();
            params.addProperty("sheduler", shedulerString);
            params.addProperty("nextruntime", "");

            pluginSettingsService.setConfigJson(params.toString());

        }

        // примеры выражений крон
        // https://support.atlassian.com/jira-software-cloud/docs/construct-cron-expressions-for-a-filter-subscription/
        // 0 0 8 * * ? - каждый день в 8.00

        // Second	Minute	Hour	Day-of-month	Month	Day-of-week	    Year (optional)


        System.out.println("Starting...");
        log.warn("Starting ..... ");

        scheduler.registerJobRunner(JOB_RUNNER_KEY, this);
//        final JobConfig jobConfig = JobConfig.forJobRunnerKey(JOB_RUNNER_KEY)
//                                            .withRunMode(RunMode.RUN_LOCALLY)
//                                            .withSchedule(Schedule.forInterval(EVERY_MINUTE, null));



        final JobConfig jobConfig = JobConfig.forJobRunnerKey(JOB_RUNNER_KEY)
                .withRunMode(RunMode.RUN_LOCALLY)
                .withSchedule(Schedule.forCronExpression(shedulerString));


        try {
            scheduler.scheduleJob(JOB_ID, jobConfig);

            SimpleDateFormat formatDay = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

            // установим значение следующего времени запуска
            JsonObject params = new JsonObject();
            params.addProperty("sheduler", shedulerString);
            params.addProperty("nextruntime", formatDay.format(scheduler.calculateNextRunTime(Schedule.forCronExpression(shedulerString))));


            pluginSettingsService.setConfigJson(params.toString());

        } catch (SchedulerServiceException e) {
            e.printStackTrace();
        }
    }
}
