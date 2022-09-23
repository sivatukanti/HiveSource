// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.util;

import java.util.EventObject;

public class IterationEvent extends EventObject
{
    private static final long serialVersionUID = 20120128L;
    private final int iterations;
    
    public IterationEvent(final Object source, final int iterations) {
        super(source);
        this.iterations = iterations;
    }
    
    public int getIterations() {
        return this.iterations;
    }
}
