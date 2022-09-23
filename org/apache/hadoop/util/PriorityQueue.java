// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public abstract class PriorityQueue<T>
{
    private T[] heap;
    private int size;
    private int maxSize;
    
    protected abstract boolean lessThan(final Object p0, final Object p1);
    
    protected final void initialize(final int maxSize) {
        this.size = 0;
        final int heapSize = maxSize + 1;
        this.heap = (T[])new Object[heapSize];
        this.maxSize = maxSize;
    }
    
    public final void put(final T element) {
        ++this.size;
        this.heap[this.size] = element;
        this.upHeap();
    }
    
    public boolean insert(final T element) {
        if (this.size < this.maxSize) {
            this.put(element);
            return true;
        }
        if (this.size > 0 && !this.lessThan(element, this.top())) {
            this.heap[1] = element;
            this.adjustTop();
            return true;
        }
        return false;
    }
    
    public final T top() {
        if (this.size > 0) {
            return this.heap[1];
        }
        return null;
    }
    
    public final T pop() {
        if (this.size > 0) {
            final T result = this.heap[1];
            this.heap[1] = this.heap[this.size];
            this.heap[this.size] = null;
            --this.size;
            this.downHeap();
            return result;
        }
        return null;
    }
    
    public final void adjustTop() {
        this.downHeap();
    }
    
    public final int size() {
        return this.size;
    }
    
    public final void clear() {
        for (int i = 0; i <= this.size; ++i) {
            this.heap[i] = null;
        }
        this.size = 0;
    }
    
    private final void upHeap() {
        int i = this.size;
        final T node = this.heap[i];
        for (int j = i >>> 1; j > 0 && this.lessThan(node, this.heap[j]); j >>>= 1) {
            this.heap[i] = this.heap[j];
            i = j;
        }
        this.heap[i] = node;
    }
    
    private final void downHeap() {
        int i = 1;
        final T node = this.heap[i];
        int j = i << 1;
        int k = j + 1;
        if (k <= this.size && this.lessThan(this.heap[k], this.heap[j])) {
            j = k;
        }
        while (j <= this.size && this.lessThan(this.heap[j], node)) {
            this.heap[i] = this.heap[j];
            i = j;
            j = i << 1;
            k = j + 1;
            if (k <= this.size && this.lessThan(this.heap[k], this.heap[j])) {
                j = k;
            }
        }
        this.heap[i] = node;
    }
}
