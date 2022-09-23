// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Random;
import java.util.Comparator;
import parquet.it.unimi.dsi.fastutil.Arrays;
import java.lang.reflect.Array;
import parquet.it.unimi.dsi.fastutil.Hash;

public class ObjectArrays
{
    public static final Object[] EMPTY_ARRAY;
    private static final int SMALL = 7;
    private static final int MEDIUM = 50;
    public static final Hash.Strategy HASH_STRATEGY;
    
    private ObjectArrays() {
    }
    
    private static <K> K[] newArray(final K[] prototype, final int length) {
        final Class<?> componentType = prototype.getClass().getComponentType();
        if (length == 0 && componentType == Object.class) {
            return (K[])ObjectArrays.EMPTY_ARRAY;
        }
        return (K[])Array.newInstance(prototype.getClass().getComponentType(), length);
    }
    
    public static <K> K[] ensureCapacity(final K[] array, final int length) {
        if (length > array.length) {
            final K[] t = (K[])newArray((Object[])array, length);
            System.arraycopy(array, 0, t, 0, array.length);
            return t;
        }
        return array;
    }
    
    public static <K> K[] ensureCapacity(final K[] array, final int length, final int preserve) {
        if (length > array.length) {
            final K[] t = newArray(array, length);
            System.arraycopy(array, 0, t, 0, preserve);
            return t;
        }
        return array;
    }
    
    public static <K> K[] grow(final K[] array, final int length) {
        if (length > array.length) {
            final int newLength = (int)Math.min(Math.max(2L * array.length, length), 2147483639L);
            final K[] t = (K[])newArray((Object[])array, newLength);
            System.arraycopy(array, 0, t, 0, array.length);
            return t;
        }
        return array;
    }
    
    public static <K> K[] grow(final K[] array, final int length, final int preserve) {
        if (length > array.length) {
            final int newLength = (int)Math.min(Math.max(2L * array.length, length), 2147483639L);
            final K[] t = newArray(array, newLength);
            System.arraycopy(array, 0, t, 0, preserve);
            return t;
        }
        return array;
    }
    
    public static <K> K[] trim(final K[] array, final int length) {
        if (length >= array.length) {
            return array;
        }
        final K[] t = (K[])newArray((Object[])array, length);
        System.arraycopy(array, 0, t, 0, length);
        return t;
    }
    
    public static <K> K[] setLength(final K[] array, final int length) {
        if (length == array.length) {
            return array;
        }
        if (length < array.length) {
            return (K[])trim((Object[])array, length);
        }
        return (K[])ensureCapacity((Object[])array, length);
    }
    
    public static <K> K[] copy(final K[] array, final int offset, final int length) {
        ensureOffsetLength(array, offset, length);
        final K[] a = newArray(array, length);
        System.arraycopy(array, offset, a, 0, length);
        return a;
    }
    
    public static <K> K[] copy(final K[] array) {
        return array.clone();
    }
    
    public static <K> void fill(final K[] array, final K value) {
        int i = array.length;
        while (i-- != 0) {
            array[i] = value;
        }
    }
    
