package ru.segezhagroup.alx.rest;

import com.atlassian.jira.issue.Issue;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.segezhagroup.alx.tools.MailSender;
import ru.segezhagroup.alx.tools.QueryIssues;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/test")
public class TestRest {

    private static final Logger log = LoggerFactory.getLogger(TestRest.class);

//    @GET
//    @Path("/testmail")
//    public Response testMailSend() {
//
//        String testBody = MailSender.getReportText(true);
//
//        MailSender.sendEmail("test@asd.ru", "test subject", testBody);
//        return Response.ok("[]").build();
//    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/testjql")
    public Response testjql() {

        String userKey = "admin";
        String jqlQuery = "assignee = admin AND resolution = Unresolved order by updated DESC";

        if (!QueryIssues.validateJql(userKey, jqlQuery)) {
            return Response.ok("[]").build();
        }

        List<Issue> issueList = QueryIssues.getIssueFromJql(userKey, jqlQuery);

        JsonArray jsonIssueArray = new JsonArray();

        for (Issue oneIssue : issueList) {
            JsonObject jsonIssueObject = new JsonParser().parse("{\"issukey\":\"" + oneIssue.getKey() + "\"}").getAsJsonObject();
            jsonIssueArray.add(jsonIssueObject);
        }

        Gson gson = new Gson();
        return Response.ok(gson.toJson(jsonIssueArray)).build();
    }


}
