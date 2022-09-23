// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.base;

import org.joda.time.format.DateTimeFormatter;
import org.joda.convert.ToString;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.field.FieldUtils;
import java.util.Date;
import org.joda.time.MutableDateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Chronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;

public abstract class AbstractInstant implements ReadableInstant
{
    protected AbstractInstant() {
    }
    
    public DateTimeZone getZone() {
        return this.getChronology().getZone();
    }
    
    public int get(final DateTimeFieldType dateTimeFieldType) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("The DateTimeFieldType must not be null");
        }
        return dateTimeFieldType.getField(this.getChronology()).get(this.getMillis());
    }
    
    public boolean isSupported(final DateTimeFieldType dateTimeFieldType) {
        return dateTimeFieldType != null && dateTimeFieldType.getField(this.getChronology()).isSupported();
    }
    
    public int get(final DateTimeField dateTimeField) {
        if (dateTimeField == null) {
            throw new IllegalArgumentException("The DateTimeField must not be null");
        }
        return dateTimeField.get(this.getMillis());
    }
    
    public Instant toInstant() {
        return new Instant(this.getMillis());
    }
    
    public DateTime toDateTime() {
        return new DateTime(this.getMillis(), this.getZone());
    }
    
    public DateTime toDateTimeISO() {
        return new DateTime(this.getMillis(), ISOChronology.getInstance(this.getZone()));
    }
    
    public DateTime toDateTime(final DateTimeZone dateTimeZone) {
        return new DateTime(this.getMillis(), DateTimeUtils.getChronology(this.getChronology()).withZone(dateTimeZone));
    }
    
    public DateTime toDateTime(final Chronology chronology) {
        return new DateTime(this.getMillis(), chronology);
    }
    
    public MutableDateTime toMutableDateTime() {
        return new MutableDateTime(this.getMillis(), this.getZone());
    }
    
    public MutableDateTime toMutableDateTimeISO() {
        return new MutableDateTime(this.getMillis(), ISOChronology.getInstance(this.getZone()));
    }
    
    public MutableDateTime toMutableDateTime(final DateTimeZone dateTimeZone) {
        return new MutableDateTime(this.getMillis(), DateTimeUtils.getChronology(this.getChronology()).withZone(dateTimeZone));
    }
    
    public MutableDateTime toMutableDateTime(final Chronology chronology) {
        return new MutableDateTime(this.getMillis(), chronology);
    }
    
    public Date toDate() {
        return new Date(this.getMillis());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReadableInstant)) {
            return false;
        }
        final ReadableInstant readableInstant = (ReadableInstant)o;
        return this.getMillis() == readableInstant.getMillis() && FieldUtils.equals(this.getChronology(), readableInstant.getChronology());
    }
    
    @Override
    public int hashCode() {
        return (int)(this.getMillis() ^ this.getMillis() >>> 32) + this.getChronology().hashCode();
    }
    
    public int compareTo(final ReadableInstant readableInstant) {
        if (this == readableInstant) {
            return 0;
        }
        final long millis = readableInstant.getMillis();
        final long millis2 = this.getMillis();
        if (millis2 == millis) {
            return 0;
        }
        if (millis2 < millis) {
            return -1;
        }
        return 1;
    }
    
    public boolean isAfter(final long n) {
        return this.getMillis() > n;
    }
    
    public boolean isAfterNow() {
        return this.isAfter(DateTimeUtils.currentTimeMillis());
    }
    
    public boolean isAfter(final ReadableInstant readableInstant) {
        return this.isAfter(DateTimeUtils.getInstantMillis(readableInstant));
    }
    
    public boolean isBefore(final long n) {
        return this.getMillis() < n;
    }
    
    public boolean isBeforeNow() {
        return this.isBefore(DateTimeUtils.currentTimeMillis());
    }
    
    public boolean isBefore(final ReadableInstant readableInstant) {
        return this.isBefore(DateTimeUtils.getInstantMillis(readableInstant));
    }
    
    public boolean isEqual(final long n) {
        return this.getMillis() == n;
    }
    
    public boolean isEqualNow() {
        return this.isEqual(DateTimeUtils.currentTimeMillis());
    }
    
    public boolean isEqual(final ReadableInstant readableInstant) {
        return this.isEqual(DateTimeUtils.getInstantMillis(readableInstant));
    }
    
    @ToString
    @Override
    public String toString() {
        return ISODateTimeFormat.dateTime().print(this);
    }
    
    public String toString(final DateTimeFormatter dateTimeFormatter) {
        if (dateTimeFormatter == null) {
            return this.toString();
        }
        return dateTimeFormatter.print(this);
    }
}
