// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

public abstract class DurationField implements Comparable<DurationField>
{
    public abstract DurationFieldType getType();
    
    public abstract String getName();
    
    public abstract boolean isSupported();
    
    public abstract boolean isPrecise();
    
    public abstract long getUnitMillis();
    
    public abstract int getValue(final long p0);
    
    public abstract long getValueAsLong(final long p0);
    
    public abstract int getValue(final long p0, final long p1);
    
    public abstract long getValueAsLong(final long p0, final long p1);
    
    public abstract long getMillis(final int p0);
    
    public abstract long getMillis(final long p0);
    
    public abstract long getMillis(final int p0, final long p1);
    
    public abstract long getMillis(final long p0, final long p1);
    
    public abstract long add(final long p0, final int p1);
    
    public abstract long add(final long p0, final long p1);
    
    public long subtract(final long n, final int n2) {
        if (n2 == Integer.MIN_VALUE) {
            return this.subtract(n, (long)n2);
        }
        return this.add(n, -n2);
    }
    
    public long subtract(final long n, final long n2) {
        if (n2 == Long.MIN_VALUE) {
            throw new ArithmeticException("Long.MIN_VALUE cannot be negated");
        }
        return this.add(n, -n2);
    }
    
    public abstract int getDifference(final long p0, final long p1);
    
    public abstract long getDifferenceAsLong(final long p0, final long p1);
    
    @Override
    public abstract String toString();
}
