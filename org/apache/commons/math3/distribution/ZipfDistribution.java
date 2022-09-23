// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.distribution;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

public class ZipfDistribution extends AbstractIntegerDistribution
{
    private static final long serialVersionUID = -140627372283420404L;
    private final int numberOfElements;
    private final double exponent;
    private double numericalMean;
    private boolean numericalMeanIsCalculated;
    private double numericalVariance;
    private boolean numericalVarianceIsCalculated;
    
    public ZipfDistribution(final int numberOfElements, final double exponent) {
        this(new Well19937c(), numberOfElements, exponent);
    }
    
    public ZipfDistribution(final RandomGenerator rng, final int numberOfElements, final double exponent) throws NotStrictlyPositiveException {
        super(rng);
        this.numericalMean = Double.NaN;
        this.numericalMeanIsCalculated = false;
        this.numericalVariance = Double.NaN;
        this.numericalVarianceIsCalculated = false;
        if (numberOfElements <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.DIMENSION, numberOfElements);
        }
        if (exponent <= 0.0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.EXPONENT, exponent);
        }
        this.numberOfElements = numberOfElements;
        this.exponent = exponent;
    }
    
    public int getNumberOfElements() {
        return this.numberOfElements;
    }
    
    public double getExponent() {
        return this.exponent;
    }
    
    public double probability(final int x) {
        if (x <= 0 || x > this.numberOfElements) {
            return 0.0;
        }
        return 1.0 / FastMath.pow(x, this.exponent) / this.generalizedHarmonic(this.numberOfElements, this.exponent);
    }
    
    public double cumulativeProbability(final int x) {
        if (x <= 0) {
            return 0.0;
        }
        if (x >= this.numberOfElements) {
            return 1.0;
        }
        return this.generalizedHarmonic(x, this.exponent) / this.generalizedHarmonic(this.numberOfElements, this.exponent);
    }
    
    public double getNumericalMean() {
        if (!this.numericalMeanIsCalculated) {
            this.numericalMean = this.calculateNumericalMean();
            this.numericalMeanIsCalculated = true;
        }
        return this.numericalMean;
    }
    
    protected double calculateNumericalMean() {
        final int N = this.getNumberOfElements();
        final double s = this.getExponent();
        final double Hs1 = this.generalizedHarmonic(N, s - 1.0);
        final double Hs2 = this.generalizedHarmonic(N, s);
        return Hs1 / Hs2;
    }
    
    public double getNumericalVariance() {
        if (!this.numericalVarianceIsCalculated) {
            this.numericalVariance = this.calculateNumericalVariance();
            this.numericalVarianceIsCalculated = true;
        }
        return this.numericalVariance;
    }
    
    protected double calculateNumericalVariance() {
        final int N = this.getNumberOfElements();
        final double s = this.getExponent();
        final double Hs2 = this.generalizedHarmonic(N, s - 2.0);
        final double Hs3 = this.generalizedHarmonic(N, s - 1.0);
        final double Hs4 = this.generalizedHarmonic(N, s);
        return Hs2 / Hs4 - Hs3 * Hs3 / (Hs4 * Hs4);
    }
    
    private double generalizedHarmonic(final int n, final double m) {
        double value = 0.0;
        for (int k = n; k > 0; --k) {
            value += 1.0 / FastMath.pow(k, m);
        }
        return value;
    }
    
    public int getSupportLowerBound() {
        return 1;
    }
    
    public int getSupportUpperBound() {
        return this.getNumberOfElements();
    }
    
    public boolean isSupportConnected() {
        return true;
    }
}
