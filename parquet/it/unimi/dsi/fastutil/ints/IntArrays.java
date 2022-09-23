// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.ints;

import java.io.Serializable;
import java.util.Random;
import parquet.it.unimi.dsi.fastutil.Arrays;
import parquet.it.unimi.dsi.fastutil.Hash;

public class IntArrays
{
    public static final int[] EMPTY_ARRAY;
    private static final int SMALL = 7;
    private static final int MEDIUM = 50;
    private static final int DIGIT_BITS = 8;
    private static final int DIGIT_MASK = 255;
    private static final int DIGITS_PER_ELEMENT = 4;
    public static final Hash.Strategy<int[]> HASH_STRATEGY;
    
    private IntArrays() {
    }
    
    public static int[] ensureCapacity(final int[] array, final int length) {
        if (length > array.length) {
            final int[] t = new int[length];
            System.arraycopy(array, 0, t, 0, array.length);
            return t;
        }
        return array;
    }
    
    public static int[] ensureCapacity(final int[] array, final int length, final int preserve) {
        if (length > array.length) {
            final int[] t = new int[length];
            System.arraycopy(array, 0, t, 0, preserve);
            return t;
        }
        return array;
    }
    
    public static int[] grow(final int[] array, final int length) {
        if (length > array.length) {
            final int newLength = (int)Math.min(Math.max(2L * array.length, length), 2147483639L);
            final int[] t = new int[newLength];
            System.arraycopy(array, 0, t, 0, array.length);
            return t;
        }
        return array;
    }
    
    public static int[] grow(final int[] array, final int length, final int preserve) {
        if (length > array.length) {
            final int newLength = (int)Math.min(Math.max(2L * array.length, length), 2147483639L);
            final int[] t = new int[newLength];
            System.arraycopy(array, 0, t, 0, preserve);
            return t;
        }
        return array;
    }
    
    public static int[] trim(final int[] array, final int length) {
        if (length >= array.length) {
            return array;
        }
        final int[] t = (length == 0) ? IntArrays.EMPTY_ARRAY : new int[length];
        System.arraycopy(array, 0, t, 0, length);
        return t;
    }
    
    public static int[] setLength(final int[] array, final int length) {
        if (length == array.length) {
            return array;
        }
        if (length < array.length) {
            return trim(array, length);
        }
        return ensureCapacity(array, length);
    }
    
    public static int[] copy(final int[] array, final int offset, final int length) {
        ensureOffsetLength(array, offset, length);
        final int[] a = (length == 0) ? IntArrays.EMPTY_ARRAY : new int[length];
        System.arraycopy(array, offset, a, 0, length);
        return a;
    }
    
    public static int[] copy(final int[] array) {
        return array.clone();
    }
    
    public static void fill(final int[] array, final int value) {
        int i = array.length;
        while (i-- != 0) {
            array[i] = value;
        }
    }
    
