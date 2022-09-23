// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values;

public interface RequiresFallback
{
    boolean shouldFallBack();
    
    boolean isCompressionSatisfying(final long p0, final long p1);
    
    void fallBackAllValuesTo(final ValuesWriter p0);
}
