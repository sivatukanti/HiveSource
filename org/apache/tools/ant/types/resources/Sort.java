// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.Project;
import java.util.Stack;
import org.apache.tools.ant.types.resources.comparators.ResourceComparator;
import org.apache.tools.ant.types.ResourceCollection;
import java.util.Comparator;
import java.util.Iterator;
import org.apache.tools.ant.util.CollectionUtils;
import java.util.List;
import java.util.Collections;
import org.apache.tools.ant.types.Resource;
import java.util.Collection;
import org.apache.tools.ant.types.resources.comparators.DelegatedResourceComparator;

public class Sort extends BaseResourceCollectionWrapper
{
    private DelegatedResourceComparator comp;
    
    public Sort() {
        this.comp = new DelegatedResourceComparator();
    }
    
    @Override
    protected synchronized Collection<Resource> getCollection() {
        final ResourceCollection rc = this.getResourceCollection();
        final Iterator<Resource> iter = rc.iterator();
        if (!iter.hasNext()) {
            return (Collection<Resource>)Collections.emptySet();
        }
        final List<Resource> result = (List<Resource>)(List)CollectionUtils.asCollection((Iterator<? extends Resource>)iter);
        Collections.sort(result, this.comp);
        return result;
    }
    
    public synchronized void add(final ResourceComparator c) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.comp.add(c);
        FailFast.invalidate(this);
        this.setChecked(false);
    }
    
    @Override
    protected synchronized void dieOnCircularReference(final Stack<Object> stk, final Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        super.dieOnCircularReference(stk, p);
        if (!this.isReference()) {
            DataType.pushAndInvokeCircularReferenceCheck(this.comp, stk, p);
            this.setChecked(true);
        }
    }
}
