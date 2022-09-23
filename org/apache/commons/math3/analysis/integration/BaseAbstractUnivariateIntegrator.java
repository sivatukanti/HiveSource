// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.analysis.integration;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.analysis.solvers.UnivariateSolverUtils;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.Incrementor;

public abstract class BaseAbstractUnivariateIntegrator implements UnivariateIntegrator
{
    public static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-15;
    public static final double DEFAULT_RELATIVE_ACCURACY = 1.0E-6;
    public static final int DEFAULT_MIN_ITERATIONS_COUNT = 3;
    public static final int DEFAULT_MAX_ITERATIONS_COUNT = Integer.MAX_VALUE;
    protected final Incrementor iterations;
    private final double absoluteAccuracy;
    private final double relativeAccuracy;
    private final int minimalIterationCount;
    private final Incrementor evaluations;
    private UnivariateFunction function;
    private double min;
    private double max;
    
    protected BaseAbstractUnivariateIntegrator(final double relativeAccuracy, final double absoluteAccuracy, final int minimalIterationCount, final int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException {
        this.relativeAccuracy = relativeAccuracy;
        this.absoluteAccuracy = absoluteAccuracy;
        if (minimalIterationCount <= 0) {
            throw new NotStrictlyPositiveException(minimalIterationCount);
        }
        if (maximalIterationCount <= minimalIterationCount) {
            throw new NumberIsTooSmallException(maximalIterationCount, minimalIterationCount, false);
        }
        this.minimalIterationCount = minimalIterationCount;
        (this.iterations = new Incrementor()).setMaximalCount(maximalIterationCount);
        this.evaluations = new Incrementor();
    }
    
    protected BaseAbstractUnivariateIntegrator(final double relativeAccuracy, final double absoluteAccuracy) {
        this(relativeAccuracy, absoluteAccuracy, 3, Integer.MAX_VALUE);
    }
    
    protected BaseAbstractUnivariateIntegrator(final int minimalIterationCount, final int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException {
        this(1.0E-6, 1.0E-15, minimalIterationCount, maximalIterationCount);
    }
    
    public double getRelativeAccuracy() {
        return this.relativeAccuracy;
    }
    
    public double getAbsoluteAccuracy() {
        return this.absoluteAccuracy;
    }
    
    public int getMinimalIterationCount() {
        return this.minimalIterationCount;
    }
    
    public int getMaximalIterationCount() {
        return this.iterations.getMaximalCount();
    }
    
    public int getEvaluations() {
        return this.evaluations.getCount();
    }
    
    public int getIterations() {
        return this.iterations.getCount();
    }
    
    protected double getMin() {
        return this.min;
    }
    
    protected double getMax() {
        return this.max;
    }
    
    protected double computeObjectiveValue(final double point) throws TooManyEvaluationsException {
        try {
            this.evaluations.incrementCount();
        }
        catch (MaxCountExceededException e) {
            throw new TooManyEvaluationsException(e.getMax());
        }
        return this.function.value(point);
    }
    
    protected void setup(final int maxEval, final UnivariateFunction f, final double lower, final double upper) throws NullArgumentException, MathIllegalArgumentException {
        MathUtils.checkNotNull(f);
        UnivariateSolverUtils.verifyInterval(lower, upper);
        this.min = lower;
        this.max = upper;
        this.function = f;
        this.evaluations.setMaximalCount(maxEval);
        this.evaluations.resetCount();
        this.iterations.resetCount();
    }
    
    public double integrate(final int maxEval, final UnivariateFunction f, final double lower, final double upper) throws TooManyEvaluationsException, MaxCountExceededException, MathIllegalArgumentException, NullArgumentException {
        this.setup(maxEval, f, lower, upper);
        return this.doIntegrate();
    }
    
    protected abstract double doIntegrate() throws TooManyEvaluationsException, MaxCountExceededException;
}
