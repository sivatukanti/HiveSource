// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

public interface ReadablePartial extends Comparable<ReadablePartial>
{
    int size();
    
    DateTimeFieldType getFieldType(final int p0);
    
    DateTimeField getField(final int p0);
    
    int getValue(final int p0);
    
    Chronology getChronology();
    
    int get(final DateTimeFieldType p0);
    
    boolean isSupported(final DateTimeFieldType p0);
    
    DateTime toDateTime(final ReadableInstant p0);
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    String toString();
}
