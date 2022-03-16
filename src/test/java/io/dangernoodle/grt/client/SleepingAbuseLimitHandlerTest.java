package io.dangernoodle.grt.client;

import static io.dangernoodle.grt.client.SleepingAbuseLimitHandler.RETRY_AFTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.connector.GitHubConnectorResponse;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class SleepingAbuseLimitHandlerTest
{
    private int actualDelay;

    private SleepingAbuseLimitHandler handler;

    @Mock
    private GitHubConnectorResponse mockResponse;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
        handler = new SleepingAbuseLimitHandler()
        {
            @Override
            void napTime(int delay) throws InterruptedException
            {
                actualDelay = delay;
                super.napTime(0);
            }
        };
    }

    @Test
    public void testRetryInHeader() throws Exception
    {
        givenResponseHasHeader();
        whenOnError();
        thenNapWouldHappen();
    }

    private void givenResponseHasHeader()
    {
        when(mockResponse.header(RETRY_AFTER)).thenReturn("1");
    }

    private void thenNapWouldHappen()
    {
        assertEquals(mockResponse.header(RETRY_AFTER), String.valueOf(actualDelay));
    }

    private void whenOnError() throws IOException
    {
        handler.onError(mockResponse);
    }
}
