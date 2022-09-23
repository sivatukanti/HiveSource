// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.optim.nonlinear.scalar;

import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optim.OptimizationData;

public class ObjectiveFunctionGradient implements OptimizationData
{
    private final MultivariateVectorFunction gradient;
    
    public ObjectiveFunctionGradient(final MultivariateVectorFunction g) {
        this.gradient = g;
    }
    
    public MultivariateVectorFunction getObjectiveFunctionGradient() {
        return this.gradient;
    }
}
