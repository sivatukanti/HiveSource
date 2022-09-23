// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public final class NonReentrantLock extends AbstractQueuedSynchronizer implements Lock
{
    private static final long serialVersionUID = -833780837233068610L;
    private Thread owner;
    
    public void lock() {
        this.acquire(1);
    }
    
    public void lockInterruptibly() throws InterruptedException {
        this.acquireInterruptibly(1);
    }
    
    public boolean tryLock() {
        return this.tryAcquire(1);
    }
    
    public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
        return this.tryAcquireNanos(1, unit.toNanos(time));
    }
    
    public void unlock() {
        this.release(1);
    }
    
    public boolean isHeldByCurrentThread() {
        return this.isHeldExclusively();
    }
    
    public Condition newCondition() {
        return new ConditionObject();
    }
    
    @Override
    protected boolean tryAcquire(final int acquires) {
        if (this.compareAndSetState(0, 1)) {
            this.owner = Thread.currentThread();
            return true;
        }
        return false;
    }
    
    @Override
    protected boolean tryRelease(final int releases) {
        if (Thread.currentThread() != this.owner) {
            throw new IllegalMonitorStateException();
        }
        this.owner = null;
        this.setState(0);
        return true;
    }
    
    @Override
    protected boolean isHeldExclusively() {
        return this.getState() != 0 && this.owner == Thread.currentThread();
    }
}
