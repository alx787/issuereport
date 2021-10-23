package ru.segezhagroup.alx.rest;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.segezhagroup.alx.ao.ReportTaskDao;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A resource of message.
 */
@Path("/shedulers")
public class TasksRest {

    private static final Logger log = LoggerFactory.getLogger(TasksRest.class);
    private final ReportTaskDao reportTaskDao;


    @Inject
    public TasksRest(ReportTaskDao reportTaskDao) {
        this.reportTaskDao = reportTaskDao;
    }


    // методы
    // 1 - получить список всех задач
    // 2 - получить одну задачу со всеми получателями
    // 3 - создать задачу
    // 4 - изменить задачу
    // 5 - удалить задачу

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/gettask/{taskid}")
    public Response getTask(@PathParam("taskid") String taskId) {
        if ((taskId == null) || taskId.equals("")) {
            return Response.ok("[]").build();
        }

        return Response.ok("[" + taskId + "]").build();
    }




}