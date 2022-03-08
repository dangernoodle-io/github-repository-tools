package io.dangernoodle.grt.credentials;

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


public class ChainedCredentialsTest
{
    private static final String KEY = "key";

    private static final Map<String, String> MAP = Collections.emptyMap();

    private static final String TOKEN = "token";

    private ChainedCredentials chain;

    private Map<String, String> map;

    @Mock
    private Credentials mockCredentials;

    private String token;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);

        chain = new ChainedCredentials(mockCredentials, mockCredentials);
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
        when(mockCredentials.getNameValue(KEY)).thenReturn(null)
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
        verify(mockCredentials, times(2)).getNameValue(KEY);
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
        map = chain.getNameValue(KEY);
    }
}
