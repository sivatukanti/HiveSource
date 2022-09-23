// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.type;

import org.apache.hive.common.util.Decimal128FastBuffer;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.nio.IntBuffer;

public final class Decimal128 extends Number implements Comparable<Decimal128>
{
    public static final short MAX_SCALE = 38;
    public static final short MIN_SCALE = 0;
    public static final Decimal128 ONE;
    public static final Decimal128 MAX_VALUE;
    public static final Decimal128 MIN_VALUE;
    private static final long serialVersionUID = 1L;
    private UnsignedInt128 unscaledValue;
    private short scale;
    private byte signum;
    private int[] tmpArray;
    
    public static int getIntsPerElement(final int precision) {
        return UnsignedInt128.getIntsPerElement(precision) + 1;
    }
    
    public Decimal128() {
        this.tmpArray = new int[2];
        this.unscaledValue = new UnsignedInt128();
        this.scale = 0;
        this.signum = 0;
    }
    
    public Decimal128(final Decimal128 o) {
        this.tmpArray = new int[2];
        this.unscaledValue = new UnsignedInt128(o.unscaledValue);
        this.scale = o.scale;
        this.signum = o.signum;
    }
    
    public Decimal128(final double val, final short scale) {
        this();
        this.update(val, scale);
    }
    
    public Decimal128(final UnsignedInt128 unscaledVal, final short scale, final boolean negative) {
        this.tmpArray = new int[2];
        checkScaleRange(scale);
        this.unscaledValue = new UnsignedInt128(unscaledVal);
        this.scale = scale;
        if (this.unscaledValue.isZero()) {
            this.signum = 0;
        }
        else {
            this.signum = (byte)(negative ? -1 : 1);
        }
        this.unscaledValue.throwIfExceedsTenToThirtyEight();
    }
    
    public Decimal128(final long val) {
        this(val, (short)0);
    }
    
    public Decimal128(final long val, final short scale) {
        this();
        this.update(val, scale);
    }
    
    public Decimal128(final String str, final short scale) {
        this();
        this.update(str, scale);
    }
    
    public Decimal128(final char[] str, final int offset, final int length, final short scale) {
        this();
        this.update(str, offset, length, scale);
    }
    
    public Decimal128 zeroClear() {
        this.unscaledValue.zeroClear();
        this.signum = 0;
        return this;
    }
    
    public boolean isZero() {
        assert this.signum != 0 && !this.unscaledValue.isZero();
        return this.signum == 0;
    }
    
    public Decimal128 update(final Decimal128 o) {
        this.unscaledValue.update(o.unscaledValue);
        this.scale = o.scale;
        this.signum = o.signum;
        return this;
    }
    
    public Decimal128 update(final Decimal128 o, final short scale) {
        this.update(o);
        this.changeScaleDestructive(scale);
        return this;
    }
    
    public Decimal128 update(final long val) {
        return this.update(val, (short)0);
    }
    
    public Decimal128 update(final long val, final short scale) {
        this.scale = 0;
        if (val < 0L) {
            this.unscaledValue.update(-val);
            this.signum = -1;
        }
        else if (val == 0L) {
            this.zeroClear();
        }
        else {
            this.unscaledValue.update(val);
            this.signum = 1;
        }
        if (scale != 0) {
            this.changeScaleDestructive(scale);
        }
        return this;
    }
    
    public Decimal128 update(final double val, final short scale) {
        if (Double.isInfinite(val) || Double.isNaN(val)) {
            throw new NumberFormatException("Infinite or NaN");
        }
        checkScaleRange(scale);
        this.scale = scale;
        final long valBits = Double.doubleToLongBits(val);
        final byte sign = (byte)((valBits >> 63 == 0L) ? 1 : -1);
        short exponent = (short)(valBits >> 52 & 0x7FFL);
        long significand = (exponent == 0) ? ((valBits & 0xFFFFFFFFFFFFFL) << 1) : ((valBits & 0xFFFFFFFFFFFFFL) | 0x10000000000000L);
        exponent -= 1075;
        if (significand == 0L) {
            this.zeroClear();
            return this;
        }
        this.signum = sign;
        while ((significand & 0x1L) == 0x0L) {
            significand >>= 1;
            ++exponent;
        }
        this.unscaledValue.update(significand);
        if (exponent >= 0) {
            this.unscaledValue.shiftLeftDestructiveCheckOverflow(exponent);
            this.unscaledValue.scaleUpTenDestructive(scale);
        }
        else {
            final short twoScaleDown = (short)(-exponent);
            if (scale >= twoScaleDown) {
                this.unscaledValue.shiftLeftDestructiveCheckOverflow(scale - twoScaleDown);
                this.unscaledValue.scaleUpFiveDestructive(scale);
            }
            else {
                this.unscaledValue.multiplyShiftDestructive(SqlMathUtil.POWER_FIVES_INT128[scale], (short)(twoScaleDown - scale));
            }
        }
        return this;
    }
    
