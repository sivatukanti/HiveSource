// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.ReadablePartial;
import org.joda.time.IllegalFieldValueException;
import java.util.Locale;
import org.joda.time.field.BaseDateTimeField;
import org.joda.time.field.BaseDurationField;
import org.joda.time.DateTimeField;
import java.util.HashMap;
import org.joda.time.IllegalInstantException;
import org.joda.time.DurationField;
import org.joda.time.DateTimeZone;
import org.joda.time.Chronology;

public final class ZonedChronology extends AssembledChronology
{
    private static final long serialVersionUID = -1079258847191166848L;
    
    public static ZonedChronology getInstance(Chronology withUTC, final DateTimeZone dateTimeZone) {
        if (withUTC == null) {
            throw new IllegalArgumentException("Must supply a chronology");
        }
        withUTC = withUTC.withUTC();
        if (withUTC == null) {
            throw new IllegalArgumentException("UTC chronology must not be null");
        }
        if (dateTimeZone == null) {
            throw new IllegalArgumentException("DateTimeZone must not be null");
        }
        return new ZonedChronology(withUTC, dateTimeZone);
    }
    
    static boolean useTimeArithmetic(final DurationField durationField) {
        return durationField != null && durationField.getUnitMillis() < 43200000L;
    }
    
    private ZonedChronology(final Chronology chronology, final DateTimeZone dateTimeZone) {
        super(chronology, dateTimeZone);
    }
    
    @Override
    public DateTimeZone getZone() {
        return (DateTimeZone)this.getParam();
    }
    
    @Override
    public Chronology withUTC() {
        return this.getBase();
    }
    
    @Override
    public Chronology withZone(DateTimeZone default1) {
        if (default1 == null) {
            default1 = DateTimeZone.getDefault();
        }
        if (default1 == this.getParam()) {
            return this;
        }
        if (default1 == DateTimeZone.UTC) {
            return this.getBase();
        }
        return new ZonedChronology(this.getBase(), default1);
    }
    
    @Override
    public long getDateTimeMillis(final int n, final int n2, final int n3, final int n4) throws IllegalArgumentException {
        return this.localToUTC(this.getBase().getDateTimeMillis(n, n2, n3, n4));
    }
    
    @Override
    public long getDateTimeMillis(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) throws IllegalArgumentException {
        return this.localToUTC(this.getBase().getDateTimeMillis(n, n2, n3, n4, n5, n6, n7));
    }
    
    @Override
    public long getDateTimeMillis(final long n, final int n2, final int n3, final int n4, final int n5) throws IllegalArgumentException {
        return this.localToUTC(this.getBase().getDateTimeMillis(n + this.getZone().getOffset(n), n2, n3, n4, n5));
    }
    
    private long localToUTC(final long n) {
        final DateTimeZone zone = this.getZone();
        final int offsetFromLocal = zone.getOffsetFromLocal(n);
        final long n2 = n - offsetFromLocal;
        if (offsetFromLocal != zone.getOffset(n2)) {
            throw new IllegalInstantException(n, zone.getID());
        }
        return n2;
    }
    
    @Override
    protected void assemble(final Fields fields) {
        final HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
        fields.eras = this.convertField(fields.eras, hashMap);
        fields.centuries = this.convertField(fields.centuries, hashMap);
        fields.years = this.convertField(fields.years, hashMap);
        fields.months = this.convertField(fields.months, hashMap);
        fields.weekyears = this.convertField(fields.weekyears, hashMap);
        fields.weeks = this.convertField(fields.weeks, hashMap);
        fields.days = this.convertField(fields.days, hashMap);
        fields.halfdays = this.convertField(fields.halfdays, hashMap);
        fields.hours = this.convertField(fields.hours, hashMap);
        fields.minutes = this.convertField(fields.minutes, hashMap);
        fields.seconds = this.convertField(fields.seconds, hashMap);
        fields.millis = this.convertField(fields.millis, hashMap);
        fields.year = this.convertField(fields.year, hashMap);
        fields.yearOfEra = this.convertField(fields.yearOfEra, hashMap);
        fields.yearOfCentury = this.convertField(fields.yearOfCentury, hashMap);
        fields.centuryOfEra = this.convertField(fields.centuryOfEra, hashMap);
        fields.era = this.convertField(fields.era, hashMap);
        fields.dayOfWeek = this.convertField(fields.dayOfWeek, hashMap);
        fields.dayOfMonth = this.convertField(fields.dayOfMonth, hashMap);
        fields.dayOfYear = this.convertField(fields.dayOfYear, hashMap);
        fields.monthOfYear = this.convertField(fields.monthOfYear, hashMap);
        fields.weekOfWeekyear = this.convertField(fields.weekOfWeekyear, hashMap);
        fields.weekyear = this.convertField(fields.weekyear, hashMap);
        fields.weekyearOfCentury = this.convertField(fields.weekyearOfCentury, hashMap);
        fields.millisOfSecond = this.convertField(fields.millisOfSecond, hashMap);
        fields.millisOfDay = this.convertField(fields.millisOfDay, hashMap);
        fields.secondOfMinute = this.convertField(fields.secondOfMinute, hashMap);
        fields.secondOfDay = this.convertField(fields.secondOfDay, hashMap);
        fields.minuteOfHour = this.convertField(fields.minuteOfHour, hashMap);
        fields.minuteOfDay = this.convertField(fields.minuteOfDay, hashMap);
        fields.hourOfDay = this.convertField(fields.hourOfDay, hashMap);
        fields.hourOfHalfday = this.convertField(fields.hourOfHalfday, hashMap);
        fields.clockhourOfDay = this.convertField(fields.clockhourOfDay, hashMap);
        fields.clockhourOfHalfday = this.convertField(fields.clockhourOfHalfday, hashMap);
        fields.halfdayOfDay = this.convertField(fields.halfdayOfDay, hashMap);
    }
    
