// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.NoSuchElementException;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class UnionStorageStatistics extends StorageStatistics
{
    private final StorageStatistics[] stats;
    
    public UnionStorageStatistics(final String name, final StorageStatistics[] stats) {
        super(name);
        Preconditions.checkArgument(name != null, (Object)"The name of union storage statistics can not be null!");
        Preconditions.checkArgument(stats != null, (Object)"The stats of union storage statistics can not be null!");
        for (final StorageStatistics stat : stats) {
            Preconditions.checkArgument(stat != null, (Object)"The stats of union storage statistics can not have null element!");
        }
        this.stats = stats;
    }
    
    @Override
    public Iterator<LongStatistic> getLongStatistics() {
        return new LongStatisticIterator();
    }
    
    @Override
    public Long getLong(final String key) {
        for (final StorageStatistics stat : this.stats) {
            final Long val = stat.getLong(key);
            if (val != null) {
                return val;
            }
        }
        return null;
    }
    
    @Override
    public boolean isTracked(final String key) {
        for (final StorageStatistics stat : this.stats) {
            if (stat.isTracked(key)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void reset() {
        for (final StorageStatistics stat : this.stats) {
            stat.reset();
        }
    }
    
    private class LongStatisticIterator implements Iterator<LongStatistic>
    {
        private int statIdx;
        private Iterator<LongStatistic> cur;
        
        LongStatisticIterator() {
            this.statIdx = 0;
            this.cur = null;
        }
        
        @Override
        public boolean hasNext() {
            return this.getIter() != null;
        }
        
        private Iterator<LongStatistic> getIter() {
            while (this.cur == null || !this.cur.hasNext()) {
                if (UnionStorageStatistics.this.stats.length >= this.statIdx) {
                    return null;
                }
                this.cur = UnionStorageStatistics.this.stats[this.statIdx++].getLongStatistics();
            }
            return this.cur;
        }
        
        @Override
        public LongStatistic next() {
            final Iterator<LongStatistic> iter = this.getIter();
            if (iter == null) {
                throw new NoSuchElementException();
            }
            return iter.next();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
