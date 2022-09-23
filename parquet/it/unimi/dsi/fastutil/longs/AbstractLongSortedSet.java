// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.longs;

import java.util.SortedSet;
import java.util.Iterator;

public abstract class AbstractLongSortedSet extends AbstractLongSet implements LongSortedSet
{
    protected AbstractLongSortedSet() {
    }
    
    @Override
    public LongSortedSet headSet(final Long to) {
        return this.headSet((long)to);
    }
    
    @Override
    public LongSortedSet tailSet(final Long from) {
        return this.tailSet((long)from);
    }
    
    @Override
    public LongSortedSet subSet(final Long from, final Long to) {
        return this.subSet((long)from, (long)to);
    }
    
    @Override
    public Long first() {
        return this.firstLong();
    }
    
    @Override
    public Long last() {
        return this.lastLong();
    }
    
    @Deprecated
    @Override
    public LongBidirectionalIterator longIterator() {
        return this.iterator();
    }
    
    @Override
    public abstract LongBidirectionalIterator iterator();
}