    private DurationField convertField(final DurationField key, final HashMap<Object, Object> hashMap) {
        if (key == null || !key.isSupported()) {
            return key;
        }
        if (hashMap.containsKey(key)) {
            return hashMap.get(key);
        }
        final ZonedDurationField value = new ZonedDurationField(key, this.getZone());
        hashMap.put(key, value);
        return value;
    }
    
    private DateTimeField convertField(final DateTimeField key, final HashMap<Object, Object> hashMap) {
        if (key == null || !key.isSupported()) {
            return key;
        }
        if (hashMap.containsKey(key)) {
            return hashMap.get(key);
        }
        final ZonedDateTimeField value = new ZonedDateTimeField(key, this.getZone(), this.convertField(key.getDurationField(), hashMap), this.convertField(key.getRangeDurationField(), hashMap), this.convertField(key.getLeapDurationField(), hashMap));
        hashMap.put(key, value);
        return value;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ZonedChronology)) {
            return false;
        }
        final ZonedChronology zonedChronology = (ZonedChronology)o;
        return this.getBase().equals(zonedChronology.getBase()) && this.getZone().equals(zonedChronology.getZone());
    }
    
    @Override
    public int hashCode() {
        return 326565 + this.getZone().hashCode() * 11 + this.getBase().hashCode() * 7;
    }
    
    @Override
    public String toString() {
        return "ZonedChronology[" + this.getBase() + ", " + this.getZone().getID() + ']';
    }
    
    static class ZonedDurationField extends BaseDurationField
    {
        private static final long serialVersionUID = -485345310999208286L;
        final DurationField iField;
        final boolean iTimeField;
        final DateTimeZone iZone;
        
        ZonedDurationField(final DurationField iField, final DateTimeZone iZone) {
            super(iField.getType());
            if (!iField.isSupported()) {
                throw new IllegalArgumentException();
            }
            this.iField = iField;
            this.iTimeField = ZonedChronology.useTimeArithmetic(iField);
            this.iZone = iZone;
        }
        
        @Override
        public boolean isPrecise() {
            return this.iTimeField ? this.iField.isPrecise() : (this.iField.isPrecise() && this.iZone.isFixed());
        }
        
        @Override
        public long getUnitMillis() {
            return this.iField.getUnitMillis();
        }
        
        @Override
        public int getValue(final long n, final long n2) {
            return this.iField.getValue(n, this.addOffset(n2));
        }
        
        @Override
        public long getValueAsLong(final long n, final long n2) {
            return this.iField.getValueAsLong(n, this.addOffset(n2));
        }
        
        @Override
        public long getMillis(final int n, final long n2) {
            return this.iField.getMillis(n, this.addOffset(n2));
        }
        
        @Override
        public long getMillis(final long n, final long n2) {
            return this.iField.getMillis(n, this.addOffset(n2));
        }
        
        @Override
        public long add(long add, final int n) {
            final int offsetToAdd = this.getOffsetToAdd(add);
            add = this.iField.add(add + offsetToAdd, n);
            return add - (this.iTimeField ? offsetToAdd : this.getOffsetFromLocalToSubtract(add));
        }
        
        @Override
        public long add(long add, final long n) {
            final int offsetToAdd = this.getOffsetToAdd(add);
            add = this.iField.add(add + offsetToAdd, n);
            return add - (this.iTimeField ? offsetToAdd : this.getOffsetFromLocalToSubtract(add));
        }
        
        @Override
        public int getDifference(final long n, final long n2) {
            final int offsetToAdd = this.getOffsetToAdd(n2);
            return this.iField.getDifference(n + (this.iTimeField ? offsetToAdd : this.getOffsetToAdd(n)), n2 + offsetToAdd);
        }
        
        @Override
        public long getDifferenceAsLong(final long n, final long n2) {
            final int offsetToAdd = this.getOffsetToAdd(n2);
            return this.iField.getDifferenceAsLong(n + (this.iTimeField ? offsetToAdd : this.getOffsetToAdd(n)), n2 + offsetToAdd);
        }
        
        private int getOffsetToAdd(final long n) {
            final int offset = this.iZone.getOffset(n);
            if ((n ^ n + offset) < 0L && (n ^ (long)offset) >= 0L) {
                throw new ArithmeticException("Adding time zone offset caused overflow");
            }
            return offset;
        }
        
        private int getOffsetFromLocalToSubtract(final long n) {
            final int offsetFromLocal = this.iZone.getOffsetFromLocal(n);
            if ((n ^ n - offsetFromLocal) < 0L && (n ^ (long)offsetFromLocal) < 0L) {
                throw new ArithmeticException("Subtracting time zone offset caused overflow");
            }
            return offsetFromLocal;
        }
        
        private long addOffset(final long n) {
            return this.iZone.convertUTCToLocal(n);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof ZonedDurationField) {
                final ZonedDurationField zonedDurationField = (ZonedDurationField)o;
                return this.iField.equals(zonedDurationField.iField) && this.iZone.equals(zonedDurationField.iZone);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return this.iField.hashCode() ^ this.iZone.hashCode();
        }
    }
    
    static final class ZonedDateTimeField extends BaseDateTimeField
    {
        private static final long serialVersionUID = -3968986277775529794L;
        final DateTimeField iField;
        final DateTimeZone iZone;
        final DurationField iDurationField;
        final boolean iTimeField;
        final DurationField iRangeDurationField;
        final DurationField iLeapDurationField;
        
        ZonedDateTimeField(final DateTimeField iField, final DateTimeZone iZone, final DurationField iDurationField, final DurationField iRangeDurationField, final DurationField iLeapDurationField) {
            super(iField.getType());
            if (!iField.isSupported()) {
                throw new IllegalArgumentException();
            }
            this.iField = iField;
            this.iZone = iZone;
            this.iDurationField = iDurationField;
            this.iTimeField = ZonedChronology.useTimeArithmetic(iDurationField);
            this.iRangeDurationField = iRangeDurationField;
            this.iLeapDurationField = iLeapDurationField;
        }
        
        @Override
        public boolean isLenient() {
            return this.iField.isLenient();
        }
        
        @Override
        public int get(final long n) {
            return this.iField.get(this.iZone.convertUTCToLocal(n));
        }
        
        @Override
        public String getAsText(final long n, final Locale locale) {
            return this.iField.getAsText(this.iZone.convertUTCToLocal(n), locale);
        }
        
        @Override
        public String getAsShortText(final long n, final Locale locale) {
            return this.iField.getAsShortText(this.iZone.convertUTCToLocal(n), locale);
        }
        
        @Override
        public String getAsText(final int n, final Locale locale) {
            return this.iField.getAsText(n, locale);
        }
        
        @Override
        public String getAsShortText(final int n, final Locale locale) {
            return this.iField.getAsShortText(n, locale);
        }
        
        @Override
        public long add(final long n, final int n2) {
            if (this.iTimeField) {
                final int offsetToAdd = this.getOffsetToAdd(n);
                return this.iField.add(n + offsetToAdd, n2) - offsetToAdd;
            }
            return this.iZone.convertLocalToUTC(this.iField.add(this.iZone.convertUTCToLocal(n), n2), false, n);
        }
        
        @Override
        public long add(final long n, final long n2) {
            if (this.iTimeField) {
                final int offsetToAdd = this.getOffsetToAdd(n);
                return this.iField.add(n + offsetToAdd, n2) - offsetToAdd;
            }
            return this.iZone.convertLocalToUTC(this.iField.add(this.iZone.convertUTCToLocal(n), n2), false, n);
        }
        
        @Override
        public long addWrapField(final long n, final int n2) {
            if (this.iTimeField) {
                final int offsetToAdd = this.getOffsetToAdd(n);
                return this.iField.addWrapField(n + offsetToAdd, n2) - offsetToAdd;
            }
            return this.iZone.convertLocalToUTC(this.iField.addWrapField(this.iZone.convertUTCToLocal(n), n2), false, n);
        }
        
        @Override
        public long set(final long n, final int i) {
            final long set = this.iField.set(this.iZone.convertUTCToLocal(n), i);
            final long convertLocalToUTC = this.iZone.convertLocalToUTC(set, false, n);
            if (this.get(convertLocalToUTC) != i) {
                final IllegalInstantException cause = new IllegalInstantException(set, this.iZone.getID());
                final IllegalFieldValueException ex = new IllegalFieldValueException(this.iField.getType(), i, cause.getMessage());
                ex.initCause(cause);
                throw ex;
            }
            return convertLocalToUTC;
        }
        
        @Override
        public long set(final long n, final String s, final Locale locale) {
            return this.iZone.convertLocalToUTC(this.iField.set(this.iZone.convertUTCToLocal(n), s, locale), false, n);
        }
        
        @Override
        public int getDifference(final long n, final long n2) {
            final int offsetToAdd = this.getOffsetToAdd(n2);
            return this.iField.getDifference(n + (this.iTimeField ? offsetToAdd : this.getOffsetToAdd(n)), n2 + offsetToAdd);
        }
        
        @Override
        public long getDifferenceAsLong(final long n, final long n2) {
            final int offsetToAdd = this.getOffsetToAdd(n2);
            return this.iField.getDifferenceAsLong(n + (this.iTimeField ? offsetToAdd : this.getOffsetToAdd(n)), n2 + offsetToAdd);
        }
        
        @Override
        public final DurationField getDurationField() {
            return this.iDurationField;
        }
        
        @Override
        public final DurationField getRangeDurationField() {
            return this.iRangeDurationField;
        }
        
        @Override
        public boolean isLeap(final long n) {
            return this.iField.isLeap(this.iZone.convertUTCToLocal(n));
        }
        
        @Override
        public int getLeapAmount(final long n) {
            return this.iField.getLeapAmount(this.iZone.convertUTCToLocal(n));
        }
        
        @Override
        public final DurationField getLeapDurationField() {
            return this.iLeapDurationField;
        }
        
        @Override
        public long roundFloor(long roundFloor) {
            if (this.iTimeField) {
                final int offsetToAdd = this.getOffsetToAdd(roundFloor);
                roundFloor = this.iField.roundFloor(roundFloor + offsetToAdd);
                return roundFloor - offsetToAdd;
            }
            return this.iZone.convertLocalToUTC(this.iField.roundFloor(this.iZone.convertUTCToLocal(roundFloor)), false, roundFloor);
        }
        
        @Override
        public long roundCeiling(long roundCeiling) {
            if (this.iTimeField) {
                final int offsetToAdd = this.getOffsetToAdd(roundCeiling);
                roundCeiling = this.iField.roundCeiling(roundCeiling + offsetToAdd);
                return roundCeiling - offsetToAdd;
            }
            return this.iZone.convertLocalToUTC(this.iField.roundCeiling(this.iZone.convertUTCToLocal(roundCeiling)), false, roundCeiling);
        }
        
        @Override
        public long remainder(final long n) {
            return this.iField.remainder(this.iZone.convertUTCToLocal(n));
        }
        
        @Override
        public int getMinimumValue() {
            return this.iField.getMinimumValue();
        }
        
        @Override
        public int getMinimumValue(final long n) {
            return this.iField.getMinimumValue(this.iZone.convertUTCToLocal(n));
        }
        
        @Override
        public int getMinimumValue(final ReadablePartial readablePartial) {
            return this.iField.getMinimumValue(readablePartial);
        }
        
        @Override
        public int getMinimumValue(final ReadablePartial readablePartial, final int[] array) {
            return this.iField.getMinimumValue(readablePartial, array);
        }
        
        @Override
        public int getMaximumValue() {
            return this.iField.getMaximumValue();
        }
        
        @Override
        public int getMaximumValue(final long n) {
            return this.iField.getMaximumValue(this.iZone.convertUTCToLocal(n));
        }
        
        @Override
        public int getMaximumValue(final ReadablePartial readablePartial) {
            return this.iField.getMaximumValue(readablePartial);
        }
        
        @Override
        public int getMaximumValue(final ReadablePartial readablePartial, final int[] array) {
            return this.iField.getMaximumValue(readablePartial, array);
        }
        
        @Override
        public int getMaximumTextLength(final Locale locale) {
            return this.iField.getMaximumTextLength(locale);
        }
        
        @Override
        public int getMaximumShortTextLength(final Locale locale) {
            return this.iField.getMaximumShortTextLength(locale);
        }
        
        private int getOffsetToAdd(final long n) {
            final int offset = this.iZone.getOffset(n);
            if ((n ^ n + offset) < 0L && (n ^ (long)offset) >= 0L) {
                throw new ArithmeticException("Adding time zone offset caused overflow");
            }
            return offset;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof ZonedDateTimeField) {
                final ZonedDateTimeField zonedDateTimeField = (ZonedDateTimeField)o;
                return this.iField.equals(zonedDateTimeField.iField) && this.iZone.equals(zonedDateTimeField.iZone) && this.iDurationField.equals(zonedDateTimeField.iDurationField) && this.iRangeDurationField.equals(zonedDateTimeField.iRangeDurationField);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return this.iField.hashCode() ^ this.iZone.hashCode();
        }
    }
}
