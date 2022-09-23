// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.retry;

import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;

public class BoundedExponentialBackoffRetry extends ExponentialBackoffRetry
{
    private final int maxSleepTimeMs;
    
    public BoundedExponentialBackoffRetry(final int baseSleepTimeMs, final int maxSleepTimeMs, final int maxRetries) {
        super(baseSleepTimeMs, maxRetries);
        this.maxSleepTimeMs = maxSleepTimeMs;
    }
    
    @VisibleForTesting
    public int getMaxSleepTimeMs() {
        return this.maxSleepTimeMs;
    }
    
    @Override
    protected long getSleepTimeMs(final int retryCount, final long elapsedTimeMs) {
        return Math.min(this.maxSleepTimeMs, super.getSleepTimeMs(retryCount, elapsedTimeMs));
    }
}
