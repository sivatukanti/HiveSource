// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.statistics;

import parquet.bytes.BytesUtils;

public class DoubleStatistics extends Statistics<Double>
{
    private double max;
    private double min;
    
    @Override
    public void updateStats(final double value) {
        if (!this.hasNonNullValue()) {
            this.initializeStats(value, value);
        }
        else {
            this.updateStats(value, value);
        }
    }
    
    public void mergeStatisticsMinMax(final Statistics stats) {
        final DoubleStatistics doubleStats = (DoubleStatistics)stats;
        if (!this.hasNonNullValue()) {
            this.initializeStats(doubleStats.getMin(), doubleStats.getMax());
        }
        else {
            this.updateStats(doubleStats.getMin(), doubleStats.getMax());
        }
    }
    
    @Override
    public void setMinMaxFromBytes(final byte[] minBytes, final byte[] maxBytes) {
        this.max = Double.longBitsToDouble(BytesUtils.bytesToLong(maxBytes));
        this.min = Double.longBitsToDouble(BytesUtils.bytesToLong(minBytes));
        this.markAsNotEmpty();
    }
    
    @Override
    public byte[] getMaxBytes() {
        return BytesUtils.longToBytes(Double.doubleToLongBits(this.max));
    }
    
    @Override
    public byte[] getMinBytes() {
        return BytesUtils.longToBytes(Double.doubleToLongBits(this.min));
    }
    
    @Override
    public String toString() {
        if (this.hasNonNullValue()) {
            return String.format("min: %.5f, max: %.5f, num_nulls: %d", this.min, this.max, this.getNumNulls());
        }
        if (!this.isEmpty()) {
            return String.format("num_nulls: %d, min/max not defined", this.getNumNulls());
        }
        return "no stats for this column";
    }
    
    public void updateStats(final double min_value, final double max_value) {
        if (min_value < this.min) {
            this.min = min_value;
        }
        if (max_value > this.max) {
            this.max = max_value;
        }
    }
    
    public void initializeStats(final double min_value, final double max_value) {
        this.min = min_value;
        this.max = max_value;
        this.markAsNotEmpty();
    }
    
    @Override
    public Double genericGetMin() {
        return this.min;
    }
    
    @Override
    public Double genericGetMax() {
        return this.max;
    }
    
    public double getMax() {
        return this.max;
    }
    
    public double getMin() {
        return this.min;
    }
    
    public void setMinMax(final double min, final double max) {
        this.max = max;
        this.min = min;
        this.markAsNotEmpty();
    }
}
