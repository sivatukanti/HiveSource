// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.util;

import com.google.common.base.Joiner;
import com.google.common.annotations.VisibleForTesting;
import java.util.TreeMap;
import java.util.Map;
import com.google.common.base.Preconditions;
import java.util.ListIterator;
import java.util.Arrays;
import java.util.LinkedList;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class SampleQuantiles implements QuantileEstimator
{
    private long count;
    private LinkedList<SampleItem> samples;
    private long[] buffer;
    private int bufferCount;
    private final Quantile[] quantiles;
    
    public SampleQuantiles(final Quantile[] quantiles) {
        this.count = 0L;
        this.buffer = new long[500];
        this.bufferCount = 0;
        this.quantiles = quantiles;
        this.samples = new LinkedList<SampleItem>();
    }
    
    private double allowableError(final int rank) {
        final int size = this.samples.size();
        double minError = size + 1;
        for (final Quantile q : this.quantiles) {
            double error;
            if (rank <= q.quantile * size) {
                error = 2.0 * q.error * (size - rank) / (1.0 - q.quantile);
            }
            else {
                error = 2.0 * q.error * rank / q.quantile;
            }
            if (error < minError) {
                minError = error;
            }
        }
        return minError;
    }
    
    @Override
    public synchronized void insert(final long v) {
        this.buffer[this.bufferCount] = v;
        ++this.bufferCount;
        ++this.count;
        if (this.bufferCount == this.buffer.length) {
            this.insertBatch();
            this.compress();
        }
    }
    
    private void insertBatch() {
        if (this.bufferCount == 0) {
            return;
        }
        Arrays.sort(this.buffer, 0, this.bufferCount);
        int start = 0;
        if (this.samples.size() == 0) {
            final SampleItem newItem = new SampleItem(this.buffer[0], 1, 0);
            this.samples.add(newItem);
            ++start;
        }
        final ListIterator<SampleItem> it = this.samples.listIterator();
        SampleItem item = it.next();
        for (int i = start; i < this.bufferCount; ++i) {
            long v;
            for (v = this.buffer[i]; it.nextIndex() < this.samples.size() && item.value < v; item = it.next()) {}
            if (item.value > v) {
                it.previous();
            }
            int delta;
            if (it.previousIndex() == 0 || it.nextIndex() == this.samples.size()) {
                delta = 0;
            }
            else {
                delta = (int)Math.floor(this.allowableError(it.nextIndex())) - 1;
            }
            final SampleItem newItem2 = new SampleItem(v, 1, delta);
            it.add(newItem2);
            item = newItem2;
        }
        this.bufferCount = 0;
    }
    
    private void compress() {
        if (this.samples.size() < 2) {
            return;
        }
        final ListIterator<SampleItem> it = this.samples.listIterator();
        SampleItem prev = null;
        SampleItem next = it.next();
        while (it.hasNext()) {
            prev = next;
            next = it.next();
            if (prev.g + next.g + next.delta <= this.allowableError(it.previousIndex())) {
                final SampleItem sampleItem = next;
                sampleItem.g += prev.g;
                it.previous();
                it.previous();
                it.remove();
                it.next();
            }
        }
    }
    
    private long query(final double quantile) {
        Preconditions.checkState(!this.samples.isEmpty(), (Object)"no data in estimator");
        int rankMin = 0;
        final int desired = (int)(quantile * this.count);
        final ListIterator<SampleItem> it = this.samples.listIterator();
        SampleItem prev = null;
        SampleItem cur = it.next();
        for (int i = 1; i < this.samples.size(); ++i) {
            prev = cur;
            cur = it.next();
            rankMin += prev.g;
            if (rankMin + cur.g + cur.delta > desired + this.allowableError(i) / 2.0) {
                return prev.value;
            }
        }
        return this.samples.get(this.samples.size() - 1).value;
    }
    
    @Override
    public synchronized Map<Quantile, Long> snapshot() {
        this.insertBatch();
        if (this.samples.isEmpty()) {
            return null;
        }
        final Map<Quantile, Long> values = new TreeMap<Quantile, Long>();
        for (int i = 0; i < this.quantiles.length; ++i) {
            values.put(this.quantiles[i], this.query(this.quantiles[i].quantile));
        }
        return values;
    }
    
    @Override
    public synchronized long getCount() {
        return this.count;
    }
    
    @VisibleForTesting
    public synchronized int getSampleCount() {
        return this.samples.size();
    }
    
    @Override
    public synchronized void clear() {
        this.count = 0L;
        this.bufferCount = 0;
        this.samples.clear();
    }
    
    @Override
    public synchronized String toString() {
        final Map<Quantile, Long> data = this.snapshot();
        if (data == null) {
            return "[no samples]";
        }
        return Joiner.on("\n").withKeyValueSeparator(": ").join(data);
    }
    
    private static class SampleItem
    {
        public final long value;
        public int g;
        public final int delta;
        
        public SampleItem(final long value, final int lowerDelta, final int delta) {
            this.value = value;
            this.g = lowerDelta;
            this.delta = delta;
        }
        
        @Override
        public String toString() {
            return String.format("%d, %d, %d", this.value, this.g, this.delta);
        }
    }
}
