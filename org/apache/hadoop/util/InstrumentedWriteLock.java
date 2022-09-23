// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class InstrumentedWriteLock extends InstrumentedLock
{
    public InstrumentedWriteLock(final String name, final Logger logger, final ReentrantReadWriteLock readWriteLock, final long minLoggingGapMs, final long lockWarningThresholdMs) {
        this(name, logger, readWriteLock, minLoggingGapMs, lockWarningThresholdMs, new Timer());
    }
    
    @VisibleForTesting
    InstrumentedWriteLock(final String name, final Logger logger, final ReentrantReadWriteLock readWriteLock, final long minLoggingGapMs, final long lockWarningThresholdMs, final Timer clock) {
        super(name, logger, readWriteLock.writeLock(), minLoggingGapMs, lockWarningThresholdMs, clock);
    }
}
