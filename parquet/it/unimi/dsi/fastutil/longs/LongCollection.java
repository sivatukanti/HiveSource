// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.longs;

import java.util.Collection;

public interface LongCollection extends Collection<Long>, LongIterable
{
    LongIterator iterator();
    
    @Deprecated
    LongIterator longIterator();
    
     <T> T[] toArray(final T[] p0);
    
    boolean contains(final long p0);
    
    long[] toLongArray();
    
    long[] toLongArray(final long[] p0);
    
    long[] toArray(final long[] p0);
    
    boolean add(final long p0);
    
    boolean rem(final long p0);
    
    boolean addAll(final LongCollection p0);
    
    boolean containsAll(final LongCollection p0);
    
    boolean removeAll(final LongCollection p0);
    
    boolean retainAll(final LongCollection p0);
}
