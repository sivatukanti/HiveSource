// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.retry;

import org.apache.curator.RetrySleeper;

public class RetryUntilElapsed extends SleepingRetry
{
    private final int maxElapsedTimeMs;
    private final int sleepMsBetweenRetries;
    
    public RetryUntilElapsed(final int maxElapsedTimeMs, final int sleepMsBetweenRetries) {
        super(Integer.MAX_VALUE);
        this.maxElapsedTimeMs = maxElapsedTimeMs;
        this.sleepMsBetweenRetries = sleepMsBetweenRetries;
    }
    
    @Override
    public boolean allowRetry(final int retryCount, final long elapsedTimeMs, final RetrySleeper sleeper) {
        return super.allowRetry(retryCount, elapsedTimeMs, sleeper) && elapsedTimeMs < this.maxElapsedTimeMs;
    }
    
    @Override
    protected long getSleepTimeMs(final int retryCount, final long elapsedTimeMs) {
        return this.sleepMsBetweenRetries;
    }
}
