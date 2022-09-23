// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.cache;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.derby.iapi.services.daemon.DaemonService;
import java.util.Iterator;
import org.apache.derby.iapi.util.Matchable;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.cache.Cacheable;
import org.apache.derby.iapi.services.cache.CacheableFactory;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.derby.iapi.services.cache.CacheManager;

final class ConcurrentCache implements CacheManager
{
    private final ConcurrentHashMap<Object, CacheEntry> cache;
    private final CacheableFactory holderFactory;
    private final String name;
    private final int maxSize;
    private final ReplacementPolicy replacementPolicy;
    private volatile boolean stopped;
    private BackgroundCleaner cleaner;
    
    ConcurrentCache(final CacheableFactory holderFactory, final String name, final int initialCapacity, final int maxSize) {
        this.cache = new ConcurrentHashMap<Object, CacheEntry>(initialCapacity);
        this.replacementPolicy = new ClockPolicy(this, initialCapacity, maxSize);
        this.holderFactory = holderFactory;
        this.name = name;
        this.maxSize = maxSize;
    }
    
    ReplacementPolicy getReplacementPolicy() {
        return this.replacementPolicy;
    }
    
    private CacheEntry getEntry(final Object key) {
        CacheEntry cacheEntry = this.cache.get(key);
        while (true) {
            if (cacheEntry != null) {
                cacheEntry.lock();
                cacheEntry.waitUntilIdentityIsSet();
                if (cacheEntry.isValid()) {
                    return cacheEntry;
                }
                cacheEntry.unlock();
                cacheEntry = this.cache.get(key);
            }
            else {
                final CacheEntry value = new CacheEntry();
                value.lock();
                final CacheEntry cacheEntry2 = this.cache.putIfAbsent(key, value);
                if (cacheEntry2 == null) {
                    return value;
                }
                cacheEntry = cacheEntry2;
            }
        }
    }
    
    private void removeEntry(final Object key) {
        final CacheEntry cacheEntry = this.cache.remove(key);
        final Cacheable cacheable = cacheEntry.getCacheable();
        if (cacheable != null && cacheable.getIdentity() != null) {
            cacheable.clearIdentity();
        }
        cacheEntry.free();
    }
    
    void evictEntry(final Object key) {
        final CacheEntry cacheEntry = this.cache.remove(key);
        cacheEntry.getCacheable().clearIdentity();
        cacheEntry.setCacheable(null);
    }
    
    private Cacheable insertIntoFreeSlot(final Object o, final CacheEntry cacheEntry) throws StandardException {
        try {
            this.replacementPolicy.insertEntry(cacheEntry);
        }
        catch (StandardException ex) {
            this.removeEntry(o);
            throw ex;
        }
        Cacheable cacheable = cacheEntry.getCacheable();
        if (cacheable == null) {
            cacheable = this.holderFactory.newCacheable(this);
        }
        cacheEntry.keep(true);
        return cacheable;
    }
    
    private void settingIdentityComplete(final Object o, final CacheEntry cacheEntry, final Cacheable cacheable) {
        cacheEntry.lock();
        try {
            cacheEntry.settingIdentityComplete();
            if (cacheable != null) {
                cacheEntry.setCacheable(cacheable);
            }
            else {
                this.removeEntry(o);
            }
        }
        finally {
            cacheEntry.unlock();
        }
    }
    
    public Cacheable find(final Object identity) throws StandardException {
        if (this.stopped) {
            return null;
        }
        final CacheEntry entry = this.getEntry(identity);
        Cacheable cacheable;
        try {
            cacheable = entry.getCacheable();
            if (cacheable != null) {
                entry.keep(true);
                return cacheable;
            }
            cacheable = this.insertIntoFreeSlot(identity, entry);
        }
        finally {
            entry.unlock();
        }
        Cacheable setIdentity = null;
        try {
            setIdentity = cacheable.setIdentity(identity);
        }
        finally {
            this.settingIdentityComplete(identity, entry, setIdentity);
        }
        return setIdentity;
    }
    
    public Cacheable findCached(final Object key) throws StandardException {
        if (this.stopped) {
            return null;
        }
        final CacheEntry cacheEntry = this.cache.get(key);
        if (cacheEntry == null) {
            return null;
        }
        cacheEntry.lock();
        try {
            cacheEntry.waitUntilIdentityIsSet();
            final Cacheable cacheable = cacheEntry.getCacheable();
            if (cacheable != null) {
                cacheEntry.keep(true);
            }
            return cacheable;
        }
        finally {
            cacheEntry.unlock();
        }
    }
    
