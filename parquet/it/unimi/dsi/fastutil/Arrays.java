// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil;

import parquet.it.unimi.dsi.fastutil.ints.IntComparator;

public class Arrays
{
    public static final int MAX_ARRAY_SIZE = 2147483639;
    private static final int SMALL = 7;
    private static final int MEDIUM = 40;
    
    private Arrays() {
    }
    
    public static void ensureFromTo(final int arrayLength, final int from, final int to) {
        if (from < 0) {
            throw new ArrayIndexOutOfBoundsException("Start index (" + from + ") is negative");
        }
        if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        if (to > arrayLength) {
            throw new ArrayIndexOutOfBoundsException("End index (" + to + ") is greater than array length (" + arrayLength + ")");
        }
    }
    
    public static void ensureOffsetLength(final int arrayLength, final int offset, final int length) {
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative");
        }
        if (length < 0) {
            throw new IllegalArgumentException("Length (" + length + ") is negative");
        }
        if (offset + length > arrayLength) {
            throw new ArrayIndexOutOfBoundsException("Last index (" + (offset + length) + ") is greater than array length (" + arrayLength + ")");
        }
    }
    
    private static void inPlaceMerge(final int from, int mid, final int to, final IntComparator comp, final Swapper swapper) {
        if (from >= mid || mid >= to) {
            return;
        }
        if (to - from == 2) {
            if (comp.compare(mid, from) < 0) {
                swapper.swap(from, mid);
            }
            return;
        }
        int firstCut;
        int secondCut;
        if (mid - from > to - mid) {
            firstCut = from + (mid - from) / 2;
            secondCut = lowerBound(mid, to, firstCut, comp);
        }
        else {
            secondCut = mid + (to - mid) / 2;
            firstCut = upperBound(from, mid, secondCut, comp);
        }
        final int first2 = firstCut;
        final int middle2 = mid;
        final int last2 = secondCut;
        if (middle2 != first2 && middle2 != last2) {
            int first3 = first2;
            int last3 = middle2;
            while (first3 < --last3) {
                swapper.swap(first3++, last3);
            }
            first3 = middle2;
            last3 = last2;
            while (first3 < --last3) {
                swapper.swap(first3++, last3);
            }
            first3 = first2;
            last3 = last2;
            while (first3 < --last3) {
                swapper.swap(first3++, last3);
            }
        }
        mid = firstCut + (secondCut - mid);
        inPlaceMerge(from, firstCut, mid, comp, swapper);
        inPlaceMerge(mid, secondCut, to, comp, swapper);
    }
    
    private static int lowerBound(int from, final int to, final int pos, final IntComparator comp) {
        int len = to - from;
        while (len > 0) {
            final int half = len / 2;
            final int middle = from + half;
            if (comp.compare(middle, pos) < 0) {
                from = middle + 1;
                len -= half + 1;
            }
            else {
                len = half;
            }
        }
        return from;
    }
    
    private static int upperBound(int from, final int mid, final int pos, final IntComparator comp) {
        int len = mid - from;
        while (len > 0) {
            final int half = len / 2;
            final int middle = from + half;
            if (comp.compare(pos, middle) < 0) {
                len = half;
            }
            else {
                from = middle + 1;
                len -= half + 1;
            }
        }
        return from;
    }
    
    private static int med3(final int a, final int b, final int c, final IntComparator comp) {
        final int ab = comp.compare(a, b);
        final int ac = comp.compare(a, c);
        final int bc = comp.compare(b, c);
        return (ab < 0) ? ((bc < 0) ? b : ((ac < 0) ? c : a)) : ((bc > 0) ? b : ((ac > 0) ? c : a));
    }
    
    public static void mergeSort(final int from, final int to, final IntComparator c, final Swapper swapper) {
        final int length = to - from;
        if (length < 7) {
            for (int i = from; i < to; ++i) {
                for (int j = i; j > from && c.compare(j - 1, j) > 0; --j) {
                    swapper.swap(j, j - 1);
                }
            }
            return;
        }
        final int mid = from + to >>> 1;
        mergeSort(from, mid, c, swapper);
        mergeSort(mid, to, c, swapper);
        if (c.compare(mid - 1, mid) <= 0) {
            return;
        }
        inPlaceMerge(from, mid, to, c, swapper);
    }
    
    public static void quickSort(final int from, final int to, final IntComparator comp, final Swapper swapper) {
        final int len = to - from;
        if (len < 7) {
            for (int i = from; i < to; ++i) {
                for (int j = i; j > from && comp.compare(j - 1, j) > 0; --j) {
                    swapper.swap(j, j - 1);
                }
            }
            return;
        }
        int m = from + len / 2;
        if (len > 7) {
            int l = from;
            int n = to - 1;
            if (len > 40) {
                final int s = len / 8;
                l = med3(l, l + s, l + 2 * s, comp);
                m = med3(m - s, m, m + s, comp);
                n = med3(n - 2 * s, n - s, n, comp);
            }
            m = med3(l, m, n, comp);
        }
        int b;
        int a = b = from;
        int d;
        int c = d = to - 1;
        while (true) {
            int comparison;
            if (b <= c && (comparison = comp.compare(b, m)) <= 0) {
                if (comparison == 0) {
                    if (a == m) {
                        m = b;
                    }
                    else if (b == m) {
                        m = a;
                    }
                    swapper.swap(a++, b);
                }
                ++b;
            }
            else {
                while (c >= b && (comparison = comp.compare(c, m)) >= 0) {
                    if (comparison == 0) {
                        if (c == m) {
                            m = d;
                        }
                        else if (d == m) {
                            m = c;
                        }
                        swapper.swap(c, d--);
                    }
                    --c;
                }
                if (b > c) {
                    break;
                }
                if (b == m) {
                    m = d;
                }
                else if (c == m) {
                    m = c;
                }
                swapper.swap(b++, c--);
            }
        }
        final int n2 = to;
        int s2 = Math.min(a - from, b - a);
        vecSwap(swapper, from, b - s2, s2);
        s2 = Math.min(d - c, n2 - d - 1);
        vecSwap(swapper, b, n2 - s2, s2);
        if ((s2 = b - a) > 1) {
            quickSort(from, from + s2, comp, swapper);
        }
        if ((s2 = d - c) > 1) {
            quickSort(n2 - s2, n2, comp, swapper);
        }
    }
    
    private static void vecSwap(final Swapper swapper, int from, int l, final int s) {
        for (int i = 0; i < s; ++i, ++from, ++l) {
            swapper.swap(from, l);
        }
    }
}
