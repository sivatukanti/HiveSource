// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.longs;

import java.util.Set;

public interface LongSet extends LongCollection, Set<Long>
{
    LongIterator iterator();
    
    boolean remove(final long p0);
}
