// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.base;

import org.joda.time.convert.InstantConverter;
import org.joda.time.convert.ConverterManager;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.Chronology;
import java.io.Serializable;
import org.joda.time.ReadableDateTime;

public abstract class BaseDateTime extends AbstractDateTime implements ReadableDateTime, Serializable
{
    private static final long serialVersionUID = -6728882245981L;
    private volatile long iMillis;
    private volatile Chronology iChronology;
    
    public BaseDateTime() {
        this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance());
    }
    
    public BaseDateTime(final DateTimeZone dateTimeZone) {
        this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance(dateTimeZone));
    }
    
    public BaseDateTime(final Chronology chronology) {
        this(DateTimeUtils.currentTimeMillis(), chronology);
    }
    
    public BaseDateTime(final long n) {
        this(n, ISOChronology.getInstance());
    }
    
    public BaseDateTime(final long n, final DateTimeZone dateTimeZone) {
        this(n, ISOChronology.getInstance(dateTimeZone));
    }
    
    public BaseDateTime(final long n, final Chronology chronology) {
        this.iChronology = this.checkChronology(chronology);
        this.iMillis = this.checkInstant(n, this.iChronology);
        if (this.iChronology.year().isSupported()) {
            this.iChronology.year().set(this.iMillis, this.iChronology.year().get(this.iMillis));
        }
    }
    
    public BaseDateTime(final Object o, final DateTimeZone dateTimeZone) {
        final InstantConverter instantConverter = ConverterManager.getInstance().getInstantConverter(o);
        final Chronology checkChronology = this.checkChronology(instantConverter.getChronology(o, dateTimeZone));
        this.iChronology = checkChronology;
        this.iMillis = this.checkInstant(instantConverter.getInstantMillis(o, checkChronology), checkChronology);
    }
    
    public BaseDateTime(final Object o, final Chronology chronology) {
        final InstantConverter instantConverter = ConverterManager.getInstance().getInstantConverter(o);
        this.iChronology = this.checkChronology(instantConverter.getChronology(o, chronology));
        this.iMillis = this.checkInstant(instantConverter.getInstantMillis(o, chronology), this.iChronology);
    }
    
    public BaseDateTime(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        this(n, n2, n3, n4, n5, n6, n7, ISOChronology.getInstance());
    }
    
    public BaseDateTime(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final DateTimeZone dateTimeZone) {
        this(n, n2, n3, n4, n5, n6, n7, ISOChronology.getInstance(dateTimeZone));
    }
    
    public BaseDateTime(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final Chronology chronology) {
        this.iChronology = this.checkChronology(chronology);
        this.iMillis = this.checkInstant(this.iChronology.getDateTimeMillis(n, n2, n3, n4, n5, n6, n7), this.iChronology);
    }
    
    protected Chronology checkChronology(final Chronology chronology) {
        return DateTimeUtils.getChronology(chronology);
    }
    
    protected long checkInstant(final long n, final Chronology chronology) {
        return n;
    }
    
    public long getMillis() {
        return this.iMillis;
    }
    
    public Chronology getChronology() {
        return this.iChronology;
    }
    
    protected void setMillis(final long n) {
        this.iMillis = this.checkInstant(n, this.iChronology);
    }
    
    protected void setChronology(final Chronology chronology) {
        this.iChronology = this.checkChronology(chronology);
    }
}
