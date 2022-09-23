// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.optim.nonlinear.vector;

import org.apache.commons.math3.optim.OptimizationData;

public class Target implements OptimizationData
{
    private final double[] target;
    
    public Target(final double[] observations) {
        this.target = observations.clone();
    }
    
    public double[] getTarget() {
        return this.target.clone();
    }
}
