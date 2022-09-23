// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.cache;

import org.apache.derby.iapi.services.cache.CacheableFactory;
import org.apache.derby.iapi.services.cache.CacheManager;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.cache.Cacheable;

final class CachedItem
{
    private boolean valid_;
    private boolean removeRequested_;
    private boolean settingIdentity_;
    private boolean removeOk_;
    private boolean recentlyUsed_;
    private int keepCount;
    private Cacheable entry;
    
    public CachedItem() {
        this.valid_ = false;
        this.removeRequested_ = false;
        this.settingIdentity_ = false;
        this.removeOk_ = false;
        this.recentlyUsed_ = false;
    }
    
    public void keepAfterSearch() {
        ++this.keepCount;
        this.setUsed(true);
    }
    
    public void keepForCreate() {
        this.keepCount = 1;
        this.settingIdentity_ = true;
    }
    
    public void unkeepForCreate() {
        this.settingIdentityComplete();
        this.unkeep();
    }
    
    public void keepForClean() {
        ++this.keepCount;
    }
    
    public synchronized boolean unkeep() {
        final int keepCount = this.keepCount - 1;
        this.keepCount = keepCount;
        return keepCount == 0 && this.removeRequested_;
    }
    
    public final boolean isKept() {
        return this.keepCount != 0;
    }
    
    public void clean(final boolean b) throws StandardException {
        this.entry.clean(b);
    }
    
    public synchronized void setRemoveState() {
        this.removeRequested_ = true;
    }
    
    public final synchronized boolean isValid() {
        return this.valid_;
    }
    
    public synchronized void setValidState(final boolean b) {
        this.valid_ = b;
        this.removeRequested_ = false;
        this.removeOk_ = false;
        this.recentlyUsed_ = b;
    }
    
    public Cacheable getEntry() {
        return this.entry;
    }
    
    public Cacheable takeOnIdentity(final CacheManager cacheManager, final CacheableFactory cacheableFactory, final Object identity, final boolean b, final Object o) throws StandardException {
        Cacheable entry = this.entry;
        if (entry == null) {
            entry = cacheableFactory.newCacheable(cacheManager);
        }
        if (b) {
            this.entry = entry.createIdentity(identity, o);
        }
        else {
            this.entry = entry.setIdentity(identity);
        }
        if (this.entry != null) {
            return this.entry;
        }
        this.entry = entry;
        return null;
    }
    
    public synchronized void settingIdentityComplete() {
        this.settingIdentity_ = false;
        this.notifyAll();
    }
    
    public synchronized Cacheable use() throws StandardException {
        while (this.settingIdentity_) {
            try {
                this.wait();
                continue;
            }
            catch (InterruptedException ex) {
                throw StandardException.interrupt(ex);
            }
            break;
        }
        if (!this.valid_) {
            return null;
        }
        return this.entry;
    }
    
    public void remove(final boolean b) throws StandardException {
        if (!b) {
            synchronized (this) {
                while (!this.removeOk_) {
                    try {
                        this.wait();
                        continue;
                    }
                    catch (InterruptedException ex) {
                        throw StandardException.interrupt(ex);
                    }
                    break;
                }
            }
        }
        this.clean(true);
    }
    
    public synchronized void notifyRemover() {
        this.removeOk_ = true;
        this.notifyAll();
    }
    
    public synchronized void setUsed(final boolean recentlyUsed_) {
        this.recentlyUsed_ = recentlyUsed_;
    }
    
    public synchronized boolean recentlyUsed() {
        return this.recentlyUsed_;
    }
}
