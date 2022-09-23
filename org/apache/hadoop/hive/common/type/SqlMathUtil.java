// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.type;

import java.util.Arrays;

public final class SqlMathUtil
{
    public static final long NEGATIVE_LONG_MASK = Long.MIN_VALUE;
    public static final long FULLBITS_63 = Long.MAX_VALUE;
    public static final int NEGATIVE_INT_MASK = Integer.MIN_VALUE;
    public static final long LONG_MASK = 4294967295L;
    public static final int FULLBITS_31 = Integer.MAX_VALUE;
    public static final int FULLBITS_32 = -1;
    public static final int MAX_POWER_FIVE_INT31 = 13;
    public static final int[] POWER_FIVES_INT31;
    public static final int MAX_POWER_FIVE_INT63 = 27;
    public static final long[] POWER_FIVES_INT63;
    public static final int MAX_POWER_FIVE_INT128 = 55;
    public static final UnsignedInt128[] POWER_FIVES_INT128;
    public static final UnsignedInt128[] INVERSE_POWER_FIVES_INT128;
    public static final int MAX_POWER_TEN_INT31 = 9;
    public static final int[] POWER_TENS_INT31;
    public static final int[] ROUND_POWER_TENS_INT31;
    public static final int MAX_POWER_TEN_INT128 = 38;
    public static final UnsignedInt128[] POWER_TENS_INT128;
    public static final UnsignedInt128[] ROUND_POWER_TENS_INT128;
    public static final UnsignedInt128[] INVERSE_POWER_TENS_INT128;
    public static final int[] INVERSE_POWER_TENS_INT128_WORD_SHIFTS;
    private static final byte[] BIT_LENGTH;
    private static final long BASE = 4294967296L;
    
    public static int setSignBitInt(final int val, final boolean positive) {
        if (positive) {
            return val & Integer.MAX_VALUE;
        }
        return val | Integer.MIN_VALUE;
    }
    
    public static long setSignBitLong(final long val, final boolean positive) {
        if (positive) {
            return val & Long.MAX_VALUE;
        }
        return val | Long.MIN_VALUE;
    }
    
    public static short bitLengthInWord(final int word) {
        if (word < 0) {
            return 32;
        }
        if (word < 65536) {
            if (word < 256) {
                return SqlMathUtil.BIT_LENGTH[word];
            }
            return (short)(SqlMathUtil.BIT_LENGTH[word >>> 8] + 8);
        }
        else {
            if (word < 16777216) {
                return (short)(SqlMathUtil.BIT_LENGTH[word >>> 16] + 16);
            }
            return (short)(SqlMathUtil.BIT_LENGTH[word >>> 24] + 24);
        }
    }
    
    public static short bitLength(final int v0, final int v1, final int v2, final int v3) {
        if (v3 != 0) {
            return (short)(bitLengthInWord(v3) + 96);
        }
        if (v2 != 0) {
            return (short)(bitLengthInWord(v2) + 64);
        }
        if (v1 != 0) {
            return (short)(bitLengthInWord(v1) + 32);
        }
        return bitLengthInWord(v0);
    }
    
    public static int compareUnsignedInt(final int x, final int y) {
        if (x == y) {
            return 0;
        }
        if (x + Integer.MIN_VALUE < y + Integer.MIN_VALUE) {
            return -1;
        }
        return 1;
    }
    
    public static int compareUnsignedLong(final long x, final long y) {
        if (x == y) {
            return 0;
        }
        if (x + Long.MIN_VALUE < y + Long.MIN_VALUE) {
            return -1;
        }
        return 1;
    }
    
    public static long divideUnsignedLong(final long dividend, final long divisor) {
        if (divisor < 0L) {
            return (compareUnsignedLong(dividend, divisor) >= 0) ? 1 : 0;
        }
        if (dividend >= 0L) {
            return dividend / divisor;
        }
        final long quotient = (dividend >>> 1) / divisor << 1;
        final long remainder = dividend - quotient * divisor;
        if (compareUnsignedLong(remainder, divisor) >= 0) {
            return quotient + 1L;
        }
        return quotient;
    }
    
