// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

public interface ReadWritableInterval extends ReadableInterval
{
    void setInterval(final long p0, final long p1);
    
    void setInterval(final ReadableInterval p0);
    
    void setInterval(final ReadableInstant p0, final ReadableInstant p1);
    
    void setChronology(final Chronology p0);
    
    void setStartMillis(final long p0);
    
    void setStart(final ReadableInstant p0);
    
    void setEndMillis(final long p0);
    
    void setEnd(final ReadableInstant p0);
    
    void setDurationAfterStart(final ReadableDuration p0);
    
    void setDurationBeforeEnd(final ReadableDuration p0);
    
    void setPeriodAfterStart(final ReadablePeriod p0);
    
    void setPeriodBeforeEnd(final ReadablePeriod p0);
}
