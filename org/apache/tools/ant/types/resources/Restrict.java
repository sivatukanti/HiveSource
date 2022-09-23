// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.Project;
import java.util.Stack;
import java.util.Iterator;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.selectors.ResourceSelectorContainer;

public class Restrict extends ResourceSelectorContainer implements ResourceCollection
{
    private LazyResourceCollectionWrapper w;
    
    public Restrict() {
        this.w = new LazyResourceCollectionWrapper() {
            @Override
            protected boolean filterResource(final Resource r) {
                final Iterator<ResourceSelector> i = Restrict.this.getSelectors();
                while (i.hasNext()) {
                    if (!i.next().isSelected(r)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
    
    public synchronized void add(final ResourceCollection c) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (c == null) {
            return;
        }
        this.w.add(c);
        this.setChecked(false);
    }
    
    public synchronized void setCache(final boolean b) {
        this.w.setCache(b);
    }
    
    public synchronized boolean isCache() {
        return this.w.isCache();
    }
    
    @Override
    public synchronized void add(final ResourceSelector s) {
        if (s == null) {
            return;
        }
        super.add(s);
        FailFast.invalidate(this);
    }
    
    public final synchronized Iterator<Resource> iterator() {
        if (this.isReference()) {
            return ((Restrict)this.getCheckedRef()).iterator();
        }
        this.dieOnCircularReference();
        return this.w.iterator();
    }
    
    public synchronized int size() {
        if (this.isReference()) {
            return ((Restrict)this.getCheckedRef()).size();
        }
        this.dieOnCircularReference();
        return this.w.size();
    }
    
    public synchronized boolean isFilesystemOnly() {
        if (this.isReference()) {
            return ((Restrict)this.getCheckedRef()).isFilesystemOnly();
        }
        this.dieOnCircularReference();
        return this.w.isFilesystemOnly();
    }
    
    @Override
    public synchronized String toString() {
        if (this.isReference()) {
            return this.getCheckedRef().toString();
        }
        this.dieOnCircularReference();
        return this.w.toString();
    }
    
    @Override
    protected synchronized void dieOnCircularReference(final Stack<Object> stk, final Project p) {
        if (this.isChecked()) {
            return;
        }
        super.dieOnCircularReference(stk, p);
        if (!this.isReference()) {
            DataType.pushAndInvokeCircularReferenceCheck(this.w, stk, p);
            this.setChecked(true);
        }
    }
}
