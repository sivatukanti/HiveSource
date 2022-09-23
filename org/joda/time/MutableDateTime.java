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
import org.joda.time.field.FieldUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.convert.FromString;
import org.joda.time.format.ISODateTimeFormat;
import java.io.Serializable;
import org.joda.time.base.BaseDateTime;

public class MutableDateTime extends BaseDateTime implements ReadWritableDateTime, Cloneable, Serializable
{
    private static final long serialVersionUID = 2852608688135209575L;
    public static final int ROUND_NONE = 0;
    public static final int ROUND_FLOOR = 1;
    public static final int ROUND_CEILING = 2;
    public static final int ROUND_HALF_FLOOR = 3;
    public static final int ROUND_HALF_CEILING = 4;
    public static final int ROUND_HALF_EVEN = 5;
    private DateTimeField iRoundingField;
    private int iRoundingMode;
    
    public static MutableDateTime now() {
        return new MutableDateTime();
    }
    
    public static MutableDateTime now(final DateTimeZone dateTimeZone) {
        if (dateTimeZone == null) {
            throw new NullPointerException("Zone must not be null");
        }
        return new MutableDateTime(dateTimeZone);
    }
    
    public static MutableDateTime now(final Chronology chronology) {
        if (chronology == null) {
            throw new NullPointerException("Chronology must not be null");
        }
        return new MutableDateTime(chronology);
    }
    
    @FromString
    public static MutableDateTime parse(final String s) {
        return parse(s, ISODateTimeFormat.dateTimeParser().withOffsetParsed());
    }
    
    public static MutableDateTime parse(final String s, final DateTimeFormatter dateTimeFormatter) {
        return dateTimeFormatter.parseDateTime(s).toMutableDateTime();
    }
    
    public MutableDateTime() {
    }
    
    public MutableDateTime(final DateTimeZone dateTimeZone) {
        super(dateTimeZone);
    }
    
    public MutableDateTime(final Chronology chronology) {
        super(chronology);
    }
    
    public MutableDateTime(final long n) {
        super(n);
    }
    
    public MutableDateTime(final long n, final DateTimeZone dateTimeZone) {
        super(n, dateTimeZone);
    }
    
    public MutableDateTime(final long n, final Chronology chronology) {
        super(n, chronology);
    }
    
    public MutableDateTime(final Object o) {
        super(o, (Chronology)null);
    }
    
    public MutableDateTime(final Object o, final DateTimeZone dateTimeZone) {
        super(o, dateTimeZone);
    }
    
    public MutableDateTime(final Object o, final Chronology chronology) {
        super(o, DateTimeUtils.getChronology(chronology));
    }
    
    public MutableDateTime(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        super(n, n2, n3, n4, n5, n6, n7);
    }
    
    public MutableDateTime(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final DateTimeZone dateTimeZone) {
        super(n, n2, n3, n4, n5, n6, n7, dateTimeZone);
    }
    
    public MutableDateTime(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final Chronology chronology) {
        super(n, n2, n3, n4, n5, n6, n7, chronology);
    }
    
    public DateTimeField getRoundingField() {
        return this.iRoundingField;
    }
    
    public int getRoundingMode() {
        return this.iRoundingMode;
    }
    
    public void setRounding(final DateTimeField dateTimeField) {
        this.setRounding(dateTimeField, 1);
    }
    
    public void setRounding(final DateTimeField dateTimeField, final int i) {
        if (dateTimeField != null && (i < 0 || i > 5)) {
            throw new IllegalArgumentException("Illegal rounding mode: " + i);
        }
        this.iRoundingField = ((i == 0) ? null : dateTimeField);
        this.iRoundingMode = ((dateTimeField == null) ? 0 : i);
        this.setMillis(this.getMillis());
    }
    
    public void setMillis(long millis) {
        switch (this.iRoundingMode) {
            case 1: {
                millis = this.iRoundingField.roundFloor(millis);
                break;
            }
            case 2: {
                millis = this.iRoundingField.roundCeiling(millis);
                break;
            }
            case 3: {
                millis = this.iRoundingField.roundHalfFloor(millis);
                break;
            }
            case 4: {
                millis = this.iRoundingField.roundHalfCeiling(millis);
                break;
            }
            case 5: {
                millis = this.iRoundingField.roundHalfEven(millis);
                break;
            }
        }
        super.setMillis(millis);
    }
    
    public void setMillis(final ReadableInstant readableInstant) {
        this.setMillis(DateTimeUtils.getInstantMillis(readableInstant));
    }
    
    public void add(final long n) {
        this.setMillis(FieldUtils.safeAdd(this.getMillis(), n));
    }
    
    public void add(final ReadableDuration readableDuration) {
        this.add(readableDuration, 1);
    }
    
