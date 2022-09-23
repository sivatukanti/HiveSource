// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.optim.nonlinear.scalar;

import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.BaseMultivariateOptimizer;

public abstract class MultivariateOptimizer extends BaseMultivariateOptimizer<PointValuePair>
{
    private MultivariateFunction function;
    private GoalType goal;
    
    protected MultivariateOptimizer(final ConvergenceChecker<PointValuePair> checker) {
        super(checker);
    }
    
    @Override
    public PointValuePair optimize(final OptimizationData... optData) throws TooManyEvaluationsException {
        this.parseOptimizationData(optData);
        return super.optimize(optData);
    }
    
    private void parseOptimizationData(final OptimizationData... optData) {
        for (final OptimizationData data : optData) {
            if (data instanceof GoalType) {
                this.goal = (GoalType)data;
            }
            else if (data instanceof ObjectiveFunction) {
                this.function = ((ObjectiveFunction)data).getObjectiveFunction();
            }
        }
    }
    
    public GoalType getGoalType() {
        return this.goal;
    }
    
    protected double computeObjectiveValue(final double[] params) {
        super.incrementEvaluationCount();
        return this.function.value(params);
    }
}
