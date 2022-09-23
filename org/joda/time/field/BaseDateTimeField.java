// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.DurationField;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.ReadablePartial;
import java.util.Locale;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeField;

public abstract class BaseDateTimeField extends DateTimeField
{
    private final DateTimeFieldType iType;
    
    protected BaseDateTimeField(final DateTimeFieldType iType) {
        if (iType == null) {
            throw new IllegalArgumentException("The type must not be null");
        }
        this.iType = iType;
    }
    
    @Override
    public final DateTimeFieldType getType() {
        return this.iType;
    }
    
    @Override
    public final String getName() {
        return this.iType.getName();
    }
    
    @Override
    public final boolean isSupported() {
        return true;
    }
    
    @Override
    public abstract int get(final long p0);
    
    @Override
    public String getAsText(final long n, final Locale locale) {
        return this.getAsText(this.get(n), locale);
    }
    
    @Override
    public final String getAsText(final long n) {
        return this.getAsText(n, null);
    }
    
    @Override
    public String getAsText(final ReadablePartial readablePartial, final int n, final Locale locale) {
        return this.getAsText(n, locale);
    }
    
    @Override
    public final String getAsText(final ReadablePartial readablePartial, final Locale locale) {
        return this.getAsText(readablePartial, readablePartial.get(this.getType()), locale);
    }
    
    @Override
    public String getAsText(final int i, final Locale locale) {
        return Integer.toString(i);
    }
    
    @Override
    public String getAsShortText(final long n, final Locale locale) {
        return this.getAsShortText(this.get(n), locale);
    }
    
    @Override
    public final String getAsShortText(final long n) {
        return this.getAsShortText(n, null);
    }
    
    @Override
    public String getAsShortText(final ReadablePartial readablePartial, final int n, final Locale locale) {
        return this.getAsShortText(n, locale);
    }
    
    @Override
    public final String getAsShortText(final ReadablePartial readablePartial, final Locale locale) {
        return this.getAsShortText(readablePartial, readablePartial.get(this.getType()), locale);
    }
    
