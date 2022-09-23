// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.types.Resource;

public class Not implements ResourceSelector
{
    private ResourceSelector sel;
    
    public Not() {
    }
    
    public Not(final ResourceSelector s) {
        this.add(s);
    }
    
    public void add(final ResourceSelector s) {
        if (this.sel != null) {
            throw new IllegalStateException("The Not ResourceSelector accepts a single nested ResourceSelector");
        }
        this.sel = s;
    }
    
    public boolean isSelected(final Resource r) {
        return !this.sel.isSelected(r);
    }
}
