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
import org.joda.time.convert.PartialConverter;
import org.joda.time.convert.ConverterManager;
import org.joda.time.chrono.ISOChronology;
import java.util.Date;
import java.util.Calendar;
import org.joda.time.format.DateTimeFormatter;
import org.joda.convert.FromString;
import org.joda.time.format.ISODateTimeFormat;
import java.util.Set;
import java.io.Serializable;
import org.joda.time.base.BaseLocal;

public final class LocalTime extends BaseLocal implements ReadablePartial, Serializable
{
    private static final long serialVersionUID = -12873158713873L;
    public static final LocalTime MIDNIGHT;
    private static final int HOUR_OF_DAY = 0;
    private static final int MINUTE_OF_HOUR = 1;
    private static final int SECOND_OF_MINUTE = 2;
    private static final int MILLIS_OF_SECOND = 3;
    private static final Set<DurationFieldType> TIME_DURATION_TYPES;
    private final long iLocalMillis;
    private final Chronology iChronology;
    
    public static LocalTime now() {
        return new LocalTime();
    }
    
    public static LocalTime now(final DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            throw new NullPointerException("Zone must not be null");
        }
        return new LocalTime(dateTimeZone);
    }
    
    public static LocalTime now(final Chronology chronology) {
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        return new LocalTime(chronology);
    }
    
    @FromString
    public static LocalTime parse(final String s) {
        return parse(s, ISODateTimeFormat.localTimeParser());
    }
    
    public static LocalTime parse(final String s, final DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.parseLocalTime(s);
    }
    
    public static LocalTime fromMillisOfDay(final long n) {
        return fromMillisOfDay(n, null);
    }
    
    public static LocalTime fromMillisOfDay(final long n, Chronology withUTC) {
        withUTC = DateTimeUtils.getChronology(withUTC).withUTC();
        return new LocalTime(n, withUTC);
    }
    
    public static LocalTime fromCalendarFields(final Calendar calendar) {
        if (calendar == null) {
            throw new IllegalArgumentException("The calendar must not be null");
        }
        return new LocalTime(calendar.get(11), calendar.get(12), calendar.get(13), calendar.get(14));
    }
    
    public static LocalTime fromDateFields(final Date date) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return new LocalTime(date.getHours(), date.getMinutes(), date.getSeconds(), ((int)(date.getTime() % 1000L) + 1000) % 1000);
    }
    
    public LocalTime() {
        this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance());
    }
    
    public LocalTime(final DateTimeZone dateTimeZone) {
        this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance(dateTimeZone));
    }
    
    public LocalTime(final Chronology chronology) {
        this(DateTimeUtils.currentTimeMillis(), chronology);
    }
    
    public LocalTime(final long n) {
        this(n, ISOChronology.getInstance());
    }
    
    public LocalTime(final long n, final DateTimeZone dateTimeZone) {
        this(n, ISOChronology.getInstance(dateTimeZone));
    }
    
    public LocalTime(final long n, Chronology iChronology) {
        iChronology = DateTimeUtils.getChronology(iChronology);
        final long millisKeepLocal = iChronology.getZone().getMillisKeepLocal(DateTimeZone.UTC, n);
        iChronology = iChronology.withUTC();
        this.iLocalMillis = iChronology.millisOfDay().get(millisKeepLocal);
        this.iChronology = iChronology;
    }
    
    public LocalTime(final Object o) {
        this(o, (Chronology)null);
    }
    
    public LocalTime(final Object o, final DateTimeZone dateTimeZone) {
        final PartialConverter partialConverter = ConverterManager.getInstance().getPartialConverter(o);
        final Chronology chronology = DateTimeUtils.getChronology(partialConverter.getChronology(o, dateTimeZone));
        this.iChronology = chronology.withUTC();
        final int[] partialValues = partialConverter.getPartialValues(this, o, chronology, ISODateTimeFormat.localTimeParser());
        this.iLocalMillis = this.iChronology.getDateTimeMillis(0L, partialValues[0], partialValues[1], partialValues[2], partialValues[3]);
    }
    
    public LocalTime(final Object o, Chronology chronology) {
        final PartialConverter partialConverter = ConverterManager.getInstance().getPartialConverter(o);
        chronology = partialConverter.getChronology(o, chronology);
        chronology = DateTimeUtils.getChronology(chronology);
        this.iChronology = chronology.withUTC();
        final int[] partialValues = partialConverter.getPartialValues(this, o, chronology, ISODateTimeFormat.localTimeParser());
        this.iLocalMillis = this.iChronology.getDateTimeMillis(0L, partialValues[0], partialValues[1], partialValues[2], partialValues[3]);
    }
    
    public LocalTime(final int n, final int n2) {
        this(n, n2, 0, 0, ISOChronology.getInstanceUTC());
    }
    
    public LocalTime(final int n, final int n2, final int n3) {
        this(n, n2, n3, 0, ISOChronology.getInstanceUTC());
    }
    
    public LocalTime(final int n, final int n2, final int n3, final int n4) {
        this(n, n2, n3, n4, ISOChronology.getInstanceUTC());
    }
    
    public LocalTime(final int n, final int n2, final int n3, final int n4, Chronology withUTC) {
        withUTC = DateTimeUtils.getChronology(withUTC).withUTC();
        final long dateTimeMillis = withUTC.getDateTimeMillis(0L, n, n2, n3, n4);
        this.iChronology = withUTC;
        this.iLocalMillis = dateTimeMillis;
    }
    
    private Object readResolve() {
        if (this.iChronology == null) {
            return new LocalTime(this.iLocalMillis, ISOChronology.getInstanceUTC());
        }
        if (!DateTimeZone.UTC.equals(this.iChronology.getZone())) {
            return new LocalTime(this.iLocalMillis, this.iChronology.withUTC());
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
    
    public int getValue(final int i) {
        switch (i) {
            case 0: {
                return this.getChronology().hourOfDay().get(this.getLocalMillis());
            }
            case 1: {
                return this.getChronology().minuteOfHour().get(this.getLocalMillis());
            }
            case 2: {
                return this.getChronology().secondOfMinute().get(this.getLocalMillis());
            }
            case 3: {
                return this.getChronology().millisOfSecond().get(this.getLocalMillis());
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
        if (!this.isSupported(dateTimeFieldType.getDurationType())) {
            return false;
        }
        final DurationFieldType rangeDurationType = dateTimeFieldType.getRangeDurationType();
        return this.isSupported(rangeDurationType) || rangeDurationType == DurationFieldType.days();
    }
    
    public boolean isSupported(final DurationFieldType durationFieldType) {
        if (durationFieldType == null) {
            return false;
        }
        final DurationField field = durationFieldType.getField(this.getChronology());
        return (LocalTime.TIME_DURATION_TYPES.contains(durationFieldType) || field.getUnitMillis() < this.getChronology().days().getUnitMillis()) && field.isSupported();
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
        if (o instanceof LocalTime) {
            final LocalTime localTime = (LocalTime)o;
            if (this.iChronology.equals(localTime.iChronology)) {
                return this.iLocalMillis == localTime.iLocalMillis;
            }
        }
        return super.equals(o);
    }
    
    @Override
    public int compareTo(final ReadablePartial readablePartial) {
        if (this == readablePartial) {
            return 0;
        }
        if (readablePartial instanceof LocalTime) {
            final LocalTime localTime = (LocalTime)readablePartial;
            if (this.iChronology.equals(localTime.iChronology)) {
                return (this.iLocalMillis < localTime.iLocalMillis) ? -1 : ((this.iLocalMillis == localTime.iLocalMillis) ? false : true);
            }
        }
        return super.compareTo(readablePartial);
    }
    
    LocalTime withLocalMillis(final long n) {
        return (n == this.getLocalMillis()) ? this : new LocalTime(n, this.getChronology());
    }
    
    public LocalTime withFields(final ReadablePartial readablePartial) {
        if (readablePartial == null) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().set(readablePartial, this.getLocalMillis()));
    }
    
    public LocalTime withField(final DateTimeFieldType obj, final int n) {
        if (obj == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        if (!this.isSupported(obj)) {
            throw new IllegalArgumentException("Field '" + obj + "' is not supported");
        }
        return this.withLocalMillis(obj.getField(this.getChronology()).set(this.getLocalMillis(), n));
    }
    
    public LocalTime withFieldAdded(final DurationFieldType obj, final int n) {
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
    
    public LocalTime withPeriodAdded(final ReadablePeriod readablePeriod, final int n) {
        if (readablePeriod == null || n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().add(readablePeriod, this.getLocalMillis(), n));
    }
    
    public LocalTime plus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }
    
    public LocalTime plusHours(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().hours().add(this.getLocalMillis(), n));
    }
    
    public LocalTime plusMinutes(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().minutes().add(this.getLocalMillis(), n));
    }
    
    public LocalTime plusSeconds(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().seconds().add(this.getLocalMillis(), n));
    }
    
    public LocalTime plusMillis(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().millis().add(this.getLocalMillis(), n));
    }
    
    public LocalTime minus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }
    
    public LocalTime minusHours(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().hours().subtract(this.getLocalMillis(), n));
    }
    
    public LocalTime minusMinutes(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().minutes().subtract(this.getLocalMillis(), n));
    }
    
    public LocalTime minusSeconds(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withLocalMillis(this.getChronology().seconds().subtract(this.getLocalMillis(), n));
    }
    
    public LocalTime minusMillis(final int n) {
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
    
    public LocalTime withHourOfDay(final int n) {
        return this.withLocalMillis(this.getChronology().hourOfDay().set(this.getLocalMillis(), n));
    }
    
    public LocalTime withMinuteOfHour(final int n) {
        return this.withLocalMillis(this.getChronology().minuteOfHour().set(this.getLocalMillis(), n));
    }
    
    public LocalTime withSecondOfMinute(final int n) {
        return this.withLocalMillis(this.getChronology().secondOfMinute().set(this.getLocalMillis(), n));
    }
    
    public LocalTime withMillisOfSecond(final int n) {
        return this.withLocalMillis(this.getChronology().millisOfSecond().set(this.getLocalMillis(), n));
    }
    
    public LocalTime withMillisOfDay(final int n) {
        return this.withLocalMillis(this.getChronology().millisOfDay().set(this.getLocalMillis(), n));
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
    
    public DateTime toDateTimeToday() {
        return this.toDateTimeToday(null);
    }
    
    public DateTime toDateTimeToday(final DateTimeZone dateTimeZone) {
        final Chronology withZone = this.getChronology().withZone(dateTimeZone);
        return new DateTime(withZone.set(this, DateTimeUtils.currentTimeMillis()), withZone);
    }
    
    @ToString
    @Override
    public String toString() {
        return ISODateTimeFormat.time().print(this);
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
        MIDNIGHT = new LocalTime(0, 0, 0, 0);
        (TIME_DURATION_TYPES = new HashSet<DurationFieldType>()).add(DurationFieldType.millis());
        LocalTime.TIME_DURATION_TYPES.add(DurationFieldType.seconds());
        LocalTime.TIME_DURATION_TYPES.add(DurationFieldType.minutes());
        LocalTime.TIME_DURATION_TYPES.add(DurationFieldType.hours());
    }
    
    public static final class Property extends AbstractReadableInstantFieldProperty
    {
        private static final long serialVersionUID = -325842547277223L;
        private transient LocalTime iInstant;
        private transient DateTimeField iField;
        
        Property(final LocalTime iInstant, final DateTimeField iField) {
            this.iInstant = iInstant;
            this.iField = iField;
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeObject(this.iInstant);
            objectOutputStream.writeObject(this.iField.getType());
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            this.iInstant = (LocalTime)objectInputStream.readObject();
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
        
        public LocalTime getLocalTime() {
            return this.iInstant;
        }
        
        public LocalTime addCopy(final int n) {
            return this.iInstant.withLocalMillis(this.iField.add(this.iInstant.getLocalMillis(), n));
        }
        
        public LocalTime addCopy(final long n) {
            return this.iInstant.withLocalMillis(this.iField.add(this.iInstant.getLocalMillis(), n));
        }
        
        public LocalTime addNoWrapToCopy(final int n) {
            final long add = this.iField.add(this.iInstant.getLocalMillis(), n);
            if (this.iInstant.getChronology().millisOfDay().get(add) != add) {
                throw new IllegalArgumentException("The addition exceeded the boundaries of LocalTime");
            }
            return this.iInstant.withLocalMillis(add);
        }
        
        public LocalTime addWrapFieldToCopy(final int n) {
            return this.iInstant.withLocalMillis(this.iField.addWrapField(this.iInstant.getLocalMillis(), n));
        }
        
        public LocalTime setCopy(final int n) {
            return this.iInstant.withLocalMillis(this.iField.set(this.iInstant.getLocalMillis(), n));
        }
        
        public LocalTime setCopy(final String s, final Locale locale) {
            return this.iInstant.withLocalMillis(this.iField.set(this.iInstant.getLocalMillis(), s, locale));
        }
        
        public LocalTime setCopy(final String s) {
            return this.setCopy(s, null);
        }
        
        public LocalTime withMaximumValue() {
            return this.setCopy(this.getMaximumValue());
        }
        
        public LocalTime withMinimumValue() {
            return this.setCopy(this.getMinimumValue());
        }
        
        public LocalTime roundFloorCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundFloor(this.iInstant.getLocalMillis()));
        }
        
        public LocalTime roundCeilingCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundCeiling(this.iInstant.getLocalMillis()));
        }
        
        public LocalTime roundHalfFloorCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundHalfFloor(this.iInstant.getLocalMillis()));
        }
        
        public LocalTime roundHalfCeilingCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundHalfCeiling(this.iInstant.getLocalMillis()));
        }
        
        public LocalTime roundHalfEvenCopy() {
            return this.iInstant.withLocalMillis(this.iField.roundHalfEven(this.iInstant.getLocalMillis()));
        }
    }
}
