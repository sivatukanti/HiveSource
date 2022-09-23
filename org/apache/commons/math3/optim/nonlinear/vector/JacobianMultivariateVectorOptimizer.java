// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.optim.nonlinear.vector;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;

public abstract class JacobianMultivariateVectorOptimizer extends MultivariateVectorOptimizer
{
    private MultivariateMatrixFunction jacobian;
    
    protected JacobianMultivariateVectorOptimizer(final ConvergenceChecker<PointVectorValuePair> checker) {
        super(checker);
    }
    
    protected double[][] computeJacobian(final double[] params) {
        return this.jacobian.value(params);
    }
    
    @Override
    public PointVectorValuePair optimize(final OptimizationData... optData) throws TooManyEvaluationsException, DimensionMismatchException {
        this.parseOptimizationData(optData);
        return super.optimize(optData);
    }
    
    private void parseOptimizationData(final OptimizationData... optData) {
        for (final OptimizationData data : optData) {
            if (data instanceof ModelFunctionJacobian) {
                this.jacobian = ((ModelFunctionJacobian)data).getModelFunctionJacobian();
                break;
            }
        }
    }
}