    public Decimal128 update(final IntBuffer buf, final int precision) {
        final int scaleAndSignum = buf.get();
        this.scale = (short)(scaleAndSignum >> 16);
        this.signum = (byte)(scaleAndSignum & 0xFF);
        this.unscaledValue.update(buf, precision);
        assert this.signum == 0 == this.unscaledValue.isZero();
        return this;
    }
    
    public Decimal128 update128(final IntBuffer buf) {
        final int scaleAndSignum = buf.get();
        this.scale = (short)(scaleAndSignum >> 16);
        this.signum = (byte)(scaleAndSignum & 0xFF);
        this.unscaledValue.update128(buf);
        assert this.signum == 0 == this.unscaledValue.isZero();
        return this;
    }
    
    public Decimal128 update96(final IntBuffer buf) {
        final int scaleAndSignum = buf.get();
        this.scale = (short)(scaleAndSignum >> 16);
        this.signum = (byte)(scaleAndSignum & 0xFF);
        this.unscaledValue.update96(buf);
        assert this.signum == 0 == this.unscaledValue.isZero();
        return this;
    }
    
    public Decimal128 update64(final IntBuffer buf) {
        final int scaleAndSignum = buf.get();
        this.scale = (short)(scaleAndSignum >> 16);
        this.signum = (byte)(scaleAndSignum & 0xFF);
        this.unscaledValue.update64(buf);
        assert this.signum == 0 == this.unscaledValue.isZero();
        return this;
    }
    
    public Decimal128 update32(final IntBuffer buf) {
        final int scaleAndSignum = buf.get();
        this.scale = (short)(scaleAndSignum >> 16);
        this.signum = (byte)(scaleAndSignum & 0xFF);
        this.unscaledValue.update32(buf);
        assert this.signum == 0 == this.unscaledValue.isZero();
        return this;
    }
    
    public Decimal128 update(final int[] array, final int offset, final int precision) {
        final int scaleAndSignum = array[offset];
        this.scale = (short)(scaleAndSignum >> 16);
        this.signum = (byte)(scaleAndSignum & 0xFF);
        this.unscaledValue.update(array, offset + 1, precision);
        return this;
    }
    
    public Decimal128 update128(final int[] array, final int offset) {
        final int scaleAndSignum = array[offset];
        this.scale = (short)(scaleAndSignum >> 16);
        this.signum = (byte)(scaleAndSignum & 0xFF);
        this.unscaledValue.update128(array, offset + 1);
        return this;
    }
    
    public Decimal128 update96(final int[] array, final int offset) {
        final int scaleAndSignum = array[offset];
        this.scale = (short)(scaleAndSignum >> 16);
        this.signum = (byte)(scaleAndSignum & 0xFF);
        this.unscaledValue.update96(array, offset + 1);
        return this;
    }
    
    public Decimal128 update64(final int[] array, final int offset) {
        final int scaleAndSignum = array[offset];
        this.scale = (short)(scaleAndSignum >> 16);
        this.signum = (byte)(scaleAndSignum & 0xFF);
        this.unscaledValue.update64(array, offset + 1);
        return this;
    }
    
    public Decimal128 update32(final int[] array, final int offset) {
        final int scaleAndSignum = array[offset];
        this.scale = (short)(scaleAndSignum >> 16);
        this.signum = (byte)(scaleAndSignum & 0xFF);
        this.unscaledValue.update32(array, offset + 1);
        return this;
    }
    
    public Decimal128 update(final BigDecimal bigDecimal) {
        return this.update(bigDecimal.unscaledValue(), (short)bigDecimal.scale());
    }
    