    @Override
    public String getAsShortText(final int n, final Locale locale) {
        return this.getAsText(n, locale);
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
    public int[] add(final ReadablePartial readablePartial, final int n, int[] array, int i) {
        if (i == 0) {
            return array;
        }
        DateTimeField dateTimeField;
        int maximumValue;
        for (dateTimeField = null; i > 0; i -= maximumValue + 1 - array[n], array = dateTimeField.add(readablePartial, n - 1, array, 1), array[n] = this.getMinimumValue(readablePartial, array)) {
            maximumValue = this.getMaximumValue(readablePartial, array);
            final long n2 = array[n] + i;
            if (n2 <= maximumValue) {
                array[n] = (int)n2;
                break;
            }
            if (dateTimeField == null) {
                if (n == 0) {
                    throw new IllegalArgumentException("Maximum value exceeded for add");
                }
                dateTimeField = readablePartial.getField(n - 1);
                if (this.getRangeDurationField().getType() != dateTimeField.getDurationField().getType()) {
                    throw new IllegalArgumentException("Fields invalid for add");
                }
            }
        }
        while (i < 0) {
            final int minimumValue = this.getMinimumValue(readablePartial, array);
            final long n3 = array[n] + i;
            if (n3 >= minimumValue) {
                array[n] = (int)n3;
                break;
            }
            if (dateTimeField == null) {
                if (n == 0) {
                    throw new IllegalArgumentException("Maximum value exceeded for add");
                }
                dateTimeField = readablePartial.getField(n - 1);
                if (this.getRangeDurationField().getType() != dateTimeField.getDurationField().getType()) {
                    throw new IllegalArgumentException("Fields invalid for add");
                }
            }
            i -= minimumValue - 1 - array[n];
            array = dateTimeField.add(readablePartial, n - 1, array, -1);
            array[n] = this.getMaximumValue(readablePartial, array);
        }
        return this.set(readablePartial, n, array, array[n]);
    }
    
    @Override
    public int[] addWrapPartial(final ReadablePartial readablePartial, final int n, int[] array, int i) {
        if (i == 0) {
            return array;
        }
        DateTimeField dateTimeField = null;
        while (i > 0) {
            final int maximumValue = this.getMaximumValue(readablePartial, array);
            final long n2 = array[n] + i;
            if (n2 <= maximumValue) {
                array[n] = (int)n2;
                break;
            }
            if (dateTimeField == null) {
                if (n == 0) {
                    i -= maximumValue + 1 - array[n];
                    array[n] = this.getMinimumValue(readablePartial, array);
                    continue;
                }
                dateTimeField = readablePartial.getField(n - 1);
                if (this.getRangeDurationField().getType() != dateTimeField.getDurationField().getType()) {
                    throw new IllegalArgumentException("Fields invalid for add");
                }
            }
            i -= maximumValue + 1 - array[n];
            array = dateTimeField.addWrapPartial(readablePartial, n - 1, array, 1);
            array[n] = this.getMinimumValue(readablePartial, array);
        }
        while (i < 0) {
            final int minimumValue = this.getMinimumValue(readablePartial, array);
            final long n3 = array[n] + i;
            if (n3 >= minimumValue) {
                array[n] = (int)n3;
                break;
            }
            if (dateTimeField == null) {
                if (n == 0) {
                    i -= minimumValue - 1 - array[n];
                    array[n] = this.getMaximumValue(readablePartial, array);
                    continue;
                }
                dateTimeField = readablePartial.getField(n - 1);
                if (this.getRangeDurationField().getType() != dateTimeField.getDurationField().getType()) {
                    throw new IllegalArgumentException("Fields invalid for add");
                }
            }
            i -= minimumValue - 1 - array[n];
            array = dateTimeField.addWrapPartial(readablePartial, n - 1, array, -1);
            array[n] = this.getMaximumValue(readablePartial, array);
        }
        return this.set(readablePartial, n, array, array[n]);
    }
    
    @Override
    public long addWrapField(final long n, final int n2) {
        return this.set(n, FieldUtils.getWrappedValue(this.get(n), n2, this.getMinimumValue(n), this.getMaximumValue(n)));
    }
    
    @Override
    public int[] addWrapField(final ReadablePartial readablePartial, final int n, final int[] array, final int n2) {
        return this.set(readablePartial, n, array, FieldUtils.getWrappedValue(array[n], n2, this.getMinimumValue(readablePartial), this.getMaximumValue(readablePartial)));
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
    public abstract long set(final long p0, final int p1);
    
    @Override
    public int[] set(final ReadablePartial readablePartial, final int n, final int[] array, final int n2) {
        FieldUtils.verifyValueBounds(this, n2, this.getMinimumValue(readablePartial, array), this.getMaximumValue(readablePartial, array));
        array[n] = n2;
        for (int i = n + 1; i < readablePartial.size(); ++i) {
            final DateTimeField field = readablePartial.getField(i);
            if (array[i] > field.getMaximumValue(readablePartial, array)) {
                array[i] = field.getMaximumValue(readablePartial, array);
            }
            if (array[i] < field.getMinimumValue(readablePartial, array)) {
                array[i] = field.getMinimumValue(readablePartial, array);
            }
        }
        return array;
    }
    
    @Override
    public long set(final long n, final String s, final Locale locale) {
        return this.set(n, this.convertText(s, locale));
    }
    
    @Override
    public final long set(final long n, final String s) {
        return this.set(n, s, null);
    }
    
    @Override
    public int[] set(final ReadablePartial readablePartial, final int n, final int[] array, final String s, final Locale locale) {
        return this.set(readablePartial, n, array, this.convertText(s, locale));
    }
    
    protected int convertText(final String s, final Locale locale) {
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException ex) {
            throw new IllegalFieldValueException(this.getType(), s);
        }
    }
    
    @Override
    public abstract DurationField getDurationField();
    
    @Override
    public abstract DurationField getRangeDurationField();
    
    @Override
    public boolean isLeap(final long n) {
        return false;
    }
    
    @Override
    public int getLeapAmount(final long n) {
        return 0;
    }
    
    @Override
    public DurationField getLeapDurationField() {
        return null;
    }
    
    @Override
    public abstract int getMinimumValue();
    
    @Override
    public int getMinimumValue(final long n) {
        return this.getMinimumValue();
    }
    
    @Override
    public int getMinimumValue(final ReadablePartial readablePartial) {
        return this.getMinimumValue();
    }
    
    @Override
    public int getMinimumValue(final ReadablePartial readablePartial, final int[] array) {
        return this.getMinimumValue(readablePartial);
    }
    
    @Override
    public abstract int getMaximumValue();
    
    @Override
    public int getMaximumValue(final long n) {
        return this.getMaximumValue();
    }
    
    @Override
    public int getMaximumValue(final ReadablePartial readablePartial) {
        return this.getMaximumValue();
    }
    
    @Override
    public int getMaximumValue(final ReadablePartial readablePartial, final int[] array) {
        return this.getMaximumValue(readablePartial);
    }
    
    @Override
    public int getMaximumTextLength(final Locale locale) {
        final int maximumValue = this.getMaximumValue();
        if (maximumValue >= 0) {
            if (maximumValue < 10) {
                return 1;
            }
            if (maximumValue < 100) {
                return 2;
            }
            if (maximumValue < 1000) {
                return 3;
            }
        }
        return Integer.toString(maximumValue).length();
    }
    
    @Override
    public int getMaximumShortTextLength(final Locale locale) {
        return this.getMaximumTextLength(locale);
    }
    
    @Override
    public abstract long roundFloor(final long p0);
    
    @Override
    public long roundCeiling(long add) {
        final long roundFloor = this.roundFloor(add);
        if (roundFloor != add) {
            add = this.add(roundFloor, 1);
        }
        return add;
    }
    
    @Override
    public long roundHalfFloor(final long n) {
        final long roundFloor = this.roundFloor(n);
        final long roundCeiling = this.roundCeiling(n);
        if (n - roundFloor <= roundCeiling - n) {
            return roundFloor;
        }
        return roundCeiling;
    }
    
    @Override
    public long roundHalfCeiling(final long n) {
        final long roundFloor = this.roundFloor(n);
        final long roundCeiling = this.roundCeiling(n);
        if (roundCeiling - n <= n - roundFloor) {
            return roundCeiling;
        }
        return roundFloor;
    }
    
    @Override
    public long roundHalfEven(final long n) {
        final long roundFloor = this.roundFloor(n);
        final long roundCeiling = this.roundCeiling(n);
        final long n2 = n - roundFloor;
        final long n3 = roundCeiling - n;
        if (n2 < n3) {
            return roundFloor;
        }
        if (n3 < n2) {
            return roundCeiling;
        }
        if ((this.get(roundCeiling) & 0x1) == 0x0) {
            return roundCeiling;
        }
        return roundFloor;
    }
    
    @Override
    public long remainder(final long n) {
        return n - this.roundFloor(n);
    }
    
    @Override
    public String toString() {
        return "DateTimeField[" + this.getName() + ']';
    }
}
