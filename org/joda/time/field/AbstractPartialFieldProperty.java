// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.ReadableInstant;
import org.joda.time.DurationField;
import java.util.Locale;
import org.joda.time.ReadablePartial;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeField;

public abstract class AbstractPartialFieldProperty
{
    protected AbstractPartialFieldProperty() {
    }
    
    public abstract DateTimeField getField();
    
    public DateTimeFieldType getFieldType() {
        return this.getField().getType();
    }
    
    public String getName() {
        return this.getField().getName();
    }
    
    protected abstract ReadablePartial getReadablePartial();
    
    public abstract int get();
    
    public String getAsString() {
        return Integer.toString(this.get());
    }
    
    public String getAsText() {
        return this.getAsText(null);
    }
    
    public String getAsText(final Locale locale) {
        return this.getField().getAsText(this.getReadablePartial(), this.get(), locale);
    }
    
    public String getAsShortText() {
        return this.getAsShortText(null);
    }
    
    public String getAsShortText(final Locale locale) {
        return this.getField().getAsShortText(this.getReadablePartial(), this.get(), locale);
    }
    
    public DurationField getDurationField() {
        return this.getField().getDurationField();
    }
    
    public DurationField getRangeDurationField() {
        return this.getField().getRangeDurationField();
    }
    
    public int getMinimumValueOverall() {
        return this.getField().getMinimumValue();
    }
    
    public int getMinimumValue() {
        return this.getField().getMinimumValue(this.getReadablePartial());
    }
    
    public int getMaximumValueOverall() {
        return this.getField().getMaximumValue();
    }
    
    public int getMaximumValue() {
        return this.getField().getMaximumValue(this.getReadablePartial());
    }
    
    public int getMaximumTextLength(final Locale locale) {
        return this.getField().getMaximumTextLength(locale);
    }
    
    public int getMaximumShortTextLength(final Locale locale) {
        return this.getField().getMaximumShortTextLength(locale);
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
            throw new IllegalArgumentException("The instant must not be null");
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
        if (!(o instanceof AbstractPartialFieldProperty)) {
            return false;
        }
        final AbstractPartialFieldProperty abstractPartialFieldProperty = (AbstractPartialFieldProperty)o;
        return this.get() == abstractPartialFieldProperty.get() && this.getFieldType() == abstractPartialFieldProperty.getFieldType() && FieldUtils.equals(this.getReadablePartial().getChronology(), abstractPartialFieldProperty.getReadablePartial().getChronology());
    }
    
    @Override
    public int hashCode() {
        return 13 * (13 * (13 * 19 + this.get()) + this.getFieldType().hashCode()) + this.getReadablePartial().getChronology().hashCode();
    }
    
    @Override
    public String toString() {
        return "Property[" + this.getName() + "]";
    }
}
