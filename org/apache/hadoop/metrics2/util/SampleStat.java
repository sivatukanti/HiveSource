// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.util;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class SampleStat
{
    private final MinMax minmax;
    private long numSamples;
    private double a0;
    private double a1;
    private double s0;
    private double s1;
    private double total;
    
    public SampleStat() {
        this.minmax = new MinMax();
        this.numSamples = 0L;
        final double n = 0.0;
        this.s0 = n;
        this.a0 = n;
        this.total = 0.0;
    }
    
    public void reset() {
        this.numSamples = 0L;
        final double n = 0.0;
        this.s0 = n;
        this.a0 = n;
        this.total = 0.0;
        this.minmax.reset();
    }
    
    void reset(final long numSamples, final double a0, final double a1, final double s0, final double s1, final double total, final MinMax minmax) {
        this.numSamples = numSamples;
        this.a0 = a0;
        this.a1 = a1;
        this.s0 = s0;
        this.s1 = s1;
        this.total = total;
        this.minmax.reset(minmax);
    }
    
    public void copyTo(final SampleStat other) {
        other.reset(this.numSamples, this.a0, this.a1, this.s0, this.s1, this.total, this.minmax);
    }
    
    public SampleStat add(final double x) {
        this.minmax.add(x);
        return this.add(1L, x);
    }
    
    public SampleStat add(final long nSamples, final double x) {
        this.numSamples += nSamples;
        this.total += x;
        if (this.numSamples == 1L) {
            this.a1 = x;
            this.a0 = x;
            this.s0 = 0.0;
        }
        else {
            this.a1 = this.a0 + (x - this.a0) / this.numSamples;
            this.s1 = this.s0 + (x - this.a0) * (x - this.a1);
            this.a0 = this.a1;
            this.s0 = this.s1;
        }
        return this;
    }
    
    public long numSamples() {
        return this.numSamples;
    }
    
    public double total() {
        return this.total;
    }
    
    public double mean() {
        return (this.numSamples > 0L) ? (this.total / this.numSamples) : 0.0;
    }
    
    public double variance() {
        return (this.numSamples > 1L) ? (this.s1 / (this.numSamples - 1L)) : 0.0;
    }
    
    public double stddev() {
        return Math.sqrt(this.variance());
    }
    
    public double min() {
        return this.minmax.min();
    }
    
    public double max() {
        return this.minmax.max();
    }
    
    @Override
    public String toString() {
        try {
            return "Samples = " + this.numSamples() + "  Min = " + this.min() + "  Mean = " + this.mean() + "  Std Dev = " + this.stddev() + "  Max = " + this.max();
        }
        catch (Throwable t) {
            return super.toString();
        }
    }
    
    public static class MinMax
    {
        static final double DEFAULT_MIN_VALUE = 3.4028234663852886E38;
        static final double DEFAULT_MAX_VALUE = 1.401298464324817E-45;
        private double min;
        private double max;
        
        public MinMax() {
            this.min = 3.4028234663852886E38;
            this.max = 1.401298464324817E-45;
        }
        
        public void add(final double value) {
            if (value > this.max) {
                this.max = value;
            }
            if (value < this.min) {
                this.min = value;
            }
        }
        
        public double min() {
            return this.min;
        }
        
        public double max() {
            return this.max;
        }
        
        public void reset() {
            this.min = 3.4028234663852886E38;
            this.max = 1.401298464324817E-45;
        }
        
        public void reset(final MinMax other) {
            this.min = other.min();
            this.max = other.max();
        }
    }
}
