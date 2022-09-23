// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.floats;

import parquet.it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface FloatBidirectionalIterator extends FloatIterator, ObjectBidirectionalIterator<Float>
{
    float previousFloat();
    
    int back(final int p0);
}
