// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.doubles;

import java.io.Serializable;
import java.util.Random;
import parquet.it.unimi.dsi.fastutil.ints.IntArrays;
import parquet.it.unimi.dsi.fastutil.Arrays;
import parquet.it.unimi.dsi.fastutil.Hash;

public class DoubleArrays
{
    public static final double[] EMPTY_ARRAY;
    private static final int SMALL = 7;
    private static final int MEDIUM = 50;
    private static final int DIGIT_BITS = 8;
    private static final int DIGIT_MASK = 255;
    private static final int DIGITS_PER_ELEMENT = 8;
    public static final Hash.Strategy<double[]> HASH_STRATEGY;
    
    private DoubleArrays() {
    }
    
    public static double[] ensureCapacity(final double[] array, final int length) {
        if (length > array.length) {
            final double[] t = new double[length];
            System.arraycopy(array, 0, t, 0, array.length);
            return t;
        }
        return array;
    }
    
    public static double[] ensureCapacity(final double[] array, final int length, final int preserve) {
        if (length > array.length) {
            final double[] t = new double[length];
            System.arraycopy(array, 0, t, 0, preserve);
            return t;
        }
        return array;
    }
    
    public static double[] grow(final double[] array, final int length) {
        if (length > array.length) {
            final int newLength = (int)Math.min(Math.max(2L * array.length, length), 2147483639L);
            final double[] t = new double[newLength];
            System.arraycopy(array, 0, t, 0, array.length);
            return t;
        }
        return array;
    }
    
    public static double[] grow(final double[] array, final int length, final int preserve) {
        if (length > array.length) {
            final int newLength = (int)Math.min(Math.max(2L * array.length, length), 2147483639L);
            final double[] t = new double[newLength];
            System.arraycopy(array, 0, t, 0, preserve);
            return t;
        }
        return array;
    }
    
    public static double[] trim(final double[] array, final int length) {
        if (length >= array.length) {
            return array;
        }
        final double[] t = (length == 0) ? DoubleArrays.EMPTY_ARRAY : new double[length];
        System.arraycopy(array, 0, t, 0, length);
        return t;
    }
    
    public static double[] setLength(final double[] array, final int length) {
        if (length == array.length) {
            return array;
        }
        if (length < array.length) {
            return trim(array, length);
        }
        return ensureCapacity(array, length);
    }
    
    public static double[] copy(final double[] array, final int offset, final int length) {
        ensureOffsetLength(array, offset, length);
        final double[] a = (length == 0) ? DoubleArrays.EMPTY_ARRAY : new double[length];
        System.arraycopy(array, offset, a, 0, length);
        return a;
    }
    
    public static double[] copy(final double[] array) {
        return array.clone();
    }
    
    public static void fill(final double[] array, final double value) {
        int i = array.length;
        while (i-- != 0) {
            array[i] = value;
        }
    }
    
    public static void fill(final double[] array, final int from, int to, final double value) {
        ensureFromTo(array, from, to);
        if (from == 0) {
            while (to-- != 0) {
                array[to] = value;
            }
        }
        else {
            for (int i = from; i < to; ++i) {
                array[i] = value;
            }
        }
    }
    
