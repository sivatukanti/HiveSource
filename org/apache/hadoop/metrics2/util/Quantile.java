// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.util;

import com.google.common.collect.ComparisonChain;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class Quantile implements Comparable<Quantile>
{
    public final double quantile;
    public final double error;
    
    public Quantile(final double quantile, final double error) {
        this.quantile = quantile;
        this.error = error;
    }
    
    @Override
    public boolean equals(final Object aThat) {
        if (this == aThat) {
            return true;
        }
        if (!(aThat instanceof Quantile)) {
            return false;
        }
        final Quantile that = (Quantile)aThat;
        final long qbits = Double.doubleToLongBits(this.quantile);
        final long ebits = Double.doubleToLongBits(this.error);
        return qbits == Double.doubleToLongBits(that.quantile) && ebits == Double.doubleToLongBits(that.error);
    }
    
    @Override
    public int hashCode() {
        return (int)(Double.doubleToLongBits(this.quantile) ^ Double.doubleToLongBits(this.error));
    }
    
    @Override
    public int compareTo(final Quantile other) {
        return ComparisonChain.start().compare(this.quantile, other.quantile).compare(this.error, other.error).result();
    }
    
    @Override
    public String toString() {
        return String.format("%.2f %%ile +/- %.2f%%", this.quantile * 100.0, this.error * 100.0);
    }
}
