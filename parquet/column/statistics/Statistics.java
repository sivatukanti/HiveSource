// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.statistics;

import java.util.Arrays;
import parquet.io.api.Binary;
import parquet.column.UnknownColumnTypeException;
import parquet.schema.PrimitiveType;

public abstract class Statistics<T extends Comparable<T>>
{
    private boolean hasNonNullValue;
    private long num_nulls;
    
    public Statistics() {
        this.hasNonNullValue = false;
        this.num_nulls = 0L;
    }
    
    public static Statistics getStatsBasedOnType(final PrimitiveType.PrimitiveTypeName type) {
        switch (type) {
            case INT32: {
                return new IntStatistics();
            }
            case INT64: {
                return new LongStatistics();
            }
            case FLOAT: {
                return new FloatStatistics();
            }
            case DOUBLE: {
                return new DoubleStatistics();
            }
            case BOOLEAN: {
                return new BooleanStatistics();
            }
            case BINARY: {
                return new BinaryStatistics();
            }
            case INT96: {
                return new BinaryStatistics();
            }
            case FIXED_LEN_BYTE_ARRAY: {
                return new BinaryStatistics();
            }
            default: {
                throw new UnknownColumnTypeException(type);
            }
        }
    }
    
    public void updateStats(final int value) {
        throw new UnsupportedOperationException();
    }
    
    public void updateStats(final long value) {
        throw new UnsupportedOperationException();
    }
    
    public void updateStats(final float value) {
        throw new UnsupportedOperationException();
    }
    
    public void updateStats(final double value) {
        throw new UnsupportedOperationException();
    }
    
    public void updateStats(final boolean value) {
        throw new UnsupportedOperationException();
    }
    
    public void updateStats(final Binary value) {
        throw new UnsupportedOperationException();
    }
    
    public boolean equals(final Statistics stats) {
        return Arrays.equals(stats.getMaxBytes(), this.getMaxBytes()) && Arrays.equals(stats.getMinBytes(), this.getMinBytes()) && stats.getNumNulls() == this.getNumNulls();
    }
    
    @Override
    public int hashCode() {
        return 31 * Arrays.hashCode(this.getMaxBytes()) + 17 * Arrays.hashCode(this.getMinBytes()) + Long.valueOf(this.getNumNulls()).hashCode();
    }
    
    public void mergeStatistics(final Statistics stats) {
        if (stats.isEmpty()) {
            return;
        }
        if (this.getClass() == stats.getClass()) {
            this.incrementNumNulls(stats.getNumNulls());
            if (stats.hasNonNullValue()) {
                this.mergeStatisticsMinMax(stats);
                this.markAsNotEmpty();
            }
            return;
        }
        throw new StatisticsClassException(this.getClass().toString(), stats.getClass().toString());
    }
    
    protected abstract void mergeStatisticsMinMax(final Statistics p0);
    
    public abstract void setMinMaxFromBytes(final byte[] p0, final byte[] p1);
    
    public abstract T genericGetMin();
    
    public abstract T genericGetMax();
    
    public abstract byte[] getMaxBytes();
    
    public abstract byte[] getMinBytes();
    
    @Override
    public abstract String toString();
    
    public void incrementNumNulls() {
        ++this.num_nulls;
    }
    
    public void incrementNumNulls(final long increment) {
        this.num_nulls += increment;
    }
    
    public long getNumNulls() {
        return this.num_nulls;
    }
    
    public void setNumNulls(final long nulls) {
        this.num_nulls = nulls;
    }
    
    public boolean isEmpty() {
        return !this.hasNonNullValue && this.num_nulls == 0L;
    }
    
    public boolean hasNonNullValue() {
        return this.hasNonNullValue;
    }
    
    protected void markAsNotEmpty() {
        this.hasNonNullValue = true;
    }
}
