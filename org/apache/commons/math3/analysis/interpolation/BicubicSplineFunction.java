// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.analysis.BivariateFunction;

class BicubicSplineFunction implements BivariateFunction
{
    private static final short N = 4;
    private final double[][] a;
    private BivariateFunction partialDerivativeX;
    private BivariateFunction partialDerivativeY;
    private BivariateFunction partialDerivativeXX;
    private BivariateFunction partialDerivativeYY;
    private BivariateFunction partialDerivativeXY;
    
    public BicubicSplineFunction(final double[] a) {
        this.a = new double[4][4];
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.a[i][j] = a[i + 4 * j];
            }
        }
    }
    
    public double value(final double x, final double y) {
        if (x < 0.0 || x > 1.0) {
            throw new OutOfRangeException(x, 0, 1);
        }
        if (y < 0.0 || y > 1.0) {
            throw new OutOfRangeException(y, 0, 1);
        }
        final double x2 = x * x;
        final double x3 = x2 * x;
        final double[] pX = { 1.0, x, x2, x3 };
        final double y2 = y * y;
        final double y3 = y2 * y;
        final double[] pY = { 1.0, y, y2, y3 };
        return this.apply(pX, pY, this.a);
    }
    
    private double apply(final double[] pX, final double[] pY, final double[][] coeff) {
        double result = 0.0;
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                result += coeff[i][j] * pX[i] * pY[j];
            }
        }
        return result;
    }
    
    public BivariateFunction partialDerivativeX() {
        if (this.partialDerivativeX == null) {
            this.computePartialDerivatives();
        }
        return this.partialDerivativeX;
    }
    
    public BivariateFunction partialDerivativeY() {
        if (this.partialDerivativeY == null) {
            this.computePartialDerivatives();
        }
        return this.partialDerivativeY;
    }
    
    public BivariateFunction partialDerivativeXX() {
        if (this.partialDerivativeXX == null) {
            this.computePartialDerivatives();
        }
        return this.partialDerivativeXX;
    }
    
    public BivariateFunction partialDerivativeYY() {
        if (this.partialDerivativeYY == null) {
            this.computePartialDerivatives();
        }
        return this.partialDerivativeYY;
    }
    
    public BivariateFunction partialDerivativeXY() {
        if (this.partialDerivativeXY == null) {
            this.computePartialDerivatives();
        }
        return this.partialDerivativeXY;
    }
    
    private void computePartialDerivatives() {
        final double[][] aX = new double[4][4];
        final double[][] aY = new double[4][4];
        final double[][] aXX = new double[4][4];
        final double[][] aYY = new double[4][4];
        final double[][] aXY = new double[4][4];
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                final double c = this.a[i][j];
                aX[i][j] = i * c;
                aY[i][j] = j * c;
                aXX[i][j] = (i - 1) * aX[i][j];
                aYY[i][j] = (j - 1) * aY[i][j];
                aXY[i][j] = j * aX[i][j];
            }
        }
        this.partialDerivativeX = new BivariateFunction() {
            public double value(final double x, final double y) {
                final double x2 = x * x;
                final double[] pX = { 0.0, 1.0, x, x2 };
                final double y2 = y * y;
                final double y3 = y2 * y;
                final double[] pY = { 1.0, y, y2, y3 };
                return BicubicSplineFunction.this.apply(pX, pY, aX);
            }
        };
        this.partialDerivativeY = new BivariateFunction() {
            public double value(final double x, final double y) {
                final double x2 = x * x;
                final double x3 = x2 * x;
                final double[] pX = { 1.0, x, x2, x3 };
                final double y2 = y * y;
                final double[] pY = { 0.0, 1.0, y, y2 };
                return BicubicSplineFunction.this.apply(pX, pY, aY);
            }
        };
        this.partialDerivativeXX = new BivariateFunction() {
            public double value(final double x, final double y) {
                final double[] pX = { 0.0, 0.0, 1.0, x };
                final double y2 = y * y;
                final double y3 = y2 * y;
                final double[] pY = { 1.0, y, y2, y3 };
                return BicubicSplineFunction.this.apply(pX, pY, aXX);
            }
        };
        this.partialDerivativeYY = new BivariateFunction() {
            public double value(final double x, final double y) {
                final double x2 = x * x;
                final double x3 = x2 * x;
                final double[] pX = { 1.0, x, x2, x3 };
                final double[] pY = { 0.0, 0.0, 1.0, y };
                return BicubicSplineFunction.this.apply(pX, pY, aYY);
            }
        };
        this.partialDerivativeXY = new BivariateFunction() {
            public double value(final double x, final double y) {
                final double x2 = x * x;
                final double[] pX = { 0.0, 1.0, x, x2 };
                final double y2 = y * y;
                final double[] pY = { 0.0, 1.0, y, y2 };
                return BicubicSplineFunction.this.apply(pX, pY, aXY);
            }
        };
    }
}