    public static <K> void fill(final K[] array, final int from, int to, final K value) {
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
    public static <K> boolean equals(final K[] a1, final K[] a2) {
        int i = a1.length;
        if (i != a2.length) {
            return false;
        }
        while (i-- != 0) {
            if (a1[i] == null) {
                if (a2[i] == null) {
                    continue;
                }
            }
            else if (a1[i].equals(a2[i])) {
                continue;
            }
            return false;
        }
        return true;
    }
    
    public static <K> void ensureFromTo(final K[] a, final int from, final int to) {
        Arrays.ensureFromTo(a.length, from, to);
    }
    
    public static <K> void ensureOffsetLength(final K[] a, final int offset, final int length) {
        Arrays.ensureOffsetLength(a.length, offset, length);
    }
    
    private static <K> void swap(final K[] x, final int a, final int b) {
        final K t = x[a];
        x[a] = x[b];
        x[b] = t;
    }
    
    private static <K> void vecSwap(final K[] x, int a, int b, final int n) {
        for (int i = 0; i < n; ++i, ++a, ++b) {
            swap(x, a, b);
        }
    }
    
    private static <K> int med3(final K[] x, final int a, final int b, final int c, final Comparator<K> comp) {
        final int ab = comp.compare(x[a], x[b]);
        final int ac = comp.compare(x[a], x[c]);
        final int bc = comp.compare(x[b], x[c]);
        return (ab < 0) ? ((bc < 0) ? b : ((ac < 0) ? c : a)) : ((bc > 0) ? b : ((ac > 0) ? c : a));
    }
    
    private static <K> void selectionSort(final K[] a, final int from, final int to, final Comparator<K> comp) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (comp.compare(a[j], a[m]) < 0) {
                    m = j;
                }
            }
            if (m != i) {
                final K u = a[i];
                a[i] = a[m];
                a[m] = u;
            }
        }
    }
    
    private static <K> void insertionSort(final K[] a, final int from, final int to, final Comparator<K> comp) {
        int i = from;
        while (++i < to) {
            final K t = a[i];
            int j = i;
            for (K u = a[j - 1]; comp.compare(t, u) < 0; u = a[--j - 1]) {
                a[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
            }
            a[j] = t;
        }
    }
    
    private static <K> void selectionSort(final K[] a, final int from, final int to) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (((Comparable)a[j]).compareTo(a[m]) < 0) {
                    m = j;
                }
            }
            if (m != i) {
                final K u = a[i];
                a[i] = a[m];
                a[m] = u;
            }
        }
    }
    
    private static <K> void insertionSort(final K[] a, final int from, final int to) {
        int i = from;
        while (++i < to) {
            final K t = a[i];
            int j = i;
            for (K u = a[j - 1]; ((Comparable)t).compareTo(u) < 0; u = a[--j - 1]) {
                a[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
            }
            a[j] = t;
        }
    }
    
    public static <K> void quickSort(final K[] x, final int from, final int to, final Comparator<K> comp) {
        final int len = to - from;
        if (len < 7) {
            selectionSort(x, from, to, (Comparator<Object>)comp);
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
        final K v = x[m];
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
            quickSort(x, from, from + s2, (Comparator<Object>)comp);
        }
        if ((s2 = d - c) > 1) {
            quickSort(x, n2 - s2, n2, (Comparator<Object>)comp);
        }
    }
    
    public static <K> void quickSort(final K[] x, final Comparator<K> comp) {
        quickSort(x, 0, x.length, comp);
    }
    
    private static <K> int med3(final K[] x, final int a, final int b, final int c) {
        final int ab = ((Comparable)x[a]).compareTo(x[b]);
        final int ac = ((Comparable)x[a]).compareTo(x[c]);
        final int bc = ((Comparable)x[b]).compareTo(x[c]);
        return (ab < 0) ? ((bc < 0) ? b : ((ac < 0) ? c : a)) : ((bc > 0) ? b : ((ac > 0) ? c : a));
    }
    
    @Deprecated
    public static <K> void quickSort(final K[] x, final int from, final int to) {
        final int len = to - from;
        if (len < 7) {
            selectionSort((Object[])x, from, to);
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
        final K v = x[m];
        int b;
        int a = b = from;
        int d;
        int c = d = to - 1;
        while (true) {
            int comparison;
            if (b <= c && (comparison = ((Comparable)x[b]).compareTo(v)) <= 0) {
                if (comparison == 0) {
                    swap((Object[])x, a++, b);
                }
                ++b;
            }
            else {
                while (c >= b && (comparison = ((Comparable)x[c]).compareTo(v)) >= 0) {
                    if (comparison == 0) {
                        swap((Object[])x, c, d--);
                    }
                    --c;
                }
                if (b > c) {
                    break;
                }
                swap((Object[])x, b++, c--);
            }
        }
        final int n2 = to;
        int s2 = Math.min(a - from, b - a);
        vecSwap(x, from, b - s2, s2);
        s2 = Math.min(d - c, n2 - d - 1);
        vecSwap(x, b, n2 - s2, s2);
        if ((s2 = b - a) > 1) {
            quickSort((Object[])x, from, from + s2);
        }
        if ((s2 = d - c) > 1) {
            quickSort((Object[])x, n2 - s2, n2);
        }
    }
    
    @Deprecated
    public static <K> void quickSort(final K[] x) {
        quickSort(x, 0, x.length);
    }
    
    public static <K> void mergeSort(final K[] a, final int from, final int to, final K[] supp) {
        final int len = to - from;
        if (len < 7) {
            insertionSort(a, from, to);
            return;
        }
        final int mid = from + to >>> 1;
        mergeSort(supp, from, mid, (Object[])a);
        mergeSort(supp, mid, to, (Object[])a);
        if (((Comparable)supp[mid - 1]).compareTo(supp[mid]) <= 0) {
            System.arraycopy(supp, from, a, from, len);
            return;
        }
        int i = from;
        int p = from;
        int q = mid;
        while (i < to) {
            if (q >= to || (p < mid && ((Comparable)supp[p]).compareTo(supp[q]) <= 0)) {
                a[i] = supp[p++];
            }
            else {
                a[i] = supp[q++];
            }
            ++i;
        }
    }
    
    public static <K> void mergeSort(final K[] a, final int from, final int to) {
        mergeSort(a, from, to, a.clone());
    }
    
    public static <K> void mergeSort(final K[] a) {
        mergeSort(a, 0, a.length);
    }
    
    public static <K> void mergeSort(final K[] a, final int from, final int to, final Comparator<K> comp, final K[] supp) {
        final int len = to - from;
        if (len < 7) {
            insertionSort(a, from, to, comp);
            return;
        }
        final int mid = from + to >>> 1;
        mergeSort(supp, from, mid, (Comparator<Object>)comp, a);
        mergeSort(supp, mid, to, (Comparator<Object>)comp, a);
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
    
    public static <K> void mergeSort(final K[] a, final int from, final int to, final Comparator<K> comp) {
        mergeSort(a, from, to, comp, a.clone());
    }
    
    public static <K> void mergeSort(final K[] a, final Comparator<K> comp) {
        mergeSort(a, 0, a.length, comp);
    }
    
    public static <K> int binarySearch(final K[] a, int from, int to, final K key) {
        --to;
        while (from <= to) {
            final int mid = from + to >>> 1;
            final K midVal = a[mid];
            final int cmp = ((Comparable)midVal).compareTo(key);
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
    
    public static <K> int binarySearch(final K[] a, final K key) {
        return binarySearch(a, 0, a.length, key);
    }
    
    public static <K> int binarySearch(final K[] a, int from, int to, final K key, final Comparator<K> c) {
        --to;
        while (from <= to) {
            final int mid = from + to >>> 1;
            final K midVal = a[mid];
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
    
    public static <K> int binarySearch(final K[] a, final K key, final Comparator<K> c) {
        return binarySearch(a, 0, a.length, key, c);
    }
    
    public static <K> K[] shuffle(final K[] a, final int from, final int to, final Random random) {
        int i = to - from;
        while (i-- != 0) {
            final int p = random.nextInt(i + 1);
            final K t = a[from + i];
            a[from + i] = a[from + p];
            a[from + p] = t;
        }
        return a;
    }
    
    public static <K> K[] shuffle(final K[] a, final Random random) {
        int i = a.length;
        while (i-- != 0) {
            final int p = random.nextInt(i + 1);
            final K t = a[i];
            a[i] = a[p];
            a[p] = t;
        }
        return a;
    }
    
    public static <K> K[] reverse(final K[] a) {
        final int length = a.length;
        int i = length / 2;
        while (i-- != 0) {
            final K t = a[length - i - 1];
            a[length - i - 1] = a[i];
            a[i] = t;
        }
        return a;
    }
    
    static {
        EMPTY_ARRAY = new Object[0];
        HASH_STRATEGY = new ArrayHashStrategy();
    }
    
    private static final class ArrayHashStrategy<K> implements Hash.Strategy<K[]>, Serializable
    {
        private static final long serialVersionUID = -7046029254386353129L;
        
        @Override
        public int hashCode(final K[] o) {
            return java.util.Arrays.hashCode(o);
        }
        
        @Override
        public boolean equals(final K[] a, final K[] b) {
            return java.util.Arrays.equals(a, b);
        }
    }
}
