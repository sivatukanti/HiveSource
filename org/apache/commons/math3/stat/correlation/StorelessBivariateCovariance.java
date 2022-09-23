// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.stat.correlation;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

class StorelessBivariateCovariance
{
    private double meanX;
    private double meanY;
    private double n;
    private double covarianceNumerator;
    private boolean biasCorrected;
    
    public StorelessBivariateCovariance() {
        this(true);
    }
    
    public StorelessBivariateCovariance(final boolean biasCorrection) {
        final double n = 0.0;
        this.meanY = n;
        this.meanX = n;
        this.n = 0.0;
        this.covarianceNumerator = 0.0;
        this.biasCorrected = biasCorrection;
    }
    
    public void increment(final double x, final double y) {
        ++this.n;
        final double deltaX = x - this.meanX;
        final double deltaY = y - this.meanY;
        this.meanX += deltaX / this.n;
        this.meanY += deltaY / this.n;
        this.covarianceNumerator += (this.n - 1.0) / this.n * deltaX * deltaY;
    }
    
    public double getN() {
        return this.n;
    }
    
    public double getResult() throws NumberIsTooSmallException {
        if (this.n < 2.0) {
            throw new NumberIsTooSmallException(LocalizedFormats.INSUFFICIENT_DIMENSION, this.n, 2, true);
        }
        if (this.biasCorrected) {
            return this.covarianceNumerator / (this.n - 1.0);
        }
        return this.covarianceNumerator / this.n;
    }
}
