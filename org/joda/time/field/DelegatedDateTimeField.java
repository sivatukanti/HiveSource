// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.ReadablePartial;
import java.util.Locale;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationField;
import java.io.Serializable;
import org.joda.time.DateTimeField;

public class DelegatedDateTimeField extends DateTimeField implements Serializable
{
    private static final long serialVersionUID = -4730164440214502503L;
    private final DateTimeField iField;
    private final DurationField iRangeDurationField;
    private final DateTimeFieldType iType;
    
    public DelegatedDateTimeField(final DateTimeField dateTimeField) {
        this(dateTimeField, null);
    }
    
    public DelegatedDateTimeField(final DateTimeField dateTimeField, final DateTimeFieldType dateTimeFieldType) {
        this(dateTimeField, null, dateTimeFieldType);
    }
    
    public DelegatedDateTimeField(final DateTimeField iField, final DurationField iRangeDurationField, final DateTimeFieldType dateTimeFieldType) {
        if (iField == null) {
            throw new IllegalArgumentException("The field must not be null");
        }
        this.iField = iField;
        this.iRangeDurationField = iRangeDurationField;
        this.iType = ((dateTimeFieldType == null) ? iField.getType() : dateTimeFieldType);
    }
    
    public final DateTimeField getWrappedField() {
        return this.iField;
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
        return this.iField.isSupported();
    }
    
    @Override
    public boolean isLenient() {
        return this.iField.isLenient();
    }
    
    @Override
    public int get(final long n) {
        return this.iField.get(n);
    }
    
    @Override
    public String getAsText(final long n, final Locale locale) {
        return this.iField.getAsText(n, locale);
    }
    
    @Override
    public String getAsText(final long n) {
        return this.iField.getAsText(n);
    }
    
    @Override
    public String getAsText(final ReadablePartial readablePartial, final int n, final Locale locale) {
        return this.iField.getAsText(readablePartial, n, locale);
    }
    
    @Override
    public String getAsText(final ReadablePartial readablePartial, final Locale locale) {
        return this.iField.getAsText(readablePartial, locale);
    }
    
    @Override
    public String getAsText(final int n, final Locale locale) {
        return this.iField.getAsText(n, locale);
    }
    
    @Override
    public String getAsShortText(final long n, final Locale locale) {
        return this.iField.getAsShortText(n, locale);
    }
    
    @Override
    public String getAsShortText(final long n) {
        return this.iField.getAsShortText(n);
    }
    
    @Override
    public String getAsShortText(final ReadablePartial readablePartial, final int n, final Locale locale) {
        return this.iField.getAsShortText(readablePartial, n, locale);
    }
    
    @Override
    public String getAsShortText(final ReadablePartial readablePartial, final Locale locale) {
        return this.iField.getAsShortText(readablePartial, locale);
    }
    
    @Override
    public String getAsShortText(final int n, final Locale locale) {
        return this.iField.getAsShortText(n, locale);
    }
    
    @Override
    public long add(final long n, final int n2) {
        return this.iField.add(n, n2);
    }
    
    @Override
    public long add(final long n, final long n2) {
        return this.iField.add(n, n2);
    }
    
    @Override
    public int[] add(final ReadablePartial readablePartial, final int n, final int[] array, final int n2) {
        return this.iField.add(readablePartial, n, array, n2);
    }
    
    @Override
    public int[] addWrapPartial(final ReadablePartial readablePartial, final int n, final int[] array, final int n2) {
        return this.iField.addWrapPartial(readablePartial, n, array, n2);
    }
    
    @Override
    public long addWrapField(final long n, final int n2) {
        return this.iField.addWrapField(n, n2);
    }
    
    @Override
    public int[] addWrapField(final ReadablePartial readablePartial, final int n, final int[] array, final int n2) {
        return this.iField.addWrapField(readablePartial, n, array, n2);
    }
    
    @Override
    public int getDifference(final long n, final long n2) {
        return this.iField.getDifference(n, n2);
    }
    
    @Override
    public long getDifferenceAsLong(final long n, final long n2) {
        return this.iField.getDifferenceAsLong(n, n2);
    }
    
    @Override
    public long set(final long n, final int n2) {
        return this.iField.set(n, n2);
    }
    
    @Override
    public long set(final long n, final String s, final Locale locale) {
        return this.iField.set(n, s, locale);
    }
    
    @Override
    public long set(final long n, final String s) {
        return this.iField.set(n, s);
    }
    
    @Override
    public int[] set(final ReadablePartial readablePartial, final int n, final int[] array, final int n2) {
        return this.iField.set(readablePartial, n, array, n2);
    }
    
    @Override
    public int[] set(final ReadablePartial readablePartial, final int n, final int[] array, final String s, final Locale locale) {
        return this.iField.set(readablePartial, n, array, s, locale);
    }
    
    @Override
    public DurationField getDurationField() {
        return this.iField.getDurationField();
    }
    
    @Override
    public DurationField getRangeDurationField() {
        if (this.iRangeDurationField != null) {
            return this.iRangeDurationField;
        }
        return this.iField.getRangeDurationField();
    }
    
    @Override
    public boolean isLeap(final long n) {
        return this.iField.isLeap(n);
    }
    
    @Override
    public int getLeapAmount(final long n) {
        return this.iField.getLeapAmount(n);
    }
    
    @Override
    public DurationField getLeapDurationField() {
        return this.iField.getLeapDurationField();
    }
    
    @Override
    public int getMinimumValue() {
        return this.iField.getMinimumValue();
    }
    
    @Override
    public int getMinimumValue(final long n) {
        return this.iField.getMinimumValue(n);
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
        return this.iField.getMaximumValue(n);
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
    
    @Override
    public long roundFloor(final long n) {
        return this.iField.roundFloor(n);
    }
    
    @Override
    public long roundCeiling(final long n) {
        return this.iField.roundCeiling(n);
    }
    
    @Override
    public long roundHalfFloor(final long n) {
        return this.iField.roundHalfFloor(n);
    }
    
    @Override
    public long roundHalfCeiling(final long n) {
        return this.iField.roundHalfCeiling(n);
    }
    
    @Override
    public long roundHalfEven(final long n) {
        return this.iField.roundHalfEven(n);
    }
    
    @Override
    public long remainder(final long n) {
        return this.iField.remainder(n);
    }
    
    @Override
    public String toString() {
        return "DateTimeField[" + this.getName() + ']';
    }
}
