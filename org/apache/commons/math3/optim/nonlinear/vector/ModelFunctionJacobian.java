// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.optim.nonlinear.vector;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.optim.OptimizationData;

public class ModelFunctionJacobian implements OptimizationData
{
    private final MultivariateMatrixFunction jacobian;
    
    public ModelFunctionJacobian(final MultivariateMatrixFunction j) {
        this.jacobian = j;
    }
    
    public MultivariateMatrixFunction getModelFunctionJacobian() {
        return this.jacobian;
    }
}
