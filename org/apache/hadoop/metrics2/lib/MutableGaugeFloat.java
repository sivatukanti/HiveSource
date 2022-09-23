// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.metrics2.MetricsInfo;
import java.util.concurrent.atomic.AtomicInteger;

public class MutableGaugeFloat extends MutableGauge
{
    private AtomicInteger value;
    
    MutableGaugeFloat(final MetricsInfo info, final float initValue) {
        super(info);
        (this.value = new AtomicInteger()).set(Float.floatToIntBits(initValue));
    }
    
    public float value() {
        return Float.intBitsToFloat(this.value.get());
    }
    
    @Override
    public void incr() {
        this.incr(1.0f);
    }
    
    @Override
    public void decr() {
        this.incr(-1.0f);
    }
    
    @Override
    public void snapshot(final MetricsRecordBuilder builder, final boolean all) {
        if (all || this.changed()) {
            builder.addGauge(this.info(), this.value());
            this.clearChanged();
        }
    }
    
    public void set(final float value) {
        this.value.set(Float.floatToIntBits(value));
        this.setChanged();
    }
    
    private final boolean compareAndSet(final float expect, final float update) {
        return this.value.compareAndSet(Float.floatToIntBits(expect), Float.floatToIntBits(update));
    }
    
    private void incr(final float delta) {
        float current;
        float next;
        do {
            current = (float)this.value.get();
            next = current + delta;
        } while (!this.compareAndSet(current, next));
        this.setChanged();
    }
}
