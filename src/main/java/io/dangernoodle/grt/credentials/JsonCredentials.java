package io.dangernoodle.grt.credentials;

import java.util.Map;

import io.dangernoodle.grt.Credentials;
import io.dangernoodle.grt.util.JsonTransformer.JsonObject;
import io.dangernoodle.grt.util.JsonTransformer.JsonObject.Deserializer;


/**
 * @since 0.9.0
 */
public class JsonCredentials implements Credentials
{
    private final JsonObject json;

    public JsonCredentials(JsonObject json)
    {
        this.json = json;
    }

    @Override
    public String getCredentials(String key)
    {
        return json.getString(key);
    }

    @Override
    public Map<String, Object> getNameValue(String key)
    {
        return json.getMap(key, new Deserializer<>()
        {
            @Override
            public Object apply(String value)
            {
                return value;
            }
        });
    }
}