    @Deprecated
    public static boolean equals(final double[] a1, final double[] a2) {
        int i = a1.length;
        if (i != a2.length) {
            return false;
        }
        while (i-- != 0) {
            if (a1[i] != a2[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static void ensureFromTo(final double[] a, final int from, final int to) {
        Arrays.ensureFromTo(a.length, from, to);
    }
    
    public static void ensureOffsetLength(final double[] a, final int offset, final int length) {
        Arrays.ensureOffsetLength(a.length, offset, length);
    }
    
    private static void swap(final double[] x, final int a, final int b) {
        final double t = x[a];
        x[a] = x[b];
        x[b] = t;
    }
    
    private static void vecSwap(final double[] x, int a, int b, final int n) {
        for (int i = 0; i < n; ++i, ++a, ++b) {
            swap(x, a, b);
        }
    }
    
    private static int med3(final double[] x, final int a, final int b, final int c, final DoubleComparator comp) {
        final int ab = comp.compare(x[a], x[b]);
        final int ac = comp.compare(x[a], x[c]);
        final int bc = comp.compare(x[b], x[c]);
        return (ab < 0) ? ((bc < 0) ? b : ((ac < 0) ? c : a)) : ((bc > 0) ? b : ((ac > 0) ? c : a));
    }
    
    private static void selectionSort(final double[] a, final int from, final int to, final DoubleComparator comp) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (comp.compare(a[j], a[m]) < 0) {
                    m = j;
                }
            }
            if (m != i) {
                final double u = a[i];
                a[i] = a[m];
                a[m] = u;
            }
        }
    }
    
    private static void insertionSort(final double[] a, final int from, final int to, final DoubleComparator comp) {
        int i = from;
        while (++i < to) {
            final double t = a[i];
            int j = i;
            for (double u = a[j - 1]; comp.compare(t, u) < 0; u = a[--j - 1]) {
                a[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
            }
            a[j] = t;
        }
    }
    
    private static void selectionSort(final double[] a, final int from, final int to) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (Double.compare(a[j], a[m]) < 0) {
                    m = j;
                }
            }
            if (m != i) {
                final double u = a[i];
                a[i] = a[m];
                a[m] = u;
            }
        }
    }
    
    private static void insertionSort(final double[] a, final int from, final int to) {
        int i = from;
        while (++i < to) {
            final double t = a[i];
            int j = i;
            for (double u = a[j - 1]; Double.compare(t, u) < 0; u = a[--j - 1]) {
                a[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
            }
            a[j] = t;
        }
    }
    
    public static void quickSort(final double[] x, final int from, final int to, final DoubleComparator comp) {
        final int len = to - from;
        if (len < 7) {
            selectionSort(x, from, to, comp);
            return;
        }
        int m = from + len / 2;
        if (len > 7) {
            int l = from;
            int n = to - 1;
            if (len > 50) {
                final int s = len / 8;
                l = med3(x, l, l + s, l + 2 * s, comp);
                m = med3(x, m - s, m, m + s, comp);
                n = med3(x, n - 2 * s, n - s, n, comp);
            }
            m = med3(x, l, m, n, comp);
        }
        final double v = x[m];
        int b;
        int a = b = from;
        int d;
        int c = d = to - 1;
        while (true) {
            int comparison;
            if (b <= c && (comparison = comp.compare(x[b], v)) <= 0) {
                if (comparison == 0) {
                    swap(x, a++, b);
                }
                ++b;
            }
            else {
                while (c >= b && (comparison = comp.compare(x[c], v)) >= 0) {
                    if (comparison == 0) {
                        swap(x, c, d--);
                    }
                    --c;
                }
                if (b > c) {
                    break;
                }
                swap(x, b++, c--);
            }
        }
        final int n2 = to;
        int s2 = Math.min(a - from, b - a);
        vecSwap(x, from, b - s2, s2);
        s2 = Math.min(d - c, n2 - d - 1);
        vecSwap(x, b, n2 - s2, s2);
        if ((s2 = b - a) > 1) {
            quickSort(x, from, from + s2, comp);
        }
        if ((s2 = d - c) > 1) {
            quickSort(x, n2 - s2, n2, comp);
        }
    }
    
    public static void quickSort(final double[] x, final DoubleComparator comp) {
        quickSort(x, 0, x.length, comp);
    }
    
    private static int med3(final double[] x, final int a, final int b, final int c) {
        final int ab = Double.compare(x[a], x[b]);
        final int ac = Double.compare(x[a], x[c]);
        final int bc = Double.compare(x[b], x[c]);
        return (ab < 0) ? ((bc < 0) ? b : ((ac < 0) ? c : a)) : ((bc > 0) ? b : ((ac > 0) ? c : a));
    }
    
    @Deprecated
    public static void quickSort(final double[] x, final int from, final int to) {
        final int len = to - from;
        if (len < 7) {
            selectionSort(x, from, to);
            return;
        }
        int m = from + len / 2;
        if (len > 7) {
            int l = from;
            int n = to - 1;
            if (len > 50) {
                final int s = len / 8;
                l = med3(x, l, l + s, l + 2 * s);
                m = med3(x, m - s, m, m + s);
                n = med3(x, n - 2 * s, n - s, n);
            }
            m = med3(x, l, m, n);
        }
        final double v = x[m];
        int b;
        int a = b = from;
        int d;
        int c = d = to - 1;
        while (true) {
            int comparison;
            if (b <= c && (comparison = Double.compare(x[b], v)) <= 0) {
                if (comparison == 0) {
                    swap(x, a++, b);
                }
                ++b;
            }
            else {
                while (c >= b && (comparison = Double.compare(x[c], v)) >= 0) {
                    if (comparison == 0) {
                        swap(x, c, d--);
                    }
                    --c;
                }
                if (b > c) {
                    break;
                }
                swap(x, b++, c--);
            }
        }
        final int n2 = to;
        int s2 = Math.min(a - from, b - a);
        vecSwap(x, from, b - s2, s2);
        s2 = Math.min(d - c, n2 - d - 1);
        vecSwap(x, b, n2 - s2, s2);
        if ((s2 = b - a) > 1) {
            quickSort(x, from, from + s2);
        }
        if ((s2 = d - c) > 1) {
            quickSort(x, n2 - s2, n2);
        }
    }
    
    @Deprecated
    public static void quickSort(final double[] x) {
        quickSort(x, 0, x.length);
    }
    
    public static void mergeSort(final double[] a, final int from, final int to, final double[] supp) {
        final int len = to - from;
        if (len < 7) {
            insertionSort(a, from, to);
            return;
        }
        final int mid = from + to >>> 1;
        mergeSort(supp, from, mid, a);
        mergeSort(supp, mid, to, a);
        if (Double.compare(supp[mid - 1], supp[mid]) <= 0) {
            System.arraycopy(supp, from, a, from, len);
            return;
        }
        int i = from;
        int p = from;
        int q = mid;
        while (i < to) {
            if (q >= to || (p < mid && Double.compare(supp[p], supp[q]) <= 0)) {
                a[i] = supp[p++];
            }
            else {
                a[i] = supp[q++];
            }
            ++i;
        }
    }
    
    public static void mergeSort(final double[] a, final int from, final int to) {
        mergeSort(a, from, to, a.clone());
    }
    
    public static void mergeSort(final double[] a) {
        mergeSort(a, 0, a.length);
    }
    
    public static void mergeSort(final double[] a, final int from, final int to, final DoubleComparator comp, final double[] supp) {
        final int len = to - from;
        if (len < 7) {
            insertionSort(a, from, to, comp);
            return;
        }
        final int mid = from + to >>> 1;
        mergeSort(supp, from, mid, comp, a);
        mergeSort(supp, mid, to, comp, a);
        if (comp.compare(supp[mid - 1], supp[mid]) <= 0) {
            System.arraycopy(supp, from, a, from, len);
            return;
        }
        int i = from;
        int p = from;
        int q = mid;
        while (i < to) {
            if (q >= to || (p < mid && comp.compare(supp[p], supp[q]) <= 0)) {
                a[i] = supp[p++];
            }
            else {
                a[i] = supp[q++];
            }
            ++i;
        }
    }
    
    public static void mergeSort(final double[] a, final int from, final int to, final DoubleComparator comp) {
        mergeSort(a, from, to, comp, a.clone());
    }
    
    public static void mergeSort(final double[] a, final DoubleComparator comp) {
        mergeSort(a, 0, a.length, comp);
    }
    
    public static int binarySearch(final double[] a, int from, int to, final double key) {
        --to;
        while (from <= to) {
            final int mid = from + to >>> 1;
            final double midVal = a[mid];
            if (midVal < key) {
                from = mid + 1;
            }
            else {
                if (midVal <= key) {
                    return mid;
                }
                to = mid - 1;
            }
        }
        return -(from + 1);
    }
    
    public static int binarySearch(final double[] a, final double key) {
        return binarySearch(a, 0, a.length, key);
    }
    
    public static int binarySearch(final double[] a, int from, int to, final double key, final DoubleComparator c) {
        --to;
        while (from <= to) {
            final int mid = from + to >>> 1;
            final double midVal = a[mid];
            final int cmp = c.compare(midVal, key);
            if (cmp < 0) {
                from = mid + 1;
            }
            else {
                if (cmp <= 0) {
                    return mid;
                }
                to = mid - 1;
            }
        }
        return -(from + 1);
    }
    
    public static int binarySearch(final double[] a, final double key, final DoubleComparator c) {
        return binarySearch(a, 0, a.length, key, c);
    }
    
    private static final long fixDouble(final double d) {
        final long l = Double.doubleToLongBits(d);
        return (l >= 0L) ? l : (l ^ Long.MAX_VALUE);
    }
    
    public static void radixSort(final double[] a) {
        radixSort(a, 0, a.length);
    }
    
    public static void radixSort(final double[] a, final int from, final int to) {
        final int maxLevel = 7;
        final int stackSize = 1786;
        final int[] offsetStack = new int[1786];
        int offsetPos = 0;
        final int[] lengthStack = new int[1786];
        int lengthPos = 0;
        final int[] levelStack = new int[1786];
        int levelPos = 0;
        offsetStack[offsetPos++] = from;
        lengthStack[lengthPos++] = to - from;
        levelStack[levelPos++] = 0;
        final int[] count = new int[256];
        final int[] pos = new int[256];
        final byte[] digit = new byte[to - from];
        while (offsetPos > 0) {
            final int first = offsetStack[--offsetPos];
            final int length = lengthStack[--lengthPos];
            final int level = levelStack[--levelPos];
            final int signMask = (level % 8 == 0) ? 128 : 0;
            if (length < 50) {
                selectionSort(a, first, first + length);
            }
            else {
                final int shift = (7 - level % 8) * 8;
                int i = length;
                while (i-- != 0) {
                    digit[i] = (byte)((fixDouble(a[first + i]) >>> shift & 0xFFL) ^ (long)signMask);
                }
                i = length;
                while (i-- != 0) {
                    final int[] array = count;
                    final int n = digit[i] & 0xFF;
                    ++array[n];
                }
                int lastUsed = -1;
                int j = 0;
                int p = 0;
                while (j < 256) {
                    if (count[j] != 0) {
                        lastUsed = j;
                        if (level < 7 && count[j] > 1) {
                            offsetStack[offsetPos++] = p + first;
                            lengthStack[lengthPos++] = count[j];
                            levelStack[levelPos++] = level + 1;
                        }
                    }
                    p = (pos[j] = p + count[j]);
                    ++j;
                }
                final int end = length - count[lastUsed];
                count[lastUsed] = 0;
                for (int k = 0, c = -1; k < end; k += count[c], count[c] = 0) {
                    double t = a[k + first];
                    c = (digit[k] & 0xFF);
                    while (true) {
                        final int[] array2 = pos;
                        final int n2 = c;
                        final int n3 = array2[n2] - 1;
                        array2[n2] = n3;
                        final int d = n3;
                        if (n3 <= k) {
                            break;
                        }
                        final double z = t;
                        final int zz = c;
                        t = a[d + first];
                        c = (digit[d] & 0xFF);
                        a[d + first] = z;
                        digit[d] = (byte)zz;
                    }
                    a[k + first] = t;
                }
            }
        }
    }
    
    private static void insertionSortIndirect(final int[] perm, final double[] a, final int from, final int to) {
        int i = from;
        while (++i < to) {
            final int t = perm[i];
            int j = i;
            for (int u = perm[j - 1]; Double.compare(a[t], a[u]) < 0; u = perm[--j - 1]) {
                perm[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
            }
            perm[j] = t;
        }
    }
    
    public static void radixSortIndirect(final int[] perm, final double[] a, final boolean stable) {
        radixSortIndirect(perm, a, 0, perm.length, stable);
    }
    
    public static void radixSortIndirect(final int[] perm, final double[] a, final int from, final int to, final boolean stable) {
        final int maxLevel = 7;
        final int stackSize = 1786;
        final int[] offsetStack = new int[1786];
        int offsetPos = 0;
        final int[] lengthStack = new int[1786];
        int lengthPos = 0;
        final int[] levelStack = new int[1786];
        int levelPos = 0;
        offsetStack[offsetPos++] = from;
        lengthStack[lengthPos++] = to - from;
        levelStack[levelPos++] = 0;
        final int[] count = new int[256];
        final int[] pos = (int[])(stable ? null : new int[256]);
        final int[] support = (int[])(stable ? new int[perm.length] : null);
        final byte[] digit = new byte[to - from];
        while (offsetPos > 0) {
            final int first = offsetStack[--offsetPos];
            final int length = lengthStack[--lengthPos];
            final int level = levelStack[--levelPos];
            final int signMask = (level % 8 == 0) ? 128 : 0;
            if (length < 50) {
                insertionSortIndirect(perm, a, first, first + length);
            }
            else {
                final int shift = (7 - level % 8) * 8;
                int i = length;
                while (i-- != 0) {
                    digit[i] = (byte)((fixDouble(a[perm[first + i]]) >>> shift & 0xFFL) ^ (long)signMask);
                }
                i = length;
                while (i-- != 0) {
                    final int[] array = count;
                    final int n = digit[i] & 0xFF;
                    ++array[n];
                }
                int lastUsed = -1;
                int j = 0;
                int p = 0;
                while (j < 256) {
                    if (count[j] != 0) {
                        lastUsed = j;
                        if (level < 7 && count[j] > 1) {
                            offsetStack[offsetPos++] = p + first;
                            lengthStack[lengthPos++] = count[j];
                            levelStack[levelPos++] = level + 1;
                        }
                    }
                    if (stable) {
                        p = (count[j] += p);
                    }
                    else {
                        p = (pos[j] = p + count[j]);
                    }
                    ++j;
                }
                if (stable) {
                    j = length;
                    while (j-- != 0) {
                        final int[] array2 = support;
                        final int[] array3 = count;
                        final int n2 = digit[j] & 0xFF;
                        array2[--array3[n2]] = perm[first + j];
                    }
                    System.arraycopy(support, 0, perm, first, length);
                    IntArrays.fill(count, 0);
                }
                else {
                    final int end = length - count[lastUsed];
                    count[lastUsed] = 0;
                    for (int k = 0, c = -1; k < end; k += count[c], count[c] = 0) {
                        int t = perm[k + first];
                        c = (digit[k] & 0xFF);
                        while (true) {
                            final int[] array4 = pos;
                            final int n3 = c;
                            final int n4 = array4[n3] - 1;
                            array4[n3] = n4;
                            final int d = n4;
                            if (n4 <= k) {
                                break;
                            }
                            final int z = t;
                            final int zz = c;
                            t = perm[d + first];
                            c = (digit[d] & 0xFF);
                            perm[d + first] = z;
                            digit[d] = (byte)zz;
                        }
                        perm[k + first] = t;
                    }
                }
            }
        }
    }
    
    private static void selectionSort(final double[] a, final double[] b, final int from, final int to) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (a[j] < a[m] || (a[j] == a[m] && b[j] < b[m])) {
                    m = j;
                }
            }
            if (m != i) {
                double t = a[i];
                a[i] = a[m];
                a[m] = t;
                t = b[i];
                b[i] = b[m];
                b[m] = t;
            }
        }
    }
    
    public static void radixSort(final double[] a, final double[] b) {
        radixSort(a, b, 0, a.length);
    }
    
    public static void radixSort(final double[] a, final double[] b, final int from, final int to) {
        final int layers = 2;
        if (a.length != b.length) {
            throw new IllegalArgumentException("Array size mismatch.");
        }
        final int maxLevel = 15;
        final int stackSize = 3826;
        final int[] offsetStack = new int[3826];
        int offsetPos = 0;
        final int[] lengthStack = new int[3826];
        int lengthPos = 0;
        final int[] levelStack = new int[3826];
        int levelPos = 0;
        offsetStack[offsetPos++] = from;
        lengthStack[lengthPos++] = to - from;
        levelStack[levelPos++] = 0;
        final int[] count = new int[256];
        final int[] pos = new int[256];
        final byte[] digit = new byte[to - from];
        while (offsetPos > 0) {
            final int first = offsetStack[--offsetPos];
            final int length = lengthStack[--lengthPos];
            final int level = levelStack[--levelPos];
            final int signMask = (level % 8 == 0) ? 128 : 0;
            if (length < 50) {
                selectionSort(a, b, first, first + length);
            }
            else {
                final double[] k = (level < 8) ? a : b;
                final int shift = (7 - level % 8) * 8;
                int i = length;
                while (i-- != 0) {
                    digit[i] = (byte)((fixDouble(k[first + i]) >>> shift & 0xFFL) ^ (long)signMask);
                }
                i = length;
                while (i-- != 0) {
                    final int[] array = count;
                    final int n = digit[i] & 0xFF;
                    ++array[n];
                }
                int lastUsed = -1;
                int j = 0;
                int p = 0;
                while (j < 256) {
                    if (count[j] != 0) {
                        lastUsed = j;
                        if (level < 15 && count[j] > 1) {
                            offsetStack[offsetPos++] = p + first;
                            lengthStack[lengthPos++] = count[j];
                            levelStack[levelPos++] = level + 1;
                        }
                    }
                    p = (pos[j] = p + count[j]);
                    ++j;
                }
                final int end = length - count[lastUsed];
                count[lastUsed] = 0;
                for (int l = 0, c = -1; l < end; l += count[c], count[c] = 0) {
                    double t = a[l + first];
                    double u = b[l + first];
                    c = (digit[l] & 0xFF);
                    while (true) {
                        final int[] array2 = pos;
                        final int n2 = c;
                        final int n3 = array2[n2] - 1;
                        array2[n2] = n3;
                        final int d = n3;
                        if (n3 <= l) {
                            break;
                        }
                        double z = t;
                        final int zz = c;
                        t = a[d + first];
                        a[d + first] = z;
                        z = u;
                        u = b[d + first];
                        b[d + first] = z;
                        c = (digit[d] & 0xFF);
                        digit[d] = (byte)zz;
                    }
                    a[l + first] = t;
                    b[l + first] = u;
                }
            }
        }
    }
    
    private static void insertionSortIndirect(final int[] perm, final double[] a, final double[] b, final int from, final int to) {
        int i = from;
        while (++i < to) {
            final int t = perm[i];
            int j = i;
            for (int u = perm[j - 1]; Double.compare(a[t], a[u]) < 0 || (Double.compare(a[t], a[u]) == 0 && Double.compare(b[t], b[u]) < 0); u = perm[--j - 1]) {
                perm[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
            }
            perm[j] = t;
        }
    }
    
    public static void radixSortIndirect(final int[] perm, final double[] a, final double[] b, final boolean stable) {
        radixSortIndirect(perm, a, b, 0, perm.length, stable);
    }
    
    public static void radixSortIndirect(final int[] perm, final double[] a, final double[] b, final int from, final int to, final boolean stable) {
        final int layers = 2;
        if (a.length != b.length) {
            throw new IllegalArgumentException("Array size mismatch.");
        }
        final int maxLevel = 15;
        final int stackSize = 3826;
        final int[] offsetStack = new int[3826];
        int offsetPos = 0;
        final int[] lengthStack = new int[3826];
        int lengthPos = 0;
        final int[] levelStack = new int[3826];
        int levelPos = 0;
        offsetStack[offsetPos++] = from;
        lengthStack[lengthPos++] = to - from;
        levelStack[levelPos++] = 0;
        final int[] count = new int[256];
        final int[] pos = (int[])(stable ? null : new int[256]);
        final int[] support = (int[])(stable ? new int[perm.length] : null);
        final byte[] digit = new byte[to - from];
        while (offsetPos > 0) {
            final int first = offsetStack[--offsetPos];
            final int length = lengthStack[--lengthPos];
            final int level = levelStack[--levelPos];
            final int signMask = (level % 8 == 0) ? 128 : 0;
            if (length < 50) {
                insertionSortIndirect(perm, a, b, first, first + length);
            }
            else {
                final double[] k = (level < 8) ? a : b;
                final int shift = (7 - level % 8) * 8;
                int i = length;
                while (i-- != 0) {
                    digit[i] = (byte)((fixDouble(k[perm[first + i]]) >>> shift & 0xFFL) ^ (long)signMask);
                }
                i = length;
                while (i-- != 0) {
                    final int[] array = count;
                    final int n = digit[i] & 0xFF;
                    ++array[n];
                }
                int lastUsed = -1;
                int j = 0;
                int p = 0;
                while (j < 256) {
                    if (count[j] != 0) {
                        lastUsed = j;
                        if (level < 15 && count[j] > 1) {
                            offsetStack[offsetPos++] = p + first;
                            lengthStack[lengthPos++] = count[j];
                            levelStack[levelPos++] = level + 1;
                        }
                    }
                    if (stable) {
                        p = (count[j] += p);
                    }
                    else {
                        p = (pos[j] = p + count[j]);
                    }
                    ++j;
                }
                if (stable) {
                    j = length;
                    while (j-- != 0) {
                        final int[] array2 = support;
                        final int[] array3 = count;
                        final int n2 = digit[j] & 0xFF;
                        array2[--array3[n2]] = perm[first + j];
                    }
                    System.arraycopy(support, 0, perm, first, length);
                    IntArrays.fill(count, 0);
                }
                else {
                    final int end = length - count[lastUsed];
                    count[lastUsed] = 0;
                    for (int l = 0, c = -1; l < end; l += count[c], count[c] = 0) {
                        int t = perm[l + first];
                        c = (digit[l] & 0xFF);
                        while (true) {
                            final int[] array4 = pos;
                            final int n3 = c;
                            final int n4 = array4[n3] - 1;
                            array4[n3] = n4;
                            final int d = n4;
                            if (n4 <= l) {
                                break;
                            }
                            final int z = t;
                            final int zz = c;
                            t = perm[d + first];
                            c = (digit[d] & 0xFF);
                            perm[d + first] = z;
                            digit[d] = (byte)zz;
                        }
                        perm[l + first] = t;
                    }
                }
            }
        }
    }
    
    private static void selectionSort(final double[][] a, final int from, final int to, final int level) {
        final int layers = a.length;
        final int firstLayer = level / 8;
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                for (int p = firstLayer; p < layers; ++p) {
                    if (a[p][j] < a[p][m]) {
                        m = j;
                        break;
                    }
                    if (a[p][j] > a[p][m]) {
                        break;
                    }
                }
            }
            if (m != i) {
                int p2 = layers;
                while (p2-- != 0) {
                    final double u = a[p2][i];
                    a[p2][i] = a[p2][m];
                    a[p2][m] = u;
                }
            }
        }
    }
    
    public static void radixSort(final double[][] a) {
        radixSort(a, 0, a[0].length);
    }
    
    public static void radixSort(final double[][] a, final int from, final int to) {
        final int layers = a.length;
        final int maxLevel = 8 * layers - 1;
        int p = layers;
        final int l = a[0].length;
        while (p-- != 0) {
            if (a[p].length != l) {
                throw new IllegalArgumentException("The array of index " + p + " has not the same length of the array of index 0.");
            }
        }
        final int stackSize = 255 * (layers * 8 - 1) + 1;
        final int[] offsetStack = new int[stackSize];
        int offsetPos = 0;
        final int[] lengthStack = new int[stackSize];
        int lengthPos = 0;
        final int[] levelStack = new int[stackSize];
        int levelPos = 0;
        offsetStack[offsetPos++] = from;
        lengthStack[lengthPos++] = to - from;
        levelStack[levelPos++] = 0;
        final int[] count = new int[256];
        final int[] pos = new int[256];
        final byte[] digit = new byte[to - from];
        final double[] t = new double[layers];
        while (offsetPos > 0) {
            final int first = offsetStack[--offsetPos];
            final int length = lengthStack[--lengthPos];
            final int level = levelStack[--levelPos];
            final int signMask = (level % 8 == 0) ? 128 : 0;
            if (length < 50) {
                selectionSort(a, first, first + length, level);
            }
            else {
                final double[] k = a[level / 8];
                final int shift = (7 - level % 8) * 8;
                int i = length;
                while (i-- != 0) {
                    digit[i] = (byte)((fixDouble(k[first + i]) >>> shift & 0xFFL) ^ (long)signMask);
                }
                i = length;
                while (i-- != 0) {
                    final int[] array = count;
                    final int n = digit[i] & 0xFF;
                    ++array[n];
                }
                int lastUsed = -1;
                int j = 0;
                int p2 = 0;
                while (j < 256) {
                    if (count[j] != 0) {
                        lastUsed = j;
                        if (level < maxLevel && count[j] > 1) {
                            offsetStack[offsetPos++] = p2 + first;
                            lengthStack[lengthPos++] = count[j];
                            levelStack[levelPos++] = level + 1;
                        }
                    }
                    p2 = (pos[j] = p2 + count[j]);
                    ++j;
                }
                final int end = length - count[lastUsed];
                count[lastUsed] = 0;
                for (int m = 0, c = -1; m < end; m += count[c], count[c] = 0) {
                    int p3 = layers;
                    while (p3-- != 0) {
                        t[p3] = a[p3][m + first];
                    }
                    c = (digit[m] & 0xFF);
                    while (true) {
                        final int[] array2 = pos;
                        final int n2 = c;
                        final int n3 = array2[n2] - 1;
                        array2[n2] = n3;
                        final int d = n3;
                        if (n3 <= m) {
                            break;
                        }
                        p3 = layers;
                        while (p3-- != 0) {
                            final double u = t[p3];
                            t[p3] = a[p3][d + first];
                            a[p3][d + first] = u;
                        }
                        final int zz = c;
                        c = (digit[d] & 0xFF);
                        digit[d] = (byte)zz;
                    }
                    p3 = layers;
                    while (p3-- != 0) {
                        a[p3][m + first] = t[p3];
                    }
                }
            }
        }
    }
    
    public static double[] shuffle(final double[] a, final int from, final int to, final Random random) {
        int i = to - from;
        while (i-- != 0) {
            final int p = random.nextInt(i + 1);
            final double t = a[from + i];
            a[from + i] = a[from + p];
            a[from + p] = t;
        }
        return a;
    }
    
    public static double[] shuffle(final double[] a, final Random random) {
        int i = a.length;
        while (i-- != 0) {
            final int p = random.nextInt(i + 1);
            final double t = a[i];
            a[i] = a[p];
            a[p] = t;
        }
        return a;
    }
    
    public static double[] reverse(final double[] a) {
        final int length = a.length;
        int i = length / 2;
        while (i-- != 0) {
            final double t = a[length - i - 1];
            a[length - i - 1] = a[i];
            a[i] = t;
        }
        return a;
    }
    
    static {
        EMPTY_ARRAY = new double[0];
        HASH_STRATEGY = new ArrayHashStrategy();
    }
    
    private static final class ArrayHashStrategy implements Hash.Strategy<double[]>, Serializable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        
        @Override
        public int hashCode(final double[] o) {
            return java.util.Arrays.hashCode(o);
        }
        
        @Override
        public boolean equals(final double[] a, final double[] b) {
            return java.util.Arrays.equals(a, b);
        }
    }
}
