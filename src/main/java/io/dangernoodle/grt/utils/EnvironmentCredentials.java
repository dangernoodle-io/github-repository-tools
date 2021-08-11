package io.dangernoodle.grt.utils;

import java.util.Map;
import java.util.Optional;

import io.dangernoodle.grt.Credentials;


public class EnvironmentCredentials implements Credentials
{
    @Override
    public String getAuthToken(String key)
    {
        return Optional.ofNullable(System.getenv("GRT_GITHUB_OAUTH"))
                       .orElse(null);
    }

    @Override
    public Map<String, String> getCredentials(String key)
    {
        return null;
    }
}
