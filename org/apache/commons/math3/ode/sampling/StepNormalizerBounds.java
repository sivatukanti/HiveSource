// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.ode.sampling;

public enum StepNormalizerBounds
{
    NEITHER(false, false), 
    FIRST(true, false), 
    LAST(false, true), 
    BOTH(true, true);
    
    private final boolean first;
    private final boolean last;
    
    private StepNormalizerBounds(final boolean first, final boolean last) {
        this.first = first;
        this.last = last;
    }
    
    public boolean firstIncluded() {
        return this.first;
    }
    
    public boolean lastIncluded() {
        return this.last;
    }
}
