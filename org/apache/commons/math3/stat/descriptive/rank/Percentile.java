// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.stat.descriptive.rank;

import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.util.Arrays;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import java.io.Serializable;
import org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic;

public class Percentile extends AbstractUnivariateStatistic implements Serializable
{
    private static final long serialVersionUID = -8091216485095130416L;
    private static final int MIN_SELECT_SIZE = 15;
    private static final int MAX_CACHED_LEVELS = 10;
    private double quantile;
    private int[] cachedPivots;
    
    public Percentile() {
        this(50.0);
    }
    
    public Percentile(final double p) throws MathIllegalArgumentException {
        this.quantile = 0.0;
        this.setQuantile(p);
        this.cachedPivots = null;
    }
    
    public Percentile(final Percentile original) throws NullArgumentException {
        this.quantile = 0.0;
        copy(original, this);
    }
    
    @Override
    public void setData(final double[] values) {
        if (values == null) {
            this.cachedPivots = null;
        }
        else {
            Arrays.fill(this.cachedPivots = new int[1023], -1);
        }
        super.setData(values);
    }
    
    @Override
    public void setData(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        if (values == null) {
            this.cachedPivots = null;
        }
        else {
            Arrays.fill(this.cachedPivots = new int[1023], -1);
        }
        super.setData(values, begin, length);
    }
    
    public double evaluate(final double p) throws MathIllegalArgumentException {
        return this.evaluate(this.getDataRef(), p);
    }
    
    public double evaluate(final double[] values, final double p) throws MathIllegalArgumentException {
        this.test(values, 0, 0);
        return this.evaluate(values, 0, values.length, p);
    }
    
    @Override
    public double evaluate(final double[] values, final int start, final int length) throws MathIllegalArgumentException {
        return this.evaluate(values, start, length, this.quantile);
    }
    
    public double evaluate(final double[] values, final int begin, final int length, final double p) throws MathIllegalArgumentException {
        this.test(values, begin, length);
        if (p > 100.0 || p <= 0.0) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUNDS_QUANTILE_VALUE, p, 0, 100);
        }
        if (length == 0) {
            return Double.NaN;
        }
        if (length == 1) {
            return values[begin];
        }
        final double n = length;
        final double pos = p * (n + 1.0) / 100.0;
        final double fpos = FastMath.floor(pos);
        final int intPos = (int)fpos;
        final double dif = pos - fpos;
        double[] work;
        int[] pivotsHeap;
        if (values == this.getDataRef()) {
            work = this.getDataRef();
            pivotsHeap = this.cachedPivots;
        }
        else {
            work = new double[length];
            System.arraycopy(values, begin, work, 0, length);
            pivotsHeap = new int[1023];
            Arrays.fill(pivotsHeap, -1);
        }
        if (pos < 1.0) {
            return this.select(work, pivotsHeap, 0);
        }
        if (pos >= n) {
            return this.select(work, pivotsHeap, length - 1);
        }
        final double lower = this.select(work, pivotsHeap, intPos - 1);
        final double upper = this.select(work, pivotsHeap, intPos);
        return lower + dif * (upper - lower);
    }
    
    private double select(final double[] work, final int[] pivotsHeap, final int k) {
        int begin = 0;
        int end = work.length;
        int node = 0;
        while (end - begin > 15) {
            int pivot;
            if (node < pivotsHeap.length && pivotsHeap[node] >= 0) {
                pivot = pivotsHeap[node];
            }
            else {
                pivot = this.partition(work, begin, end, this.medianOf3(work, begin, end));
                if (node < pivotsHeap.length) {
                    pivotsHeap[node] = pivot;
                }
            }
            if (k == pivot) {
                return work[k];
            }
            if (k < pivot) {
                end = pivot;
                node = FastMath.min(2 * node + 1, pivotsHeap.length);
            }
            else {
                begin = pivot + 1;
                node = FastMath.min(2 * node + 2, pivotsHeap.length);
            }
        }
        this.insertionSort(work, begin, end);
        return work[k];
    }
    
    int medianOf3(final double[] work, final int begin, final int end) {
        final int inclusiveEnd = end - 1;
        final int middle = begin + (inclusiveEnd - begin) / 2;
        final double wBegin = work[begin];
        final double wMiddle = work[middle];
        final double wEnd = work[inclusiveEnd];
        if (wBegin < wMiddle) {
            if (wMiddle < wEnd) {
                return middle;
            }
            return (wBegin < wEnd) ? inclusiveEnd : begin;
        }
        else {
            if (wBegin < wEnd) {
                return begin;
            }
            return (wMiddle < wEnd) ? inclusiveEnd : middle;
        }
    }
    
    private int partition(final double[] work, final int begin, final int end, final int pivot) {
        final double value = work[pivot];
        work[pivot] = work[begin];
        int i = begin + 1;
        double tmp = 0.0;
        for (int j = end - 1; i < j; work[i++] = work[j], work[j--] = tmp) {
            while (i < j && work[j] > value) {
                --j;
            }
            while (i < j && work[i] < value) {
                ++i;
            }
            if (i < j) {
                tmp = work[i];
            }
        }
        if (i >= end || work[i] > value) {
            --i;
        }
        work[begin] = work[i];
        work[i] = value;
        return i;
    }
    
    private void insertionSort(final double[] work, final int begin, final int end) {
        for (int j = begin + 1; j < end; ++j) {
            double saved;
            int i;
            for (saved = work[j], i = j - 1; i >= begin && saved < work[i]; --i) {
                work[i + 1] = work[i];
            }
            work[i + 1] = saved;
        }
    }
    
    public double getQuantile() {
        return this.quantile;
    }
    
    public void setQuantile(final double p) throws MathIllegalArgumentException {
        if (p <= 0.0 || p > 100.0) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUNDS_QUANTILE_VALUE, p, 0, 100);
        }
        this.quantile = p;
    }
    
    @Override
    public Percentile copy() {
        final Percentile result = new Percentile();
        copy(this, result);
        return result;
    }
    
    public static void copy(final Percentile source, final Percentile dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        if (source.cachedPivots != null) {
            System.arraycopy(source.cachedPivots, 0, dest.cachedPivots, 0, source.cachedPivots.length);
        }
        dest.quantile = source.quantile;
    }
}
