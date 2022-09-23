// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

public abstract class Chronology
{
    public abstract DateTimeZone getZone();
    
    public abstract Chronology withUTC();
    
    public abstract Chronology withZone(final DateTimeZone p0);
    
    public abstract long getDateTimeMillis(final int p0, final int p1, final int p2, final int p3);
    
    public abstract long getDateTimeMillis(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6);
    
    public abstract long getDateTimeMillis(final long p0, final int p1, final int p2, final int p3, final int p4);
    
    public abstract void validate(final ReadablePartial p0, final int[] p1);
    
    public abstract int[] get(final ReadablePartial p0, final long p1);
    
    public abstract long set(final ReadablePartial p0, final long p1);
    
    public abstract int[] get(final ReadablePeriod p0, final long p1, final long p2);
    
    public abstract int[] get(final ReadablePeriod p0, final long p1);
    
    public abstract long add(final ReadablePeriod p0, final long p1, final int p2);
    
    public abstract long add(final long p0, final long p1, final int p2);
    
    public abstract DurationField millis();
    
    public abstract DateTimeField millisOfSecond();
    
    public abstract DateTimeField millisOfDay();
    
    public abstract DurationField seconds();
    
    public abstract DateTimeField secondOfMinute();
    
    public abstract DateTimeField secondOfDay();
    
    public abstract DurationField minutes();
    
    public abstract DateTimeField minuteOfHour();
    
    public abstract DateTimeField minuteOfDay();
    
    public abstract DurationField hours();
    
    public abstract DateTimeField hourOfDay();
    
    public abstract DateTimeField clockhourOfDay();
    
    public abstract DurationField halfdays();
    
    public abstract DateTimeField hourOfHalfday();
    
    public abstract DateTimeField clockhourOfHalfday();
    
    public abstract DateTimeField halfdayOfDay();
    
    public abstract DurationField days();
    
    public abstract DateTimeField dayOfWeek();
    
    public abstract DateTimeField dayOfMonth();
    
    public abstract DateTimeField dayOfYear();
    
    public abstract DurationField weeks();
    
    public abstract DateTimeField weekOfWeekyear();
    
    public abstract DurationField weekyears();
    
    public abstract DateTimeField weekyear();
    
    public abstract DateTimeField weekyearOfCentury();
    
    public abstract DurationField months();
    
    public abstract DateTimeField monthOfYear();
    
    public abstract DurationField years();
    
    public abstract DateTimeField year();
    
    public abstract DateTimeField yearOfEra();
    
    public abstract DateTimeField yearOfCentury();
    
    public abstract DurationField centuries();
    
    public abstract DateTimeField centuryOfEra();
    
    public abstract DurationField eras();
    
    public abstract DateTimeField era();
    
    @Override
    public abstract String toString();
}
