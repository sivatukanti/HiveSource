// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.joda.time.field.AbstractReadableInstantFieldProperty;
import java.util.HashSet;
import java.util.Locale;
import org.joda.time.format.DateTimeFormat;
import org.joda.convert.ToString;
import org.joda.time.field.FieldUtils;
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
import java.util.Set;
import java.io.Serializable;
import org.joda.time.base.BaseLocal;

public final class LocalDate extends BaseLocal implements ReadablePartial, Serializable
{
    private static final long serialVersionUID = -8775358157899L;
    private static final int YEAR = 0;
    private static final int MONTH_OF_YEAR = 1;
    private static final int DAY_OF_MONTH = 2;
    private static final Set<DurationFieldType> DATE_DURATION_TYPES;
    private final long iLocalMillis;
    private final Chronology iChronology;
    private transient int iHash;
    
    public static LocalDate now() {
        return new LocalDate();
    }
    
    public static LocalDate now(final DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            throw new NullPointerException("Zone must not be null");
        }
        return new LocalDate(dateTimeZone);
    }
    
    public static LocalDate now(final Chronology chronology) {
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        return new LocalDate(chronology);
    }
    
    @FromString
    public static LocalDate parse(final String s) {
        return parse(s, ISODateTimeFormat.localDateParser());
    }
    
    public static LocalDate parse(final String s, final DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.parseLocalDate(s);
    }
    
    public static LocalDate fromCalendarFields(final Calendar calendar) {
        if (calendar == null) {
            throw new IllegalArgumentException("The calendar must not be null");
        }
        final int value = calendar.get(0);
        final int value2 = calendar.get(1);
        return new LocalDate((value == 1) ? value2 : (1 - value2), calendar.get(2) + 1, calendar.get(5));
    }
    
    public static LocalDate fromDateFields(final Date time) {
        if (time == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        if (time.getTime() < 0L) {
            final GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(time);
            return fromCalendarFields(gregorianCalendar);
        }
        return new LocalDate(time.getYear() + 1900, time.getMonth() + 1, time.getDate());
    }
    
    public LocalDate() {
        this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance());
    }
    
    public LocalDate(final DateTimeZone dateTimeZone) {
        this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance(dateTimeZone));
    }
    
    public LocalDate(final Chronology chronology) {
        this(DateTimeUtils.currentTimeMillis(), chronology);
    }
    
    public LocalDate(final long n) {
        this(n, ISOChronology.getInstance());
    }
    
    public LocalDate(final long n, final DateTimeZone dateTimeZone) {
        this(n, ISOChronology.getInstance(dateTimeZone));
    }
    
    public LocalDate(final long n, Chronology iChronology) {
        iChronology = DateTimeUtils.getChronology(iChronology);
        final long millisKeepLocal = iChronology.getZone().getMillisKeepLocal(DateTimeZone.UTC, n);
        iChronology = iChronology.withUTC();
        this.iLocalMillis = iChronology.dayOfMonth().roundFloor(millisKeepLocal);
        this.iChronology = iChronology;
    }
    
    public LocalDate(final Object o) {
        this(o, (Chronology)null);
    }
    
    public LocalDate(final Object o, final DateTimeZone dateTimeZone) {
        final PartialConverter partialConverter = ConverterManager.getInstance().getPartialConverter(o);
        final Chronology chronology = DateTimeUtils.getChronology(partialConverter.getChronology(o, dateTimeZone));
        this.iChronology = chronology.withUTC();
        final int[] partialValues = partialConverter.getPartialValues(this, o, chronology, ISODateTimeFormat.localDateParser());
        this.iLocalMillis = this.iChronology.getDateTimeMillis(partialValues[0], partialValues[1], partialValues[2], 0);
    }
    
    public LocalDate(final Object o, Chronology chronology) {
        final PartialConverter partialConverter = ConverterManager.getInstance().getPartialConverter(o);
        chronology = partialConverter.getChronology(o, chronology);
        chronology = DateTimeUtils.getChronology(chronology);
        this.iChronology = chronology.withUTC();
        final int[] partialValues = partialConverter.getPartialValues(this, o, chronology, ISODateTimeFormat.localDateParser());
        this.iLocalMillis = this.iChronology.getDateTimeMillis(partialValues[0], partialValues[1], partialValues[2], 0);
    }
    
    public LocalDate(final int n, final int n2, final int n3) {
        this(n, n2, n3, ISOChronology.getInstanceUTC());
    }
    
    public LocalDate(final int n, final int n2, final int n3, Chronology withUTC) {
        withUTC = DateTimeUtils.getChronology(withUTC).withUTC();
        final long dateTimeMillis = withUTC.getDateTimeMillis(n, n2, n3, 0);
        this.iChronology = withUTC;
        this.iLocalMillis = dateTimeMillis;
    }
    
    private Object readResolve() {
        if (this.iChronology == null) {
            return new LocalDate(this.iLocalMillis, ISOChronology.getInstanceUTC());
        }
        if (!DateTimeZone.UTC.equals(this.iChronology.getZone())) {
            return new LocalDate(this.iLocalMillis, this.iChronology.withUTC());
        }
        return this;
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
            default: {
                throw new IndexOutOfBoundsException("Invalid index: " + i);
            }
        }
    }
    
    @Override
    public int get(final DateTimeFieldType obj) {
        if (obj == null) {
            throw new IllegalArgumentException("The DateTimeFieldType must not be null");
        }
        if (!this.isSupported(obj)) {
            throw new IllegalArgumentException("Field '" + obj + "' is not supported");
        }
        return obj.getField(this.getChronology()).get(this.getLocalMillis());
    }
    
    @Override
    public boolean isSupported(final DateTimeFieldType dateTimeFieldType) {
        if (dateTimeFieldType == null) {
            return false;
        }
        final DurationFieldType durationType = dateTimeFieldType.getDurationType();
        return (LocalDate.DATE_DURATION_TYPES.contains(durationType) || durationType.getField(this.getChronology()).getUnitMillis() >= this.getChronology().days().getUnitMillis()) && dateTimeFieldType.getField(this.getChronology()).isSupported();
    }
    
    public boolean isSupported(final DurationFieldType durationFieldType) {
        if (durationFieldType == null) {
            return false;
        }
        final DurationField field = durationFieldType.getField(this.getChronology());
        return (LocalDate.DATE_DURATION_TYPES.contains(durationFieldType) || field.getUnitMillis() >= this.getChronology().days().getUnitMillis()) && field.isSupported();
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
        if (o instanceof LocalDate) {
            final LocalDate localDate = (LocalDate)o;
            if (this.iChronology.equals(localDate.iChronology)) {
                return this.iLocalMillis == localDate.iLocalMillis;
            }
        }
        return super.equals(o);
    }
    
    @Override
    public int hashCode() {
        int iHash = this.iHash;
        if (iHash == 0) {
            final int hashCode = super.hashCode();
            this.iHash = hashCode;
            iHash = hashCode;
        }
        return iHash;
    }
    
    @Override
    public int compareTo(final ReadablePartial readablePartial) {
        if (this == readablePartial) {
            return 0;
        }
        if (readablePartial instanceof LocalDate) {
            final LocalDate localDate = (LocalDate)readablePartial;
            if (this.iChronology.equals(localDate.iChronology)) {
                return (this.iLocalMillis < localDate.iLocalMillis) ? -1 : ((this.iLocalMillis == localDate.iLocalMillis) ? false : true);
            }
        }
        return super.compareTo(readablePartial);
    }
    
    public DateTime toDateTimeAtStartOfDay() {
        return this.toDateTimeAtStartOfDay(null);
    }
    
    public DateTime toDateTimeAtStartOfDay(DateTimeZone zone) {
        zone = DateTimeUtils.getZone(zone);
        final Chronology withZone = this.getChronology().withZone(zone);
        return new DateTime(withZone.dayOfMonth().roundFloor(zone.convertLocalToUTC(this.getLocalMillis() + 21600000L, false)), withZone);
    }
    
    @Deprecated
    public DateTime toDateTimeAtMidnight() {
        return this.toDateTimeAtMidnight(null);
    }
    
    @Deprecated
    public DateTime toDateTimeAtMidnight(DateTimeZone zone) {
        zone = DateTimeUtils.getZone(zone);
        return new DateTime(this.getYear(), this.getMonthOfYear(), this.getDayOfMonth(), 0, 0, 0, 0, this.getChronology().withZone(zone));
    }
    
    public DateTime toDateTimeAtCurrentTime() {
        return this.toDateTimeAtCurrentTime(null);
    }
    
    public DateTime toDateTimeAtCurrentTime(DateTimeZone zone) {
        zone = DateTimeUtils.getZone(zone);
        final Chronology withZone = this.getChronology().withZone(zone);
        return new DateTime(withZone.set(this, DateTimeUtils.currentTimeMillis()), withZone);
    }
    
    @Deprecated
    public DateMidnight toDateMidnight() {
        return this.toDateMidnight(null);
    }
    
    @Deprecated
    public DateMidnight toDateMidnight(DateTimeZone zone) {
        zone = DateTimeUtils.getZone(zone);
        return new DateMidnight(this.getYear(), this.getMonthOfYear(), this.getDayOfMonth(), this.getChronology().withZone(zone));
    }
    
    public LocalDateTime toLocalDateTime(final LocalTime localTime) {
        if (localTime == null) {
            throw new IllegalArgumentException("The time must not be null");
        }
        if (this.getChronology() != localTime.getChronology()) {
            throw new IllegalArgumentException("The chronology of the time does not match");
        }
        return new LocalDateTime(this.getLocalMillis() + localTime.getLocalMillis(), this.getChronology());
    }
    
    public DateTime toDateTime(final LocalTime localTime) {
        return this.toDateTime(localTime, null);
    }
    
    public DateTime toDateTime(final LocalTime localTime, final DateTimeZone dateTimeZone) {
        if (localTime == null) {
            return this.toDateTimeAtCurrentTime(dateTimeZone);
        }
        if (this.getChronology() != localTime.getChronology()) {
            throw new IllegalArgumentException("The chronology of the time does not match");
        }
        return new DateTime(this.getYear(), this.getMonthOfYear(), this.getDayOfMonth(), localTime.getHourOfDay(), localTime.getMinuteOfHour(), localTime.getSecondOfMinute(), localTime.getMillisOfSecond(), this.getChronology().withZone(dateTimeZone));
    }
    
    public Interval toInterval() {
        return this.toInterval(null);
    }
    
    public Interval toInterval(DateTimeZone zone) {
        zone = DateTimeUtils.getZone(zone);
        return new Interval(this.toDateTimeAtStartOfDay(zone), this.plusDays(1).toDateTimeAtStartOfDay(zone));
    }
    
    public Date toDate() {
        final int dayOfMonth = this.getDayOfMonth();
        Date date = new Date(this.getYear() - 1900, this.getMonthOfYear() - 1, dayOfMonth);
        LocalDate localDate = fromDateFields(date);
        if (localDate.isBefore(this)) {
            while (!localDate.equals(this)) {
                date.setTime(date.getTime() + 3600000L);
                localDate = fromDateFields(date);
            }
            while (date.getDate() == dayOfMonth) {
                date.setTime(date.getTime() - 1000L);
            }
            date.setTime(date.getTime() + 1000L);
        }
        else if (localDate.equals(this)) {
            final Date date2 = new Date(date.getTime() - TimeZone.getDefault().getDSTSavings());
            if (date2.getDate() == dayOfMonth) {
                date = date2;
            }
        }
        return date;
    }
    
    LocalDate withLocalMillis(long roundFloor) {
        roundFloor = this.iChronology.dayOfMonth().roundFloor(roundFloor);
        return (roundFloor == this.getLocalMillis()) ? this : new LocalDate(roundFloor, this.getChronology());
    }
    
    public LocalDate withFields(final ReadablePartial readablePartial) {
        if (readablePartial == null) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().set(readablePartial, this.getLocalMillis()));
    }
    
    public LocalDate withField(final DateTimeFieldType obj, final int n) {
        if (obj == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        if (!this.isSupported(obj)) {
            throw new IllegalArgumentException("Field '" + obj + "' is not supported");
        }
        return this.withLocalMillis(obj.getField(this.getChronology()).set(this.getLocalMillis(), n));
    }
    
    public LocalDate withFieldAdded(final DurationFieldType obj, final int n) {
        if (obj == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        if (!this.isSupported(obj)) {
            throw new IllegalArgumentException("Field '" + obj + "' is not supported");
        }
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(obj.getField(this.getChronology()).add(this.getLocalMillis(), n));
    }
    
    public LocalDate withPeriodAdded(final ReadablePeriod readablePeriod, final int n) {
        if (readablePeriod == null || n == 0) {
            return this;
        }
        long n2 = this.getLocalMillis();
        final Chronology chronology = this.getChronology();
        for (int i = 0; i < readablePeriod.size(); ++i) {
            final long n3 = FieldUtils.safeMultiply(readablePeriod.getValue(i), n);
            final DurationFieldType fieldType = readablePeriod.getFieldType(i);
            if (this.isSupported(fieldType)) {
                n2 = fieldType.getField(chronology).add(n2, n3);
            }
        }
        return this.withLocalMillis(n2);
    }
    
    public LocalDate plus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }
    
    public LocalDate plusYears(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().years().add(this.getLocalMillis(), n));
    }
    
    public LocalDate plusMonths(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().months().add(this.getLocalMillis(), n));
    }
    
    public LocalDate plusWeeks(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().weeks().add(this.getLocalMillis(), n));
    }
    
    public LocalDate plusDays(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().days().add(this.getLocalMillis(), n));
    }
    
    public LocalDate minus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }
    
    public LocalDate minusYears(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().years().subtract(this.getLocalMillis(), n));
    }
    
    public LocalDate minusMonths(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().months().subtract(this.getLocalMillis(), n));
    }
    
    public LocalDate minusWeeks(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().weeks().subtract(this.getLocalMillis(), n));
    }
    
    public LocalDate minusDays(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().days().subtract(this.getLocalMillis(), n));
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
    
    public LocalDate withEra(final int n) {
        return this.withLocalMillis(this.getChronology().era().set(this.getLocalMillis(), n));
    }
    
    public LocalDate withCenturyOfEra(final int n) {
        return this.withLocalMillis(this.getChronology().centuryOfEra().set(this.getLocalMillis(), n));
    }
    
    public LocalDate withYearOfEra(final int n) {
        return this.withLocalMillis(this.getChronology().yearOfEra().set(this.getLocalMillis(), n));
    }
    
    public LocalDate withYearOfCentury(final int n) {
        return this.withLocalMillis(this.getChronology().yearOfCentury().set(this.getLocalMillis(), n));
    }
    
    public LocalDate withYear(final int n) {
        return this.withLocalMillis(this.getChronology().year().set(this.getLocalMillis(), n));
    }
    
    public LocalDate withWeekyear(final int n) {
        return this.withLocalMillis(this.getChronology().weekyear().set(this.getLocalMillis(), n));
    }
    
    public LocalDate withMonthOfYear(final int n) {
        return this.withLocalMillis(this.getChronology().monthOfYear().set(this.getLocalMillis(), n));
    }
    
    public LocalDate withWeekOfWeekyear(final int n) {
        return this.withLocalMillis(this.getChronology().weekOfWeekyear().set(this.getLocalMillis(), n));
    }
    
    public LocalDate withDayOfYear(final int n) {
        return this.withLocalMillis(this.getChronology().dayOfYear().set(this.getLocalMillis(), n));
    }
    
    public LocalDate withDayOfMonth(final int n) {
        return this.withLocalMillis(this.getChronology().dayOfMonth().set(this.getLocalMillis(), n));
    }
    
    public LocalDate withDayOfWeek(final int n) {
        return this.withLocalMillis(this.getChronology().dayOfWeek().set(this.getLocalMillis(), n));
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
    
    @ToString
    @Override
    public String toString() {
        return ISODateTimeFormat.date().print(this);
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
    
    static {
        (DATE_DURATION_TYPES = new HashSet<DurationFieldType>()).add(DurationFieldType.days());
        LocalDate.DATE_DURATION_TYPES.add(DurationFieldType.weeks());
        LocalDate.DATE_DURATION_TYPES.add(DurationFieldType.months());
        LocalDate.DATE_DURATION_TYPES.add(DurationFieldType.weekyears());
        LocalDate.DATE_DURATION_TYPES.add(DurationFieldType.years());
        LocalDate.DATE_DURATION_TYPES.add(DurationFieldType.centuries());
        LocalDate.DATE_DURATION_TYPES.add(DurationFieldType.eras());
    }
    
    public static final class Property extends AbstractReadableInstantFieldProperty
    {
        private static final long serialVersionUID = -3193829732634L;
        private transient LocalDate iInstant;
        private transient DateTimeField iField;
        
        Property(final LocalDate iInstant, final DateTimeField iField) {
            this.iInstant = iInstant;
            this.iField = iField;
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeObject(this.iInstant);
            objectOutputStream.writeObject(this.iField.getType());
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            this.iInstant = (LocalDate)objectInputStream.readObject();
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
        
        public LocalDate getLocalDate() {
            return this.iInstant;
        }
        
        public LocalDate addToCopy(final int n) {
            return this.iInstant.withLocalMillis(this.iField.add(this.iInstant.getLocalMillis(), n));
        }
        
        public LocalDate addWrapFieldToCopy(final int n) {
            return this.iInstant.withLocalMillis(this.iField.addWrapField(this.iInstant.getLocalMillis(), n));
        }
        
        public LocalDate setCopy(final int n) {
            return this.iInstant.withLocalMillis(this.iField.set(this.iInstant.getLocalMillis(), n));
        }
        
        public LocalDate setCopy(final String s, final Locale locale) {
            return this.iInstant.withLocalMillis(this.iField.set(this.iInstant.getLocalMillis(), s, locale));
        }
        
        public LocalDate setCopy(final String s) {
            return this.setCopy(s, null);
        }
        
        public LocalDate withMaximumValue() {
            return this.setCopy(this.getMaximumValue());
        }
        
        public LocalDate withMinimumValue() {
            return this.setCopy(this.getMinimumValue());
        }
        
        public LocalDate roundFloorCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundFloor(this.iInstant.getLocalMillis()));
        }
        
        public LocalDate roundCeilingCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundCeiling(this.iInstant.getLocalMillis()));
        }
        
        public LocalDate roundHalfFloorCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundHalfFloor(this.iInstant.getLocalMillis()));
        }
        
        public LocalDate roundHalfCeilingCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundHalfCeiling(this.iInstant.getLocalMillis()));
        }
        
        public LocalDate roundHalfEvenCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundHalfEven(this.iInstant.getLocalMillis()));
        }
    }
}
