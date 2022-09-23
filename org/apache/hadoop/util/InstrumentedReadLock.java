// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.locks.Lock;
import org.slf4j.Logger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class InstrumentedReadLock extends InstrumentedLock
{
    private final ReentrantReadWriteLock readWriteLock;
    private final ThreadLocal<Long> readLockHeldTimeStamp;
    
    public InstrumentedReadLock(final String name, final Logger logger, final ReentrantReadWriteLock readWriteLock, final long minLoggingGapMs, final long lockWarningThresholdMs) {
        this(name, logger, readWriteLock, minLoggingGapMs, lockWarningThresholdMs, new Timer());
    }
    
    @VisibleForTesting
    InstrumentedReadLock(final String name, final Logger logger, final ReentrantReadWriteLock readWriteLock, final long minLoggingGapMs, final long lockWarningThresholdMs, final Timer clock) {
        super(name, logger, readWriteLock.readLock(), minLoggingGapMs, lockWarningThresholdMs, clock);
        this.readLockHeldTimeStamp = new ThreadLocal<Long>() {
            @Override
            protected Long initialValue() {
                return Long.MAX_VALUE;
            }
        };
        this.readWriteLock = readWriteLock;
    }
    
    @Override
    public void unlock() {
        final boolean needReport = this.readWriteLock.getReadHoldCount() == 1;
        final long localLockReleaseTime = this.getTimer().monotonicNow();
        final long localLockAcquireTime = this.readLockHeldTimeStamp.get();
        this.getLock().unlock();
        if (needReport) {
            this.readLockHeldTimeStamp.remove();
            this.check(localLockAcquireTime, localLockReleaseTime);
        }
    }
    
    @Override
    protected void startLockTiming() {
        if (this.readWriteLock.getReadHoldCount() == 1) {
            this.readLockHeldTimeStamp.set(this.getTimer().monotonicNow());
        }
    }
}
