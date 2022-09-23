// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.field.UnsupportedDateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.field.UnsupportedDurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.field.FieldUtils;
import org.joda.time.DurationField;
import org.joda.time.ReadablePeriod;
import org.joda.time.DateTimeField;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.ReadablePartial;
import org.joda.time.DateTimeZone;
import java.io.Serializable;
import org.joda.time.Chronology;

public abstract class BaseChronology extends Chronology implements Serializable
{
    private static final long serialVersionUID = -7310865996721419676L;
    
    protected BaseChronology() {
    }
    
    @Override
    public abstract DateTimeZone getZone();
    
    @Override
    public abstract Chronology withUTC();
    
    @Override
    public abstract Chronology withZone(final DateTimeZone p0);
    
    @Override
    public long getDateTimeMillis(final int n, final int n2, final int n3, final int n4) throws IllegalArgumentException {
        return this.millisOfDay().set(this.dayOfMonth().set(this.monthOfYear().set(this.year().set(0L, n), n2), n3), n4);
    }
    
    @Override
    public long getDateTimeMillis(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) throws IllegalArgumentException {
        return this.millisOfSecond().set(this.secondOfMinute().set(this.minuteOfHour().set(this.hourOfDay().set(this.dayOfMonth().set(this.monthOfYear().set(this.year().set(0L, n), n2), n3), n4), n5), n6), n7);
    }
    
    @Override
    public long getDateTimeMillis(long n, final int n2, final int n3, final int n4, final int n5) throws IllegalArgumentException {
        n = this.hourOfDay().set(n, n2);
        n = this.minuteOfHour().set(n, n3);
        n = this.secondOfMinute().set(n, n4);
        return this.millisOfSecond().set(n, n5);
    }
    
    @Override
    public void validate(final ReadablePartial readablePartial, final int[] array) {
        final int size = readablePartial.size();
        for (int i = 0; i < size; ++i) {
            final int n = array[i];
            final DateTimeField field = readablePartial.getField(i);
            if (n < field.getMinimumValue()) {
                throw new IllegalFieldValueException(field.getType(), n, field.getMinimumValue(), null);
            }
            if (n > field.getMaximumValue()) {
                throw new IllegalFieldValueException(field.getType(), n, null, field.getMaximumValue());
            }
        }
        for (int j = 0; j < size; ++j) {
            final int n2 = array[j];
            final DateTimeField field2 = readablePartial.getField(j);
            if (n2 < field2.getMinimumValue(readablePartial, array)) {
                throw new IllegalFieldValueException(field2.getType(), n2, field2.getMinimumValue(readablePartial, array), null);
            }
            if (n2 > field2.getMaximumValue(readablePartial, array)) {
                throw new IllegalFieldValueException(field2.getType(), n2, null, field2.getMaximumValue(readablePartial, array));
            }
        }
    }
    
    @Override
    public int[] get(final ReadablePartial readablePartial, final long n) {
        final int size = readablePartial.size();
        final int[] array = new int[size];
        for (int i = 0; i < size; ++i) {
            array[i] = readablePartial.getFieldType(i).getField(this).get(n);
        }
        return array;
    }
    
    @Override
    public long set(final ReadablePartial readablePartial, long set) {
        for (int i = 0; i < readablePartial.size(); ++i) {
            set = readablePartial.getFieldType(i).getField(this).set(set, readablePartial.getValue(i));
        }
        return set;
    }
    
    @Override
    public int[] get(final ReadablePeriod readablePeriod, long add, final long n) {
        final int size = readablePeriod.size();
        final int[] array = new int[size];
        if (add != n) {
            for (int i = 0; i < size; ++i) {
                final DurationField field = readablePeriod.getFieldType(i).getField(this);
                final int difference = field.getDifference(n, add);
                if (difference != 0) {
                    add = field.add(add, difference);
                }
                array[i] = difference;
            }
        }
        return array;
    }
    
    @Override
    public int[] get(final ReadablePeriod readablePeriod, final long n) {
        final int size = readablePeriod.size();
        final int[] array = new int[size];
        if (n != 0L) {
            long add = 0L;
            for (int i = 0; i < size; ++i) {
                final DurationField field = readablePeriod.getFieldType(i).getField(this);
                if (field.isPrecise()) {
                    final int difference = field.getDifference(n, add);
                    add = field.add(add, difference);
                    array[i] = difference;
                }
            }
        }
        return array;
    }
    
