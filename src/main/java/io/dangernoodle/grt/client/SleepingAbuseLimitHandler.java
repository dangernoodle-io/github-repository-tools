package io.dangernoodle.grt.client;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Optional;

import org.kohsuke.github.GitHubAbuseLimitHandler;
import org.kohsuke.github.connector.GitHubConnectorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @since 0.9.0
 */
public class SleepingAbuseLimitHandler extends GitHubAbuseLimitHandler
{
    private static final Logger logger = LoggerFactory.getLogger(SleepingAbuseLimitHandler.class);

    static final String RETRY_AFTER = "Retry-After";

    @Override
    public void onError(GitHubConnectorResponse response) throws IOException
    {
        try
        {
            int delay = parseRetryAfter(response);
            logger.warn("abuse limit detected, sleeping for [{}] seconds", delay);

            napTime(delay);
        }
        catch (InterruptedException e)
        {
            throw (IOException) new InterruptedIOException().initCause(e);
        }
    }

    void napTime(int delay) throws InterruptedException
    {
        Thread.sleep(delay * 1000);
    }

    private int parseRetryAfter(GitHubConnectorResponse response)
    {
        return Optional.ofNullable(response.header(RETRY_AFTER))
                       .map(retry -> Math.max(1, Integer.parseInt(retry)))
                       .orElse(60);
    }
}
