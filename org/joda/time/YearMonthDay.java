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
public final class YearMonthDay extends BasePartial implements ReadablePartial, Serializable
{
    private static final long serialVersionUID = 797544782896179L;
    private static final DateTimeFieldType[] FIELD_TYPES;
    public static final int YEAR = 0;
    public static final int MONTH_OF_YEAR = 1;
    public static final int DAY_OF_MONTH = 2;
    
    public static YearMonthDay fromCalendarFields(final Calendar calendar) {
        if (calendar == null) {
            throw new IllegalArgumentException("The calendar must not be null");
        }
        return new YearMonthDay(calendar.get(1), calendar.get(2) + 1, calendar.get(5));
    }
    
    public static YearMonthDay fromDateFields(final Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return new YearMonthDay(date.getYear() + 1900, date.getMonth() + 1, date.getDate());
    }
    
    public YearMonthDay() {
    }
    
    public YearMonthDay(final DateTimeZone dateTimeZone) {
        super(ISOChronology.getInstance(dateTimeZone));
    }
    
    public YearMonthDay(final Chronology chronology) {
        super(chronology);
    }
    
    public YearMonthDay(final long n) {
        super(n);
    }
    
    public YearMonthDay(final long n, final Chronology chronology) {
        super(n, chronology);
    }
    
    public YearMonthDay(final Object o) {
        super(o, null, ISODateTimeFormat.dateOptionalTimeParser());
    }
    
    public YearMonthDay(final Object o, final Chronology chronology) {
        super(o, DateTimeUtils.getChronology(chronology), ISODateTimeFormat.dateOptionalTimeParser());
    }
    
    public YearMonthDay(final int n, final int n2, final int n3) {
        this(n, n2, n3, null);
    }
    
    public YearMonthDay(final int n, final int n2, final int n3, final Chronology chronology) {
        super(new int[] { n, n2, n3 }, chronology);
    }
    
    YearMonthDay(final YearMonthDay yearMonthDay, final int[] array) {
        super(yearMonthDay, array);
    }
    
    YearMonthDay(final YearMonthDay yearMonthDay, final Chronology chronology) {
        super(yearMonthDay, chronology);
    }
    
    public int size() {
        return 3;
    }
    
    @Override
    protected DateTimeField getField(final int i, final Chronology chronology) {
        switch (i) {
            case 0: {
                return chronology.year();
            }
            case 1: {
                return chronology.monthOfYear();
            }
            case 2: {
                return chronology.dayOfMonth();
            }
            default: {
                throw new IndexOutOfBoundsException("Invalid index: " + i);
            }
        }
    }
    
    @Override
    public DateTimeFieldType getFieldType(final int n) {
        return YearMonthDay.FIELD_TYPES[n];
    }
    
    @Override
    public DateTimeFieldType[] getFieldTypes() {
        return YearMonthDay.FIELD_TYPES.clone();
    }
    
    public YearMonthDay withChronologyRetainFields(Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        chronology = chronology.withUTC();
        if (chronology == this.getChronology()) {
            return this;
        }
        final YearMonthDay yearMonthDay = new YearMonthDay(this, chronology);
        chronology.validate(yearMonthDay, this.getValues());
        return yearMonthDay;
    }
    
    public YearMonthDay withField(final DateTimeFieldType dateTimeFieldType, final int n) {
        final int indexOfSupported = this.indexOfSupported(dateTimeFieldType);
        if (n == this.getValue(indexOfSupported)) {
            return this;
        }
        return new YearMonthDay(this, this.getField(indexOfSupported).set(this, indexOfSupported, this.getValues(), n));
    }
    
    public YearMonthDay withFieldAdded(final DurationFieldType durationFieldType, final int n) {
        final int indexOfSupported = this.indexOfSupported(durationFieldType);
        if (n == 0) {
            return this;
        }
        return new YearMonthDay(this, this.getField(indexOfSupported).add(this, indexOfSupported, this.getValues(), n));
    }
    
    public YearMonthDay withPeriodAdded(final ReadablePeriod readablePeriod, final int n) {
        if (readablePeriod == null || n == 0) {
            return this;
        }
        int[] array = this.getValues();
        for (int i = 0; i < readablePeriod.size(); ++i) {
            final int index = this.indexOf(readablePeriod.getFieldType(i));
            if (index >= 0) {
                array = this.getField(index).add(this, index, array, FieldUtils.safeMultiply(readablePeriod.getValue(i), n));
            }
        }
        return new YearMonthDay(this, array);
    }
    
