// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.ReadablePartial;
import java.util.Locale;
import org.joda.time.DurationField;
import org.joda.time.DateTimeFieldType;
import java.util.HashMap;
import java.io.Serializable;
import org.joda.time.DateTimeField;

public final class UnsupportedDateTimeField extends DateTimeField implements Serializable
{
    private static final long serialVersionUID = -1934618396111902255L;
    private static HashMap<DateTimeFieldType, UnsupportedDateTimeField> cCache;
    private final DateTimeFieldType iType;
    private final DurationField iDurationField;
    
    public static synchronized UnsupportedDateTimeField getInstance(final DateTimeFieldType dateTimeFieldType, final DurationField durationField) {
        UnsupportedDateTimeField value;
        if (UnsupportedDateTimeField.cCache == null) {
            UnsupportedDateTimeField.cCache = new HashMap<DateTimeFieldType, UnsupportedDateTimeField>(7);
            value = null;
        }
        else {
            value = UnsupportedDateTimeField.cCache.get(dateTimeFieldType);
            if (value != null && value.getDurationField() != durationField) {
                value = null;
            }
        }
        if (value == null) {
            value = new UnsupportedDateTimeField(dateTimeFieldType, durationField);
            UnsupportedDateTimeField.cCache.put(dateTimeFieldType, value);
        }
        return value;
    }
    
    private UnsupportedDateTimeField(final DateTimeFieldType iType, final DurationField iDurationField) {
        if (iType == null || iDurationField == null) {
            throw new IllegalArgumentException();
        }
        this.iType = iType;
        this.iDurationField = iDurationField;
    }
    
    @Override
    public DateTimeFieldType getType() {
        return this.iType;
    }
    
    @Override
    public String getName() {
        return this.iType.getName();
    }
    
    @Override
    public boolean isSupported() {
        return false;
    }
    
    @Override
    public boolean isLenient() {
        return false;
    }
    
    @Override
    public int get(final long n) {
        throw this.unsupported();
    }
    
    @Override
    public String getAsText(final long n, final Locale locale) {
        throw this.unsupported();
    }
    
    @Override
    public String getAsText(final long n) {
        throw this.unsupported();
    }
    
    @Override
    public String getAsText(final ReadablePartial readablePartial, final int n, final Locale locale) {
        throw this.unsupported();
    }
    
    @Override
    public String getAsText(final ReadablePartial readablePartial, final Locale locale) {
        throw this.unsupported();
    }
    
    @Override
    public String getAsText(final int n, final Locale locale) {
        throw this.unsupported();
    }
    
    @Override
    public String getAsShortText(final long n, final Locale locale) {
        throw this.unsupported();
    }
    
    @Override
    public String getAsShortText(final long n) {
        throw this.unsupported();
    }
    
    @Override
    public String getAsShortText(final ReadablePartial readablePartial, final int n, final Locale locale) {
        throw this.unsupported();
    }
    
    @Override
    public String getAsShortText(final ReadablePartial readablePartial, final Locale locale) {
        throw this.unsupported();
    }
    
    @Override
    public String getAsShortText(final int n, final Locale locale) {
        throw this.unsupported();
    }
    
    @Override
    public long add(final long n, final int n2) {
        return this.getDurationField().add(n, n2);
    }
    
    @Override
    public long add(final long n, final long n2) {
        return this.getDurationField().add(n, n2);
    }
    
    @Override
    public int[] add(final ReadablePartial readablePartial, final int n, final int[] array, final int n2) {
        throw this.unsupported();
    }
    
    @Override
    public int[] addWrapPartial(final ReadablePartial readablePartial, final int n, final int[] array, final int n2) {
        throw this.unsupported();
    }
    
    @Override
    public long addWrapField(final long n, final int n2) {
        throw this.unsupported();
    }
    
    @Override
    public int[] addWrapField(final ReadablePartial readablePartial, final int n, final int[] array, final int n2) {
        throw this.unsupported();
    }
    
    @Override
    public int getDifference(final long n, final long n2) {
        return this.getDurationField().getDifference(n, n2);
    }
    
    @Override
    public long getDifferenceAsLong(final long n, final long n2) {
        return this.getDurationField().getDifferenceAsLong(n, n2);
    }
    
    @Override
    public long set(final long n, final int n2) {
        throw this.unsupported();
    }
    
    @Override
    public int[] set(final ReadablePartial readablePartial, final int n, final int[] array, final int n2) {
        throw this.unsupported();
    }
    
    @Override
    public long set(final long n, final String s, final Locale locale) {
        throw this.unsupported();
    }
    
    @Override
    public long set(final long n, final String s) {
        throw this.unsupported();
    }
    
    @Override
    public int[] set(final ReadablePartial readablePartial, final int n, final int[] array, final String s, final Locale locale) {
        throw this.unsupported();
    }
    
    @Override
    public DurationField getDurationField() {
        return this.iDurationField;
    }
    
    @Override
    public DurationField getRangeDurationField() {
        return null;
    }
    
    @Override
    public boolean isLeap(final long n) {
        throw this.unsupported();
    }
    
    @Override
    public int getLeapAmount(final long n) {
        throw this.unsupported();
    }
    
    @Override
    public DurationField getLeapDurationField() {
        return null;
    }
    
    @Override
    public int getMinimumValue() {
        throw this.unsupported();
    }
    
    @Override
    public int getMinimumValue(final long n) {
        throw this.unsupported();
    }
    
    @Override
    public int getMinimumValue(final ReadablePartial readablePartial) {
        throw this.unsupported();
    }
    
    @Override
    public int getMinimumValue(final ReadablePartial readablePartial, final int[] array) {
        throw this.unsupported();
    }
    
    @Override
    public int getMaximumValue() {
        throw this.unsupported();
    }
    
    @Override
    public int getMaximumValue(final long n) {
        throw this.unsupported();
    }
    
    @Override
    public int getMaximumValue(final ReadablePartial readablePartial) {
        throw this.unsupported();
    }
    
    @Override
    public int getMaximumValue(final ReadablePartial readablePartial, final int[] array) {
        throw this.unsupported();
    }
    
    @Override
    public int getMaximumTextLength(final Locale locale) {
        throw this.unsupported();
    }
    
    @Override
    public int getMaximumShortTextLength(final Locale locale) {
        throw this.unsupported();
    }
    
    @Override
    public long roundFloor(final long n) {
        throw this.unsupported();
    }
    
    @Override
    public long roundCeiling(final long n) {
        throw this.unsupported();
    }
    
    @Override
    public long roundHalfFloor(final long n) {
        throw this.unsupported();
    }
    
    @Override
    public long roundHalfCeiling(final long n) {
        throw this.unsupported();
    }
    
    @Override
    public long roundHalfEven(final long n) {
        throw this.unsupported();
    }
    
    @Override
    public long remainder(final long n) {
        throw this.unsupported();
    }
    
    @Override
    public String toString() {
        return "UnsupportedDateTimeField";
    }
    
    private Object readResolve() {
        return getInstance(this.iType, this.iDurationField);
    }
    
    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException(this.iType + " field is unsupported");
    }
}
