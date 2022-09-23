// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.base;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.field.FieldUtils;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.DurationFieldType;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeField;
import org.joda.time.Chronology;
import org.joda.time.ReadablePartial;

public abstract class AbstractPartial implements ReadablePartial, Comparable<ReadablePartial>
{
    protected AbstractPartial() {
    }
    
    protected abstract DateTimeField getField(final int p0, final Chronology p1);
    
    public DateTimeFieldType getFieldType(final int n) {
        return this.getField(n, this.getChronology()).getType();
    }
    
    public DateTimeFieldType[] getFieldTypes() {
        final DateTimeFieldType[] array = new DateTimeFieldType[this.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.getFieldType(i);
        }
        return array;
    }
    
    public DateTimeField getField(final int n) {
        return this.getField(n, this.getChronology());
    }
    
    public DateTimeField[] getFields() {
        final DateTimeField[] array = new DateTimeField[this.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.getField(i);
        }
        return array;
    }
    
    public int[] getValues() {
        final int[] array = new int[this.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.getValue(i);
        }
        return array;
    }
    
    public int get(final DateTimeFieldType dateTimeFieldType) {
        return this.getValue(this.indexOfSupported(dateTimeFieldType));
    }
    
    public boolean isSupported(final DateTimeFieldType dateTimeFieldType) {
        return this.indexOf(dateTimeFieldType) != -1;
    }
    
    public int indexOf(final DateTimeFieldType dateTimeFieldType) {
        for (int i = 0; i < this.size(); ++i) {
            if (this.getFieldType(i) == dateTimeFieldType) {
                return i;
            }
        }
        return -1;
    }
    
    protected int indexOfSupported(final DateTimeFieldType obj) {
        final int index = this.indexOf(obj);
        if (index == -1) {
            throw new IllegalArgumentException("Field '" + obj + "' is not supported");
        }
        return index;
    }
    
    protected int indexOf(final DurationFieldType durationFieldType) {
        for (int i = 0; i < this.size(); ++i) {
            if (this.getFieldType(i).getDurationType() == durationFieldType) {
                return i;
            }
        }
        return -1;
    }
    
    protected int indexOfSupported(final DurationFieldType obj) {
        final int index = this.indexOf(obj);
        if (index == -1) {
            throw new IllegalArgumentException("Field '" + obj + "' is not supported");
        }
        return index;
    }
    
    public DateTime toDateTime(final ReadableInstant readableInstant) {
        final Chronology instantChronology = DateTimeUtils.getInstantChronology(readableInstant);
        return new DateTime(instantChronology.set(this, DateTimeUtils.getInstantMillis(readableInstant)), instantChronology);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReadablePartial)) {
            return false;
        }
        final ReadablePartial readablePartial = (ReadablePartial)o;
        if (this.size() != readablePartial.size()) {
            return false;
        }
        for (int i = 0; i < this.size(); ++i) {
            if (this.getValue(i) != readablePartial.getValue(i) || this.getFieldType(i) != readablePartial.getFieldType(i)) {
                return false;
            }
        }
        return FieldUtils.equals(this.getChronology(), readablePartial.getChronology());
    }
    
    @Override
    public int hashCode() {
        int n = 157;
        for (int i = 0; i < this.size(); ++i) {
            n = 23 * (23 * n + this.getValue(i)) + this.getFieldType(i).hashCode();
        }
        return n + this.getChronology().hashCode();
    }
    
    public int compareTo(final ReadablePartial readablePartial) {
        if (this == readablePartial) {
            return 0;
        }
        if (this.size() != readablePartial.size()) {
            throw new ClassCastException("ReadablePartial objects must have matching field types");
        }
        for (int i = 0; i < this.size(); ++i) {
            if (this.getFieldType(i) != readablePartial.getFieldType(i)) {
                throw new ClassCastException("ReadablePartial objects must have matching field types");
            }
        }
        for (int j = 0; j < this.size(); ++j) {
            if (this.getValue(j) > readablePartial.getValue(j)) {
                return 1;
            }
            if (this.getValue(j) < readablePartial.getValue(j)) {
                return -1;
            }
        }
        return 0;
    }
    
    public boolean isAfter(final ReadablePartial readablePartial) {
        if (readablePartial == null) {
            throw new IllegalArgumentException("Partial cannot be null");
        }
        return this.compareTo(readablePartial) > 0;
    }
    
    public boolean isBefore(final ReadablePartial readablePartial) {
        if (readablePartial == null) {
            throw new IllegalArgumentException("Partial cannot be null");
        }
        return this.compareTo(readablePartial) < 0;
    }
    
    public boolean isEqual(final ReadablePartial readablePartial) {
        if (readablePartial == null) {
            throw new IllegalArgumentException("Partial cannot be null");
        }
        return this.compareTo(readablePartial) == 0;
    }
    
    public String toString(final DateTimeFormatter dateTimeFormatter) {
        if (dateTimeFormatter == null) {
            return this.toString();
        }
        return dateTimeFormatter.print(this);
    }
}
