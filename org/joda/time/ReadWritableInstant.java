// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

public interface ReadWritableInstant extends ReadableInstant
{
    void setMillis(final long p0);
    
    void setMillis(final ReadableInstant p0);
    
    void setChronology(final Chronology p0);
    
    void setZone(final DateTimeZone p0);
    
    void setZoneRetainFields(final DateTimeZone p0);
    
    void add(final long p0);
    
    void add(final ReadableDuration p0);
    
    void add(final ReadableDuration p0, final int p1);
    
    void add(final ReadablePeriod p0);
    
    void add(final ReadablePeriod p0, final int p1);
    
    void set(final DateTimeFieldType p0, final int p1);
    
    void add(final DurationFieldType p0, final int p1);
}