    public static long remainderUnsignedLong(final long dividend, final long divisor) {
        if (divisor < 0L) {
            return (compareUnsignedLong(dividend, divisor) < 0) ? dividend : (dividend - divisor);
        }
        if (dividend >= 0L) {
            return dividend % divisor;
        }
        final long quotient = (dividend >>> 1) / divisor << 1;
        final long remainder = dividend - quotient * divisor;
        if (compareUnsignedLong(remainder, divisor) >= 0) {
            return remainder - divisor;
        }
        return remainder;
    }
    
    public static long combineInts(final int lo, final int hi) {
        return ((long)hi & 0xFFFFFFFFL) << 32 | ((long)lo & 0xFFFFFFFFL);
    }
    
    public static int extractHiInt(final long val) {
        return (int)(val >> 32);
    }
    
    public static int extractLowInt(final long val) {
        return (int)val;
    }
    
    static void throwOverflowException() {
        throw new ArithmeticException("Overflow");
    }
    
    static void throwZeroDivisionException() {
        throw new ArithmeticException("Divide by zero");
    }
    
    private static void multiplyMultiPrecision(final int[] inOut, final int multiplier) {
        final long multiplierUnsigned = (long)multiplier & 0xFFFFFFFFL;
        long product = 0L;
        for (int i = 0; i < inOut.length; ++i) {
            product = ((long)inOut[i] & 0xFFFFFFFFL) * multiplierUnsigned + (product >>> 32);
            inOut[i] = (int)product;
        }
        if (product >> 32 != 0L) {
            throwOverflowException();
        }
    }
    
    private static int divideMultiPrecision(final int[] inOut, final int divisor) {
        final long divisorUnsigned = (long)divisor & 0xFFFFFFFFL;
        long remainder = 0L;
        for (int i = inOut.length - 1; i >= 0; --i) {
            remainder = ((long)inOut[i] & 0xFFFFFFFFL) + (remainder << 32);
            final long quotient = remainder / divisorUnsigned;
            inOut[i] = (int)quotient;
            remainder %= divisorUnsigned;
        }
        return (int)remainder;
    }
    
    private static int arrayValidLength(final int[] array) {
        int len;
        for (len = array.length; len > 0 && array[len - 1] == 0; --len) {}
        return (len <= 0) ? 0 : len;
    }
    
