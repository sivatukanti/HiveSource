// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.doubles;

import java.util.SortedSet;
import java.util.Iterator;

public abstract class AbstractDoubleSortedSet extends AbstractDoubleSet implements DoubleSortedSet
{
    protected AbstractDoubleSortedSet() {
    }
    
    @Override
    public DoubleSortedSet headSet(final Double to) {
        return this.headSet((double)to);
    }
    
    @Override
    public DoubleSortedSet tailSet(final Double from) {
        return this.tailSet((double)from);
    }
    
    @Override
    public DoubleSortedSet subSet(final Double from, final Double to) {
        return this.subSet((double)from, (double)to);
    }
    
    @Override
    public Double first() {
        return this.firstDouble();
    }
    
    @Override
    public Double last() {
        return this.lastDouble();
    }
    
    @Deprecated
    @Override
    public DoubleBidirectionalIterator doubleIterator() {
        return this.iterator();
    }
    
    @Override
    public abstract DoubleBidirectionalIterator iterator();
}
