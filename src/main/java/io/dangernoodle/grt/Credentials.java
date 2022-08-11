package io.dangernoodle.grt;

import static io.dangernoodle.grt.Constants.APP_ID;
import static io.dangernoodle.grt.Constants.APP_KEY;
import static io.dangernoodle.grt.Constants.GITHUB;
import static io.dangernoodle.grt.Constants.GITHUB_APP;
import static io.dangernoodle.grt.Constants.INSTALL_ID;
import static io.dangernoodle.grt.Constants.PASSWORD;
import static io.dangernoodle.grt.Constants.USERNAME;

import java.io.Reader;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;


public interface Credentials
{
    String getCredentials(String key);

    /**
     * @since 0.9.0
     */
    default GithubApp getGithubApp() throws IllegalStateException
    {
        return Optional.ofNullable(getNameValue(GITHUB_APP))
                       .map(map -> new GithubApp(map))
                       .orElseThrow(illegalState("github app credentials not found!"));
    }

    default String getGithubOAuthToken() throws IllegalStateException
    {
        return Optional.ofNullable(getCredentials(GITHUB))
                       .orElseThrow(illegalState("github oauth token not found!"));
    }

    /**
     * @since 0.9.0
     */
    Map<String, Object> getNameValue(String key);

    /**
     * @since 0.11.0
     */
    default UserPass getUserPass(String name)
    {
        return Optional.ofNullable(getNameValue(name))
                       .map(map -> {
                           String user = Optional.ofNullable(map.get(USERNAME))
                                                 .orElseThrow(illegalState("'username' key not found for [" + name + "]"))
                                                 .toString();

                           String pass = Optional.ofNullable(map.get(PASSWORD))
                                                 .orElseThrow(illegalState("'password' key not found for [" + name + "]"))
                                                 .toString();

                           return new UserPass(user, pass);
                       })
                       .orElseThrow(illegalState("credentials for [" + name + "] not found"));
    }

    /**
     * @since 0.9.0
     */
    default boolean runAsApp()
    {
        return false;
    }

    private Supplier<IllegalStateException> illegalState(String message)
    {
        return () -> new IllegalStateException(message);
    }

    public static class GithubApp
    {
        private final Map<String, Object> creds;

        GithubApp(Map<String, Object> creds)
        {
            this.creds = creds;
        }

        public String getAppId()
        {
            return creds.get(APP_ID).toString();
        }

        public Reader getAppKey()
        {
            return (Reader) creds.get(APP_KEY);
        }

        public long getInstallId()
        {
            return Long.valueOf(creds.get(INSTALL_ID).toString());
        }
    }

    public static class UserPass
    {
        private final String password;

        private final String username;

        UserPass(String username, String password)
        {
            this.username = username;
            this.password = password;
        }

        public String getPassword()
        {
            return password;
        }

        public String getUsername()
        {
            return username;
        }
    }
}
