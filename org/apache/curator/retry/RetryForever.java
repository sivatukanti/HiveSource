// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.retry;

import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import org.apache.curator.RetrySleeper;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.apache.curator.RetryPolicy;

public class RetryForever implements RetryPolicy
{
    private static final Logger log;
    private final int retryIntervalMs;
    
    public RetryForever(final int retryIntervalMs) {
        Preconditions.checkArgument(retryIntervalMs > 0);
        this.retryIntervalMs = retryIntervalMs;
    }
    
    @Override
    public boolean allowRetry(final int retryCount, final long elapsedTimeMs, final RetrySleeper sleeper) {
        try {
            sleeper.sleepFor(this.retryIntervalMs, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            RetryForever.log.warn("Error occurred while sleeping", e);
            return false;
        }
        return true;
    }
    
    static {
        log = LoggerFactory.getLogger(RetryForever.class);
    }
}
