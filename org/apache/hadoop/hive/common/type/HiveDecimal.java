// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.type;

import java.math.RoundingMode;
import java.math.BigInteger;
import java.math.BigDecimal;

public class HiveDecimal implements Comparable<HiveDecimal>
{
    public static final int MAX_PRECISION = 38;
    public static final int MAX_SCALE = 38;
    public static final int USER_DEFAULT_PRECISION = 10;
    public static final int USER_DEFAULT_SCALE = 0;
    public static final int SYSTEM_DEFAULT_PRECISION = 38;
    public static final int SYSTEM_DEFAULT_SCALE = 18;
    public static final HiveDecimal ZERO;
    public static final HiveDecimal ONE;
    public static final int ROUND_FLOOR = 3;
    public static final int ROUND_CEILING = 2;
    public static final int ROUND_HALF_UP = 4;
    private BigDecimal bd;
    
    private HiveDecimal(final BigDecimal bd) {
        this.bd = BigDecimal.ZERO;
        this.bd = bd;
    }
    
    public static HiveDecimal create(final BigDecimal b) {
        return create(b, true);
    }
    
    public static HiveDecimal create(final BigDecimal b, final boolean allowRounding) {
        final BigDecimal bd = normalize(b, allowRounding);
        return (bd == null) ? null : new HiveDecimal(bd);
    }
    
    public static HiveDecimal create(final BigInteger unscaled, final int scale) {
        final BigDecimal bd = normalize(new BigDecimal(unscaled, scale), true);
        return (bd == null) ? null : new HiveDecimal(bd);
    }
    
    public static HiveDecimal create(final String dec) {
        BigDecimal bd;
        try {
            bd = new BigDecimal(dec);
        }
        catch (NumberFormatException ex) {
            return null;
        }
        bd = normalize(bd, true);
        return (bd == null) ? null : new HiveDecimal(bd);
    }
    
    public static HiveDecimal create(final BigInteger bi) {
        final BigDecimal bd = normalize(new BigDecimal(bi), true);
        return (bd == null) ? null : new HiveDecimal(bd);
    }
    
    public static HiveDecimal create(final int i) {
        return new HiveDecimal(new BigDecimal(i));
    }
    
    public static HiveDecimal create(final long l) {
        return new HiveDecimal(new BigDecimal(l));
    }
    
    @Override
    public String toString() {
        return this.bd.toPlainString();
    }
    
    public HiveDecimal setScale(final int i) {
        return new HiveDecimal(this.bd.setScale(i, RoundingMode.HALF_UP));
    }
    
    @Override
    public int compareTo(final HiveDecimal dec) {
        return this.bd.compareTo(dec.bd);
    }
    
    @Override
    public int hashCode() {
        return this.bd.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj.getClass() == this.getClass() && this.bd.equals(((HiveDecimal)obj).bd);
    }
    
    public int scale() {
        return this.bd.scale();
    }
    
    public int precision() {
        final int bdPrecision = this.bd.precision();
        final int bdScale = this.bd.scale();
        if (bdPrecision < bdScale) {
            return bdScale;
        }
        return bdPrecision;
    }
    
    public int intValue() {
        return this.bd.intValue();
    }
    
    public double doubleValue() {
        return this.bd.doubleValue();
    }
    
    public long longValue() {
        return this.bd.longValue();
    }
    
    public short shortValue() {
        return this.bd.shortValue();
    }
    
    public float floatValue() {
        return this.bd.floatValue();
    }
    
    public BigDecimal bigDecimalValue() {
        return this.bd;
    }
    
    public byte byteValue() {
        return this.bd.byteValue();
    }
    
    public HiveDecimal setScale(final int adjustedScale, final int rm) {
        return create(this.bd.setScale(adjustedScale, rm));
    }
    
    public HiveDecimal subtract(final HiveDecimal dec) {
        return create(this.bd.subtract(dec.bd));
    }
    
    public HiveDecimal multiply(final HiveDecimal dec) {
        return create(this.bd.multiply(dec.bd), false);
    }
    
    public BigInteger unscaledValue() {
        return this.bd.unscaledValue();
    }
    
    public HiveDecimal scaleByPowerOfTen(final int n) {
        return create(this.bd.scaleByPowerOfTen(n));
    }
    
    public HiveDecimal abs() {
        return create(this.bd.abs());
    }
    
    public HiveDecimal negate() {
        return create(this.bd.negate());
    }
    
    public HiveDecimal add(final HiveDecimal dec) {
        return create(this.bd.add(dec.bd));
    }
    
    public HiveDecimal pow(final int n) {
        final BigDecimal result = normalize(this.bd.pow(n), false);
        return (result == null) ? null : new HiveDecimal(result);
    }
    
    public HiveDecimal remainder(final HiveDecimal dec) {
        return create(this.bd.remainder(dec.bd));
    }
    
    public HiveDecimal divide(final HiveDecimal dec) {
        return create(this.bd.divide(dec.bd, 38, RoundingMode.HALF_UP), true);
    }
    
    public int signum() {
        return this.bd.signum();
    }
    
    private static BigDecimal trim(BigDecimal d) {
        if (d.compareTo(BigDecimal.ZERO) == 0) {
            d = BigDecimal.ZERO;
        }
        else {
            d = d.stripTrailingZeros();
            if (d.scale() < 0) {
                d = d.setScale(0);
            }
        }
        return d;
    }
    
    private static BigDecimal normalize(BigDecimal bd, final boolean allowRounding) {
        if (bd == null) {
            return null;
        }
        bd = trim(bd);
        final int intDigits = bd.precision() - bd.scale();
        if (intDigits > 38) {
            return null;
        }
        final int maxScale = Math.min(38, Math.min(38 - intDigits, bd.scale()));
        if (bd.scale() > maxScale) {
            if (allowRounding) {
                bd = bd.setScale(maxScale, RoundingMode.HALF_UP);
                bd = trim(bd);
            }
            else {
                bd = null;
            }
        }
        return bd;
    }
    
    public static BigDecimal enforcePrecisionScale(BigDecimal bd, final int maxPrecision, final int maxScale) {
        if (bd == null) {
            return null;
        }
        bd = trim(bd);
        if (bd.scale() > maxScale) {
            bd = bd.setScale(maxScale, RoundingMode.HALF_UP);
        }
        final int maxIntDigits = maxPrecision - maxScale;
        final int intDigits = bd.precision() - bd.scale();
        if (intDigits > maxIntDigits) {
            return null;
        }
        return bd;
    }
    
    public static HiveDecimal enforcePrecisionScale(final HiveDecimal dec, final int maxPrecision, final int maxScale) {
        if (dec == null) {
            return null;
        }
        final BigDecimal bd = enforcePrecisionScale(dec.bd, maxPrecision, maxScale);
        if (bd == null) {
            return null;
        }
        return create(bd);
    }
    
    static {
        ZERO = new HiveDecimal(BigDecimal.ZERO);
        ONE = new HiveDecimal(BigDecimal.ONE);
    }
}
