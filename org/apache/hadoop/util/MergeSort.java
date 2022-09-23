// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.Comparator;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "MapReduce" })
@InterfaceStability.Unstable
public class MergeSort
{
    IntWritable I;
    IntWritable J;
    private Comparator<IntWritable> comparator;
    
    public MergeSort(final Comparator<IntWritable> comparator) {
        this.I = new IntWritable(0);
        this.J = new IntWritable(0);
        this.comparator = comparator;
    }
    
    public void mergeSort(final int[] src, final int[] dest, final int low, final int high) {
        final int length = high - low;
        if (length < 7) {
            for (int i = low; i < high; ++i) {
                for (int j = i; j > low; --j) {
                    this.I.set(dest[j - 1]);
                    this.J.set(dest[j]);
                    if (this.comparator.compare(this.I, this.J) > 0) {
                        this.swap(dest, j, j - 1);
                    }
                }
            }
            return;
        }
        final int mid = low + high >>> 1;
        this.mergeSort(dest, src, low, mid);
        this.mergeSort(dest, src, mid, high);
        this.I.set(src[mid - 1]);
        this.J.set(src[mid]);
        if (this.comparator.compare(this.I, this.J) <= 0) {
            System.arraycopy(src, low, dest, low, length);
            return;
        }
        int k = low;
        int p = low;
        int q = mid;
        while (k < high) {
            if (q < high && p < mid) {
                this.I.set(src[p]);
                this.J.set(src[q]);
            }
            if (q >= high || (p < mid && this.comparator.compare(this.I, this.J) <= 0)) {
                dest[k] = src[p++];
            }
            else {
                dest[k] = src[q++];
            }
            ++k;
        }
    }
    
    private void swap(final int[] x, final int a, final int b) {
        final int t = x[a];
        x[a] = x[b];
        x[b] = t;
    }
}
