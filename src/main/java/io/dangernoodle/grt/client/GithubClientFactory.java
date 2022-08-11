package io.dangernoodle.grt.client;

import static io.dangernoodle.grt.util.GithubRepositoryToolsUtils.readPrivateKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

import org.kohsuke.github.GHAppInstallation;
import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubAbuseLimitHandler;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.RateLimitChecker;
import org.kohsuke.github.authorization.AuthorizationProvider;
import org.kohsuke.github.authorization.ImmutableAuthorizationProvider;
import org.kohsuke.github.connector.GitHubConnector;
import org.kohsuke.github.extras.authorization.JWTTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dangernoodle.grt.Credentials;


public class GithubClientFactory
{
    private static final Logger logger = LoggerFactory.getLogger(GithubClientFactory.class);

    protected final boolean all;

    protected final GitHubConnector connector;

    protected final Credentials credentials;

    private final GHAppInstallation appInstall;

    public GithubClientFactory(Credentials credentials, GitHubConnector connector, boolean all) throws IllegalStateException
    {
        this.all = all;
        this.connector = connector;
        this.credentials = credentials;

        this.appInstall = Optional.ofNullable(getGitubApp())
                                  .map(this::getAppInstall)
                                  .orElse(null);
    }

    public GithubClient create() throws IOException
    {
        GitHubBuilder builder = createGithubBuilder();
        builder.withAuthorizationProvider(authorizationProvider())
               .withAbuseLimitHandler(abuseLimitHandler())
               .withConnector(connector)
               .withRateLimitChecker(rateLimitChecker());

        GitHub github = builder.build();
        github.checkApiUrlValidity();

        GHUser user = getCurrentUser(github);

        if (user == null)
        {
            user = github.getMyself();
        }

        GHRateLimit.Record core = github.getRateLimit().getCore();
        logger.info("current user [{}] - {} of {} api calls remaining (resets {})", user.getLogin(), core.getRemaining(),
                core.getLimit(), core.getResetDate());

        return new GithubClient(github, user);
    }

    protected GitHubAbuseLimitHandler abuseLimitHandler()
    {
        return new SleepingAbuseLimitHandler();
    }

    protected AuthorizationProvider authorizationProvider() throws IOException
    {
        if (appInstall == null)
        {
            return ImmutableAuthorizationProvider.fromOauthToken(credentials.getGithubOAuthToken());
        }

        return ImmutableAuthorizationProvider.fromAppInstallationToken(appInstall.createToken()
                                                                                 .create()
                                                                                 .getToken());
    }

    protected RateLimitChecker rateLimitChecker()
    {
        return new PercentRateLimitChecker(all);
    }

    // visible for testing
    GitHubBuilder createGithubBuilder()
    {
        return new GitHubBuilder();
    }

    private GHAppInstallation getAppInstall(Credentials.GithubApp creds) throws IllegalStateException
    {
        try
        {
            return new GitHubBuilder().withAuthorizationProvider(new JWTTokenProvider(creds.getAppId(), readPrivateKey(creds.getAppKey())))
                                      .withConnector(connector)
                                      .build()
                                      .getApp()
                                      .getInstallationById(creds.getInstallId());
        }
        catch (GeneralSecurityException | IOException e)
        {
            throw new IllegalStateException(e);
        }
    }

    private GHUser getCurrentUser(GitHub github) throws IOException
    {
        if (appInstall == null)
        {
            return github.getMyself();
        }

        return appInstall.getAccount();
    }

    private Credentials.GithubApp getGitubApp()
    {
        return credentials.runAsApp() ? credentials.getGithubApp() : null;
    }
}
