// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.misc;

import java.util.NoSuchElementException;

public abstract class LookaheadStream<T> extends FastQueue<T>
{
    public static final int UNINITIALIZED_EOF_ELEMENT_INDEX = Integer.MAX_VALUE;
    protected int currentElementIndex;
    protected T prevElement;
    public T eof;
    protected int lastMarker;
    protected int markDepth;
    
    public LookaheadStream() {
        this.currentElementIndex = 0;
        this.eof = null;
        this.markDepth = 0;
    }
    
    public void reset() {
        super.reset();
        this.currentElementIndex = 0;
        this.p = 0;
        this.prevElement = null;
    }
    
    public abstract T nextElement();
    
    public abstract boolean isEOF(final T p0);
    
    public T remove() {
        final T o = this.elementAt(0);
        ++this.p;
        if (this.p == this.data.size() && this.markDepth == 0) {
            this.clear();
        }
        return o;
    }
    
    public void consume() {
        this.syncAhead(1);
        this.prevElement = this.remove();
        ++this.currentElementIndex;
    }
    
    protected void syncAhead(final int need) {
        final int n = this.p + need - 1 - this.data.size() + 1;
        if (n > 0) {
            this.fill(n);
        }
    }
    
    public void fill(final int n) {
        for (int i = 1; i <= n; ++i) {
            final T o = this.nextElement();
            if (this.isEOF(o)) {
                this.eof = o;
            }
            this.data.add(o);
        }
    }
    
    public int size() {
        throw new UnsupportedOperationException("streams are of unknown size");
    }
    
    public T LT(final int k) {
        if (k == 0) {
            return null;
        }
        if (k < 0) {
            return this.LB(-k);
        }
        this.syncAhead(k);
        if (this.p + k - 1 > this.data.size()) {
            return this.eof;
        }
        return this.elementAt(k - 1);
    }
    
    public int index() {
        return this.currentElementIndex;
    }
    
    public int mark() {
        ++this.markDepth;
        return this.lastMarker = this.p;
    }
    
    public void release(final int marker) {
    }
    
    public void rewind(final int marker) {
        --this.markDepth;
        this.seek(marker);
    }
    
    public void rewind() {
        this.seek(this.lastMarker);
    }
    
    public void seek(final int index) {
        this.p = index;
    }
    
    protected T LB(final int k) {
        if (k == 1) {
            return this.prevElement;
        }
        throw new NoSuchElementException("can't look backwards more than one token in this stream");
    }
}
