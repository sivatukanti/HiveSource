// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public abstract class AbstractUnivariateStatistic implements UnivariateStatistic
{
    private double[] storedData;
    
    public void setData(final double[] values) {
        this.storedData = (double[])((values == null) ? null : ((double[])values.clone()));
    }
    
    public double[] getData() {
        return (double[])((this.storedData == null) ? null : ((double[])this.storedData.clone()));
    }
    
    protected double[] getDataRef() {
        return this.storedData;
    }
    
    public void setData(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        if (values == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY, new Object[0]);
        }
        if (begin < 0) {
            throw new NotPositiveException(LocalizedFormats.START_POSITION, begin);
        }
        if (length < 0) {
            throw new NotPositiveException(LocalizedFormats.LENGTH, length);
        }
        if (begin + length > values.length) {
            throw new NumberIsTooLargeException(LocalizedFormats.SUBARRAY_ENDS_AFTER_ARRAY_END, begin + length, values.length, true);
        }
        System.arraycopy(values, begin, this.storedData = new double[length], 0, length);
    }
    
    public double evaluate() throws MathIllegalArgumentException {
        return this.evaluate(this.storedData);
    }
    
    public double evaluate(final double[] values) throws MathIllegalArgumentException {
        this.test(values, 0, 0);
        return this.evaluate(values, 0, values.length);
    }
    
    public abstract double evaluate(final double[] p0, final int p1, final int p2) throws MathIllegalArgumentException;
    
    public abstract UnivariateStatistic copy();
    
    protected boolean test(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        return this.test(values, begin, length, false);
    }
    
    protected boolean test(final double[] values, final int begin, final int length, final boolean allowEmpty) throws MathIllegalArgumentException {
        if (values == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY, new Object[0]);
        }
        if (begin < 0) {
            throw new NotPositiveException(LocalizedFormats.START_POSITION, begin);
        }
        if (length < 0) {
            throw new NotPositiveException(LocalizedFormats.LENGTH, length);
        }
        if (begin + length > values.length) {
            throw new NumberIsTooLargeException(LocalizedFormats.SUBARRAY_ENDS_AFTER_ARRAY_END, begin + length, values.length, true);
        }
        return length != 0 || allowEmpty;
    }
    
    protected boolean test(final double[] values, final double[] weights, final int begin, final int length) throws MathIllegalArgumentException {
        return this.test(values, weights, begin, length, false);
    }
    
    protected boolean test(final double[] values, final double[] weights, final int begin, final int length, final boolean allowEmpty) throws MathIllegalArgumentException {
        if (weights == null || values == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY, new Object[0]);
        }
        if (weights.length != values.length) {
            throw new DimensionMismatchException(weights.length, values.length);
        }
        boolean containsPositiveWeight = false;
        for (int i = begin; i < begin + length; ++i) {
            if (Double.isNaN(weights[i])) {
                throw new MathIllegalArgumentException(LocalizedFormats.NAN_ELEMENT_AT_INDEX, new Object[] { i });
            }
            if (Double.isInfinite(weights[i])) {
                throw new MathIllegalArgumentException(LocalizedFormats.INFINITE_ARRAY_ELEMENT, new Object[] { weights[i], i });
            }
            if (weights[i] < 0.0) {
                throw new MathIllegalArgumentException(LocalizedFormats.NEGATIVE_ELEMENT_AT_INDEX, new Object[] { i, weights[i] });
            }
            if (!containsPositiveWeight && weights[i] > 0.0) {
                containsPositiveWeight = true;
            }
        }
        if (!containsPositiveWeight) {
            throw new MathIllegalArgumentException(LocalizedFormats.WEIGHT_AT_LEAST_ONE_NON_ZERO, new Object[0]);
        }
        return this.test(values, begin, length, allowEmpty);
    }
}
