package ru.segezhagroup.alx.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PluginSettingsServiceTools {
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
}
