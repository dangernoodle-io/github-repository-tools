package io.dangernoodle.grt;

import static io.dangernoodle.grt.Repository.GITHUB;

import io.dangernoodle.grt.utils.JsonTransformer.JsonObject;


public class Credentials
{
    public static final String FILENAME = "credentials.json";

    private final JsonObject json;

    public Credentials(JsonObject json)
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
}
