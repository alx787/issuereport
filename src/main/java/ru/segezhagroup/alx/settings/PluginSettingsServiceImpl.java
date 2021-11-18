package ru.segezhagroup.alx.settings;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import javax.inject.Inject;
import javax.inject.Named;

@Named("pluginSettingsServiceIssueReport")
public class PluginSettingsServiceImpl implements PluginSettingsService {

    private final PluginSettings pluginSettings;
    private static final String PLUGIN_STORAGE_KEY = "ru.segezhagroup.settings.";
    private static final String CONFIG_KEY = "issuereport";


    @Inject
    public PluginSettingsServiceImpl(@ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
    }

    private String getSettingValue(String settingKey) {
        if (settingKey == null) {
            return "";
        } else {
            return (String) pluginSettings.get(PLUGIN_STORAGE_KEY + settingKey);
        }
    }

    private void setSettingValue(String settingKey, String settingValue) {
        if (settingKey == null)
            return;

        if (settingValue == null) {
            this.pluginSettings.put(PLUGIN_STORAGE_KEY + settingKey,"");
        } else {
            this.pluginSettings.put(PLUGIN_STORAGE_KEY + settingKey, settingValue);
        }
    }

    @Override
    public String getConfigJson() {
        return getSettingValue(CONFIG_KEY);
    }

    @Override
    public void setConfigJson(String settingValueInJsonFormat) {
        setSettingValue(CONFIG_KEY, settingValueInJsonFormat);
    }
}
