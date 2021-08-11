package io.dangernoodle.grt.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.dangernoodle.grt.Credentials;


public class CredentialsChainTest
{
    private static final String KEY = "key";

    private static final Map<String, String> MAP = Collections.emptyMap();

    private static final String TOKEN = "token";

    private CredentialsChain chain;

    private Map<String, String> map;

    @Mock
    private Credentials mockCredentials;

    private String token;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);

        chain = new CredentialsChain(mockCredentials, mockCredentials);
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

    private void givenCredentials()
    {
        when(mockCredentials.getCredentials(KEY)).thenReturn(null)
                                                 .thenReturn(MAP);
    }

    private void givenTokenCreds()
    {
        when(mockCredentials.getAuthToken(KEY)).thenReturn(null)
                                               .thenReturn(TOKEN);
    }

    private void thenAuthTokenChecked()
    {
        verify(mockCredentials, times(2)).getAuthToken(KEY);
    }

    private void thenCredentialsChecked()
    {
        verify(mockCredentials, times(2)).getCredentials(KEY);
    }

    private void thenCredsAreCorrect()
    {
        assertEquals(MAP, map);
    }

    private void thenTokenIsCorrect()
    {
        assertEquals(TOKEN, token);
    }

    private void whenGetAuthToken()
    {
        token = chain.getAuthToken(KEY);
    }

    private void whenGetCredentials()
    {
        map = chain.getCredentials(KEY);
    }
}
