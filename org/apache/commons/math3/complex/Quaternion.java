// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.complex;

import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.DimensionMismatchException;
import java.io.Serializable;

public final class Quaternion implements Serializable
{
    public static final Quaternion IDENTITY;
    public static final Quaternion ZERO;
    public static final Quaternion I;
    public static final Quaternion J;
    public static final Quaternion K;
    private static final long serialVersionUID = 20092012L;
    private final double q0;
    private final double q1;
    private final double q2;
    private final double q3;
    
    public Quaternion(final double a, final double b, final double c, final double d) {
        this.q0 = a;
        this.q1 = b;
        this.q2 = c;
        this.q3 = d;
    }
    
    public Quaternion(final double scalar, final double[] v) throws DimensionMismatchException {
        if (v.length != 3) {
            throw new DimensionMismatchException(v.length, 3);
        }
        this.q0 = scalar;
        this.q1 = v[0];
        this.q2 = v[1];
        this.q3 = v[2];
    }
    
    public Quaternion(final double[] v) {
        this(0.0, v);
    }
    
    public Quaternion getConjugate() {
        return new Quaternion(this.q0, -this.q1, -this.q2, -this.q3);
    }
    
    public static Quaternion multiply(final Quaternion q1, final Quaternion q2) {
        final double q1a = q1.getQ0();
        final double q1b = q1.getQ1();
        final double q1c = q1.getQ2();
        final double q1d = q1.getQ3();
        final double q2a = q2.getQ0();
        final double q2b = q2.getQ1();
        final double q2c = q2.getQ2();
        final double q2d = q2.getQ3();
        final double w = q1a * q2a - q1b * q2b - q1c * q2c - q1d * q2d;
        final double x = q1a * q2b + q1b * q2a + q1c * q2d - q1d * q2c;
        final double y = q1a * q2c - q1b * q2d + q1c * q2a + q1d * q2b;
        final double z = q1a * q2d + q1b * q2c - q1c * q2b + q1d * q2a;
        return new Quaternion(w, x, y, z);
    }
    
    public Quaternion multiply(final Quaternion q) {
        return multiply(this, q);
    }
    
    public static Quaternion add(final Quaternion q1, final Quaternion q2) {
        return new Quaternion(q1.getQ0() + q2.getQ0(), q1.getQ1() + q2.getQ1(), q1.getQ2() + q2.getQ2(), q1.getQ3() + q2.getQ3());
    }
    
    public Quaternion add(final Quaternion q) {
        return add(this, q);
    }
    
    public static Quaternion subtract(final Quaternion q1, final Quaternion q2) {
        return new Quaternion(q1.getQ0() - q2.getQ0(), q1.getQ1() - q2.getQ1(), q1.getQ2() - q2.getQ2(), q1.getQ3() - q2.getQ3());
    }
    
    public Quaternion subtract(final Quaternion q) {
        return subtract(this, q);
    }
    
    public static double dotProduct(final Quaternion q1, final Quaternion q2) {
        return q1.getQ0() * q2.getQ0() + q1.getQ1() * q2.getQ1() + q1.getQ2() * q2.getQ2() + q1.getQ3() * q2.getQ3();
    }
    
    public double dotProduct(final Quaternion q) {
        return dotProduct(this, q);
    }
    
    public double getNorm() {
        return FastMath.sqrt(this.q0 * this.q0 + this.q1 * this.q1 + this.q2 * this.q2 + this.q3 * this.q3);
    }
    
    public Quaternion normalize() {
        final double norm = this.getNorm();
        if (norm < Precision.SAFE_MIN) {
            throw new ZeroException(LocalizedFormats.NORM, new Object[] { norm });
        }
        return new Quaternion(this.q0 / norm, this.q1 / norm, this.q2 / norm, this.q3 / norm);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Quaternion) {
            final Quaternion q = (Quaternion)other;
            return this.q0 == q.getQ0() && this.q1 == q.getQ1() && this.q2 == q.getQ2() && this.q3 == q.getQ3();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        for (final double comp : new double[] { this.q0, this.q1, this.q2, this.q3 }) {
            final int c = MathUtils.hash(comp);
            result = 31 * result + c;
        }
        return result;
    }
    
    public boolean equals(final Quaternion q, final double eps) {
        return Precision.equals(this.q0, q.getQ0(), eps) && Precision.equals(this.q1, q.getQ1(), eps) && Precision.equals(this.q2, q.getQ2(), eps) && Precision.equals(this.q3, q.getQ3(), eps);
    }
    
    public boolean isUnitQuaternion(final double eps) {
        return Precision.equals(this.getNorm(), 1.0, eps);
    }
    
    public boolean isPureQuaternion(final double eps) {
        return FastMath.abs(this.getQ0()) <= eps;
    }
    
    public Quaternion getPositivePolarForm() {
        if (this.getQ0() < 0.0) {
            final Quaternion unitQ = this.normalize();
            return new Quaternion(-unitQ.getQ0(), -unitQ.getQ1(), -unitQ.getQ2(), -unitQ.getQ3());
        }
        return this.normalize();
    }
    
    public Quaternion getInverse() {
        final double squareNorm = this.q0 * this.q0 + this.q1 * this.q1 + this.q2 * this.q2 + this.q3 * this.q3;
        if (squareNorm < Precision.SAFE_MIN) {
            throw new ZeroException(LocalizedFormats.NORM, new Object[] { squareNorm });
        }
        return new Quaternion(this.q0 / squareNorm, -this.q1 / squareNorm, -this.q2 / squareNorm, -this.q3 / squareNorm);
    }
    
    public double getQ0() {
        return this.q0;
    }
    
    public double getQ1() {
        return this.q1;
    }
    
    public double getQ2() {
        return this.q2;
    }
    
    public double getQ3() {
        return this.q3;
    }
    
    public double getScalarPart() {
        return this.getQ0();
    }
    
    public double[] getVectorPart() {
        return new double[] { this.getQ1(), this.getQ2(), this.getQ3() };
    }
    
    public Quaternion multiply(final double alpha) {
        return new Quaternion(alpha * this.q0, alpha * this.q1, alpha * this.q2, alpha * this.q3);
    }
    
    @Override
    public String toString() {
        final String sp = " ";
        final StringBuilder s = new StringBuilder();
        s.append("[").append(this.q0).append(" ").append(this.q1).append(" ").append(this.q2).append(" ").append(this.q3).append("]");
        return s.toString();
    }
    
    static {
        IDENTITY = new Quaternion(1.0, 0.0, 0.0, 0.0);
        ZERO = new Quaternion(0.0, 0.0, 0.0, 0.0);
        I = new Quaternion(0.0, 1.0, 0.0, 0.0);
        J = new Quaternion(0.0, 0.0, 1.0, 0.0);
        K = new Quaternion(0.0, 0.0, 0.0, 1.0);
    }
}
