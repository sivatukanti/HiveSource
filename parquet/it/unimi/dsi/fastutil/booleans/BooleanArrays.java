// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.booleans;

import java.io.Serializable;
import java.util.Random;
import parquet.it.unimi.dsi.fastutil.Arrays;
import parquet.it.unimi.dsi.fastutil.Hash;

public class BooleanArrays
{
    public static final boolean[] EMPTY_ARRAY;
    private static final int SMALL = 7;
    private static final int MEDIUM = 50;
    public static final Hash.Strategy<boolean[]> HASH_STRATEGY;
    
    private BooleanArrays() {
    }
    
    public static boolean[] ensureCapacity(final boolean[] array, final int length) {
        if (length > array.length) {
            final boolean[] t = new boolean[length];
            System.arraycopy(array, 0, t, 0, array.length);
            return t;
        }
        return array;
    }
    
    public static boolean[] ensureCapacity(final boolean[] array, final int length, final int preserve) {
        if (length > array.length) {
            final boolean[] t = new boolean[length];
            System.arraycopy(array, 0, t, 0, preserve);
            return t;
        }
        return array;
    }
    
    public static boolean[] grow(final boolean[] array, final int length) {
        if (length > array.length) {
            final int newLength = (int)Math.min(Math.max(2L * array.length, length), 2147483639L);
            final boolean[] t = new boolean[newLength];
            System.arraycopy(array, 0, t, 0, array.length);
            return t;
        }
        return array;
    }
    
    public static boolean[] grow(final boolean[] array, final int length, final int preserve) {
        if (length > array.length) {
            final int newLength = (int)Math.min(Math.max(2L * array.length, length), 2147483639L);
            final boolean[] t = new boolean[newLength];
            System.arraycopy(array, 0, t, 0, preserve);
            return t;
        }
        return array;
    }
    
    public static boolean[] trim(final boolean[] array, final int length) {
        if (length >= array.length) {
            return array;
        }
        final boolean[] t = (length == 0) ? BooleanArrays.EMPTY_ARRAY : new boolean[length];
        System.arraycopy(array, 0, t, 0, length);
        return t;
    }
    
    public static boolean[] setLength(final boolean[] array, final int length) {
        if (length == array.length) {
            return array;
        }
        if (length < array.length) {
            return trim(array, length);
        }
        return ensureCapacity(array, length);
    }
    
    public static boolean[] copy(final boolean[] array, final int offset, final int length) {
        ensureOffsetLength(array, offset, length);
        final boolean[] a = (length == 0) ? BooleanArrays.EMPTY_ARRAY : new boolean[length];
        System.arraycopy(array, offset, a, 0, length);
        return a;
    }
    
    public static boolean[] copy(final boolean[] array) {
        return array.clone();
    }
    
    public static void fill(final boolean[] array, final boolean value) {
        int i = array.length;
        while (i-- != 0) {
            array[i] = value;
        }
    }
    
