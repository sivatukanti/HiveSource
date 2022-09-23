// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import java.util.concurrent.locks.Lock;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.util.concurrent.locks.ReadWriteLock;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class InstrumentedReadWriteLock implements ReadWriteLock
{
    private final Lock readLock;
    private final Lock writeLock;
    
    InstrumentedReadWriteLock(final boolean fair, final String name, final Logger logger, final long minLoggingGapMs, final long lockWarningThresholdMs) {
        final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(fair);
        this.readLock = new InstrumentedReadLock(name, logger, readWriteLock, minLoggingGapMs, lockWarningThresholdMs);
        this.writeLock = new InstrumentedWriteLock(name, logger, readWriteLock, minLoggingGapMs, lockWarningThresholdMs);
    }
    
    @Override
    public Lock readLock() {
        return this.readLock;
    }
    
    @Override
    public Lock writeLock() {
        return this.writeLock;
    }
}
