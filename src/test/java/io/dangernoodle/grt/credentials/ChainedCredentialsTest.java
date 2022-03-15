package io.dangernoodle.grt.credentials;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Credentials;


public class ChainedCredentialsTest
{
    private static final String KEY = "key";

    private static final Map<String, Object> MAP = Collections.emptyMap();

    private static final String TOKEN = "token";

    private ChainedCredentials chain;

    private Map<String, Object> map;

    @Mock
    private Credentials mockCredentials;

    private boolean runAsApp;

    private String token;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);

        chain = new ChainedCredentials(List.of(mockCredentials, mockCredentials));
    }

    @Test
    public void testGetAuthToken()
    {
        givenTokenCreds();
        whenGetAuthToken();
        thenTokenIsCorrect();
        thenAuthTokenChecked();
    }

    @Test
    public void testGetCredentials()
    {
        givenCredentials();
        whenGetCredentials();
        thenCredsAreCorrect();
        thenCredentialsChecked();
    }

    @Test
    public void testRunaAsApp()
    {
        givenRunAsApp();
        whenCheckRunAsApp();
        thenRunAsApp();
    }

    private void givenCredentials()
    {
        when(mockCredentials.getNameValue(KEY)).thenReturn(null)
                                               .thenReturn(MAP);
    }

    private void givenRunAsApp()
    {
        when(mockCredentials.runAsApp()).thenReturn(false)
                                        .thenReturn(true);
    }

    private void givenTokenCreds()
    {
        when(mockCredentials.getCredentials(KEY)).thenReturn(null)
                                                 .thenReturn(TOKEN);
    }

    private void thenAuthTokenChecked()
    {
        verify(mockCredentials, times(2)).getCredentials(KEY);
    }

    private void thenCredentialsChecked()
    {
        verify(mockCredentials, times(2)).getNameValue(KEY);
    }

    private void thenCredsAreCorrect()
    {
        assertEquals(MAP, map);
    }

    private void thenRunAsApp()
    {
        assertTrue(runAsApp);
    }

    private void thenTokenIsCorrect()
    {
        assertEquals(TOKEN, token);
    }

    private void whenCheckRunAsApp()
    {
        runAsApp = chain.runAsApp();
    }

    private void whenGetAuthToken()
    {
        token = chain.getCredentials(KEY);
    }

    private void whenGetCredentials()
    {
        map = chain.getNameValue(KEY);
    }
}
