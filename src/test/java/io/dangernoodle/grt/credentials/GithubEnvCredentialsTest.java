package io.dangernoodle.grt.credentials;

import static io.dangernoodle.grt.Constants.APP_ID;
import static io.dangernoodle.grt.Constants.APP_KEY;
import static io.dangernoodle.grt.Constants.GRT_GITHUB_APP_ID;
import static io.dangernoodle.grt.Constants.GRT_GITHUB_APP_KEY;
import static io.dangernoodle.grt.Constants.GRT_GITHUB_INSTALL_ID;
import static io.dangernoodle.grt.Constants.GRT_GITHUB_OAUTH;
import static io.dangernoodle.grt.Constants.INSTALL_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.Reader;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Credentials;


public class GithubEnvCredentialsTest
{
    private Map<String, Object> actualNvp;

    private String actualToken;

    private Credentials credentials;

    private String expectedToken;

    @Mock
    private Map<String, String> mockEnv;

    private boolean runAsApp;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);

        credentials = new EnvironmentCredentials.Github()
        {
            @Override
            Map<String, String> getSystemEnv()
            {
                return mockEnv;
            }
        };
    }

    @Test
    public void testGithubApp()
    {
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
    public void testRunAsApp()
    {
        givenEnvHaveOpts();
        whenRunAsApp();
        thenRunAsAppFalse();

        whenRunAsApp();
        thenRunAsAppFalse();

        whenRunAsApp();
        thenRunAsAppFalse();

        whenRunAsApp();
        thenRunAsAppTrue();
    }

    private void givenAGithubApp()
    {
        when(mockEnv.get(GRT_GITHUB_APP_ID)).thenReturn(APP_ID);
        when(mockEnv.get(GRT_GITHUB_INSTALL_ID)).thenReturn(INSTALL_ID);
        when(mockEnv.get(GRT_GITHUB_APP_KEY)).thenReturn(APP_KEY);
    }

    private void givenAGithubToken()
    {
        expectedToken = "oauth";
        when(mockEnv.get(GRT_GITHUB_OAUTH)).thenReturn(expectedToken);
    }

    private void givenEnvHaveOpts()
    {
        when(mockEnv.containsKey(GRT_GITHUB_APP_ID)).thenReturn(false)
                                                    .thenReturn(true);

        when(mockEnv.containsKey(GRT_GITHUB_INSTALL_ID)).thenReturn(false)
                                                        .thenReturn(true);

        when(mockEnv.containsKey(GRT_GITHUB_APP_KEY)).thenReturn(false)
                                                     .thenReturn(true);
    }

    private void thenAppCredsMatch()
    {
        assertEquals(APP_ID, actualNvp.get(APP_ID));
        assertEquals(INSTALL_ID, actualNvp.get(INSTALL_ID));

        assertNotNull(actualNvp.get(APP_KEY));
        assertTrue(actualNvp.get(APP_KEY) instanceof Reader);
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

    private void whenGetGithubApp()
    {
        actualNvp = credentials.getGithubApp();
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
