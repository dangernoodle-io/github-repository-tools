package io.dangernoodle.grt.creds;

import java.util.Map;

import io.dangernoodle.grt.Credentials;
import io.dangernoodle.grt.utils.JsonTransformer.JsonObject;
import io.dangernoodle.grt.utils.JsonTransformer.JsonObject.Deserializer;


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
    public String getAuthToken(String key)
    {
        return json.getString(key);
    }

    @Override
    public Map<String, String> getNameValue(String key)
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
}
