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
        this(tokens, Collections.emptyMap(), Function.identity());
    }

    public EnvironmentCredentials(Map<String, String> tokens, Map<String, Collection<String>> credentials, Function<String, String> mapper)
    {
        this.tokens = new HashMap<>(tokens);
        this.tokens.put(GITHUB, GRT_GITHUB_OAUTH);

        this.mapper = mapper;
        this.nameValue = new HashMap<>(credentials);
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
                                          .collect(Collectors.toMap(this::mapKey, this::getEnvironmentVariable)))
                       .orElse(null);
    }

    String systemGetEnv(String name)
    {
        return System.getenv(name);
    }

    private Object getEnvironmentVariable(String name)
    {
        return Optional.ofNullable(name)
                       .map(this::systemGetEnv)
                       .orElse(null);

    }

    private String mapKey(String key)
    {
        return mapper.apply(key);
    }
}
