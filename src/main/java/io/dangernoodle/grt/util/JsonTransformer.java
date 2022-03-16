package io.dangernoodle.grt.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JsonTransformer
{
    public static final Object NULL = JSONObject.NULL;

    public static final String SCHEMA = "/repository-schema.json";

    private static final Logger logger = LoggerFactory.getLogger(JsonTransformer.class);

    private final Schema schema;

    public JsonTransformer()
    {
        this.schema = loadSchema();
    }

    public JsonObject deserialize(Path path) throws IOException
    {
        return deserialize(new InputStreamReader(Files.newInputStream(path)));
    }

    public JsonObject deserialize(Reader reader)
    {
        return new JsonObject(loadJson(() -> reader));
    }

    public JsonObject deserialize(String json)
    {
        return deserialize(new StringReader(json));
    }

    public String prettyPrint(String json)
    {
        return new JSONObject(json).toString(4);
    }

    public JsonObject serialize(Map<?, ?> object)
    {
        return object == null ? JsonObject.NULL : new JsonObject(new JSONObject(object));
    }

    public JsonObject serialize(Object object)
    {
        return object == null ? JsonObject.NULL : new JsonObject(new JSONObject(object));
    }

    /**
     * @since 0.9.0
     */
    public JsonObject validate(InputStream inputStream) throws JsonValidationException
    {
        return validate(new InputStreamReader(inputStream));
    }

    public JsonObject validate(Path path) throws IOException, JsonValidationException
    {
        return validate(deserialize(path));
    }

    /**
     * @since 0.9.0
     */
    public JsonObject validate(Reader reader) throws JsonValidationException
    {
        return validate(deserialize(reader));
    }

    private JSONObject loadJson(Supplier<Reader> supplier)
    {
        try (Reader reader = supplier.get())
        {
            return new JSONObject(new JSONTokener(reader));
        }
        catch (IOException e)
        {
            // this 'catch' is for the auto close
            throw new UncheckedIOException(e);
        }
    }

    private Schema loadSchema()
    {
        return SchemaLoader.load(loadJson(() -> new InputStreamReader(getClass().getResourceAsStream(SCHEMA))));
    }

    private void logViolations(ValidationException exception)
    {
        logger.error("{}", exception.getMessage());
        exception.getCausingExceptions()
                 .stream()
                 .forEach(this::logViolations);
    }

    private JsonObject validate(JsonObject object) throws JsonValidationException
    {
        try
        {
            schema.validate(object.json);
            return object;
        }
        catch (ValidationException e)
        {
            logViolations(e);
            throw new JsonValidationException(e);
        }
    }

    public static class JsonArray implements Iterable<Object>
    {
        public static final JsonArray NULL = new JsonArray(new JSONArray());

        private final JSONArray json;

        private JsonArray(JSONArray json)
        {
            this.json = json;
        }

        private JsonArray(Object json)
        {
            this((JSONArray) json);
        }

        public boolean isNotNull()
        {
            return this != NULL;
        }

        @Override
        public Iterator<Object> iterator()
        {
            return new Itr(json.iterator());
        }

        public List<Object> toList()
        {
            return json.toList();
        }

        @Override
        public String toString()
        {
            return json.toString();
        }

        private class Itr implements Iterator<Object>
        {
            private Iterator<Object> iterator;

            private Itr(Iterator<Object> iterator)
            {
                this.iterator = iterator;
            }

            @Override
            public boolean hasNext()
            {
                return iterator.hasNext();
            }

            @Override
            public Object next()
            {
                Object next = iterator.next();
                return (next instanceof JSONObject) ? new JsonObject(next) : next;
            }
        }
    }

    public static class JsonObject
    {
        public static final JsonObject NULL = new JsonObject(new JSONObject());

        private final JSONObject json;

        private JsonObject(JSONObject json)
        {
            this.json = json;
        }

        private JsonObject(Object json)
        {
            this((JSONObject) json);
        }

        public Boolean getBoolean(String key)
        {
            return convert(key, () -> Boolean.valueOf(json.getBoolean(key)));
        }

        public boolean getBoolean(String key, boolean dflt)
        {
            return Optional.ofNullable(getBoolean(key))
                           .map(Boolean::booleanValue)
                           .orElse(dflt);
        }

        public Collection<String> getCollection(String key)
        {
            return Optional.ofNullable(json.optJSONArray(key))
                           .map(array -> {
                               return array.toList()
                                           .stream()
                                           .map(Object::toString)
                                           .collect(Collectors.toList());
                           })
                           .orElse(null);
        }

        public Collection<String> getCollection(String key, Collection<String> dflt)
        {
            return Optional.ofNullable(getCollection(key))
                           .orElse(dflt);
        }

        public Integer getInteger(String key)
        {
            return convert(key, () -> Integer.valueOf(json.getInt(key)));
        }

        public int getInteger(String key, int dflt)
        {
            return Optional.ofNullable(getInteger(key))
                           .map(Integer::intValue)
                           .orElse(dflt);
        }

        /**
         * @since 0.5.0
         */
        public JsonArray getJsonArray(String key)
        {
            return Optional.ofNullable(json.getJSONArray(key))
                           .map(JsonArray::new)
                           .orElse(JsonArray.NULL);
        }

        public JsonObject getJsonObject(String key)
        {
            return Optional.ofNullable(json.optJSONObject(key))
                           .map(JsonObject::new)
                           .orElse(JsonObject.NULL);
        }

        public <T> Map<String, T> getMap(String key, Deserializer<T> deserializer)
        {
            return Optional.ofNullable(convert(key, () -> {
                Map<String, T> map = new HashMap<>();
                JSONObject object = json.getJSONObject(key);

                object.keySet()
                      .forEach(k -> {
                          T result = null;
                          Object value = object.get(k);

                          if (value instanceof JSONObject)
                          {
                              result = deserializer.apply(new JsonObject(value));
                          }
                          else if (value instanceof JSONArray)
                          {
                              result = deserializer.apply(new JsonArray(value));
                          }
                          else
                          {
                              result = deserializer.apply(value.toString());
                          }

                          map.put(k, result);
                      });

                return map;
            })).orElse(null);
        }

        public String getString(String key)
        {
            return getString(key, null);
        }

        public String getString(String key, String dlft)
        {
            return json.optString(key, dlft);
        }

        public boolean has(String key)
        {
            return json.has(key);
        }

        public boolean isNotNull()
        {
            return this != NULL;
        }

        public String prettyPrint()
        {
            return json.toString(4);
        }

        public Map<String, Object> toMap()
        {
            return json.toMap();
        }

        @Override
        public String toString()
        {
            return json.toString();
        }

        private <T> T convert(String key, Supplier<T> supplier)
        {
            return json.has(key) ? supplier.get() : null;
        }

        public static interface Deserializer<V>
        {
            default V apply(JsonArray json)
            {
                return null;
            }

            default V apply(JsonObject json)
            {
                return null;
            }

            default V apply(String value)
            {
                return null;
            }
        }
    }
}
