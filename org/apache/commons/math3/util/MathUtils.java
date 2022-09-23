// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NotFiniteNumberException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.util.Arrays;

public final class MathUtils
{
    public static final double TWO_PI = 6.283185307179586;
    
    private MathUtils() {
    }
    
    public static int hash(final double value) {
        return new Double(value).hashCode();
    }
    
    public static int hash(final double[] value) {
        return Arrays.hashCode(value);
    }
    
    public static double normalizeAngle(final double a, final double center) {
        return a - 6.283185307179586 * FastMath.floor((a + 3.141592653589793 - center) / 6.283185307179586);
    }
    
    public static double reduce(final double a, final double period, final double offset) {
        final double p = FastMath.abs(period);
        return a - p * FastMath.floor((a - offset) / p) - offset;
    }
    
    public static byte copySign(final byte magnitude, final byte sign) throws MathArithmeticException {
        if ((magnitude >= 0 && sign >= 0) || (magnitude < 0 && sign < 0)) {
            return magnitude;
        }
        if (sign >= 0 && magnitude == -128) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW, new Object[0]);
        }
        return (byte)(-magnitude);
    }
    
    public static short copySign(final short magnitude, final short sign) throws MathArithmeticException {
        if ((magnitude >= 0 && sign >= 0) || (magnitude < 0 && sign < 0)) {
            return magnitude;
        }
        if (sign >= 0 && magnitude == -32768) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW, new Object[0]);
        }
        return (short)(-magnitude);
    }
    
    public static int copySign(final int magnitude, final int sign) throws MathArithmeticException {
        if ((magnitude >= 0 && sign >= 0) || (magnitude < 0 && sign < 0)) {
            return magnitude;
        }
        if (sign >= 0 && magnitude == Integer.MIN_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW, new Object[0]);
        }
        return -magnitude;
    }
    
    public static long copySign(final long magnitude, final long sign) throws MathArithmeticException {
        if ((magnitude >= 0L && sign >= 0L) || (magnitude < 0L && sign < 0L)) {
            return magnitude;
        }
        if (sign >= 0L && magnitude == Long.MIN_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW, new Object[0]);
        }
        return -magnitude;
    }
    
    public static void checkFinite(final double x) throws NotFiniteNumberException {
        if (Double.isInfinite(x) || Double.isNaN(x)) {
            throw new NotFiniteNumberException(x, new Object[0]);
        }
    }
    
    public static void checkFinite(final double[] val) throws NotFiniteNumberException {
        for (int i = 0; i < val.length; ++i) {
            final double x = val[i];
            if (Double.isInfinite(x) || Double.isNaN(x)) {
                throw new NotFiniteNumberException(LocalizedFormats.ARRAY_ELEMENT, x, new Object[] { i });
            }
        }
    }
    
    public static void checkNotNull(final Object o, final Localizable pattern, final Object... args) throws NullArgumentException {
        if (o == null) {
            throw new NullArgumentException(pattern, args);
        }
    }
    
    public static void checkNotNull(final Object o) throws NullArgumentException {
        if (o == null) {
            throw new NullArgumentException();
        }
    }
}
