// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import org.joda.time.chrono.ISOChronology;
import java.io.Serializable;
import org.joda.time.base.BaseInterval;

public final class Interval extends BaseInterval implements ReadableInterval, Serializable
{
    private static final long serialVersionUID = 4922451897541386752L;
    
    public static Interval parse(final String s) {
        return new Interval(s);
    }
    
    public Interval(final long n, final long n2) {
        super(n, n2, null);
    }
    
    public Interval(final long n, final long n2, final DateTimeZone dateTimeZone) {
        super(n, n2, ISOChronology.getInstance(dateTimeZone));
    }
    
    public Interval(final long n, final long n2, final Chronology chronology) {
        super(n, n2, chronology);
    }
    
    public Interval(final ReadableInstant readableInstant, final ReadableInstant readableInstant2) {
        super(readableInstant, readableInstant2);
    }
    
    public Interval(final ReadableInstant readableInstant, final ReadableDuration readableDuration) {
        super(readableInstant, readableDuration);
    }
    
    public Interval(final ReadableDuration readableDuration, final ReadableInstant readableInstant) {
        super(readableDuration, readableInstant);
    }
    
    public Interval(final ReadableInstant readableInstant, final ReadablePeriod readablePeriod) {
        super(readableInstant, readablePeriod);
    }
    
    public Interval(final ReadablePeriod readablePeriod, final ReadableInstant readableInstant) {
        super(readablePeriod, readableInstant);
    }
    
    public Interval(final Object o) {
        super(o, null);
    }
    
    public Interval(final Object o, final Chronology chronology) {
        super(o, chronology);
    }
    
    @Override
    public Interval toInterval() {
        return this;
    }
    
    public Interval overlap(ReadableInterval readableInterval) {
        readableInterval = DateTimeUtils.getReadableInterval(readableInterval);
        if (!this.overlaps(readableInterval)) {
            return null;
        }
        return new Interval(Math.max(this.getStartMillis(), readableInterval.getStartMillis()), Math.min(this.getEndMillis(), readableInterval.getEndMillis()), this.getChronology());
    }
    
    public Interval gap(ReadableInterval readableInterval) {
        readableInterval = DateTimeUtils.getReadableInterval(readableInterval);
        final long startMillis = readableInterval.getStartMillis();
        final long endMillis = readableInterval.getEndMillis();
        final long startMillis2 = this.getStartMillis();
        final long endMillis2 = this.getEndMillis();
        if (startMillis2 > endMillis) {
            return new Interval(endMillis, startMillis2, this.getChronology());
        }
        if (startMillis > endMillis2) {
            return new Interval(endMillis2, startMillis, this.getChronology());
        }
        return null;
    }
    
    public boolean abuts(final ReadableInterval readableInterval) {
        if (readableInterval == null) {
            final long currentTimeMillis = DateTimeUtils.currentTimeMillis();
            return this.getStartMillis() == currentTimeMillis || this.getEndMillis() == currentTimeMillis;
        }
        return readableInterval.getEndMillis() == this.getStartMillis() || this.getEndMillis() == readableInterval.getStartMillis();
    }
    
    public Interval withChronology(final Chronology chronology) {
        if (this.getChronology() == chronology) {
            return this;
        }
        return new Interval(this.getStartMillis(), this.getEndMillis(), chronology);
    }
    
    public Interval withStartMillis(final long n) {
        if (n == this.getStartMillis()) {
            return this;
        }
        return new Interval(n, this.getEndMillis(), this.getChronology());
    }
    
    public Interval withStart(final ReadableInstant readableInstant) {
        return this.withStartMillis(DateTimeUtils.getInstantMillis(readableInstant));
    }
    
    public Interval withEndMillis(final long n) {
        if (n == this.getEndMillis()) {
            return this;
        }
        return new Interval(this.getStartMillis(), n, this.getChronology());
    }
    
    public Interval withEnd(final ReadableInstant readableInstant) {
        return this.withEndMillis(DateTimeUtils.getInstantMillis(readableInstant));
    }
    
    public Interval withDurationAfterStart(final ReadableDuration readableDuration) {
        final long durationMillis = DateTimeUtils.getDurationMillis(readableDuration);
        if (durationMillis == this.toDurationMillis()) {
            return this;
        }
        final Chronology chronology = this.getChronology();
        final long startMillis = this.getStartMillis();
        return new Interval(startMillis, chronology.add(startMillis, durationMillis, 1), chronology);
    }
    
    public Interval withDurationBeforeEnd(final ReadableDuration readableDuration) {
        final long durationMillis = DateTimeUtils.getDurationMillis(readableDuration);
        if (durationMillis == this.toDurationMillis()) {
            return this;
        }
        final Chronology chronology = this.getChronology();
        final long endMillis = this.getEndMillis();
        return new Interval(chronology.add(endMillis, durationMillis, -1), endMillis, chronology);
    }
    
    public Interval withPeriodAfterStart(final ReadablePeriod readablePeriod) {
        if (readablePeriod == null) {
            return this.withDurationAfterStart(null);
        }
        final Chronology chronology = this.getChronology();
        final long startMillis = this.getStartMillis();
        return new Interval(startMillis, chronology.add(readablePeriod, startMillis, 1), chronology);
    }
    
    public Interval withPeriodBeforeEnd(final ReadablePeriod readablePeriod) {
        if (readablePeriod == null) {
            return this.withDurationBeforeEnd(null);
        }
        final Chronology chronology = this.getChronology();
        final long endMillis = this.getEndMillis();
        return new Interval(chronology.add(readablePeriod, endMillis, -1), endMillis, chronology);
    }
}
