// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.TimeUnit;
import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.util.concurrent.locks.Lock;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class InstrumentedLock implements Lock
{
    private final Lock lock;
    private final Logger logger;
    private final String name;
    private final Timer clock;
    private final long minLoggingGap;
    private final long lockWarningThreshold;
    private volatile long lockAcquireTimestamp;
    private final AtomicLong lastLogTimestamp;
    private final AtomicLong warningsSuppressed;
    
    public InstrumentedLock(final String name, final Logger logger, final long minLoggingGapMs, final long lockWarningThresholdMs) {
        this(name, logger, new ReentrantLock(), minLoggingGapMs, lockWarningThresholdMs);
    }
    
    public InstrumentedLock(final String name, final Logger logger, final Lock lock, final long minLoggingGapMs, final long lockWarningThresholdMs) {
        this(name, logger, lock, minLoggingGapMs, lockWarningThresholdMs, new Timer());
    }
    
    @VisibleForTesting
    InstrumentedLock(final String name, final Logger logger, final Lock lock, final long minLoggingGapMs, final long lockWarningThresholdMs, final Timer clock) {
        this.warningsSuppressed = new AtomicLong(0L);
        this.name = name;
        this.lock = lock;
        this.clock = clock;
        this.logger = logger;
        this.minLoggingGap = minLoggingGapMs;
        this.lockWarningThreshold = lockWarningThresholdMs;
        this.lastLogTimestamp = new AtomicLong(clock.monotonicNow() - Math.max(this.minLoggingGap, this.lockWarningThreshold));
    }
    
    @Override
    public void lock() {
        this.lock.lock();
        this.startLockTiming();
    }
    
    @Override
    public void lockInterruptibly() throws InterruptedException {
        this.lock.lockInterruptibly();
        this.startLockTiming();
    }
    
    @Override
    public boolean tryLock() {
        if (this.lock.tryLock()) {
            this.startLockTiming();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
        if (this.lock.tryLock(time, unit)) {
            this.startLockTiming();
            return true;
        }
        return false;
    }
    
    @Override
    public void unlock() {
        final long localLockReleaseTime = this.clock.monotonicNow();
        final long localLockAcquireTime = this.lockAcquireTimestamp;
        this.lock.unlock();
        this.check(localLockAcquireTime, localLockReleaseTime);
    }
    
    @Override
    public Condition newCondition() {
        return this.lock.newCondition();
    }
    
    @VisibleForTesting
    void logWarning(final long lockHeldTime, final long suppressed) {
        this.logger.warn(String.format("Lock held time above threshold: lock identifier: %s lockHeldTimeMs=%d ms. Suppressed %d lock warnings. The stack trace is: %s", this.name, lockHeldTime, suppressed, StringUtils.getStackTrace(Thread.currentThread())));
    }
    
    protected void startLockTiming() {
        this.lockAcquireTimestamp = this.clock.monotonicNow();
    }
    
    protected void check(final long acquireTime, final long releaseTime) {
        if (!this.logger.isWarnEnabled()) {
            return;
        }
        final long lockHeldTime = releaseTime - acquireTime;
        if (this.lockWarningThreshold - lockHeldTime < 0L) {
            long localLastLogTs;
            long now;
            do {
                now = this.clock.monotonicNow();
                localLastLogTs = this.lastLogTimestamp.get();
                final long deltaSinceLastLog = now - localLastLogTs;
                if (deltaSinceLastLog - this.minLoggingGap < 0L) {
                    this.warningsSuppressed.incrementAndGet();
                    return;
                }
            } while (!this.lastLogTimestamp.compareAndSet(localLastLogTs, now));
            final long suppressed = this.warningsSuppressed.getAndSet(0L);
            this.logWarning(lockHeldTime, suppressed);
        }
    }
    
    protected Lock getLock() {
        return this.lock;
    }
    
    protected Timer getTimer() {
        return this.clock;
    }
}
