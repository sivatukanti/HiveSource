// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.base;

import org.joda.time.convert.IntervalConverter;
import org.joda.time.MutableInterval;
import org.joda.time.ReadWritableInterval;
import org.joda.time.convert.ConverterManager;
import org.joda.time.ReadablePeriod;
import org.joda.time.field.FieldUtils;
import org.joda.time.ReadableDuration;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.ReadableInstant;
import org.joda.time.DateTimeUtils;
import org.joda.time.Chronology;
import java.io.Serializable;
import org.joda.time.ReadableInterval;

public abstract class BaseInterval extends AbstractInterval implements ReadableInterval, Serializable
{
    private static final long serialVersionUID = 576586928732749278L;
    private volatile Chronology iChronology;
    private volatile long iStartMillis;
    private volatile long iEndMillis;
    
    protected BaseInterval(final long iStartMillis, final long iEndMillis, final Chronology chronology) {
        this.iChronology = DateTimeUtils.getChronology(chronology);
        this.checkInterval(iStartMillis, iEndMillis);
        this.iStartMillis = iStartMillis;
        this.iEndMillis = iEndMillis;
    }
    
    protected BaseInterval(final ReadableInstant readableInstant, final ReadableInstant readableInstant2) {
        if (readableInstant == null && readableInstant2 == null) {
            final long currentTimeMillis = DateTimeUtils.currentTimeMillis();
            this.iEndMillis = currentTimeMillis;
            this.iStartMillis = currentTimeMillis;
            this.iChronology = ISOChronology.getInstance();
        }
        else {
            this.iChronology = DateTimeUtils.getInstantChronology(readableInstant);
            this.iStartMillis = DateTimeUtils.getInstantMillis(readableInstant);
            this.iEndMillis = DateTimeUtils.getInstantMillis(readableInstant2);
            this.checkInterval(this.iStartMillis, this.iEndMillis);
        }
    }
    
    protected BaseInterval(final ReadableInstant readableInstant, final ReadableDuration readableDuration) {
        this.iChronology = DateTimeUtils.getInstantChronology(readableInstant);
        this.iStartMillis = DateTimeUtils.getInstantMillis(readableInstant);
        this.iEndMillis = FieldUtils.safeAdd(this.iStartMillis, DateTimeUtils.getDurationMillis(readableDuration));
        this.checkInterval(this.iStartMillis, this.iEndMillis);
    }
    
    protected BaseInterval(final ReadableDuration readableDuration, final ReadableInstant readableInstant) {
        this.iChronology = DateTimeUtils.getInstantChronology(readableInstant);
        this.iEndMillis = DateTimeUtils.getInstantMillis(readableInstant);
        this.checkInterval(this.iStartMillis = FieldUtils.safeAdd(this.iEndMillis, -DateTimeUtils.getDurationMillis(readableDuration)), this.iEndMillis);
    }
    
    protected BaseInterval(final ReadableInstant readableInstant, final ReadablePeriod readablePeriod) {
        final Chronology instantChronology = DateTimeUtils.getInstantChronology(readableInstant);
        this.iChronology = instantChronology;
        this.iStartMillis = DateTimeUtils.getInstantMillis(readableInstant);
        if (readablePeriod == null) {
            this.iEndMillis = this.iStartMillis;
        }
        else {
            this.iEndMillis = instantChronology.add(readablePeriod, this.iStartMillis, 1);
        }
        this.checkInterval(this.iStartMillis, this.iEndMillis);
    }
    
    protected BaseInterval(final ReadablePeriod readablePeriod, final ReadableInstant readableInstant) {
        final Chronology instantChronology = DateTimeUtils.getInstantChronology(readableInstant);
        this.iChronology = instantChronology;
        this.iEndMillis = DateTimeUtils.getInstantMillis(readableInstant);
        if (readablePeriod == null) {
            this.iStartMillis = this.iEndMillis;
        }
        else {
            this.iStartMillis = instantChronology.add(readablePeriod, this.iEndMillis, -1);
        }
        this.checkInterval(this.iStartMillis, this.iEndMillis);
    }
    
    protected BaseInterval(final Object o, final Chronology chronology) {
        final IntervalConverter intervalConverter = ConverterManager.getInstance().getIntervalConverter(o);
        if (intervalConverter.isReadableInterval(o, chronology)) {
            final ReadableInterval readableInterval = (ReadableInterval)o;
            this.iChronology = ((chronology != null) ? chronology : readableInterval.getChronology());
            this.iStartMillis = readableInterval.getStartMillis();
            this.iEndMillis = readableInterval.getEndMillis();
        }
        else if (this instanceof ReadWritableInterval) {
            intervalConverter.setInto((ReadWritableInterval)this, o, chronology);
        }
        else {
            final MutableInterval mutableInterval = new MutableInterval();
            intervalConverter.setInto(mutableInterval, o, chronology);
            this.iChronology = mutableInterval.getChronology();
            this.iStartMillis = mutableInterval.getStartMillis();
            this.iEndMillis = mutableInterval.getEndMillis();
        }
        this.checkInterval(this.iStartMillis, this.iEndMillis);
    }
    
    public Chronology getChronology() {
        return this.iChronology;
    }
    
    public long getStartMillis() {
        return this.iStartMillis;
    }
    
    public long getEndMillis() {
        return this.iEndMillis;
    }
    
    protected void setInterval(final long iStartMillis, final long iEndMillis, final Chronology chronology) {
        this.checkInterval(iStartMillis, iEndMillis);
        this.iStartMillis = iStartMillis;
        this.iEndMillis = iEndMillis;
        this.iChronology = DateTimeUtils.getChronology(chronology);
    }
}
