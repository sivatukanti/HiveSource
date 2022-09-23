// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

public interface ReadableInstant extends Comparable<ReadableInstant>
{
    long getMillis();
    
    Chronology getChronology();
    
    DateTimeZone getZone();
    
    int get(final DateTimeFieldType p0);
    
    boolean isSupported(final DateTimeFieldType p0);
    
    Instant toInstant();
    
    boolean isEqual(final ReadableInstant p0);
    
    boolean isAfter(final ReadableInstant p0);
    
    boolean isBefore(final ReadableInstant p0);
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    String toString();
}
