// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathArithmeticException;
import java.math.BigDecimal;

public class Precision
{
    public static final double EPSILON;
    public static final double SAFE_MIN;
    private static final long EXPONENT_OFFSET = 1023L;
    private static final long SGN_MASK = Long.MIN_VALUE;
    private static final int SGN_MASK_FLOAT = Integer.MIN_VALUE;
    
    private Precision() {
    }
    
    public static int compareTo(final double x, final double y, final double eps) {
        if (equals(x, y, eps)) {
            return 0;
        }
        if (x < y) {
            return -1;
        }
        return 1;
    }
    
    public static int compareTo(final double x, final double y, final int maxUlps) {
        if (equals(x, y, maxUlps)) {
            return 0;
        }
        if (x < y) {
            return -1;
        }
        return 1;
    }
    
    public static boolean equals(final float x, final float y) {
        return equals(x, y, 1);
    }
    
    public static boolean equalsIncludingNaN(final float x, final float y) {
        return (Float.isNaN(x) && Float.isNaN(y)) || equals(x, y, 1);
    }
    
    public static boolean equals(final float x, final float y, final float eps) {
        return equals(x, y, 1) || FastMath.abs(y - x) <= eps;
    }
    
    public static boolean equalsIncludingNaN(final float x, final float y, final float eps) {
        return equalsIncludingNaN(x, y) || FastMath.abs(y - x) <= eps;
    }
    
    public static boolean equals(final float x, final float y, final int maxUlps) {
        int xInt = Float.floatToIntBits(x);
        int yInt = Float.floatToIntBits(y);
        if (xInt < 0) {
            xInt = Integer.MIN_VALUE - xInt;
        }
        if (yInt < 0) {
            yInt = Integer.MIN_VALUE - yInt;
        }
        final boolean isEqual = FastMath.abs(xInt - yInt) <= maxUlps;
        return isEqual && !Float.isNaN(x) && !Float.isNaN(y);
    }
    
    public static boolean equalsIncludingNaN(final float x, final float y, final int maxUlps) {
        return (Float.isNaN(x) && Float.isNaN(y)) || equals(x, y, maxUlps);
    }
    
    public static boolean equals(final double x, final double y) {
        return equals(x, y, 1);
    }
    
    public static boolean equalsIncludingNaN(final double x, final double y) {
        return (Double.isNaN(x) && Double.isNaN(y)) || equals(x, y, 1);
    }
    
    public static boolean equals(final double x, final double y, final double eps) {
        return equals(x, y, 1) || FastMath.abs(y - x) <= eps;
    }
    
    public static boolean equalsWithRelativeTolerance(final double x, final double y, final double eps) {
        if (equals(x, y, 1)) {
            return true;
        }
        final double absoluteMax = FastMath.max(FastMath.abs(x), FastMath.abs(y));
        final double relativeDifference = FastMath.abs((x - y) / absoluteMax);
        return relativeDifference <= eps;
    }
    
    public static boolean equalsIncludingNaN(final double x, final double y, final double eps) {
        return equalsIncludingNaN(x, y) || FastMath.abs(y - x) <= eps;
    }
    
    public static boolean equals(final double x, final double y, final int maxUlps) {
        long xInt = Double.doubleToLongBits(x);
        long yInt = Double.doubleToLongBits(y);
        if (xInt < 0L) {
            xInt = Long.MIN_VALUE - xInt;
        }
        if (yInt < 0L) {
            yInt = Long.MIN_VALUE - yInt;
        }
        final boolean isEqual = FastMath.abs(xInt - yInt) <= maxUlps;
        return isEqual && !Double.isNaN(x) && !Double.isNaN(y);
    }
    
    public static boolean equalsIncludingNaN(final double x, final double y, final int maxUlps) {
        return (Double.isNaN(x) && Double.isNaN(y)) || equals(x, y, maxUlps);
    }
    
    public static double round(final double x, final int scale) {
        return round(x, scale, 4);
    }
    
    public static double round(final double x, final int scale, final int roundingMethod) {
        try {
            return new BigDecimal(Double.toString(x)).setScale(scale, roundingMethod).doubleValue();
        }
        catch (NumberFormatException ex) {
            if (Double.isInfinite(x)) {
                return x;
            }
            return Double.NaN;
        }
    }
    
    public static float round(final float x, final int scale) {
        return round(x, scale, 4);
    }
    
    public static float round(final float x, final int scale, final int roundingMethod) throws MathArithmeticException, MathIllegalArgumentException {
        final float sign = FastMath.copySign(1.0f, x);
        final float factor = (float)FastMath.pow(10.0, scale) * sign;
        return (float)roundUnscaled(x * factor, sign, roundingMethod) / factor;
    }
    
    private static double roundUnscaled(double unscaled, final double sign, final int roundingMethod) throws MathArithmeticException, MathIllegalArgumentException {
        switch (roundingMethod) {
            case 2: {
                if (sign == -1.0) {
                    unscaled = FastMath.floor(FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY));
                    break;
                }
                unscaled = FastMath.ceil(FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY));
                break;
            }
            case 1: {
                unscaled = FastMath.floor(FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY));
                break;
            }
            case 3: {
                if (sign == -1.0) {
                    unscaled = FastMath.ceil(FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY));
                    break;
                }
                unscaled = FastMath.floor(FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY));
                break;
            }
            case 5: {
                unscaled = FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY);
                final double fraction = unscaled - FastMath.floor(unscaled);
                if (fraction > 0.5) {
                    unscaled = FastMath.ceil(unscaled);
                    break;
                }
                unscaled = FastMath.floor(unscaled);
                break;
            }
            case 6: {
                final double fraction = unscaled - FastMath.floor(unscaled);
                if (fraction > 0.5) {
                    unscaled = FastMath.ceil(unscaled);
                    break;
                }
                if (fraction < 0.5) {
                    unscaled = FastMath.floor(unscaled);
                    break;
                }
                if (FastMath.floor(unscaled) / 2.0 == FastMath.floor(Math.floor(unscaled) / 2.0)) {
                    unscaled = FastMath.floor(unscaled);
                    break;
                }
                unscaled = FastMath.ceil(unscaled);
                break;
            }
            case 4: {
                unscaled = FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY);
                final double fraction = unscaled - FastMath.floor(unscaled);
                if (fraction >= 0.5) {
                    unscaled = FastMath.ceil(unscaled);
                    break;
                }
                unscaled = FastMath.floor(unscaled);
                break;
            }
            case 7: {
                if (unscaled != FastMath.floor(unscaled)) {
                    throw new MathArithmeticException();
                }
                break;
            }
            case 0: {
                unscaled = FastMath.ceil(FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY));
                break;
            }
            default: {
                throw new MathIllegalArgumentException(LocalizedFormats.INVALID_ROUNDING_METHOD, new Object[] { roundingMethod, "ROUND_CEILING", 2, "ROUND_DOWN", 1, "ROUND_FLOOR", 3, "ROUND_HALF_DOWN", 5, "ROUND_HALF_EVEN", 6, "ROUND_HALF_UP", 4, "ROUND_UNNECESSARY", 7, "ROUND_UP", 0 });
            }
        }
        return unscaled;
    }
    
    public static double representableDelta(final double x, final double originalDelta) {
        return x + originalDelta - x;
    }
    
    static {
        EPSILON = Double.longBitsToDouble(4368491638549381120L);
        SAFE_MIN = Double.longBitsToDouble(4503599627370496L);
    }
}
