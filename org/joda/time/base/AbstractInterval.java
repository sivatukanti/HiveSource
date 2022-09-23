// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.base;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.PeriodType;
import org.joda.time.Period;
import org.joda.time.Duration;
import org.joda.time.field.FieldUtils;
import org.joda.time.MutableInterval;
import org.joda.time.Interval;
import org.joda.time.ReadableInstant;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTime;
import org.joda.time.ReadableInterval;

public abstract class AbstractInterval implements ReadableInterval
{
    protected AbstractInterval() {
    }
    
    protected void checkInterval(final long n, final long n2) {
        if (n2 < n) {
            throw new IllegalArgumentException("The end instant must be greater or equal to the start");
        }
    }
    
    public DateTime getStart() {
        return new DateTime(this.getStartMillis(), this.getChronology());
    }
    
    public DateTime getEnd() {
        return new DateTime(this.getEndMillis(), this.getChronology());
    }
    
    public boolean contains(final long n) {
        final long startMillis = this.getStartMillis();
        final long endMillis = this.getEndMillis();
        return n >= startMillis && n < endMillis;
    }
    
    public boolean containsNow() {
        return this.contains(DateTimeUtils.currentTimeMillis());
    }
    
    public boolean contains(final ReadableInstant readableInstant) {
        if (readableInstant == null) {
            return this.containsNow();
        }
        return this.contains(readableInstant.getMillis());
    }
    
    public boolean contains(final ReadableInterval readableInterval) {
        if (readableInterval == null) {
            return this.containsNow();
        }
        final long startMillis = readableInterval.getStartMillis();
        final long endMillis = readableInterval.getEndMillis();
        final long startMillis2 = this.getStartMillis();
        final long endMillis2 = this.getEndMillis();
        return startMillis2 <= startMillis && startMillis < endMillis2 && endMillis <= endMillis2;
    }
    
    public boolean overlaps(final ReadableInterval readableInterval) {
        final long startMillis = this.getStartMillis();
        final long endMillis = this.getEndMillis();
        if (readableInterval == null) {
            final long currentTimeMillis = DateTimeUtils.currentTimeMillis();
            return startMillis < currentTimeMillis && currentTimeMillis < endMillis;
        }
        final long startMillis2 = readableInterval.getStartMillis();
        return startMillis < readableInterval.getEndMillis() && startMillis2 < endMillis;
    }
    
    public boolean isEqual(final ReadableInterval readableInterval) {
        return this.getStartMillis() == readableInterval.getStartMillis() && this.getEndMillis() == readableInterval.getEndMillis();
    }
    
    public boolean isBefore(final long n) {
        return this.getEndMillis() <= n;
    }
    
    public boolean isBeforeNow() {
        return this.isBefore(DateTimeUtils.currentTimeMillis());
    }
    
    public boolean isBefore(final ReadableInstant readableInstant) {
        if (readableInstant == null) {
            return this.isBeforeNow();
        }
        return this.isBefore(readableInstant.getMillis());
    }
    
    public boolean isBefore(final ReadableInterval readableInterval) {
        if (readableInterval == null) {
            return this.isBeforeNow();
        }
        return this.isBefore(readableInterval.getStartMillis());
    }
    
    public boolean isAfter(final long n) {
        return this.getStartMillis() > n;
    }
    
    public boolean isAfterNow() {
        return this.isAfter(DateTimeUtils.currentTimeMillis());
    }
    
    public boolean isAfter(final ReadableInstant readableInstant) {
        if (readableInstant == null) {
            return this.isAfterNow();
        }
        return this.isAfter(readableInstant.getMillis());
    }
    
    public boolean isAfter(final ReadableInterval readableInterval) {
        long n;
        if (readableInterval == null) {
            n = DateTimeUtils.currentTimeMillis();
        }
        else {
            n = readableInterval.getEndMillis();
        }
        return this.getStartMillis() >= n;
    }
    
    public Interval toInterval() {
        return new Interval(this.getStartMillis(), this.getEndMillis(), this.getChronology());
    }
    
    public MutableInterval toMutableInterval() {
        return new MutableInterval(this.getStartMillis(), this.getEndMillis(), this.getChronology());
    }
    
    public long toDurationMillis() {
        return FieldUtils.safeAdd(this.getEndMillis(), -this.getStartMillis());
    }
    
    public Duration toDuration() {
        final long durationMillis = this.toDurationMillis();
        if (durationMillis == 0L) {
            return Duration.ZERO;
        }
        return new Duration(durationMillis);
    }
    
    public Period toPeriod() {
        return new Period(this.getStartMillis(), this.getEndMillis(), this.getChronology());
    }
    
    public Period toPeriod(final PeriodType periodType) {
        return new Period(this.getStartMillis(), this.getEndMillis(), periodType, this.getChronology());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReadableInterval)) {
            return false;
        }
        final ReadableInterval readableInterval = (ReadableInterval)o;
        return this.getStartMillis() == readableInterval.getStartMillis() && this.getEndMillis() == readableInterval.getEndMillis() && FieldUtils.equals(this.getChronology(), readableInterval.getChronology());
    }
    
    @Override
    public int hashCode() {
        final long startMillis = this.getStartMillis();
        final long endMillis = this.getEndMillis();
        return 31 * (31 * (31 * 97 + (int)(startMillis ^ startMillis >>> 32)) + (int)(endMillis ^ endMillis >>> 32)) + this.getChronology().hashCode();
    }
    
    @Override
    public String toString() {
        final DateTimeFormatter withChronology = ISODateTimeFormat.dateTime().withChronology(this.getChronology());
        final StringBuffer sb = new StringBuffer(48);
        withChronology.printTo(sb, this.getStartMillis());
        sb.append('/');
        withChronology.printTo(sb, this.getEndMillis());
        return sb.toString();
    }
}
