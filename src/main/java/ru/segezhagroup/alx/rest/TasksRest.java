package ru.segezhagroup.alx.rest;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.segezhagroup.alx.ao.Receiver;
import ru.segezhagroup.alx.ao.ReceiverDao;
import ru.segezhagroup.alx.ao.ReportTask;
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
    private final ReceiverDao receiverDao;



    @Inject
    public TasksRest(ReportTaskDao reportTaskDao, ReceiverDao receiverDao) {
        this.reportTaskDao = reportTaskDao;
        this.receiverDao = receiverDao;
    }


    // методы
    // 1 - получить список всех задач
    // 2 - получить одну задачу со всеми получателями
    // 3 - создать задачу
    // 4 - изменить задачу
    // 5 - удалить задачу

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/getalltasks")
    public Response getAllTasks() {

//        {
//            taskid: 1,
//            name: "new task",
//            filterstring: "sdfsdf",
//            shedtime: "223ge",
//            active : "true",
//            receivers: [
//                {key:"1", email:"e1@mail.com", name: "alx"},
//                {key:"2", email:"e2@mail.com", name: "gle"}
//            ]
//        }


        JsonArray jsonTaskArray = new JsonArray();

        ReportTask[] reportTasks = reportTaskDao.findAll();
        for (ReportTask reportTask : reportTasks) {
            jsonTaskArray.add(prepareJsonObjectFromReportTask(reportTask));
        }


        Gson gson = new Gson();
        return Response.ok(gson.toJson(jsonTaskArray)).build();
    }


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/gettask/{taskid}")
    public Response getTask(@PathParam("taskid") String taskId) {
        if ((taskId == null) || taskId.equals("")) {
            return Response.ok("[]").build();
        }

        int intTaskId = 0;

        try {
            intTaskId = Integer.valueOf(taskId);
        } catch (NumberFormatException e) {

        }


        ReportTask reportTask = reportTaskDao.findById(intTaskId);
        if (reportTask == null) {
//            return Response.ok("{\"status\":\"error\", \"description\":\"cant find task\"}").build();
            return Response.ok("{}").build();
        }


        JsonObject jsonTaskObject = prepareJsonObjectFromReportTask(reportTask);


        Gson gson = new Gson();
        return Response.ok(gson.toJson(jsonTaskObject)).build();

    }


    // получим для одной строки задачи
    private JsonObject prepareJsonObjectFromReportTask(ReportTask reportTask) {
        JsonObject jsonTaskObject = new JsonObject();
        jsonTaskObject.addProperty("taskid", reportTask.getID());
        jsonTaskObject.addProperty("name", reportTask.getName());
        jsonTaskObject.addProperty("filterstring", reportTask.getFilterString());
        jsonTaskObject.addProperty("active", reportTask.getIsActive());

        JsonArray jsonUserArray = new JsonArray();

        Receiver[] receivers = reportTask.getReceivers();

        for (Receiver receiver : receivers) {

            JsonObject jsonUserObject = new JsonObject();

            jsonUserObject.addProperty("userid", receiver.getID());
            jsonUserObject.addProperty("key", receiver.getUserKey());
            jsonUserObject.addProperty("email", receiver.getUserEmail());
            jsonUserObject.addProperty("name", receiver.getUserName());


            jsonUserArray.add(jsonUserObject);
        }

        jsonTaskObject.add("receivers", jsonUserArray);

        return jsonTaskObject;
    }


    @POST
