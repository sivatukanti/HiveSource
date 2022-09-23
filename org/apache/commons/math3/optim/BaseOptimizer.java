// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.optim;

import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.Incrementor;

public abstract class BaseOptimizer<PAIR>
{
    protected final Incrementor evaluations;
    protected final Incrementor iterations;
    private ConvergenceChecker<PAIR> checker;
    
    protected BaseOptimizer(final ConvergenceChecker<PAIR> checker) {
        this.checker = checker;
        this.evaluations = new Incrementor(0, new MaxEvalCallback());
        this.iterations = new Incrementor(0, new MaxIterCallback());
    }
    
    public int getMaxEvaluations() {
        return this.evaluations.getMaximalCount();
    }
    
    public int getEvaluations() {
        return this.evaluations.getCount();
    }
    
    public int getMaxIterations() {
        return this.iterations.getMaximalCount();
    }
    
    public int getIterations() {
        return this.iterations.getCount();
    }
    
    public ConvergenceChecker<PAIR> getConvergenceChecker() {
        return this.checker;
    }
    
    public PAIR optimize(final OptimizationData... optData) throws TooManyEvaluationsException, TooManyIterationsException {
        this.parseOptimizationData(optData);
        this.evaluations.resetCount();
        this.iterations.resetCount();
        return this.doOptimize();
    }
    
    protected abstract PAIR doOptimize();
    
    protected void incrementEvaluationCount() throws TooManyEvaluationsException {
        this.evaluations.incrementCount();
    }
    
    protected void incrementIterationCount() throws TooManyIterationsException {
        this.iterations.incrementCount();
    }
    
    private void parseOptimizationData(final OptimizationData... optData) {
        for (final OptimizationData data : optData) {
            if (data instanceof MaxEval) {
                this.evaluations.setMaximalCount(((MaxEval)data).getMaxEval());
            }
            else if (data instanceof MaxIter) {
                this.iterations.setMaximalCount(((MaxIter)data).getMaxIter());
            }
        }
    }
    
    private static class MaxEvalCallback implements Incrementor.MaxCountExceededCallback
    {
        public void trigger(final int max) {
            throw new TooManyEvaluationsException(max);
        }
    }
    
    private static class MaxIterCallback implements Incrementor.MaxCountExceededCallback
    {
        public void trigger(final int max) {
            throw new TooManyIterationsException(max);
        }
    }
}
