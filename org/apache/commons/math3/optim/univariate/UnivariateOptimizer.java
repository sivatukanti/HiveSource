// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.optim.univariate;

import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.BaseOptimizer;

public abstract class UnivariateOptimizer extends BaseOptimizer<UnivariatePointValuePair>
{
    private UnivariateFunction function;
    private GoalType goal;
    private double start;
    private double min;
    private double max;
    
    protected UnivariateOptimizer(final ConvergenceChecker<UnivariatePointValuePair> checker) {
        super(checker);
    }
    
    @Override
    public UnivariatePointValuePair optimize(final OptimizationData... optData) throws TooManyEvaluationsException {
        this.parseOptimizationData(optData);
        return super.optimize(optData);
    }
    
    public GoalType getGoalType() {
        return this.goal;
    }
    
    private void parseOptimizationData(final OptimizationData... optData) {
        for (final OptimizationData data : optData) {
            if (data instanceof SearchInterval) {
                final SearchInterval interval = (SearchInterval)data;
                this.min = interval.getMin();
                this.max = interval.getMax();
                this.start = interval.getStartValue();
            }
            else if (data instanceof UnivariateObjectiveFunction) {
                this.function = ((UnivariateObjectiveFunction)data).getObjectiveFunction();
            }
            else if (data instanceof GoalType) {
                this.goal = (GoalType)data;
            }
        }
    }
    
    public double getStartValue() {
        return this.start;
    }
    
    public double getMin() {
        return this.min;
    }
    
    public double getMax() {
        return this.max;
    }
    
    protected double computeObjectiveValue(final double x) {
        super.incrementEvaluationCount();
        return this.function.value(x);
    }
}
