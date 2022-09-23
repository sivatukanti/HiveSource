// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.base;

import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.convert.ToString;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Locale;
import org.joda.time.DateTimeFieldType;
import org.joda.time.ReadableDateTime;

public abstract class AbstractDateTime extends AbstractInstant implements ReadableDateTime
{
    protected AbstractDateTime() {
    }
    
    @Override
    public int get(final DateTimeFieldType dateTimeFieldType) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("The DateTimeFieldType must not be null");
        }
        return dateTimeFieldType.getField(this.getChronology()).get(this.getMillis());
    }
    
    public int getEra() {
        return this.getChronology().era().get(this.getMillis());
    }
    
    public int getCenturyOfEra() {
        return this.getChronology().centuryOfEra().get(this.getMillis());
    }
    
    public int getYearOfEra() {
        return this.getChronology().yearOfEra().get(this.getMillis());
    }
    
    public int getYearOfCentury() {
        return this.getChronology().yearOfCentury().get(this.getMillis());
    }
    
    public int getYear() {
        return this.getChronology().year().get(this.getMillis());
    }
    
    public int getWeekyear() {
        return this.getChronology().weekyear().get(this.getMillis());
    }
    
    public int getMonthOfYear() {
        return this.getChronology().monthOfYear().get(this.getMillis());
    }
    
    public int getWeekOfWeekyear() {
        return this.getChronology().weekOfWeekyear().get(this.getMillis());
    }
    
    public int getDayOfYear() {
        return this.getChronology().dayOfYear().get(this.getMillis());
    }
    
    public int getDayOfMonth() {
        return this.getChronology().dayOfMonth().get(this.getMillis());
    }
    
    public int getDayOfWeek() {
        return this.getChronology().dayOfWeek().get(this.getMillis());
    }
    
    public int getHourOfDay() {
        return this.getChronology().hourOfDay().get(this.getMillis());
    }
    
    public int getMinuteOfDay() {
        return this.getChronology().minuteOfDay().get(this.getMillis());
    }
    
    public int getMinuteOfHour() {
        return this.getChronology().minuteOfHour().get(this.getMillis());
    }
    
    public int getSecondOfDay() {
        return this.getChronology().secondOfDay().get(this.getMillis());
    }
    
    public int getSecondOfMinute() {
        return this.getChronology().secondOfMinute().get(this.getMillis());
    }
    
    public int getMillisOfDay() {
        return this.getChronology().millisOfDay().get(this.getMillis());
    }
    
    public int getMillisOfSecond() {
        return this.getChronology().millisOfSecond().get(this.getMillis());
    }
    
    public Calendar toCalendar(Locale default1) {
        if (default1 == null) {
            default1 = Locale.getDefault();
        }
        final Calendar instance = Calendar.getInstance(this.getZone().toTimeZone(), default1);
        instance.setTime(this.toDate());
        return instance;
    }
    
    public GregorianCalendar toGregorianCalendar() {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar(this.getZone().toTimeZone());
        gregorianCalendar.setTime(this.toDate());
        return gregorianCalendar;
    }
    
    @ToString
    @Override
    public String toString() {
        return super.toString();
    }
    
    public String toString(final String s) {
        if (s == null) {
            return this.toString();
        }
        return DateTimeFormat.forPattern(s).print(this);
    }
    
    public String toString(final String s, final Locale locale) throws IllegalArgumentException {
        if (s == null) {
            return this.toString();
        }
        return DateTimeFormat.forPattern(s).withLocale(locale).print(this);
    }
}
