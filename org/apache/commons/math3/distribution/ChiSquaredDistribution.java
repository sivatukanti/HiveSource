// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.distribution;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

public class ChiSquaredDistribution extends AbstractRealDistribution
{
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
    private static final long serialVersionUID = -8352658048349159782L;
    private final GammaDistribution gamma;
    private final double solverAbsoluteAccuracy;
    
    public ChiSquaredDistribution(final double degreesOfFreedom) {
        this(degreesOfFreedom, 1.0E-9);
    }
    
    public ChiSquaredDistribution(final double degreesOfFreedom, final double inverseCumAccuracy) {
        this(new Well19937c(), degreesOfFreedom, inverseCumAccuracy);
    }
    
    public ChiSquaredDistribution(final RandomGenerator rng, final double degreesOfFreedom, final double inverseCumAccuracy) {
        super(rng);
        this.gamma = new GammaDistribution(degreesOfFreedom / 2.0, 2.0);
        this.solverAbsoluteAccuracy = inverseCumAccuracy;
    }
    
    public double getDegreesOfFreedom() {
        return this.gamma.getShape() * 2.0;
    }
    
    public double density(final double x) {
        return this.gamma.density(x);
    }
    
    public double cumulativeProbability(final double x) {
        return this.gamma.cumulativeProbability(x);
    }
    
    @Override
    protected double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }
    
    public double getNumericalMean() {
        return this.getDegreesOfFreedom();
    }
    
    public double getNumericalVariance() {
        return 2.0 * this.getDegreesOfFreedom();
    }
    
    public double getSupportLowerBound() {
        return 0.0;
    }
    
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }
    
    public boolean isSupportLowerBoundInclusive() {
        return true;
    }
    
    public boolean isSupportUpperBoundInclusive() {
        return false;
    }
    
    public boolean isSupportConnected() {
        return true;
    }
}
