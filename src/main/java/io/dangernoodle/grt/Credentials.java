package io.dangernoodle.grt;

import static io.dangernoodle.grt.Constants.GITHUB;
import static io.dangernoodle.grt.Constants.GITHUB_APP;

import java.util.Map;
import java.util.Optional;


public interface Credentials
{
    String getCredentials(String key);

    default Map<String, Object> getGithubApp() throws IllegalStateException
    {
        return Optional.ofNullable(getNameValue(GITHUB_APP))
                       .orElseThrow(() -> new IllegalStateException("github app credentials not found!"));
    }

    default String getGithubOAuthToken() throws IllegalStateException
    {
        return Optional.ofNullable(getCredentials(GITHUB))
                       .orElseThrow(() -> new IllegalStateException("github oauth token not found!"));
    }

    /**
     * @since 0.9.0
     */
    Map<String, Object> getNameValue(String key);

    /**
     * @since 0.9.0
     */
    default boolean runAsApp()
    {
        return false;
    }
}
