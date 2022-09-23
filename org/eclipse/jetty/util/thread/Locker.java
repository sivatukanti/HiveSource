// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Locker
{
    private final ReentrantLock _lock;
    private final Lock _unlock;
    
    public Locker() {
        this._lock = new ReentrantLock();
        this._unlock = new Lock();
    }
    
    public Lock lock() {
        if (this._lock.isHeldByCurrentThread()) {
            throw new IllegalStateException("Locker is not reentrant");
        }
        this._lock.lock();
        return this._unlock;
    }
    
    public boolean isLocked() {
        return this._lock.isLocked();
    }
    
    public Condition newCondition() {
        return this._lock.newCondition();
    }
    
    public class Lock implements AutoCloseable
    {
        @Override
        public void close() {
            Locker.this._lock.unlock();
        }
    }
}
