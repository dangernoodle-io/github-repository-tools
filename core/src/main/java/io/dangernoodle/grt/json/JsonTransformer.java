package io.dangernoodle.grt.json;

import java.io.Reader;


public interface JsonTransformer
{
    <T> T deserialize(Reader reader, Class<T> clazz);

    <T> T deserialize(String json, Class<T> clazz);

    String prettyPrint(String json);

    String serialize(Object object);
}