    public Decimal128 update(final BigInteger bigInt, final short scale) {
        this.scale = scale;
        this.signum = (byte)bigInt.compareTo(BigInteger.ZERO);
        if (this.signum == 0) {
            this.update(0L);
        }
        else if (this.signum < 0) {
            this.unscaledValue.update(bigInt.negate());
        }
        else {
            this.unscaledValue.update(bigInt);
        }
        return this;
    }
    
    public Decimal128 update(final String str, final short scale) {
        return this.update(str.toCharArray(), 0, str.length(), scale);
    }
    
    public Decimal128 update(final char[] str, final int offset, final int length, final short scale) {
        final int end = offset + length;
        assert end <= str.length;
        int cursor = offset;
        boolean negative = false;
        if (str[cursor] == '+') {
            ++cursor;
        }
        else if (str[cursor] == '-') {
            negative = true;
            ++cursor;
        }
        while (cursor < end && str[cursor] == '0') {
            ++cursor;
        }
        this.scale = scale;
        this.zeroClear();
        if (cursor == end) {
            return this;
        }
        int accumulated = 0;
        int accumulatedCount = 0;
        boolean fractional = false;
        int fractionalDigits = 0;
        int exponent = 0;
        while (cursor < end) {
            if (str[cursor] == '.') {
                if (fractional) {
                    throw new NumberFormatException("Invalid string:" + new String(str, offset, length));
                }
                fractional = true;
            }
            else if (str[cursor] >= '0' && str[cursor] <= '9') {
                if (accumulatedCount == 9) {
                    this.unscaledValue.scaleUpTenDestructive((short)accumulatedCount);
                    this.unscaledValue.addDestructive(accumulated);
                    accumulated = 0;
                    accumulatedCount = 0;
                }
                final int digit = str[cursor] - '0';
                accumulated = accumulated * 10 + digit;
                ++accumulatedCount;
                if (fractional) {
                    ++fractionalDigits;
                }
            }
            else {
                if (str[cursor] != 'e' && str[cursor] != 'E') {
                    throw new NumberFormatException("Invalid string:" + new String(str, offset, length));
                }
                ++cursor;
                boolean exponentNagative = false;
                if (str[cursor] == '+') {
                    ++cursor;
                }
                else if (str[cursor] == '-') {
                    exponentNagative = true;
                    ++cursor;
                }
                while (cursor < end) {
                    if (str[cursor] >= '0' && str[cursor] <= '9') {
                        final int exponentDigit = str[cursor] - '0';
                        exponent *= 10;
                        exponent += exponentDigit;
                    }
                    ++cursor;
                }
                if (exponentNagative) {
                    exponent = -exponent;
                }
            }
            ++cursor;
        }
        if (accumulatedCount > 0) {
            this.unscaledValue.scaleUpTenDestructive((short)accumulatedCount);
            this.unscaledValue.addDestructive(accumulated);
        }
        final int scaleAdjust = scale - fractionalDigits + exponent;
        if (scaleAdjust > 0) {
            this.unscaledValue.scaleUpTenDestructive((short)scaleAdjust);
        }
        else if (scaleAdjust < 0) {
            this.unscaledValue.scaleDownTenDestructive((short)(-scaleAdjust));
        }
        this.signum = (byte)(this.unscaledValue.isZero() ? 0 : (negative ? -1 : 1));
        return this;
    }
    
    public int fastSerializeForHiveDecimal(final Decimal128FastBuffer scratch) {
        return this.unscaledValue.fastSerializeForHiveDecimal(scratch, this.signum);
    }
    
    public void serializeTo(final int[] array, final int offset, final int precision) {
        array[offset] = (this.scale << 16 | (this.signum & 0xFF));
        this.unscaledValue.serializeTo(array, offset + 1, precision);
    }
    
    public void serializeTo128(final int[] array, final int offset) {
        array[offset] = (this.scale << 16 | (this.signum & 0xFF));
        this.unscaledValue.serializeTo128(array, offset + 1);
    }
    
    public void serializeTo96(final int[] array, final int offset) {
        array[offset] = (this.scale << 16 | (this.signum & 0xFF));
        this.unscaledValue.serializeTo96(array, offset + 1);
    }
    
    public void serializeTo64(final int[] array, final int offset) {
        array[offset] = (this.scale << 16 | (this.signum & 0xFF));
        this.unscaledValue.serializeTo64(array, offset + 1);
    }
    