    public void add(final ReadableDuration readableDuration, final int n) {
        if (readableDuration != null) {
            this.add(FieldUtils.safeMultiply(readableDuration.getMillis(), n));
        }
    }
    
    public void add(final ReadablePeriod readablePeriod) {
        this.add(readablePeriod, 1);
    }
    
    public void add(final ReadablePeriod readablePeriod, final int n) {
        if (readablePeriod != null) {
            this.setMillis(this.getChronology().add(readablePeriod, this.getMillis(), n));
        }
    }
    
    public void setChronology(final Chronology chronology) {
        super.setChronology(chronology);
    }
    
    public void setZone(DateTimeZone zone) {
        zone = DateTimeUtils.getZone(zone);
        final Chronology chronology = this.getChronology();
        if (chronology.getZone() != zone) {
            this.setChronology(chronology.withZone(zone));
        }
    }
    
    public void setZoneRetainFields(DateTimeZone zone) {
        zone = DateTimeUtils.getZone(zone);
        final DateTimeZone zone2 = DateTimeUtils.getZone(this.getZone());
        if (zone == zone2) {
            return;
        }
        final long millisKeepLocal = zone2.getMillisKeepLocal(zone, this.getMillis());
        this.setChronology(this.getChronology().withZone(zone));
        this.setMillis(millisKeepLocal);
    }
    
