package ru.segezhagroup.alx.rest;

import com.atlassian.scheduler.config.Schedule;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.segezhagroup.alx.settings.PluginSettingsService;
import ru.segezhagroup.alx.settings.PluginSettingsServiceTools;

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
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/savesheduler")
    public Response saveSettingsSheduler(String inputJson) {


        String configJson = pluginSettingService.getConfigJson();

        String sheduler = PluginSettingsServiceTools.getValueFromSettingsCfg(configJson, "sheduler");
        if (sheduler.equals("")) {
            sheduler = "0 0 8 * * ?"; // по умолчанию 8 часов каждый день
        }

        String textError = "";

        if (inputJson != null) {
            if (!inputJson.isEmpty()) {
                // прочитаем параметр
                JsonParser parser = new JsonParser();
                JsonObject cfgObj = parser.parse(inputJson).getAsJsonObject();
                if (cfgObj != null) {
                    JsonElement jsonElementSheduler = cfgObj.get("sheduler");
                    if ((jsonElementSheduler != null) && (jsonElementSheduler != JsonNull.INSTANCE)) {
                        if (!jsonElementSheduler.getAsString().isEmpty()) {
                            sheduler = jsonElementSheduler.getAsString();
                        } else {
                            textError = "пустой параметр";
                        }
                    } else {
                        textError = "не заполненный параметр";
                    }
                } else {
                    textError = "неправильный формат переменной";
                }
            } else {
                textError = "пустой параметр";
            }
        } else {
            textError = "нет параметра";
        }


        pluginSettingService.setConfigJson(PluginSettingsServiceTools.setValueFromSettingsCfg(configJson, "sheduler", sheduler));



        // ошибка
        if (!textError.equals("")) {
            JsonObject jsonOutput = new JsonObject();
            jsonOutput.addProperty("status", "error");
            jsonOutput.addProperty("descr", textError + " значение установлено в " + sheduler);

            Gson gson = new Gson();
            return Response.ok(gson.toJson(jsonOutput)).build();

        }

        return Response.ok("{\"status\":\"ok\", \"descr\":\"сохранено\"}").build();

    }



    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/savedzk")
    public Response saveSettingsDzk(String inputJson) {


        String configJson = pluginSettingService.getConfigJson();

        String dzkfieldid = PluginSettingsServiceTools.getValueFromSettingsCfg(configJson, "dzkfieldid");
        if (dzkfieldid.equals("")) {
            dzkfieldid = "0"; // по умолчанию 0
        }


        JsonParser parser = new JsonParser();
        JsonObject cfgObj = parser.parse(inputJson).getAsJsonObject();

        String textError = "";

        try {
            dzkfieldid = cfgObj.get("dzkfieldid").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            textError = e.getMessage();
        }

        if (!textError.equals("")) {
            return Response.ok("{\"status\":\"error\", \"descr\":\"" + textError + "\"}").build();
        }

        pluginSettingService.setConfigJson(PluginSettingsServiceTools.setValueFromSettingsCfg(configJson, "dzkfieldid", dzkfieldid));

        return Response.ok("{\"status\":\"ok\", \"descr\":\"сохранено\"}").build();

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
