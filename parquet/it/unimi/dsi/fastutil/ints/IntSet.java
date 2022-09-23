// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.ints;

import java.util.Set;

public interface IntSet extends IntCollection, Set<Integer>
{
    IntIterator iterator();
    
    boolean remove(final int p0);
}
