// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.type;

import org.apache.hive.common.util.Decimal128FastBuffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.nio.IntBuffer;
import java.math.BigInteger;
import java.io.Serializable;

public final class UnsignedInt128 implements Comparable<UnsignedInt128>, Serializable
{
    public static final int INT_COUNT = 4;
    public static final int BYTE_SIZE = 16;
    public static final int MAX_DIGITS = 38;
    public static final UnsignedInt128 MAX_VALUE;
    public static final UnsignedInt128 MIN_VALUE;
    public static final UnsignedInt128 TEN_TO_THIRTYEIGHT;
    private int[] v;
    private byte count;
    
    public static int getIntsPerElement(final int precision) {
        assert precision >= 0 && precision <= 38;
        if (precision <= 9) {
            return 1;
        }
        if (precision <= 19) {
            return 2;
        }
        if (precision <= 28) {
            return 3;
        }
        return 4;
    }
    
    public UnsignedInt128() {
        this.v = new int[4];
        this.zeroClear();
    }
    
    public UnsignedInt128(final UnsignedInt128 o) {
        this.v = new int[4];
        this.update(o);
    }
    
    public UnsignedInt128(final int v0, final int v1, final int v2, final int v3) {
        this.v = new int[4];
        this.update(v0, v1, v2, v3);
    }
    
    public UnsignedInt128(final long v) {
        this.v = new int[4];
        this.update(v);
    }
    
    public UnsignedInt128(final String str) {
        this.v = new int[4];
        this.update(str);
    }
    
    public UnsignedInt128(final char[] str, final int offset, final int length) {
        this.v = new int[4];
        this.update(str, offset, length);
    }
    
    public UnsignedInt128(final BigInteger bigInt) {
        this.v = new int[4];
        this.update(bigInt);
    }
    
    public void update(final BigInteger bigInt) {
        final int v0 = bigInt.intValue();
        final int v2 = bigInt.shiftRight(32).intValue();
        final int v3 = bigInt.shiftRight(64).intValue();
        final int v4 = bigInt.shiftRight(96).intValue();
        this.update(v0, v2, v3, v4);
    }
    
    public int getV0() {
        return this.v[0];
    }
    
    public int getV1() {
        return this.v[1];
    }
    
    public int getV2() {
        return this.v[2];
    }
    
    public int getV3() {
        return this.v[3];
    }
    
    public void setV0(final int val) {
        this.v[0] = val;
        this.updateCount();
    }
    
    public void setV1(final int val) {
        this.v[1] = val;
        this.updateCount();
    }
    
    public void setV2(final int val) {
        this.v[2] = val;
        this.updateCount();
    }
    
    public void setV3(final int val) {
        this.v[3] = val;
        this.updateCount();
    }
    
    public boolean exceedsTenToThirtyEight() {
        if (this.v[3] != 1262177448) {
            return this.v[3] < 0 || this.v[3] > 1262177448;
        }
        if (this.v[2] != 1518781562) {
            return this.v[2] < 0 || this.v[2] > 1518781562;
        }
        return this.v[1] < 0 || this.v[1] > 160047680;
    }
    
    public void throwIfExceedsTenToThirtyEight() {
        if (this.exceedsTenToThirtyEight()) {
            SqlMathUtil.throwOverflowException();
        }
    }
    
    public long asLong() {
        if (this.count > 2 || this.v[1] < 0) {
            SqlMathUtil.throwOverflowException();
        }
        return (long)this.v[1] << 32 | (long)this.v[0];
    }
    
    public void zeroClear() {
        this.v[0] = 0;
        this.v[1] = 0;
        this.v[2] = 0;
        this.v[3] = 0;
        this.count = 0;
    }
    
    public boolean isZero() {
        return this.count == 0;
    }
    
    public boolean isOne() {
        return this.v[0] == 1 && this.count == 1;
    }
    
    public boolean fitsInt32() {
        return this.count <= 1;
    }
    
    public void update(final UnsignedInt128 o) {
        this.update(o.v[0], o.v[1], o.v[2], o.v[3]);
    }
    
    public void update(final long v) {
        assert v >= 0L;
        this.update((int)v, (int)(v >> 32), 0, 0);
    }
    
    public void update(final int v0, final int v1, final int v2, final int v3) {
        this.v[0] = v0;
        this.v[1] = v1;
        this.v[2] = v2;
        this.v[3] = v3;
        this.updateCount();
    }
    
    public void update(final IntBuffer buf, final int precision) {
        switch (getIntsPerElement(precision)) {
            case 1: {
                this.update32(buf);
                break;
            }
            case 2: {
                this.update64(buf);
                break;
            }
            case 3: {
                this.update96(buf);
                break;
            }
            case 4: {
                this.update128(buf);
                break;
            }
            default: {
                throw new RuntimeException();
            }
        }
    }
    
    public void update128(final IntBuffer buf) {
        buf.get(this.v, 0, 4);
        this.updateCount();
    }
    
    public void update96(final IntBuffer buf) {
        buf.get(this.v, 0, 3);
        this.v[3] = 0;
        this.updateCount();
    }
    
    public void update64(final IntBuffer buf) {
        buf.get(this.v, 0, 2);
        this.v[2] = 0;
        this.v[3] = 0;
        this.updateCount();
    }
    
    public void update32(final IntBuffer buf) {
        this.v[0] = buf.get();
        this.v[1] = 0;
        this.v[2] = 0;
        this.v[3] = 0;
        this.updateCount();
    }
    
    public void update(final int[] array, final int offset, final int precision) {
        switch (getIntsPerElement(precision)) {
            case 1: {
                this.update32(array, offset);
                break;
            }
            case 2: {
                this.update64(array, offset);
                break;
            }
            case 3: {
                this.update96(array, offset);
                break;
            }
            case 4: {
                this.update128(array, offset);
                break;
            }
            default: {
                throw new RuntimeException();
            }
        }
    }
    
    public void update128(final int[] array, final int offset) {
        System.arraycopy(array, offset, this.v, 0, 4);
        this.updateCount();
    }
    
    public void update96(final int[] array, final int offset) {
        System.arraycopy(array, offset, this.v, 0, 3);
        this.v[3] = 0;
        this.updateCount();
    }
    
    public void update64(final int[] array, final int offset) {
        System.arraycopy(array, offset, this.v, 0, 2);
        this.v[2] = 0;
        this.v[3] = 0;
        this.updateCount();
    }
    
    public void update32(final int[] array, final int offset) {
        this.v[0] = array[offset];
        this.v[1] = 0;
        this.v[2] = 0;
        this.v[3] = 0;
        this.updateCount();
    }
    
    public void update(final String str) {
        this.update(str.toCharArray(), 0, str.length());
    }
    
    public void update(final char[] str, final int offset, final int length) {
        final int end = offset + length;
        assert end <= str.length;
        int cursor;
        for (cursor = offset; cursor < end && str[cursor] == '0'; ++cursor) {}
        if (cursor == end) {
            this.zeroClear();
            return;
        }
        if (end - cursor > 38) {
            SqlMathUtil.throwOverflowException();
        }
        int accumulated = 0;
        int accumulatedCount = 0;
        while (cursor < end) {
            if (str[cursor] < '0' || str[cursor] > '9') {
                throw new NumberFormatException("Invalid string:" + new String(str, offset, length));
            }
            if (accumulatedCount == 9) {
                this.scaleUpTenDestructive((short)accumulatedCount);
                this.addDestructive(accumulated);
                accumulated = 0;
                accumulatedCount = 0;
            }
            final int digit = str[cursor] - '0';
            accumulated = accumulated * 10 + digit;
            ++accumulatedCount;
            ++cursor;
        }
        if (accumulatedCount > 0) {
            this.scaleUpTenDestructive((short)accumulatedCount);
            this.addDestructive(accumulated);
        }
    }
    
