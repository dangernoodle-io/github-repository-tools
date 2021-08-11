package io.dangernoodle.grt.utils;

import java.io.IOException;
import java.util.Map;

import io.dangernoodle.grt.Credentials;
import io.dangernoodle.grt.internal.FileLoader;
import io.dangernoodle.grt.utils.JsonTransformer.JsonObject;
import io.dangernoodle.grt.utils.JsonTransformer.JsonObject.Deserializer;


public class JsonFileCredentials implements Credentials
{
    public static final String FILENAME = "credentials.json";

    private final JsonObject json;

    public JsonFileCredentials(JsonObject json)
    {
        this.json = json;
    }

    @Override
    public String getAuthToken(String key)
    {
        return json.getString(key);
    }

    @Override
    public Map<String, String> getCredentials(String key)
    {
        return json.getMap(key, new Deserializer<String>()
        {
            @Override
            public String apply(String value)
            {
                return value;
            }
        });
    }

    public static Credentials loadCredentials(FileLoader fileLoader, JsonTransformer transformer)
    {
        try
        {
            return new JsonFileCredentials(transformer.deserialize(fileLoader.loadCredentials()));
        }
        catch (@SuppressWarnings("unused") IOException e)
        {
            return Credentials.NULL;
        }
    }
}