    public static int[] divideMultiPrecision(final int[] dividend, int[] divisor, final int[] quotient) {
        final int dividendLength = arrayValidLength(dividend);
        final int divisorLength = arrayValidLength(divisor);
        Arrays.fill(quotient, 0);
        final int[] remainder = new int[dividend.length + 1];
        System.arraycopy(dividend, 0, remainder, 0, dividend.length);
        remainder[remainder.length - 1] = 0;
        if (divisorLength == 0) {
            throwZeroDivisionException();
        }
        if (dividendLength < divisorLength) {
            return remainder;
        }
        if (divisorLength == 1) {
            final int rem = divideMultiPrecision(remainder, divisor[0]);
            System.arraycopy(remainder, 0, quotient, 0, quotient.length);
            Arrays.fill(remainder, 0);
            remainder[0] = rem;
            return remainder;
        }
        final int d1 = (int)(4294967296L / (((long)divisor[divisorLength - 1] & 0xFFFFFFFFL) + 1L));
        if (d1 > 1) {
            final int[] newDivisor = new int[divisorLength];
            System.arraycopy(divisor, 0, newDivisor, 0, divisorLength);
            multiplyMultiPrecision(newDivisor, d1);
            divisor = newDivisor;
            multiplyMultiPrecision(remainder, d1);
        }
        final long dHigh = (long)divisor[divisorLength - 1] & 0xFFFFFFFFL;
        final long dLow = (long)divisor[divisorLength - 2] & 0xFFFFFFFFL;
        for (int rIndex = remainder.length - 1; rIndex >= divisorLength; --rIndex) {
            long accum = combineInts(remainder[rIndex - 1], remainder[rIndex]);
            int qhat;
            if (dHigh == ((long)remainder[rIndex] & 0xFFFFFFFFL)) {
                qhat = -1;
            }
            else {
                qhat = (int)divideUnsignedLong(accum, dHigh);
            }
            for (int rhat = (int)(accum - ((long)qhat & 0xFFFFFFFFL) * dHigh); compareUnsignedLong(dLow * ((long)qhat & 0xFFFFFFFFL), combineInts(remainder[rIndex - 2], rhat)) > 0; rhat += (int)dHigh) {
                --qhat;
                if (((long)rhat & 0xFFFFFFFFL) >= -(int)dHigh) {
                    break;
                }
            }
            long dwlMulAccum = 0L;
            accum = 4294967296L;
            int iulRwork = rIndex - divisorLength;
            for (int dIndex = 0; dIndex < divisorLength; ++dIndex, ++iulRwork) {
                dwlMulAccum += ((long)qhat & 0xFFFFFFFFL) * ((long)divisor[dIndex] & 0xFFFFFFFFL);
                accum += ((long)remainder[iulRwork] & 0xFFFFFFFFL) - ((long)extractLowInt(dwlMulAccum) & 0xFFFFFFFFL);
                dwlMulAccum = ((long)extractHiInt(dwlMulAccum) & 0xFFFFFFFFL);
                remainder[iulRwork] = extractLowInt(accum);
                accum = ((long)extractHiInt(accum) & 0xFFFFFFFFL) + 4294967296L - 1L;
            }
            accum += ((long)remainder[iulRwork] & 0xFFFFFFFFL) - dwlMulAccum;
            remainder[iulRwork] = extractLowInt(accum);
            quotient[rIndex - divisorLength] = qhat;
            if (extractHiInt(accum) == 0) {
                quotient[rIndex - divisorLength] = qhat - 1;
                int carry = 0;
                int dIndex2;
                for (dIndex2 = 0, iulRwork = rIndex - divisorLength; dIndex2 < divisorLength; ++dIndex2, ++iulRwork) {
                    final long accum2 = ((long)divisor[dIndex2] & 0xFFFFFFFFL) + ((long)remainder[iulRwork] & 0xFFFFFFFFL) + ((long)carry & 0xFFFFFFFFL);
                    carry = extractHiInt(accum2);
                    remainder[iulRwork] = extractLowInt(accum2);
                }
                final int[] array = remainder;
                final int n = iulRwork;
                array[n] += carry;
            }
        }
        if (d1 > 1) {
            divideMultiPrecision(remainder, d1);
        }
        return remainder;
    }
    
    private SqlMathUtil() {
    }
    
