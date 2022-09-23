// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public final class QuickSort implements IndexedSorter
{
    private static final IndexedSorter alt;
    
    private static void fix(final IndexedSortable s, final int p, final int r) {
        if (s.compare(p, r) > 0) {
            s.swap(p, r);
        }
    }
    
    protected static int getMaxDepth(final int x) {
        if (x <= 0) {
            throw new IllegalArgumentException("Undefined for " + x);
        }
        return 32 - Integer.numberOfLeadingZeros(x - 1) << 2;
    }
    
    @Override
    public void sort(final IndexedSortable s, final int p, final int r) {
        this.sort(s, p, r, null);
    }
    
    @Override
    public void sort(final IndexedSortable s, final int p, final int r, final Progressable rep) {
        sortInternal(s, p, r, rep, getMaxDepth(r - p));
    }
    
    private static void sortInternal(final IndexedSortable s, int p, int r, final Progressable rep, int depth) {
        if (null != rep) {
            rep.progress();
        }
        while (r - p >= 13) {
            if (--depth < 0) {
                QuickSort.alt.sort(s, p, r, rep);
                return;
            }
            fix(s, p + r >>> 1, p);
            fix(s, p + r >>> 1, r - 1);
            fix(s, p, r - 1);
            int i = p;
            int j = r;
            int ll = p;
            int rr = r;
            while (true) {
                if (++i < j) {
                    final int cr;
                    if ((cr = s.compare(i, p)) <= 0) {
                        if (0 == cr && ++ll != i) {
                            s.swap(ll, i);
                            continue;
                        }
                        continue;
                    }
                }
                int cr;
                while (--j > i && (cr = s.compare(p, j)) <= 0) {
                    if (0 == cr && --rr != j) {
                        s.swap(rr, j);
                    }
                }
                if (i >= j) {
                    break;
                }
                s.swap(i, j);
            }
            j = i;
            while (ll >= p) {
                s.swap(ll--, --i);
            }
            while (rr < r) {
                s.swap(rr++, j++);
            }
            assert i != j;
            if (i - p < r - j) {
                sortInternal(s, p, i, rep, depth);
                p = j;
            }
            else {
                sortInternal(s, j, r, rep, depth);
                r = i;
            }
        }
        for (int i = p; i < r; ++i) {
            for (int j = i; j > p && s.compare(j - 1, j) > 0; --j) {
                s.swap(j, j - 1);
            }
        }
    }
    
    static {
        alt = new HeapSort();
    }
}
