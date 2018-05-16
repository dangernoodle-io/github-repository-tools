package io.dangernoodle.grt.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;


public final class JsonTransformer
{
    public static final Object NULL = JSONObject.NULL;

    public static JsonObject deserialize(File file) throws FileNotFoundException
    {
        return deserialize(new FileReader(file));
    }

    public static JsonObject deserialize(InputStream inputStream)
    {
        return deserialize(new InputStreamReader(inputStream));
    }

    public static JsonObject deserialize(Reader reader)
    {
        return loadJson(() -> reader);
    }

    public static JsonObject deserialize(String json)
    {
        return deserialize(new StringReader(json));
    }

    public static String prettyPrint(String json)
    {
        return new JSONObject(json).toString(4);
    }

    public static JsonObject serialize(Map<?, ?> object)
    {
        return new JsonObject(new JSONObject(adjustPlugins(object)));
    }

    public static String serialize(Object object)
    {
        return new JSONObject(object).toString();
    }

    private static Map<?, ?> adjustPlugins(Map<?, ?> object)
    {
        // pull out the JSONObject/Array objects being wrapped and insert back into map for proper serialization
        if (object.containsKey("plugins"))
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> plugins = (Map<String, Object>) object.get("plugins");

            for (Entry<String, Object> entry : plugins.entrySet())
            {
                Object value = entry.getValue();
                if (value instanceof JsonObject)
                {
                    plugins.replace(entry.getKey(), ((JsonObject) value).json);
                }
                else
                {
                    plugins.replace(entry.getKey(), ((JsonArray) value).json);
                }
            }
        }

        return object;
    }

    private static JsonObject loadJson(Supplier<Reader> supplier)
    {
        try (Reader reader = supplier.get())
        {
            return new JsonObject(new JSONObject(new JSONTokener(reader)));
        }
        catch (IOException e)
        {
            // this 'catch' is for the auto close
            throw new UncheckedIOException(e);
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

        public <T> Collection<String> getCollection(String key)
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

        public Integer getInteger(String key)
        {
            return convert(key, () -> Integer.valueOf(json.getInt(key)));
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
            return json.optString(key, null);
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

        private <T> T convert(String key, Supplier<T> supplier)
        {
            return json.has(key) ? supplier.get() : null;
        }

        public static abstract class Deserializer<V>
        {
            public V apply(JsonArray json)
            {
                return null;
            }

            public V apply(JsonObject json)
            {
                return null;
            }

            public V apply(String value)
            {
                return null;
            }
        }
    }
}