    public void serializeTo32(final int[] array, final int offset) {
        array[offset] = (this.scale << 16 | (this.signum & 0xFF));
        this.unscaledValue.serializeTo32(array, offset + 1);
    }
    
    public void serializeTo(final IntBuffer buf, final int precision) {
        buf.put(this.scale << 16 | (this.signum & 0xFF));
        this.unscaledValue.serializeTo(buf, precision);
    }
    
    public void serializeTo128(final IntBuffer buf) {
        buf.put(this.scale << 16 | (this.signum & 0xFF));
        this.unscaledValue.serializeTo128(buf);
    }
    
    public void serializeTo96(final IntBuffer buf) {
        buf.put(this.scale << 16 | (this.signum & 0xFF));
        this.unscaledValue.serializeTo96(buf);
    }
    
    public void serializeTo64(final IntBuffer buf) {
        buf.put(this.scale << 16 | (this.signum & 0xFF));
        this.unscaledValue.serializeTo64(buf);
    }
    
    public void serializeTo32(final IntBuffer buf) {
        buf.put(this.scale << 16 | (this.signum & 0xFF));
        this.unscaledValue.serializeTo32(buf);
    }
    
    public void changeScaleDestructive(final short scale) {
        if (scale == this.scale) {
            return;
        }
        checkScaleRange(scale);
        final short scaleDown = (short)(this.scale - scale);
        if (scaleDown > 0) {
            this.unscaledValue.scaleDownTenDestructive(scaleDown);
            if (this.unscaledValue.isZero()) {
                this.signum = 0;
            }
        }
        else if (scaleDown < 0) {
            this.unscaledValue.scaleUpTenDestructive((short)(-scaleDown));
        }
        this.scale = scale;
        this.unscaledValue.throwIfExceedsTenToThirtyEight();
    }
    
    public static void add(final Decimal128 left, final Decimal128 right, final Decimal128 result, final short scale) {
        result.update(left);
        result.addDestructive(right, scale);
    }
    
    public Decimal128 addDestructive(final Decimal128 right, final short scale) {
        this.changeScaleDestructive(scale);
        if (right.signum == 0) {
            return this;
        }
        if (this.signum == 0) {
            this.update(right);
            this.changeScaleDestructive(scale);
            return this;
        }
        final short rightScaleTen = (short)(scale - right.scale);
        if (this.signum == right.signum) {
            this.unscaledValue.addDestructiveScaleTen(right.unscaledValue, rightScaleTen);
        }
        else {
            final byte cmp = UnsignedInt128.differenceScaleTen(this.unscaledValue, right.unscaledValue, this.unscaledValue, rightScaleTen);
            if (cmp == 0) {
                this.signum = 0;
            }
            else if (cmp < 0) {
                this.signum = right.signum;
            }
        }
        this.unscaledValue.throwIfExceedsTenToThirtyEight();
        return this;
    }
    
    public static void subtract(final Decimal128 left, final Decimal128 right, final Decimal128 result, final short scale) {
        result.update(left);
        result.subtractDestructive(right, scale);
    }
    
    public Decimal128 subtractDestructive(final Decimal128 right, final short scale) {
        this.changeScaleDestructive(scale);
        if (right.signum == 0) {
            return this;
        }
        if (this.signum == 0) {
            this.update(right);
            this.changeScaleDestructive(scale);
            this.negateDestructive();
            return this;
        }
        final short rightScaleTen = (short)(scale - right.scale);
        if (this.signum != right.signum) {
            this.unscaledValue.addDestructiveScaleTen(right.unscaledValue, rightScaleTen);
        }
        else {
            final byte cmp = UnsignedInt128.differenceScaleTen(this.unscaledValue, right.unscaledValue, this.unscaledValue, rightScaleTen);
            if (cmp == 0) {
                this.signum = 0;
            }
            else if (cmp < 0) {
                this.signum = (byte)(-right.signum);
            }
        }
        this.unscaledValue.throwIfExceedsTenToThirtyEight();
        return this;
    }
    
    public static void multiply(final Decimal128 left, final Decimal128 right, final Decimal128 result, final short scale) {
        if (result == left || result == right) {
            throw new IllegalArgumentException("result object cannot be left or right operand");
        }
        result.update(left);
        result.multiplyDestructive(right, scale);
    }
    
