// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.concurrent.locks.Condition;
import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

public class AutoCloseableLock implements AutoCloseable
{
    private final Lock lock;
    
    public AutoCloseableLock() {
        this(new ReentrantLock());
    }
    
    public AutoCloseableLock(final Lock lock) {
        this.lock = lock;
    }
    
    public AutoCloseableLock acquire() {
        this.lock.lock();
        return this;
    }
    
    public void release() {
        this.lock.unlock();
    }
    
    @Override
    public void close() {
        this.release();
    }
    
    public boolean tryLock() {
        return this.lock.tryLock();
    }
    
    @VisibleForTesting
    boolean isLocked() {
        if (this.lock instanceof ReentrantLock) {
            return ((ReentrantLock)this.lock).isLocked();
        }
        throw new UnsupportedOperationException();
    }
    
    public Condition newCondition() {
        return this.lock.newCondition();
    }
}
