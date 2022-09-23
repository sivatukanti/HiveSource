// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

public interface ReadableDuration extends Comparable<ReadableDuration>
{
    long getMillis();
    
    Duration toDuration();
    
    Period toPeriod();
    
    boolean isEqual(final ReadableDuration p0);
    
    boolean isLongerThan(final ReadableDuration p0);
    
    boolean isShorterThan(final ReadableDuration p0);
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    String toString();
}
