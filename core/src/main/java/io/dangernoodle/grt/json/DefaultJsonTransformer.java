package io.dangernoodle.grt.json;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import io.dangernoodle.grt.Repository.Color;
import io.dangernoodle.grt.Repository.Plugins;


public final class DefaultJsonTransformer extends SimpleJsonTransformer
{
    public static final JsonTransformer transformer = new DefaultJsonTransformer();

    DefaultJsonTransformer()
    {
        super();
    }

    @Override
    protected GsonBuilder createGsonBuilder()
    {
        return new GsonBuilder().registerTypeAdapter(Color.class, deserializeColor())
                                .registerTypeAdapter(Plugins.class, deserializePlugins());
    }

    private JsonDeserializer<Color> deserializeColor()
    {
        return (json, typeOfT, context) -> Color.from(json.getAsString());
    }

    private JsonDeserializer<Plugins> deserializePlugins()
    {
        return (json, typeOfT, context) -> {
            Map<String, String> map = new HashMap<>();
            json.getAsJsonObject()
                .entrySet()
                .forEach(entry -> map.put(entry.getKey(), entry.getValue().toString()));

            return new Plugins(map);
        };
    }
}
