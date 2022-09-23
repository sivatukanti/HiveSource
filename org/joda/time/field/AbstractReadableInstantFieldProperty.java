// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.ReadablePartial;
import org.joda.time.Interval;
import org.joda.time.DurationField;
import org.joda.time.DateTimeUtils;
import org.joda.time.ReadableInstant;
import java.util.Locale;
import org.joda.time.Chronology;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeField;
import java.io.Serializable;

public abstract class AbstractReadableInstantFieldProperty implements Serializable
{
    private static final long serialVersionUID = 1971226328211649661L;
    
    public abstract DateTimeField getField();
    
    public DateTimeFieldType getFieldType() {
        return this.getField().getType();
    }
    
    public String getName() {
        return this.getField().getName();
    }
    
    protected abstract long getMillis();
    
    protected Chronology getChronology() {
        throw new UnsupportedOperationException("The method getChronology() was added in v1.4 and needs to be implemented by subclasses of AbstractReadableInstantFieldProperty");
    }
    
    public int get() {
        return this.getField().get(this.getMillis());
    }
    
    public String getAsString() {
        return Integer.toString(this.get());
    }
    
    public String getAsText() {
        return this.getAsText(null);
    }
    
    public String getAsText(final Locale locale) {
        return this.getField().getAsText(this.getMillis(), locale);
    }
    
    public String getAsShortText() {
        return this.getAsShortText(null);
    }
    
    public String getAsShortText(final Locale locale) {
        return this.getField().getAsShortText(this.getMillis(), locale);
    }
    
    public int getDifference(final ReadableInstant readableInstant) {
        if (readableInstant == null) {
            return this.getField().getDifference(this.getMillis(), DateTimeUtils.currentTimeMillis());
        }
        return this.getField().getDifference(this.getMillis(), readableInstant.getMillis());
    }
    
    public long getDifferenceAsLong(final ReadableInstant readableInstant) {
        if (readableInstant == null) {
            return this.getField().getDifferenceAsLong(this.getMillis(), DateTimeUtils.currentTimeMillis());
        }
        return this.getField().getDifferenceAsLong(this.getMillis(), readableInstant.getMillis());
    }
    
    public DurationField getDurationField() {
        return this.getField().getDurationField();
    }
    
    public DurationField getRangeDurationField() {
        return this.getField().getRangeDurationField();
    }
    
    public boolean isLeap() {
        return this.getField().isLeap(this.getMillis());
    }
    
    public int getLeapAmount() {
        return this.getField().getLeapAmount(this.getMillis());
    }
    
    public DurationField getLeapDurationField() {
        return this.getField().getLeapDurationField();
    }
    
    public int getMinimumValueOverall() {
        return this.getField().getMinimumValue();
    }
    
    public int getMinimumValue() {
        return this.getField().getMinimumValue(this.getMillis());
    }
    
    public int getMaximumValueOverall() {
        return this.getField().getMaximumValue();
    }
    
    public int getMaximumValue() {
        return this.getField().getMaximumValue(this.getMillis());
    }
    
    public int getMaximumTextLength(final Locale locale) {
        return this.getField().getMaximumTextLength(locale);
    }
    
    public int getMaximumShortTextLength(final Locale locale) {
        return this.getField().getMaximumShortTextLength(locale);
    }
    
    public long remainder() {
        return this.getField().remainder(this.getMillis());
    }
    
    public Interval toInterval() {
        final DateTimeField field = this.getField();
        final long roundFloor = field.roundFloor(this.getMillis());
        return new Interval(roundFloor, field.add(roundFloor, 1));
    }
    
    public int compareTo(final ReadableInstant readableInstant) {
        if (readableInstant == null) {
            throw new IllegalArgumentException("The instant must not be null");
        }
        final int value = this.get();
        final int value2 = readableInstant.get(this.getFieldType());
        if (value < value2) {
            return -1;
        }
        if (value > value2) {
            return 1;
        }
        return 0;
    }
    
    public int compareTo(final ReadablePartial readablePartial) {
        if (readablePartial == null) {
            throw new IllegalArgumentException("The partial must not be null");
        }
        final int value = this.get();
        final int value2 = readablePartial.get(this.getFieldType());
        if (value < value2) {
            return -1;
        }
        if (value > value2) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractReadableInstantFieldProperty)) {
            return false;
        }
        final AbstractReadableInstantFieldProperty abstractReadableInstantFieldProperty = (AbstractReadableInstantFieldProperty)o;
        return this.get() == abstractReadableInstantFieldProperty.get() && this.getFieldType().equals(abstractReadableInstantFieldProperty.getFieldType()) && FieldUtils.equals(this.getChronology(), abstractReadableInstantFieldProperty.getChronology());
    }
    
    @Override
    public int hashCode() {
        return this.get() * 17 + this.getFieldType().hashCode() + this.getChronology().hashCode();
    }
    
    @Override
    public String toString() {
        return "Property[" + this.getName() + "]";
    }
}
