package io.dangernoodle.grt.internal;

import static io.dangernoodle.grt.Constants.APP_ID_OPT;
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
    }
    
    private void givenAnOAuthToken()
    {
        args.add(OAUTH_OPT + "=1234");
    }

    private void thenArgsContainsGithubApp()
    {
        assertTrue(parseResult.hasMatchedOption(APP_ID_OPT));
    }

    private void thenArgsContainsOAuthToken()
    {
        assertTrue(parseResult.hasMatchedOption(OAUTH_OPT));
    }
}
