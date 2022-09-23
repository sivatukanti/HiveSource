// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.doubles;

import java.util.Set;

public interface DoubleSet extends DoubleCollection, Set<Double>
{
    DoubleIterator iterator();
    
    boolean remove(final double p0);
}