    static {
        POWER_FIVES_INT31 = new int[14];
        POWER_FIVES_INT63 = new long[28];
        POWER_FIVES_INT128 = new UnsignedInt128[56];
        INVERSE_POWER_FIVES_INT128 = new UnsignedInt128[56];
        POWER_TENS_INT31 = new int[10];
        ROUND_POWER_TENS_INT31 = new int[10];
        POWER_TENS_INT128 = new UnsignedInt128[39];
        ROUND_POWER_TENS_INT128 = new UnsignedInt128[39];
        INVERSE_POWER_TENS_INT128 = new UnsignedInt128[39];
        INVERSE_POWER_TENS_INT128_WORD_SHIFTS = new int[39];
        (BIT_LENGTH = new byte[256])[0] = 0;
        for (int i = 1; i < 8; ++i) {
            for (int j = 1 << i - 1; j < 1 << i; ++j) {
                SqlMathUtil.BIT_LENGTH[j] = (byte)i;
            }
        }
        SqlMathUtil.POWER_FIVES_INT31[0] = 1;
        for (int i = 1; i < SqlMathUtil.POWER_FIVES_INT31.length; ++i) {
            SqlMathUtil.POWER_FIVES_INT31[i] = SqlMathUtil.POWER_FIVES_INT31[i - 1] * 5;
            assert SqlMathUtil.POWER_FIVES_INT31[i] > 0;
        }
        SqlMathUtil.POWER_FIVES_INT63[0] = 1L;
        for (int i = 1; i < SqlMathUtil.POWER_FIVES_INT63.length; ++i) {
            SqlMathUtil.POWER_FIVES_INT63[i] = SqlMathUtil.POWER_FIVES_INT63[i - 1] * 5L;
            assert SqlMathUtil.POWER_FIVES_INT63[i] > 0L;
        }
        SqlMathUtil.POWER_TENS_INT31[0] = 1;
        SqlMathUtil.ROUND_POWER_TENS_INT31[0] = 0;
        for (int i = 1; i < SqlMathUtil.POWER_TENS_INT31.length; ++i) {
            SqlMathUtil.POWER_TENS_INT31[i] = SqlMathUtil.POWER_TENS_INT31[i - 1] * 10;
            assert SqlMathUtil.POWER_TENS_INT31[i] > 0;
            SqlMathUtil.ROUND_POWER_TENS_INT31[i] = SqlMathUtil.POWER_TENS_INT31[i] >> 1;
        }
        SqlMathUtil.POWER_FIVES_INT128[0] = new UnsignedInt128(1L);
        SqlMathUtil.INVERSE_POWER_FIVES_INT128[0] = new UnsignedInt128(-1, -1, -1, -1);
        for (int i = 1; i < SqlMathUtil.POWER_FIVES_INT128.length; ++i) {
            (SqlMathUtil.POWER_FIVES_INT128[i] = new UnsignedInt128(SqlMathUtil.POWER_FIVES_INT128[i - 1])).multiplyDestructive(5);
            (SqlMathUtil.INVERSE_POWER_FIVES_INT128[i] = new UnsignedInt128(SqlMathUtil.INVERSE_POWER_FIVES_INT128[i - 1])).divideDestructive(5);
        }
        SqlMathUtil.POWER_TENS_INT128[0] = new UnsignedInt128(1L);
        SqlMathUtil.ROUND_POWER_TENS_INT128[0] = new UnsignedInt128(0L);
        SqlMathUtil.INVERSE_POWER_TENS_INT128[0] = new UnsignedInt128(-1, -1, -1, -1);
        SqlMathUtil.INVERSE_POWER_TENS_INT128_WORD_SHIFTS[0] = 0;
        final int[] inverseTens = new int[8];
        Arrays.fill(inverseTens, -1);
        for (int k = 1; k < SqlMathUtil.POWER_TENS_INT128.length; ++k) {
            final int divisor = 10;
            (SqlMathUtil.POWER_TENS_INT128[k] = new UnsignedInt128(SqlMathUtil.POWER_TENS_INT128[k - 1])).multiplyDestructive(10);
            SqlMathUtil.ROUND_POWER_TENS_INT128[k] = SqlMathUtil.POWER_TENS_INT128[k].shiftRightConstructive(1, false);
            long remainder = 0L;
            for (int l = inverseTens.length - 1; l >= 0; --l) {
                final long quotient = (((long)inverseTens[l] & 0xFFFFFFFFL) + (remainder << 32)) / 10L;
                remainder = (((long)inverseTens[l] & 0xFFFFFFFFL) + (remainder << 32)) % 10L;
                inverseTens[l] = (int)quotient;
            }
            int wordShifts = 0;
            for (int m = inverseTens.length - 1; m >= 4 && inverseTens[m] == 0; --m) {
                ++wordShifts;
            }
            SqlMathUtil.INVERSE_POWER_TENS_INT128_WORD_SHIFTS[k] = wordShifts;
            SqlMathUtil.INVERSE_POWER_TENS_INT128[k] = new UnsignedInt128(inverseTens[inverseTens.length - 4 - wordShifts], inverseTens[inverseTens.length - 3 - wordShifts], inverseTens[inverseTens.length - 2 - wordShifts], inverseTens[inverseTens.length - 1 - wordShifts]);
        }
    }
}
