// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import java.util.Locale;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.joda.time.field.AbstractReadableInstantFieldProperty;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormatter;
import org.joda.convert.FromString;
import org.joda.time.format.ISODateTimeFormat;
import java.io.Serializable;
import org.joda.time.base.BaseDateTime;

public final class DateTime extends BaseDateTime implements ReadableDateTime, Serializable
{
    private static final long serialVersionUID = -5171125899451703815L;
    
    public static DateTime now() {
        return new DateTime();
    }
    
    public static DateTime now(final DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            throw new NullPointerException("Zone must not be null");
        }
        return new DateTime(dateTimeZone);
    }
    
    public static DateTime now(final Chronology chronology) {
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        return new DateTime(chronology);
    }
    
    @FromString
    public static DateTime parse(final String s) {
        return parse(s, ISODateTimeFormat.dateTimeParser().withOffsetParsed());
    }
    
    public static DateTime parse(final String s, final DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.parseDateTime(s);
    }
    
    public DateTime() {
    }
    
    public DateTime(final DateTimeZone dateTimeZone) {
        super(dateTimeZone);
    }
    
    public DateTime(final Chronology chronology) {
        super(chronology);
    }
    
    public DateTime(final long n) {
        super(n);
    }
    
    public DateTime(final long n, final DateTimeZone dateTimeZone) {
        super(n, dateTimeZone);
    }
    
    public DateTime(final long n, final Chronology chronology) {
        super(n, chronology);
    }
    
    public DateTime(final Object o) {
        super(o, (Chronology)null);
    }
    
    public DateTime(final Object o, final DateTimeZone dateTimeZone) {
        super(o, dateTimeZone);
    }
    
    public DateTime(final Object o, final Chronology chronology) {
        super(o, DateTimeUtils.getChronology(chronology));
    }
    
    public DateTime(final int n, final int n2, final int n3, final int n4, final int n5) {
        super(n, n2, n3, n4, n5, 0, 0);
    }
    
    public DateTime(final int n, final int n2, final int n3, final int n4, final int n5, final DateTimeZone dateTimeZone) {
        super(n, n2, n3, n4, n5, 0, 0, dateTimeZone);
    }
    
    public DateTime(final int n, final int n2, final int n3, final int n4, final int n5, final Chronology chronology) {
        super(n, n2, n3, n4, n5, 0, 0, chronology);
    }
    
    public DateTime(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        super(n, n2, n3, n4, n5, n6, 0);
    }
    
    public DateTime(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final DateTimeZone dateTimeZone) {
        super(n, n2, n3, n4, n5, n6, 0, dateTimeZone);
    }
    
    public DateTime(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final Chronology chronology) {
        super(n, n2, n3, n4, n5, n6, 0, chronology);
    }
    
    public DateTime(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        super(n, n2, n3, n4, n5, n6, n7);
    }
    
    public DateTime(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final DateTimeZone dateTimeZone) {
        super(n, n2, n3, n4, n5, n6, n7, dateTimeZone);
    }
    
    public DateTime(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final Chronology chronology) {
        super(n, n2, n3, n4, n5, n6, n7, chronology);
    }
    
    @Override
    public DateTime toDateTime() {
        return this;
    }
    
    @Override
    public DateTime toDateTimeISO() {
        if (this.getChronology() == ISOChronology.getInstance()) {
            return this;
        }
        return super.toDateTimeISO();
    }
    
    @Override
    public DateTime toDateTime(DateTimeZone zone) {
        zone = DateTimeUtils.getZone(zone);
        if (this.getZone() == zone) {
            return this;
        }
        return super.toDateTime(zone);
    }
    
    @Override
    public DateTime toDateTime(Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        if (this.getChronology() == chronology) {
            return this;
        }
        return super.toDateTime(chronology);
    }
    
    public DateTime withMillis(final long n) {
        return (n == this.getMillis()) ? this : new DateTime(n, this.getChronology());
    }
    
    public DateTime withChronology(Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        return (chronology == this.getChronology()) ? this : new DateTime(this.getMillis(), chronology);
    }
    
    public DateTime withZone(final DateTimeZone dateTimeZone) {
        return this.withChronology(this.getChronology().withZone(dateTimeZone));
    }
    
    public DateTime withZoneRetainFields(DateTimeZone zone) {
        zone = DateTimeUtils.getZone(zone);
        final DateTimeZone zone2 = DateTimeUtils.getZone(this.getZone());
        if (zone == zone2) {
            return this;
        }
        return new DateTime(zone2.getMillisKeepLocal(zone, this.getMillis()), this.getChronology().withZone(zone));
    }
    
    public DateTime withEarlierOffsetAtOverlap() {
        return this.withMillis(this.getZone().adjustOffset(this.getMillis(), false));
    }
    
    public DateTime withLaterOffsetAtOverlap() {
        return this.withMillis(this.getZone().adjustOffset(this.getMillis(), true));
    }
    
    public DateTime withDate(final int n, final int n2, final int n3) {
        final Chronology chronology = this.getChronology();
        return this.withMillis(chronology.dayOfMonth().set(chronology.monthOfYear().set(chronology.year().set(this.getMillis(), n), n2), n3));
    }
    
    public DateTime withTime(final int n, final int n2, final int n3, final int n4) {
        final Chronology chronology = this.getChronology();
        return this.withMillis(chronology.millisOfSecond().set(chronology.secondOfMinute().set(chronology.minuteOfHour().set(chronology.hourOfDay().set(this.getMillis(), n), n2), n3), n4));
    }
    
    public DateTime withTimeAtStartOfDay() {
        return this.toLocalDate().toDateTimeAtStartOfDay(this.getZone());
    }
    
    public DateTime withFields(final ReadablePartial readablePartial) {
        if (readablePartial == null) {
            return this;
        }
        return this.withMillis(this.getChronology().set(readablePartial, this.getMillis()));
    }
    
    public DateTime withField(final DateTimeFieldType dateTimeFieldType, final int n) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        return this.withMillis(dateTimeFieldType.getField(this.getChronology()).set(this.getMillis(), n));
    }
    
    public DateTime withFieldAdded(final DurationFieldType durationFieldType, final int n) {
        if (durationFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        if (n == 0) {
            return this;
        }
        return this.withMillis(durationFieldType.getField(this.getChronology()).add(this.getMillis(), n));
    }
    
    public DateTime withDurationAdded(final long n, final int n2) {
        if (n == 0L || n2 == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().add(this.getMillis(), n, n2));
    }
    
    public DateTime withDurationAdded(final ReadableDuration readableDuration, final int n) {
        if (readableDuration == null || n == 0) {
            return this;
        }
        return this.withDurationAdded(readableDuration.getMillis(), n);
    }
    
    public DateTime withPeriodAdded(final ReadablePeriod readablePeriod, final int n) {
        if (readablePeriod == null || n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().add(readablePeriod, this.getMillis(), n));
    }
    
    public DateTime plus(final long n) {
        return this.withDurationAdded(n, 1);
    }
    
    public DateTime plus(final ReadableDuration readableDuration) {
        return this.withDurationAdded(readableDuration, 1);
    }
    
    public DateTime plus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }
    
    public DateTime plusYears(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().years().add(this.getMillis(), n));
    }
    
    public DateTime plusMonths(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().months().add(this.getMillis(), n));
    }
    
    public DateTime plusWeeks(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().weeks().add(this.getMillis(), n));
    }
    
    public DateTime plusDays(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().days().add(this.getMillis(), n));
    }
    
    public DateTime plusHours(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().hours().add(this.getMillis(), n));
    }
    
    public DateTime plusMinutes(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().minutes().add(this.getMillis(), n));
    }
    
    public DateTime plusSeconds(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().seconds().add(this.getMillis(), n));
    }
    
    public DateTime plusMillis(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().millis().add(this.getMillis(), n));
    }
    
    public DateTime minus(final long n) {
        return this.withDurationAdded(n, -1);
    }
    
    public DateTime minus(final ReadableDuration readableDuration) {
        return this.withDurationAdded(readableDuration, -1);
    }
    
    public DateTime minus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }
    
    public DateTime minusYears(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().years().subtract(this.getMillis(), n));
    }
    
    public DateTime minusMonths(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().months().subtract(this.getMillis(), n));
    }
    
    public DateTime minusWeeks(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().weeks().subtract(this.getMillis(), n));
    }
    
    public DateTime minusDays(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().days().subtract(this.getMillis(), n));
    }
    
    public DateTime minusHours(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().hours().subtract(this.getMillis(), n));
    }
    
    public DateTime minusMinutes(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().minutes().subtract(this.getMillis(), n));
    }
    
    public DateTime minusSeconds(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().seconds().subtract(this.getMillis(), n));
    }
    
    public DateTime minusMillis(final int n) {
        if (n == 0) {
            return this;
        }
        return this.withMillis(this.getChronology().millis().subtract(this.getMillis(), n));
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
    public DateMidnight toDateMidnight() {
        return new DateMidnight(this.getMillis(), this.getChronology());
    }
    
    @Deprecated
    public YearMonthDay toYearMonthDay() {
        return new YearMonthDay(this.getMillis(), this.getChronology());
    }
    
    @Deprecated
    public TimeOfDay toTimeOfDay() {
        return new TimeOfDay(this.getMillis(), this.getChronology());
    }
    
    public LocalDateTime toLocalDateTime() {
        return new LocalDateTime(this.getMillis(), this.getChronology());
    }
    
    public LocalDate toLocalDate() {
        return new LocalDate(this.getMillis(), this.getChronology());
    }
    
    public LocalTime toLocalTime() {
        return new LocalTime(this.getMillis(), this.getChronology());
    }
    
    public DateTime withEra(final int n) {
        return this.withMillis(this.getChronology().era().set(this.getMillis(), n));
    }
    
    public DateTime withCenturyOfEra(final int n) {
        return this.withMillis(this.getChronology().centuryOfEra().set(this.getMillis(), n));
    }
    
    public DateTime withYearOfEra(final int n) {
        return this.withMillis(this.getChronology().yearOfEra().set(this.getMillis(), n));
    }
    
    public DateTime withYearOfCentury(final int n) {
        return this.withMillis(this.getChronology().yearOfCentury().set(this.getMillis(), n));
    }
    
    public DateTime withYear(final int n) {
        return this.withMillis(this.getChronology().year().set(this.getMillis(), n));
    }
    
    public DateTime withWeekyear(final int n) {
        return this.withMillis(this.getChronology().weekyear().set(this.getMillis(), n));
    }
    
    public DateTime withMonthOfYear(final int n) {
        return this.withMillis(this.getChronology().monthOfYear().set(this.getMillis(), n));
    }
    
    public DateTime withWeekOfWeekyear(final int n) {
        return this.withMillis(this.getChronology().weekOfWeekyear().set(this.getMillis(), n));
    }
    
    public DateTime withDayOfYear(final int n) {
        return this.withMillis(this.getChronology().dayOfYear().set(this.getMillis(), n));
    }
    
    public DateTime withDayOfMonth(final int n) {
        return this.withMillis(this.getChronology().dayOfMonth().set(this.getMillis(), n));
    }
    
    public DateTime withDayOfWeek(final int n) {
        return this.withMillis(this.getChronology().dayOfWeek().set(this.getMillis(), n));
    }
    
    public DateTime withHourOfDay(final int n) {
        return this.withMillis(this.getChronology().hourOfDay().set(this.getMillis(), n));
    }
    
    public DateTime withMinuteOfHour(final int n) {
        return this.withMillis(this.getChronology().minuteOfHour().set(this.getMillis(), n));
    }
    
    public DateTime withSecondOfMinute(final int n) {
        return this.withMillis(this.getChronology().secondOfMinute().set(this.getMillis(), n));
    }
    
    public DateTime withMillisOfSecond(final int n) {
        return this.withMillis(this.getChronology().millisOfSecond().set(this.getMillis(), n));
    }
    
    public DateTime withMillisOfDay(final int n) {
        return this.withMillis(this.getChronology().millisOfDay().set(this.getMillis(), n));
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
    
    public Property minuteOfDay() {
        return new Property(this, this.getChronology().minuteOfDay());
    }
    
    public Property minuteOfHour() {
        return new Property(this, this.getChronology().minuteOfHour());
    }
    
    public Property secondOfDay() {
        return new Property(this, this.getChronology().secondOfDay());
    }
    
    public Property secondOfMinute() {
        return new Property(this, this.getChronology().secondOfMinute());
    }
    
    public Property millisOfDay() {
        return new Property(this, this.getChronology().millisOfDay());
    }
    
    public Property millisOfSecond() {
        return new Property(this, this.getChronology().millisOfSecond());
    }
    
    public static final class Property extends AbstractReadableInstantFieldProperty
    {
        private static final long serialVersionUID = -6983323811635733510L;
        private DateTime iInstant;
        private DateTimeField iField;
        
        Property(final DateTime iInstant, final DateTimeField iField) {
            this.iInstant = iInstant;
            this.iField = iField;
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeObject(this.iInstant);
            objectOutputStream.writeObject(this.iField.getType());
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            this.iInstant = (DateTime)objectInputStream.readObject();
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
        
        public DateTime getDateTime() {
            return this.iInstant;
        }
        
        public DateTime addToCopy(final int n) {
            return this.iInstant.withMillis(this.iField.add(this.iInstant.getMillis(), n));
        }
        
        public DateTime addToCopy(final long n) {
            return this.iInstant.withMillis(this.iField.add(this.iInstant.getMillis(), n));
        }
        
        public DateTime addWrapFieldToCopy(final int n) {
            return this.iInstant.withMillis(this.iField.addWrapField(this.iInstant.getMillis(), n));
        }
        
        public DateTime setCopy(final int n) {
            return this.iInstant.withMillis(this.iField.set(this.iInstant.getMillis(), n));
        }
        
        public DateTime setCopy(final String s, final Locale locale) {
            return this.iInstant.withMillis(this.iField.set(this.iInstant.getMillis(), s, locale));
        }
        
        public DateTime setCopy(final String s) {
            return this.setCopy(s, null);
        }
        
        public DateTime withMaximumValue() {
            try {
                return this.setCopy(this.getMaximumValue());
            }
            catch (RuntimeException ex) {
                if (IllegalInstantException.isIllegalInstant(ex)) {
                    return new DateTime(this.getChronology().getZone().previousTransition(this.getMillis() + 86400000L), this.getChronology());
                }
                throw ex;
            }
        }
        
        public DateTime withMinimumValue() {
            try {
                return this.setCopy(this.getMinimumValue());
            }
            catch (RuntimeException ex) {
                if (IllegalInstantException.isIllegalInstant(ex)) {
                    return new DateTime(this.getChronology().getZone().nextTransition(this.getMillis() - 86400000L), this.getChronology());
                }
                throw ex;
            }
        }
        
        public DateTime roundFloorCopy() {
            return this.iInstant.withMillis(this.iField.roundFloor(this.iInstant.getMillis()));
        }
        
        public DateTime roundCeilingCopy() {
            return this.iInstant.withMillis(this.iField.roundCeiling(this.iInstant.getMillis()));
        }
        
        public DateTime roundHalfFloorCopy() {
            return this.iInstant.withMillis(this.iField.roundHalfFloor(this.iInstant.getMillis()));
        }
        
        public DateTime roundHalfCeilingCopy() {
            return this.iInstant.withMillis(this.iField.roundHalfCeiling(this.iInstant.getMillis()));
        }
        
        public DateTime roundHalfEvenCopy() {
            return this.iInstant.withMillis(this.iField.roundHalfEven(this.iInstant.getMillis()));
        }
    }
}
