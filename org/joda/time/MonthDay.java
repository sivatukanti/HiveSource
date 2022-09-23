// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import org.joda.time.field.AbstractPartialFieldProperty;
import org.joda.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import org.joda.time.format.DateTimeFormat;
import org.joda.convert.ToString;
import java.util.Collection;
import java.util.ArrayList;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.chrono.ISOChronology;
import java.util.Date;
import java.util.Calendar;
import org.joda.convert.FromString;
import org.joda.time.format.DateTimeFormatter;
import java.io.Serializable;
import org.joda.time.base.BasePartial;

public final class MonthDay extends BasePartial implements ReadablePartial, Serializable
{
    private static final long serialVersionUID = 2954560699050434609L;
    private static final DateTimeFieldType[] FIELD_TYPES;
    private static final DateTimeFormatter PARSER;
    public static final int MONTH_OF_YEAR = 0;
    public static final int DAY_OF_MONTH = 1;
    
    public static MonthDay now() {
        return new MonthDay();
    }
    
    public static MonthDay now(final DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            throw new NullPointerException("Zone must not be null");
        }
        return new MonthDay(dateTimeZone);
    }
    
    public static MonthDay now(final Chronology chronology) {
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        return new MonthDay(chronology);
    }
    
    @FromString
    public static MonthDay parse(final String s) {
        return parse(s, MonthDay.PARSER);
    }
    
    public static MonthDay parse(final String s, final DateTimeFormatter dateTimeFormatter) {
        final LocalDate localDate = dateTimeFormatter.parseLocalDate(s);
        return new MonthDay(localDate.getMonthOfYear(), localDate.getDayOfMonth());
    }
    
    public static MonthDay fromCalendarFields(final Calendar calendar) {
        if (calendar == null) {
            throw new IllegalArgumentException("The calendar must not be null");
        }
        return new MonthDay(calendar.get(2) + 1, calendar.get(5));
    }
    
    public static MonthDay fromDateFields(final Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return new MonthDay(date.getMonth() + 1, date.getDate());
    }
    
    public MonthDay() {
    }
    
    public MonthDay(final DateTimeZone dateTimeZone) {
        super(ISOChronology.getInstance(dateTimeZone));
    }
    
    public MonthDay(final Chronology chronology) {
        super(chronology);
    }
    
    public MonthDay(final long n) {
        super(n);
    }
    
    public MonthDay(final long n, final Chronology chronology) {
        super(n, chronology);
    }
    
    public MonthDay(final Object o) {
        super(o, null, ISODateTimeFormat.localDateParser());
    }
    
    public MonthDay(final Object o, final Chronology chronology) {
        super(o, DateTimeUtils.getChronology(chronology), ISODateTimeFormat.localDateParser());
    }
    
    public MonthDay(final int n, final int n2) {
        this(n, n2, null);
    }
    
    public MonthDay(final int n, final int n2, final Chronology chronology) {
        super(new int[] { n, n2 }, chronology);
    }
    
    MonthDay(final MonthDay monthDay, final int[] array) {
        super(monthDay, array);
    }
    
    MonthDay(final MonthDay monthDay, final Chronology chronology) {
        super(monthDay, chronology);
    }
    
    private Object readResolve() {
        if (!DateTimeZone.UTC.equals(this.getChronology().getZone())) {
            return new MonthDay(this, this.getChronology().withUTC());
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
                return chronology.monthOfYear();
            }
            case 1: {
                return chronology.dayOfMonth();
            }
            default: {
                throw new IndexOutOfBoundsException("Invalid index: " + i);
            }
        }
    }
    
    @Override
    public DateTimeFieldType getFieldType(final int n) {
        return MonthDay.FIELD_TYPES[n];
    }
    
    @Override
    public DateTimeFieldType[] getFieldTypes() {
        return MonthDay.FIELD_TYPES.clone();
    }
    
    public MonthDay withChronologyRetainFields(Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        chronology = chronology.withUTC();
        if (chronology == this.getChronology()) {
            return this;
        }
        final MonthDay monthDay = new MonthDay(this, chronology);
        chronology.validate(monthDay, this.getValues());
        return monthDay;
    }
    
    public MonthDay withField(final DateTimeFieldType dateTimeFieldType, final int n) {
        final int indexOfSupported = this.indexOfSupported(dateTimeFieldType);
        if (n == this.getValue(indexOfSupported)) {
            return this;
        }
        return new MonthDay(this, this.getField(indexOfSupported).set(this, indexOfSupported, this.getValues(), n));
    }
    
    public MonthDay withFieldAdded(final DurationFieldType durationFieldType, final int n) {
        final int indexOfSupported = this.indexOfSupported(durationFieldType);
        if (n == 0) {
            return this;
        }
        return new MonthDay(this, this.getField(indexOfSupported).add(this, indexOfSupported, this.getValues(), n));
    }
    
    public MonthDay withPeriodAdded(final ReadablePeriod readablePeriod, final int n) {
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
        return new MonthDay(this, array);
    }
    
    public MonthDay plus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }
    
    public MonthDay plusMonths(final int n) {
        return this.withFieldAdded(DurationFieldType.months(), n);
    }
    
    public MonthDay plusDays(final int n) {
        return this.withFieldAdded(DurationFieldType.days(), n);
    }
    
    public MonthDay minus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }
    
    public MonthDay minusMonths(final int n) {
        return this.withFieldAdded(DurationFieldType.months(), FieldUtils.safeNegate(n));
    }
    
    public MonthDay minusDays(final int n) {
        return this.withFieldAdded(DurationFieldType.days(), FieldUtils.safeNegate(n));
    }
    
    public LocalDate toLocalDate(final int n) {
        return new LocalDate(n, this.getMonthOfYear(), this.getDayOfMonth(), this.getChronology());
    }
    
    public int getMonthOfYear() {
        return this.getValue(0);
    }
    
    public int getDayOfMonth() {
        return this.getValue(1);
    }
    
    public MonthDay withMonthOfYear(final int n) {
        return new MonthDay(this, this.getChronology().monthOfYear().set(this, 0, this.getValues(), n));
    }
    
    public MonthDay withDayOfMonth(final int n) {
        return new MonthDay(this, this.getChronology().dayOfMonth().set(this, 1, this.getValues(), n));
    }
    
    public Property property(final DateTimeFieldType dateTimeFieldType) {
        return new Property(this, this.indexOfSupported(dateTimeFieldType));
    }
    
    public Property monthOfYear() {
        return new Property(this, 0);
    }
    
    public Property dayOfMonth() {
        return new Property(this, 1);
    }
    
    @ToString
    @Override
    public String toString() {
        final ArrayList<DateTimeFieldType> list = new ArrayList<DateTimeFieldType>();
        list.add(DateTimeFieldType.monthOfYear());
        list.add(DateTimeFieldType.dayOfMonth());
        return ISODateTimeFormat.forFields(list, true, true).print(this);
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
        FIELD_TYPES = new DateTimeFieldType[] { DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth() };
        PARSER = new DateTimeFormatterBuilder().appendOptional(ISODateTimeFormat.localDateParser().getParser()).appendOptional(DateTimeFormat.forPattern("--MM-dd").getParser()).toFormatter();
    }
    
    public static class Property extends AbstractPartialFieldProperty implements Serializable
    {
        private static final long serialVersionUID = 5727734012190224363L;
        private final MonthDay iBase;
        private final int iFieldIndex;
        
        Property(final MonthDay iBase, final int iFieldIndex) {
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
        
        public MonthDay getMonthDay() {
            return this.iBase;
        }
        
        @Override
        public int get() {
            return this.iBase.getValue(this.iFieldIndex);
        }
        
        public MonthDay addToCopy(final int n) {
            return new MonthDay(this.iBase, this.getField().add(this.iBase, this.iFieldIndex, this.iBase.getValues(), n));
        }
        
        public MonthDay addWrapFieldToCopy(final int n) {
            return new MonthDay(this.iBase, this.getField().addWrapField(this.iBase, this.iFieldIndex, this.iBase.getValues(), n));
        }
        
        public MonthDay setCopy(final int n) {
            return new MonthDay(this.iBase, this.getField().set(this.iBase, this.iFieldIndex, this.iBase.getValues(), n));
        }
        
        public MonthDay setCopy(final String s, final Locale locale) {
            return new MonthDay(this.iBase, this.getField().set(this.iBase, this.iFieldIndex, this.iBase.getValues(), s, locale));
        }
        
        public MonthDay setCopy(final String s) {
            return this.setCopy(s, null);
        }
    }
}
