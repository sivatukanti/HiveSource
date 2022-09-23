// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.statistics;

import parquet.io.api.Binary;

public class BinaryStatistics extends Statistics<Binary>
{
    private Binary max;
    private Binary min;
    
    @Override
    public void updateStats(final Binary value) {
        if (!this.hasNonNullValue()) {
            this.initializeStats(value, value);
        }
        else {
            this.updateStats(value, value);
        }
    }
    
    public void mergeStatisticsMinMax(final Statistics stats) {
        final BinaryStatistics binaryStats = (BinaryStatistics)stats;
        if (!this.hasNonNullValue()) {
            this.initializeStats(binaryStats.getMin(), binaryStats.getMax());
        }
        else {
            this.updateStats(binaryStats.getMin(), binaryStats.getMax());
        }
    }
    
    @Override
    public void setMinMaxFromBytes(final byte[] minBytes, final byte[] maxBytes) {
        this.max = Binary.fromByteArray(maxBytes);
        this.min = Binary.fromByteArray(minBytes);
        this.markAsNotEmpty();
    }
    
    @Override
    public byte[] getMaxBytes() {
        return this.max.getBytes();
    }
    
    @Override
    public byte[] getMinBytes() {
        return this.min.getBytes();
    }
    
    @Override
    public String toString() {
        if (this.hasNonNullValue()) {
            return String.format("min: %s, max: %s, num_nulls: %d", this.min.toStringUsingUTF8(), this.max.toStringUsingUTF8(), this.getNumNulls());
        }
        if (!this.isEmpty()) {
            return String.format("num_nulls: %d, min/max not defined", this.getNumNulls());
        }
        return "no stats for this column";
    }
    
    public void updateStats(final Binary min_value, final Binary max_value) {
        if (this.min.compareTo(min_value) > 0) {
            this.min = min_value;
        }
        if (this.max.compareTo(max_value) < 0) {
            this.max = max_value;
        }
    }
    
    public void initializeStats(final Binary min_value, final Binary max_value) {
        this.min = min_value;
        this.max = max_value;
        this.markAsNotEmpty();
    }
    
    @Override
    public Binary genericGetMin() {
        return this.min;
    }
    
    @Override
    public Binary genericGetMax() {
        return this.max;
    }
    
    public Binary getMax() {
        return this.max;
    }
    
    public Binary getMin() {
        return this.min;
    }
    
    public void setMinMax(final Binary min, final Binary max) {
        this.max = max;
        this.min = min;
        this.markAsNotEmpty();
    }
}
