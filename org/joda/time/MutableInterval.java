// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import org.joda.time.field.FieldUtils;
import java.io.Serializable;
import org.joda.time.base.BaseInterval;

public class MutableInterval extends BaseInterval implements ReadWritableInterval, Cloneable, Serializable
{
    private static final long serialVersionUID = -5982824024992428470L;
    
    public static MutableInterval parse(final String s) {
        return new MutableInterval(s);
    }
    
    public MutableInterval() {
        super(0L, 0L, null);
    }
    
    public MutableInterval(final long n, final long n2) {
        super(n, n2, null);
    }
    
    public MutableInterval(final long n, final long n2, final Chronology chronology) {
        super(n, n2, chronology);
    }
    
    public MutableInterval(final ReadableInstant readableInstant, final ReadableInstant readableInstant2) {
        super(readableInstant, readableInstant2);
    }
    
    public MutableInterval(final ReadableInstant readableInstant, final ReadableDuration readableDuration) {
        super(readableInstant, readableDuration);
    }
    
    public MutableInterval(final ReadableDuration readableDuration, final ReadableInstant readableInstant) {
        super(readableDuration, readableInstant);
    }
    
    public MutableInterval(final ReadableInstant readableInstant, final ReadablePeriod readablePeriod) {
        super(readableInstant, readablePeriod);
    }
    
    public MutableInterval(final ReadablePeriod readablePeriod, final ReadableInstant readableInstant) {
        super(readablePeriod, readableInstant);
    }
    
    public MutableInterval(final Object o) {
        super(o, null);
    }
    
    public MutableInterval(final Object o, final Chronology chronology) {
        super(o, chronology);
    }
    
    public void setInterval(final long n, final long n2) {
        super.setInterval(n, n2, this.getChronology());
    }
    
    public void setInterval(final ReadableInterval readableInterval) {
        if (readableInterval == null) {
            throw new IllegalArgumentException("Interval must not be null");
        }
        super.setInterval(readableInterval.getStartMillis(), readableInterval.getEndMillis(), readableInterval.getChronology());
    }
    
    public void setInterval(final ReadableInstant readableInstant, final ReadableInstant readableInstant2) {
        if (readableInstant == null && readableInstant2 == null) {
            final long currentTimeMillis = DateTimeUtils.currentTimeMillis();
            this.setInterval(currentTimeMillis, currentTimeMillis);
        }
        else {
            super.setInterval(DateTimeUtils.getInstantMillis(readableInstant), DateTimeUtils.getInstantMillis(readableInstant2), DateTimeUtils.getInstantChronology(readableInstant));
        }
    }
    
    public void setChronology(final Chronology chronology) {
        super.setInterval(this.getStartMillis(), this.getEndMillis(), chronology);
    }
    
    public void setStartMillis(final long n) {
        super.setInterval(n, this.getEndMillis(), this.getChronology());
    }
    
    public void setStart(final ReadableInstant readableInstant) {
        super.setInterval(DateTimeUtils.getInstantMillis(readableInstant), this.getEndMillis(), this.getChronology());
    }
    
    public void setEndMillis(final long n) {
        super.setInterval(this.getStartMillis(), n, this.getChronology());
    }
    
    public void setEnd(final ReadableInstant readableInstant) {
        super.setInterval(this.getStartMillis(), DateTimeUtils.getInstantMillis(readableInstant), this.getChronology());
    }
    
    public void setDurationAfterStart(final long n) {
        this.setEndMillis(FieldUtils.safeAdd(this.getStartMillis(), n));
    }
    
    public void setDurationBeforeEnd(final long n) {
        this.setStartMillis(FieldUtils.safeAdd(this.getEndMillis(), -n));
    }
    
    public void setDurationAfterStart(final ReadableDuration readableDuration) {
        this.setEndMillis(FieldUtils.safeAdd(this.getStartMillis(), DateTimeUtils.getDurationMillis(readableDuration)));
    }
    
    public void setDurationBeforeEnd(final ReadableDuration readableDuration) {
        this.setStartMillis(FieldUtils.safeAdd(this.getEndMillis(), -DateTimeUtils.getDurationMillis(readableDuration)));
    }
    
    public void setPeriodAfterStart(final ReadablePeriod readablePeriod) {
        if (readablePeriod == null) {
            this.setEndMillis(this.getStartMillis());
        }
        else {
            this.setEndMillis(this.getChronology().add(readablePeriod, this.getStartMillis(), 1));
        }
    }
    
    public void setPeriodBeforeEnd(final ReadablePeriod readablePeriod) {
        if (readablePeriod == null) {
            this.setStartMillis(this.getEndMillis());
        }
        else {
            this.setStartMillis(this.getChronology().add(readablePeriod, this.getEndMillis(), -1));
        }
    }
    
    public MutableInterval copy() {
        return (MutableInterval)this.clone();
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new InternalError("Clone error");
        }
    }
}