    public static void fill(final boolean[] array, final int from, int to, final boolean value) {
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
    public static boolean equals(final boolean[] a1, final boolean[] a2) {
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
    
    public static void ensureFromTo(final boolean[] a, final int from, final int to) {
        Arrays.ensureFromTo(a.length, from, to);
    }
    
    public static void ensureOffsetLength(final boolean[] a, final int offset, final int length) {
        Arrays.ensureOffsetLength(a.length, offset, length);
    }
    
    private static void swap(final boolean[] x, final int a, final int b) {
        final boolean t = x[a];
        x[a] = x[b];
        x[b] = t;
    }
    
    private static void vecSwap(final boolean[] x, int a, int b, final int n) {
        for (int i = 0; i < n; ++i, ++a, ++b) {
            swap(x, a, b);
        }
    }
    
    private static int med3(final boolean[] x, final int a, final int b, final int c, final BooleanComparator comp) {
        final int ab = comp.compare(x[a], x[b]);
        final int ac = comp.compare(x[a], x[c]);
        final int bc = comp.compare(x[b], x[c]);
        return (ab < 0) ? ((bc < 0) ? b : ((ac < 0) ? c : a)) : ((bc > 0) ? b : ((ac > 0) ? c : a));
    }
    
    private static void selectionSort(final boolean[] a, final int from, final int to, final BooleanComparator comp) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (comp.compare(a[j], a[m]) < 0) {
                    m = j;
                }
            }
            if (m != i) {
                final boolean u = a[i];
                a[i] = a[m];
                a[m] = u;
            }
        }
    }
    
    private static void insertionSort(final boolean[] a, final int from, final int to, final BooleanComparator comp) {
        int i = from;
        while (++i < to) {
            final boolean t = a[i];
            int j = i;
            for (boolean u = a[j - 1]; comp.compare(t, u) < 0; u = a[--j - 1]) {
                a[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
            }
            a[j] = t;
        }
    }
    
    private static void selectionSort(final boolean[] a, final int from, final int to) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (!a[j] && a[m]) {
                    m = j;
                }
            }
            if (m != i) {
                final boolean u = a[i];
                a[i] = a[m];
                a[m] = u;
            }
        }
    }
    
    private static void insertionSort(final boolean[] a, final int from, final int to) {
        int i = from;
        while (++i < to) {
            final boolean t = a[i];
            int j = i;
            for (boolean u = a[j - 1]; !t && u; u = a[--j - 1]) {
                a[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
            }
            a[j] = t;
        }
    }
    
    public static void quickSort(final boolean[] x, final int from, final int to, final BooleanComparator comp) {
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
        final boolean v = x[m];
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
    
    public static void quickSort(final boolean[] x, final BooleanComparator comp) {
        quickSort(x, 0, x.length, comp);
    }
    
    private static int med3(final boolean[] x, final int a, final int b, final int c) {
        final int ab = (!x[a] && x[b]) ? -1 : ((x[a] == x[b]) ? 0 : 1);
        final int ac = (!x[a] && x[c]) ? -1 : ((x[a] == x[c]) ? 0 : 1);
        final int bc = (!x[b] && x[c]) ? -1 : ((x[b] == x[c]) ? 0 : 1);
        return (ab < 0) ? ((bc < 0) ? b : ((ac < 0) ? c : a)) : ((bc > 0) ? b : ((ac > 0) ? c : a));
    }
    
    @Deprecated
    public static void quickSort(final boolean[] x, final int from, final int to) {
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
        final boolean v = x[m];
        int b;
        int a = b = from;
        int d;
        int c = d = to - 1;
        while (true) {
            if (b <= c) {
                int n4;
                final int n3 = (!x[b] && v) ? (n4 = -1) : ((x[b] == v) ? (n4 = 0) : (n4 = 1));
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
                final int n5 = (!x[c] && v) ? (n6 = -1) : ((x[c] == v) ? (n6 = 0) : (n6 = 1));
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
    public static void quickSort(final boolean[] x) {
        quickSort(x, 0, x.length);
    }
    
    public static void mergeSort(final boolean[] a, final int from, final int to, final boolean[] supp) {
        final int len = to - from;
        if (len < 7) {
            insertionSort(a, from, to);
            return;
        }
        final int mid = from + to >>> 1;
        mergeSort(supp, from, mid, a);
        mergeSort(supp, mid, to, a);
        if (!supp[mid - 1] || supp[mid]) {
            System.arraycopy(supp, from, a, from, len);
            return;
        }
        int i = from;
        int p = from;
        int q = mid;
        while (i < to) {
            if (q >= to || (p < mid && (!supp[p] || supp[q]))) {
                a[i] = supp[p++];
            }
            else {
                a[i] = supp[q++];
            }
            ++i;
        }
    }
    
    public static void mergeSort(final boolean[] a, final int from, final int to) {
        mergeSort(a, from, to, a.clone());
    }
    
    public static void mergeSort(final boolean[] a) {
        mergeSort(a, 0, a.length);
    }
    
    public static void mergeSort(final boolean[] a, final int from, final int to, final BooleanComparator comp, final boolean[] supp) {
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
    
    public static void mergeSort(final boolean[] a, final int from, final int to, final BooleanComparator comp) {
        mergeSort(a, from, to, comp, a.clone());
    }
    
    public static void mergeSort(final boolean[] a, final BooleanComparator comp) {
        mergeSort(a, 0, a.length, comp);
    }
    
    public static boolean[] shuffle(final boolean[] a, final int from, final int to, final Random random) {
        int i = to - from;
        while (i-- != 0) {
            final int p = random.nextInt(i + 1);
            final boolean t = a[from + i];
            a[from + i] = a[from + p];
            a[from + p] = t;
        }
        return a;
    }
    
    public static boolean[] shuffle(final boolean[] a, final Random random) {
        int i = a.length;
        while (i-- != 0) {
            final int p = random.nextInt(i + 1);
            final boolean t = a[i];
            a[i] = a[p];
            a[p] = t;
        }
        return a;
    }
    
    public static boolean[] reverse(final boolean[] a) {
        final int length = a.length;
        int i = length / 2;
        while (i-- != 0) {
            final boolean t = a[length - i - 1];
            a[length - i - 1] = a[i];
            a[i] = t;
        }
        return a;
    }
    
    static {
        EMPTY_ARRAY = new boolean[0];
        HASH_STRATEGY = new ArrayHashStrategy();
    }
    
    private static final class ArrayHashStrategy implements Hash.Strategy<boolean[]>, Serializable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        
        @Override
        public int hashCode(final boolean[] o) {
            return java.util.Arrays.hashCode(o);
        }
        
        @Override
        public boolean equals(final boolean[] a, final boolean[] b) {
            return java.util.Arrays.equals(a, b);
        }
    }
}
