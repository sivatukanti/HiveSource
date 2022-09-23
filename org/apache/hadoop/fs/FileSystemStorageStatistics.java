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
public class FileSystemStorageStatistics extends StorageStatistics
{
    private final FileSystem.Statistics stats;
    private static final String[] KEYS;
    
    private static Long fetch(final FileSystem.Statistics.StatisticsData data, final String key) {
        Preconditions.checkArgument(key != null, (Object)"The stat key of FileSystemStorageStatistics should not be null!");
        switch (key) {
            case "bytesRead": {
                return data.getBytesRead();
            }
            case "bytesWritten": {
                return data.getBytesWritten();
            }
            case "readOps": {
                return (long)(data.getReadOps() + data.getLargeReadOps());
            }
            case "largeReadOps": {
                return (long)data.getLargeReadOps();
            }
            case "writeOps": {
                return (long)data.getWriteOps();
            }
            case "bytesReadLocalHost": {
                return data.getBytesReadLocalHost();
            }
            case "bytesReadDistanceOfOneOrTwo": {
                return data.getBytesReadDistanceOfOneOrTwo();
            }
            case "bytesReadDistanceOfThreeOrFour": {
                return data.getBytesReadDistanceOfThreeOrFour();
            }
            case "bytesReadDistanceOfFiveOrLarger": {
                return data.getBytesReadDistanceOfFiveOrLarger();
            }
            case "bytesReadErasureCoded": {
                return data.getBytesReadErasureCoded();
            }
            default: {
                return null;
            }
        }
    }
    
    FileSystemStorageStatistics(final String name, final FileSystem.Statistics stats) {
        super(name);
        Preconditions.checkArgument(stats != null, (Object)"FileSystem.Statistics can not be null");
        Preconditions.checkArgument(stats.getData() != null, (Object)"FileSystem.Statistics can not have null data");
        this.stats = stats;
    }
    
    @Override
    public String getScheme() {
        return this.stats.getScheme();
    }
    
    @Override
    public Iterator<LongStatistic> getLongStatistics() {
        return new LongStatisticIterator(this.stats.getData());
    }
    
    @Override
    public Long getLong(final String key) {
        return fetch(this.stats.getData(), key);
    }
    
    @Override
    public boolean isTracked(final String key) {
        for (final String k : FileSystemStorageStatistics.KEYS) {
            if (k.equals(key)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void reset() {
        this.stats.reset();
    }
    
    static {
        KEYS = new String[] { "bytesRead", "bytesWritten", "readOps", "largeReadOps", "writeOps", "bytesReadLocalHost", "bytesReadDistanceOfOneOrTwo", "bytesReadDistanceOfThreeOrFour", "bytesReadDistanceOfFiveOrLarger", "bytesReadErasureCoded" };
    }
    
    private static class LongStatisticIterator implements Iterator<LongStatistic>
    {
        private final FileSystem.Statistics.StatisticsData data;
        private int keyIdx;
        
        LongStatisticIterator(final FileSystem.Statistics.StatisticsData data) {
            this.data = data;
            this.keyIdx = 0;
        }
        
        @Override
        public boolean hasNext() {
            return this.keyIdx < FileSystemStorageStatistics.KEYS.length;
        }
        
        @Override
        public LongStatistic next() {
            if (this.keyIdx >= FileSystemStorageStatistics.KEYS.length) {
                throw new NoSuchElementException();
            }
            final String key = FileSystemStorageStatistics.KEYS[this.keyIdx++];
            final Long val = fetch(this.data, key);
            return new LongStatistic(key, val);
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
