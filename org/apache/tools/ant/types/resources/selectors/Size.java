// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.Comparison;

public class Size implements ResourceSelector
{
    private long size;
    private Comparison when;
    
    public Size() {
        this.size = -1L;
        this.when = Comparison.EQUAL;
    }
    
    public void setSize(final long l) {
        this.size = l;
    }
    
    public long getSize() {
        return this.size;
    }
    
    public void setWhen(final Comparison c) {
        this.when = c;
    }
    
    public Comparison getWhen() {
        return this.when;
    }
    
    public boolean isSelected(final Resource r) {
        final long diff = r.getSize() - this.size;
        return this.when.evaluate((diff == 0L) ? 0 : ((int)(diff / Math.abs(diff))));
    }
}
