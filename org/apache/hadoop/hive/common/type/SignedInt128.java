// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.type;

import java.math.BigInteger;
import java.nio.IntBuffer;

public final class SignedInt128 extends Number implements Comparable<SignedInt128>
{
    public static final SignedInt128 MAX_VALUE;
    public static final SignedInt128 MIN_VALUE;
    private static final long serialVersionUID = 1L;
    private final UnsignedInt128 mag;
    private boolean negative;
    
    public static int getIntsPerElement(final int precision) {
        return UnsignedInt128.getIntsPerElement(precision);
    }
    
    public SignedInt128() {
        this.negative = false;
        this.mag = new UnsignedInt128(0, 0, 0, 0);
    }
    
    public SignedInt128(final long v) {
        this.negative = (v < 0L);
        this.mag = new UnsignedInt128((v < 0L) ? (-v) : v);
    }
    
    public SignedInt128(final UnsignedInt128 mag) {
        this.negative = (mag.getV3() < 0);
        this.mag = new UnsignedInt128(mag.getV0(), mag.getV1(), mag.getV2(), mag.getV3() & Integer.MAX_VALUE);
    }
    
    public SignedInt128(final SignedInt128 o) {
        this.negative = o.negative;
        this.mag = new UnsignedInt128(o.mag);
    }
    
    public SignedInt128(final int v0, final int v1, final int v2, final int v3) {
        this.negative = (v3 < 0);
        this.mag = new UnsignedInt128(v0, v1, v2, v3 & Integer.MAX_VALUE);
    }
    
    public SignedInt128(final String str) {
        this();
        this.update(str);
    }
    
    public SignedInt128(final char[] str, final int offset, final int length) {
        this();
        this.update(str, offset, length);
    }
    
    public int getV0() {
        return this.mag.getV0();
    }
    
    public int getV1() {
        return this.mag.getV1();
    }
    
    public int getV2() {
        return this.mag.getV2();
    }
    
    public int getV3() {
        return this.mag.getV3();
    }
    
    public void zeroClear() {
        this.mag.zeroClear();
        this.negative = false;
    }
    
    public void update(final long v) {
        this.negative = (v < 0L);
        this.mag.update((v < 0L) ? (-v) : v);
    }
    
    public void update(final SignedInt128 o) {
        this.negative = o.negative;
        this.mag.update(o.mag);
    }
    
    public void update(final String str) {
        this.update(str.toCharArray(), 0, str.length());
    }
    
    public void update(final char[] str, int offset, int length) {
        if (length == 0) {
            this.zeroClear();
            return;
        }
        this.negative = false;
        if (str[offset] == '-') {
            this.negative = true;
            ++offset;
            --length;
        }
        else if (str[offset] == '+') {
            ++offset;
            --length;
        }
        this.mag.update(str, offset, length);
        if (this.mag.isZero()) {
            this.negative = false;
        }
    }
    
    public void update128(final int v0, final int v1, final int v2, final int v3) {
        this.negative = (v3 < 0);
        this.mag.update(v0, v1, v2, v3 & Integer.MAX_VALUE);
    }
    
    public void update96(final int v0, final int v1, final int v2) {
        this.negative = (v2 < 0);
        this.mag.update(v0, v1, v2 & Integer.MAX_VALUE, 0);
    }
    
    public void update64(final int v0, final int v1) {
        this.negative = (v1 < 0);
        this.mag.update(v0, v1 & Integer.MAX_VALUE, 0, 0);
    }
    
    public void update32(final int v0) {
        this.negative = (v0 < 0);
        this.mag.update(v0 & Integer.MAX_VALUE, 0, 0, 0);
    }
    
    public void update128(final int[] array, final int offset) {
        this.update128(array[offset], array[offset + 1], array[offset + 2], array[offset + 3]);
    }
    
    public void update96(final int[] array, final int offset) {
        this.update96(array[offset], array[offset + 1], array[offset + 2]);
    }
    
