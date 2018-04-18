package io.dangernoodle.grt.json;

import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SimpleJsonTransformer implements JsonTransformer
{
    public static final JsonTransformer transformer = new SimpleJsonTransformer();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Gson gson;

    private final JsonParser jsonParser;

    private final Gson prettyPrinter;

    SimpleJsonTransformer()
    {
        GsonBuilder builder = createGsonBuilder();

        this.gson = builder.create();
        this.jsonParser = new JsonParser();
        this.prettyPrinter = builder.setPrettyPrinting().create();
    }

    @Override
    public <T> T deserialize(Reader reader, Class<T> clazz)
    {
        return gson.fromJson(reader, clazz);
    }

    @Override
    public <T> T deserialize(String json, Class<T> clazz)
    {
        return gson.fromJson(json, clazz);
    }

    @Override
    public String prettyPrint(String json)
    {
        return prettyPrinter.toJson(jsonParser.parse(json).getAsJsonObject());
    }

    @Override
    public String serialize(Object object)
    {
        return gson.toJson(object);
    }

    protected GsonBuilder createGsonBuilder()
    {
        return new GsonBuilder();
    }
}
