// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.convert;

import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.JulianChronology;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;
import java.util.GregorianCalendar;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.DateTimeZone;
import java.util.Calendar;
import org.joda.time.Chronology;

final class CalendarConverter extends AbstractConverter implements InstantConverter, PartialConverter
{
    static final CalendarConverter INSTANCE;
    
    protected CalendarConverter() {
    }
    
    @Override
    public Chronology getChronology(final Object o, final Chronology chronology) {
        if (chronology != null) {
            return chronology;
        }
        final Calendar calendar = (Calendar)o;
        DateTimeZone dateTimeZone;
        try {
            dateTimeZone = DateTimeZone.forTimeZone(calendar.getTimeZone());
        }
        catch (IllegalArgumentException ex) {
            dateTimeZone = DateTimeZone.getDefault();
        }
        return this.getChronology(calendar, dateTimeZone);
    }
    
    @Override
    public Chronology getChronology(final Object o, final DateTimeZone dateTimeZone) {
        if (o.getClass().getName().endsWith(".BuddhistCalendar")) {
            return BuddhistChronology.getInstance(dateTimeZone);
        }
        if (!(o instanceof GregorianCalendar)) {
            return ISOChronology.getInstance(dateTimeZone);
        }
        final long time = ((GregorianCalendar)o).getGregorianChange().getTime();
        if (time == Long.MIN_VALUE) {
            return GregorianChronology.getInstance(dateTimeZone);
        }
        if (time == Long.MAX_VALUE) {
            return JulianChronology.getInstance(dateTimeZone);
        }
        return GJChronology.getInstance(dateTimeZone, time, 4);
    }
    
    @Override
    public long getInstantMillis(final Object o, final Chronology chronology) {
        return ((Calendar)o).getTime().getTime();
    }
    
    public Class<?> getSupportedType() {
        return Calendar.class;
    }
    
    static {
        INSTANCE = new CalendarConverter();
    }
}
