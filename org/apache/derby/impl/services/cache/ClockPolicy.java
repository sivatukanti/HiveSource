// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.cache;

import org.apache.derby.iapi.services.cache.Cacheable;
import org.apache.derby.iapi.error.StandardException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;

final class ClockPolicy implements ReplacementPolicy
{
    private static final int MIN_ITEMS_TO_CHECK = 20;
    private static final float MAX_ROTATION = 0.2f;
    private static final float PART_OF_CLOCK_FOR_SHRINK = 0.1f;
    private final ConcurrentCache cacheManager;
    private final int maxSize;
    private final ArrayList<Holder> clock;
    private int hand;
    private final AtomicInteger freeEntries;
    private final AtomicBoolean isShrinking;
    
    ClockPolicy(final ConcurrentCache cacheManager, final int initialCapacity, final int maxSize) {
        this.freeEntries = new AtomicInteger();
        this.isShrinking = new AtomicBoolean();
        this.cacheManager = cacheManager;
        this.maxSize = maxSize;
        this.clock = new ArrayList<Holder>(initialCapacity);
    }
    
    public void insertEntry(final CacheEntry cacheEntry) throws StandardException {
        final int size;
        synchronized (this.clock) {
            size = this.clock.size();
            if (size < this.maxSize && this.freeEntries.get() == 0) {
                this.clock.add(new Holder(cacheEntry));
                return;
            }
        }
        if (size > this.maxSize) {
            final BackgroundCleaner backgroundCleaner = this.cacheManager.getBackgroundCleaner();
            if (backgroundCleaner != null) {
                backgroundCleaner.scheduleShrink();
            }
            else {
                this.doShrink();
            }
        }
        if (this.rotateClock(cacheEntry, size >= this.maxSize) == null) {
            synchronized (this.clock) {
                this.clock.add(new Holder(cacheEntry));
            }
        }
    }
    
    private Holder moveHand() {
        synchronized (this.clock) {
            if (this.clock.isEmpty()) {
                return null;
            }
            if (this.hand >= this.clock.size()) {
                this.hand = 0;
            }
            return this.clock.get(this.hand++);
        }
    }
    
    private Holder rotateClock(final CacheEntry cacheEntry, final boolean b) throws StandardException {
        int max = 0;
        if (b) {
            synchronized (this.clock) {
                max = Math.max(20, (int)(this.clock.size() * 0.2f));
            }
        }
        while (max-- > 0 || this.freeEntries.get() > 0) {
            final Holder moveHand = this.moveHand();
            if (moveHand == null) {
                return null;
            }
            final CacheEntry entry = moveHand.getEntry();
            if (entry == null) {
                if (moveHand.takeIfFree(cacheEntry)) {
                    return moveHand;
                }
                continue;
            }
            else {
                if (!b) {
                    continue;
                }
                entry.lock();
                Cacheable cacheable2;
                try {
                    if (!this.isEvictable(entry, moveHand, true)) {
                        continue;
                    }
                    final Cacheable cacheable = entry.getCacheable();
                    if (!cacheable.isDirty()) {
                        moveHand.switchEntry(cacheEntry);
                        this.cacheManager.evictEntry(cacheable.getIdentity());
                        return moveHand;
                    }
                    final BackgroundCleaner backgroundCleaner = this.cacheManager.getBackgroundCleaner();
                    if (backgroundCleaner != null && backgroundCleaner.scheduleClean(entry)) {
                        continue;
                    }
                    entry.keep(false);
                    cacheable2 = cacheable;
                }
                finally {
                    entry.unlock();
                }
                this.cacheManager.cleanAndUnkeepEntry(entry, cacheable2);
            }
        }
        return null;
    }
    
    private boolean isEvictable(final CacheEntry cacheEntry, final Holder holder, final boolean b) {
        if (holder.getEntry() != cacheEntry) {
            return false;
        }
        if (cacheEntry.isKept()) {
            return false;
        }
        if (holder.recentlyUsed) {
            if (b) {
                holder.recentlyUsed = false;
            }
            return false;
        }
        return true;
    }
    
    private void removeHolder(final int index, final Holder holder) {
        synchronized (this.clock) {
            final Holder holder2 = this.clock.remove(index);
        }
    }
    
    public void doShrink() {
        if (this.isShrinking.compareAndSet(false, true)) {
            try {
                this.shrinkMe();
            }
            finally {
                this.isShrinking.set(false);
            }
        }
    }
    
    private void shrinkMe() {
        int max = Math.max(1, (int)(this.maxSize * 0.1f));
        int hand;
        synchronized (this.clock) {
            hand = this.hand;
        }
        while (max-- > 0) {
            final int size;
            final Holder holder;
            synchronized (this.clock) {
                size = this.clock.size();
                if (hand >= size) {
                    hand = 0;
                }
                holder = this.clock.get(hand);
            }
            final int n = hand;
            ++hand;
            if (size <= this.maxSize) {
                break;
            }
            final CacheEntry entry = holder.getEntry();
            if (entry == null) {
                if (!holder.evictIfFree()) {
                    continue;
                }
                this.removeHolder(n, holder);
                hand = n;
            }
            else {
                entry.lock();
                try {
                    if (!this.isEvictable(entry, holder, false)) {
                        continue;
                    }
                    final Cacheable cacheable = entry.getCacheable();
                    if (cacheable.isDirty()) {
                        continue;
                    }
                    holder.setEvicted();
                    this.cacheManager.evictEntry(cacheable.getIdentity());
                    this.removeHolder(n, holder);
                    hand = n;
                }
                finally {
                    entry.unlock();
                }
            }
        }
    }
    
    private class Holder implements Callback
    {
        boolean recentlyUsed;
        private CacheEntry entry;
        private Cacheable freedCacheable;
        private boolean evicted;
        
        Holder(final CacheEntry entry) {
            (this.entry = entry).setCallback(this);
        }
        
        public void access() {
            this.recentlyUsed = true;
        }
        
        public synchronized void free() {
            this.freedCacheable = this.entry.getCacheable();
            this.entry = null;
            this.recentlyUsed = false;
            ClockPolicy.this.freeEntries.incrementAndGet();
        }
        
        synchronized boolean takeIfFree(final CacheEntry entry) {
            if (this.entry == null && !this.evicted) {
                ClockPolicy.this.freeEntries.decrementAndGet();
                entry.setCacheable(this.freedCacheable);
                entry.setCallback(this);
                this.entry = entry;
                this.freedCacheable = null;
                return true;
            }
            return false;
        }
        
        synchronized CacheEntry getEntry() {
            return this.entry;
        }
        
        synchronized void switchEntry(final CacheEntry entry) {
            entry.setCallback(this);
            entry.setCacheable(this.entry.getCacheable());
            this.entry = entry;
        }
        
        synchronized boolean evictIfFree() {
            if (this.entry == null && !this.evicted) {
                ClockPolicy.this.freeEntries.decrementAndGet();
                return this.evicted = true;
            }
            return false;
        }
        
        synchronized void setEvicted() {
            this.evicted = true;
            this.entry = null;
        }
        
        synchronized boolean isEvicted() {
            return this.evicted;
        }
    }
}
