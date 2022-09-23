// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import org.joda.time.field.AbstractPartialFieldProperty;
import java.util.Locale;
import org.joda.time.format.DateTimeFormat;
import org.joda.convert.ToString;
import org.joda.time.field.FieldUtils;
import org.joda.time.chrono.ISOChronology;
import java.util.Date;
import java.util.Calendar;
import org.joda.time.format.DateTimeFormatter;
import org.joda.convert.FromString;
import org.joda.time.format.ISODateTimeFormat;
import java.io.Serializable;
import org.joda.time.base.BasePartial;

public final class YearMonth extends BasePartial implements ReadablePartial, Serializable
{
    private static final long serialVersionUID = 797544782896179L;
    private static final DateTimeFieldType[] FIELD_TYPES;
    public static final int YEAR = 0;
    public static final int MONTH_OF_YEAR = 1;
    
    public static YearMonth now() {
        return new YearMonth();
    }
    
    public static YearMonth now(final DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            throw new NullPointerException("Zone must not be null");
        }
        return new YearMonth(dateTimeZone);
    }
    
    public static YearMonth now(final Chronology chronology) {
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        return new YearMonth(chronology);
    }
    
    @FromString
    public static YearMonth parse(final String s) {
        return parse(s, ISODateTimeFormat.localDateParser());
    }
    
    public static YearMonth parse(final String s, final DateTimeFormatter dateTimeFormatter) {
        final LocalDate localDate = dateTimeFormatter.parseLocalDate(s);
        return new YearMonth(localDate.getYear(), localDate.getMonthOfYear());
    }
    
    public static YearMonth fromCalendarFields(final Calendar calendar) {
        if (calendar == null) {
            throw new IllegalArgumentException("The calendar must not be null");
        }
        return new YearMonth(calendar.get(1), calendar.get(2) + 1);
    }
    
    public static YearMonth fromDateFields(final Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return new YearMonth(date.getYear() + 1900, date.getMonth() + 1);
    }
    
    public YearMonth() {
    }
    
    public YearMonth(final DateTimeZone dateTimeZone) {
        super(ISOChronology.getInstance(dateTimeZone));
    }
    
    public YearMonth(final Chronology chronology) {
        super(chronology);
    }
    
    public YearMonth(final long n) {
        super(n);
    }
    
    public YearMonth(final long n, final Chronology chronology) {
        super(n, chronology);
    }
    
    public YearMonth(final Object o) {
        super(o, null, ISODateTimeFormat.localDateParser());
    }
    
    public YearMonth(final Object o, final Chronology chronology) {
        super(o, DateTimeUtils.getChronology(chronology), ISODateTimeFormat.localDateParser());
    }
    
    public YearMonth(final int n, final int n2) {
        this(n, n2, null);
    }
    
    public YearMonth(final int n, final int n2, final Chronology chronology) {
        super(new int[] { n, n2 }, chronology);
    }
    
    YearMonth(final YearMonth yearMonth, final int[] array) {
        super(yearMonth, array);
    }
    
    YearMonth(final YearMonth yearMonth, final Chronology chronology) {
        super(yearMonth, chronology);
    }
    
    private Object readResolve() {
        if (!DateTimeZone.UTC.equals(this.getChronology().getZone())) {
            return new YearMonth(this, this.getChronology().withUTC());
        }
        return this;
    }
    
    public int size() {
        return 2;
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
            default: {
                throw new IndexOutOfBoundsException("Invalid index: " + i);
            }
        }
    }
    
    @Override
    public DateTimeFieldType getFieldType(final int n) {
        return YearMonth.FIELD_TYPES[n];
    }
    
    @Override
    public DateTimeFieldType[] getFieldTypes() {
        return YearMonth.FIELD_TYPES.clone();
    }
    
    public YearMonth withChronologyRetainFields(Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        chronology = chronology.withUTC();
        if (chronology == this.getChronology()) {
            return this;
        }
        final YearMonth yearMonth = new YearMonth(this, chronology);
        chronology.validate(yearMonth, this.getValues());
        return yearMonth;
    }
    
    public YearMonth withField(final DateTimeFieldType dateTimeFieldType, final int n) {
        final int indexOfSupported = this.indexOfSupported(dateTimeFieldType);
        if (n == this.getValue(indexOfSupported)) {
            return this;
        }
        return new YearMonth(this, this.getField(indexOfSupported).set(this, indexOfSupported, this.getValues(), n));
    }
    
    public YearMonth withFieldAdded(final DurationFieldType durationFieldType, final int n) {
        final int indexOfSupported = this.indexOfSupported(durationFieldType);
        if (n == 0) {
            return this;
        }
        return new YearMonth(this, this.getField(indexOfSupported).add(this, indexOfSupported, this.getValues(), n));
    }
    
    public YearMonth withPeriodAdded(final ReadablePeriod readablePeriod, final int n) {
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
        return new YearMonth(this, array);
    }
    
    public YearMonth plus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }
    
    public YearMonth plusYears(final int n) {
        return this.withFieldAdded(DurationFieldType.years(), n);
    }
    
    public YearMonth plusMonths(final int n) {
        return this.withFieldAdded(DurationFieldType.months(), n);
    }
    
    public YearMonth minus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }
    
    public YearMonth minusYears(final int n) {
        return this.withFieldAdded(DurationFieldType.years(), FieldUtils.safeNegate(n));
    }
    
    public YearMonth minusMonths(final int n) {
        return this.withFieldAdded(DurationFieldType.months(), FieldUtils.safeNegate(n));
    }
    
    public LocalDate toLocalDate(final int n) {
        return new LocalDate(this.getYear(), this.getMonthOfYear(), n, this.getChronology());
    }
    
    public Interval toInterval() {
        return this.toInterval(null);
    }
    
    public Interval toInterval(DateTimeZone zone) {
        zone = DateTimeUtils.getZone(zone);
        return new Interval(this.toLocalDate(1).toDateTimeAtStartOfDay(zone), this.plusMonths(1).toLocalDate(1).toDateTimeAtStartOfDay(zone));
    }
    
    public int getYear() {
        return this.getValue(0);
    }
    
    public int getMonthOfYear() {
        return this.getValue(1);
    }
    
    public YearMonth withYear(final int n) {
        return new YearMonth(this, this.getChronology().year().set(this, 0, this.getValues(), n));
    }
    
    public YearMonth withMonthOfYear(final int n) {
        return new YearMonth(this, this.getChronology().monthOfYear().set(this, 1, this.getValues(), n));
    }
    
    public Property property(final DateTimeFieldType dateTimeFieldType) {
        return new Property(this, this.indexOfSupported(dateTimeFieldType));
    }
    
    public Property year() {
        return new Property(this, 0);
    }
    
    public Property monthOfYear() {
        return new Property(this, 1);
    }
    
    @ToString
    @Override
    public String toString() {
        return ISODateTimeFormat.yearMonth().print(this);
    }
    
    @Override
    public String toString(final String s) {
        if (s == null) {
            return this.toString();
        }
        return DateTimeFormat.forPattern(s).print(this);
    }
    
    @Override
    public String toString(final String s, final Locale locale) throws IllegalArgumentException {
        if (s == null) {
            return this.toString();
        }
        return DateTimeFormat.forPattern(s).withLocale(locale).print(this);
    }
    
    static {
        FIELD_TYPES = new DateTimeFieldType[] { DateTimeFieldType.year(), DateTimeFieldType.monthOfYear() };
    }
    
    public static class Property extends AbstractPartialFieldProperty implements Serializable
    {
        private static final long serialVersionUID = 5727734012190224363L;
        private final YearMonth iBase;
        private final int iFieldIndex;
        
        Property(final YearMonth iBase, final int iFieldIndex) {
            this.iBase = iBase;
            this.iFieldIndex = iFieldIndex;
        }
        
        @Override
        public DateTimeField getField() {
            return this.iBase.getField(this.iFieldIndex);
        }
        
        @Override
        protected ReadablePartial getReadablePartial() {
            return this.iBase;
        }
        
        public YearMonth getYearMonth() {
            return this.iBase;
        }
        
        @Override
        public int get() {
            return this.iBase.getValue(this.iFieldIndex);
        }
        
        public YearMonth addToCopy(final int n) {
            return new YearMonth(this.iBase, this.getField().add(this.iBase, this.iFieldIndex, this.iBase.getValues(), n));
        }
        
        public YearMonth addWrapFieldToCopy(final int n) {
            return new YearMonth(this.iBase, this.getField().addWrapField(this.iBase, this.iFieldIndex, this.iBase.getValues(), n));
        }
        
        public YearMonth setCopy(final int n) {
            return new YearMonth(this.iBase, this.getField().set(this.iBase, this.iFieldIndex, this.iBase.getValues(), n));
        }
        
        public YearMonth setCopy(final String s, final Locale locale) {
            return new YearMonth(this.iBase, this.getField().set(this.iBase, this.iFieldIndex, this.iBase.getValues(), s, locale));
        }
        
        public YearMonth setCopy(final String s) {
            return this.setCopy(s, null);
        }
    }
}
