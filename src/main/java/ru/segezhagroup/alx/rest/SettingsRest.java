package ru.segezhagroup.alx.rest;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.segezhagroup.alx.settings.PluginSettingsService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

        if (cfgObj == null) {
            JsonObject jsonOutput = new JsonObject();
            jsonOutput.addProperty("status", "error");
            jsonOutput.addProperty("descr", "ошибка чтения параметра переданного на сервере");

            Gson gson = new Gson();

            return Response.ok(gson.toJson(jsonOutput)).build();
        }


        // {"sheduler": "0 17 12 * * ?"}
        JsonElement jsonElement = cfgObj.get("sheduler");
        if ((jsonElement == null) || (jsonElement == JsonNull.INSTANCE)) {
            JsonObject jsonOutput = new JsonObject();
            jsonOutput.addProperty("status", "error");
            jsonOutput.addProperty("descr", "параметр пустой");

            Gson gson = new Gson();

            return Response.ok(gson.toJson(jsonOutput)).build();
        }

        JsonObject params = new JsonObject();
        params.addProperty("sheduler", jsonElement.getAsString());

        pluginSettingService.setConfigJson(params.toString());

        JsonObject jsonOutput = new JsonObject();
        jsonOutput.addProperty("status", "ok");
        jsonOutput.addProperty("descr", jsonElement.getAsString());

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
            jsonOutput.addProperty("descr", "ошибка чтения параметра сервера");

            Gson gson = new Gson();

            return Response.ok(gson.toJson(jsonOutput)).build();
        }

        // прочитаем параметр
        JsonParser parser = new JsonParser();
        JsonObject cfgObj = parser.parse(paramsString).getAsJsonObject();

        JsonObject jsonOutput = new JsonObject();
        jsonOutput.addProperty("status", "ok");
        jsonOutput.addProperty("descr", cfgObj.get("sheduler").getAsString());

        Gson gson = new Gson();

        return Response.ok(gson.toJson(jsonOutput)).build();


    }

}
