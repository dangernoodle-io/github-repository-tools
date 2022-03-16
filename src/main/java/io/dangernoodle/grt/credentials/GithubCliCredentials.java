package io.dangernoodle.grt.credentials;

import static io.dangernoodle.grt.Constants.APP_ID;
import static io.dangernoodle.grt.Constants.APP_ID_OPT;
import static io.dangernoodle.grt.Constants.APP_KEY;
import static io.dangernoodle.grt.Constants.APP_KEY_OPT;
import static io.dangernoodle.grt.Constants.APP_OPT;
import static io.dangernoodle.grt.Constants.GITHUB;
import static io.dangernoodle.grt.Constants.GITHUB_APP;
import static io.dangernoodle.grt.Constants.INSTALL_ID;
import static io.dangernoodle.grt.Constants.INSTALL_ID_OPT;
import static io.dangernoodle.grt.Constants.OAUTH_OPT;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.Credentials;


/**
 * Provides <code>Github</code> credentials from the command line.
 * <p>
 * Credentials can either be an <code>oauth</code> token or <code>Github App</code> private key.
 * </p>
 * 
 * @since 0.9.0
 */
public class GithubCliCredentials implements Credentials
{
    private final Arguments arguments;

    public GithubCliCredentials(Arguments arguments)
    {
        this.arguments = arguments;
    }

    @Override
    public String getCredentials(String key)
    {
        return arguments.getOption(GITHUB.equals(key) ? OAUTH_OPT : key);
    }

    @Override
    public Map<String, Object> getNameValue(String key) throws UncheckedIOException
    {
        if (GITHUB_APP.equals(key) && hasRequired())
        {
            return Map.of(
                    APP_ID, arguments.getOption(APP_ID_OPT),
                    INSTALL_ID, arguments.getOption(INSTALL_ID_OPT),
                    // add the private key as a ready-to-go reader
                    APP_KEY, convertToReader(arguments.getOption(APP_KEY_OPT)));
        }

        return null;
    }

    @Override
    public boolean runAsApp()
    {
        // checking for 'APT_OPT' is a cheat that will allow the credentials to come from another providers
        return arguments.hasOption(APP_OPT) || arguments.hasOption(APP_ID_OPT);
    }

    private Reader convertToReader(Path path) throws UncheckedIOException
    {
        try
        {
            return Files.newBufferedReader(path);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    private boolean hasRequired()
    {
        return arguments.hasOption(APP_ID_OPT) && arguments.hasOption(INSTALL_ID_OPT) && arguments.hasOption(APP_KEY_OPT);
    }
}
