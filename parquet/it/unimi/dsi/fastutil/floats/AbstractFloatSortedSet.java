// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.floats;

import java.util.SortedSet;
import java.util.Iterator;

public abstract class AbstractFloatSortedSet extends AbstractFloatSet implements FloatSortedSet
{
    protected AbstractFloatSortedSet() {
    }
    
    @Override
    public FloatSortedSet headSet(final Float to) {
        return this.headSet((float)to);
    }
    
    @Override
    public FloatSortedSet tailSet(final Float from) {
        return this.tailSet((float)from);
    }
    
    @Override
    public FloatSortedSet subSet(final Float from, final Float to) {
        return this.subSet((float)from, (float)to);
    }
    
    @Override
    public Float first() {
        return this.firstFloat();
    }
    
    @Override
    public Float last() {
        return this.lastFloat();
    }
    
    @Deprecated
    @Override
    public FloatBidirectionalIterator floatIterator() {
        return this.iterator();
    }
    
    @Override
    public abstract FloatBidirectionalIterator iterator();
}
