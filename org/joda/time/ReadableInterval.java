// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

public interface ReadableInterval
{
    Chronology getChronology();
    
    long getStartMillis();
    
    DateTime getStart();
    
    long getEndMillis();
    
    DateTime getEnd();
    
    boolean contains(final ReadableInstant p0);
    
    boolean contains(final ReadableInterval p0);
    
    boolean overlaps(final ReadableInterval p0);
    
    boolean isAfter(final ReadableInstant p0);
    
    boolean isAfter(final ReadableInterval p0);
    
    boolean isBefore(final ReadableInstant p0);
    
    boolean isBefore(final ReadableInterval p0);
    
    Interval toInterval();
    
    MutableInterval toMutableInterval();
    
    Duration toDuration();
    
    long toDurationMillis();
    
    Period toPeriod();
    
    Period toPeriod(final PeriodType p0);
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    String toString();
}
