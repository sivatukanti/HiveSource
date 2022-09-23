// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.metrics2.MetricsInfo;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class MutableGaugeInt extends MutableGauge
{
    private AtomicInteger value;
    
    MutableGaugeInt(final MetricsInfo info, final int initValue) {
        super(info);
        (this.value = new AtomicInteger()).set(initValue);
    }
    
    public int value() {
        return this.value.get();
    }
    
    @Override
    public void incr() {
        this.incr(1);
    }
    
    public void incr(final int delta) {
        this.value.addAndGet(delta);
        this.setChanged();
    }
    
    @Override
    public void decr() {
        this.decr(1);
    }
    
    public void decr(final int delta) {
        this.value.addAndGet(-delta);
        this.setChanged();
    }
    
    public void set(final int value) {
        this.value.set(value);
        this.setChanged();
    }
    
    @Override
    public void snapshot(final MetricsRecordBuilder builder, final boolean all) {
        if (all || this.changed()) {
            builder.addGauge(this.info(), this.value());
            this.clearChanged();
        }
    }
}
