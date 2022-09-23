// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.analysis.polynomials;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public class PolynomialFunctionNewtonForm implements UnivariateDifferentiableFunction
{
    private double[] coefficients;
    private final double[] c;
    private final double[] a;
    private boolean coefficientsComputed;
    
    public PolynomialFunctionNewtonForm(final double[] a, final double[] c) {
        verifyInputArray(a, c);
        this.a = new double[a.length];
        this.c = new double[c.length];
        System.arraycopy(a, 0, this.a, 0, a.length);
        System.arraycopy(c, 0, this.c, 0, c.length);
        this.coefficientsComputed = false;
    }
    
    public double value(final double z) {
        return evaluate(this.a, this.c, z);
    }
    
    public DerivativeStructure value(final DerivativeStructure t) {
        verifyInputArray(this.a, this.c);
        final int n = this.c.length;
        DerivativeStructure value = new DerivativeStructure(t.getFreeParameters(), t.getOrder(), this.a[n]);
        for (int i = n - 1; i >= 0; --i) {
            value = t.subtract(this.c[i]).multiply(value).add(this.a[i]);
        }
        return value;
    }
    
    public int degree() {
        return this.c.length;
    }
    
    public double[] getNewtonCoefficients() {
        final double[] out = new double[this.a.length];
        System.arraycopy(this.a, 0, out, 0, this.a.length);
        return out;
    }
    
    public double[] getCenters() {
        final double[] out = new double[this.c.length];
        System.arraycopy(this.c, 0, out, 0, this.c.length);
        return out;
    }
    
    public double[] getCoefficients() {
        if (!this.coefficientsComputed) {
            this.computeCoefficients();
        }
        final double[] out = new double[this.coefficients.length];
        System.arraycopy(this.coefficients, 0, out, 0, this.coefficients.length);
        return out;
    }
    
    public static double evaluate(final double[] a, final double[] c, final double z) {
        verifyInputArray(a, c);
        final int n = c.length;
        double value = a[n];
        for (int i = n - 1; i >= 0; --i) {
            value = a[i] + (z - c[i]) * value;
        }
        return value;
    }
    
    protected void computeCoefficients() {
        final int n = this.degree();
        this.coefficients = new double[n + 1];
        for (int i = 0; i <= n; ++i) {
            this.coefficients[i] = 0.0;
        }
        this.coefficients[0] = this.a[n];
        for (int i = n - 1; i >= 0; --i) {
            for (int j = n - i; j > 0; --j) {
                this.coefficients[j] = this.coefficients[j - 1] - this.c[i] * this.coefficients[j];
            }
            this.coefficients[0] = this.a[i] - this.c[i] * this.coefficients[0];
        }
        this.coefficientsComputed = true;
    }
    
    protected static void verifyInputArray(final double[] a, final double[] c) {
        if (a.length == 0 || c.length == 0) {
            throw new NoDataException(LocalizedFormats.EMPTY_POLYNOMIALS_COEFFICIENTS_ARRAY);
        }
        if (a.length != c.length + 1) {
            throw new DimensionMismatchException(LocalizedFormats.ARRAY_SIZES_SHOULD_HAVE_DIFFERENCE_1, a.length, c.length);
        }
    }
}
