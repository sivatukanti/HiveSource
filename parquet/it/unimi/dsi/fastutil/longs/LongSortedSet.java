// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.longs;

import java.util.SortedSet;

public interface LongSortedSet extends LongSet, SortedSet<Long>
{
    LongBidirectionalIterator iterator(final long p0);
    
    @Deprecated
    LongBidirectionalIterator longIterator();
    
    LongBidirectionalIterator iterator();
    
    LongSortedSet subSet(final Long p0, final Long p1);
    
    LongSortedSet headSet(final Long p0);
    
    LongSortedSet tailSet(final Long p0);
    
    LongComparator comparator();
    
    LongSortedSet subSet(final long p0, final long p1);
    
    LongSortedSet headSet(final long p0);
    
    LongSortedSet tailSet(final long p0);
    
    long firstLong();
    
    long lastLong();
}
