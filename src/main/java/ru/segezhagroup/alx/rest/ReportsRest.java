package ru.segezhagroup.alx.rest;

import com.atlassian.jira.issue.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.segezhagroup.alx.ao.ReportTask;
import ru.segezhagroup.alx.ao.ReportTaskDao;
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

    @Inject
    public ReportsRest(ReportTaskDao reportTaskDao) {
        this.reportTaskDao = reportTaskDao;
    }

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/getreport/{taskid}")
    public Response getReport(@PathParam("taskid") String taskId) {

        int intTaskId = 0;

        try {
            intTaskId = Integer.valueOf(taskId);
        } catch (NumberFormatException e) {
            return Response.ok("Номер отчета должен быть числом: " + taskId).build();
        }

        ReportTask reportTask = reportTaskDao.findById(intTaskId);

        if (reportTask == null) {
            return Response.ok("Отчет с номером " + taskId + " не найден").build();
        }

        List<Issue> issueList = QueryIssues.getIssueFromJql(reportTask.getUserKey(), reportTask.getFilterString());

        if(issueList == null) {
            return Response.ok("Ошибка получения отчета по фильтру").build();
        }


        return Response.ok(MailSender.getReportText(reportTask.getName(), reportTask.getUserKey(), issueList, false)).build();
//        return Response.ok(MailSender.getReportText(issueList, true)).build();
    }

}
