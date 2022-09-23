// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.retry;

public class RetryOneTime extends RetryNTimes
{
    public RetryOneTime(final int sleepMsBetweenRetry) {
        super(1, sleepMsBetweenRetry);
    }
}