    public void set(final DateTimeFieldType dateTimeFieldType, final int n) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        this.setMillis(dateTimeFieldType.getField(this.getChronology()).set(this.getMillis(), n));
    }
    
    public void add(final DurationFieldType durationFieldType, final int n) {
        if (durationFieldType == null) {
            throw new IllegalArgumentException("Field must not be null");
        }
        if (n != 0) {
            this.setMillis(durationFieldType.getField(this.getChronology()).add(this.getMillis(), n));
        }
    }
    
    public void setYear(final int n) {
        this.setMillis(this.getChronology().year().set(this.getMillis(), n));
    }
    
    public void addYears(final int n) {
        if (n != 0) {
            this.setMillis(this.getChronology().years().add(this.getMillis(), n));
        }
    }
    
    public void setWeekyear(final int n) {
        this.setMillis(this.getChronology().weekyear().set(this.getMillis(), n));
    }
    
    public void addWeekyears(final int n) {
        if (n != 0) {
            this.setMillis(this.getChronology().weekyears().add(this.getMillis(), n));
        }
    }
    
    public void setMonthOfYear(final int n) {
        this.setMillis(this.getChronology().monthOfYear().set(this.getMillis(), n));
    }
    
    public void addMonths(final int n) {
        if (n != 0) {
            this.setMillis(this.getChronology().months().add(this.getMillis(), n));
        }
    }
    
    public void setWeekOfWeekyear(final int n) {
        this.setMillis(this.getChronology().weekOfWeekyear().set(this.getMillis(), n));
    }
    
    public void addWeeks(final int n) {
        if (n != 0) {
            this.setMillis(this.getChronology().weeks().add(this.getMillis(), n));
        }
    }
    
    public void setDayOfYear(final int n) {
        this.setMillis(this.getChronology().dayOfYear().set(this.getMillis(), n));
    }
    
    public void setDayOfMonth(final int n) {
        this.setMillis(this.getChronology().dayOfMonth().set(this.getMillis(), n));
    }
    
    public void setDayOfWeek(final int n) {
        this.setMillis(this.getChronology().dayOfWeek().set(this.getMillis(), n));
    }
    
    public void addDays(final int n) {
        if (n != 0) {
            this.setMillis(this.getChronology().days().add(this.getMillis(), n));
        }
    }
    
    public void setHourOfDay(final int n) {
        this.setMillis(this.getChronology().hourOfDay().set(this.getMillis(), n));
    }
    
    public void addHours(final int n) {
        if (n != 0) {
            this.setMillis(this.getChronology().hours().add(this.getMillis(), n));
        }
    }
    
    public void setMinuteOfDay(final int n) {
        this.setMillis(this.getChronology().minuteOfDay().set(this.getMillis(), n));
    }
    
    public void setMinuteOfHour(final int n) {
        this.setMillis(this.getChronology().minuteOfHour().set(this.getMillis(), n));
    }
    
    public void addMinutes(final int n) {
        if (n != 0) {
            this.setMillis(this.getChronology().minutes().add(this.getMillis(), n));
        }
    }
    
    public void setSecondOfDay(final int n) {
        this.setMillis(this.getChronology().secondOfDay().set(this.getMillis(), n));
    }
    
    public void setSecondOfMinute(final int n) {
        this.setMillis(this.getChronology().secondOfMinute().set(this.getMillis(), n));
    }
    
    public void addSeconds(final int n) {
        if (n != 0) {
            this.setMillis(this.getChronology().seconds().add(this.getMillis(), n));
        }
    }
    
    public void setMillisOfDay(final int n) {
        this.setMillis(this.getChronology().millisOfDay().set(this.getMillis(), n));
    }
    
    public void setMillisOfSecond(final int n) {
        this.setMillis(this.getChronology().millisOfSecond().set(this.getMillis(), n));
    }
    
    public void addMillis(final int n) {
        if (n != 0) {
            this.setMillis(this.getChronology().millis().add(this.getMillis(), n));
        }
    }
    
    public void setDate(final long n) {
        this.setMillis(this.getChronology().millisOfDay().set(n, this.getMillisOfDay()));
    }
    
    public void setDate(final ReadableInstant readableInstant) {
        long date = DateTimeUtils.getInstantMillis(readableInstant);
        if (readableInstant instanceof ReadableDateTime) {
            final DateTimeZone zone = DateTimeUtils.getChronology(((ReadableDateTime)readableInstant).getChronology()).getZone();
            if (zone != null) {
                date = zone.getMillisKeepLocal(this.getZone(), date);
            }
        }
        this.setDate(date);
    }
    
    public void setDate(final int n, final int n2, final int n3) {
        this.setDate(this.getChronology().getDateTimeMillis(n, n2, n3, 0));
    }
    
    public void setTime(final long n) {
        this.setMillis(this.getChronology().millisOfDay().set(this.getMillis(), ISOChronology.getInstanceUTC().millisOfDay().get(n)));
    }
    
    public void setTime(final ReadableInstant readableInstant) {
        long time = DateTimeUtils.getInstantMillis(readableInstant);
        final DateTimeZone zone = DateTimeUtils.getInstantChronology(readableInstant).getZone();
        if (zone != null) {
            time = zone.getMillisKeepLocal(DateTimeZone.UTC, time);
        }
        this.setTime(time);
    }
    
    public void setTime(final int n, final int n2, final int n3, final int n4) {
        this.setMillis(this.getChronology().getDateTimeMillis(this.getMillis(), n, n2, n3, n4));
    }
    
    public void setDateTime(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        this.setMillis(this.getChronology().getDateTimeMillis(n, n2, n3, n4, n5, n6, n7));
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
    
    public MutableDateTime copy() {
        return (MutableDateTime)this.clone();
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new InternalError("Clone error");
        }
    }
    
    public static final class Property extends AbstractReadableInstantFieldProperty
    {
        private static final long serialVersionUID = -4481126543819298617L;
        private MutableDateTime iInstant;
        private DateTimeField iField;
        
        Property(final MutableDateTime iInstant, final DateTimeField iField) {
            this.iInstant = iInstant;
            this.iField = iField;
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeObject(this.iInstant);
            objectOutputStream.writeObject(this.iField.getType());
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            this.iInstant = (MutableDateTime)objectInputStream.readObject();
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
        
        public MutableDateTime getMutableDateTime() {
            return this.iInstant;
        }
        
        public MutableDateTime add(final int n) {
            this.iInstant.setMillis(this.getField().add(this.iInstant.getMillis(), n));
            return this.iInstant;
        }
        
        public MutableDateTime add(final long n) {
            this.iInstant.setMillis(this.getField().add(this.iInstant.getMillis(), n));
            return this.iInstant;
        }
        
        public MutableDateTime addWrapField(final int n) {
            this.iInstant.setMillis(this.getField().addWrapField(this.iInstant.getMillis(), n));
            return this.iInstant;
        }
        
        public MutableDateTime set(final int n) {
            this.iInstant.setMillis(this.getField().set(this.iInstant.getMillis(), n));
            return this.iInstant;
        }
        
        public MutableDateTime set(final String s, final Locale locale) {
            this.iInstant.setMillis(this.getField().set(this.iInstant.getMillis(), s, locale));
            return this.iInstant;
        }
        
        public MutableDateTime set(final String s) {
            this.set(s, null);
            return this.iInstant;
        }
        
        public MutableDateTime roundFloor() {
            this.iInstant.setMillis(this.getField().roundFloor(this.iInstant.getMillis()));
            return this.iInstant;
        }
        
        public MutableDateTime roundCeiling() {
            this.iInstant.setMillis(this.getField().roundCeiling(this.iInstant.getMillis()));
            return this.iInstant;
        }
        
        public MutableDateTime roundHalfFloor() {
            this.iInstant.setMillis(this.getField().roundHalfFloor(this.iInstant.getMillis()));
            return this.iInstant;
        }
        
        public MutableDateTime roundHalfCeiling() {
            this.iInstant.setMillis(this.getField().roundHalfCeiling(this.iInstant.getMillis()));
            return this.iInstant;
        }
        
        public MutableDateTime roundHalfEven() {
            this.iInstant.setMillis(this.getField().roundHalfEven(this.iInstant.getMillis()));
            return this.iInstant;
        }
    }
}
