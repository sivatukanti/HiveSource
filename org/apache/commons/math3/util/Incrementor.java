// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;

public class Incrementor
{
    private int maximalCount;
    private int count;
    private final MaxCountExceededCallback maxCountCallback;
    
    public Incrementor() {
        this(0);
    }
    
    public Incrementor(final int max) {
        this(max, new MaxCountExceededCallback() {
            public void trigger(final int max) {
                throw new MaxCountExceededException(max);
            }
        });
    }
    
    public Incrementor(final int max, final MaxCountExceededCallback cb) {
        this.count = 0;
        if (cb == null) {
            throw new NullArgumentException();
        }
        this.maximalCount = max;
        this.maxCountCallback = cb;
    }
    
    public void setMaximalCount(final int max) {
        this.maximalCount = max;
    }
    
    public int getMaximalCount() {
        return this.maximalCount;
    }
    
    public int getCount() {
        return this.count;
    }
    
    public boolean canIncrement() {
        return this.count < this.maximalCount;
    }
    
    public void incrementCount(final int value) {
        for (int i = 0; i < value; ++i) {
            this.incrementCount();
        }
    }
    
    public void incrementCount() {
        if (++this.count > this.maximalCount) {
            this.maxCountCallback.trigger(this.maximalCount);
        }
    }
    
    public void resetCount() {
        this.count = 0;
    }
    
    public interface MaxCountExceededCallback
    {
        void trigger(final int p0);
    }
}
