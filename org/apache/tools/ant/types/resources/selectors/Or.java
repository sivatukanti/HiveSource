// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources.selectors;

import java.util.Iterator;
import org.apache.tools.ant.types.Resource;

public class Or extends ResourceSelectorContainer implements ResourceSelector
{
    public Or() {
    }
    
    public Or(final ResourceSelector[] r) {
        super(r);
    }
    
    public boolean isSelected(final Resource r) {
        final Iterator<ResourceSelector> i = this.getSelectors();
        while (i.hasNext()) {
            if (i.next().isSelected(r)) {
                return true;
            }
        }
        return false;
    }
}
