// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.util;

import java.math.BigInteger;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.util.concurrent.atomic.AtomicReference;

public final class ArithmeticUtils
{
    static final long[] FACTORIALS;
    static final AtomicReference<long[][]> STIRLING_S2;
    
    private ArithmeticUtils() {
    }
    
    public static int addAndCheck(final int x, final int y) throws MathArithmeticException {
        final long s = x + (long)y;
        if (s < -2147483648L || s > 2147483647L) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, new Object[] { x, y });
        }
        return (int)s;
    }
    
    public static long addAndCheck(final long a, final long b) throws MathArithmeticException {
        return addAndCheck(a, b, LocalizedFormats.OVERFLOW_IN_ADDITION);
    }
    
    public static long binomialCoefficient(final int n, final int k) throws NotPositiveException, NumberIsTooLargeException, MathArithmeticException {
        checkBinomial(n, k);
        if (n == k || k == 0) {
            return 1L;
        }
        if (k == 1 || k == n - 1) {
            return n;
        }
        if (k > n / 2) {
            return binomialCoefficient(n, n - k);
        }
        long result = 1L;
        if (n <= 61) {
            int i = n - k + 1;
            for (int j = 1; j <= k; ++j) {
                result = result * i / j;
                ++i;
            }
        }
        else if (n <= 66) {
            int i = n - k + 1;
            for (int j = 1; j <= k; ++j) {
                final long d = gcd(i, j);
                result = result / (j / d) * (i / d);
                ++i;
            }
        }
        else {
            int i = n - k + 1;
            for (int j = 1; j <= k; ++j) {
                final long d = gcd(i, j);
                result = mulAndCheck(result / (j / d), i / d);
                ++i;
            }
        }
        return result;
    }
    
    public static double binomialCoefficientDouble(final int n, final int k) throws NotPositiveException, NumberIsTooLargeException, MathArithmeticException {
        checkBinomial(n, k);
        if (n == k || k == 0) {
            return 1.0;
        }
        if (k == 1 || k == n - 1) {
            return n;
        }
        if (k > n / 2) {
            return binomialCoefficientDouble(n, n - k);
        }
        if (n < 67) {
            return (double)binomialCoefficient(n, k);
        }
        double result = 1.0;
        for (int i = 1; i <= k; ++i) {
            result *= (n - k + i) / (double)i;
        }
        return FastMath.floor(result + 0.5);
    }
    
    public static double binomialCoefficientLog(final int n, final int k) throws NotPositiveException, NumberIsTooLargeException, MathArithmeticException {
        checkBinomial(n, k);
        if (n == k || k == 0) {
            return 0.0;
        }
        if (k == 1 || k == n - 1) {
            return FastMath.log(n);
        }
        if (n < 67) {
            return FastMath.log((double)binomialCoefficient(n, k));
        }
        if (n < 1030) {
            return FastMath.log(binomialCoefficientDouble(n, k));
        }
        if (k > n / 2) {
            return binomialCoefficientLog(n, n - k);
        }
        double logSum = 0.0;
        for (int i = n - k + 1; i <= n; ++i) {
            logSum += FastMath.log(i);
        }
        for (int i = 2; i <= k; ++i) {
            logSum -= FastMath.log(i);
        }
        return logSum;
    }
    
    public static long factorial(final int n) throws NotPositiveException, MathArithmeticException {
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.FACTORIAL_NEGATIVE_PARAMETER, n);
        }
        if (n > 20) {
            throw new MathArithmeticException();
        }
        return ArithmeticUtils.FACTORIALS[n];
    }
    
    public static double factorialDouble(final int n) throws NotPositiveException {
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.FACTORIAL_NEGATIVE_PARAMETER, n);
        }
        if (n < 21) {
            return (double)ArithmeticUtils.FACTORIALS[n];
        }
        return FastMath.floor(FastMath.exp(factorialLog(n)) + 0.5);
    }
    
    public static double factorialLog(final int n) throws NotPositiveException {
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.FACTORIAL_NEGATIVE_PARAMETER, n);
        }
        if (n < 21) {
            return FastMath.log((double)ArithmeticUtils.FACTORIALS[n]);
        }
        double logSum = 0.0;
        for (int i = 2; i <= n; ++i) {
            logSum += FastMath.log(i);
        }
        return logSum;
    }
    
    public static int gcd(final int p, final int q) throws MathArithmeticException {
        int a = p;
        int b = q;
        if (a != 0 && b != 0) {
            long al = a;
            long bl = b;
            boolean useLong = false;
            if (a < 0) {
                if (Integer.MIN_VALUE == a) {
                    useLong = true;
                }
                else {
                    a = -a;
                }
                al = -al;
            }
            if (b < 0) {
                if (Integer.MIN_VALUE == b) {
                    useLong = true;
                }
                else {
                    b = -b;
                }
                bl = -bl;
            }
            if (useLong) {
                if (al == bl) {
                    throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_32_BITS, new Object[] { p, q });
                }
                long blbu = bl;
                bl = al;
                al = blbu % al;
                if (al == 0L) {
                    if (bl > 2147483647L) {
                        throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_32_BITS, new Object[] { p, q });
                    }
                    return (int)bl;
                }
                else {
                    blbu = bl;
                    b = (int)al;
                    a = (int)(blbu % al);
                }
            }
            return gcdPositive(a, b);
        }
        if (a == Integer.MIN_VALUE || b == Integer.MIN_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_32_BITS, new Object[] { p, q });
        }
        return FastMath.abs(a + b);
    }
    
    private static int gcdPositive(int a, int b) {
        if (a == 0) {
            return b;
        }
        if (b == 0) {
            return a;
        }
        final int aTwos = Integer.numberOfTrailingZeros(a);
        a >>= aTwos;
        final int bTwos = Integer.numberOfTrailingZeros(b);
        b >>= bTwos;
        final int shift = Math.min(aTwos, bTwos);
        while (a != b) {
            final int delta = a - b;
            b = Math.min(a, b);
            a = Math.abs(delta);
            a >>= Integer.numberOfTrailingZeros(a);
        }
        return a << shift;
    }
    
    public static long gcd(final long p, final long q) throws MathArithmeticException {
        long u = p;
        long v = q;
        if (u == 0L || v == 0L) {
            if (u == Long.MIN_VALUE || v == Long.MIN_VALUE) {
                throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_64_BITS, new Object[] { p, q });
            }
            return FastMath.abs(u) + FastMath.abs(v);
        }
        else {
            if (u > 0L) {
                u = -u;
            }
            if (v > 0L) {
                v = -v;
            }
            int k;
            for (k = 0; (u & 0x1L) == 0x0L && (v & 0x1L) == 0x0L && k < 63; u /= 2L, v /= 2L, ++k) {}
            if (k == 63) {
                throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_64_BITS, new Object[] { p, q });
            }
            long t = ((u & 0x1L) == 0x1L) ? v : (-(u / 2L));
            while (true) {
                if ((t & 0x1L) == 0x0L) {
                    t /= 2L;
                }
                else {
                    if (t > 0L) {
                        u = -t;
                    }
                    else {
                        v = t;
                    }
                    t = (v - u) / 2L;
                    if (t == 0L) {
                        break;
                    }
                    continue;
                }
            }
            return -u * (1L << k);
        }
    }
    
    public static int lcm(final int a, final int b) throws MathArithmeticException {
        if (a == 0 || b == 0) {
            return 0;
        }
        final int lcm = FastMath.abs(mulAndCheck(a / gcd(a, b), b));
        if (lcm == Integer.MIN_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.LCM_OVERFLOW_32_BITS, new Object[] { a, b });
        }
        return lcm;
    }
    
    public static long lcm(final long a, final long b) throws MathArithmeticException {
        if (a == 0L || b == 0L) {
            return 0L;
        }
        final long lcm = FastMath.abs(mulAndCheck(a / gcd(a, b), b));
        if (lcm == Long.MIN_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.LCM_OVERFLOW_64_BITS, new Object[] { a, b });
        }
        return lcm;
    }
    
    public static int mulAndCheck(final int x, final int y) throws MathArithmeticException {
        final long m = x * (long)y;
        if (m < -2147483648L || m > 2147483647L) {
            throw new MathArithmeticException();
        }
        return (int)m;
    }
    
    public static long mulAndCheck(final long a, final long b) throws MathArithmeticException {
        long ret;
        if (a > b) {
            ret = mulAndCheck(b, a);
        }
        else if (a < 0L) {
            if (b < 0L) {
                if (a < Long.MAX_VALUE / b) {
                    throw new MathArithmeticException();
                }
                ret = a * b;
            }
            else if (b > 0L) {
                if (Long.MIN_VALUE / b > a) {
                    throw new MathArithmeticException();
                }
                ret = a * b;
            }
            else {
                ret = 0L;
            }
        }
        else if (a > 0L) {
            if (a > Long.MAX_VALUE / b) {
                throw new MathArithmeticException();
            }
            ret = a * b;
        }
        else {
            ret = 0L;
        }
        return ret;
    }
    
    public static int subAndCheck(final int x, final int y) throws MathArithmeticException {
        final long s = x - (long)y;
        if (s < -2147483648L || s > 2147483647L) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_SUBTRACTION, new Object[] { x, y });
        }
        return (int)s;
    }
    
    public static long subAndCheck(final long a, final long b) throws MathArithmeticException {
        long ret;
        if (b == Long.MIN_VALUE) {
            if (a >= 0L) {
                throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, new Object[] { a, -b });
            }
            ret = a - b;
        }
        else {
            ret = addAndCheck(a, -b, LocalizedFormats.OVERFLOW_IN_ADDITION);
        }
        return ret;
    }
    
    public static int pow(final int k, int e) throws NotPositiveException {
        if (e < 0) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }
        int result = 1;
        int k2p = k;
        while (e != 0) {
            if ((e & 0x1) != 0x0) {
                result *= k2p;
            }
            k2p *= k2p;
            e >>= 1;
        }
        return result;
    }
    
    public static int pow(final int k, long e) throws NotPositiveException {
        if (e < 0L) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }
        int result = 1;
        int k2p = k;
        while (e != 0L) {
            if ((e & 0x1L) != 0x0L) {
                result *= k2p;
            }
            k2p *= k2p;
            e >>= 1;
        }
        return result;
    }
    
    public static long pow(final long k, int e) throws NotPositiveException {
        if (e < 0) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }
        long result = 1L;
        long k2p = k;
        while (e != 0) {
            if ((e & 0x1) != 0x0) {
                result *= k2p;
            }
            k2p *= k2p;
            e >>= 1;
        }
        return result;
    }
    
    public static long pow(final long k, long e) throws NotPositiveException {
        if (e < 0L) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }
        long result = 1L;
        long k2p = k;
        while (e != 0L) {
            if ((e & 0x1L) != 0x0L) {
                result *= k2p;
            }
            k2p *= k2p;
            e >>= 1;
        }
        return result;
    }
    
    public static BigInteger pow(final BigInteger k, final int e) throws NotPositiveException {
        if (e < 0) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }
        return k.pow(e);
    }
    
    public static BigInteger pow(final BigInteger k, long e) throws NotPositiveException {
        if (e < 0L) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }
        BigInteger result = BigInteger.ONE;
        BigInteger k2p = k;
        while (e != 0L) {
            if ((e & 0x1L) != 0x0L) {
                result = result.multiply(k2p);
            }
            k2p = k2p.multiply(k2p);
            e >>= 1;
        }
        return result;
    }
    
    public static BigInteger pow(final BigInteger k, BigInteger e) throws NotPositiveException {
        if (e.compareTo(BigInteger.ZERO) < 0) {
            throw new NotPositiveException(LocalizedFormats.EXPONENT, e);
        }
        BigInteger result = BigInteger.ONE;
        BigInteger k2p = k;
        while (!BigInteger.ZERO.equals(e)) {
            if (e.testBit(0)) {
                result = result.multiply(k2p);
            }
            k2p = k2p.multiply(k2p);
            e = e.shiftRight(1);
        }
        return result;
    }
    
    public static long stirlingS2(final int n, final int k) throws NotPositiveException, NumberIsTooLargeException, MathArithmeticException {
        if (k < 0) {
            throw new NotPositiveException(k);
        }
        if (k > n) {
            throw new NumberIsTooLargeException(k, n, true);
        }
        long[][] stirlingS2 = ArithmeticUtils.STIRLING_S2.get();
        if (stirlingS2 == null) {
            final int maxIndex = 26;
            stirlingS2 = new long[26][];
            stirlingS2[0] = new long[] { 1L };
            for (int i = 1; i < stirlingS2.length; ++i) {
                (stirlingS2[i] = new long[i + 1])[0] = 0L;
                stirlingS2[i][1] = 1L;
                stirlingS2[i][i] = 1L;
                for (int j = 2; j < i; ++j) {
                    stirlingS2[i][j] = j * stirlingS2[i - 1][j] + stirlingS2[i - 1][j - 1];
                }
            }
            ArithmeticUtils.STIRLING_S2.compareAndSet(null, stirlingS2);
        }
        if (n < stirlingS2.length) {
            return stirlingS2[n][k];
        }
        if (k == 0) {
            return 0L;
        }
        if (k == 1 || k == n) {
            return 1L;
        }
        if (k == 2) {
            return (1L << n - 1) - 1L;
        }
        if (k == n - 1) {
            return binomialCoefficient(n, 2);
        }
        long sum = 0L;
        long sign = ((k & 0x1) == 0x0) ? 1L : -1L;
        for (int l = 1; l <= k; ++l) {
            sign = -sign;
            sum += sign * binomialCoefficient(k, l) * pow(l, n);
            if (sum < 0L) {
                throw new MathArithmeticException(LocalizedFormats.ARGUMENT_OUTSIDE_DOMAIN, new Object[] { n, 0, stirlingS2.length - 1 });
            }
        }
        return sum / factorial(k);
    }
    
    private static long addAndCheck(final long a, final long b, final Localizable pattern) throws MathArithmeticException {
        long ret;
        if (a > b) {
            ret = addAndCheck(b, a, pattern);
        }
        else if (a < 0L) {
            if (b < 0L) {
                if (Long.MIN_VALUE - b > a) {
                    throw new MathArithmeticException(pattern, new Object[] { a, b });
                }
                ret = a + b;
            }
            else {
                ret = a + b;
            }
        }
        else {
            if (a > Long.MAX_VALUE - b) {
                throw new MathArithmeticException(pattern, new Object[] { a, b });
            }
            ret = a + b;
        }
        return ret;
    }
    
    private static void checkBinomial(final int n, final int k) throws NumberIsTooLargeException, NotPositiveException {
        if (n < k) {
            throw new NumberIsTooLargeException(LocalizedFormats.BINOMIAL_INVALID_PARAMETERS_ORDER, k, n, true);
        }
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.BINOMIAL_NEGATIVE_PARAMETER, n);
        }
    }
    
    public static boolean isPowerOfTwo(final long n) {
        return n > 0L && (n & n - 1L) == 0x0L;
    }
    
    static {
        FACTORIALS = new long[] { 1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 3628800L, 39916800L, 479001600L, 6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 2432902008176640000L };
        STIRLING_S2 = new AtomicReference<long[][]>(null);
    }
}