    public static void fill(final int[] array, final int from, int to, final int value) {
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
    public static boolean equals(final int[] a1, final int[] a2) {
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
    
    public static void ensureFromTo(final int[] a, final int from, final int to) {
        Arrays.ensureFromTo(a.length, from, to);
    }
    
    public static void ensureOffsetLength(final int[] a, final int offset, final int length) {
        Arrays.ensureOffsetLength(a.length, offset, length);
    }
    
    private static void swap(final int[] x, final int a, final int b) {
        final int t = x[a];
        x[a] = x[b];
        x[b] = t;
    }
    
    private static void vecSwap(final int[] x, int a, int b, final int n) {
        for (int i = 0; i < n; ++i, ++a, ++b) {
            swap(x, a, b);
        }
    }
    
    private static int med3(final int[] x, final int a, final int b, final int c, final IntComparator comp) {
        final int ab = comp.compare(x[a], x[b]);
        final int ac = comp.compare(x[a], x[c]);
        final int bc = comp.compare(x[b], x[c]);
        return (ab < 0) ? ((bc < 0) ? b : ((ac < 0) ? c : a)) : ((bc > 0) ? b : ((ac > 0) ? c : a));
    }
    
    private static void selectionSort(final int[] a, final int from, final int to, final IntComparator comp) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (comp.compare(a[j], a[m]) < 0) {
                    m = j;
                }
            }
            if (m != i) {
                final int u = a[i];
                a[i] = a[m];
                a[m] = u;
            }
        }
    }
    
    private static void insertionSort(final int[] a, final int from, final int to, final IntComparator comp) {
        int i = from;
        while (++i < to) {
            final int t = a[i];
            int j = i;
            for (int u = a[j - 1]; comp.compare(t, u) < 0; u = a[--j - 1]) {
                a[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
            }
            a[j] = t;
        }
    }
    
    private static void selectionSort(final int[] a, final int from, final int to) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (a[j] < a[m]) {
                    m = j;
                }
            }
            if (m != i) {
                final int u = a[i];
                a[i] = a[m];
                a[m] = u;
            }
        }
    }
    
    private static void insertionSort(final int[] a, final int from, final int to) {
        int i = from;
        while (++i < to) {
            final int t = a[i];
            int j = i;
            for (int u = a[j - 1]; t < u; u = a[--j - 1]) {
                a[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
            }
            a[j] = t;
        }
    }
    
    public static void quickSort(final int[] x, final int from, final int to, final IntComparator comp) {
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
        final int v = x[m];
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
    
    public static void quickSort(final int[] x, final IntComparator comp) {
        quickSort(x, 0, x.length, comp);
    }
    
    private static int med3(final int[] x, final int a, final int b, final int c) {
        final int ab = (x[a] < x[b]) ? -1 : ((x[a] == x[b]) ? 0 : 1);
        final int ac = (x[a] < x[c]) ? -1 : ((x[a] == x[c]) ? 0 : 1);
        final int bc = (x[b] < x[c]) ? -1 : ((x[b] == x[c]) ? 0 : 1);
        return (ab < 0) ? ((bc < 0) ? b : ((ac < 0) ? c : a)) : ((bc > 0) ? b : ((ac > 0) ? c : a));
    }
    
    @Deprecated
    public static void quickSort(final int[] x, final int from, final int to) {
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
        final int v = x[m];
        int b;
        int a = b = from;
        int d;
        int c = d = to - 1;
        while (true) {
            if (b <= c) {
                int n4;
                final int n3 = (x[b] < v) ? (n4 = -1) : ((x[b] == v) ? (n4 = 0) : (n4 = 1));
                final int comparison = n4;
                if (n3 <= 0) {
                    if (comparison == 0) {
                        swap(x, a++, b);
                    }
                    ++b;
                    continue;
                }
            }
            while (c >= b) {
                int n6;
                final int n5 = (x[c] < v) ? (n6 = -1) : ((x[c] == v) ? (n6 = 0) : (n6 = 1));
                final int comparison = n6;
                if (n5 < 0) {
                    break;
                }
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
    public static void quickSort(final int[] x) {
        quickSort(x, 0, x.length);
    }
    
    public static void mergeSort(final int[] a, final int from, final int to, final int[] supp) {
        final int len = to - from;
        if (len < 7) {
            insertionSort(a, from, to);
            return;
        }
        final int mid = from + to >>> 1;
        mergeSort(supp, from, mid, a);
        mergeSort(supp, mid, to, a);
        if (supp[mid - 1] <= supp[mid]) {
            System.arraycopy(supp, from, a, from, len);
            return;
        }
        int i = from;
        int p = from;
        int q = mid;
        while (i < to) {
            if (q >= to || (p < mid && supp[p] <= supp[q])) {
                a[i] = supp[p++];
            }
            else {
                a[i] = supp[q++];
            }
            ++i;
        }
    }
    
    public static void mergeSort(final int[] a, final int from, final int to) {
        mergeSort(a, from, to, a.clone());
    }
    
    public static void mergeSort(final int[] a) {
        mergeSort(a, 0, a.length);
    }
    
    public static void mergeSort(final int[] a, final int from, final int to, final IntComparator comp, final int[] supp) {
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
    
    public static void mergeSort(final int[] a, final int from, final int to, final IntComparator comp) {
        mergeSort(a, from, to, comp, a.clone());
    }
    
    public static void mergeSort(final int[] a, final IntComparator comp) {
        mergeSort(a, 0, a.length, comp);
    }
    
    public static int binarySearch(final int[] a, int from, int to, final int key) {
        --to;
        while (from <= to) {
            final int mid = from + to >>> 1;
            final int midVal = a[mid];
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
    
    public static int binarySearch(final int[] a, final int key) {
        return binarySearch(a, 0, a.length, key);
    }
    
    public static int binarySearch(final int[] a, int from, int to, final int key, final IntComparator c) {
        --to;
        while (from <= to) {
            final int mid = from + to >>> 1;
            final int midVal = a[mid];
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
    
    public static int binarySearch(final int[] a, final int key, final IntComparator c) {
        return binarySearch(a, 0, a.length, key, c);
    }
    
    public static void radixSort(final int[] a) {
        radixSort(a, 0, a.length);
    }
    
    public static void radixSort(final int[] a, final int from, final int to) {
        final int maxLevel = 3;
        final int stackSize = 766;
        final int[] offsetStack = new int[766];
        int offsetPos = 0;
        final int[] lengthStack = new int[766];
        int lengthPos = 0;
        final int[] levelStack = new int[766];
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
            final int signMask = (level % 4 == 0) ? 128 : 0;
            if (length < 50) {
                selectionSort(a, first, first + length);
            }
            else {
                final int shift = (3 - level % 4) * 8;
                int i = length;
                while (i-- != 0) {
                    digit[i] = (byte)((a[first + i] >>> shift & 0xFF) ^ signMask);
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
                        if (level < 3 && count[j] > 1) {
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
                    int t = a[k + first];
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
                        final int z = t;
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
    
    private static void insertionSortIndirect(final int[] perm, final int[] a, final int from, final int to) {
        int i = from;
        while (++i < to) {
            final int t = perm[i];
            int j = i;
            for (int u = perm[j - 1]; a[t] < a[u]; u = perm[--j - 1]) {
                perm[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
            }
            perm[j] = t;
        }
    }
    
    public static void radixSortIndirect(final int[] perm, final int[] a, final boolean stable) {
        radixSortIndirect(perm, a, 0, perm.length, stable);
    }
    
    public static void radixSortIndirect(final int[] perm, final int[] a, final int from, final int to, final boolean stable) {
        final int maxLevel = 3;
        final int stackSize = 766;
        final int[] offsetStack = new int[766];
        int offsetPos = 0;
        final int[] lengthStack = new int[766];
        int lengthPos = 0;
        final int[] levelStack = new int[766];
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
            final int signMask = (level % 4 == 0) ? 128 : 0;
            if (length < 50) {
                insertionSortIndirect(perm, a, first, first + length);
            }
            else {
                final int shift = (3 - level % 4) * 8;
                int i = length;
                while (i-- != 0) {
                    digit[i] = (byte)((a[perm[first + i]] >>> shift & 0xFF) ^ signMask);
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
                        if (level < 3 && count[j] > 1) {
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
                    fill(count, 0);
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
    
    private static void selectionSort(final int[] a, final int[] b, final int from, final int to) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (a[j] < a[m] || (a[j] == a[m] && b[j] < b[m])) {
                    m = j;
                }
            }
            if (m != i) {
                int t = a[i];
                a[i] = a[m];
                a[m] = t;
                t = b[i];
                b[i] = b[m];
                b[m] = t;
            }
        }
    }
    
    public static void radixSort(final int[] a, final int[] b) {
        radixSort(a, b, 0, a.length);
    }
    
    public static void radixSort(final int[] a, final int[] b, final int from, final int to) {
        final int layers = 2;
        if (a.length != b.length) {
            throw new IllegalArgumentException("Array size mismatch.");
        }
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
            final int signMask = (level % 4 == 0) ? 128 : 0;
            if (length < 50) {
                selectionSort(a, b, first, first + length);
            }
            else {
                final int[] k = (level < 4) ? a : b;
                final int shift = (3 - level % 4) * 8;
                int i = length;
                while (i-- != 0) {
                    digit[i] = (byte)((k[first + i] >>> shift & 0xFF) ^ signMask);
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
                for (int l = 0, c = -1; l < end; l += count[c], count[c] = 0) {
                    int t = a[l + first];
                    int u = b[l + first];
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
                        int z = t;
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
    
    private static void insertionSortIndirect(final int[] perm, final int[] a, final int[] b, final int from, final int to) {
        int i = from;
        while (++i < to) {
            final int t = perm[i];
            int j = i;
            for (int u = perm[j - 1]; a[t] < a[u] || (a[t] == a[u] && b[t] < b[u]); u = perm[--j - 1]) {
                perm[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
            }
            perm[j] = t;
        }
    }
    
    public static void radixSortIndirect(final int[] perm, final int[] a, final int[] b, final boolean stable) {
        radixSortIndirect(perm, a, b, 0, perm.length, stable);
    }
    
    public static void radixSortIndirect(final int[] perm, final int[] a, final int[] b, final int from, final int to, final boolean stable) {
        final int layers = 2;
        if (a.length != b.length) {
            throw new IllegalArgumentException("Array size mismatch.");
        }
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
            final int signMask = (level % 4 == 0) ? 128 : 0;
            if (length < 50) {
                insertionSortIndirect(perm, a, b, first, first + length);
            }
            else {
                final int[] k = (level < 4) ? a : b;
                final int shift = (3 - level % 4) * 8;
                int i = length;
                while (i-- != 0) {
                    digit[i] = (byte)((k[perm[first + i]] >>> shift & 0xFF) ^ signMask);
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
                    fill(count, 0);
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
    
    private static void selectionSort(final int[][] a, final int from, final int to, final int level) {
        final int layers = a.length;
        final int firstLayer = level / 4;
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
                    final int u = a[p2][i];
                    a[p2][i] = a[p2][m];
                    a[p2][m] = u;
                }
            }
        }
    }
    
    public static void radixSort(final int[][] a) {
        radixSort(a, 0, a[0].length);
    }
    
    public static void radixSort(final int[][] a, final int from, final int to) {
        final int layers = a.length;
        final int maxLevel = 4 * layers - 1;
        int p = layers;
        final int l = a[0].length;
        while (p-- != 0) {
            if (a[p].length != l) {
                throw new IllegalArgumentException("The array of index " + p + " has not the same length of the array of index 0.");
            }
        }
        final int stackSize = 255 * (layers * 4 - 1) + 1;
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
        final int[] t = new int[layers];
        while (offsetPos > 0) {
            final int first = offsetStack[--offsetPos];
            final int length = lengthStack[--lengthPos];
            final int level = levelStack[--levelPos];
            final int signMask = (level % 4 == 0) ? 128 : 0;
            if (length < 50) {
                selectionSort(a, first, first + length, level);
            }
            else {
                final int[] k = a[level / 4];
                final int shift = (3 - level % 4) * 8;
                int i = length;
                while (i-- != 0) {
                    digit[i] = (byte)((k[first + i] >>> shift & 0xFF) ^ signMask);
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
                            final int u = t[p3];
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
    
    public static int[] shuffle(final int[] a, final int from, final int to, final Random random) {
        int i = to - from;
        while (i-- != 0) {
            final int p = random.nextInt(i + 1);
            final int t = a[from + i];
            a[from + i] = a[from + p];
            a[from + p] = t;
        }
        return a;
    }
    
    public static int[] shuffle(final int[] a, final Random random) {
        int i = a.length;
        while (i-- != 0) {
            final int p = random.nextInt(i + 1);
            final int t = a[i];
            a[i] = a[p];
            a[p] = t;
        }
        return a;
    }
    
    public static int[] reverse(final int[] a) {
        final int length = a.length;
        int i = length / 2;
        while (i-- != 0) {
            final int t = a[length - i - 1];
            a[length - i - 1] = a[i];
            a[i] = t;
        }
        return a;
    }
    
    static {
        EMPTY_ARRAY = new int[0];
        HASH_STRATEGY = new ArrayHashStrategy();
    }
    
    private static final class ArrayHashStrategy implements Hash.Strategy<int[]>, Serializable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        
        @Override
        public int hashCode(final int[] o) {
            return java.util.Arrays.hashCode(o);
        }
        
        @Override
        public boolean equals(final int[] a, final int[] b) {
            return java.util.Arrays.equals(a, b);
        }
    }
}
