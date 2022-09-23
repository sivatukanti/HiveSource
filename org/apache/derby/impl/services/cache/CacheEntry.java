// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.cache;

import java.util.concurrent.locks.Condition;
import org.apache.derby.iapi.services.cache.Cacheable;
import java.util.concurrent.locks.ReentrantLock;

final class CacheEntry
{
    private final ReentrantLock mutex;
    private Cacheable cacheable;
    private int keepCount;
    private Condition forRemove;
    private Condition settingIdentity;
    private ReplacementPolicy.Callback callback;
    
    CacheEntry() {
        this.mutex = new ReentrantLock();
        this.settingIdentity = this.mutex.newCondition();
    }
    
    void lock() {
        this.mutex.lock();
    }
    
    void waitUntilIdentityIsSet() {
        while (this.settingIdentity != null) {
            this.settingIdentity.awaitUninterruptibly();
        }
    }
    
    void unlock() {
        this.mutex.unlock();
    }
    
    void settingIdentityComplete() {
        this.settingIdentity.signalAll();
        this.settingIdentity = null;
    }
    
    void keep(final boolean b) {
        ++this.keepCount;
        if (b) {
            this.callback.access();
        }
    }
    
    void unkeep() {
        --this.keepCount;
        if (this.forRemove != null && this.keepCount == 1) {
            this.forRemove.signal();
        }
    }
    
    boolean isKept() {
        return this.keepCount > 0;
    }
    
    void unkeepForRemove() {
        if (this.keepCount > 1) {
            this.forRemove = this.mutex.newCondition();
            while (this.keepCount > 1) {
                this.forRemove.awaitUninterruptibly();
            }
            this.forRemove = null;
        }
        --this.keepCount;
    }
    
    void setCacheable(final Cacheable cacheable) {
        this.cacheable = cacheable;
    }
    
    Cacheable getCacheable() {
        return this.cacheable;
    }
    
    boolean isValid() {
        return this.settingIdentity == null && this.cacheable != null;
    }
    
    void setCallback(final ReplacementPolicy.Callback callback) {
        this.callback = callback;
    }
    
    void free() {
        if (this.callback != null) {
            this.callback.free();
        }
        this.cacheable = null;
    }
}
