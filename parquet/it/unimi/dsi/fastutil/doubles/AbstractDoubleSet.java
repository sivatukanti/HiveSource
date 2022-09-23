// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.doubles;

import java.util.Iterator;
import parquet.it.unimi.dsi.fastutil.HashCommon;
import java.util.Collection;
import java.util.Set;

public abstract class AbstractDoubleSet extends AbstractDoubleCollection implements Cloneable, DoubleSet
{
    protected AbstractDoubleSet() {
    }
    
    @Override
    public abstract DoubleIterator iterator();
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Set)) {
            return false;
        }
        final Set<?> s = (Set<?>)o;
        return s.size() == this.size() && this.containsAll(s);
    }
    
    @Override
    public int hashCode() {
        int h = 0;
        int n = this.size();
        final DoubleIterator i = this.iterator();
        while (n-- != 0) {
            final double k = i.nextDouble();
            h += HashCommon.double2int(k);
        }
        return h;
    }
    
    @Override
    public boolean remove(final double k) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean rem(final double k) {
        return this.remove(k);
    }
    
    @Override
    public boolean remove(final Object o) {
        return this.remove((double)o);
    }
}