    public void multiplyDestructiveNativeDecimal128(final Decimal128 right, final short newScale) {
        if (this.signum == 0 || right.signum == 0) {
            this.zeroClear();
            this.scale = newScale;
            return;
        }
        final short currentTotalScale = (short)(this.scale + right.scale);
        final short scaleBack = (short)(currentTotalScale - newScale);
        if (scaleBack > 0) {
            this.unscaledValue.multiplyScaleDownTenDestructive(right.unscaledValue, scaleBack);
        }
        else {
            this.unscaledValue.multiplyDestructive(right.unscaledValue);
            this.unscaledValue.scaleUpTenDestructive((short)(-scaleBack));
        }
        this.scale = newScale;
        this.signum *= right.signum;
        if (this.unscaledValue.isZero()) {
            this.signum = 0;
        }
        this.unscaledValue.throwIfExceedsTenToThirtyEight();
    }
    
    public void multiplyDestructive(final Decimal128 right, final short newScale) {
        final HiveDecimal rightHD = HiveDecimal.create(right.toBigDecimal());
        final HiveDecimal thisHD = HiveDecimal.create(this.toBigDecimal());
        final HiveDecimal result = thisHD.multiply(rightHD);
        if (result == null) {
            throw new ArithmeticException("null multiply result");
        }
        this.update(result.bigDecimalValue().toPlainString(), newScale);
        this.unscaledValue.throwIfExceedsTenToThirtyEight();
    }
    
    public static void divide(final Decimal128 left, final Decimal128 right, final Decimal128 quotient, final short scale) {
        if (quotient == left || quotient == right) {
            throw new IllegalArgumentException("result object cannot be left or right operand");
        }
        quotient.update(left);
        quotient.divideDestructive(right, scale);
    }
    
    public void divideDestructiveNativeDecimal128(final Decimal128 right, final short newScale, final Decimal128 remainder) {
        if (right.signum == 0) {
            SqlMathUtil.throwZeroDivisionException();
        }
        if (this.signum == 0) {
            this.scale = newScale;
            remainder.update(this);
            return;
        }
        final short scaleBack = (short)(this.scale - right.scale - newScale);
        if (scaleBack >= 0) {
            this.unscaledValue.divideDestructive(right.unscaledValue, remainder.unscaledValue);
            this.unscaledValue.scaleDownTenDestructive(scaleBack);
            remainder.unscaledValue.scaleDownTenDestructive(scaleBack);
        }
        else {
            this.unscaledValue.divideScaleUpTenDestructive(right.unscaledValue, (short)(-scaleBack), remainder.unscaledValue);
        }
        this.scale = newScale;
        this.signum = (byte)(this.unscaledValue.isZero() ? 0 : (this.signum * right.signum));
        remainder.scale = this.scale;
        remainder.signum = (byte)(remainder.unscaledValue.isZero() ? 0 : 1);
        this.unscaledValue.throwIfExceedsTenToThirtyEight();
    }
    
    public void divideDestructive(final Decimal128 right, final short newScale) {
        final HiveDecimal rightHD = HiveDecimal.create(right.toBigDecimal());
        final HiveDecimal thisHD = HiveDecimal.create(this.toBigDecimal());
        final HiveDecimal result = thisHD.divide(rightHD);
        if (result == null) {
            throw new ArithmeticException("null divide result");
        }
        this.update(result.bigDecimalValue().toPlainString(), newScale);
        this.unscaledValue.throwIfExceedsTenToThirtyEight();
    }
    
    public static void modulo(final Decimal128 left, final Decimal128 right, final Decimal128 result, final short scale) {
        divide(left, right, result, scale);
        result.zeroFractionPart();
        result.multiplyDestructive(right, scale);
        result.negateDestructive();
        result.addDestructive(left, scale);
    }
    
    public void absDestructive() {
        if (this.signum < 0) {
            this.signum = 1;
        }
    }
    
    public void negateDestructive() {
        this.signum = (byte)(-this.signum);
    }
    
    public double sqrtAsDouble() {
        if (this.signum == 0) {
            return 0.0;
        }
        if (this.signum < 0) {
            throw new ArithmeticException("sqrt will not be a real number");
        }
        final double val = this.doubleValue();
        return Math.sqrt(val);
    }
    