    public Cacheable create(final Object key, final Object o) throws StandardException {
        if (this.stopped) {
            return null;
        }
        final CacheEntry value = new CacheEntry();
        value.lock();
        if (this.cache.putIfAbsent(key, value) != null) {
            throw StandardException.newException("XBCA0.S", this.name, key);
        }
        Cacheable insertIntoFreeSlot;
        try {
            insertIntoFreeSlot = this.insertIntoFreeSlot(key, value);
        }
        finally {
            value.unlock();
        }
        Cacheable identity = null;
        try {
            identity = insertIntoFreeSlot.createIdentity(key, o);
        }
        finally {
            this.settingIdentityComplete(key, value, identity);
        }
        return identity;
    }
    
    public void release(final Cacheable cacheable) {
        final CacheEntry cacheEntry = this.cache.get(cacheable.getIdentity());
        cacheEntry.lock();
        try {
            cacheEntry.unkeep();
        }
        finally {
            cacheEntry.unlock();
        }
    }
    
    public void remove(final Cacheable cacheable) throws StandardException {
        final Object identity = cacheable.getIdentity();
        final CacheEntry cacheEntry = this.cache.get(identity);
        cacheEntry.lock();
        try {
            cacheEntry.unkeepForRemove();
            cacheable.clean(true);
            this.removeEntry(identity);
        }
        finally {
            cacheEntry.unlock();
        }
    }
    
    public void cleanAll() throws StandardException {
        this.cleanCache(null);
    }
    
    public void clean(final Matchable matchable) throws StandardException {
        this.cleanCache(matchable);
    }
    
    private void cleanCache(final Matchable matchable) throws StandardException {
        for (final CacheEntry cacheEntry : this.cache.values()) {
            cacheEntry.lock();
            Cacheable cacheable2;
            try {
                if (!cacheEntry.isValid()) {
                    continue;
                }
                final Cacheable cacheable = cacheEntry.getCacheable();
                if (matchable != null && !matchable.match(cacheable.getIdentity())) {
                    continue;
                }
                if (!cacheable.isDirty()) {
                    continue;
                }
                cacheEntry.keep(false);
                cacheable2 = cacheable;
            }
            finally {
                cacheEntry.unlock();
            }
            this.cleanAndUnkeepEntry(cacheEntry, cacheable2);
        }
    }
    
    void cleanEntry(final CacheEntry cacheEntry) throws StandardException {
        cacheEntry.lock();
        Cacheable cacheable;
        try {
            cacheable = cacheEntry.getCacheable();
            if (cacheable == null) {
                return;
            }
            cacheEntry.keep(false);
        }
        finally {
            cacheEntry.unlock();
        }
        this.cleanAndUnkeepEntry(cacheEntry, cacheable);
    }
    
    void cleanAndUnkeepEntry(final CacheEntry cacheEntry, final Cacheable cacheable) throws StandardException {
        try {
            cacheable.clean(false);
        }
        finally {
            cacheEntry.lock();
            try {
                cacheEntry.unkeep();
            }
            finally {
                cacheEntry.unlock();
            }
        }
    }
    
    public void ageOut() {
        for (final CacheEntry cacheEntry : this.cache.values()) {
            cacheEntry.lock();
            try {
                if (cacheEntry.isKept()) {
                    continue;
                }
                final Cacheable cacheable = cacheEntry.getCacheable();
                if (cacheable == null || cacheable.isDirty()) {
                    continue;
                }
                this.removeEntry(cacheable.getIdentity());
            }
            finally {
                cacheEntry.unlock();
            }
        }
    }
    
    public void shutdown() throws StandardException {
        this.stopped = true;
        this.cleanAll();
        this.ageOut();
        if (this.cleaner != null) {
            this.cleaner.unsubscribe();
        }
    }
    
    public void useDaemonService(final DaemonService daemonService) {
        if (this.cleaner != null) {
            this.cleaner.unsubscribe();
        }
        this.cleaner = new BackgroundCleaner(this, daemonService, Math.max(this.maxSize / 10, 1));
    }
    
    BackgroundCleaner getBackgroundCleaner() {
        return this.cleaner;
    }
    
    public boolean discard(final Matchable matchable) {
        boolean b = true;
        for (final CacheEntry cacheEntry : this.cache.values()) {
            cacheEntry.lock();
            try {
                final Cacheable cacheable = cacheEntry.getCacheable();
                if (cacheable == null) {
                    continue;
                }
                if (matchable != null && !matchable.match(cacheable.getIdentity())) {
                    continue;
                }
                if (cacheEntry.isKept()) {
                    b = false;
                }
                else {
                    this.removeEntry(cacheable.getIdentity());
                }
            }
            finally {
                cacheEntry.unlock();
            }
        }
        return b;
    }
    
    public Collection<Cacheable> values() {
        final ArrayList<Cacheable> list = new ArrayList<Cacheable>();
        for (final CacheEntry cacheEntry : this.cache.values()) {
            cacheEntry.lock();
            try {
                final Cacheable cacheable = cacheEntry.getCacheable();
                if (cacheable == null) {
                    continue;
                }
                list.add(cacheable);
            }
            finally {
                cacheEntry.unlock();
            }
        }
        return list;
    }
}
