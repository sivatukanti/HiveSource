// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Space;

public class BoundaryAttribute<S extends Space>
{
    private final SubHyperplane<S> plusOutside;
    private final SubHyperplane<S> plusInside;
    
    public BoundaryAttribute(final SubHyperplane<S> plusOutside, final SubHyperplane<S> plusInside) {
        this.plusOutside = plusOutside;
        this.plusInside = plusInside;
    }
    
    public SubHyperplane<S> getPlusOutside() {
        return this.plusOutside;
    }
    
    public SubHyperplane<S> getPlusInside() {
        return this.plusInside;
    }
}
