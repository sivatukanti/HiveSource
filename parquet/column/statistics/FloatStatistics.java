// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.statistics;

import parquet.bytes.BytesUtils;

public class FloatStatistics extends Statistics<Float>
{
    private float max;
    private float min;
    
    @Override
    public void updateStats(final float value) {
        if (!this.hasNonNullValue()) {
            this.initializeStats(value, value);
        }
        else {
            this.updateStats(value, value);
        }
    }
    
    public void mergeStatisticsMinMax(final Statistics stats) {
        final FloatStatistics floatStats = (FloatStatistics)stats;
        if (!this.hasNonNullValue()) {
            this.initializeStats(floatStats.getMin(), floatStats.getMax());
        }
        else {
            this.updateStats(floatStats.getMin(), floatStats.getMax());
        }
    }
    
    @Override
    public void setMinMaxFromBytes(final byte[] minBytes, final byte[] maxBytes) {
        this.max = Float.intBitsToFloat(BytesUtils.bytesToInt(maxBytes));
        this.min = Float.intBitsToFloat(BytesUtils.bytesToInt(minBytes));
        this.markAsNotEmpty();
    }
    
    @Override
    public byte[] getMaxBytes() {
        return BytesUtils.intToBytes(Float.floatToIntBits(this.max));
    }
    
    @Override
    public byte[] getMinBytes() {
        return BytesUtils.intToBytes(Float.floatToIntBits(this.min));
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
    
    public void updateStats(final float min_value, final float max_value) {
        if (min_value < this.min) {
            this.min = min_value;
        }
        if (max_value > this.max) {
            this.max = max_value;
        }
    }
    
    public void initializeStats(final float min_value, final float max_value) {
        this.min = min_value;
        this.max = max_value;
        this.markAsNotEmpty();
    }
    
    @Override
    public Float genericGetMin() {
        return this.min;
    }
    
    @Override
    public Float genericGetMax() {
        return this.max;
    }
    
    public float getMax() {
        return this.max;
    }
    
    public float getMin() {
        return this.min;
    }
    
    public void setMinMax(final float min, final float max) {
        this.max = max;
        this.min = min;
        this.markAsNotEmpty();
    }
}