    public void update64(final int[] array, final int offset) {
        this.update64(array[offset], array[offset + 1]);
    }
    
    public void update32(final int[] array, final int offset) {
        this.update32(array[offset]);
    }
    
    public void update128(final IntBuffer buf) {
        this.update128(buf.get(), buf.get(), buf.get(), buf.get());
    }
    
    public void update96(final IntBuffer buf) {
        this.update96(buf.get(), buf.get(), buf.get());
    }
    
    public void update64(final IntBuffer buf) {
        this.update64(buf.get(), buf.get());
    }
    
    public void update32(final IntBuffer buf) {
        this.update32(buf.get());
    }
    
    public void serializeTo128(final int[] array, final int offset) {
        assert this.mag.getV3() >= 0;
        array[offset] = this.mag.getV0();
        array[offset + 1] = this.mag.getV1();
        array[offset + 2] = this.mag.getV2();
        array[offset + 3] = (this.mag.getV3() | (this.negative ? Integer.MIN_VALUE : 0));
    }
    
    public void serializeTo96(final int[] array, final int offset) {
        assert this.mag.getV3() == 0 && this.mag.getV2() >= 0;
        array[offset] = this.mag.getV0();
        array[offset + 1] = this.mag.getV1();
        array[offset + 2] = (this.mag.getV2() | (this.negative ? Integer.MIN_VALUE : 0));
    }
    
    public void serializeTo64(final int[] array, final int offset) {
        assert this.mag.getV3() == 0 && this.mag.getV2() == 0 && this.mag.getV1() >= 0;
        array[offset] = this.mag.getV0();
        array[offset + 1] = (this.mag.getV1() | (this.negative ? Integer.MIN_VALUE : 0));
    }
    
    public void serializeTo32(final int[] array, final int offset) {
        assert this.mag.getV3() == 0 && this.mag.getV2() == 0 && this.mag.getV1() == 0 && this.mag.getV0() >= 0;
        array[offset] = (this.mag.getV0() | (this.negative ? Integer.MIN_VALUE : 0));
    }
    
    public void serializeTo128(final IntBuffer buf) {
        assert this.mag.getV3() >= 0;
        buf.put(this.mag.getV0());
        buf.put(this.mag.getV1());
        buf.put(this.mag.getV2());
        buf.put(this.mag.getV3() | (this.negative ? Integer.MIN_VALUE : 0));
    }
    
    public void serializeTo96(final IntBuffer buf) {
        assert this.mag.getV3() == 0 && this.mag.getV2() >= 0;
        buf.put(this.mag.getV0());
        buf.put(this.mag.getV1());
        buf.put(this.mag.getV2() | (this.negative ? Integer.MIN_VALUE : 0));
    }
    
    public void serializeTo64(final IntBuffer buf) {
        assert this.mag.getV3() == 0 && this.mag.getV2() == 0 && this.mag.getV1() >= 0;
        buf.put(this.mag.getV0());
        buf.put(this.mag.getV1() | (this.negative ? Integer.MIN_VALUE : 0));
    }
    
    public void serializeTo32(final IntBuffer buf) {
        assert this.mag.getV3() == 0 && this.mag.getV2() == 0 && this.mag.getV1() == 0 && this.mag.getV0() >= 0;
        buf.put(this.mag.getV0() | (this.negative ? Integer.MIN_VALUE : 0));
    }
    
