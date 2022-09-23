// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import java.util.Locale;

public abstract class DateTimeField
{
    public abstract DateTimeFieldType getType();
    
    public abstract String getName();
    
    public abstract boolean isSupported();
    
    public abstract boolean isLenient();
    
    public abstract int get(final long p0);
    
    public abstract String getAsText(final long p0, final Locale p1);
    
    public abstract String getAsText(final long p0);
    
    public abstract String getAsText(final ReadablePartial p0, final int p1, final Locale p2);
    
    public abstract String getAsText(final ReadablePartial p0, final Locale p1);
    
    public abstract String getAsText(final int p0, final Locale p1);
    
    public abstract String getAsShortText(final long p0, final Locale p1);
    
    public abstract String getAsShortText(final long p0);
    
    public abstract String getAsShortText(final ReadablePartial p0, final int p1, final Locale p2);
    
    public abstract String getAsShortText(final ReadablePartial p0, final Locale p1);
    
    public abstract String getAsShortText(final int p0, final Locale p1);
    
    public abstract long add(final long p0, final int p1);
    
    public abstract long add(final long p0, final long p1);
    
    public abstract int[] add(final ReadablePartial p0, final int p1, final int[] p2, final int p3);
    
    public abstract int[] addWrapPartial(final ReadablePartial p0, final int p1, final int[] p2, final int p3);
    
    public abstract long addWrapField(final long p0, final int p1);
    
    public abstract int[] addWrapField(final ReadablePartial p0, final int p1, final int[] p2, final int p3);
    
    public abstract int getDifference(final long p0, final long p1);
    
    public abstract long getDifferenceAsLong(final long p0, final long p1);
    
    public abstract long set(final long p0, final int p1);
    
    public abstract int[] set(final ReadablePartial p0, final int p1, final int[] p2, final int p3);
    
    public abstract long set(final long p0, final String p1, final Locale p2);
    
    public abstract long set(final long p0, final String p1);
    
    public abstract int[] set(final ReadablePartial p0, final int p1, final int[] p2, final String p3, final Locale p4);
    
    public abstract DurationField getDurationField();
    
    public abstract DurationField getRangeDurationField();
    
    public abstract boolean isLeap(final long p0);
    
    public abstract int getLeapAmount(final long p0);
    
    public abstract DurationField getLeapDurationField();
    
    public abstract int getMinimumValue();
    
    public abstract int getMinimumValue(final long p0);
    
    public abstract int getMinimumValue(final ReadablePartial p0);
    
    public abstract int getMinimumValue(final ReadablePartial p0, final int[] p1);
    
    public abstract int getMaximumValue();
    
    public abstract int getMaximumValue(final long p0);
    
    public abstract int getMaximumValue(final ReadablePartial p0);
    
    public abstract int getMaximumValue(final ReadablePartial p0, final int[] p1);
    
    public abstract int getMaximumTextLength(final Locale p0);
    
    public abstract int getMaximumShortTextLength(final Locale p0);
    
    public abstract long roundFloor(final long p0);
    
    public abstract long roundCeiling(final long p0);
    
    public abstract long roundHalfFloor(final long p0);
    
    public abstract long roundHalfCeiling(final long p0);
    
    public abstract long roundHalfEven(final long p0);
    
    public abstract long remainder(final long p0);
    
    @Override
    public abstract String toString();
}
