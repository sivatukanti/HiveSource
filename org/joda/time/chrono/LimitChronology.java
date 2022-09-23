// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import java.util.Locale;
import org.joda.time.field.DecoratedDateTimeField;
import org.joda.time.field.DecoratedDurationField;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.field.FieldUtils;
import org.joda.time.DateTimeField;
import org.joda.time.DurationField;
import java.util.HashMap;
import org.joda.time.MutableDateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableDateTime;
import org.joda.time.Chronology;
import org.joda.time.DateTime;

public final class LimitChronology extends AssembledChronology
{
    private static final long serialVersionUID = 7670866536893052522L;
    final DateTime iLowerLimit;
    final DateTime iUpperLimit;
    private transient LimitChronology iWithUTC;
    
    public static LimitChronology getInstance(final Chronology chronology, final ReadableDateTime readableDateTime, final ReadableDateTime readableDateTime2) {
        if (chronology == null) {
            throw new IllegalArgumentException("Must supply a chronology");
        }
        final DateTime dateTime = (readableDateTime == null) ? null : readableDateTime.toDateTime();
        final DateTime dateTime2 = (readableDateTime2 == null) ? null : readableDateTime2.toDateTime();
        if (dateTime != null && dateTime2 != null && !dateTime.isBefore(dateTime2)) {
            throw new IllegalArgumentException("The lower limit must be come before than the upper limit");
        }
        return new LimitChronology(chronology, dateTime, dateTime2);
    }
    
    private LimitChronology(final Chronology chronology, final DateTime iLowerLimit, final DateTime iUpperLimit) {
        super(chronology, null);
        this.iLowerLimit = iLowerLimit;
        this.iUpperLimit = iUpperLimit;
    }
    
    public DateTime getLowerLimit() {
        return this.iLowerLimit;
    }
    
    public DateTime getUpperLimit() {
        return this.iUpperLimit;
    }
    
    @Override
    public Chronology withUTC() {
        return this.withZone(DateTimeZone.UTC);
    }
    
    @Override
    public Chronology withZone(DateTimeZone default1) {
        if (default1 == null) {
            default1 = DateTimeZone.getDefault();
        }
        if (default1 == this.getZone()) {
            return this;
        }
        if (default1 == DateTimeZone.UTC && this.iWithUTC != null) {
            return this.iWithUTC;
        }
        DateTime dateTime = this.iLowerLimit;
        if (dateTime != null) {
            final MutableDateTime mutableDateTime = dateTime.toMutableDateTime();
            mutableDateTime.setZoneRetainFields(default1);
            dateTime = mutableDateTime.toDateTime();
        }
        DateTime dateTime2 = this.iUpperLimit;
        if (dateTime2 != null) {
            final MutableDateTime mutableDateTime2 = dateTime2.toMutableDateTime();
            mutableDateTime2.setZoneRetainFields(default1);
            dateTime2 = mutableDateTime2.toDateTime();
        }
        final LimitChronology instance = getInstance(this.getBase().withZone(default1), dateTime, dateTime2);
        if (default1 == DateTimeZone.UTC) {
            this.iWithUTC = instance;
        }
        return instance;
    }
    
    @Override
    public long getDateTimeMillis(final int n, final int n2, final int n3, final int n4) throws IllegalArgumentException {
        final long dateTimeMillis = this.getBase().getDateTimeMillis(n, n2, n3, n4);
        this.checkLimits(dateTimeMillis, "resulting");
        return dateTimeMillis;
    }
    
    @Override
    public long getDateTimeMillis(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) throws IllegalArgumentException {
        final long dateTimeMillis = this.getBase().getDateTimeMillis(n, n2, n3, n4, n5, n6, n7);
        this.checkLimits(dateTimeMillis, "resulting");
        return dateTimeMillis;
    }
    
    @Override
    public long getDateTimeMillis(long dateTimeMillis, final int n, final int n2, final int n3, final int n4) throws IllegalArgumentException {
        this.checkLimits(dateTimeMillis, null);
        dateTimeMillis = this.getBase().getDateTimeMillis(dateTimeMillis, n, n2, n3, n4);
        this.checkLimits(dateTimeMillis, "resulting");
        return dateTimeMillis;
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
        final LimitDurationField value = new LimitDurationField(key);
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
        final LimitDateTimeField value = new LimitDateTimeField(key, this.convertField(key.getDurationField(), hashMap), this.convertField(key.getRangeDurationField(), hashMap), this.convertField(key.getLeapDurationField(), hashMap));
        hashMap.put(key, value);
        return value;
    }
    
