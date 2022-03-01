package io.dangernoodle.grt.credentials;

import static io.dangernoodle.grt.Constants.GITHUB;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.dangernoodle.grt.Credentials;


/**
 * @since 0.8.0
 */
public class EnvironmentCredentials implements Credentials
{
    static final String GRT_GITHUB_OAUTH = "GRT_GITHUB_OAUTH_TOKEN";

    private final Function<String, String> mapper;

    private final Map<String, Collection<String>> nameValue;

    private final Map<String, String> tokens;

    public EnvironmentCredentials()
    {
        this(Collections.emptyMap());
    }

    public EnvironmentCredentials(Map<String, Collection<String>> nameValue, Function<String, String> mapper)
    {
        this(Collections.emptyMap(), nameValue, mapper);
    }

    public EnvironmentCredentials(Map<String, String> tokens)
    {
        // no mapper if no name/value pairs
        this(tokens, null, null);
    }

    public EnvironmentCredentials(Map<String, String> tokens, Map<String, Collection<String>> credentials, Function<String, String> mapper)
    {
        this.tokens = new HashMap<>(tokens);
        this.tokens.put(GITHUB, GRT_GITHUB_OAUTH);

        this.nameValue = Optional.ofNullable(credentials)
                                 .map(map -> new HashMap<>(credentials))
                                 .orElse(null);

        this.mapper = mapper;
    }

    @Override
    public String getAuthToken(String key)
    {
        return Optional.ofNullable(getEnvironmentVariable(get(key, tokens)))
                       .orElse(null);
    }

    @Override
    public Map<String, String> getNameValue(String key)
    {
        Collection<String> names = get(key, nameValue);
        return names.stream()
                    .collect(Collectors.toMap(this::mapKey, this::getEnvironmentVariable));
    }

    String getEnvironmentVariable(String name)
    {
        return System.getenv(name);
    }

    private <T> T get(String key, Map<String, T> map)
    {
        return Optional.ofNullable(map.get(key))
                       .orElseThrow(() -> new IllegalStateException("environment variable name not found for key [" + key + "]"));
    }

    private String mapKey(String key)
    {
        return mapper.apply(key);
    }
}
