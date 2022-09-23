// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.optim.nonlinear.scalar;

import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;

public abstract class GradientMultivariateOptimizer extends MultivariateOptimizer
{
    private MultivariateVectorFunction gradient;
    
    protected GradientMultivariateOptimizer(final ConvergenceChecker<PointValuePair> checker) {
        super(checker);
    }
    
    protected double[] computeObjectiveGradient(final double[] params) {
        return this.gradient.value(params);
    }
    
    @Override
    public PointValuePair optimize(final OptimizationData... optData) throws TooManyEvaluationsException {
        this.parseOptimizationData(optData);
        return super.optimize(optData);
    }
    
    private void parseOptimizationData(final OptimizationData... optData) {
        for (final OptimizationData data : optData) {
            if (data instanceof ObjectiveFunctionGradient) {
                this.gradient = ((ObjectiveFunctionGradient)data).getObjectiveFunctionGradient();
                break;
            }
        }
    }
}