    void checkLimits(final long n, final String s) {
        final DateTime iLowerLimit;
        if ((iLowerLimit = this.iLowerLimit) != null && n < iLowerLimit.getMillis()) {
            throw new LimitException(s, true);
        }
        final DateTime iUpperLimit;
        if ((iUpperLimit = this.iUpperLimit) != null && n >= iUpperLimit.getMillis()) {
            throw new LimitException(s, false);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LimitChronology)) {
            return false;
        }
        final LimitChronology limitChronology = (LimitChronology)o;
        return this.getBase().equals(limitChronology.getBase()) && FieldUtils.equals(this.getLowerLimit(), limitChronology.getLowerLimit()) && FieldUtils.equals(this.getUpperLimit(), limitChronology.getUpperLimit());
    }
    
    @Override
    public int hashCode() {
        return 317351877 + ((this.getLowerLimit() != null) ? this.getLowerLimit().hashCode() : 0) + ((this.getUpperLimit() != null) ? this.getUpperLimit().hashCode() : 0) + this.getBase().hashCode() * 7;
    }
    
    @Override
    public String toString() {
        return "LimitChronology[" + this.getBase().toString() + ", " + ((this.getLowerLimit() == null) ? "NoLimit" : this.getLowerLimit().toString()) + ", " + ((this.getUpperLimit() == null) ? "NoLimit" : this.getUpperLimit().toString()) + ']';
    }
    
    private class LimitException extends IllegalArgumentException
    {
        private static final long serialVersionUID = -5924689995607498581L;
        private final boolean iIsLow;
        
        LimitException(final String s, final boolean iIsLow) {
            super(s);
            this.iIsLow = iIsLow;
        }
        
        @Override
        public String getMessage() {
            final StringBuffer sb = new StringBuffer(85);
            sb.append("The");
            final String message = super.getMessage();
            if (message != null) {
                sb.append(' ');
                sb.append(message);
            }
            sb.append(" instant is ");
            final DateTimeFormatter withChronology = ISODateTimeFormat.dateTime().withChronology(LimitChronology.this.getBase());
            if (this.iIsLow) {
                sb.append("below the supported minimum of ");
                withChronology.printTo(sb, LimitChronology.this.getLowerLimit().getMillis());
            }
            else {
                sb.append("above the supported maximum of ");
                withChronology.printTo(sb, LimitChronology.this.getUpperLimit().getMillis());
            }
            sb.append(" (");
            sb.append(LimitChronology.this.getBase());
            sb.append(')');
            return sb.toString();
        }
        
        @Override
        public String toString() {
            return "IllegalArgumentException: " + this.getMessage();
        }
    }
    
    private class LimitDurationField extends DecoratedDurationField
    {
        private static final long serialVersionUID = 8049297699408782284L;
        
        LimitDurationField(final DurationField durationField) {
            super(durationField, durationField.getType());
        }
        
        @Override
        public int getValue(final long n, final long n2) {
            LimitChronology.this.checkLimits(n2, null);
            return this.getWrappedField().getValue(n, n2);
        }
        
        @Override
        public long getValueAsLong(final long n, final long n2) {
            LimitChronology.this.checkLimits(n2, null);
            return this.getWrappedField().getValueAsLong(n, n2);
        }
        
        @Override
        public long getMillis(final int n, final long n2) {
            LimitChronology.this.checkLimits(n2, null);
            return this.getWrappedField().getMillis(n, n2);
        }
        
        @Override
        public long getMillis(final long n, final long n2) {
            LimitChronology.this.checkLimits(n2, null);
            return this.getWrappedField().getMillis(n, n2);
        }
        
        @Override
        public long add(final long n, final int n2) {
            LimitChronology.this.checkLimits(n, null);
            final long add = this.getWrappedField().add(n, n2);
            LimitChronology.this.checkLimits(add, "resulting");
            return add;
        }
        
        @Override
        public long add(final long n, final long n2) {
            LimitChronology.this.checkLimits(n, null);
            final long add = this.getWrappedField().add(n, n2);
            LimitChronology.this.checkLimits(add, "resulting");
            return add;
        }
        
        @Override
        public int getDifference(final long n, final long n2) {
            LimitChronology.this.checkLimits(n, "minuend");
            LimitChronology.this.checkLimits(n2, "subtrahend");
            return this.getWrappedField().getDifference(n, n2);
        }
        
        @Override
        public long getDifferenceAsLong(final long n, final long n2) {
            LimitChronology.this.checkLimits(n, "minuend");
            LimitChronology.this.checkLimits(n2, "subtrahend");
            return this.getWrappedField().getDifferenceAsLong(n, n2);
        }
    }
    
    private class LimitDateTimeField extends DecoratedDateTimeField
    {
        private static final long serialVersionUID = -2435306746995699312L;
        private final DurationField iDurationField;
        private final DurationField iRangeDurationField;
        private final DurationField iLeapDurationField;
        
        LimitDateTimeField(final DateTimeField dateTimeField, final DurationField iDurationField, final DurationField iRangeDurationField, final DurationField iLeapDurationField) {
            super(dateTimeField, dateTimeField.getType());
            this.iDurationField = iDurationField;
            this.iRangeDurationField = iRangeDurationField;
            this.iLeapDurationField = iLeapDurationField;
        }
        
        @Override
        public int get(final long n) {
            LimitChronology.this.checkLimits(n, null);
            return this.getWrappedField().get(n);
        }
        
        @Override
        public String getAsText(final long n, final Locale locale) {
            LimitChronology.this.checkLimits(n, null);
            return this.getWrappedField().getAsText(n, locale);
        }
        
        @Override
        public String getAsShortText(final long n, final Locale locale) {
            LimitChronology.this.checkLimits(n, null);
            return this.getWrappedField().getAsShortText(n, locale);
        }
        
        @Override
        public long add(final long n, final int n2) {
            LimitChronology.this.checkLimits(n, null);
            final long add = this.getWrappedField().add(n, n2);
            LimitChronology.this.checkLimits(add, "resulting");
            return add;
        }
        
        @Override
        public long add(final long n, final long n2) {
            LimitChronology.this.checkLimits(n, null);
            final long add = this.getWrappedField().add(n, n2);
            LimitChronology.this.checkLimits(add, "resulting");
            return add;
        }
        
        @Override
        public long addWrapField(final long n, final int n2) {
            LimitChronology.this.checkLimits(n, null);
            final long addWrapField = this.getWrappedField().addWrapField(n, n2);
            LimitChronology.this.checkLimits(addWrapField, "resulting");
            return addWrapField;
        }
        
        @Override
        public int getDifference(final long n, final long n2) {
            LimitChronology.this.checkLimits(n, "minuend");
            LimitChronology.this.checkLimits(n2, "subtrahend");
            return this.getWrappedField().getDifference(n, n2);
        }
        
        @Override
        public long getDifferenceAsLong(final long n, final long n2) {
            LimitChronology.this.checkLimits(n, "minuend");
            LimitChronology.this.checkLimits(n2, "subtrahend");
            return this.getWrappedField().getDifferenceAsLong(n, n2);
        }
        
        @Override
        public long set(final long n, final int n2) {
            LimitChronology.this.checkLimits(n, null);
            final long set = this.getWrappedField().set(n, n2);
            LimitChronology.this.checkLimits(set, "resulting");
            return set;
        }
        
        @Override
        public long set(final long n, final String s, final Locale locale) {
            LimitChronology.this.checkLimits(n, null);
            final long set = this.getWrappedField().set(n, s, locale);
            LimitChronology.this.checkLimits(set, "resulting");
            return set;
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
            LimitChronology.this.checkLimits(n, null);
            return this.getWrappedField().isLeap(n);
        }
        
        @Override
        public int getLeapAmount(final long n) {
            LimitChronology.this.checkLimits(n, null);
            return this.getWrappedField().getLeapAmount(n);
        }
        
        @Override
        public final DurationField getLeapDurationField() {
            return this.iLeapDurationField;
        }
        
        @Override
        public long roundFloor(final long n) {
            LimitChronology.this.checkLimits(n, null);
            final long roundFloor = this.getWrappedField().roundFloor(n);
            LimitChronology.this.checkLimits(roundFloor, "resulting");
            return roundFloor;
        }
        
        @Override
        public long roundCeiling(final long n) {
            LimitChronology.this.checkLimits(n, null);
            final long roundCeiling = this.getWrappedField().roundCeiling(n);
            LimitChronology.this.checkLimits(roundCeiling, "resulting");
            return roundCeiling;
        }
        
        @Override
        public long roundHalfFloor(final long n) {
            LimitChronology.this.checkLimits(n, null);
            final long roundHalfFloor = this.getWrappedField().roundHalfFloor(n);
            LimitChronology.this.checkLimits(roundHalfFloor, "resulting");
            return roundHalfFloor;
        }
        
        @Override
        public long roundHalfCeiling(final long n) {
            LimitChronology.this.checkLimits(n, null);
            final long roundHalfCeiling = this.getWrappedField().roundHalfCeiling(n);
            LimitChronology.this.checkLimits(roundHalfCeiling, "resulting");
            return roundHalfCeiling;
        }
        
        @Override
        public long roundHalfEven(final long n) {
            LimitChronology.this.checkLimits(n, null);
            final long roundHalfEven = this.getWrappedField().roundHalfEven(n);
            LimitChronology.this.checkLimits(roundHalfEven, "resulting");
            return roundHalfEven;
        }
        
        @Override
        public long remainder(final long n) {
            LimitChronology.this.checkLimits(n, null);
            final long remainder = this.getWrappedField().remainder(n);
            LimitChronology.this.checkLimits(remainder, "resulting");
            return remainder;
        }
        
        @Override
        public int getMinimumValue(final long n) {
            LimitChronology.this.checkLimits(n, null);
            return this.getWrappedField().getMinimumValue(n);
        }
        
        @Override
        public int getMaximumValue(final long n) {
            LimitChronology.this.checkLimits(n, null);
            return this.getWrappedField().getMaximumValue(n);
        }
        
        @Override
        public int getMaximumTextLength(final Locale locale) {
            return this.getWrappedField().getMaximumTextLength(locale);
        }
        
        @Override
        public int getMaximumShortTextLength(final Locale locale) {
            return this.getWrappedField().getMaximumShortTextLength(locale);
        }
    }
}
