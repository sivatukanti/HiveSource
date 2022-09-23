// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.retry;

import org.apache.curator.RetrySleeper;

public class RetryNTimes extends SleepingRetry
{
    private final int sleepMsBetweenRetries;
    
    public RetryNTimes(final int n, final int sleepMsBetweenRetries) {
        super(n);
        this.sleepMsBetweenRetries = sleepMsBetweenRetries;
    }
    
    @Override
    protected long getSleepTimeMs(final int retryCount, final long elapsedTimeMs) {
        return this.sleepMsBetweenRetries;
    }
}
