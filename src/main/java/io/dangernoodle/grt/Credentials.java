package io.dangernoodle.grt;

import java.util.Map;


public class Credentials
{
    public static final String FILENAME = "credentials.json";

    private final Map<String, Object> credentials;

    private final String githubToken;

    public Credentials(Map<String, Object> credentials)
    {
        this.credentials = credentials;
        this.githubToken = credentials.remove("github").toString();
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
}
