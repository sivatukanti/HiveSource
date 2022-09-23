// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.base;

import org.joda.time.Interval;
import org.joda.time.Chronology;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.convert.ConverterManager;
import org.joda.time.DateTimeUtils;
import org.joda.time.ReadableInstant;
import org.joda.time.field.FieldUtils;
import java.io.Serializable;
import org.joda.time.ReadableDuration;

public abstract class BaseDuration extends AbstractDuration implements ReadableDuration, Serializable
{
    private static final long serialVersionUID = 2581698638990L;
    private volatile long iMillis;
    
    protected BaseDuration(final long iMillis) {
        this.iMillis = iMillis;
    }
    
    protected BaseDuration(final long n, final long n2) {
        this.iMillis = FieldUtils.safeSubtract(n2, n);
    }
    
    protected BaseDuration(final ReadableInstant readableInstant, final ReadableInstant readableInstant2) {
        if (readableInstant == readableInstant2) {
            this.iMillis = 0L;
        }
        else {
            this.iMillis = FieldUtils.safeSubtract(DateTimeUtils.getInstantMillis(readableInstant2), DateTimeUtils.getInstantMillis(readableInstant));
        }
    }
    
    protected BaseDuration(final Object o) {
        this.iMillis = ConverterManager.getInstance().getDurationConverter(o).getDurationMillis(o);
    }
    
    public long getMillis() {
        return this.iMillis;
    }
    
    protected void setMillis(final long iMillis) {
        this.iMillis = iMillis;
    }
    
    public Period toPeriod(final PeriodType periodType) {
        return new Period(this.getMillis(), periodType);
    }
    
    public Period toPeriod(final Chronology chronology) {
        return new Period(this.getMillis(), chronology);
    }
    
    public Period toPeriod(final PeriodType periodType, final Chronology chronology) {
        return new Period(this.getMillis(), periodType, chronology);
    }
    
    public Period toPeriodFrom(final ReadableInstant readableInstant) {
        return new Period(readableInstant, this);
    }
    
    public Period toPeriodFrom(final ReadableInstant readableInstant, final PeriodType periodType) {
        return new Period(readableInstant, this, periodType);
    }
    
    public Period toPeriodTo(final ReadableInstant readableInstant) {
        return new Period(this, readableInstant);
    }
    
    public Period toPeriodTo(final ReadableInstant readableInstant, final PeriodType periodType) {
        return new Period(this, readableInstant, periodType);
    }
    
    public Interval toIntervalFrom(final ReadableInstant readableInstant) {
        return new Interval(readableInstant, this);
    }
    
    public Interval toIntervalTo(final ReadableInstant readableInstant) {
        return new Interval(this, readableInstant);
    }
}
