package io.dangernoodle.grt;

import static io.dangernoodle.grt.json.DefaultJsonTransformer.transformer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;


public class Credentials
{
    private static final String FILENAME = "credentials.json";

    private final Map<String, Object> credentials;

    private final String githubToken;

    Credentials(Map<String, Object> credentials)
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

    @ApplicationScoped
    public static class CredentialsProducer
    {
        @Produces
        @ApplicationScoped
        @SuppressWarnings("unchecked")
        public Credentials get(Arguments arguments) throws FileNotFoundException
        {
            FileReader reader = new FileReader(arguments.getRoot() + File.separator + FILENAME);
            return new Credentials(transformer.deserialize(reader, Map.class));
        }
    }
}
