// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import java.util.Locale;
import org.joda.time.field.AbstractPartialFieldProperty;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.chrono.ISOChronology;
import java.util.Date;
import java.util.Calendar;
import java.io.Serializable;
import org.joda.time.base.BasePartial;

@Deprecated
public final class TimeOfDay extends BasePartial implements ReadablePartial, Serializable
{
    private static final long serialVersionUID = 3633353405803318660L;
    private static final DateTimeFieldType[] FIELD_TYPES;
    public static final TimeOfDay MIDNIGHT;
    public static final int HOUR_OF_DAY = 0;
    public static final int MINUTE_OF_HOUR = 1;
    public static final int SECOND_OF_MINUTE = 2;
    public static final int MILLIS_OF_SECOND = 3;
    
    public static TimeOfDay fromCalendarFields(final Calendar calendar) {
        if (calendar == null) {
            throw new IllegalArgumentException("The calendar must not be null");
        }
        return new TimeOfDay(calendar.get(11), calendar.get(12), calendar.get(13), calendar.get(14));
    }
    
    public static TimeOfDay fromDateFields(final Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return new TimeOfDay(date.getHours(), date.getMinutes(), date.getSeconds(), ((int)(date.getTime() % 1000L) + 1000) % 1000);
    }
    
    public static TimeOfDay fromMillisOfDay(final long n) {
        return fromMillisOfDay(n, null);
    }
    
    public static TimeOfDay fromMillisOfDay(final long n, Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        chronology = chronology.withUTC();
        return new TimeOfDay(n, chronology);
    }
    
    public TimeOfDay() {
    }
    
    public TimeOfDay(final DateTimeZone dateTimeZone) {
        super(ISOChronology.getInstance(dateTimeZone));
    }
    
    public TimeOfDay(final Chronology chronology) {
        super(chronology);
    }
    
    public TimeOfDay(final long n) {
        super(n);
    }
    
    public TimeOfDay(final long n, final Chronology chronology) {
        super(n, chronology);
    }
    
    public TimeOfDay(final Object o) {
        super(o, null, ISODateTimeFormat.timeParser());
    }
    
    public TimeOfDay(final Object o, final Chronology chronology) {
        super(o, DateTimeUtils.getChronology(chronology), ISODateTimeFormat.timeParser());
    }
    
    public TimeOfDay(final int n, final int n2) {
        this(n, n2, 0, 0, null);
    }
    
    public TimeOfDay(final int n, final int n2, final Chronology chronology) {
        this(n, n2, 0, 0, chronology);
    }
    
    public TimeOfDay(final int n, final int n2, final int n3) {
        this(n, n2, n3, 0, null);
    }
    
    public TimeOfDay(final int n, final int n2, final int n3, final Chronology chronology) {
        this(n, n2, n3, 0, chronology);
    }
    
    public TimeOfDay(final int n, final int n2, final int n3, final int n4) {
        this(n, n2, n3, n4, null);
    }
    
    public TimeOfDay(final int n, final int n2, final int n3, final int n4, final Chronology chronology) {
        super(new int[] { n, n2, n3, n4 }, chronology);
    }
    
    TimeOfDay(final TimeOfDay timeOfDay, final int[] array) {
        super(timeOfDay, array);
    }
    
    TimeOfDay(final TimeOfDay timeOfDay, final Chronology chronology) {
        super(timeOfDay, chronology);
    }
    
    public int size() {
        return 4;
    }
    
    @Override
    protected DateTimeField getField(final int i, final Chronology chronology) {
        switch (i) {
            case 0: {
                return chronology.hourOfDay();
            }
            case 1: {
                return chronology.minuteOfHour();
            }
            case 2: {
                return chronology.secondOfMinute();
            }
            case 3: {
                return chronology.millisOfSecond();
            }
            default: {
                throw new IndexOutOfBoundsException("Invalid index: " + i);
            }
        }
    }
    
    @Override
    public DateTimeFieldType getFieldType(final int n) {
        return TimeOfDay.FIELD_TYPES[n];
    }
    