    public double powAsDouble(final double n) {
        if (this.signum == 0) {
            return 0.0;
        }
        final double val = this.doubleValue();
        final double result = Math.pow(val, n);
        if (Double.isInfinite(result) || Double.isNaN(result)) {
            SqlMathUtil.throwOverflowException();
        }
        return result;
    }
    
    public byte getSignum() {
        return this.signum;
    }
    
    public short getScale() {
        return this.scale;
    }
    
    public UnsignedInt128 getUnscaledValue() {
        return this.unscaledValue;
    }
    
    @Override
    public int compareTo(final Decimal128 val) {
        if (val == this) {
            return 0;
        }
        if (this.signum != val.signum) {
            return this.signum - val.signum;
        }
        int cmp;
        if (this.scale >= val.scale) {
            cmp = this.unscaledValue.compareToScaleTen(val.unscaledValue, (short)(this.scale - val.scale));
        }
        else {
            cmp = -val.unscaledValue.compareToScaleTen(this.unscaledValue, (short)(val.scale - this.scale));
        }
        return cmp * this.signum;
    }
    
    @Override
    public boolean equals(final Object x) {
        if (x == this) {
            return true;
        }
        if (!(x instanceof Decimal128)) {
            return false;
        }
        final Decimal128 xDec = (Decimal128)x;
        return this.scale == xDec.scale && this.signum == xDec.signum && this.unscaledValue.equals(xDec.unscaledValue);
    }
    
    @Override
    public int hashCode() {
        if (this.signum == 0) {
            return 0;
        }
        return this.signum * (this.scale * 31 + this.unscaledValue.hashCode());
    }
    
    @Override
    public long longValue() {
        if (this.signum == 0) {
            return 0L;
        }
        if (this.scale != 0) {
            final HiveDecimal hd = HiveDecimal.create(this.toBigDecimal());
            return hd.longValue();
        }
        long ret = this.unscaledValue.getV1();
        ret <<= 32;
        ret |= (0xFFFFFFFFL & (long)this.unscaledValue.getV0());
        if (this.signum >= 0) {
            return ret;
        }
        return -ret;
    }
    
    @Override
    public int intValue() {
        if (this.signum == 0) {
            return 0;
        }
        int ret;
        if (this.scale == 0) {
            ret = this.unscaledValue.getV0();
        }
        else {
            final UnsignedInt128 tmp = new UnsignedInt128(this.unscaledValue);
            tmp.scaleDownTenDestructive(this.scale);
            ret = tmp.getV0();
        }
        return SqlMathUtil.setSignBitInt(ret, this.signum > 0);
    }
    
    @Override
    public float floatValue() {
        return Float.parseFloat(this.toFormalString());
    }
    
    @Override
    public double doubleValue() {
        return Double.parseDouble(this.toFormalString());
    }
    
    public BigDecimal toBigDecimal() {
        return new BigDecimal(this.toFormalString());
    }
    
    public void checkPrecisionOverflow(final int precision) {
        if (precision <= 0 || precision > 38) {
            throw new IllegalArgumentException("Invalid precision " + precision);
        }
        if (this.unscaledValue.compareTo(SqlMathUtil.POWER_TENS_INT128[precision]) >= 0) {
            SqlMathUtil.throwOverflowException();
        }
    }
    
    private static void checkScaleRange(final short scale) {
        if (scale < 0) {
            throw new ArithmeticException("Decimal128 does not support negative scaling");
        }
        if (scale > 38) {
            throw new ArithmeticException("Beyond possible Decimal128 scaling");
        }
    }
    
    public String getHiveDecimalString() {
        if (this.signum == 0) {
            return "0";
        }
        final StringBuilder buf = new StringBuilder(50);
        if (this.signum < 0) {
            buf.append('-');
        }
        final char[] unscaled = this.unscaledValue.getDigitsArray(this.tmpArray);
        final int unscaledLength = this.tmpArray[0];
        final int trailingZeros = this.tmpArray[1];
        final int numIntegerDigits = unscaledLength - this.scale;
        if (numIntegerDigits > 0) {
            for (int i = 0; i < numIntegerDigits; ++i) {
                buf.append(unscaled[i]);
            }
            if (this.scale > trailingZeros) {
                buf.append('.');
                for (int i = numIntegerDigits; i < unscaledLength - trailingZeros; ++i) {
                    buf.append(unscaled[i]);
                }
            }
        }
        else {
            buf.append('0');
            if (this.scale > trailingZeros) {
                buf.append('.');
                for (int i = unscaledLength; i < this.scale; ++i) {
                    buf.append('0');
                }
                for (int i = 0; i < unscaledLength - trailingZeros; ++i) {
                    buf.append(unscaled[i]);
                }
            }
        }
        return new String(buf);
    }
    