    @Override
    public long add(final ReadablePeriod readablePeriod, long add, final int n) {
        if (n != 0 && readablePeriod != null) {
            for (int i = 0; i < readablePeriod.size(); ++i) {
                final long n2 = readablePeriod.getValue(i);
                if (n2 != 0L) {
                    add = readablePeriod.getFieldType(i).getField(this).add(add, n2 * n);
                }
            }
        }
        return add;
    }
    
    @Override
    public long add(final long n, final long n2, final int n3) {
        if (n2 == 0L || n3 == 0) {
            return n;
        }
        return FieldUtils.safeAdd(n, FieldUtils.safeMultiply(n2, n3));
    }
    
    @Override
    public DurationField millis() {
        return UnsupportedDurationField.getInstance(DurationFieldType.millis());
    }
    
    @Override
    public DateTimeField millisOfSecond() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.millisOfSecond(), this.millis());
    }
    
    @Override
    public DateTimeField millisOfDay() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.millisOfDay(), this.millis());
    }
    
    @Override
    public DurationField seconds() {
        return UnsupportedDurationField.getInstance(DurationFieldType.seconds());
    }
    
    @Override
    public DateTimeField secondOfMinute() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.secondOfMinute(), this.seconds());
    }
    
    @Override
    public DateTimeField secondOfDay() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.secondOfDay(), this.seconds());
    }
    
    @Override
    public DurationField minutes() {
        return UnsupportedDurationField.getInstance(DurationFieldType.minutes());
    }
    
    @Override
    public DateTimeField minuteOfHour() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.minuteOfHour(), this.minutes());
    }
    
    @Override
    public DateTimeField minuteOfDay() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.minuteOfDay(), this.minutes());
    }
    
    @Override
    public DurationField hours() {
        return UnsupportedDurationField.getInstance(DurationFieldType.hours());
    }
    
    @Override
    public DateTimeField hourOfDay() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.hourOfDay(), this.hours());
    }
    
    @Override
    public DateTimeField clockhourOfDay() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.clockhourOfDay(), this.hours());
    }
    
    @Override
    public DurationField halfdays() {
        return UnsupportedDurationField.getInstance(DurationFieldType.halfdays());
    }
    
    @Override
    public DateTimeField hourOfHalfday() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.hourOfHalfday(), this.hours());
    }
    
    @Override
    public DateTimeField clockhourOfHalfday() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.clockhourOfHalfday(), this.hours());
    }
    
    @Override
    public DateTimeField halfdayOfDay() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.halfdayOfDay(), this.halfdays());
    }
    
    @Override
    public DurationField days() {
        return UnsupportedDurationField.getInstance(DurationFieldType.days());
    }
    
    @Override
    public DateTimeField dayOfWeek() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.dayOfWeek(), this.days());
    }
    
    @Override
    public DateTimeField dayOfMonth() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.dayOfMonth(), this.days());
    }
    
    @Override
    public DateTimeField dayOfYear() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.dayOfYear(), this.days());
    }
    
    @Override
    public DurationField weeks() {
        return UnsupportedDurationField.getInstance(DurationFieldType.weeks());
    }
    
    @Override
    public DateTimeField weekOfWeekyear() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.weekOfWeekyear(), this.weeks());
    }
    
    @Override
    public DurationField weekyears() {
        return UnsupportedDurationField.getInstance(DurationFieldType.weekyears());
    }
    
    @Override
    public DateTimeField weekyear() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.weekyear(), this.weekyears());
    }
    
    @Override
    public DateTimeField weekyearOfCentury() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.weekyearOfCentury(), this.weekyears());
    }
    
    @Override
    public DurationField months() {
        return UnsupportedDurationField.getInstance(DurationFieldType.months());
    }
    
    @Override
    public DateTimeField monthOfYear() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.monthOfYear(), this.months());
    }
    
    @Override
    public DurationField years() {
        return UnsupportedDurationField.getInstance(DurationFieldType.years());
    }
    
    @Override
    public DateTimeField year() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.year(), this.years());
    }
    
    @Override
    public DateTimeField yearOfEra() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.yearOfEra(), this.years());
    }
    
    @Override
    public DateTimeField yearOfCentury() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.yearOfCentury(), this.years());
    }
    
    @Override
    public DurationField centuries() {
        return UnsupportedDurationField.getInstance(DurationFieldType.centuries());
    }
    
    @Override
    public DateTimeField centuryOfEra() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.centuryOfEra(), this.centuries());
    }
    
    @Override
    public DurationField eras() {
        return UnsupportedDurationField.getInstance(DurationFieldType.eras());
    }
    
    @Override
    public DateTimeField era() {
        return UnsupportedDateTimeField.getInstance(DateTimeFieldType.era(), this.eras());
    }
    
    @Override
    public abstract String toString();
}
