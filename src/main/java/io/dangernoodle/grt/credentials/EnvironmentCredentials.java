package io.dangernoodle.grt.credentials;

import static io.dangernoodle.grt.Constants.APP_ID;
import static io.dangernoodle.grt.Constants.APP_KEY;
import static io.dangernoodle.grt.Constants.GITHUB;
import static io.dangernoodle.grt.Constants.GITHUB_APP;
import static io.dangernoodle.grt.Constants.GRT_GITHUB_APP_ID;
import static io.dangernoodle.grt.Constants.GRT_GITHUB_APP_KEY;
import static io.dangernoodle.grt.Constants.GRT_GITHUB_INSTALL_ID;
import static io.dangernoodle.grt.Constants.GRT_GITHUB_OAUTH;
import static io.dangernoodle.grt.Constants.INSTALL_ID;

import java.io.StringReader;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.dangernoodle.grt.Credentials;


/**
 * Provides credentials from the system environment.
 * <p>
 * An optional mapper may be specified to transform name/value keys to match what is expected.
 * </p>
 * 
 * @since 0.8.0
 */
public class EnvironmentCredentials implements Credentials
{
    private final Function<String, Object> mapper;

    private final Map<String, Collection<String>> nameValue;

    private final Map<String, String> tokens;

    public EnvironmentCredentials()
    {
        this(Collections.emptyMap());
    }

    public EnvironmentCredentials(Map<String, Collection<String>> nameValue, Function<String, Object> mapper)
    {
        this(Collections.emptyMap(), nameValue, mapper);
    }

    public EnvironmentCredentials(Map<String, String> tokens)
    {
        // no mapper if no name/value pairs
        this(tokens, Collections.emptyMap(), k -> k);
    }

    public EnvironmentCredentials(Map<String, String> tokens, Map<String, Collection<String>> nameValue, Function<String, Object> mapper)
    {
        this.mapper = mapper;

        this.tokens = new HashMap<>(tokens);
        this.nameValue = new HashMap<>(nameValue);
    }

    @Override
    public String getCredentials(String key)
    {
        return Optional.ofNullable(getEnvironmentVariable(tokens.get(key)))
                       .map(Object::toString)
                       .orElse(null);
    }

    @Override
    public Map<String, Object> getNameValue(String key)
    {
        return Optional.ofNullable(nameValue.get(key))
                       .map(names -> names.stream()
                                          .map(name -> new AbstractMap.SimpleEntry<>(mapKey(name), getEnvironmentVariable(name)))
                                          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                       .orElse(null);
    }

    protected Object getEnvironmentVariable(String name)
    {
        return getSystemEnv().get(name);
    }

    Map<String, String> getSystemEnv()
    {
        return System.getenv();
    }

    private String mapKey(String key)
    {
        return mapper.apply(key).toString();
    }

    /**
     * @since 0.9.0
     */
    public static class Github extends EnvironmentCredentials
    {
        public Github()
        {
            super(githubToken(), githubApp(), appMapper());
        }

        @Override
        public boolean runAsApp()
        {
            Map<String, String> env = getSystemEnv();
            return env.containsKey(GRT_GITHUB_APP_ID) && env.containsKey(GRT_GITHUB_INSTALL_ID) && env.containsKey(GRT_GITHUB_APP_KEY);
        }

        @Override
        protected Object getEnvironmentVariable(String name)
        {
            Object value = super.getEnvironmentVariable(name);

            if (GRT_GITHUB_APP_KEY.equals(name))
            {
                return new StringReader(value.toString());
            }

            return value;
        }

        private static Function<String, Object> appMapper()
        {
            return key -> {
                switch (key)
                {
                    case GRT_GITHUB_APP_ID:
                        return APP_ID;
                    case GRT_GITHUB_APP_KEY:
                        return APP_KEY;
                    default:
                        return INSTALL_ID;
                }
            };
        }

        private static Map<String, Collection<String>> githubApp()
        {
            return Map.of(GITHUB_APP, List.of(GRT_GITHUB_APP_ID, GRT_GITHUB_INSTALL_ID, GRT_GITHUB_APP_KEY));
        }

        private static Map<String, String> githubToken()
        {
            return Map.of(GITHUB, GRT_GITHUB_OAUTH);
        }
    }
}
