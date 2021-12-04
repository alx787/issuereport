package ru.segezhagroup.alx.settings;

import com.google.gson.*;

public class PluginSettingsServiceTools {


    // получение настройки по имени
    public static String getValueFromSettingsCfg(String jsonCfg, String jsonKey) {

        if (jsonCfg.equals("")) {
            return  "";
        }

        JsonParser parser = new JsonParser();
        JsonObject cfgObj = parser.parse(jsonCfg).getAsJsonObject();

        if (cfgObj == null) {
            return  "";
        }

        JsonElement jsonElement = cfgObj.get(jsonKey);
        if ((jsonElement != null) && (jsonElement != JsonNull.INSTANCE)) {
            return jsonElement.getAsString();
        }

        return "";
    }


    // сохранение настройки по имени
    // {"sheduler":"","dzkfieldid":10300,"nextruntime":""} - всегда состоит из трек частей
    // возвращает настройку
    public static String setValueFromSettingsCfg(String jsonCfg, String jsonKey, String jsonValue) {

        JsonParser parser = new JsonParser();
        JsonObject cfgObj = parser.parse(jsonCfg).getAsJsonObject();


        String sheduler = "";
        String dzkfieldid = "";
        String nextruntime = "";


        JsonElement jsonElement = cfgObj.get("sheduler");

        if ((jsonElement != null) && (jsonElement != JsonNull.INSTANCE)) {
            sheduler = jsonElement.getAsString();
            if (jsonKey.equals("sheduler")) {
                sheduler = jsonValue;
            }
        }


        jsonElement = cfgObj.get("dzkfieldid");

        if ((jsonElement != null) && (jsonElement != JsonNull.INSTANCE)) {
            dzkfieldid = jsonElement.getAsString();
            if (jsonKey.equals("dzkfieldid")) {
                dzkfieldid = jsonValue;
            }
        }


        jsonElement = cfgObj.get("nextruntime");

        if ((jsonElement != null) && (jsonElement != JsonNull.INSTANCE)) {
            nextruntime = jsonElement.getAsString();
            if (jsonKey.equals("nextruntime")) {
                nextruntime = jsonValue;
            }
        }


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sheduler", sheduler);
        jsonObject.addProperty("dzkfieldid", dzkfieldid);
        jsonObject.addProperty("nextruntime", nextruntime);


        Gson gson = new Gson();

        return gson.toJson(jsonObject);

    }

}
