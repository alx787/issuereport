package ru.segezhagroup.alx.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.segezhagroup.alx.tools.MailSender;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/reports")
public class ReportsRest {

    private static final Logger log = LoggerFactory.getLogger(TasksRest.class);

    @GET
    @Produces({MediaType.TEXT_HTML})
    @Path("/getreport/{taskid}")
    public Response getReport(@PathParam("taskid") String taskId) {
        return Response.ok(MailSender.getMailText()).build();
    }

}
