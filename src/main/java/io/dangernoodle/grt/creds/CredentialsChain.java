package io.dangernoodle.grt.creds;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import io.dangernoodle.grt.Credentials;

/**
 * @since 0.8.0
 */
public class CredentialsChain implements Credentials
{
    private final Collection<Credentials> providers;

    public CredentialsChain(Credentials... providers)
    {
        this.providers = Arrays.asList(providers);
    }

    @Override
    public String getAuthToken(String key)
    {
        return findCredentials(provider -> provider.getAuthToken(key));
    }

    @Override
    public Map<String, String> getNameValue(String key)
    {
        return findCredentials(provider -> provider.getNameValue(key));
    }

    private <T> T findCredentials(Function<Credentials, T> function)
    {
        return providers.stream()
                        .map(provider -> function.apply(provider))
                        .filter(value -> value != null)
                        .findFirst()
                        .orElse(null);
    }
}
