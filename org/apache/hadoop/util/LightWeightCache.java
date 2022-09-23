// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.Iterator;
import org.apache.hadoop.HadoopIllegalArgumentException;
import com.google.common.base.Preconditions;
import com.google.common.annotations.VisibleForTesting;
import java.util.PriorityQueue;
import java.util.Comparator;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class LightWeightCache<K, E extends K> extends LightWeightGSet<K, E>
{
    private static final int EVICTION_LIMIT = 65536;
    private static final Comparator<Entry> expirationTimeComparator;
    private final PriorityQueue<Entry> queue;
    private final long creationExpirationPeriod;
    private final long accessExpirationPeriod;
    private final int sizeLimit;
    private final Timer timer;
    
    private static int updateRecommendedLength(final int recommendedLength, final int sizeLimit) {
        return (sizeLimit > 0 && sizeLimit < recommendedLength) ? (sizeLimit / 4 * 3) : recommendedLength;
    }
    
    public LightWeightCache(final int recommendedLength, final int sizeLimit, final long creationExpirationPeriod, final long accessExpirationPeriod) {
        this(recommendedLength, sizeLimit, creationExpirationPeriod, accessExpirationPeriod, new Timer());
    }
    
    @VisibleForTesting
    LightWeightCache(final int recommendedLength, final int sizeLimit, final long creationExpirationPeriod, final long accessExpirationPeriod, final Timer timer) {
        super(updateRecommendedLength(recommendedLength, sizeLimit));
        this.sizeLimit = sizeLimit;
        if (creationExpirationPeriod <= 0L) {
            throw new IllegalArgumentException("creationExpirationPeriod = " + creationExpirationPeriod + " <= 0");
        }
        this.creationExpirationPeriod = creationExpirationPeriod;
        if (accessExpirationPeriod < 0L) {
            throw new IllegalArgumentException("accessExpirationPeriod = " + accessExpirationPeriod + " < 0");
        }
        this.accessExpirationPeriod = accessExpirationPeriod;
        this.queue = new PriorityQueue<Entry>((sizeLimit > 0) ? (sizeLimit + 1) : 1024, LightWeightCache.expirationTimeComparator);
        this.timer = timer;
    }
    
    void setExpirationTime(final Entry e, final long expirationPeriod) {
        e.setExpirationTime(this.timer.monotonicNowNanos() + expirationPeriod);
    }
    
    boolean isExpired(final Entry e, final long now) {
        return now > e.getExpirationTime();
    }
    
    private E evict() {
        final E polled = (E)this.queue.poll();
        final E removed = super.remove(polled);
        Preconditions.checkState(removed == polled);
        return polled;
    }
    
    private void evictExpiredEntries() {
        final long now = this.timer.monotonicNowNanos();
        for (int i = 0; i < 65536; ++i) {
            final Entry peeked = this.queue.peek();
            if (peeked == null || !this.isExpired(peeked, now)) {
                return;
            }
            final E evicted = this.evict();
            Preconditions.checkState(evicted == peeked);
        }
    }
    
    private void evictEntries() {
        if (this.sizeLimit > 0) {
            for (int i = this.size(); i > this.sizeLimit; --i) {
                this.evict();
            }
        }
    }
    
    @Override
    public E get(final K key) {
        final E entry = super.get(key);
        if (entry != null && this.accessExpirationPeriod > 0L) {
            final Entry existing = (Entry)entry;
            Preconditions.checkState(this.queue.remove(existing));
            this.setExpirationTime(existing, this.accessExpirationPeriod);
            this.queue.offer(existing);
        }
        return entry;
    }
    
    @Override
    public E put(final E entry) {
        if (!(entry instanceof Entry)) {
            throw new HadoopIllegalArgumentException("!(entry instanceof Entry), entry.getClass()=" + entry.getClass());
        }
        this.evictExpiredEntries();
        final E existing = super.put(entry);
        if (existing != null) {
            this.queue.remove(existing);
        }
        final Entry e = (Entry)entry;
        this.setExpirationTime(e, this.creationExpirationPeriod);
        this.queue.offer(e);
        this.evictEntries();
        return existing;
    }
    
    @Override
    public E remove(final K key) {
        this.evictExpiredEntries();
        final E removed = super.remove(key);
        if (removed != null) {
            Preconditions.checkState(this.queue.remove(removed));
        }
        return removed;
    }
    
    @Override
    public Iterator<E> iterator() {
        final Iterator<E> iter = super.iterator();
        return new Iterator<E>() {
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }
            
            @Override
            public E next() {
                return iter.next();
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove via iterator is not supported for LightWeightCache");
            }
        };
    }
    
    static {
        expirationTimeComparator = new Comparator<Entry>() {
            @Override
            public int compare(final Entry left, final Entry right) {
                final long l = left.getExpirationTime();
                final long r = right.getExpirationTime();
                return (l > r) ? 1 : ((l < r) ? -1 : 0);
            }
        };
    }
    
    public interface Entry extends LinkedElement
    {
        void setExpirationTime(final long p0);
        
        long getExpirationTime();
    }
}
