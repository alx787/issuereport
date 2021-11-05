package ru.segezhagroup.alx.dataprovider;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.json.marshal.wrapped.JsonableString;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class WebDataProvider implements WebResourceDataProvider {
    @Override
    public Jsonable get() {
//        return new JsonableString("Hello world!");
        return new Jsonable()
        {
            @Override
            public void write(Writer writer) throws IOException
            {
                Gson gson = new Gson();
                gson.toJson(webData(), Map.class, new PrintWriter(writer));
            }
        };
    }

    public Map<String, Object> webData()
    {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("home_url", ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL));
//        map.put("species", "Canine");
//        map.put("breed", "Shiba Inu");
        return map;
    }
}
