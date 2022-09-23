// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;

public class Decimal64 extends Number implements FieldElement<Decimal64>, Comparable<Decimal64>
{
    public static final Decimal64 ZERO;
    public static final Decimal64 ONE;
    public static final Decimal64 NEGATIVE_INFINITY;
    public static final Decimal64 POSITIVE_INFINITY;
    public static final Decimal64 NAN;
    private static final long serialVersionUID = 20120227L;
    private final double value;
    
    public Decimal64(final double x) {
        this.value = x;
    }
    
    public Field<Decimal64> getField() {
        return Decimal64Field.getInstance();
    }
    
    public Decimal64 add(final Decimal64 a) {
        return new Decimal64(this.value + a.value);
    }
    
    public Decimal64 subtract(final Decimal64 a) {
        return new Decimal64(this.value - a.value);
    }
    
    public Decimal64 negate() {
        return new Decimal64(-this.value);
    }
    
    public Decimal64 multiply(final Decimal64 a) {
        return new Decimal64(this.value * a.value);
    }
    
    public Decimal64 multiply(final int n) {
        return new Decimal64(n * this.value);
    }
    
    public Decimal64 divide(final Decimal64 a) {
        return new Decimal64(this.value / a.value);
    }
    
    public Decimal64 reciprocal() {
        return new Decimal64(1.0 / this.value);
    }
    
    @Override
    public byte byteValue() {
        return (byte)this.value;
    }
    
    @Override
    public short shortValue() {
        return (short)this.value;
    }
    
    @Override
    public int intValue() {
        return (int)this.value;
    }
    
    @Override
    public long longValue() {
        return (long)this.value;
    }
    
    @Override
    public float floatValue() {
        return (float)this.value;
    }
    
    @Override
    public double doubleValue() {
        return this.value;
    }
    
    public int compareTo(final Decimal64 o) {
        return Double.compare(this.value, o.value);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Decimal64) {
            final Decimal64 that = (Decimal64)obj;
            return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(that.value);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        final long v = Double.doubleToLongBits(this.value);
        return (int)(v ^ v >>> 32);
    }
    
    @Override
    public String toString() {
        return Double.toString(this.value);
    }
    
    public boolean isInfinite() {
        return Double.isInfinite(this.value);
    }
    
    public boolean isNaN() {
        return Double.isNaN(this.value);
    }
    
    static {
        ZERO = new Decimal64(0.0);
        ONE = new Decimal64(1.0);
        NEGATIVE_INFINITY = new Decimal64(Double.NEGATIVE_INFINITY);
        POSITIVE_INFINITY = new Decimal64(Double.POSITIVE_INFINITY);
        NAN = new Decimal64(Double.NaN);
    }
}