    @Override
    public DateTimeFieldType[] getFieldTypes() {
        return TimeOfDay.FIELD_TYPES.clone();
    }
    
    public TimeOfDay withChronologyRetainFields(Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        chronology = chronology.withUTC();
        if (chronology == this.getChronology()) {
            return this;
        }
        final TimeOfDay timeOfDay = new TimeOfDay(this, chronology);
        chronology.validate(timeOfDay, this.getValues());
        return timeOfDay;
    }
    
    public TimeOfDay withField(final DateTimeFieldType dateTimeFieldType, final int n) {
        final int indexOfSupported = this.indexOfSupported(dateTimeFieldType);
        if (n == this.getValue(indexOfSupported)) {
            return this;
        }
        return new TimeOfDay(this, this.getField(indexOfSupported).set(this, indexOfSupported, this.getValues(), n));
    }
    
    public TimeOfDay withFieldAdded(final DurationFieldType durationFieldType, final int n) {
        final int indexOfSupported = this.indexOfSupported(durationFieldType);
        if (n == 0) {
            return this;
        }
        return new TimeOfDay(this, this.getField(indexOfSupported).addWrapPartial(this, indexOfSupported, this.getValues(), n));
    }
    
    public TimeOfDay withPeriodAdded(final ReadablePeriod readablePeriod, final int n) {
        if (readablePeriod == null || n == 0) {
            return this;
        }
        int[] array = this.getValues();
        for (int i = 0; i < readablePeriod.size(); ++i) {
            final int index = this.indexOf(readablePeriod.getFieldType(i));
            if (index >= 0) {
                array = this.getField(index).addWrapPartial(this, index, array, FieldUtils.safeMultiply(readablePeriod.getValue(i), n));
            }
        }
        return new TimeOfDay(this, array);
    }
    
