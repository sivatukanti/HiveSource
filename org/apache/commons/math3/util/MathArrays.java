// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.DimensionMismatchException;

public class MathArrays
{
    private static final int SPLIT_FACTOR = 134217729;
    
    private MathArrays() {
    }
    
    public static double[] ebeAdd(final double[] a, final double[] b) {
        if (a.length != b.length) {
            throw new DimensionMismatchException(a.length, b.length);
        }
        final double[] result = a.clone();
        for (int i = 0; i < a.length; ++i) {
            final double[] array = result;
            final int n = i;
            array[n] += b[i];
        }
        return result;
    }
    
    public static double[] ebeSubtract(final double[] a, final double[] b) {
        if (a.length != b.length) {
            throw new DimensionMismatchException(a.length, b.length);
        }
        final double[] result = a.clone();
        for (int i = 0; i < a.length; ++i) {
            final double[] array = result;
            final int n = i;
            array[n] -= b[i];
        }
        return result;
    }
    
    public static double[] ebeMultiply(final double[] a, final double[] b) {
        if (a.length != b.length) {
            throw new DimensionMismatchException(a.length, b.length);
        }
        final double[] result = a.clone();
        for (int i = 0; i < a.length; ++i) {
            final double[] array = result;
            final int n = i;
            array[n] *= b[i];
        }
        return result;
    }
    
    public static double[] ebeDivide(final double[] a, final double[] b) {
        if (a.length != b.length) {
            throw new DimensionMismatchException(a.length, b.length);
        }
        final double[] result = a.clone();
        for (int i = 0; i < a.length; ++i) {
            final double[] array = result;
            final int n = i;
            array[n] /= b[i];
        }
        return result;
    }
    
    public static double distance1(final double[] p1, final double[] p2) {
        double sum = 0.0;
        for (int i = 0; i < p1.length; ++i) {
            sum += FastMath.abs(p1[i] - p2[i]);
        }
        return sum;
    }
    
    public static int distance1(final int[] p1, final int[] p2) {
        int sum = 0;
        for (int i = 0; i < p1.length; ++i) {
            sum += FastMath.abs(p1[i] - p2[i]);
        }
        return sum;
    }
    
    public static double distance(final double[] p1, final double[] p2) {
        double sum = 0.0;
        for (int i = 0; i < p1.length; ++i) {
            final double dp = p1[i] - p2[i];
            sum += dp * dp;
        }
        return FastMath.sqrt(sum);
    }
    
    public static double distance(final int[] p1, final int[] p2) {
        double sum = 0.0;
        for (int i = 0; i < p1.length; ++i) {
            final double dp = p1[i] - p2[i];
            sum += dp * dp;
        }
        return FastMath.sqrt(sum);
    }
    
    public static double distanceInf(final double[] p1, final double[] p2) {
        double max = 0.0;
        for (int i = 0; i < p1.length; ++i) {
            max = FastMath.max(max, FastMath.abs(p1[i] - p2[i]));
        }
        return max;
    }
    
    public static int distanceInf(final int[] p1, final int[] p2) {
        int max = 0;
        for (int i = 0; i < p1.length; ++i) {
            max = FastMath.max(max, FastMath.abs(p1[i] - p2[i]));
        }
        return max;
    }
    
    public static <T extends Comparable<? super T>> boolean isMonotonic(final T[] val, final OrderDirection dir, final boolean strict) {
        T previous = val[0];
        for (int max = val.length, i = 1; i < max; ++i) {
            switch (dir) {
                case INCREASING: {
                    final int comp = previous.compareTo((Object)val[i]);
                    if (strict) {
                        if (comp >= 0) {
                            return false;
                        }
                        break;
                    }
                    else {
                        if (comp > 0) {
                            return false;
                        }
                        break;
                    }
                    break;
                }
                case DECREASING: {
                    final int comp = val[i].compareTo((Object)previous);
                    if (strict) {
                        if (comp >= 0) {
                            return false;
                        }
                        break;
                    }
                    else {
                        if (comp > 0) {
                            return false;
                        }
                        break;
                    }
                    break;
                }
                default: {
                    throw new MathInternalError();
                }
            }
            previous = val[i];
        }
        return true;
    }
    
    public static boolean isMonotonic(final double[] val, final OrderDirection dir, final boolean strict) {
        return checkOrder(val, dir, strict, false);
    }
    
