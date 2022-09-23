// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.misc;

import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.List;

public class FastQueue<T>
{
    protected List<T> data;
    protected int p;
    protected int range;
    
    public FastQueue() {
        this.data = new ArrayList<T>();
        this.p = 0;
        this.range = -1;
    }
    
    public void reset() {
        this.clear();
    }
    
    public void clear() {
        this.p = 0;
        this.data.clear();
    }
    
    public T remove() {
        final T o = this.elementAt(0);
        ++this.p;
        if (this.p == this.data.size()) {
            this.clear();
        }
        return o;
    }
    
    public void add(final T o) {
        this.data.add(o);
    }
    
    public int size() {
        return this.data.size() - this.p;
    }
    
    public int range() {
        return this.range;
    }
    
    public T head() {
        return this.elementAt(0);
    }
    
    public T elementAt(final int i) {
        final int absIndex = this.p + i;
        if (absIndex >= this.data.size()) {
            throw new NoSuchElementException("queue index " + absIndex + " > last index " + (this.data.size() - 1));
        }
        if (absIndex < 0) {
            throw new NoSuchElementException("queue index " + absIndex + " < 0");
        }
        if (absIndex > this.range) {
            this.range = absIndex;
        }
        return this.data.get(absIndex);
    }
    
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        for (int n = this.size(), i = 0; i < n; ++i) {
            buf.append(this.elementAt(i));
            if (i + 1 < n) {
                buf.append(" ");
            }
        }
        return buf.toString();
    }
}
