// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.convert;

import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.ReadablePartial;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.DateTimeZone;
import org.joda.time.DateTimeUtils;
import org.joda.time.Chronology;

public abstract class AbstractConverter implements Converter
{
    protected AbstractConverter() {
    }
    
    public long getInstantMillis(final Object o, final Chronology chronology) {
        return DateTimeUtils.currentTimeMillis();
    }
    
    public Chronology getChronology(final Object o, final DateTimeZone dateTimeZone) {
        return ISOChronology.getInstance(dateTimeZone);
    }
    
    public Chronology getChronology(final Object o, final Chronology chronology) {
        return DateTimeUtils.getChronology(chronology);
    }
    
    public int[] getPartialValues(final ReadablePartial readablePartial, final Object o, final Chronology chronology) {
        return chronology.get(readablePartial, this.getInstantMillis(o, chronology));
    }
    
    public int[] getPartialValues(final ReadablePartial readablePartial, final Object o, final Chronology chronology, final DateTimeFormatter dateTimeFormatter) {
        return this.getPartialValues(readablePartial, o, chronology);
    }
    
    public PeriodType getPeriodType(final Object o) {
        return PeriodType.standard();
    }
    
    public boolean isReadableInterval(final Object o, final Chronology chronology) {
        return false;
    }
    
    @Override
    public String toString() {
        return "Converter[" + ((this.getSupportedType() == null) ? "null" : this.getSupportedType().getName()) + "]";
    }
}