    public String toFormalString() {
        if (this.signum == 0) {
            return "0";
        }
        final StringBuilder buf = new StringBuilder(50);
        if (this.signum < 0) {
            buf.append('-');
        }
        final String unscaled = this.unscaledValue.toFormalString();
        if (unscaled.length() > this.scale) {
            buf.append(unscaled, 0, unscaled.length() - this.scale);
            if (this.scale > 0) {
                buf.append('.');
                buf.append(unscaled, unscaled.length() - this.scale, unscaled.length());
            }
        }
        else {
            buf.append('0');
            if (this.scale > 0) {
                buf.append('.');
                for (int i = unscaled.length(); i < this.scale; ++i) {
                    buf.append('0');
                }
                buf.append(unscaled);
            }
        }
        return new String(buf);
    }
    
    @Override
    public String toString() {
        return this.toFormalString() + "(Decimal128: scale=" + this.scale + ", signum=" + this.signum + ", BigDecimal.toString=" + this.toBigDecimal().toString() + ", unscaledValue=[" + this.unscaledValue.toString() + "])";
    }
    
    public void setNullDataValue() {
        this.unscaledValue.update(1, 0, 0, 0);
    }
    
    public void updateFixedPoint(final long val, final short scale) {
        this.scale = scale;
        if (val < 0L) {
            this.unscaledValue.update(-val);
            this.signum = -1;
        }
        else if (val == 0L) {
            this.zeroClear();
        }
        else {
            this.unscaledValue.update(val);
            this.signum = 1;
        }
    }
    
    public void zeroFractionPart(final UnsignedInt128 scratch) {
        final short placesToRemove = this.getScale();
        if (placesToRemove == 0) {
            return;
        }
        final UnsignedInt128 powerTenDivisor = SqlMathUtil.POWER_TENS_INT128[placesToRemove];
        this.getUnscaledValue().divideDestructive(powerTenDivisor, scratch);
        this.getUnscaledValue().scaleUpTenDestructive(placesToRemove);
        if (this.unscaledValue.isZero()) {
            this.signum = 0;
        }
    }
    
    public void zeroFractionPart() {
        final UnsignedInt128 scratch = new UnsignedInt128();
        this.zeroFractionPart(scratch);
    }
    
    public Decimal128 squareDestructive() {
        this.multiplyDestructive(this, this.getScale());
        return this;
    }
    
    public Decimal128 updateVarianceDestructive(final Decimal128 scratch, final Decimal128 value, final Decimal128 sum, final long count) {
        scratch.update(count);
        scratch.multiplyDestructive(value, value.getScale());
        scratch.subtractDestructive(sum, sum.getScale());
        scratch.squareDestructive();
        scratch.unscaledValue.divideDestructive(count * (count - 1L));
        this.addDestructive(scratch, this.getScale());
        return this;
    }
    
    public Decimal128 fastUpdateFromInternalStorage(final byte[] internalStorage, final short scale) {
        this.scale = scale;
        this.signum = this.unscaledValue.fastUpdateFromInternalStorage(internalStorage);
        return this;
    }
    
    public void setUnscaledValue(final UnsignedInt128 unscaledValue) {
        this.unscaledValue = unscaledValue;
    }
    
    public void setScale(final short scale) {
        this.scale = scale;
    }
    
    public void setSignum(final byte signum) {
        this.signum = signum;
    }
    
    static {
        ONE = new Decimal128().update(1L);
        MAX_VALUE = new Decimal128(UnsignedInt128.TEN_TO_THIRTYEIGHT, (short)0, false).subtractDestructive(Decimal128.ONE, (short)0);
        MIN_VALUE = new Decimal128(UnsignedInt128.TEN_TO_THIRTYEIGHT, (short)0, true).addDestructive(Decimal128.ONE, (short)0);
    }
}
