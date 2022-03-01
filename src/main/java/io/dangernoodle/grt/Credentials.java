package io.dangernoodle.grt;

import static io.dangernoodle.grt.Constants.GITHUB;

import java.util.Map;
import java.util.Optional;


public interface Credentials
{
    public static final Credentials NULL = new Credentials()
    {
        @Override
        public String getAuthToken(String key)
        {
            return null;
        }

        @Override
        public Map<String, String> getNameValue(String key)
        {
            return null;
        }
    };

    String getAuthToken(String key);

    default String getGithubToken() throws IllegalStateException
    {
        return Optional.ofNullable(getAuthToken(GITHUB))
                       .orElseThrow(() -> new IllegalStateException("github oauth token not found"));
    }

    /**
     * @since 0.8.0
     */
    Map<String, String> getNameValue(String key);
}