    public static boolean checkOrder(final double[] val, final OrderDirection dir, final boolean strict, final boolean abort) throws NonMonotonicSequenceException {
        double previous = val[0];
        int max = 0;
        int index = 0;
    Label_0132:
        for (max = val.length, index = 1; index < max; ++index) {
            switch (dir) {
                case INCREASING: {
                    if (strict) {
                        if (val[index] <= previous) {
                            break Label_0132;
                        }
                        break;
                    }
                    else {
                        if (val[index] < previous) {
                            break Label_0132;
                        }
                        break;
                    }
                    break;
                }
                case DECREASING: {
                    if (strict) {
                        if (val[index] >= previous) {
                            break Label_0132;
                        }
                        break;
                    }
                    else {
                        if (val[index] > previous) {
                            break Label_0132;
                        }
                        break;
                    }
                    break;
                }
                default: {
                    throw new MathInternalError();
                }
            }
            previous = val[index];
        }
        if (index == max) {
            return true;
        }
        if (abort) {
            throw new NonMonotonicSequenceException(val[index], previous, index, dir, strict);
        }
        return false;
    }
    
    public static void checkOrder(final double[] val, final OrderDirection dir, final boolean strict) throws NonMonotonicSequenceException {
        checkOrder(val, dir, strict, true);
    }
    
    public static void checkOrder(final double[] val) throws NonMonotonicSequenceException {
        checkOrder(val, OrderDirection.INCREASING, true);
    }
    
    public static void checkRectangular(final long[][] in) throws NullArgumentException, DimensionMismatchException {
        MathUtils.checkNotNull(in);
        for (int i = 1; i < in.length; ++i) {
            if (in[i].length != in[0].length) {
                throw new DimensionMismatchException(LocalizedFormats.DIFFERENT_ROWS_LENGTHS, in[i].length, in[0].length);
            }
        }
    }
    
    public static void checkPositive(final double[] in) throws NotStrictlyPositiveException {
        for (int i = 0; i < in.length; ++i) {
            if (in[i] <= 0.0) {
                throw new NotStrictlyPositiveException(in[i]);
            }
        }
    }
    
    public static void checkNonNegative(final long[] in) throws NotPositiveException {
        for (int i = 0; i < in.length; ++i) {
            if (in[i] < 0L) {
                throw new NotPositiveException(in[i]);
            }
        }
    }
    
    public static void checkNonNegative(final long[][] in) throws NotPositiveException {
        for (int i = 0; i < in.length; ++i) {
            for (int j = 0; j < in[i].length; ++j) {
                if (in[i][j] < 0L) {
                    throw new NotPositiveException(in[i][j]);
                }
            }
        }
    }
    
    public static double safeNorm(final double[] v) {
        final double rdwarf = 3.834E-20;
        final double rgiant = 1.304E19;
        double s1 = 0.0;
        double s2 = 0.0;
        double s3 = 0.0;
        double x1max = 0.0;
        double x3max = 0.0;
        final double floatn = v.length;
        final double agiant = rgiant / floatn;
        for (int i = 0; i < v.length; ++i) {
            final double xabs = Math.abs(v[i]);
            if (xabs < rdwarf || xabs > agiant) {
                if (xabs > rdwarf) {
                    if (xabs > x1max) {
                        final double r = x1max / xabs;
                        s1 = 1.0 + s1 * r * r;
                        x1max = xabs;
                    }
                    else {
                        final double r = xabs / x1max;
                        s1 += r * r;
                    }
                }
                else if (xabs > x3max) {
                    final double r = x3max / xabs;
                    s3 = 1.0 + s3 * r * r;
                    x3max = xabs;
                }
                else if (xabs != 0.0) {
                    final double r = xabs / x3max;
                    s3 += r * r;
                }
            }
            else {
                s2 += xabs * xabs;
            }
        }
        double norm;
        if (s1 != 0.0) {
            norm = x1max * Math.sqrt(s1 + s2 / x1max / x1max);
        }
        else if (s2 == 0.0) {
            norm = x3max * Math.sqrt(s3);
        }
        else if (s2 >= x3max) {
            norm = Math.sqrt(s2 * (1.0 + x3max / s2 * (x3max * s3)));
        }
        else {
            norm = Math.sqrt(x3max * (s2 / x3max + x3max * s3));
        }
        return norm;
    }
    
    public static void sortInPlace(final double[] x, final double[]... yList) throws DimensionMismatchException, NullArgumentException {
        sortInPlace(x, OrderDirection.INCREASING, yList);
    }
    
