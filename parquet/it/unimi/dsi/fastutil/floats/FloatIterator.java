// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.floats;

import java.util.Iterator;

public interface FloatIterator extends Iterator<Float>
{
    float nextFloat();
    
    int skip(final int p0);
}
