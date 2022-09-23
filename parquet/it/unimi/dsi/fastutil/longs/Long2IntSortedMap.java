// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.longs;

import parquet.it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import parquet.it.unimi.dsi.fastutil.ints.IntCollection;
import java.util.Map;
import parquet.it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.SortedMap;

public interface Long2IntSortedMap extends Long2IntMap, SortedMap<Long, Integer>
{
    ObjectSortedSet<Map.Entry<Long, Integer>> entrySet();
    
    ObjectSortedSet<Long2IntMap.Entry> long2IntEntrySet();
    
    LongSortedSet keySet();
    
    IntCollection values();
    
    LongComparator comparator();
    
    Long2IntSortedMap subMap(final Long p0, final Long p1);
    
    Long2IntSortedMap headMap(final Long p0);
    
    Long2IntSortedMap tailMap(final Long p0);
    
    Long2IntSortedMap subMap(final long p0, final long p1);
    
    Long2IntSortedMap headMap(final long p0);
    
    Long2IntSortedMap tailMap(final long p0);
    
    long firstLongKey();
    
    long lastLongKey();
    
    public interface FastSortedEntrySet extends ObjectSortedSet<Long2IntMap.Entry>, FastEntrySet
    {
        ObjectBidirectionalIterator<Long2IntMap.Entry> fastIterator(final Long2IntMap.Entry p0);
    }
}
