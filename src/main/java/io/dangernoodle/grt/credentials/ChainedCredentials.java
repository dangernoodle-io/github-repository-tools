package io.dangernoodle.grt.credentials;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.dangernoodle.grt.Credentials;


/**
 * @since 0.8.0
 */
public class ChainedCredentials implements Credentials
{
    private final Collection<Credentials> credentials;

    public ChainedCredentials(Collection<Credentials> credentials)
    {
        this.credentials = credentials;
    }

    public ChainedCredentials(Credentials... credentials)
    {
        this(List.of(credentials));
    }

    @Override
    public String getAuthToken(String key)
    {
        return findCredentials(credentials -> credentials.getAuthToken(key));
    }

    @Override
    public Map<String, String> getNameValue(String key)
    {
        return findCredentials(credentials -> credentials.getNameValue(key));
    }

    private <T> T findCredentials(Function<Credentials, T> function)
    {
        return credentials.stream()
                          .map(provider -> function.apply(provider))
                          .filter(value -> value != null)
                          .findFirst()
                          .orElse(null);
    }
}