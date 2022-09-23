// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.floats;

import parquet.it.unimi.dsi.fastutil.Function;

public interface Float2IntFunction extends Function<Float, Integer>
{
    int put(final float p0, final int p1);
    
    int get(final float p0);
    
    int remove(final float p0);
    
    boolean containsKey(final float p0);
    
    void defaultReturnValue(final int p0);
    
    int defaultReturnValue();
}
