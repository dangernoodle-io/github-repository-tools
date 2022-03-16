package io.dangernoodle.grt.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHRateLimit;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class PercentRateLimiterCheckerTest
{
    private long actualDelay;

    private boolean block;

    private boolean limited;

    @Mock
    private GHRateLimit.Record mockRecord;

    @BeforeEach
    public void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
        actualDelay = -1;
    }

    @Test
    public void testLimited() throws Exception
    {
        givenBlockableRecord();
        givenBlockingEnabled();
        whenCheckRateLimit();
        thenRateLimited();
    }

    @Test
    public void testNotLimited() throws Exception
    {
        givenAnOkRecord();
        whenCheckRateLimit();
        thenNotRateLimited();
        
        givenBlockingEnabled();
        whenCheckRateLimit();
        thenNotRateLimited();
    }

    @Test
    public void testNotLimitedBlocking() throws Exception
    {
        givenBlockableRecord();
        whenCheckRateLimit();
        thenNotRateLimited();
    }

    private void givenAnOkRecord()
    {
        when(mockRecord.getLimit()).thenReturn(100);
        when(mockRecord.getRemaining()).thenReturn(100);
    }

    private void givenBlockableRecord()
    {
        when(mockRecord.getLimit()).thenReturn(100);
        when(mockRecord.getRemaining()).thenReturn(1);

        when(mockRecord.getResetDate()).thenReturn(new Date());
    }

    private void givenBlockingEnabled()
    {
        block = true;
    }

    private void thenNotRateLimited()
    {
        assertFalse(limited);
    }

    private void thenRateLimited()
    {
        assertTrue(actualDelay >= 0);
    }

    private void whenCheckRateLimit() throws InterruptedException
    {
        limited = new PercentRateLimitChecker(block)
        {
            @Override
            void napTime(long delay) throws InterruptedException
            {
                actualDelay = delay;
                super.napTime(0);
            }
        }
         .checkRateLimit(mockRecord, 0);
    }
}
