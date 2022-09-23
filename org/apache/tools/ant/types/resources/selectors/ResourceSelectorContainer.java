// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import java.util.Stack;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.types.DataType;

public class ResourceSelectorContainer extends DataType
{
    private final List<ResourceSelector> resourceSelectors;
    
    public ResourceSelectorContainer() {
        this.resourceSelectors = new ArrayList<ResourceSelector>();
    }
    
    public ResourceSelectorContainer(final ResourceSelector[] r) {
        this.resourceSelectors = new ArrayList<ResourceSelector>();
        for (int i = 0; i < r.length; ++i) {
            this.add(r[i]);
        }
    }
    
    public void add(final ResourceSelector s) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (s == null) {
            return;
        }
        this.resourceSelectors.add(s);
        this.setChecked(false);
    }
    
    public boolean hasSelectors() {
        if (this.isReference()) {
            return ((ResourceSelectorContainer)this.getCheckedRef()).hasSelectors();
        }
        this.dieOnCircularReference();
        return !this.resourceSelectors.isEmpty();
    }
    
    public int selectorCount() {
        if (this.isReference()) {
            return ((ResourceSelectorContainer)this.getCheckedRef()).selectorCount();
        }
        this.dieOnCircularReference();
        return this.resourceSelectors.size();
    }
    
    public Iterator<ResourceSelector> getSelectors() {
        if (this.isReference()) {
            return ((ResourceSelectorContainer)this.getCheckedRef()).getSelectors();
        }
        this.dieOnCircularReference();
        return Collections.unmodifiableList((List<? extends ResourceSelector>)this.resourceSelectors).iterator();
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
            for (final ResourceSelector resourceSelector : this.resourceSelectors) {
                if (resourceSelector instanceof DataType) {
                    DataType.pushAndInvokeCircularReferenceCheck((DataType)resourceSelector, stk, p);
                }
            }
            this.setChecked(true);
        }
    }
}
