// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.retry;

import org.slf4j.LoggerFactory;
import org.apache.curator.RetrySleeper;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import java.util.Random;
import org.slf4j.Logger;

public class ExponentialBackoffRetry extends SleepingRetry
{
    private static final Logger log;
    private static final int MAX_RETRIES_LIMIT = 29;
    private static final int DEFAULT_MAX_SLEEP_MS = Integer.MAX_VALUE;
    private final Random random;
    private final int baseSleepTimeMs;
    private final int maxSleepMs;
    
    public ExponentialBackoffRetry(final int baseSleepTimeMs, final int maxRetries) {
        this(baseSleepTimeMs, maxRetries, Integer.MAX_VALUE);
    }
    
    public ExponentialBackoffRetry(final int baseSleepTimeMs, final int maxRetries, final int maxSleepMs) {
        super(validateMaxRetries(maxRetries));
        this.random = new Random();
        this.baseSleepTimeMs = baseSleepTimeMs;
        this.maxSleepMs = maxSleepMs;
    }
    
    @VisibleForTesting
    public int getBaseSleepTimeMs() {
        return this.baseSleepTimeMs;
    }
    
    @Override
    protected long getSleepTimeMs(final int retryCount, final long elapsedTimeMs) {
        long sleepMs = this.baseSleepTimeMs * Math.max(1, this.random.nextInt(1 << retryCount + 1));
        if (sleepMs > this.maxSleepMs) {
            ExponentialBackoffRetry.log.warn(String.format("Sleep extension too large (%d). Pinning to %d", sleepMs, this.maxSleepMs));
            sleepMs = this.maxSleepMs;
        }
        return sleepMs;
    }
    
    private static int validateMaxRetries(int maxRetries) {
        if (maxRetries > 29) {
            ExponentialBackoffRetry.log.warn(String.format("maxRetries too large (%d). Pinning to %d", maxRetries, 29));
            maxRetries = 29;
        }
        return maxRetries;
    }
    
    static {
        log = LoggerFactory.getLogger(ExponentialBackoffRetry.class);
    }
}
