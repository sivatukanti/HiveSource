// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources.comparators;

import org.apache.tools.ant.types.Resource;
import java.util.Comparator;
import org.apache.tools.ant.types.DataType;

public abstract class ResourceComparator extends DataType implements Comparator<Resource>
{
    public final int compare(final Resource foo, final Resource bar) {
        this.dieOnCircularReference();
        final ResourceComparator c = (ResourceComparator)(this.isReference() ? this.getCheckedRef() : this);
        return c.resourceCompare(foo, bar);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this.isReference()) {
            return this.getCheckedRef().equals(o);
        }
        return o != null && (o == this || o.getClass().equals(this.getClass()));
    }
    
    @Override
    public synchronized int hashCode() {
        if (this.isReference()) {
            return this.getCheckedRef().hashCode();
        }
        return this.getClass().hashCode();
    }
    
    protected abstract int resourceCompare(final Resource p0, final Resource p1);
}
