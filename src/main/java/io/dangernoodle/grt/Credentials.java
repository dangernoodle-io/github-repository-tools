package io.dangernoodle.grt;

import static io.dangernoodle.grt.Repository.GITHUB;

import java.io.File;
import java.io.IOException;

import io.dangernoodle.grt.json.JsonTransformer;
import io.dangernoodle.grt.json.JsonTransformer.JsonObject;


public class Credentials
{
    public static final String FILENAME = "credentials.json";

    private final JsonObject json;

    private Credentials(JsonObject json)
    {
        this.json = json;
    }

    public String getAuthToken(String key)
    {
        return json.getString(key);
    }

    public JsonObject getCredentials(String key)
    {
        return json.getJsonObject(key);
    }

    public String getGithubToken() throws IllegalStateException
    {
        String token = json.getString(GITHUB);
        if (token == null)
        {
            throw new IllegalStateException("github oauth token not found");
        }

        return token;
    }

    @SuppressWarnings("unchecked")
    public static Credentials load(File file) throws IOException
    {
        return new Credentials(JsonTransformer.deserialize(file));
    }
}
