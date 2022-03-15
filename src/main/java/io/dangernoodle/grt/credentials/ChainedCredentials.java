package io.dangernoodle.grt.credentials;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import io.dangernoodle.grt.Credentials;


/**
 * Chains together multiple <code>Credentials</code> providers, returning the first entry found.
 * 
 * @since 0.8.0
 */
public class ChainedCredentials implements Credentials
{
    private final Collection<Credentials> credentials;

    public ChainedCredentials(Collection<Credentials> credentials)
    {
        this.credentials = credentials;
    }

    @Override
    public String getCredentials(String key)
    {
        return findCredentials(credentials -> credentials.getCredentials(key));
    }

    @Override
    public Map<String, Object> getNameValue(String key)
    {
        return findCredentials(credentials -> credentials.getNameValue(key));
    }

    @Override
    public boolean runAsApp()
    {
        /*
         * 'findAny' is a cheat to allow the cli to indicate 'runAsApp' but have the credentials supplied via another
         * provider
         */
        return credentials.stream()
                          .filter(Credentials::runAsApp)
                          .findAny()
                          .isPresent();
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
