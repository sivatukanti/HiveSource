// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.retry;

import java.util.concurrent.TimeUnit;
import org.apache.curator.RetrySleeper;
import org.apache.curator.RetryPolicy;

abstract class SleepingRetry implements RetryPolicy
{
    private final int n;
    
    protected SleepingRetry(final int n) {
        this.n = n;
    }
    
    public int getN() {
        return this.n;
    }
    
    @Override
    public boolean allowRetry(final int retryCount, final long elapsedTimeMs, final RetrySleeper sleeper) {
        if (retryCount < this.n) {
            try {
                sleeper.sleepFor(this.getSleepTimeMs(retryCount, elapsedTimeMs), TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            return true;
        }
        return false;
    }
    
    protected abstract long getSleepTimeMs(final int p0, final long p1);
}
