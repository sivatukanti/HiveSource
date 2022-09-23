// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

public interface ReadablePeriod
{
    PeriodType getPeriodType();
    
    int size();
    
    DurationFieldType getFieldType(final int p0);
    
    int getValue(final int p0);
    
    int get(final DurationFieldType p0);
    
    boolean isSupported(final DurationFieldType p0);
    
    Period toPeriod();
    
    MutablePeriod toMutablePeriod();
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    String toString();
}