//    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/newtask")
    public Response addTask(String inputJson) {

//        {
//            name: "new task",
//            filterstring: "sdfsdf",
//            shedtime: "223ge",
//            active : "true",
//            receivers: [
//                {key:"1", email:"e1@mail.com", name: "alx"},
//                {key:"2", email:"e2@mail.com", name: "gle"}
//            ]
//        }

        JsonObject jsonInput = new JsonParser().parse(inputJson).getAsJsonObject();
        String taskName = jsonInput.get("name").getAsString();
        String filterString = jsonInput.get("filterstring").getAsString();
        String shedTime = jsonInput.get("shedtime").getAsString();
        Boolean active = jsonInput.get("active").getAsBoolean();

        // пользователи
        JsonArray jsonUserArray = jsonInput.get("receivers").getAsJsonArray();


        // создаем задачу
        ReportTask reportTask = reportTaskDao.create(taskName, filterString, shedTime, active);

        if (reportTask == null) {
            return Response.ok("{\"status\":\"error\", \"description\":\"error creating task\"}").build();
        }

        // создаем пользователей для задачи
        for (int i = 0; i < jsonUserArray.size(); i++) {
            JsonObject jsonUserObject = jsonUserArray.get(i).getAsJsonObject();
            Receiver receiver = receiverDao.create(reportTask,
                                                    jsonUserObject.get("key").getAsString(),
                                                    jsonUserObject.get("email").getAsString(),
                                                    jsonUserObject.get("name").getAsString());

            if (receiver == null) {
                // задачу по идее надо бы удалить

                return Response.ok("{\"status\":\"error\", \"description\":\"error creating user\"}").build();

            }

        }


        log.warn(jsonInput.toString());

        // готовим ответ
        JsonObject jsonOutput = new JsonObject();
        jsonOutput.addProperty("status", "ok");
        jsonOutput.addProperty("description", reportTask.getID());

        Gson gson = new Gson();

        return Response.ok(gson.toJson(jsonOutput)).build();

    }



    @POST
//    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/updatetask")
    public Response updateTask(String inputJson) {

        JsonObject jsonInput = new JsonParser().parse(inputJson).getAsJsonObject();
        int taskId = jsonInput.get("taskid").getAsInt();
        String taskName = jsonInput.get("name").getAsString();
        String filterString = jsonInput.get("filterstring").getAsString();
        String shedTime = jsonInput.get("shedtime").getAsString();
        Boolean active = jsonInput.get("active").getAsBoolean();

        // пользователи
        JsonArray jsonUserArray = jsonInput.get("receivers").getAsJsonArray();

        ReportTask reportTask = reportTaskDao.findById(taskId);

        if (reportTask == null) {
            return Response.ok("{\"status\":\"error\", \"description\":\"task not found\"}").build();
        }

        // задача найдена - нужно обновить поля задачи и удалить и создать пользоваетелей
        reportTask.setName(taskName);
        reportTask.setFilterString(filterString);
        reportTask.setShedTime(shedTime);
        reportTask.setIsActive(active);
        reportTaskDao.update(reportTask);


        // удаляем пользователей
        receiverDao.removeFromTask(reportTask);

        // создаем пользователей для задачи
        for (int i = 0; i < jsonUserArray.size(); i++) {
            JsonObject jsonUserObject = jsonUserArray.get(i).getAsJsonObject();
            Receiver receiver = receiverDao.create(reportTask,
                    jsonUserObject.get("key").getAsString(),
                    jsonUserObject.get("email").getAsString(),
                    jsonUserObject.get("name").getAsString());

            if (receiver == null) {
                // задачу по идее надо бы удалить

                return Response.ok("{\"status\":\"error\", \"description\":\"error creating user\"}").build();

            }

        }


        // готовим ответ
        JsonObject jsonOutput = new JsonObject();
        jsonOutput.addProperty("status", "ok");
        jsonOutput.addProperty("description", reportTask.getID());

        Gson gson = new Gson();

        return Response.ok(gson.toJson(jsonOutput)).build();

    }

    @POST
//    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/deletetask/{taskid}")
    public Response deleteTask(@PathParam("taskid") String taskId) {

        if ((taskId == null) || taskId.equals("")) {
            return Response.ok("{\"status\":\"error\", \"description\":\"empty task id\"}").build();
        }

        int intTaskId = 0;

        try {
            intTaskId = Integer.valueOf(taskId);
        } catch (NumberFormatException e) {

        }


        ReportTask reportTask = reportTaskDao.findById(intTaskId);
        if (reportTask == null) {
            return Response.ok("{\"status\":\"error\", \"description\":\"cant find task\"}").build();
        }

        // удаляем пользователей
//        receiverDao.removeFromTask(reportTask);
        // удаляем задачу
        reportTaskDao.remove(reportTask);

        return Response.ok("{\"status\":\"ok\", \"description\":\"task deleted\"}").build();

    }
}