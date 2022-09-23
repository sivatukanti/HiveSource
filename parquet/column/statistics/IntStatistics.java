// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.statistics;

import parquet.bytes.BytesUtils;

public class IntStatistics extends Statistics<Integer>
{
    private int max;
    private int min;
    
    @Override
    public void updateStats(final int value) {
        if (!this.hasNonNullValue()) {
            this.initializeStats(value, value);
        }
        else {
            this.updateStats(value, value);
        }
    }
    
    public void mergeStatisticsMinMax(final Statistics stats) {
        final IntStatistics intStats = (IntStatistics)stats;
        if (!this.hasNonNullValue()) {
            this.initializeStats(intStats.getMin(), intStats.getMax());
        }
        else {
            this.updateStats(intStats.getMin(), intStats.getMax());
        }
    }
    
    @Override
    public void setMinMaxFromBytes(final byte[] minBytes, final byte[] maxBytes) {
        this.max = BytesUtils.bytesToInt(maxBytes);
        this.min = BytesUtils.bytesToInt(minBytes);
        this.markAsNotEmpty();
    }
    
    @Override
    public byte[] getMaxBytes() {
        return BytesUtils.intToBytes(this.max);
    }
    
    @Override
    public byte[] getMinBytes() {
        return BytesUtils.intToBytes(this.min);
    }
    
    @Override
    public String toString() {
        if (this.hasNonNullValue()) {
            return String.format("min: %d, max: %d, num_nulls: %d", this.min, this.max, this.getNumNulls());
        }
        if (!this.isEmpty()) {
            return String.format("num_nulls: %d, min/max is not defined", this.getNumNulls());
        }
        return "no stats for this column";
    }
    
    public void updateStats(final int min_value, final int max_value) {
        if (min_value < this.min) {
            this.min = min_value;
        }
        if (max_value > this.max) {
            this.max = max_value;
        }
    }
    
    public void initializeStats(final int min_value, final int max_value) {
        this.min = min_value;
        this.max = max_value;
        this.markAsNotEmpty();
    }
    
    @Override
    public Integer genericGetMin() {
        return this.min;
    }
    
    @Override
    public Integer genericGetMax() {
        return this.max;
    }
    
    public int getMax() {
        return this.max;
    }
    
    public int getMin() {
        return this.min;
    }
    
    public void setMinMax(final int min, final int max) {
        this.max = max;
        this.min = min;
        this.markAsNotEmpty();
    }
}
