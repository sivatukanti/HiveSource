// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.distribution;

import org.apache.commons.math3.special.Beta;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

public class BetaDistribution extends AbstractRealDistribution
{
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
    private static final long serialVersionUID = -1221965979403477668L;
    private final double alpha;
    private final double beta;
    private double z;
    private final double solverAbsoluteAccuracy;
    
    public BetaDistribution(final double alpha, final double beta) {
        this(alpha, beta, 1.0E-9);
    }
    
    public BetaDistribution(final double alpha, final double beta, final double inverseCumAccuracy) {
        this(new Well19937c(), alpha, beta, inverseCumAccuracy);
    }
    
    public BetaDistribution(final RandomGenerator rng, final double alpha, final double beta, final double inverseCumAccuracy) {
        super(rng);
        this.alpha = alpha;
        this.beta = beta;
        this.z = Double.NaN;
        this.solverAbsoluteAccuracy = inverseCumAccuracy;
    }
    
    public double getAlpha() {
        return this.alpha;
    }
    
    public double getBeta() {
        return this.beta;
    }
    
    private void recomputeZ() {
        if (Double.isNaN(this.z)) {
            this.z = Gamma.logGamma(this.alpha) + Gamma.logGamma(this.beta) - Gamma.logGamma(this.alpha + this.beta);
        }
    }
    
    public double density(final double x) {
        this.recomputeZ();
        if (x < 0.0 || x > 1.0) {
            return 0.0;
        }
        if (x == 0.0) {
            if (this.alpha < 1.0) {
                throw new NumberIsTooSmallException(LocalizedFormats.CANNOT_COMPUTE_BETA_DENSITY_AT_0_FOR_SOME_ALPHA, this.alpha, 1, false);
            }
            return 0.0;
        }
        else {
            if (x != 1.0) {
                final double logX = FastMath.log(x);
                final double log1mX = FastMath.log1p(-x);
                return FastMath.exp((this.alpha - 1.0) * logX + (this.beta - 1.0) * log1mX - this.z);
            }
            if (this.beta < 1.0) {
                throw new NumberIsTooSmallException(LocalizedFormats.CANNOT_COMPUTE_BETA_DENSITY_AT_1_FOR_SOME_BETA, this.beta, 1, false);
            }
            return 0.0;
        }
    }
    
    public double cumulativeProbability(final double x) {
        if (x <= 0.0) {
            return 0.0;
        }
        if (x >= 1.0) {
            return 1.0;
        }
        return Beta.regularizedBeta(x, this.alpha, this.beta);
    }
    
    @Override
    protected double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }
    
    public double getNumericalMean() {
        final double a = this.getAlpha();
        return a / (a + this.getBeta());
    }
    
    public double getNumericalVariance() {
        final double a = this.getAlpha();
        final double b = this.getBeta();
        final double alphabetasum = a + b;
        return a * b / (alphabetasum * alphabetasum * (alphabetasum + 1.0));
    }
    
    public double getSupportLowerBound() {
        return 0.0;
    }
    
    public double getSupportUpperBound() {
        return 1.0;
    }
    
    public boolean isSupportLowerBoundInclusive() {
        return false;
    }
    
    public boolean isSupportUpperBoundInclusive() {
        return false;
    }
    
    public boolean isSupportConnected() {
        return true;
    }
}
