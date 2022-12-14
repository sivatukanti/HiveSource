// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import org.joda.time.chrono.ISOChronology;
import org.joda.time.convert.ConverterManager;
import org.joda.time.format.DateTimeFormatter;
import org.joda.convert.FromString;
import org.joda.time.format.ISODateTimeFormat;
import java.io.Serializable;
import org.joda.time.base.AbstractInstant;

public final class Instant extends AbstractInstant implements ReadableInstant, Serializable
{
    private static final long serialVersionUID = 3299096530934209741L;
    private final long iMillis;
    
    public static Instant now() {
        return new Instant();
    }
    
    @FromString
    public static Instant parse(final String s) {
        return parse(s, ISODateTimeFormat.dateTimeParser());
    }
    
    public static Instant parse(final String s, final DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.parseDateTime(s).toInstant();
    }
    
    public Instant() {
        this.iMillis = DateTimeUtils.currentTimeMillis();
    }
    
    public Instant(final long iMillis) {
        this.iMillis = iMillis;
    }
    
    public Instant(final Object o) {
        this.iMillis = ConverterManager.getInstance().getInstantConverter(o).getInstantMillis(o, ISOChronology.getInstanceUTC());
    }
    
    @Override
    public Instant toInstant() {
        return this;
    }
    
    public Instant withMillis(final long n) {
        return (n == this.iMillis) ? this : new Instant(n);
    }
    
    public Instant withDurationAdded(final long n, final int n2) {
        if (n == 0L || n2 == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().add(this.getMillis(), n, n2));
    }
    
    public Instant withDurationAdded(final ReadableDuration readableDuration, final int n) {
        if (readableDuration == null || n == 0) {
            return this;
        }
        return this.withDurationAdded(readableDuration.getMillis(), n);
    }
    
    public Instant plus(final long n) {
        return this.withDurationAdded(n, 1);
    }
    
    public Instant plus(final ReadableDuration readableDuration) {
        return this.withDurationAdded(readableDuration, 1);
    }
    
    public Instant minus(final long n) {
        return this.withDurationAdded(n, -1);
    }
    
    public Instant minus(final ReadableDuration readableDuration) {
        return this.withDurationAdded(readableDuration, -1);
    }
    
    public long getMillis() {
        return this.iMillis;
    }
    
    public Chronology getChronology() {
        return ISOChronology.getInstanceUTC();
    }
    
    @Override
    public DateTime toDateTime() {
        return new DateTime(this.getMillis(), ISOChronology.getInstance());
    }
    
    @Deprecated
    @Override
    public DateTime toDateTimeISO() {
        return this.toDateTime();
    }
    
    @Override
    public MutableDateTime toMutableDateTime() {
        return new MutableDateTime(this.getMillis(), ISOChronology.getInstance());
    }
    
    @Deprecated
    @Override
    public MutableDateTime toMutableDateTimeISO() {
        return this.toMutableDateTime();
    }
}
