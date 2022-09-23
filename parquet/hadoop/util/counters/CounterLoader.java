// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.util.counters;

public interface CounterLoader
{
    ICounter getCounterByNameAndFlag(final String p0, final String p1, final String p2);
}
