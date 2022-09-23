// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.metrics2.MetricsInfo;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class MutableGaugeLong extends MutableGauge
{
    private AtomicLong value;
    
    MutableGaugeLong(final MetricsInfo info, final long initValue) {
        super(info);
        (this.value = new AtomicLong()).set(initValue);
    }
    
    public long value() {
        return this.value.get();
    }
    
    @Override
    public void incr() {
        this.incr(1L);
    }
    
    public void incr(final long delta) {
        this.value.addAndGet(delta);
        this.setChanged();
    }
    
    @Override
    public void decr() {
        this.decr(1L);
    }
    
    public void decr(final long delta) {
        this.value.addAndGet(-delta);
        this.setChanged();
    }
    
    public void set(final long value) {
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
