// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.statistics;

import parquet.bytes.BytesUtils;

public class BooleanStatistics extends Statistics<Boolean>
{
    private boolean max;
    private boolean min;
    
    @Override
    public void updateStats(final boolean value) {
        if (!this.hasNonNullValue()) {
            this.initializeStats(value, value);
        }
        else {
            this.updateStats(value, value);
        }
    }
    
    public void mergeStatisticsMinMax(final Statistics stats) {
        final BooleanStatistics boolStats = (BooleanStatistics)stats;
        if (!this.hasNonNullValue()) {
            this.initializeStats(boolStats.getMin(), boolStats.getMax());
        }
        else {
            this.updateStats(boolStats.getMin(), boolStats.getMax());
        }
    }
    
    @Override
    public void setMinMaxFromBytes(final byte[] minBytes, final byte[] maxBytes) {
        this.max = BytesUtils.bytesToBool(maxBytes);
        this.min = BytesUtils.bytesToBool(minBytes);
        this.markAsNotEmpty();
    }
    
    @Override
    public byte[] getMaxBytes() {
        return BytesUtils.booleanToBytes(this.max);
    }
    
    @Override
    public byte[] getMinBytes() {
        return BytesUtils.booleanToBytes(this.min);
    }
    
    @Override
    public String toString() {
        if (this.hasNonNullValue()) {
            return String.format("min: %b, max: %b, num_nulls: %d", this.min, this.max, this.getNumNulls());
        }
        if (!this.isEmpty()) {
            return String.format("num_nulls: %d, min/max not defined", this.getNumNulls());
        }
        return "no stats for this column";
    }
    
    public void updateStats(final boolean min_value, final boolean max_value) {
        if (this.min && !min_value) {
            this.min = min_value;
        }
        if (!this.max && max_value) {
            this.max = max_value;
        }
    }
    
    public void initializeStats(final boolean min_value, final boolean max_value) {
        this.min = min_value;
        this.max = max_value;
        this.markAsNotEmpty();
    }
    
    @Override
    public Boolean genericGetMin() {
        return this.min;
    }
    
    @Override
    public Boolean genericGetMax() {
        return this.max;
    }
    
    public boolean getMax() {
        return this.max;
    }
    
    public boolean getMin() {
        return this.min;
    }
    
    public void setMinMax(final boolean min, final boolean max) {
        this.max = max;
        this.min = min;
        this.markAsNotEmpty();
    }
}
