// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import java.util.Locale;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.joda.time.field.AbstractReadableInstantFieldProperty;
import org.joda.time.format.DateTimeFormatter;
import org.joda.convert.FromString;
import org.joda.time.format.ISODateTimeFormat;
import java.io.Serializable;
import org.joda.time.base.BaseDateTime;

@Deprecated
public final class DateMidnight extends BaseDateTime implements ReadableDateTime, Serializable
{
    private static final long serialVersionUID = 156371964018738L;
    
    public static DateMidnight now() {
        return new DateMidnight();
    }
    
    public static DateMidnight now(final DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            throw new NullPointerException("Zone must not be null");
        }
        return new DateMidnight(dateTimeZone);
    }
    
    public static DateMidnight now(final Chronology chronology) {
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        return new DateMidnight(chronology);
    }
    
    @FromString
    public static DateMidnight parse(final String s) {
        return parse(s, ISODateTimeFormat.dateTimeParser().withOffsetParsed());
    }
    
    public static DateMidnight parse(final String s, final DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.parseDateTime(s).toDateMidnight();
    }
    
    public DateMidnight() {
    }
    
    public DateMidnight(final DateTimeZone dateTimeZone) {
        super(dateTimeZone);
    }
    
    public DateMidnight(final Chronology chronology) {
        super(chronology);
    }
    
    public DateMidnight(final long n) {
        super(n);
    }
    
    public DateMidnight(final long n, final DateTimeZone dateTimeZone) {
        super(n, dateTimeZone);
    }
    
    public DateMidnight(final long n, final Chronology chronology) {
        super(n, chronology);
    }
    
    public DateMidnight(final Object o) {
        super(o, (Chronology)null);
    }
    
    public DateMidnight(final Object o, final DateTimeZone dateTimeZone) {
        super(o, dateTimeZone);
    }
    
    public DateMidnight(final Object o, final Chronology chronology) {
        super(o, DateTimeUtils.getChronology(chronology));
    }
    
    public DateMidnight(final int n, final int n2, final int n3) {
        super(n, n2, n3, 0, 0, 0, 0);
    }
    
    public DateMidnight(final int n, final int n2, final int n3, final DateTimeZone dateTimeZone) {
        super(n, n2, n3, 0, 0, 0, 0, dateTimeZone);
    }
    
    public DateMidnight(final int n, final int n2, final int n3, final Chronology chronology) {
        super(n, n2, n3, 0, 0, 0, 0, chronology);
    }
    
    @Override
    protected long checkInstant(final long n, final Chronology chronology) {
        return chronology.dayOfMonth().roundFloor(n);
    }
    
    public DateMidnight withMillis(long checkInstant) {
        final Chronology chronology = this.getChronology();
        checkInstant = this.checkInstant(checkInstant, chronology);
        return (checkInstant == this.getMillis()) ? this : new DateMidnight(checkInstant, chronology);
    }
    
    public DateMidnight withChronology(final Chronology chronology) {
        return (chronology == this.getChronology()) ? this : new DateMidnight(this.getMillis(), chronology);
    }
    
    public DateMidnight withZoneRetainFields(DateTimeZone zone) {
        zone = DateTimeUtils.getZone(zone);
        final DateTimeZone zone2 = DateTimeUtils.getZone(this.getZone());
        if (zone == zone2) {
            return this;
        }
        return new DateMidnight(zone2.getMillisKeepLocal(zone, this.getMillis()), this.getChronology().withZone(zone));
    }
    
    public DateMidnight withFields(final ReadablePartial readablePartial) {
        if (readablePartial == null) {
            return this;
        }
        return this.withMillis(this.getChronology().set(readablePartial, this.getMillis()));
    }
    
    public DateMidnight withField(final DateTimeFieldType dateTimeFieldType, final int n) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        return this.withMillis(dateTimeFieldType.getField(this.getChronology()).set(this.getMillis(), n));
    }
    
    public DateMidnight withFieldAdded(final DurationFieldType durationFieldType, final int n) {
        if (durationFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        if (n == 0) {
            return this;
        }
        return this.withMillis(durationFieldType.getField(this.getChronology()).add(this.getMillis(), n));
    }
    
    public DateMidnight withDurationAdded(final long n, final int n2) {
        if (n == 0L || n2 == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().add(this.getMillis(), n, n2));
    }
    
    public DateMidnight withDurationAdded(final ReadableDuration readableDuration, final int n) {
        if (readableDuration == null || n == 0) {
            return this;
        }
        return this.withDurationAdded(readableDuration.getMillis(), n);
    }
    
    public DateMidnight withPeriodAdded(final ReadablePeriod readablePeriod, final int n) {
        if (readablePeriod == null || n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().add(readablePeriod, this.getMillis(), n));
    }
    
    public DateMidnight plus(final long n) {
        return this.withDurationAdded(n, 1);
    }
    
    public DateMidnight plus(final ReadableDuration readableDuration) {
        return this.withDurationAdded(readableDuration, 1);
    }
    
    public DateMidnight plus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }
    
    public DateMidnight plusYears(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().years().add(this.getMillis(), n));
    }
    
    public DateMidnight plusMonths(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().months().add(this.getMillis(), n));
    }
    
    public DateMidnight plusWeeks(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().weeks().add(this.getMillis(), n));
    }
    
    public DateMidnight plusDays(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().days().add(this.getMillis(), n));
    }
    
    public DateMidnight minus(final long n) {
        return this.withDurationAdded(n, -1);
    }
    
    public DateMidnight minus(final ReadableDuration readableDuration) {
        return this.withDurationAdded(readableDuration, -1);
    }
    
    public DateMidnight minus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }
    
    public DateMidnight minusYears(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().years().subtract(this.getMillis(), n));
    }
    
    public DateMidnight minusMonths(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().months().subtract(this.getMillis(), n));
    }
    
    public DateMidnight minusWeeks(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().weeks().subtract(this.getMillis(), n));
    }
    
    public DateMidnight minusDays(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().days().subtract(this.getMillis(), n));
    }
    
    public Property property(final DateTimeFieldType obj) {
        if (obj == null) {
            throw new IllegalArgumentException("The DateTimeFieldType must not be null");
        }
        final DateTimeField field = obj.getField(this.getChronology());
        if (!field.isSupported()) {
            throw new IllegalArgumentException("Field '" + obj + "' is not supported");
        }
        return new Property(this, field);
    }
    
    @Deprecated
    public YearMonthDay toYearMonthDay() {
        return new YearMonthDay(this.getMillis(), this.getChronology());
    }
    
    public LocalDate toLocalDate() {
        return new LocalDate(this.getMillis(), this.getChronology());
    }
    
    public Interval toInterval() {
        final Chronology chronology = this.getChronology();
        final long millis = this.getMillis();
        return new Interval(millis, DurationFieldType.days().getField(chronology).add(millis, 1), chronology);
    }
    
    public DateMidnight withEra(final int n) {
        return this.withMillis(this.getChronology().era().set(this.getMillis(), n));
    }
    
    public DateMidnight withCenturyOfEra(final int n) {
        return this.withMillis(this.getChronology().centuryOfEra().set(this.getMillis(), n));
    }
    
    public DateMidnight withYearOfEra(final int n) {
        return this.withMillis(this.getChronology().yearOfEra().set(this.getMillis(), n));
    }
    
    public DateMidnight withYearOfCentury(final int n) {
        return this.withMillis(this.getChronology().yearOfCentury().set(this.getMillis(), n));
    }
    
    public DateMidnight withYear(final int n) {
        return this.withMillis(this.getChronology().year().set(this.getMillis(), n));
    }
    
    public DateMidnight withWeekyear(final int n) {
        return this.withMillis(this.getChronology().weekyear().set(this.getMillis(), n));
    }
    
    public DateMidnight withMonthOfYear(final int n) {
        return this.withMillis(this.getChronology().monthOfYear().set(this.getMillis(), n));
    }
    
    public DateMidnight withWeekOfWeekyear(final int n) {
        return this.withMillis(this.getChronology().weekOfWeekyear().set(this.getMillis(), n));
    }
    
    public DateMidnight withDayOfYear(final int n) {
        return this.withMillis(this.getChronology().dayOfYear().set(this.getMillis(), n));
    }
    
    public DateMidnight withDayOfMonth(final int n) {
        return this.withMillis(this.getChronology().dayOfMonth().set(this.getMillis(), n));
    }
    
    public DateMidnight withDayOfWeek(final int n) {
        return this.withMillis(this.getChronology().dayOfWeek().set(this.getMillis(), n));
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
    
    public static final class Property extends AbstractReadableInstantFieldProperty
    {
        private static final long serialVersionUID = 257629620L;
        private DateMidnight iInstant;
        private DateTimeField iField;
        
        Property(final DateMidnight iInstant, final DateTimeField iField) {
            this.iInstant = iInstant;
            this.iField = iField;
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeObject(this.iInstant);
            objectOutputStream.writeObject(this.iField.getType());
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            this.iInstant = (DateMidnight)objectInputStream.readObject();
            this.iField = ((DateTimeFieldType)objectInputStream.readObject()).getField(this.iInstant.getChronology());
        }
        
        @Override
        public DateTimeField getField() {
            return this.iField;
        }
        
        @Override
        protected long getMillis() {
            return this.iInstant.getMillis();
        }
        
        @Override
        protected Chronology getChronology() {
            return this.iInstant.getChronology();
        }
        
        public DateMidnight getDateMidnight() {
            return this.iInstant;
        }
        
        public DateMidnight addToCopy(final int n) {
            return this.iInstant.withMillis(this.iField.add(this.iInstant.getMillis(), n));
        }
        
        public DateMidnight addToCopy(final long n) {
            return this.iInstant.withMillis(this.iField.add(this.iInstant.getMillis(), n));
        }
        
        public DateMidnight addWrapFieldToCopy(final int n) {
            return this.iInstant.withMillis(this.iField.addWrapField(this.iInstant.getMillis(), n));
        }
        
        public DateMidnight setCopy(final int n) {
            return this.iInstant.withMillis(this.iField.set(this.iInstant.getMillis(), n));
        }
        
        public DateMidnight setCopy(final String s, final Locale locale) {
            return this.iInstant.withMillis(this.iField.set(this.iInstant.getMillis(), s, locale));
        }
        
        public DateMidnight setCopy(final String s) {
            return this.setCopy(s, null);
        }
        
        public DateMidnight withMaximumValue() {
            return this.setCopy(this.getMaximumValue());
        }
        
        public DateMidnight withMinimumValue() {
            return this.setCopy(this.getMinimumValue());
        }
        
        public DateMidnight roundFloorCopy() {
            return this.iInstant.withMillis(this.iField.roundFloor(this.iInstant.getMillis()));
        }
        
        public DateMidnight roundCeilingCopy() {
            return this.iInstant.withMillis(this.iField.roundCeiling(this.iInstant.getMillis()));
        }
        
        public DateMidnight roundHalfFloorCopy() {
            return this.iInstant.withMillis(this.iField.roundHalfFloor(this.iInstant.getMillis()));
        }
        
        public DateMidnight roundHalfCeilingCopy() {
            return this.iInstant.withMillis(this.iField.roundHalfCeiling(this.iInstant.getMillis()));
        }
        
        public DateMidnight roundHalfEvenCopy() {
            return this.iInstant.withMillis(this.iField.roundHalfEven(this.iInstant.getMillis()));
        }
    }
}