    public boolean isZero() {
        return this.mag.isZero();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof SignedInt128) {
            final SignedInt128 o = (SignedInt128)obj;
            return this.negative == o.negative && this.mag.equals(o.mag);
        }
        return false;
    }
    
    public boolean equals(final SignedInt128 o) {
        return this.negative == o.negative && this.mag.equals(o.mag);
    }
    
    @Override
    public int hashCode() {
        return this.negative ? (-this.mag.hashCode()) : this.mag.hashCode();
    }
    
    @Override
    public int compareTo(final SignedInt128 o) {
        if (this.negative) {
            if (o.negative) {
                return o.mag.compareTo(this.mag);
            }
            return -1;
        }
        else {
            if (o.negative) {
                return 1;
            }
            return this.mag.compareTo(o.mag);
        }
    }
    
    @Override
    public int intValue() {
        final int unsigned = this.mag.getV0() & Integer.MAX_VALUE;
        return this.negative ? (-unsigned) : unsigned;
    }
    
    @Override
    public long longValue() {
        final long unsigned = SqlMathUtil.combineInts(this.mag.getV0(), this.mag.getV1()) & Long.MAX_VALUE;
        return this.negative ? (-unsigned) : unsigned;
    }
    
    @Override
    public float floatValue() {
        return (float)this.intValue();
    }
    
    @Override
    public double doubleValue() {
        return (double)this.longValue();
    }
    
    public static void add(final SignedInt128 left, final SignedInt128 right, final SignedInt128 result) {
        result.update(left);
        result.addDestructive(right);
    }
    
    public void addDestructive(final SignedInt128 right) {
        if (this.negative == right.negative) {
            this.mag.addDestructive(right.mag);
            if (this.mag.getV3() < 0) {
                SqlMathUtil.throwOverflowException();
            }
            return;
        }
        final byte signum = UnsignedInt128.difference(this.mag, right.mag, this.mag);
        this.negative = ((signum > 0) ? this.negative : right.negative);
    }
    
    public static void subtract(final SignedInt128 left, final SignedInt128 right, final SignedInt128 result) {
        result.update(left);
        result.subtractDestructive(right);
    }
    
    public void subtractDestructive(final SignedInt128 right) {
        if (this.negative != right.negative) {
            this.mag.addDestructive(right.mag);
            if (this.mag.getV3() < 0) {
                SqlMathUtil.throwOverflowException();
            }
            return;
        }
        final byte signum = UnsignedInt128.difference(this.mag, right.mag, this.mag);
        this.negative = ((signum > 0) ? this.negative : (!this.negative));
    }
    
    public static void multiply(final SignedInt128 left, final SignedInt128 right, final SignedInt128 result) {
        if (result == left || result == right) {
            throw new IllegalArgumentException("result object cannot be left or right operand");
        }
        result.update(left);
        result.multiplyDestructive(right);
    }
    
    public void multiplyDestructive(final SignedInt128 right) {
        this.mag.multiplyDestructive(right.mag);
        this.negative ^= right.negative;
        if (this.mag.getV3() < 0) {
            SqlMathUtil.throwOverflowException();
        }
    }
    
    public void multiplyDestructive(final int right) {
        if (right < 0) {
            this.mag.multiplyDestructive(-right);
            this.negative = !this.negative;
        }
        else {
            this.mag.multiplyDestructive(right);
        }
        if (this.mag.isZero()) {
            this.negative = false;
        }
        if (this.mag.getV3() < 0) {
            SqlMathUtil.throwOverflowException();
        }
    }
    
    public int divideDestructive(final int right) {
        int ret;
        if (right < 0) {
            ret = this.mag.divideDestructive(-right);
            this.negative = !this.negative;
        }
        else {
            ret = this.mag.divideDestructive(right);
        }
        ret &= Integer.MAX_VALUE;
        if (this.negative) {
            ret = -ret;
        }
        if (this.mag.isZero()) {
            this.negative = false;
        }
        return ret;
    }
    
    public static void divide(final SignedInt128 left, final SignedInt128 right, final SignedInt128 quotient, final SignedInt128 remainder) {
        if (quotient == left || quotient == right) {
            throw new IllegalArgumentException("result object cannot be left or right operand");
        }
        quotient.update(left);
        quotient.divideDestructive(right, remainder);
    }
    
    public void divideDestructive(final SignedInt128 right, final SignedInt128 remainder) {
        this.mag.divideDestructive(right.mag, remainder.mag);
        remainder.negative = false;
        this.negative ^= right.negative;
    }
    
    public void negateDestructive() {
        this.negative = !this.negative;
    }
    
    public void absDestructive() {
        this.negative = false;
    }
    
    public static void negate(final SignedInt128 left, final SignedInt128 result) {
        result.update(left);
        result.negateDestructive();
    }
    
    public static void abs(final SignedInt128 left, final SignedInt128 result) {
        result.update(left);
        result.absDestructive();
    }
    
    public void incrementDestructive() {
        if (!this.negative) {
            if (this.mag.equals(-1, -1, -1, Integer.MAX_VALUE)) {
                SqlMathUtil.throwOverflowException();
            }
            this.mag.incrementDestructive();
            assert this.mag.getV3() >= 0;
        }
        else {
            assert !this.mag.isZero();
            this.mag.decrementDestructive();
            if (this.mag.isZero()) {
                this.negative = false;
            }
        }
    }
    
    public void decrementDestructive() {
        if (this.negative) {
            if (this.mag.equals(-1, -1, -1, Integer.MAX_VALUE)) {
                SqlMathUtil.throwOverflowException();
            }
            this.mag.incrementDestructive();
            assert this.mag.getV3() >= 0;
        }
        else if (this.mag.isZero()) {
            this.negative = true;
            this.mag.incrementDestructive();
        }
        else {
            this.mag.decrementDestructive();
        }
    }
    
    public static void increment(final SignedInt128 left, final SignedInt128 result) {
        result.update(left);
        result.incrementDestructive();
    }
    
    public static void decrement(final SignedInt128 left, final SignedInt128 result) {
        result.update(left);
        result.decrementDestructive();
    }
    
    public void shiftRightDestructive(final int bits, final boolean roundUp) {
        this.mag.shiftRightDestructive(bits, roundUp);
        if (this.mag.isZero() && this.negative) {
            this.negative = false;
        }
    }
    
    public void shiftLeftDestructive(final int bits) {
        this.mag.shiftLeftDestructive(bits);
        if (this.mag.getV3() < 0) {
            SqlMathUtil.throwOverflowException();
        }
        assert this.mag.getV3() >= 0;
    }
    
    public void scaleDownTenDestructive(final short tenScale) {
        this.mag.scaleDownTenDestructive(tenScale);
        if (this.mag.isZero() && this.negative) {
            this.negative = false;
        }
    }
    
    public void scaleUpTenDestructive(final short tenScale) {
        this.mag.scaleUpTenDestructive(tenScale);
        if (this.mag.getV3() < 0) {
            SqlMathUtil.throwOverflowException();
        }
    }
    
    public static void shiftRight(final SignedInt128 left, final SignedInt128 result, final int bits, final boolean roundUp) {
        result.update(left);
        result.shiftRightDestructive(bits, roundUp);
    }
    
    public static void shiftLeft(final SignedInt128 left, final SignedInt128 result, final int bits) {
        result.update(left);
        result.shiftLeftDestructive(bits);
    }
    
    public static void scaleDownTen(final SignedInt128 left, final SignedInt128 result, final short tenScale) {
        result.update(left);
        result.scaleDownTenDestructive(tenScale);
    }
    
    public static void scaleUpTen(final SignedInt128 left, final SignedInt128 result, final short tenScale) {
        result.update(left);
        result.scaleUpTenDestructive(tenScale);
    }
    
    public BigInteger toBigIntegerSlow() {
        final BigInteger bigInt = this.mag.toBigIntegerSlow();
        return this.negative ? bigInt.negate() : bigInt;
    }
    
    public String toFormalString() {
        if (this.negative) {
            return "-" + this.mag.toFormalString();
        }
        return this.mag.toFormalString();
    }
    
    @Override
    public String toString() {
        return "SignedInt128 (" + (this.negative ? "negative" : "positive") + "). mag=" + this.mag.toString();
    }
    
    static {
        MAX_VALUE = new SignedInt128(-1, -1, -1, Integer.MAX_VALUE);
        MIN_VALUE = new SignedInt128(-1, -1, -1, -1);
    }
}
