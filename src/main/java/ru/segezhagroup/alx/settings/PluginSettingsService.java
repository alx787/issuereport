package ru.segezhagroup.alx.settings;

public interface PluginSettingsService {
    String getConfigJson();
    void setConfigJson(String json);
}
