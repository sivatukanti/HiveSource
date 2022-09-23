// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.statistic;

import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;

public class CounterStatistic
{
    protected final LongAccumulator _max;
    protected final AtomicLong _current;
    protected final LongAdder _total;
    
    public CounterStatistic() {
        this._max = new LongAccumulator(Math::max, 0L);
        this._current = new AtomicLong();
        this._total = new LongAdder();
    }
    
    public void reset() {
        this._total.reset();
        this._max.reset();
        final long current = this._current.get();
        this._total.add(current);
        this._max.accumulate(current);
    }
    
    public void reset(final long value) {
        this._current.set(value);
        this._total.reset();
        this._max.reset();
        if (value > 0L) {
            this._total.add(value);
            this._max.accumulate(value);
        }
    }
    
    public long add(final long delta) {
        final long value = this._current.addAndGet(delta);
        if (delta > 0L) {
            this._total.add(delta);
            this._max.accumulate(value);
        }
        return value;
    }
    
    public long increment() {
        final long value = this._current.incrementAndGet();
        this._total.increment();
        this._max.accumulate(value);
        return value;
    }
    
    public long decrement() {
        return this._current.decrementAndGet();
    }
    
    public long getMax() {
        return this._max.get();
    }
    
    public long getCurrent() {
        return this._current.get();
    }
    
    public long getTotal() {
        return this._total.sum();
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x{c=%d,m=%d,t=%d}", this.getClass().getSimpleName(), this.hashCode(), this._current.get(), this._max.get(), this._total.sum());
    }
}