    public void serializeTo(final IntBuffer buf, final int precision) {
        buf.put(this.v, 0, getIntsPerElement(precision));
    }
    
    public void serializeTo128(final IntBuffer buf) {
        buf.put(this.v, 0, 4);
    }
    
    public void serializeTo96(final IntBuffer buf) {
        assert this.v[3] == 0;
        buf.put(this.v, 0, 3);
    }
    
    public void serializeTo64(final IntBuffer buf) {
        assert this.v[2] == 0;
        assert this.v[3] == 0;
        buf.put(this.v, 0, 2);
    }
    
    public void serializeTo32(final IntBuffer buf) {
        assert this.v[1] == 0;
        assert this.v[2] == 0;
        assert this.v[3] == 0;
        buf.put(this.v[0]);
    }
    
    public void serializeTo(final int[] array, final int offset, final int precision) {
        System.arraycopy(this.v, 0, array, offset, getIntsPerElement(precision));
    }
    
    public void serializeTo128(final int[] array, final int offset) {
        System.arraycopy(this.v, 0, array, offset, 4);
    }
    
    public void serializeTo96(final int[] array, final int offset) {
        assert this.v[3] == 0;
        System.arraycopy(this.v, 0, array, offset, 3);
    }
    
    public void serializeTo64(final int[] array, final int offset) {
        assert this.v[2] == 0;
        assert this.v[3] == 0;
        System.arraycopy(this.v, 0, array, offset, 2);
    }
    
    public void serializeTo32(final int[] array, final int offset) {
        assert this.v[1] == 0;
        assert this.v[2] == 0;
        assert this.v[3] == 0;
        array[0] = this.v[0];
    }
    
    @Override
    public int compareTo(final UnsignedInt128 o) {
        return this.compareTo(o.v);
    }
    
    public int compareTo(final int[] o) {
        return this.compareTo(o[0], o[1], o[2], o[3]);
    }
    
    public int compareTo(final int o0, final int o1, final int o2, final int o3) {
        if (this.v[3] != o3) {
            return SqlMathUtil.compareUnsignedInt(this.v[3], o3);
        }
        if (this.v[2] != o2) {
            return SqlMathUtil.compareUnsignedInt(this.v[2], o2);
        }
        if (this.v[1] != o1) {
            return SqlMathUtil.compareUnsignedInt(this.v[1], o1);
        }
        return SqlMathUtil.compareUnsignedInt(this.v[0], o0);
    }
    
    public int compareToScaleTen(final UnsignedInt128 o, final short tenScale) {
        if (tenScale == 0) {
            return this.compareTo(o);
        }
        if (o.isZero()) {
            return this.isZero() ? 0 : 1;
        }
        if (this.isZero()) {
            if (tenScale > 0) {
                return -1;
            }
            if (tenScale < -38) {
                return 0;
            }
            final boolean oZero = o.compareTo(SqlMathUtil.ROUND_POWER_TENS_INT128[-tenScale]) < 0;
            return oZero ? 0 : -1;
        }
        else {
            if (this.fitsInt32() && o.fitsInt32() && tenScale <= 9) {
                final long v0Long = (long)this.v[0] & 0xFFFFFFFFL;
                long o2;
                if (tenScale < 0) {
                    if (tenScale < -9) {
                        o2 = 0L;
                    }
                    else {
                        o2 = ((long)o.v[0] & 0xFFFFFFFFL) / SqlMathUtil.POWER_TENS_INT31[-tenScale];
                        final long remainder = ((long)o.v[0] & 0xFFFFFFFFL) % SqlMathUtil.POWER_TENS_INT31[-tenScale];
                        if (remainder >= SqlMathUtil.ROUND_POWER_TENS_INT31[-tenScale]) {
                            assert o2 >= 0L;
                            ++o2;
                        }
                    }
                }
                else {
                    o2 = ((long)o.v[0] & 0xFFFFFFFFL) * ((long)SqlMathUtil.POWER_TENS_INT31[tenScale] & 0xFFFFFFFFL);
                }
                return SqlMathUtil.compareUnsignedLong(v0Long, o2);
            }
            final int[] ov = o.v.clone();
            if (tenScale < 0) {
                scaleDownTenArray4RoundUp(ov, (short)(-tenScale));
            }
            else {
                final boolean overflow = scaleUpTenArray(ov, tenScale);
                if (overflow) {
                    return -1;
                }
            }
            return this.compareTo(ov);
        }
    }
    
    @Override
    public int hashCode() {
        return this.v[0] * 716283427 + this.v[1] * 1226369739 + this.v[2] * -265268825 + this.v[3];
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof UnsignedInt128 && this.equals((UnsignedInt128)obj);
    }
    
    public boolean equals(final UnsignedInt128 o) {
        return this.v[0] == o.v[0] && this.v[1] == o.v[1] && this.v[2] == o.v[2] && this.v[3] == o.v[3];
    }
    
    public boolean equals(final int o0, final int o1, final int o2, final int o3) {
        return this.v[0] == o0 && this.v[1] == o1 && this.v[2] == o2 && this.v[3] == o3;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new UnsignedInt128(this);
    }
    
    public BigInteger toBigIntegerSlow() {
        BigInteger bigInt = BigInteger.valueOf((long)this.v[3] & 0xFFFFFFFFL);
        bigInt = bigInt.shiftLeft(32);
        bigInt = bigInt.add(BigInteger.valueOf((long)this.v[2] & 0xFFFFFFFFL));
        bigInt = bigInt.shiftLeft(32);
        bigInt = bigInt.add(BigInteger.valueOf((long)this.v[1] & 0xFFFFFFFFL));
        bigInt = bigInt.shiftLeft(32);
        bigInt = bigInt.add(BigInteger.valueOf((long)this.v[0] & 0xFFFFFFFFL));
        return bigInt;
    }
    
    public String toFormalString() {
        final char[] buf = new char[39];
        int bufCount = 0;
        int nonZeroBufCount = 0;
        final int tenScale = 9;
        final int tenPower = SqlMathUtil.POWER_TENS_INT31[9];
        final UnsignedInt128 tmp = new UnsignedInt128(this);
        while (!tmp.isZero()) {
            int remainder = tmp.divideDestructive(tenPower);
            for (int i = 0; i < 9 && bufCount < buf.length; ++i) {
                final int digit = remainder % 10;
                remainder /= 10;
                buf[bufCount] = (char)(digit + 48);
                ++bufCount;
                if (digit != 0) {
                    nonZeroBufCount = bufCount;
                }
            }
        }
        if (bufCount == 0) {
            return "0";
        }
        final char[] reversed = new char[nonZeroBufCount];
        for (int i = 0; i < nonZeroBufCount; ++i) {
            reversed[i] = buf[nonZeroBufCount - i - 1];
        }
        return new String(reversed);
    }
    
