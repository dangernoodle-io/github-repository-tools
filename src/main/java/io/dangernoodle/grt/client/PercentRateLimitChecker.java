package io.dangernoodle.grt.client;

import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.RateLimitChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PercentRateLimitChecker extends RateLimitChecker
{
    private static final Logger logger = LoggerFactory.getLogger(PercentRateLimitChecker.class);

    private final boolean block;

    private final double limit;

    public PercentRateLimitChecker(boolean block)
    {
        this(block, 0.10);
    }

    public PercentRateLimitChecker(boolean block, double limit)
    {
        this.block = block;
        this.limit = limit;
    }

    @Override
    protected boolean checkRateLimit(GHRateLimit.Record record, long count) throws InterruptedException
    {
        boolean slept = false;

        int limit = record.getLimit();
        int remaining = record.getRemaining();

        int percent = (int) (limit * this.limit);

        if (remaining < percent && block)
        {
            logger.warn("rate limit [{} ({}% of {})] reached, sleeping until", percent, this.limit * 100, limit,
                    record.getResetDate());

            long delay = Math.abs(record.getResetDate().getTime() - System.currentTimeMillis());
            napTime(delay);

            slept = true;
        }
        else if (remaining % 25 == 0 && block)
        {
            logger.warn("{} of {} api calls remaining", remaining, limit);
        }

        return slept;
    }
    
    void napTime(long delay) throws InterruptedException
    {
        Thread.sleep(delay);
    }
}
