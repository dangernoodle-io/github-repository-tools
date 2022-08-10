package io.dangernoodle.grt.credentials;

import static io.dangernoodle.RepositoryFiles.dummyPemPath;
import static io.dangernoodle.grt.Constants.APP_ID;
import static io.dangernoodle.grt.Constants.APP_ID_OPT;
import static io.dangernoodle.grt.Constants.APP_KEY_OPT;
import static io.dangernoodle.grt.Constants.APP_OPT;
import static io.dangernoodle.grt.Constants.INSTALL_ID_OPT;
import static io.dangernoodle.grt.Constants.OAUTH_OPT;
import static io.dangernoodle.grt.Constants.USERNAME_OPT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Arguments;
import io.dangernoodle.grt.Credentials;


public class GithubCliCredentialsTest
{
    private Credentials.GithubApp githubApp;

    private String actualToken;

    private Credentials credentials;

    private String expectedToken;

    @Mock
    private Arguments mockArguments;

    private boolean runAsApp;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
        credentials = new GithubCliCredentials(mockArguments);
    }

    @Test
    public void testGetCredentials()
    {
        givenOtherCredentials();
        whenGetCredentials();
        thenCredentialsMatch();
    }

    @Test
    public void testGithubApp() throws Exception
    {
        checkAppCredsAreNull();

        givenAppWithNoKey();
        checkAppCredsAreNull();

        givenAGithubApp();
        whenGetGithubApp();
        thenAppCredsMatch();
    }

    @Test
    public void testGithubOAuthToken()
    {
        givenAGithubToken();
        whenGetGithubToken();
        thenCredentialsMatch();
    }

    @Test
    public void testNullNameValue()
    {
        assertNull(credentials.getNameValue(USERNAME_OPT));
    }

    @Test
    public void testRunAsApp()
    {
        givenArgsHaveOpts();
        whenRunAsApp();
        thenRunAsAppFalse();

        whenRunAsApp();
        thenRunAsAppTrue();

        whenRunAsApp();
        thenRunAsAppTrue();
    }

    private void checkAppCredsAreNull()
    {
        assertThrows(IllegalStateException.class, this::whenGetGithubApp);
    }

    private void givenAGithubApp() throws URISyntaxException
    {
        when(mockArguments.hasOption(APP_ID_OPT)).thenReturn(true);
        when(mockArguments.hasOption(INSTALL_ID_OPT)).thenReturn(true);
        when(mockArguments.hasOption(APP_KEY_OPT)).thenReturn(true);

        when(mockArguments.getOption(APP_ID_OPT)).thenReturn(APP_ID);
        when(mockArguments.getOption(INSTALL_ID_OPT)).thenReturn("1");
        when(mockArguments.getOption(APP_KEY_OPT)).thenReturn(dummyPemPath());
    }

    private void givenAGithubToken()
    {
        expectedToken = "github";
        when(mockArguments.getOption(OAUTH_OPT)).thenReturn(expectedToken);
    }

    private void givenAppWithNoKey()
    {
        when(mockArguments.hasOption(APP_ID_OPT)).thenReturn(true);
        when(mockArguments.hasOption(INSTALL_ID_OPT)).thenReturn(true);
        when(mockArguments.hasOption(APP_KEY_OPT)).thenReturn(false);
    }

    private void givenArgsHaveOpts()
    {
        when(mockArguments.hasOption(APP_OPT)).thenReturn(false)
                                              .thenReturn(false)
                                              .thenReturn(true);

        when(mockArguments.hasOption(APP_ID_OPT)).thenReturn(false)
                                                 .thenReturn(true);
    }

    private void givenOtherCredentials()
    {
        expectedToken = "12345";
        when(mockArguments.getOption(USERNAME_OPT)).thenReturn(expectedToken);
    }

    private void thenAppCredsMatch()
    {
        assertEquals(APP_ID, githubApp.getAppId());
        assertEquals(1L, githubApp.getInstallId());
        assertNotNull(githubApp.getAppKey());
    }

    private void thenCredentialsMatch()
    {
        assertEquals(expectedToken, actualToken);
    }

    private void thenRunAsAppFalse()
    {
        assertFalse(runAsApp);
    }

    private void thenRunAsAppTrue()
    {
        assertTrue(runAsApp);
    }

    private void whenGetCredentials()
    {
        actualToken = credentials.getCredentials(USERNAME_OPT);
    }

    private void whenGetGithubApp()
    {
        githubApp = credentials.getGithubApp();
    }

    private void whenGetGithubToken()
    {
        actualToken = credentials.getGithubOAuthToken();
    }

    private void whenRunAsApp()
    {
        runAsApp = credentials.runAsApp();
    }
}
