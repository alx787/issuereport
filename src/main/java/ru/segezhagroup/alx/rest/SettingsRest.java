package ru.segezhagroup.alx.rest;

import com.atlassian.scheduler.config.Schedule;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.segezhagroup.alx.settings.PluginSettingsService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;

@Path("/settings")
public class SettingsRest {
    private static final Logger log = LoggerFactory.getLogger(SettingsRest.class);

    private final PluginSettingsService pluginSettingService;

    @Inject
    public SettingsRest(PluginSettingsService pluginSettingService) {
        this.pluginSettingService = pluginSettingService;
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/save")
    public Response saveSettings(String inputJson) {


        // пустой параметр
        if (inputJson == null || inputJson.equals("")) {
            JsonObject jsonOutput = new JsonObject();
            jsonOutput.addProperty("status", "error");
            jsonOutput.addProperty("descr", "строка расписания не заполнена");

            Gson gson = new Gson();

            return Response.ok(gson.toJson(jsonOutput)).build();

        }

        // прочитаем параметр
        JsonParser parser = new JsonParser();
        JsonObject cfgObj = parser.parse(inputJson).getAsJsonObject();

        // ничего не получили
        if (cfgObj == null) {
            JsonObject jsonOutput = new JsonObject();
            jsonOutput.addProperty("status", "error");
            jsonOutput.addProperty("descr", "ошибка чтения параметра переданного на сервере");

            Gson gson = new Gson();

            return Response.ok(gson.toJson(jsonOutput)).build();
        }


        // {"sheduler": "0 17 12 * * ?"}
        JsonElement jsonElementSheduler = cfgObj.get("sheduler");
        JsonElement jsonElementDzkFieldId = cfgObj.get("dzkfieldid");
        if ((jsonElementSheduler == null) || (jsonElementSheduler == JsonNull.INSTANCE)) {
            JsonObject jsonOutput = new JsonObject();
            jsonOutput.addProperty("status", "error");
            jsonOutput.addProperty("descr", "параметр пустой");

            Gson gson = new Gson();

            return Response.ok(gson.toJson(jsonOutput)).build();
        }


//        String shedulerString = jsonElement.getAsString();
//        log.warn("shed ");
//        log.warn(shedulerString);
//        log.warn(Schedule.forCronExpression(shedulerString).toString());

        Long dkzFieldId = 0L;

        try {
            dkzFieldId = jsonElementDzkFieldId.getAsLong();
        } catch (Exception e) {
            e.printStackTrace();
        }


        JsonObject params = new JsonObject();
        params.addProperty("sheduler", jsonElementSheduler.getAsString());
        params.addProperty("dzkfieldid", dkzFieldId);
        params.addProperty("nextruntime", "");


        pluginSettingService.setConfigJson(params.toString());

        JsonObject jsonOutput = new JsonObject();
        jsonOutput.addProperty("status", "ok");
        jsonOutput.addProperty("sheduler", jsonElementSheduler.getAsString());
        jsonOutput.addProperty("dzkfieldid", dkzFieldId);
        jsonOutput.addProperty("nextruntime", "");

        Gson gson = new Gson();

        return Response.ok(gson.toJson(jsonOutput)).build();

    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/get")
    public Response getSettings() {
        String paramsString = pluginSettingService.getConfigJson();

        if (paramsString == null || paramsString.equals("")) {
            JsonObject jsonOutput = new JsonObject();
            jsonOutput.addProperty("status", "error");
//            jsonOutput.addProperty("descr", "ошибка чтения параметра сервера");
            jsonOutput.addProperty("sheduler", "");
            jsonOutput.addProperty("dzkfieldid", "");
            jsonOutput.addProperty("nextruntime", "");

            Gson gson = new Gson();

            return Response.ok(gson.toJson(jsonOutput)).build();
        }

        // прочитаем параметр
        JsonParser parser = new JsonParser();
        JsonObject cfgObj = parser.parse(paramsString).getAsJsonObject();

        JsonObject jsonOutput = new JsonObject();
        jsonOutput.addProperty("status", "ok");

        if (cfgObj.get("sheduler") != null) {
            jsonOutput.addProperty("sheduler", cfgObj.get("sheduler").getAsString());
        } else {
            jsonOutput.addProperty("sheduler", "");
        }

        if (cfgObj.get("dzkfieldid") != null) {
            jsonOutput.addProperty("dzkfieldid", cfgObj.get("dzkfieldid").getAsString());
        } else {
            jsonOutput.addProperty("dzkfieldid", "");

        }

        if (cfgObj.get("nextruntime") != null) {
            jsonOutput.addProperty("nextruntime", cfgObj.get("nextruntime").getAsString());
        } else {
            jsonOutput.addProperty("nextruntime", "");

        }


//        jsonOutput.addProperty("sheduler", cfgObj.get("sheduler").getAsString());
//        jsonOutput.addProperty("dzkfieldid", cfgObj.get("dzkfieldid").getAsString());
//        jsonOutput.addProperty("nextruntime", cfgObj.get("nextruntime").getAsString());

        Gson gson = new Gson();

        return Response.ok(gson.toJson(jsonOutput)).build();

    }


}
