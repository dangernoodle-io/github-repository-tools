package io.dangernoodle.grt.internal;

import static io.dangernoodle.grt.Constants.APP_ID_OPT;
import static io.dangernoodle.grt.Constants.APP_OPT;
import static io.dangernoodle.grt.Constants.INSTALL_ID_OPT;
import static io.dangernoodle.grt.Constants.OAUTH_OPT;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.dangernoodle.grt.cli.options.AbstractOptionTest;
import picocli.CommandLine.ArgGroup;


public class CredentialsOptionTest extends AbstractOptionTest
{
    @ArgGroup
    private CredentialsOption credentials;

    @Test
    public void testGithubApp()
    {
        givenAGithubApp();
        whenParseArguments();
        thenArgsContainsGithubApp();
    }

    @Test
    public void testGithubAppOnly()
    {
        givenAGithubAppOnly();
        whenParseArguments();
        thenArgsContainsGithubAppOnly();
    }

    @Test
    public void testGithubAppAndCreds()
    {
        // test '--app' and creds specified at the same time
        givenAGithubApp();
        givenAGithubAppOnly();
        thenParseHasErrors();
    }

    @Test
    public void testOAuthToken()
    {
        givenAnOAuthToken();
        whenParseArguments();
        thenArgsContainsOAuthToken();
    }

    @Test
    public void testTokenAndApp()
    {
        givenAGithubApp();
        givenAnOAuthToken();
        thenParseHasErrors();
    }

    private void givenAGithubApp()
    {
        args.add(APP_ID_OPT + "=abcd");
        args.add(INSTALL_ID_OPT + "=123");
    }

    private void givenAGithubAppOnly()
    {
        args.add(APP_OPT);
    }

    private void givenAnOAuthToken()
    {
        args.add(OAUTH_OPT + "=1234");
    }

    private void thenArgsContainsGithubApp()
    {
        assertTrue(parseResult.hasMatchedOption(APP_ID_OPT));
        assertTrue(parseResult.hasMatchedOption(INSTALL_ID_OPT));
    }

    private void thenArgsContainsGithubAppOnly()
    {
        assertTrue(parseResult.hasMatchedOption(APP_OPT));

    }

    private void thenArgsContainsOAuthToken()
    {
        assertTrue(parseResult.hasMatchedOption(OAUTH_OPT));
    }
}