    public static void sortInPlace(final double[] x, final OrderDirection dir, final double[]... yList) throws NullArgumentException, DimensionMismatchException {
        if (x == null) {
            throw new NullArgumentException();
        }
        final int len = x.length;
        final List<Pair<Double, double[]>> list = new ArrayList<Pair<Double, double[]>>(len);
        final int yListLen = yList.length;
        for (int i = 0; i < len; ++i) {
            final double[] yValues = new double[yListLen];
            for (int j = 0; j < yListLen; ++j) {
                final double[] y = yList[j];
                if (y == null) {
                    throw new NullArgumentException();
                }
                if (y.length != len) {
                    throw new DimensionMismatchException(y.length, len);
                }
                yValues[j] = y[i];
            }
            list.add(new Pair<Double, double[]>(x[i], yValues));
        }
        final Comparator<Pair<Double, double[]>> comp = new Comparator<Pair<Double, double[]>>() {
            public int compare(final Pair<Double, double[]> o1, final Pair<Double, double[]> o2) {
                int val = 0;
                switch (dir) {
                    case INCREASING: {
                        val = o1.getKey().compareTo(o2.getKey());
                        break;
                    }
                    case DECREASING: {
                        val = o2.getKey().compareTo(o1.getKey());
                        break;
                    }
                    default: {
                        throw new MathInternalError();
                    }
                }
                return val;
            }
        };
        Collections.sort(list, comp);
        for (int k = 0; k < len; ++k) {
            final Pair<Double, double[]> e = list.get(k);
            x[k] = e.getKey();
            final double[] yValues2 = e.getValue();
            for (int l = 0; l < yListLen; ++l) {
                yList[l][k] = yValues2[l];
            }
        }
    }
    
    public static int[] copyOf(final int[] source) {
        return copyOf(source, source.length);
    }
    
    public static double[] copyOf(final double[] source) {
        return copyOf(source, source.length);
    }
    
    public static int[] copyOf(final int[] source, final int len) {
        final int[] output = new int[len];
        System.arraycopy(source, 0, output, 0, FastMath.min(len, source.length));
        return output;
    }
    
    public static double[] copyOf(final double[] source, final int len) {
        final double[] output = new double[len];
        System.arraycopy(source, 0, output, 0, FastMath.min(len, source.length));
        return output;
    }
    
    public static double linearCombination(final double[] a, final double[] b) throws DimensionMismatchException {
        final int len = a.length;
        if (len != b.length) {
            throw new DimensionMismatchException(len, b.length);
        }
        final double[] prodHigh = new double[len];
        double prodLowSum = 0.0;
        for (int i = 0; i < len; ++i) {
            final double ai = a[i];
            final double ca = 1.34217729E8 * ai;
            final double aHigh = ca - (ca - ai);
            final double aLow = ai - aHigh;
            final double bi = b[i];
            final double cb = 1.34217729E8 * bi;
            final double bHigh = cb - (cb - bi);
            final double bLow = bi - bHigh;
            prodHigh[i] = ai * bi;
            final double prodLow = aLow * bLow - (prodHigh[i] - aHigh * bHigh - aLow * bHigh - aHigh * bLow);
            prodLowSum += prodLow;
        }
        final double prodHighCur = prodHigh[0];
        double prodHighNext = prodHigh[1];
        double sHighPrev = prodHighCur + prodHighNext;
        double sPrime = sHighPrev - prodHighNext;
        double sLowSum = prodHighNext - (sHighPrev - sPrime) + (prodHighCur - sPrime);
        for (int lenMinusOne = len - 1, j = 1; j < lenMinusOne; ++j) {
            prodHighNext = prodHigh[j + 1];
            final double sHighCur = sHighPrev + prodHighNext;
            sPrime = sHighCur - prodHighNext;
            sLowSum += prodHighNext - (sHighCur - sPrime) + (sHighPrev - sPrime);
            sHighPrev = sHighCur;
        }
        double result = sHighPrev + (prodLowSum + sLowSum);
        if (Double.isNaN(result)) {
            result = 0.0;
            for (int k = 0; k < len; ++k) {
                result += a[k] * b[k];
            }
        }
        return result;
    }
    