    public YearMonthDay plus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }
    
    public YearMonthDay plusYears(final int n) {
        return this.withFieldAdded(DurationFieldType.years(), n);
    }
    
    public YearMonthDay plusMonths(final int n) {
        return this.withFieldAdded(DurationFieldType.months(), n);
    }
    
    public YearMonthDay plusDays(final int n) {
        return this.withFieldAdded(DurationFieldType.days(), n);
    }
    
    public YearMonthDay minus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }
    
    public YearMonthDay minusYears(final int n) {
        return this.withFieldAdded(DurationFieldType.years(), FieldUtils.safeNegate(n));
    }
    
    public YearMonthDay minusMonths(final int n) {
        return this.withFieldAdded(DurationFieldType.months(), FieldUtils.safeNegate(n));
    }
    
    public YearMonthDay minusDays(final int n) {
        return this.withFieldAdded(DurationFieldType.days(), FieldUtils.safeNegate(n));
    }
    
    public Property property(final DateTimeFieldType dateTimeFieldType) {
        return new Property(this, this.indexOfSupported(dateTimeFieldType));
    }
    
    public LocalDate toLocalDate() {
        return new LocalDate(this.getYear(), this.getMonthOfYear(), this.getDayOfMonth(), this.getChronology());
    }
    
    public DateTime toDateTimeAtMidnight() {
        return this.toDateTimeAtMidnight(null);
    }
    
    public DateTime toDateTimeAtMidnight(final DateTimeZone dateTimeZone) {
        return new DateTime(this.getYear(), this.getMonthOfYear(), this.getDayOfMonth(), 0, 0, 0, 0, this.getChronology().withZone(dateTimeZone));
    }
    
    public DateTime toDateTimeAtCurrentTime() {
        return this.toDateTimeAtCurrentTime(null);
    }
    
    public DateTime toDateTimeAtCurrentTime(final DateTimeZone dateTimeZone) {
        final Chronology withZone = this.getChronology().withZone(dateTimeZone);
        return new DateTime(withZone.set(this, DateTimeUtils.currentTimeMillis()), withZone);
    }
    
    public DateMidnight toDateMidnight() {
        return this.toDateMidnight(null);
    }
    
    public DateMidnight toDateMidnight(final DateTimeZone dateTimeZone) {
        return new DateMidnight(this.getYear(), this.getMonthOfYear(), this.getDayOfMonth(), this.getChronology().withZone(dateTimeZone));
    }
    
    public DateTime toDateTime(final TimeOfDay timeOfDay) {
        return this.toDateTime(timeOfDay, null);
    }
    
    public DateTime toDateTime(final TimeOfDay timeOfDay, final DateTimeZone dateTimeZone) {
        final Chronology withZone = this.getChronology().withZone(dateTimeZone);
        long n = withZone.set(this, DateTimeUtils.currentTimeMillis());
        if (timeOfDay != null) {
            n = withZone.set(timeOfDay, n);
        }
        return new DateTime(n, withZone);
    }
    
    public Interval toInterval() {
        return this.toInterval(null);
    }
    
    public Interval toInterval(DateTimeZone zone) {
        zone = DateTimeUtils.getZone(zone);
        return this.toDateMidnight(zone).toInterval();
    }
    
    public int getYear() {
        return this.getValue(0);
    }
    
    public int getMonthOfYear() {
        return this.getValue(1);
    }
    
    public int getDayOfMonth() {
        return this.getValue(2);
    }
    
    public YearMonthDay withYear(final int n) {
        return new YearMonthDay(this, this.getChronology().year().set(this, 0, this.getValues(), n));
    }
    
    public YearMonthDay withMonthOfYear(final int n) {
        return new YearMonthDay(this, this.getChronology().monthOfYear().set(this, 1, this.getValues(), n));
    }
    
    public YearMonthDay withDayOfMonth(final int n) {
        return new YearMonthDay(this, this.getChronology().dayOfMonth().set(this, 2, this.getValues(), n));
    }
    
    public Property year() {
        return new Property(this, 0);
    }
    
    public Property monthOfYear() {
        return new Property(this, 1);
    }
    
    public Property dayOfMonth() {
        return new Property(this, 2);
    }
    
    @Override
    public String toString() {
        return ISODateTimeFormat.yearMonthDay().print(this);
    }
    
    static {
        FIELD_TYPES = new DateTimeFieldType[] { DateTimeFieldType.year(), DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth() };
    }
    
    @Deprecated
    public static class Property extends AbstractPartialFieldProperty implements Serializable
    {
        private static final long serialVersionUID = 5727734012190224363L;
        private final YearMonthDay iYearMonthDay;
        private final int iFieldIndex;
        
        Property(final YearMonthDay iYearMonthDay, final int iFieldIndex) {
            this.iYearMonthDay = iYearMonthDay;
            this.iFieldIndex = iFieldIndex;
        }
        
        @Override
        public DateTimeField getField() {
            return this.iYearMonthDay.getField(this.iFieldIndex);
        }
        
        @Override
        protected ReadablePartial getReadablePartial() {
            return this.iYearMonthDay;
        }
        
        public YearMonthDay getYearMonthDay() {
            return this.iYearMonthDay;
        }
        
        @Override
        public int get() {
            return this.iYearMonthDay.getValue(this.iFieldIndex);
        }
        
        public YearMonthDay addToCopy(final int n) {
            return new YearMonthDay(this.iYearMonthDay, this.getField().add(this.iYearMonthDay, this.iFieldIndex, this.iYearMonthDay.getValues(), n));
        }
        
        public YearMonthDay addWrapFieldToCopy(final int n) {
            return new YearMonthDay(this.iYearMonthDay, this.getField().addWrapField(this.iYearMonthDay, this.iFieldIndex, this.iYearMonthDay.getValues(), n));
        }
        
        public YearMonthDay setCopy(final int n) {
            return new YearMonthDay(this.iYearMonthDay, this.getField().set(this.iYearMonthDay, this.iFieldIndex, this.iYearMonthDay.getValues(), n));
        }
        
        public YearMonthDay setCopy(final String s, final Locale locale) {
            return new YearMonthDay(this.iYearMonthDay, this.getField().set(this.iYearMonthDay, this.iFieldIndex, this.iYearMonthDay.getValues(), s, locale));
        }
        
        public YearMonthDay setCopy(final String s) {
            return this.setCopy(s, null);
        }
        
        public YearMonthDay withMaximumValue() {
            return this.setCopy(this.getMaximumValue());
        }
        
        public YearMonthDay withMinimumValue() {
            return this.setCopy(this.getMinimumValue());
        }
    }
}
