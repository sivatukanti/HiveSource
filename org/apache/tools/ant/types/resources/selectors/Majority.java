// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources.selectors;

import java.util.Iterator;
import org.apache.tools.ant.types.Resource;

public class Majority extends ResourceSelectorContainer implements ResourceSelector
{
    private boolean tie;
    
    public Majority() {
        this.tie = true;
    }
    
    public Majority(final ResourceSelector[] r) {
        super(r);
        this.tie = true;
    }
    
    public synchronized void setAllowtie(final boolean b) {
        this.tie = b;
    }
    
    public synchronized boolean isSelected(final Resource r) {
        int passed = 0;
        int failed = 0;
        final int count = this.selectorCount();
        final boolean even = count % 2 == 0;
        final int threshold = count / 2;
        final Iterator<ResourceSelector> i = this.getSelectors();
        while (i.hasNext()) {
            if (i.next().isSelected(r)) {
                if (++passed > threshold || (even && this.tie && passed == threshold)) {
                    return true;
                }
                continue;
            }
            else {
                if (++failed > threshold || (even && !this.tie && failed == threshold)) {
                    return false;
                }
                continue;
            }
        }
        return false;
    }
}