    public char[] getDigitsArray(final int[] meta) {
        final char[] buf = new char[39];
        int bufCount = 0;
        int nonZeroBufCount = 0;
        int trailingZeros = 0;
        final int tenScale = 9;
        final int tenPower = SqlMathUtil.POWER_TENS_INT31[9];
        final UnsignedInt128 tmp = new UnsignedInt128(this);
        while (!tmp.isZero()) {
            int remainder = tmp.divideDestructive(tenPower);
            for (int i = 0; i < 9 && bufCount < buf.length; ++i) {
                final int digit = remainder % 10;
                remainder /= 10;
                buf[bufCount] = (char)(digit + 48);
                ++bufCount;
                if (digit != 0) {
                    nonZeroBufCount = bufCount;
                }
                if (nonZeroBufCount == 0) {
                    ++trailingZeros;
                }
            }
        }
        if (bufCount == 0) {
            meta[meta[0] = 1] = 1;
            buf[0] = '0';
            return buf;
        }
        for (int j = 0, k = nonZeroBufCount - 1; j < k; ++j, --k) {
            final char t = buf[j];
            buf[j] = buf[k];
            buf[k] = t;
        }
        meta[0] = nonZeroBufCount;
        meta[1] = trailingZeros;
        return buf;
    }
    
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append("Int128: count=" + this.count + ",");
        str.append("v[0]=" + this.v[0] + "(0x" + Integer.toHexString(this.v[0]) + "), ");
        str.append("v[1]=" + this.v[1] + "(0x" + Integer.toHexString(this.v[1]) + "), ");
        str.append("v[2]=" + this.v[2] + "(0x" + Integer.toHexString(this.v[2]) + "), ");
        str.append("v[3]=" + this.v[3] + "(0x" + Integer.toHexString(this.v[3]) + "), ");
        str.append("BigInteger#toString=" + this.toBigIntegerSlow().toString());
        return new String(str);
    }
    
    public void addDestructive(final UnsignedInt128 right) {
        this.addDestructive(right.v);
    }
    
    public void addDestructive(final int[] r) {
        long sum = 0L;
        for (int i = 0; i < 4; ++i) {
            sum = ((long)this.v[i] & 0xFFFFFFFFL) + ((long)r[i] & 0xFFFFFFFFL) + (sum >>> 32);
            this.v[i] = (int)sum;
        }
        this.updateCount();
        if (sum >> 32 != 0L) {
            SqlMathUtil.throwOverflowException();
        }
    }
    
    public void addDestructive(final int r) {
        if (((long)this.v[0] & 0xFFFFFFFFL) + ((long)r & 0xFFFFFFFFL) >= 4294967296L) {
            final int[] v = this.v;
            final int n = 0;
            v[n] += r;
            if (this.v[1] == -1) {
                this.v[1] = 0;
                if (this.v[2] == -1) {
                    this.v[2] = 0;
                    if (this.v[3] == -1) {
                        SqlMathUtil.throwOverflowException();
                    }
                    else {
                        final int[] v2 = this.v;
                        final int n2 = 3;
                        ++v2[n2];
                    }
                }
                else {
                    final int[] v3 = this.v;
                    final int n3 = 2;
                    ++v3[n3];
                }
            }
            else {
                final int[] v4 = this.v;
                final int n4 = 1;
                ++v4[n4];
            }
        }
        else {
            final int[] v5 = this.v;
            final int n5 = 0;
            v5[n5] += r;
        }
        this.updateCount();
    }
    
    public void incrementDestructive() {
        incrementArray(this.v);
        this.updateCount();
    }
    
    public void decrementDestructive() {
        decrementArray(this.v);
        this.updateCount();
    }
    
    public void addDestructiveScaleTen(final UnsignedInt128 right, final short tenScale) {
        if (tenScale == 0) {
            this.addDestructive(right);
            return;
        }
        final int[] r = right.v.clone();
        if (tenScale < 0) {
            scaleDownTenArray4RoundUp(r, (short)(-tenScale));
        }
        else if (tenScale > 0) {
            final boolean overflow = scaleUpTenArray(r, tenScale);
            if (overflow) {
                SqlMathUtil.throwOverflowException();
            }
        }
        this.addDestructive(r);
    }
    
    public void subtractDestructive(final UnsignedInt128 right) {
        this.subtractDestructive(right.v);
    }
    
    public void subtractDestructive(final int[] r) {
        long sum = 0L;
        for (int i = 0; i < 4; ++i) {
            sum = ((long)this.v[i] & 0xFFFFFFFFL) - ((long)r[i] & 0xFFFFFFFFL) - (int)(-(sum >> 32));
            this.v[i] = (int)sum;
        }
        this.updateCount();
        if (sum >> 32 != 0L) {
            SqlMathUtil.throwOverflowException();
        }
    }
    
    public static byte difference(final UnsignedInt128 left, final UnsignedInt128 right, final UnsignedInt128 result) {
        return differenceInternal(left, right.v, result);
    }
    
    public static byte differenceScaleTen(final UnsignedInt128 left, final UnsignedInt128 right, final UnsignedInt128 result, final short tenScale) {
        if (tenScale == 0) {
            return difference(left, right, result);
        }
        final int[] r = right.v.clone();
        if (tenScale < 0) {
            scaleDownTenArray4RoundUp(r, (short)(-tenScale));
        }
        else {
            final boolean overflow = scaleUpTenArray(r, tenScale);
            if (overflow) {
                SqlMathUtil.throwOverflowException();
            }
        }
        return differenceInternal(left, r, result);
    }
    
    public void multiplyDestructive(final int right) {
        if (right == 0) {
            this.zeroClear();
            return;
        }
        if (right == 1) {
            return;
        }
        long sum = 0L;
        final long rightUnsigned = (long)right & 0xFFFFFFFFL;
        for (int i = 0; i < 4; ++i) {
            sum = ((long)this.v[i] & 0xFFFFFFFFL) * rightUnsigned + (sum >>> 32);
            this.v[i] = (int)sum;
        }
        this.updateCount();
        if (sum >> 32 != 0L) {
            SqlMathUtil.throwOverflowException();
        }
    }
    
    public void multiplyDestructive(final UnsignedInt128 right) {
        if (this.fitsInt32() && right.fitsInt32()) {
            this.multiplyDestructiveFitsInt32(right, (short)0, (short)0);
            return;
        }
        multiplyArrays4And4To4NoOverflow(this.v, right.v);
        this.updateCount();
    }
    
    public void multiplyShiftDestructive(final UnsignedInt128 right, final short rightShifts) {
        if (this.fitsInt32() && right.fitsInt32()) {
            this.multiplyDestructiveFitsInt32(right, rightShifts, (short)0);
            return;
        }
        final int[] z = multiplyArrays4And4To8(this.v, right.v);
        shiftRightArray(rightShifts, z, this.v, true);
        this.updateCount();
    }
    
    public void multiplyScaleDownTenDestructive(final UnsignedInt128 right, final short tenScale) {
        assert tenScale >= 0;
        if (this.fitsInt32() && right.fitsInt32()) {
            this.multiplyDestructiveFitsInt32(right, (short)0, tenScale);
            return;
        }
        final int[] z = multiplyArrays4And4To8(this.v, right.v);
        scaleDownTenArray8RoundUp(z, tenScale);
        this.update(z[0], z[1], z[2], z[3]);
    }
    
    public void divideDestructive(final UnsignedInt128 right, final UnsignedInt128 remainder) {
        if (right.isZero()) {
            assert right.isZero();
            SqlMathUtil.throwZeroDivisionException();
        }
        if (right.count != 1) {
            final int[] quotient = new int[5];
            final int[] rem = SqlMathUtil.divideMultiPrecision(this.v, right.v, quotient);
            this.update(quotient[0], quotient[1], quotient[2], quotient[3]);
            remainder.update(rem[0], rem[1], rem[2], rem[3]);
            return;
        }
        assert right.v[1] == 0;
        assert right.v[2] == 0;
        assert right.v[3] == 0;
        final int rem2 = this.divideDestructive(right.v[0]);
        remainder.update(rem2);
    }
    
    public void divideScaleUpTenDestructive(final UnsignedInt128 right, final short tenScale, final UnsignedInt128 remainder) {
        if (tenScale > 38) {
            SqlMathUtil.throwOverflowException();
        }
        final int[] scaledUp = this.multiplyConstructive256(SqlMathUtil.POWER_TENS_INT128[tenScale]);
        final int[] quotient = new int[5];
        final int[] rem = SqlMathUtil.divideMultiPrecision(scaledUp, right.v, quotient);
        this.update(quotient[0], quotient[1], quotient[2], quotient[3]);
        remainder.update(rem[0], rem[1], rem[2], rem[3]);
    }
    
    public int divideDestructive(final int right) {
        assert right >= 0;
        final long rightUnsigned = (long)right & 0xFFFFFFFFL;
        long remainder = 0L;
        for (int i = 3; i >= 0; --i) {
            remainder = ((long)this.v[i] & 0xFFFFFFFFL) + (remainder << 32);
            final long quotient = remainder / rightUnsigned;
            remainder %= rightUnsigned;
            this.v[i] = (int)quotient;
        }
        this.updateCount();
        return (int)remainder;
    }
    
    public long divideDestructive(final long right) {
        assert right >= 0L;
        long remainder = 0L;
        for (int i = 3; i >= 0; --i) {
            remainder = ((long)this.v[i] & 0xFFFFFFFFL) + (remainder << 32);
            final long quotient = remainder / right;
            remainder %= right;
            this.v[i] = (int)quotient;
        }
        this.updateCount();
        return remainder;
    }
    
    public void shiftRightDestructive(final int bits, final boolean roundUp) {
        assert bits >= 0;
        this.shiftRightDestructive(bits / 32, bits % 32, roundUp);
    }
    
    public void shiftLeftDestructive(final int bits) {
        assert bits >= 0;
        this.shiftLeftDestructive(bits / 32, bits % 32);
    }
    
    public void shiftLeftDestructiveCheckOverflow(final int bits) {
        if (this.bitLength() + bits >= 128) {
            SqlMathUtil.throwOverflowException();
        }
        this.shiftLeftDestructive(bits);
    }
    
    public void scaleDownTenDestructive(final short tenScale) {
        if (tenScale == 0) {
            return;
        }
        if (tenScale < 0) {
            throw new IllegalArgumentException();
        }
        if (this.isZero()) {
            return;
        }
        scaleDownTenArray4RoundUp(this.v, tenScale);
        this.updateCount();
    }
    
    public void scaleDownFiveDestructive(final short fiveScale) {
        if (fiveScale == 0) {
            return;
        }
        if (fiveScale < 0) {
            throw new IllegalArgumentException();
        }
        if (this.isZero()) {
            return;
        }
        scaleDownFiveArrayRoundUp(this.v, fiveScale);
        this.updateCount();
    }
    
    public void scaleUpTenDestructive(final short tenScale) {
        if (tenScale == 0) {
            return;
        }
        if (tenScale < 0) {
            throw new IllegalArgumentException();
        }
        if (this.isZero()) {
            return;
        }
        this.shiftLeftDestructiveCheckOverflow(tenScale);
        this.scaleUpFiveDestructive(tenScale);
    }
    
    public void scaleUpFiveDestructive(short fiveScale) {
        if (fiveScale == 0) {
            return;
        }
        if (fiveScale < 0) {
            throw new IllegalArgumentException();
        }
        if (this.isZero()) {
            return;
        }
        while (fiveScale > 0) {
            final int powerFive = Math.min(fiveScale, 13);
            this.multiplyDestructive(SqlMathUtil.POWER_FIVES_INT31[powerFive]);
            fiveScale -= (short)powerFive;
        }
    }
    
    public UnsignedInt128 addConstructive(final UnsignedInt128 right) {
        final UnsignedInt128 ret = new UnsignedInt128(this);
        ret.addDestructive(right);
        return ret;
    }
    
    public UnsignedInt128 incrementConstructive() {
        final UnsignedInt128 ret = new UnsignedInt128(this);
        ret.incrementDestructive();
        return ret;
    }
    
    public UnsignedInt128 subtractConstructive(final UnsignedInt128 right) {
        final UnsignedInt128 ret = new UnsignedInt128(this);
        ret.subtractDestructive(right);
        return ret;
    }
    
    public UnsignedInt128 decrementConstructive() {
        final UnsignedInt128 ret = new UnsignedInt128(this);
        ret.decrementDestructive();
        return ret;
    }
    
    public UnsignedInt128 multiplyConstructive(final int right) {
        final UnsignedInt128 ret = new UnsignedInt128(this);
        ret.multiplyDestructive(right);
        return ret;
    }
    
    public UnsignedInt128 multiplyConstructive(final UnsignedInt128 right) {
        final UnsignedInt128 ret = new UnsignedInt128(this);
        ret.multiplyDestructive(right);
        return ret;
    }
    
    public int[] multiplyConstructive256(final UnsignedInt128 right) {
        return multiplyArrays4And4To8(this.v, right.v);
    }
    
    public UnsignedInt128 divideConstructive(final int right) {
        final UnsignedInt128 ret = new UnsignedInt128(this);
        ret.divideDestructive(right);
        return ret;
    }
    
    public UnsignedInt128 divideConstructive(final UnsignedInt128 right, final UnsignedInt128 remainder) {
        final UnsignedInt128 ret = new UnsignedInt128(this);
        ret.divideDestructive(right, remainder);
        return ret;
    }
    
    public UnsignedInt128 shiftRightConstructive(final int bits, final boolean roundUp) {
        final UnsignedInt128 ret = new UnsignedInt128(this);
        ret.shiftRightDestructive(bits, roundUp);
        return ret;
    }
    
    public UnsignedInt128 shiftLeftConstructive(final int bits) {
        final UnsignedInt128 ret = new UnsignedInt128(this);
        ret.shiftLeftDestructive(bits);
        return ret;
    }
    
    private short bitLength() {
        return SqlMathUtil.bitLength(this.v[0], this.v[1], this.v[2], this.v[3]);
    }
    
    private void shiftRightDestructive(final int wordShifts, final int bitShiftsInWord, final boolean roundUp) {
        if (wordShifts == 0 && bitShiftsInWord == 0) {
            return;
        }
        assert wordShifts >= 0;
        assert bitShiftsInWord >= 0;
        assert bitShiftsInWord < 32;
        if (wordShifts >= 4) {
            this.zeroClear();
            return;
        }
        final int shiftRestore = 32 - bitShiftsInWord;
        final boolean noRestore = bitShiftsInWord == 0;
        final int roundCarryNoRestoreMask = Integer.MIN_VALUE;
        final int roundCarryMask = 1 << bitShiftsInWord - 1;
        int z0 = 0;
        int z2 = 0;
        int z3 = 0;
        int z4 = 0;
        boolean roundCarry = false;
        switch (wordShifts) {
            case 3: {
                roundCarry = ((noRestore ? (this.v[2] & Integer.MIN_VALUE) : (this.v[3] & roundCarryMask)) != 0);
                z0 = this.v[3] >>> bitShiftsInWord;
                break;
            }
            case 2: {
                roundCarry = ((noRestore ? (this.v[1] & Integer.MIN_VALUE) : (this.v[2] & roundCarryMask)) != 0);
                z2 = this.v[3] >>> bitShiftsInWord;
                z0 = ((noRestore ? 0 : (this.v[3] << shiftRestore)) | this.v[2] >>> bitShiftsInWord);
                break;
            }
            case 1: {
                roundCarry = ((noRestore ? (this.v[0] & Integer.MIN_VALUE) : (this.v[1] & roundCarryMask)) != 0);
                z3 = this.v[3] >>> bitShiftsInWord;
                z2 = ((noRestore ? 0 : (this.v[3] << shiftRestore)) | this.v[2] >>> bitShiftsInWord);
                z0 = ((noRestore ? 0 : (this.v[2] << shiftRestore)) | this.v[1] >>> bitShiftsInWord);
                break;
            }
            case 0: {
                roundCarry = ((noRestore ? 0 : (this.v[0] & roundCarryMask)) != 0);
                z4 = this.v[3] >>> bitShiftsInWord;
                z3 = ((noRestore ? 0 : (this.v[3] << shiftRestore)) | this.v[2] >>> bitShiftsInWord);
                z2 = ((noRestore ? 0 : (this.v[2] << shiftRestore)) | this.v[1] >>> bitShiftsInWord);
                z0 = ((noRestore ? 0 : (this.v[1] << shiftRestore)) | this.v[0] >>> bitShiftsInWord);
                break;
            }
            default: {
                assert false;
                throw new RuntimeException();
            }
        }
        this.update(z0, z2, z3, z4);
        if (roundUp && roundCarry) {
            this.incrementDestructive();
        }
    }
    
    private void shiftLeftDestructive(final int wordShifts, final int bitShiftsInWord) {
        if (wordShifts == 0 && bitShiftsInWord == 0) {
            return;
        }
        assert wordShifts >= 0;
        assert bitShiftsInWord >= 0;
        assert bitShiftsInWord < 32;
        if (wordShifts >= 4) {
            this.zeroClear();
            return;
        }
        final int shiftRestore = 32 - bitShiftsInWord;
        final boolean noRestore = bitShiftsInWord == 0;
        int z0 = 0;
        int z2 = 0;
        int z3 = 0;
        int z4 = 0;
        switch (wordShifts) {
            case 3: {
                z4 = this.v[0] << bitShiftsInWord;
                break;
            }
            case 2: {
                z3 = this.v[0] << bitShiftsInWord;
                z4 = ((noRestore ? 0 : (this.v[0] >>> shiftRestore)) | this.v[1] << bitShiftsInWord);
                break;
            }
            case 1: {
                z2 = this.v[0] << bitShiftsInWord;
                z3 = ((noRestore ? 0 : (this.v[0] >>> shiftRestore)) | this.v[1] << bitShiftsInWord);
                z4 = ((noRestore ? 0 : (this.v[1] >>> shiftRestore)) | this.v[2] << bitShiftsInWord);
                break;
            }
            case 0: {
                z0 = this.v[0] << bitShiftsInWord;
                z2 = ((noRestore ? 0 : (this.v[0] >>> shiftRestore)) | this.v[1] << bitShiftsInWord);
                z3 = ((noRestore ? 0 : (this.v[1] >>> shiftRestore)) | this.v[2] << bitShiftsInWord);
                z4 = ((noRestore ? 0 : (this.v[2] >>> shiftRestore)) | this.v[3] << bitShiftsInWord);
                break;
            }
            default: {
                assert false;
                break;
            }
        }
        this.update(z0, z2, z3, z4);
    }
    
    private static void multiplyArrays4And4To4NoOverflow(final int[] left, final int[] right) {
        assert left.length == 4;
        assert right.length == 4;
        long product = ((long)right[0] & 0xFFFFFFFFL) * ((long)left[0] & 0xFFFFFFFFL);
        final int z0 = (int)product;
        product = ((long)right[0] & 0xFFFFFFFFL) * ((long)left[1] & 0xFFFFFFFFL) + ((long)right[1] & 0xFFFFFFFFL) * ((long)left[0] & 0xFFFFFFFFL) + (product >>> 32);
        final int z2 = (int)product;
        product = ((long)right[0] & 0xFFFFFFFFL) * ((long)left[2] & 0xFFFFFFFFL) + ((long)right[1] & 0xFFFFFFFFL) * ((long)left[1] & 0xFFFFFFFFL) + ((long)right[2] & 0xFFFFFFFFL) * ((long)left[0] & 0xFFFFFFFFL) + (product >>> 32);
        final int z3 = (int)product;
        product = ((long)right[0] & 0xFFFFFFFFL) * ((long)left[3] & 0xFFFFFFFFL) + ((long)right[1] & 0xFFFFFFFFL) * ((long)left[2] & 0xFFFFFFFFL) + ((long)right[2] & 0xFFFFFFFFL) * ((long)left[1] & 0xFFFFFFFFL) + ((long)right[3] & 0xFFFFFFFFL) * ((long)left[0] & 0xFFFFFFFFL) + (product >>> 32);
        final int z4 = (int)product;
        if (product >>> 32 != 0L) {
            SqlMathUtil.throwOverflowException();
        }
        if ((right[3] != 0 && (left[3] != 0 || left[2] != 0 || left[1] != 0)) || (right[2] != 0 && (left[3] != 0 || left[2] != 0)) || (right[1] != 0 && left[3] != 0)) {
            SqlMathUtil.throwOverflowException();
        }
        left[0] = z0;
        left[1] = z2;
        left[2] = z3;
        left[3] = z4;
    }
    
    private static int[] multiplyArrays4And4To8(final int[] left, final int[] right) {
        assert left.length == 4;
        assert right.length == 4;
        final int[] z = new int[8];
        long product = ((long)right[0] & 0xFFFFFFFFL) * ((long)left[0] & 0xFFFFFFFFL);
        z[0] = (int)product;
        product = ((long)right[0] & 0xFFFFFFFFL) * ((long)left[1] & 0xFFFFFFFFL) + ((long)right[1] & 0xFFFFFFFFL) * ((long)left[0] & 0xFFFFFFFFL) + (product >>> 32);
        z[1] = (int)product;
        product = ((long)right[0] & 0xFFFFFFFFL) * ((long)left[2] & 0xFFFFFFFFL) + ((long)right[1] & 0xFFFFFFFFL) * ((long)left[1] & 0xFFFFFFFFL) + ((long)right[2] & 0xFFFFFFFFL) * ((long)left[0] & 0xFFFFFFFFL) + (product >>> 32);
        z[2] = (int)product;
        product = ((long)right[0] & 0xFFFFFFFFL) * ((long)left[3] & 0xFFFFFFFFL) + ((long)right[1] & 0xFFFFFFFFL) * ((long)left[2] & 0xFFFFFFFFL) + ((long)right[2] & 0xFFFFFFFFL) * ((long)left[1] & 0xFFFFFFFFL) + ((long)right[3] & 0xFFFFFFFFL) * ((long)left[0] & 0xFFFFFFFFL) + (product >>> 32);
        z[3] = (int)product;
        product = ((long)right[1] & 0xFFFFFFFFL) * ((long)left[3] & 0xFFFFFFFFL) + ((long)right[2] & 0xFFFFFFFFL) * ((long)left[2] & 0xFFFFFFFFL) + ((long)right[3] & 0xFFFFFFFFL) * ((long)left[1] & 0xFFFFFFFFL) + (product >>> 32);
        z[4] = (int)product;
        product = ((long)right[2] & 0xFFFFFFFFL) * ((long)left[3] & 0xFFFFFFFFL) + ((long)right[3] & 0xFFFFFFFFL) * ((long)left[2] & 0xFFFFFFFFL) + (product >>> 32);
        z[5] = (int)product;
        product = ((long)right[3] & 0xFFFFFFFFL) * ((long)left[3] & 0xFFFFFFFFL) + (product >>> 32);
        z[6] = (int)product;
        z[7] = (int)(product >>> 32);
        return z;
    }
    
    private static void incrementArray(final int[] array) {
        for (int i = 0; i < 4; ++i) {
            if (array[i] != -1) {
                array[i] = (int)(((long)array[i] & 0xFFFFFFFFL) + 1L);
                break;
            }
            array[i] = 0;
            if (i == 3) {
                SqlMathUtil.throwOverflowException();
            }
        }
    }
    
    private static void decrementArray(final int[] array) {
        for (int i = 0; i < 4; ++i) {
            if (array[i] != 0) {
                array[i] = (int)(((long)array[i] & 0xFFFFFFFFL) - 1L);
                break;
            }
            array[i] = -1;
            if (i == 3) {
                SqlMathUtil.throwOverflowException();
            }
        }
    }
    
    private static byte differenceInternal(final UnsignedInt128 left, final int[] r, final UnsignedInt128 result) {
        final int cmp = left.compareTo(r);
        if (cmp == 0) {
            result.zeroClear();
            return 0;
        }
        long sum = 0L;
        if (cmp > 0) {
            for (int i = 0; i < 4; ++i) {
                sum = ((long)left.v[i] & 0xFFFFFFFFL) - ((long)r[i] & 0xFFFFFFFFL) - (int)(-(sum >> 32));
                result.v[i] = (int)sum;
            }
        }
        else {
            for (int i = 0; i < 4; ++i) {
                sum = ((long)r[i] & 0xFFFFFFFFL) - ((long)left.v[i] & 0xFFFFFFFFL) - (int)(-(sum >> 32));
                result.v[i] = (int)sum;
            }
        }
        if (sum >> 32 != 0L) {
            SqlMathUtil.throwOverflowException();
        }
        result.updateCount();
        return (byte)((cmp > 0) ? 1 : -1);
    }
    
    private static int compareTo(final int l0, final int l1, final int l2, final int l3, final int r0, final int r1, final int r2, final int r3) {
        if (l3 != r3) {
            return SqlMathUtil.compareUnsignedInt(l3, r3);
        }
        if (l2 != r2) {
            return SqlMathUtil.compareUnsignedInt(l2, r2);
        }
        if (l1 != r1) {
            return SqlMathUtil.compareUnsignedInt(l1, r1);
        }
        if (l0 != r0) {
            return SqlMathUtil.compareUnsignedInt(l0, r0);
        }
        return 0;
    }
    
    private static boolean scaleUpTenArray(final int[] array, short tenScale) {
        while (tenScale > 0) {
            long sum = 0L;
            final int powerTen = Math.min(tenScale, 9);
            tenScale -= (short)powerTen;
            final long rightUnsigned = (long)SqlMathUtil.POWER_TENS_INT31[powerTen] & 0xFFFFFFFFL;
            for (int i = 0; i < 4; ++i) {
                sum = ((long)array[i] & 0xFFFFFFFFL) * rightUnsigned + (sum >>> 32);
                array[i] = (int)sum;
            }
            if (sum >> 32 != 0L) {
                return true;
            }
        }
        return false;
    }
    
    private static void scaleDownTenArray4RoundUp(final int[] array, final short tenScale) {
        scaleDownFiveArray(array, tenScale);
        shiftRightArray(tenScale, array, array, true);
    }
    
    private static void scaleDownTenArray8RoundUp(final int[] array, final short tenScale) {
        assert array.length == 8;
        if (tenScale > 38) {
            Arrays.fill(array, 0);
            return;
        }
        if (tenScale <= 9) {
            final int divisor = SqlMathUtil.POWER_TENS_INT31[tenScale];
            assert divisor > 0;
            final boolean round = divideCheckRound(array, divisor);
            if (round) {
                incrementArray(array);
            }
        }
        else {
            final int[] inverse = SqlMathUtil.INVERSE_POWER_TENS_INT128[tenScale].v;
            final int inverseWordShift = SqlMathUtil.INVERSE_POWER_TENS_INT128_WORD_SHIFTS[tenScale];
            assert inverseWordShift <= 3;
            assert inverse[3] != 0;
            for (int i = 5 + inverseWordShift; i < 8; ++i) {
                if (array[i] != 0) {
                    SqlMathUtil.throwOverflowException();
                }
            }
            int z4 = 0;
            int z5 = 0;
            int z6 = 0;
            int z7 = 0;
            int z8 = 0;
            int z9 = 0;
            int z10 = 0;
            long product = 0L;
            product += ((long)inverse[0] & 0xFFFFFFFFL) * ((long)array[4] & 0xFFFFFFFFL) + ((long)inverse[1] & 0xFFFFFFFFL) * ((long)array[3] & 0xFFFFFFFFL) + ((long)inverse[2] & 0xFFFFFFFFL) * ((long)array[2] & 0xFFFFFFFFL) + ((long)inverse[3] & 0xFFFFFFFFL) * ((long)array[1] & 0xFFFFFFFFL);
            z4 = (int)product;
            product >>>= 32;
            product += ((long)inverse[0] & 0xFFFFFFFFL) * ((long)array[5] & 0xFFFFFFFFL) + ((long)inverse[1] & 0xFFFFFFFFL) * ((long)array[4] & 0xFFFFFFFFL) + ((long)inverse[2] & 0xFFFFFFFFL) * ((long)array[3] & 0xFFFFFFFFL) + ((long)inverse[3] & 0xFFFFFFFFL) * ((long)array[2] & 0xFFFFFFFFL);
            z5 = (int)product;
            product >>>= 32;
            product += ((long)inverse[0] & 0xFFFFFFFFL) * ((long)array[6] & 0xFFFFFFFFL) + ((long)inverse[1] & 0xFFFFFFFFL) * ((long)array[5] & 0xFFFFFFFFL) + ((long)inverse[2] & 0xFFFFFFFFL) * ((long)array[4] & 0xFFFFFFFFL) + ((long)inverse[3] & 0xFFFFFFFFL) * ((long)array[3] & 0xFFFFFFFFL);
            z6 = (int)product;
            product >>>= 32;
            product += ((long)inverse[0] & 0xFFFFFFFFL) * ((long)array[7] & 0xFFFFFFFFL) + ((long)inverse[1] & 0xFFFFFFFFL) * ((long)array[6] & 0xFFFFFFFFL) + ((long)inverse[2] & 0xFFFFFFFFL) * ((long)array[5] & 0xFFFFFFFFL) + ((long)inverse[3] & 0xFFFFFFFFL) * ((long)array[4] & 0xFFFFFFFFL);
            z7 = (int)product;
            product >>>= 32;
            if (inverseWordShift >= 1) {
                product += ((long)inverse[1] & 0xFFFFFFFFL) * ((long)array[7] & 0xFFFFFFFFL) + ((long)inverse[2] & 0xFFFFFFFFL) * ((long)array[6] & 0xFFFFFFFFL) + ((long)inverse[3] & 0xFFFFFFFFL) * ((long)array[5] & 0xFFFFFFFFL);
                z8 = (int)product;
                product >>>= 32;
                if (inverseWordShift >= 2) {
                    product += ((long)inverse[2] & 0xFFFFFFFFL) * ((long)array[7] & 0xFFFFFFFFL) + ((long)inverse[3] & 0xFFFFFFFFL) * ((long)array[6] & 0xFFFFFFFFL);
                    z9 = (int)product;
                    product >>>= 32;
                    if (inverseWordShift >= 3) {
                        product += ((long)inverse[3] & 0xFFFFFFFFL) * ((long)array[7] & 0xFFFFFFFFL);
                        z10 = (int)product;
                        product >>>= 32;
                    }
                }
            }
            if (product != 0L) {
                SqlMathUtil.throwOverflowException();
            }
            switch (inverseWordShift) {
                case 1: {
                    z4 = z5;
                    z5 = z6;
                    z6 = z7;
                    z7 = z8;
                    break;
                }
                case 2: {
                    z4 = z6;
                    z5 = z7;
                    z6 = z8;
                    z7 = z9;
                    break;
                }
                case 3: {
                    z4 = z7;
                    z5 = z8;
                    z6 = z9;
                    z7 = z10;
                    break;
                }
            }
            final int[] power = SqlMathUtil.POWER_TENS_INT128[tenScale].v;
            final int[] half = SqlMathUtil.ROUND_POWER_TENS_INT128[tenScale].v;
            product = ((long)array[0] & 0xFFFFFFFFL) - ((long)power[0] & 0xFFFFFFFFL) * ((long)z4 & 0xFFFFFFFFL);
            final int d0 = (int)product;
            product = ((long)array[1] & 0xFFFFFFFFL) - ((long)power[0] & 0xFFFFFFFFL) * ((long)z5 & 0xFFFFFFFFL) - ((long)power[1] & 0xFFFFFFFFL) * ((long)z4 & 0xFFFFFFFFL) - (int)(-(product >> 32));
            final int d2 = (int)product;
            product = ((long)array[2] & 0xFFFFFFFFL) - ((long)power[0] & 0xFFFFFFFFL) * ((long)z6 & 0xFFFFFFFFL) - ((long)power[1] & 0xFFFFFFFFL) * ((long)z5 & 0xFFFFFFFFL) - ((long)power[2] & 0xFFFFFFFFL) * ((long)z4 & 0xFFFFFFFFL) - (int)(-(product >> 32));
            final int d3 = (int)product;
            product = ((long)array[3] & 0xFFFFFFFFL) - ((long)power[0] & 0xFFFFFFFFL) * ((long)z7 & 0xFFFFFFFFL) - ((long)power[1] & 0xFFFFFFFFL) * ((long)z6 & 0xFFFFFFFFL) - ((long)power[2] & 0xFFFFFFFFL) * ((long)z5 & 0xFFFFFFFFL) - ((long)power[3] & 0xFFFFFFFFL) * ((long)z4 & 0xFFFFFFFFL) - (int)(-(product >> 32));
            final int d4 = (int)product;
            product = ((long)array[4] & 0xFFFFFFFFL) - ((long)power[1] & 0xFFFFFFFFL) * ((long)z7 & 0xFFFFFFFFL) - ((long)power[2] & 0xFFFFFFFFL) * ((long)z6 & 0xFFFFFFFFL) - ((long)power[3] & 0xFFFFFFFFL) * ((long)z5 & 0xFFFFFFFFL) - (int)(-(product >> 32));
            final int d5 = (int)product;
            final boolean increment = d5 != 0 || compareTo(d0, d2, d3, d4, half[0], half[1], half[2], half[3]) >= 0;
            array[0] = z4;
            array[1] = z5;
            array[2] = z6;
            array[3] = z7;
            if (increment) {
                incrementArray(array);
            }
        }
    }
    
    private static boolean scaleDownFiveArray(final int[] array, short fiveScale) {
        while (true) {
            final int powerFive = Math.min(fiveScale, 13);
            fiveScale -= (short)powerFive;
            final int divisor = SqlMathUtil.POWER_FIVES_INT31[powerFive];
            assert divisor > 0;
            if (fiveScale == 0) {
                return divideCheckRound(array, divisor);
            }
            divideCheckRound(array, divisor);
        }
    }
    
    private static boolean divideCheckRound(final int[] array, final int divisor) {
        long remainder = 0L;
        for (int i = array.length - 1; i >= 0; --i) {
            remainder = ((long)array[i] & 0xFFFFFFFFL) + (remainder << 32);
            array[i] = (int)(remainder / divisor);
            remainder %= divisor;
        }
        return remainder >= divisor >> 1;
    }
    
    private static void scaleDownFiveArrayRoundUp(final int[] array, final short tenScale) {
        final boolean rounding = scaleDownFiveArray(array, tenScale);
        if (rounding) {
            incrementArray(array);
        }
    }
    
    private static void shiftRightArray(final int rightShifts, final int[] z, final int[] result, final boolean round) {
        assert rightShifts >= 0;
        if (rightShifts == 0) {
            for (int i = 0; i < 4; ++i) {
                if (z[i + 4] != 0) {
                    SqlMathUtil.throwOverflowException();
                }
            }
            result[0] = z[0];
            result[1] = z[1];
            result[2] = z[2];
            result[3] = z[3];
        }
        else {
            final int wordShifts = rightShifts / 32;
            final int bitShiftsInWord = rightShifts % 32;
            final int shiftRestore = 32 - bitShiftsInWord;
            final boolean noRestore = bitShiftsInWord == 0;
            if (z.length > 4) {
                if (wordShifts + 4 < z.length && z[wordShifts + 4] >>> bitShiftsInWord != 0) {
                    SqlMathUtil.throwOverflowException();
                }
                for (int j = 1; j < 4; ++j) {
                    if (j + wordShifts < z.length - 4 && z[j + wordShifts + 4] != 0) {
                        SqlMathUtil.throwOverflowException();
                    }
                }
            }
            boolean roundCarry = false;
            if (round) {
                if (bitShiftsInWord == 0) {
                    assert wordShifts > 0;
                    roundCarry = (z[wordShifts - 1] < 0);
                }
                else {
                    roundCarry = ((z[wordShifts] & 1 << bitShiftsInWord - 1) != 0x0);
                }
            }
            for (int k = 0; k < 4; ++k) {
                int val = 0;
                if (!noRestore && k + wordShifts + 1 < z.length) {
                    val = z[k + wordShifts + 1] << shiftRestore;
                }
                if (k + wordShifts < z.length) {
                    val |= z[k + wordShifts] >>> bitShiftsInWord;
                }
                result[k] = val;
            }
            if (roundCarry) {
                incrementArray(result);
            }
        }
    }
    
    private void multiplyDestructiveFitsInt32(final UnsignedInt128 right, final short rightShifts, final short tenScaleDown) {
        assert this.fitsInt32() && right.fitsInt32();
        assert tenScaleDown == 0;
        if (this.isZero()) {
            return;
        }
        if (right.isZero()) {
            this.zeroClear();
            return;
        }
        if (this.isOne()) {
            this.update(right);
        }
        else {
            this.multiplyDestructive(right.v[0]);
        }
        if (rightShifts > 0) {
            this.shiftRightDestructive(rightShifts, true);
        }
        else if (tenScaleDown > 0) {
            this.scaleDownTenDestructive(tenScaleDown);
        }
    }
    
    private void updateCount() {
        if (this.v[3] != 0) {
            this.count = 4;
        }
        else if (this.v[2] != 0) {
            this.count = 3;
        }
        else if (this.v[1] != 0) {
            this.count = 2;
        }
        else if (this.v[0] != 0) {
            this.count = 1;
        }
        else {
            this.count = 0;
        }
    }
    
    private static void fastSerializeIntPartForHiveDecimal(final ByteBuffer buf, final int pos, int value, final byte signum, final boolean isFirstNonZero) {
        if (signum == -1 && value != 0) {
            value = (isFirstNonZero ? (-value) : (~value));
        }
        buf.putInt(pos, value);
    }
    
    public int fastSerializeForHiveDecimal(final Decimal128FastBuffer scratch, final byte signum) {
        final int bufferUsed = this.count;
        final ByteBuffer buf = scratch.getByteBuffer(bufferUsed);
        buf.put(0, (byte)((signum == 1) ? 0 : signum));
        int pos = 1;
        int firstNonZero;
        for (firstNonZero = 0; firstNonZero < this.count && this.v[firstNonZero] == 0; ++firstNonZero) {}
        switch (this.count) {
            case 4: {
                fastSerializeIntPartForHiveDecimal(buf, pos, this.v[3], signum, firstNonZero == 3);
                pos += 4;
            }
            case 3: {
                fastSerializeIntPartForHiveDecimal(buf, pos, this.v[2], signum, firstNonZero == 2);
                pos += 4;
            }
            case 2: {
                fastSerializeIntPartForHiveDecimal(buf, pos, this.v[1], signum, firstNonZero == 1);
                pos += 4;
            }
            case 1: {
                fastSerializeIntPartForHiveDecimal(buf, pos, this.v[0], signum, true);
                break;
            }
        }
        return bufferUsed;
    }
    
    public byte fastUpdateFromInternalStorage(final byte[] internalStorage) {
        byte signum = 0;
        int skip = 0;
        this.count = 0;
        final byte firstByte = internalStorage[0];
        if (firstByte == 0 || firstByte == -1) {
            while (skip < internalStorage.length && internalStorage[skip] == firstByte) {
                ++skip;
            }
        }
        if (skip == internalStorage.length) {
            assert firstByte == -1;
            if (firstByte == -1) {
                signum = -1;
                this.count = 1;
                this.v[0] = 1;
            }
            else {
                signum = 0;
            }
        }
        else {
            signum = (byte)((firstByte < 0) ? -1 : 1);
            final int length = internalStorage.length - skip;
            int pos = skip;
            int intLength = 0;
            switch (length) {
                case 16: {
                    ++intLength;
                }
                case 15: {
                    ++intLength;
                }
                case 14: {
                    ++intLength;
                }
                case 13: {
                    ++intLength;
                    this.v[3] = this.fastUpdateIntFromInternalStorage(internalStorage, signum, pos, intLength);
                    ++this.count;
                    pos += intLength;
                    intLength = 0;
                }
                case 12: {
                    ++intLength;
                }
                case 11: {
                    ++intLength;
                }
                case 10: {
                    ++intLength;
                }
                case 9: {
                    ++intLength;
                    this.v[2] = this.fastUpdateIntFromInternalStorage(internalStorage, signum, pos, intLength);
                    ++this.count;
                    pos += intLength;
                    intLength = 0;
                }
                case 8: {
                    ++intLength;
                }
                case 7: {
                    ++intLength;
                }
                case 6: {
                    ++intLength;
                }
                case 5: {
                    ++intLength;
                    this.v[1] = this.fastUpdateIntFromInternalStorage(internalStorage, signum, pos, intLength);
                    ++this.count;
                    pos += intLength;
                    intLength = 0;
                }
                case 4: {
                    ++intLength;
                }
                case 3: {
                    ++intLength;
                }
                case 2: {
                    ++intLength;
                }
                case 1: {
                    ++intLength;
                    this.v[0] = this.fastUpdateIntFromInternalStorage(internalStorage, signum, pos, intLength);
                    ++this.count;
                    if (signum == -1) {
                        for (int i = 0; i < this.count; ++i) {
                            if (this.v[i] != 0) {
                                this.v[i] = (int)(((long)this.v[i] & 0xFFFFFFFFL) + 1L);
                                if (this.v[i] != 0) {
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    break;
                }
                default: {
                    throw new RuntimeException("Impossible HiveDecimal internal storage length!");
                }
            }
        }
        return signum;
    }
    
    private int fastUpdateIntFromInternalStorage(final byte[] internalStorage, final byte signum, int pos, final int length) {
        byte b4;
        byte b3;
        byte b2;
        if (signum == -1) {
            b2 = (b3 = (b4 = -1));
        }
        else {
            b2 = (b3 = (b4 = 0));
        }
        switch (length) {
            case 4: {
                b4 = internalStorage[pos];
                ++pos;
            }
            case 3: {
                b2 = internalStorage[pos];
                ++pos;
            }
            case 2: {
                b3 = internalStorage[pos];
                ++pos;
            }
            case 1: {
                final byte b5 = internalStorage[pos];
                int value = (b5 & 0xFF) | (b3 << 8 & 0xFF00) | (b2 << 16 & 0xFF0000) | (b4 << 24 & 0xFF000000);
                if (signum == -1 && value != 0) {
                    final int mask = -1 >>> 8 * (4 - length);
                    value = (~value & mask);
                }
                return value;
            }
            default: {
                throw new RuntimeException("Impossible HiveDecimal internal storage position!");
            }
        }
    }
    
    public int[] getV() {
        return this.v;
    }
    
    public void setV(final int[] v) {
        this.v[0] = v[0];
        this.v[1] = v[1];
        this.v[2] = v[2];
        this.v[3] = v[3];
        this.updateCount();
    }
    
    public byte getCount() {
        return this.count;
    }
    
    public void setCount(final byte count) {
        this.count = count;
    }
    
    static {
        MAX_VALUE = new UnsignedInt128(-1, -1, -1, -1);
        MIN_VALUE = new UnsignedInt128(0L);
        TEN_TO_THIRTYEIGHT = new UnsignedInt128(0, 160047680, 1518781562, 1262177448);
    }
}
