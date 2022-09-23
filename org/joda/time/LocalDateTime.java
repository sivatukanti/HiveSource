// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.joda.time.field.AbstractReadableInstantFieldProperty;
import java.util.Locale;
import org.joda.time.format.DateTimeFormat;
import org.joda.convert.ToString;
import java.util.TimeZone;
import org.joda.time.convert.PartialConverter;
import org.joda.time.convert.ConverterManager;
import org.joda.time.chrono.ISOChronology;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.Calendar;
import org.joda.time.format.DateTimeFormatter;
import org.joda.convert.FromString;
import org.joda.time.format.ISODateTimeFormat;
import java.io.Serializable;
import org.joda.time.base.BaseLocal;

public final class LocalDateTime extends BaseLocal implements ReadablePartial, Serializable
{
    private static final long serialVersionUID = -268716875315837168L;
    private static final int YEAR = 0;
    private static final int MONTH_OF_YEAR = 1;
    private static final int DAY_OF_MONTH = 2;
    private static final int MILLIS_OF_DAY = 3;
    private final long iLocalMillis;
    private final Chronology iChronology;
    
    public static LocalDateTime now() {
        return new LocalDateTime();
    }
    
    public static LocalDateTime now(final DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            throw new NullPointerException("Zone must not be null");
        }
        return new LocalDateTime(dateTimeZone);
    }
    
    public static LocalDateTime now(final Chronology chronology) {
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        return new LocalDateTime(chronology);
    }
    
    @FromString
    public static LocalDateTime parse(final String s) {
        return parse(s, ISODateTimeFormat.localDateOptionalTimeParser());
    }
    
    public static LocalDateTime parse(final String s, final DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.parseLocalDateTime(s);
    }
    
    public static LocalDateTime fromCalendarFields(final Calendar calendar) {
        if (calendar == null) {
            throw new IllegalArgumentException("The calendar must not be null");
        }
        final int value = calendar.get(0);
        final int value2 = calendar.get(1);
        return new LocalDateTime((value == 1) ? value2 : (1 - value2), calendar.get(2) + 1, calendar.get(5), calendar.get(11), calendar.get(12), calendar.get(13), calendar.get(14));
    }
    
    public static LocalDateTime fromDateFields(final Date time) {
        if (time == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        if (time.getTime() < 0L) {
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(time);
            return fromCalendarFields(gregorianCalendar);
        }
        return new LocalDateTime(time.getYear() + 1900, time.getMonth() + 1, time.getDate(), time.getHours(), time.getMinutes(), time.getSeconds(), ((int)(time.getTime() % 1000L) + 1000) % 1000);
    }
    
    public LocalDateTime() {
        this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance());
    }
    
    public LocalDateTime(final DateTimeZone dateTimeZone) {
        this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance(dateTimeZone));
    }
    
    public LocalDateTime(final Chronology chronology) {
        this(DateTimeUtils.currentTimeMillis(), chronology);
    }
    
    public LocalDateTime(final long n) {
        this(n, ISOChronology.getInstance());
    }
    
    public LocalDateTime(final long n, final DateTimeZone dateTimeZone) {
        this(n, ISOChronology.getInstance(dateTimeZone));
    }
    
    public LocalDateTime(final long n, Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        this.iLocalMillis = chronology.getZone().getMillisKeepLocal(DateTimeZone.UTC, n);
        this.iChronology = chronology.withUTC();
    }
    
    public LocalDateTime(final Object o) {
        this(o, (Chronology)null);
    }
    
    public LocalDateTime(final Object o, final DateTimeZone dateTimeZone) {
        final PartialConverter partialConverter = ConverterManager.getInstance().getPartialConverter(o);
        final Chronology chronology = DateTimeUtils.getChronology(partialConverter.getChronology(o, dateTimeZone));
        this.iChronology = chronology.withUTC();
        final int[] partialValues = partialConverter.getPartialValues(this, o, chronology, ISODateTimeFormat.localDateOptionalTimeParser());
        this.iLocalMillis = this.iChronology.getDateTimeMillis(partialValues[0], partialValues[1], partialValues[2], partialValues[3]);
    }
    
    public LocalDateTime(final Object o, Chronology chronology) {
        final PartialConverter partialConverter = ConverterManager.getInstance().getPartialConverter(o);
        chronology = partialConverter.getChronology(o, chronology);
        chronology = DateTimeUtils.getChronology(chronology);
        this.iChronology = chronology.withUTC();
        final int[] partialValues = partialConverter.getPartialValues(this, o, chronology, ISODateTimeFormat.localDateOptionalTimeParser());
        this.iLocalMillis = this.iChronology.getDateTimeMillis(partialValues[0], partialValues[1], partialValues[2], partialValues[3]);
    }
    
    public LocalDateTime(final int n, final int n2, final int n3, final int n4, final int n5) {
        this(n, n2, n3, n4, n5, 0, 0, ISOChronology.getInstanceUTC());
    }
    
    public LocalDateTime(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this(n, n2, n3, n4, n5, n6, 0, ISOChronology.getInstanceUTC());
    }
    
    public LocalDateTime(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        this(n, n2, n3, n4, n5, n6, n7, ISOChronology.getInstanceUTC());
    }
    
    public LocalDateTime(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, Chronology withUTC) {
        withUTC = DateTimeUtils.getChronology(withUTC).withUTC();
        final long dateTimeMillis = withUTC.getDateTimeMillis(n, n2, n3, n4, n5, n6, n7);
        this.iChronology = withUTC;
        this.iLocalMillis = dateTimeMillis;
    }
    
    private Object readResolve() {
        if (this.iChronology == null) {
            return new LocalDateTime(this.iLocalMillis, ISOChronology.getInstanceUTC());
        }
        if (!DateTimeZone.UTC.equals(this.iChronology.getZone())) {
            return new LocalDateTime(this.iLocalMillis, this.iChronology.withUTC());
        }
        return this;
    }
    
    public int size() {
        return 4;
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
            case 3: {
                return chronology.millisOfDay();
            }
            default: {
                throw new IndexOutOfBoundsException("Invalid index: " + i);
            }
        }
    }
    
    public int getValue(final int i) {
        switch (i) {
            case 0: {
                return this.getChronology().year().get(this.getLocalMillis());
            }
            case 1: {
                return this.getChronology().monthOfYear().get(this.getLocalMillis());
            }
            case 2: {
                return this.getChronology().dayOfMonth().get(this.getLocalMillis());
            }
            case 3: {
                return this.getChronology().millisOfDay().get(this.getLocalMillis());
            }
            default: {
                throw new IndexOutOfBoundsException("Invalid index: " + i);
            }
        }
    }
    
    @Override
    public int get(final DateTimeFieldType dateTimeFieldType) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("The DateTimeFieldType must not be null");
        }
        return dateTimeFieldType.getField(this.getChronology()).get(this.getLocalMillis());
    }
    
    @Override
    public boolean isSupported(final DateTimeFieldType dateTimeFieldType) {
        return dateTimeFieldType != null && dateTimeFieldType.getField(this.getChronology()).isSupported();
    }
    
    public boolean isSupported(final DurationFieldType durationFieldType) {
        return durationFieldType != null && durationFieldType.getField(this.getChronology()).isSupported();
    }
    
    @Override
    protected long getLocalMillis() {
        return this.iLocalMillis;
    }
    
    public Chronology getChronology() {
        return this.iChronology;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof LocalDateTime) {
            final LocalDateTime localDateTime = (LocalDateTime)o;
            if (this.iChronology.equals(localDateTime.iChronology)) {
                return this.iLocalMillis == localDateTime.iLocalMillis;
            }
        }
        return super.equals(o);
    }
    
    @Override
    public int compareTo(final ReadablePartial readablePartial) {
        if (this == readablePartial) {
            return 0;
        }
        if (readablePartial instanceof LocalDateTime) {
            final LocalDateTime localDateTime = (LocalDateTime)readablePartial;
            if (this.iChronology.equals(localDateTime.iChronology)) {
                return (this.iLocalMillis < localDateTime.iLocalMillis) ? -1 : ((this.iLocalMillis == localDateTime.iLocalMillis) ? false : true);
            }
        }
        return super.compareTo(readablePartial);
    }
    
    public DateTime toDateTime() {
        return this.toDateTime((DateTimeZone)null);
    }
    
    public DateTime toDateTime(DateTimeZone zone) {
        zone = DateTimeUtils.getZone(zone);
        return new DateTime(this.getYear(), this.getMonthOfYear(), this.getDayOfMonth(), this.getHourOfDay(), this.getMinuteOfHour(), this.getSecondOfMinute(), this.getMillisOfSecond(), this.iChronology.withZone(zone));
    }
    
    public LocalDate toLocalDate() {
        return new LocalDate(this.getLocalMillis(), this.getChronology());
    }
    
    public LocalTime toLocalTime() {
        return new LocalTime(this.getLocalMillis(), this.getChronology());
    }
    
    public Date toDate() {
        final Date date = new Date(this.getYear() - 1900, this.getMonthOfYear() - 1, this.getDayOfMonth(), this.getHourOfDay(), this.getMinuteOfHour(), this.getSecondOfMinute());
        date.setTime(date.getTime() + this.getMillisOfSecond());
        return this.correctDstTransition(date, TimeZone.getDefault());
    }
    
    public Date toDate(final TimeZone zone) {
        final Calendar instance = Calendar.getInstance(zone);
        instance.clear();
        instance.set(this.getYear(), this.getMonthOfYear() - 1, this.getDayOfMonth(), this.getHourOfDay(), this.getMinuteOfHour(), this.getSecondOfMinute());
        final Date time = instance.getTime();
        time.setTime(time.getTime() + this.getMillisOfSecond());
        return this.correctDstTransition(time, zone);
    }
    
    private Date correctDstTransition(final Date time, final TimeZone timeZone) {
        Calendar instance = Calendar.getInstance(timeZone);
        instance.setTime(time);
        LocalDateTime localDateTime = fromCalendarFields(instance);
        if (localDateTime.isBefore(this)) {
            while (localDateTime.isBefore(this)) {
                instance.setTimeInMillis(instance.getTimeInMillis() + 60000L);
                localDateTime = fromCalendarFields(instance);
            }
            while (!localDateTime.isBefore(this)) {
                instance.setTimeInMillis(instance.getTimeInMillis() - 1000L);
                localDateTime = fromCalendarFields(instance);
            }
            instance.setTimeInMillis(instance.getTimeInMillis() + 1000L);
        }
        else if (localDateTime.equals(this)) {
            final Calendar instance2 = Calendar.getInstance(timeZone);
            instance2.setTimeInMillis(instance.getTimeInMillis() - timeZone.getDSTSavings());
            if (fromCalendarFields(instance2).equals(this)) {
                instance = instance2;
            }
        }
        return instance.getTime();
    }
    
    LocalDateTime withLocalMillis(final long n) {
        return (n == this.getLocalMillis()) ? this : new LocalDateTime(n, this.getChronology());
    }
    
    public LocalDateTime withDate(final int n, final int n2, final int n3) {
        final Chronology chronology = this.getChronology();
        return this.withLocalMillis(chronology.dayOfMonth().set(chronology.monthOfYear().set(chronology.year().set(this.getLocalMillis(), n), n2), n3));
    }
    
    public LocalDateTime withTime(final int n, final int n2, final int n3, final int n4) {
        final Chronology chronology = this.getChronology();
        return this.withLocalMillis(chronology.millisOfSecond().set(chronology.secondOfMinute().set(chronology.minuteOfHour().set(chronology.hourOfDay().set(this.getLocalMillis(), n), n2), n3), n4));
    }
    
    public LocalDateTime withFields(final ReadablePartial readablePartial) {
        if (readablePartial == null) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().set(readablePartial, this.getLocalMillis()));
    }
    
    public LocalDateTime withField(final DateTimeFieldType dateTimeFieldType, final int n) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        return this.withLocalMillis(dateTimeFieldType.getField(this.getChronology()).set(this.getLocalMillis(), n));
    }
    
    public LocalDateTime withFieldAdded(final DurationFieldType durationFieldType, final int n) {
        if (durationFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(durationFieldType.getField(this.getChronology()).add(this.getLocalMillis(), n));
    }
    
    public LocalDateTime withDurationAdded(final ReadableDuration readableDuration, final int n) {
        if (readableDuration == null || n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().add(this.getLocalMillis(), readableDuration.getMillis(), n));
    }
    
    public LocalDateTime withPeriodAdded(final ReadablePeriod readablePeriod, final int n) {
        if (readablePeriod == null || n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().add(readablePeriod, this.getLocalMillis(), n));
    }
    
    public LocalDateTime plus(final ReadableDuration readableDuration) {
        return this.withDurationAdded(readableDuration, 1);
    }
    
    public LocalDateTime plus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }
    
    public LocalDateTime plusYears(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().years().add(this.getLocalMillis(), n));
    }
    
    public LocalDateTime plusMonths(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().months().add(this.getLocalMillis(), n));
    }
    
    public LocalDateTime plusWeeks(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().weeks().add(this.getLocalMillis(), n));
    }
    
    public LocalDateTime plusDays(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().days().add(this.getLocalMillis(), n));
    }
    
    public LocalDateTime plusHours(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().hours().add(this.getLocalMillis(), n));
    }
    
    public LocalDateTime plusMinutes(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().minutes().add(this.getLocalMillis(), n));
    }
    
    public LocalDateTime plusSeconds(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().seconds().add(this.getLocalMillis(), n));
    }
    
    public LocalDateTime plusMillis(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().millis().add(this.getLocalMillis(), n));
    }
    
    public LocalDateTime minus(final ReadableDuration readableDuration) {
        return this.withDurationAdded(readableDuration, -1);
    }
    
    public LocalDateTime minus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }
    
    public LocalDateTime minusYears(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().years().subtract(this.getLocalMillis(), n));
    }
    
    public LocalDateTime minusMonths(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().months().subtract(this.getLocalMillis(), n));
    }
    
    public LocalDateTime minusWeeks(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().weeks().subtract(this.getLocalMillis(), n));
    }
    
    public LocalDateTime minusDays(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().days().subtract(this.getLocalMillis(), n));
    }
    
    public LocalDateTime minusHours(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().hours().subtract(this.getLocalMillis(), n));
    }
    
    public LocalDateTime minusMinutes(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().minutes().subtract(this.getLocalMillis(), n));
    }
    
    public LocalDateTime minusSeconds(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().seconds().subtract(this.getLocalMillis(), n));
    }
    
    public LocalDateTime minusMillis(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().millis().subtract(this.getLocalMillis(), n));
    }
    
    public Property property(final DateTimeFieldType obj) {
        if (obj == null) {
            throw new IllegalArgumentException("The DateTimeFieldType must not be null");
        }
        if (!this.isSupported(obj)) {
            throw new IllegalArgumentException("Field '" + obj + "' is not supported");
        }
        return new Property(this, obj.getField(this.getChronology()));
    }
    
    public int getEra() {
        return this.getChronology().era().get(this.getLocalMillis());
    }
    
    public int getCenturyOfEra() {
        return this.getChronology().centuryOfEra().get(this.getLocalMillis());
    }
    
    public int getYearOfEra() {
        return this.getChronology().yearOfEra().get(this.getLocalMillis());
    }
    
    public int getYearOfCentury() {
        return this.getChronology().yearOfCentury().get(this.getLocalMillis());
    }
    
    public int getYear() {
        return this.getChronology().year().get(this.getLocalMillis());
    }
    
    public int getWeekyear() {
        return this.getChronology().weekyear().get(this.getLocalMillis());
    }
    
    public int getMonthOfYear() {
        return this.getChronology().monthOfYear().get(this.getLocalMillis());
    }
    
    public int getWeekOfWeekyear() {
        return this.getChronology().weekOfWeekyear().get(this.getLocalMillis());
    }
    
    public int getDayOfYear() {
        return this.getChronology().dayOfYear().get(this.getLocalMillis());
    }
    
    public int getDayOfMonth() {
        return this.getChronology().dayOfMonth().get(this.getLocalMillis());
    }
    
    public int getDayOfWeek() {
        return this.getChronology().dayOfWeek().get(this.getLocalMillis());
    }
    
    public int getHourOfDay() {
        return this.getChronology().hourOfDay().get(this.getLocalMillis());
    }
    
    public int getMinuteOfHour() {
        return this.getChronology().minuteOfHour().get(this.getLocalMillis());
    }
    
    public int getSecondOfMinute() {
        return this.getChronology().secondOfMinute().get(this.getLocalMillis());
    }
    
    public int getMillisOfSecond() {
        return this.getChronology().millisOfSecond().get(this.getLocalMillis());
    }
    
    public int getMillisOfDay() {
        return this.getChronology().millisOfDay().get(this.getLocalMillis());
    }
    
    public LocalDateTime withEra(final int n) {
        return this.withLocalMillis(this.getChronology().era().set(this.getLocalMillis(), n));
    }
    
    public LocalDateTime withCenturyOfEra(final int n) {
        return this.withLocalMillis(this.getChronology().centuryOfEra().set(this.getLocalMillis(), n));
    }
    
    public LocalDateTime withYearOfEra(final int n) {
        return this.withLocalMillis(this.getChronology().yearOfEra().set(this.getLocalMillis(), n));
    }
    
    public LocalDateTime withYearOfCentury(final int n) {
        return this.withLocalMillis(this.getChronology().yearOfCentury().set(this.getLocalMillis(), n));
    }
    
    public LocalDateTime withYear(final int n) {
        return this.withLocalMillis(this.getChronology().year().set(this.getLocalMillis(), n));
    }
    
    public LocalDateTime withWeekyear(final int n) {
        return this.withLocalMillis(this.getChronology().weekyear().set(this.getLocalMillis(), n));
    }
    
    public LocalDateTime withMonthOfYear(final int n) {
        return this.withLocalMillis(this.getChronology().monthOfYear().set(this.getLocalMillis(), n));
    }
    
    public LocalDateTime withWeekOfWeekyear(final int n) {
        return this.withLocalMillis(this.getChronology().weekOfWeekyear().set(this.getLocalMillis(), n));
    }
    
    public LocalDateTime withDayOfYear(final int n) {
        return this.withLocalMillis(this.getChronology().dayOfYear().set(this.getLocalMillis(), n));
    }
    
    public LocalDateTime withDayOfMonth(final int n) {
        return this.withLocalMillis(this.getChronology().dayOfMonth().set(this.getLocalMillis(), n));
    }
    
    public LocalDateTime withDayOfWeek(final int n) {
        return this.withLocalMillis(this.getChronology().dayOfWeek().set(this.getLocalMillis(), n));
    }
    
    public LocalDateTime withHourOfDay(final int n) {
        return this.withLocalMillis(this.getChronology().hourOfDay().set(this.getLocalMillis(), n));
    }
    
    public LocalDateTime withMinuteOfHour(final int n) {
        return this.withLocalMillis(this.getChronology().minuteOfHour().set(this.getLocalMillis(), n));
    }
    
    public LocalDateTime withSecondOfMinute(final int n) {
        return this.withLocalMillis(this.getChronology().secondOfMinute().set(this.getLocalMillis(), n));
    }
    
    public LocalDateTime withMillisOfSecond(final int n) {
        return this.withLocalMillis(this.getChronology().millisOfSecond().set(this.getLocalMillis(), n));
    }
    
    public LocalDateTime withMillisOfDay(final int n) {
        return this.withLocalMillis(this.getChronology().millisOfDay().set(this.getLocalMillis(), n));
    }
    
    public Property era() {
        return new Property(this, this.getChronology().era());
    }
    
    public Property centuryOfEra() {
        return new Property(this, this.getChronology().centuryOfEra());
    }
    
    public Property yearOfCentury() {
        return new Property(this, this.getChronology().yearOfCentury());
    }
    
    public Property yearOfEra() {
        return new Property(this, this.getChronology().yearOfEra());
    }
    
    public Property year() {
        return new Property(this, this.getChronology().year());
    }
    
    public Property weekyear() {
        return new Property(this, this.getChronology().weekyear());
    }
    
    public Property monthOfYear() {
        return new Property(this, this.getChronology().monthOfYear());
    }
    
    public Property weekOfWeekyear() {
        return new Property(this, this.getChronology().weekOfWeekyear());
    }
    
    public Property dayOfYear() {
        return new Property(this, this.getChronology().dayOfYear());
    }
    
    public Property dayOfMonth() {
        return new Property(this, this.getChronology().dayOfMonth());
    }
    
    public Property dayOfWeek() {
        return new Property(this, this.getChronology().dayOfWeek());
    }
    
    public Property hourOfDay() {
        return new Property(this, this.getChronology().hourOfDay());
    }
    
    public Property minuteOfHour() {
        return new Property(this, this.getChronology().minuteOfHour());
    }
    
    public Property secondOfMinute() {
        return new Property(this, this.getChronology().secondOfMinute());
    }
    
    public Property millisOfSecond() {
        return new Property(this, this.getChronology().millisOfSecond());
    }
    
    public Property millisOfDay() {
        return new Property(this, this.getChronology().millisOfDay());
    }
    
    @ToString
    @Override
    public String toString() {
        return ISODateTimeFormat.dateTime().print(this);
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
    
    public static final class Property extends AbstractReadableInstantFieldProperty
    {
        private static final long serialVersionUID = -358138762846288L;
        private transient LocalDateTime iInstant;
        private transient DateTimeField iField;
        
        Property(final LocalDateTime iInstant, final DateTimeField iField) {
            this.iInstant = iInstant;
            this.iField = iField;
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeObject(this.iInstant);
            objectOutputStream.writeObject(this.iField.getType());
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            this.iInstant = (LocalDateTime)objectInputStream.readObject();
            this.iField = ((DateTimeFieldType)objectInputStream.readObject()).getField(this.iInstant.getChronology());
        }
        
        @Override
        public DateTimeField getField() {
            return this.iField;
        }
        
        @Override
        protected long getMillis() {
            return this.iInstant.getLocalMillis();
        }
        
        @Override
        protected Chronology getChronology() {
            return this.iInstant.getChronology();
        }
        
        public LocalDateTime getLocalDateTime() {
            return this.iInstant;
        }
        
        public LocalDateTime addToCopy(final int n) {
            return this.iInstant.withLocalMillis(this.iField.add(this.iInstant.getLocalMillis(), n));
        }
        
        public LocalDateTime addToCopy(final long n) {
            return this.iInstant.withLocalMillis(this.iField.add(this.iInstant.getLocalMillis(), n));
        }
        
        public LocalDateTime addWrapFieldToCopy(final int n) {
            return this.iInstant.withLocalMillis(this.iField.addWrapField(this.iInstant.getLocalMillis(), n));
        }
        
        public LocalDateTime setCopy(final int n) {
            return this.iInstant.withLocalMillis(this.iField.set(this.iInstant.getLocalMillis(), n));
        }
        
        public LocalDateTime setCopy(final String s, final Locale locale) {
            return this.iInstant.withLocalMillis(this.iField.set(this.iInstant.getLocalMillis(), s, locale));
        }
        
        public LocalDateTime setCopy(final String s) {
            return this.setCopy(s, null);
        }
        
        public LocalDateTime withMaximumValue() {
            return this.setCopy(this.getMaximumValue());
        }
        
        public LocalDateTime withMinimumValue() {
            return this.setCopy(this.getMinimumValue());
        }
        
        public LocalDateTime roundFloorCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundFloor(this.iInstant.getLocalMillis()));
        }
        
        public LocalDateTime roundCeilingCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundCeiling(this.iInstant.getLocalMillis()));
        }
        
        public LocalDateTime roundHalfFloorCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundHalfFloor(this.iInstant.getLocalMillis()));
        }
        
        public LocalDateTime roundHalfCeilingCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundHalfCeiling(this.iInstant.getLocalMillis()));
        }
        
        public LocalDateTime roundHalfEvenCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundHalfEven(this.iInstant.getLocalMillis()));
        }
    }
}
