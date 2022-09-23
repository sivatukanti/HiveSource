// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.NoSuchElementException;
import java.util.Map;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import java.util.TreeMap;
import java.util.NavigableMap;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
public enum GlobalStorageStatistics
{
    INSTANCE;
    
    private final NavigableMap<String, StorageStatistics> map;
    
    private GlobalStorageStatistics() {
        this.map = new TreeMap<String, StorageStatistics>();
    }
    
    public synchronized StorageStatistics get(final String name) {
        return (name == null) ? null : this.map.get(name);
    }
    
    public synchronized StorageStatistics put(final String name, final StorageStatisticsProvider provider) {
        Preconditions.checkNotNull(name, (Object)"Storage statistics can not have a null name!");
        StorageStatistics stats = this.map.get(name);
        if (stats != null) {
            return stats;
        }
        stats = provider.provide();
        if (stats == null) {
            throw new RuntimeException("StorageStatisticsProvider for " + name + " should not provide a null StorageStatistics object.");
        }
        if (!stats.getName().equals(name)) {
            throw new RuntimeException("StorageStatisticsProvider for " + name + " provided a StorageStatistics object for " + stats.getName() + " instead.");
        }
        this.map.put(name, stats);
        return stats;
    }
    
    public synchronized void reset() {
        for (final StorageStatistics statistics : this.map.values()) {
            statistics.reset();
        }
    }
    
    public synchronized Iterator<StorageStatistics> iterator() {
        final Map.Entry<String, StorageStatistics> first = this.map.firstEntry();
        return new StorageIterator((first == null) ? null : first.getValue());
    }
    
    private class StorageIterator implements Iterator<StorageStatistics>
    {
        private StorageStatistics next;
        
        StorageIterator(final StorageStatistics first) {
            this.next = null;
            this.next = first;
        }
        
        @Override
        public boolean hasNext() {
            return this.next != null;
        }
        
        @Override
        public StorageStatistics next() {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            synchronized (GlobalStorageStatistics.this) {
                final StorageStatistics cur = this.next;
                final Map.Entry<String, StorageStatistics> nextEntry = GlobalStorageStatistics.this.map.higherEntry(cur.getName());
                this.next = ((nextEntry == null) ? null : nextEntry.getValue());
                return cur;
            }
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    public interface StorageStatisticsProvider
    {
        StorageStatistics provide();
    }
}