    public TimeOfDay plus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }
    
    public TimeOfDay plusHours(final int n) {
        return this.withFieldAdded(DurationFieldType.hours(), n);
    }
    
    public TimeOfDay plusMinutes(final int n) {
        return this.withFieldAdded(DurationFieldType.minutes(), n);
    }
    
    public TimeOfDay plusSeconds(final int n) {
        return this.withFieldAdded(DurationFieldType.seconds(), n);
    }
    
    public TimeOfDay plusMillis(final int n) {
        return this.withFieldAdded(DurationFieldType.millis(), n);
    }
    
    public TimeOfDay minus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }
    
    public TimeOfDay minusHours(final int n) {
        return this.withFieldAdded(DurationFieldType.hours(), FieldUtils.safeNegate(n));
    }
    
    public TimeOfDay minusMinutes(final int n) {
        return this.withFieldAdded(DurationFieldType.minutes(), FieldUtils.safeNegate(n));
    }
    
    public TimeOfDay minusSeconds(final int n) {
        return this.withFieldAdded(DurationFieldType.seconds(), FieldUtils.safeNegate(n));
    }
    
    public TimeOfDay minusMillis(final int n) {
        return this.withFieldAdded(DurationFieldType.millis(), FieldUtils.safeNegate(n));
    }
    
    public Property property(final DateTimeFieldType dateTimeFieldType) {
        return new Property(this, this.indexOfSupported(dateTimeFieldType));
    }
    
    public LocalTime toLocalTime() {
        return new LocalTime(this.getHourOfDay(), this.getMinuteOfHour(), this.getSecondOfMinute(), this.getMillisOfSecond(), this.getChronology());
    }
    
    public DateTime toDateTimeToday() {
        return this.toDateTimeToday(null);
    }
    
    public DateTime toDateTimeToday(final DateTimeZone dateTimeZone) {
        final Chronology withZone = this.getChronology().withZone(dateTimeZone);
        return new DateTime(withZone.set(this, DateTimeUtils.currentTimeMillis()), withZone);
    }
    
    public int getHourOfDay() {
        return this.getValue(0);
    }
    
    public int getMinuteOfHour() {
        return this.getValue(1);
    }
    
    public int getSecondOfMinute() {
        return this.getValue(2);
    }
    
    public int getMillisOfSecond() {
        return this.getValue(3);
    }
    
    public TimeOfDay withHourOfDay(final int n) {
        return new TimeOfDay(this, this.getChronology().hourOfDay().set(this, 0, this.getValues(), n));
    }
    
    public TimeOfDay withMinuteOfHour(final int n) {
        return new TimeOfDay(this, this.getChronology().minuteOfHour().set(this, 1, this.getValues(), n));
    }
    
    public TimeOfDay withSecondOfMinute(final int n) {
        return new TimeOfDay(this, this.getChronology().secondOfMinute().set(this, 2, this.getValues(), n));
    }
    
    public TimeOfDay withMillisOfSecond(final int n) {
        return new TimeOfDay(this, this.getChronology().millisOfSecond().set(this, 3, this.getValues(), n));
    }
    
    public Property hourOfDay() {
        return new Property(this, 0);
    }
    
    public Property minuteOfHour() {
        return new Property(this, 1);
    }
    
    public Property secondOfMinute() {
        return new Property(this, 2);
    }
    
    public Property millisOfSecond() {
        return new Property(this, 3);
    }
    
    @Override
    public String toString() {
        return ISODateTimeFormat.tTime().print(this);
    }
    
    static {
        FIELD_TYPES = new DateTimeFieldType[] { DateTimeFieldType.hourOfDay(), DateTimeFieldType.minuteOfHour(), DateTimeFieldType.secondOfMinute(), DateTimeFieldType.millisOfSecond() };
        MIDNIGHT = new TimeOfDay(0, 0, 0, 0);
    }
    
    @Deprecated
    public static class Property extends AbstractPartialFieldProperty implements Serializable
    {
        private static final long serialVersionUID = 5598459141741063833L;
        private final TimeOfDay iTimeOfDay;
        private final int iFieldIndex;
        
        Property(final TimeOfDay iTimeOfDay, final int iFieldIndex) {
            this.iTimeOfDay = iTimeOfDay;
            this.iFieldIndex = iFieldIndex;
        }
        
        @Override
        public DateTimeField getField() {
            return this.iTimeOfDay.getField(this.iFieldIndex);
        }
        
        @Override
        protected ReadablePartial getReadablePartial() {
            return this.iTimeOfDay;
        }
        
        public TimeOfDay getTimeOfDay() {
            return this.iTimeOfDay;
        }
        
        @Override
        public int get() {
            return this.iTimeOfDay.getValue(this.iFieldIndex);
        }
        
        public TimeOfDay addToCopy(final int n) {
            return new TimeOfDay(this.iTimeOfDay, this.getField().addWrapPartial(this.iTimeOfDay, this.iFieldIndex, this.iTimeOfDay.getValues(), n));
        }
        
        public TimeOfDay addNoWrapToCopy(final int n) {
            return new TimeOfDay(this.iTimeOfDay, this.getField().add(this.iTimeOfDay, this.iFieldIndex, this.iTimeOfDay.getValues(), n));
        }
        
        public TimeOfDay addWrapFieldToCopy(final int n) {
            return new TimeOfDay(this.iTimeOfDay, this.getField().addWrapField(this.iTimeOfDay, this.iFieldIndex, this.iTimeOfDay.getValues(), n));
        }
        
        public TimeOfDay setCopy(final int n) {
            return new TimeOfDay(this.iTimeOfDay, this.getField().set(this.iTimeOfDay, this.iFieldIndex, this.iTimeOfDay.getValues(), n));
        }
        
        public TimeOfDay setCopy(final String s, final Locale locale) {
            return new TimeOfDay(this.iTimeOfDay, this.getField().set(this.iTimeOfDay, this.iFieldIndex, this.iTimeOfDay.getValues(), s, locale));
        }
        
        public TimeOfDay setCopy(final String s) {
            return this.setCopy(s, null);
        }
        
        public TimeOfDay withMaximumValue() {
            return this.setCopy(this.getMaximumValue());
        }
        
        public TimeOfDay withMinimumValue() {
            return this.setCopy(this.getMinimumValue());
        }
    }
}
