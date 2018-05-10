package io.dangernoodle.grt;

import static io.dangernoodle.grt.Repository.GITHUB;
import static io.dangernoodle.grt.json.DefaultJsonTransformer.transformer;

import java.io.File;
import java.io.IOException;
import java.util.Map;


public class Credentials
{
    public static final String FILENAME = "credentials.json";

    private final Map<String, Object> credentials;

    private final String githubToken;

    private Credentials(Map<String, Object> credentials)
    {
        this.credentials = credentials;
        this.githubToken = credentials.remove(GITHUB).toString();
    }

    @SuppressWarnings("unchecked")
    public <T> T getCredentials(String key)
    {
        return (T) credentials.get(key);
    }

    public String getGithubToken() throws IllegalStateException
    {
        if (githubToken == null)
        {
            throw new IllegalStateException("github oauth token not found");
        }

        return githubToken;
    }

    @SuppressWarnings("unchecked")
    public static Credentials load(File file) throws IOException
    {
        return new Credentials(transformer.deserialize(file, Map.class));
    }
}
