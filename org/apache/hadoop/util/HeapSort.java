// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public final class HeapSort implements IndexedSorter
{
    private static void downHeap(final IndexedSortable s, final int b, int i, final int N) {
        for (int idx = i << 1; idx < N; idx = i << 1) {
            if (idx + 1 < N && s.compare(b + idx, b + idx + 1) < 0) {
                if (s.compare(b + i, b + idx + 1) >= 0) {
                    return;
                }
                s.swap(b + i, b + idx + 1);
                i = idx + 1;
            }
            else {
                if (s.compare(b + i, b + idx) >= 0) {
                    return;
                }
                s.swap(b + i, b + idx);
                i = idx;
            }
        }
    }
    
    @Override
    public void sort(final IndexedSortable s, final int p, final int r) {
        this.sort(s, p, r, null);
    }
    
    @Override
    public void sort(final IndexedSortable s, final int p, final int r, final Progressable rep) {
        final int N = r - p;
        int i;
        for (int t = i = Integer.highestOneBit(N); i > 1; i >>>= 1) {
            for (int j = i >>> 1; j < i; ++j) {
                downHeap(s, p - 1, j, N + 1);
            }
            if (null != rep) {
                rep.progress();
            }
        }
        for (i = r - 1; i > p; --i) {
            s.swap(p, i);
            downHeap(s, p - 1, 1, i - p + 1);
        }
    }
}
