package ru.segezhagroup.alx.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.segezhagroup.alx.ao.ReportTask;
import ru.segezhagroup.alx.ao.ReportTaskDao;
import ru.segezhagroup.alx.settings.PluginSettingsService;
import ru.segezhagroup.alx.settings.PluginSettingsServiceTools;
import ru.segezhagroup.alx.tools.MailSender;
import ru.segezhagroup.alx.tools.QueryIssues;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/reports")
public class ReportsRest {

    private static final Logger log = LoggerFactory.getLogger(TasksRest.class);
    private final ReportTaskDao reportTaskDao;
    private final PluginSettingsService pluginSettingService;

    @Inject
    public ReportsRest(ReportTaskDao reportTaskDao, PluginSettingsService pluginSettingService) {
        this.reportTaskDao = reportTaskDao;
        this.pluginSettingService = pluginSettingService;
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/getreport/{taskid}")
    public Response getReport(@PathParam("taskid") String taskId) {
        // получить таблицу отчета
        return Response.ok(getMailText(taskId, false)).build();
    }


    @GET
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/sendreport/{taskid}")
    public Response sendReport(@PathParam("taskid") String taskId) {
        // сформировать почтовое сообщение с отчетом и отправить его
        ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();

        if (user != null) {
            log.warn("user: " + user.getEmailAddress());
        } else {
            log.warn("user: null");
        }

        MailSender.sendEmail(user.getEmailAddress(), "JIRA - отчет по задачам",getMailText(taskId, true));

        return Response.ok("Отчет отправлен на " + user.getEmailAddress()).build();
    }


    private String getMailText(String taskId, boolean getMail) {

        int intTaskId = 0;

        try {
            intTaskId = Integer.valueOf(taskId);
        } catch (NumberFormatException e) {
            return "Номер отчета должен быть числом: " + taskId;
        }

        ReportTask reportTask = reportTaskDao.findById(intTaskId);

        if (reportTask == null) {
            return "Отчет с номером " + taskId + " не найден";
        }

        List<Issue> issueList = QueryIssues.getIssueFromJql(reportTask.getUserKey(), reportTask.getFilterString());

        if(issueList == null) {
            return "Ошибка получения отчета по фильтру";
        }


        // получим параметр - ид поля дзк
        String configJson = pluginSettingService.getConfigJson();
        String dzkfieldid = PluginSettingsServiceTools.getValueFromSettingsCfg(configJson, "dzkfieldid");

        return MailSender.getReportText(reportTask.getName(), reportTask.getUserKey(), issueList, dzkfieldid, reportTask.getSlaId(), getMail);
    }
}
