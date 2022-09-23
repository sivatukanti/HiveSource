// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources.comparators;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.Project;
import java.util.Stack;
import java.util.Iterator;
import org.apache.tools.ant.types.Resource;
import java.util.Vector;
import java.util.List;

public class DelegatedResourceComparator extends ResourceComparator
{
    private List<ResourceComparator> resourceComparators;
    
    public DelegatedResourceComparator() {
        this.resourceComparators = null;
    }
    
    public synchronized void add(final ResourceComparator c) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (c == null) {
            return;
        }
        (this.resourceComparators = ((this.resourceComparators == null) ? new Vector<ResourceComparator>() : this.resourceComparators)).add(c);
        this.setChecked(false);
    }
    
    @Override
    public synchronized boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (this.isReference()) {
            return this.getCheckedRef().equals(o);
        }
        if (!(o instanceof DelegatedResourceComparator)) {
            return false;
        }
        final List<ResourceComparator> ov = ((DelegatedResourceComparator)o).resourceComparators;
        return (this.resourceComparators == null) ? (ov == null) : this.resourceComparators.equals(ov);
    }
    
    @Override
    public synchronized int hashCode() {
        if (this.isReference()) {
            return this.getCheckedRef().hashCode();
        }
        return (this.resourceComparators == null) ? 0 : this.resourceComparators.hashCode();
    }
    
    @Override
    protected synchronized int resourceCompare(final Resource foo, final Resource bar) {
        if (this.resourceComparators == null || this.resourceComparators.isEmpty()) {
            return foo.compareTo(bar);
        }
        int result = 0;
        for (Iterator<ResourceComparator> i = this.resourceComparators.iterator(); result == 0 && i.hasNext(); result = i.next().resourceCompare(foo, bar)) {}
        return result;
    }
    
    @Override
    protected void dieOnCircularReference(final Stack<Object> stk, final Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        }
        else {
            if (this.resourceComparators != null && !this.resourceComparators.isEmpty()) {
                for (final ResourceComparator resourceComparator : this.resourceComparators) {
                    if (resourceComparator instanceof DataType) {
                        DataType.pushAndInvokeCircularReferenceCheck(resourceComparator, stk, p);
                    }
                }
            }
            this.setChecked(true);
        }
    }
}
