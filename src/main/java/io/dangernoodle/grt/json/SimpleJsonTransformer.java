package io.dangernoodle.grt.json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SimpleJsonTransformer
{
    public static final SimpleJsonTransformer transformer = new SimpleJsonTransformer();

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

    public <T> T deserialize(File file, Class<T> clazz) throws IOException
    {
        try (FileReader reader = new FileReader(file))
        {
            return deserialize(reader, clazz);
        }
    }

    public <T> T deserialize(Reader reader, Class<T> clazz)
    {
        return gson.fromJson(reader, clazz);
    }

    public Object deserialize(String json)
    {
        return gson.fromJson(json, JsonElement.class);
    }

    public <T> T deserialize(String json, Class<T> clazz)
    {
        return gson.fromJson(json, clazz);
    }

    public String prettyPrint(String json)
    {
        return prettyPrinter.toJson(jsonParser.parse(json).getAsJsonObject());
    }

    public String serialize(Object object)
    {
        return gson.toJson(object);
    }

    protected GsonBuilder createGsonBuilder()
    {
        return new GsonBuilder();
    }
}
