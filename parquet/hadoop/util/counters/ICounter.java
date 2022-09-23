// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.util.counters;

public interface ICounter
{
    void increment(final long p0);
    
    long getCount();
}
