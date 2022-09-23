// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public abstract class SequentialNumber implements IdGenerator
{
    private final AtomicLong currentValue;
    
    protected SequentialNumber(final long initialValue) {
        this.currentValue = new AtomicLong(initialValue);
    }
    
    public long getCurrentValue() {
        return this.currentValue.get();
    }
    
    public void setCurrentValue(final long value) {
        this.currentValue.set(value);
    }
    
    @Override
    public long nextValue() {
        return this.currentValue.incrementAndGet();
    }
    
    public void skipTo(final long newValue) throws IllegalStateException {
        while (true) {
            final long c = this.getCurrentValue();
            if (newValue < c) {
                throw new IllegalStateException("Cannot skip to less than the current value (=" + c + "), where newValue=" + newValue);
            }
            if (this.currentValue.compareAndSet(c, newValue)) {
                return;
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        if (that == null || this.getClass() != that.getClass()) {
            return false;
        }
        final AtomicLong thatValue = ((SequentialNumber)that).currentValue;
        return this.currentValue.equals(thatValue);
    }
    
    @Override
    public int hashCode() {
        final long v = this.currentValue.get();
        return (int)v ^ (int)(v >>> 32);
    }
}