    public static double linearCombination(final double a1, final double b1, final double a2, final double b2) {
        final double ca1 = 1.34217729E8 * a1;
        final double a1High = ca1 - (ca1 - a1);
        final double a1Low = a1 - a1High;
        final double cb1 = 1.34217729E8 * b1;
        final double b1High = cb1 - (cb1 - b1);
        final double b1Low = b1 - b1High;
        final double prod1High = a1 * b1;
        final double prod1Low = a1Low * b1Low - (prod1High - a1High * b1High - a1Low * b1High - a1High * b1Low);
        final double ca2 = 1.34217729E8 * a2;
        final double a2High = ca2 - (ca2 - a2);
        final double a2Low = a2 - a2High;
        final double cb2 = 1.34217729E8 * b2;
        final double b2High = cb2 - (cb2 - b2);
        final double b2Low = b2 - b2High;
        final double prod2High = a2 * b2;
        final double prod2Low = a2Low * b2Low - (prod2High - a2High * b2High - a2Low * b2High - a2High * b2Low);
        final double s12High = prod1High + prod2High;
        final double s12Prime = s12High - prod2High;
        final double s12Low = prod2High - (s12High - s12Prime) + (prod1High - s12Prime);
        double result = s12High + (prod1Low + prod2Low + s12Low);
        if (Double.isNaN(result)) {
            result = a1 * b1 + a2 * b2;
        }
        return result;
    }
    
    public static double linearCombination(final double a1, final double b1, final double a2, final double b2, final double a3, final double b3) {
        final double ca1 = 1.34217729E8 * a1;
        final double a1High = ca1 - (ca1 - a1);
        final double a1Low = a1 - a1High;
        final double cb1 = 1.34217729E8 * b1;
        final double b1High = cb1 - (cb1 - b1);
        final double b1Low = b1 - b1High;
        final double prod1High = a1 * b1;
        final double prod1Low = a1Low * b1Low - (prod1High - a1High * b1High - a1Low * b1High - a1High * b1Low);
        final double ca2 = 1.34217729E8 * a2;
        final double a2High = ca2 - (ca2 - a2);
        final double a2Low = a2 - a2High;
        final double cb2 = 1.34217729E8 * b2;
        final double b2High = cb2 - (cb2 - b2);
        final double b2Low = b2 - b2High;
        final double prod2High = a2 * b2;
        final double prod2Low = a2Low * b2Low - (prod2High - a2High * b2High - a2Low * b2High - a2High * b2Low);
        final double ca3 = 1.34217729E8 * a3;
        final double a3High = ca3 - (ca3 - a3);
        final double a3Low = a3 - a3High;
        final double cb3 = 1.34217729E8 * b3;
        final double b3High = cb3 - (cb3 - b3);
        final double b3Low = b3 - b3High;
        final double prod3High = a3 * b3;
        final double prod3Low = a3Low * b3Low - (prod3High - a3High * b3High - a3Low * b3High - a3High * b3Low);
        final double s12High = prod1High + prod2High;
        final double s12Prime = s12High - prod2High;
        final double s12Low = prod2High - (s12High - s12Prime) + (prod1High - s12Prime);
        final double s123High = s12High + prod3High;
        final double s123Prime = s123High - prod3High;
        final double s123Low = prod3High - (s123High - s123Prime) + (s12High - s123Prime);
        double result = s123High + (prod1Low + prod2Low + prod3Low + s12Low + s123Low);
        if (Double.isNaN(result)) {
            result = a1 * b1 + a2 * b2 + a3 * b3;
        }
        return result;
    }
    
