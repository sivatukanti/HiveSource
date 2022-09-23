// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.statistics;

import parquet.bytes.BytesUtils;

public class LongStatistics extends Statistics<Long>
{
    private long max;
    private long min;
    
    @Override
    public void updateStats(final long value) {
        if (!this.hasNonNullValue()) {
            this.initializeStats(value, value);
        }
        else {
            this.updateStats(value, value);
        }
    }
    
    public void mergeStatisticsMinMax(final Statistics stats) {
        final LongStatistics longStats = (LongStatistics)stats;
        if (!this.hasNonNullValue()) {
            this.initializeStats(longStats.getMin(), longStats.getMax());
        }
        else {
            this.updateStats(longStats.getMin(), longStats.getMax());
        }
    }
    
    @Override
    public void setMinMaxFromBytes(final byte[] minBytes, final byte[] maxBytes) {
        this.max = BytesUtils.bytesToLong(maxBytes);
        this.min = BytesUtils.bytesToLong(minBytes);
        this.markAsNotEmpty();
    }
    
    @Override
    public byte[] getMaxBytes() {
        return BytesUtils.longToBytes(this.max);
    }
    
    @Override
    public byte[] getMinBytes() {
        return BytesUtils.longToBytes(this.min);
    }
    
    @Override
    public String toString() {
        if (this.hasNonNullValue()) {
            return String.format("min: %d, max: %d, num_nulls: %d", this.min, this.max, this.getNumNulls());
        }
        if (!this.isEmpty()) {
            return String.format("num_nulls: %d, min/max not defined", this.getNumNulls());
        }
        return "no stats for this column";
    }
    
    public void updateStats(final long min_value, final long max_value) {
        if (min_value < this.min) {
            this.min = min_value;
        }
        if (max_value > this.max) {
            this.max = max_value;
        }
    }
    
    public void initializeStats(final long min_value, final long max_value) {
        this.min = min_value;
        this.max = max_value;
        this.markAsNotEmpty();
    }
    
    @Override
    public Long genericGetMin() {
        return this.min;
    }
    
    @Override
    public Long genericGetMax() {
        return this.max;
    }
    
    public long getMax() {
        return this.max;
    }
    
    public long getMin() {
        return this.min;
    }
    
    public void setMinMax(final long min, final long max) {
        this.max = max;
        this.min = min;
        this.markAsNotEmpty();
    }
}