    public static double linearCombination(final double a1, final double b1, final double a2, final double b2, final double a3, final double b3, final double a4, final double b4) {
        final double ca1 = 1.34217729E8 * a1;
        final double a1High = ca1 - (ca1 - a1);
        final double a1Low = a1 - a1High;
        final double cb1 = 1.34217729E8 * b1;
        final double b1High = cb1 - (cb1 - b1);
        final double b1Low = b1 - b1High;
        final double prod1High = a1 * b1;
        final double prod1Low = a1Low * b1Low - (prod1High - a1High * b1High - a1Low * b1High - a1High * b1Low);
        final double ca2 = 1.34217729E8 * a2;
        final double a2High = ca2 - (ca2 - a2);
        final double a2Low = a2 - a2High;
        final double cb2 = 1.34217729E8 * b2;
        final double b2High = cb2 - (cb2 - b2);
        final double b2Low = b2 - b2High;
        final double prod2High = a2 * b2;
        final double prod2Low = a2Low * b2Low - (prod2High - a2High * b2High - a2Low * b2High - a2High * b2Low);
        final double ca3 = 1.34217729E8 * a3;
        final double a3High = ca3 - (ca3 - a3);
        final double a3Low = a3 - a3High;
        final double cb3 = 1.34217729E8 * b3;
        final double b3High = cb3 - (cb3 - b3);
        final double b3Low = b3 - b3High;
        final double prod3High = a3 * b3;
        final double prod3Low = a3Low * b3Low - (prod3High - a3High * b3High - a3Low * b3High - a3High * b3Low);
        final double ca4 = 1.34217729E8 * a4;
        final double a4High = ca4 - (ca4 - a4);
        final double a4Low = a4 - a4High;
        final double cb4 = 1.34217729E8 * b4;
        final double b4High = cb4 - (cb4 - b4);
        final double b4Low = b4 - b4High;
        final double prod4High = a4 * b4;
        final double prod4Low = a4Low * b4Low - (prod4High - a4High * b4High - a4Low * b4High - a4High * b4Low);
        final double s12High = prod1High + prod2High;
        final double s12Prime = s12High - prod2High;
        final double s12Low = prod2High - (s12High - s12Prime) + (prod1High - s12Prime);
        final double s123High = s12High + prod3High;
        final double s123Prime = s123High - prod3High;
        final double s123Low = prod3High - (s123High - s123Prime) + (s12High - s123Prime);
        final double s1234High = s123High + prod4High;
        final double s1234Prime = s1234High - prod4High;
        final double s1234Low = prod4High - (s1234High - s1234Prime) + (s123High - s1234Prime);
        double result = s1234High + (prod1Low + prod2Low + prod3Low + prod4Low + s12Low + s123Low + s1234Low);
        if (Double.isNaN(result)) {
            result = a1 * b1 + a2 * b2 + a3 * b3 + a4 * b4;
        }
        return result;
    }
    
    public static boolean equals(final float[] x, final float[] y) {
        if (x == null || y == null) {
            return !(x == null ^ y == null);
        }
        if (x.length != y.length) {
            return false;
        }
        for (int i = 0; i < x.length; ++i) {
            if (!Precision.equals(x[i], y[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean equalsIncludingNaN(final float[] x, final float[] y) {
        if (x == null || y == null) {
            return !(x == null ^ y == null);
        }
        if (x.length != y.length) {
            return false;
        }
        for (int i = 0; i < x.length; ++i) {
            if (!Precision.equalsIncludingNaN(x[i], y[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean equals(final double[] x, final double[] y) {
        if (x == null || y == null) {
            return !(x == null ^ y == null);
        }
        if (x.length != y.length) {
            return false;
        }
        for (int i = 0; i < x.length; ++i) {
            if (!Precision.equals(x[i], y[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean equalsIncludingNaN(final double[] x, final double[] y) {
        if (x == null || y == null) {
            return !(x == null ^ y == null);
        }
        if (x.length != y.length) {
            return false;
        }
        for (int i = 0; i < x.length; ++i) {
            if (!Precision.equalsIncludingNaN(x[i], y[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static double[] normalizeArray(final double[] values, final double normalizedSum) throws MathIllegalArgumentException, MathArithmeticException {
        if (Double.isInfinite(normalizedSum)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NORMALIZE_INFINITE, new Object[0]);
        }
        if (Double.isNaN(normalizedSum)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NORMALIZE_NAN, new Object[0]);
        }
        double sum = 0.0;
        final int len = values.length;
        final double[] out = new double[len];
        for (int i = 0; i < len; ++i) {
            if (Double.isInfinite(values[i])) {
                throw new MathIllegalArgumentException(LocalizedFormats.INFINITE_ARRAY_ELEMENT, new Object[] { values[i], i });
            }
            if (!Double.isNaN(values[i])) {
                sum += values[i];
            }
        }
        if (sum == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.ARRAY_SUMS_TO_ZERO, new Object[0]);
        }
        for (int i = 0; i < len; ++i) {
            if (Double.isNaN(values[i])) {
                out[i] = Double.NaN;
            }
            else {
                out[i] = values[i] * normalizedSum / sum;
            }
        }
        return out;
    }
    
    public enum OrderDirection
    {
        INCREASING, 
        DECREASING;
    }
    
    public interface Function
    {
        double evaluate(final double[] p0);
        
        double evaluate(final double[] p0, final int p1, final int p2);
    }
}
