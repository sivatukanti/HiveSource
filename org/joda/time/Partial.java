// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import org.joda.time.field.AbstractPartialFieldProperty;
import java.util.Locale;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.DateTimeFormatter;
import java.io.Serializable;
import org.joda.time.base.AbstractPartial;

public final class Partial extends AbstractPartial implements ReadablePartial, Serializable
{
    private static final long serialVersionUID = 12324121189002L;
    private final Chronology iChronology;
    private final DateTimeFieldType[] iTypes;
    private final int[] iValues;
    private transient DateTimeFormatter[] iFormatter;
    
    public Partial() {
        this((Chronology)null);
    }
    
    public Partial(final Chronology chronology) {
        this.iChronology = DateTimeUtils.getChronology(chronology).withUTC();
        this.iTypes = new DateTimeFieldType[0];
        this.iValues = new int[0];
    }
    
    public Partial(final DateTimeFieldType dateTimeFieldType, final int n) {
        this(dateTimeFieldType, n, null);
    }
    
    public Partial(final DateTimeFieldType dateTimeFieldType, final int n, Chronology withUTC) {
        withUTC = DateTimeUtils.getChronology(withUTC).withUTC();
        this.iChronology = withUTC;
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("The field type must not be null");
        }
        this.iTypes = new DateTimeFieldType[] { dateTimeFieldType };
        withUTC.validate(this, this.iValues = new int[] { n });
    }
    
    public Partial(final DateTimeFieldType[] array, final int[] array2) {
        this(array, array2, null);
    }
    
    public Partial(final DateTimeFieldType[] iTypes, final int[] iValues, Chronology withUTC) {
        withUTC = DateTimeUtils.getChronology(withUTC).withUTC();
        this.iChronology = withUTC;
        if (iTypes == null) {
            throw new IllegalArgumentException("Types array must not be null");
        }
        if (iValues == null) {
            throw new IllegalArgumentException("Values array must not be null");
        }
        if (iValues.length != iTypes.length) {
            throw new IllegalArgumentException("Values array must be the same length as the types array");
        }
        if (iTypes.length == 0) {
            this.iTypes = iTypes;
            this.iValues = iValues;
            return;
        }
        for (int i = 0; i < iTypes.length; ++i) {
            if (iTypes[i] == null) {
                throw new IllegalArgumentException("Types array must not contain null: index " + i);
            }
        }
        DurationField durationField = null;
        for (int j = 0; j < iTypes.length; ++j) {
            final DateTimeFieldType dateTimeFieldType = iTypes[j];
            final DurationField field = dateTimeFieldType.getDurationType().getField(this.iChronology);
            if (j > 0) {
                if (!field.isSupported()) {
                    if (durationField.isSupported()) {
                        throw new IllegalArgumentException("Types array must be in order largest-smallest: " + iTypes[j - 1].getName() + " < " + dateTimeFieldType.getName());
                    }
                    throw new IllegalArgumentException("Types array must not contain duplicate unsupported: " + iTypes[j - 1].getName() + " and " + dateTimeFieldType.getName());
                }
                else {
                    final int compareTo = durationField.compareTo(field);
                    if (compareTo < 0) {
                        throw new IllegalArgumentException("Types array must be in order largest-smallest: " + iTypes[j - 1].getName() + " < " + dateTimeFieldType.getName());
                    }
                    if (compareTo == 0) {
                        if (durationField.equals(field)) {
                            final DurationFieldType rangeDurationType = iTypes[j - 1].getRangeDurationType();
                            final DurationFieldType rangeDurationType2 = dateTimeFieldType.getRangeDurationType();
                            if (rangeDurationType == null) {
                                if (rangeDurationType2 == null) {
                                    throw new IllegalArgumentException("Types array must not contain duplicate: " + iTypes[j - 1].getName() + " and " + dateTimeFieldType.getName());
                                }
                            }
                            else {
                                if (rangeDurationType2 == null) {
                                    throw new IllegalArgumentException("Types array must be in order largest-smallest: " + iTypes[j - 1].getName() + " < " + dateTimeFieldType.getName());
                                }
                                final DurationField field2 = rangeDurationType.getField(this.iChronology);
                                final DurationField field3 = rangeDurationType2.getField(this.iChronology);
                                if (field2.compareTo(field3) < 0) {
                                    throw new IllegalArgumentException("Types array must be in order largest-smallest: " + iTypes[j - 1].getName() + " < " + dateTimeFieldType.getName());
                                }
                                if (field2.compareTo(field3) == 0) {
                                    throw new IllegalArgumentException("Types array must not contain duplicate: " + iTypes[j - 1].getName() + " and " + dateTimeFieldType.getName());
                                }
                            }
                        }
                        else if (durationField.isSupported() && durationField.getType() != DurationFieldType.YEARS_TYPE) {
                            throw new IllegalArgumentException("Types array must be in order largest-smallest, for year-based fields, years is defined as being largest: " + iTypes[j - 1].getName() + " < " + dateTimeFieldType.getName());
                        }
                    }
                }
            }
            durationField = field;
        }
        this.iTypes = iTypes.clone();
        withUTC.validate(this, iValues);
        this.iValues = iValues.clone();
    }
    
    public Partial(final ReadablePartial readablePartial) {
        if (readablePartial == null) {
            throw new IllegalArgumentException("The partial must not be null");
        }
        this.iChronology = DateTimeUtils.getChronology(readablePartial.getChronology()).withUTC();
        this.iTypes = new DateTimeFieldType[readablePartial.size()];
        this.iValues = new int[readablePartial.size()];
        for (int i = 0; i < readablePartial.size(); ++i) {
            this.iTypes[i] = readablePartial.getFieldType(i);
            this.iValues[i] = readablePartial.getValue(i);
        }
    }
    
    Partial(final Partial partial, final int[] iValues) {
        this.iChronology = partial.iChronology;
        this.iTypes = partial.iTypes;
        this.iValues = iValues;
    }
    
    Partial(final Chronology iChronology, final DateTimeFieldType[] iTypes, final int[] iValues) {
        this.iChronology = iChronology;
        this.iTypes = iTypes;
        this.iValues = iValues;
    }
    
    public int size() {
        return this.iTypes.length;
    }
    
    public Chronology getChronology() {
        return this.iChronology;
    }
    
    @Override
    protected DateTimeField getField(final int n, final Chronology chronology) {
        return this.iTypes[n].getField(chronology);
    }
    
    @Override
    public DateTimeFieldType getFieldType(final int n) {
        return this.iTypes[n];
    }
    
    @Override
    public DateTimeFieldType[] getFieldTypes() {
        return this.iTypes.clone();
    }
    
    public int getValue(final int n) {
        return this.iValues[n];
    }
    
    @Override
    public int[] getValues() {
        return this.iValues.clone();
    }
    
    public Partial withChronologyRetainFields(Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        chronology = chronology.withUTC();
        if (chronology == this.getChronology()) {
            return this;
        }
        final Partial partial = new Partial(chronology, this.iTypes, this.iValues);
        chronology.validate(partial, this.iValues);
        return partial;
    }
    
    public Partial with(final DateTimeFieldType dateTimeFieldType, final int n) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("The field type must not be null");
        }
        final int index = this.indexOf(dateTimeFieldType);
        if (index == -1) {
            final DateTimeFieldType[] array = new DateTimeFieldType[this.iTypes.length + 1];
            final int[] array2 = new int[array.length];
            int i = 0;
            final DurationField field = dateTimeFieldType.getDurationType().getField(this.iChronology);
            if (field.isSupported()) {
                while (i < this.iTypes.length) {
                    final DateTimeFieldType dateTimeFieldType2 = this.iTypes[i];
                    final DurationField field2 = dateTimeFieldType2.getDurationType().getField(this.iChronology);
                    if (field2.isSupported()) {
                        final int compareTo = field.compareTo(field2);
                        if (compareTo > 0) {
                            break;
                        }
                        if (compareTo == 0) {
                            if (dateTimeFieldType.getRangeDurationType() == null) {
                                break;
                            }
                            if (dateTimeFieldType2.getRangeDurationType() != null) {
                                if (dateTimeFieldType.getRangeDurationType().getField(this.iChronology).compareTo(dateTimeFieldType2.getRangeDurationType().getField(this.iChronology)) > 0) {
                                    break;
                                }
                            }
                        }
                    }
                    ++i;
                }
            }
            System.arraycopy(this.iTypes, 0, array, 0, i);
            System.arraycopy(this.iValues, 0, array2, 0, i);
            array[i] = dateTimeFieldType;
            array2[i] = n;
            System.arraycopy(this.iTypes, i, array, i + 1, array.length - i - 1);
            System.arraycopy(this.iValues, i, array2, i + 1, array2.length - i - 1);
            final Partial partial = new Partial(array, array2, this.iChronology);
            this.iChronology.validate(partial, array2);
            return partial;
        }
        if (n == this.getValue(index)) {
            return this;
        }
        return new Partial(this, this.getField(index).set(this, index, this.getValues(), n));
    }
    
    public Partial without(final DateTimeFieldType dateTimeFieldType) {
        final int index = this.indexOf(dateTimeFieldType);
        if (index != -1) {
            final DateTimeFieldType[] array = new DateTimeFieldType[this.size() - 1];
            final int[] array2 = new int[this.size() - 1];
            System.arraycopy(this.iTypes, 0, array, 0, index);
            System.arraycopy(this.iTypes, index + 1, array, index, array.length - index);
            System.arraycopy(this.iValues, 0, array2, 0, index);
            System.arraycopy(this.iValues, index + 1, array2, index, array2.length - index);
            final Partial partial = new Partial(this.iChronology, array, array2);
            this.iChronology.validate(partial, array2);
            return partial;
        }
        return this;
    }
    
    public Partial withField(final DateTimeFieldType dateTimeFieldType, final int n) {
        final int indexOfSupported = this.indexOfSupported(dateTimeFieldType);
        if (n == this.getValue(indexOfSupported)) {
            return this;
        }
        return new Partial(this, this.getField(indexOfSupported).set(this, indexOfSupported, this.getValues(), n));
    }
    
    public Partial withFieldAdded(final DurationFieldType durationFieldType, final int n) {
        final int indexOfSupported = this.indexOfSupported(durationFieldType);
        if (n == 0) {
            return this;
        }
        return new Partial(this, this.getField(indexOfSupported).add(this, indexOfSupported, this.getValues(), n));
    }
    
    public Partial withFieldAddWrapped(final DurationFieldType durationFieldType, final int n) {
        final int indexOfSupported = this.indexOfSupported(durationFieldType);
        if (n == 0) {
            return this;
        }
        return new Partial(this, this.getField(indexOfSupported).addWrapPartial(this, indexOfSupported, this.getValues(), n));
    }
    
    public Partial withPeriodAdded(final ReadablePeriod readablePeriod, final int n) {
        if (readablePeriod == null || n == 0) {
            return this;
        }
        int[] array = this.getValues();
        for (int i = 0; i < readablePeriod.size(); ++i) {
            final int index = this.indexOf(readablePeriod.getFieldType(i));
            if (index >= 0) {
                array = this.getField(index).add(this, index, array, FieldUtils.safeMultiply(readablePeriod.getValue(i), n));
            }
        }
        return new Partial(this, array);
    }
    
    public Partial plus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, 1);
    }
    
    public Partial minus(final ReadablePeriod readablePeriod) {
        return this.withPeriodAdded(readablePeriod, -1);
    }
    
    public Property property(final DateTimeFieldType dateTimeFieldType) {
        return new Property(this, this.indexOfSupported(dateTimeFieldType));
    }
    
    public boolean isMatch(final ReadableInstant readableInstant) {
        final long instantMillis = DateTimeUtils.getInstantMillis(readableInstant);
        final Chronology instantChronology = DateTimeUtils.getInstantChronology(readableInstant);
        for (int i = 0; i < this.iTypes.length; ++i) {
            if (this.iTypes[i].getField(instantChronology).get(instantMillis) != this.iValues[i]) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isMatch(final ReadablePartial readablePartial) {
        if (readablePartial == null) {
            throw new IllegalArgumentException("The partial must not be null");
        }
        for (int i = 0; i < this.iTypes.length; ++i) {
            if (readablePartial.get(this.iTypes[i]) != this.iValues[i]) {
                return false;
            }
        }
        return true;
    }
    
    public DateTimeFormatter getFormatter() {
        DateTimeFormatter[] iFormatter = this.iFormatter;
        if (iFormatter == null) {
            if (this.size() == 0) {
                return null;
            }
            iFormatter = new DateTimeFormatter[2];
            try {
                final ArrayList<DateTimeFieldType> list = new ArrayList<DateTimeFieldType>(Arrays.asList(this.iTypes));
                iFormatter[0] = ISODateTimeFormat.forFields(list, true, false);
                if (list.size() == 0) {
                    iFormatter[1] = iFormatter[0];
                }
            }
            catch (IllegalArgumentException ex) {}
            this.iFormatter = iFormatter;
        }
        return iFormatter[0];
    }
    
    @Override
    public String toString() {
        DateTimeFormatter[] array = this.iFormatter;
        if (array == null) {
            this.getFormatter();
            array = this.iFormatter;
            if (array == null) {
                return this.toStringList();
            }
        }
        final DateTimeFormatter dateTimeFormatter = array[1];
        if (dateTimeFormatter == null) {
            return this.toStringList();
        }
        return dateTimeFormatter.print(this);
    }
    
    public String toStringList() {
        final int size = this.size();
        final StringBuilder sb = new StringBuilder(20 * size);
        sb.append('[');
        for (int i = 0; i < size; ++i) {
            if (i > 0) {
                sb.append(',').append(' ');
            }
            sb.append(this.iTypes[i].getName());
            sb.append('=');
            sb.append(this.iValues[i]);
        }
        sb.append(']');
        return sb.toString();
    }
    
    public String toString(final String s) {
        if (s == null) {
            return this.toString();
        }
        return DateTimeFormat.forPattern(s).print(this);
    }
    
    public String toString(final String s, final Locale locale) {
        if (s == null) {
            return this.toString();
        }
        return DateTimeFormat.forPattern(s).withLocale(locale).print(this);
    }
    
    public static class Property extends AbstractPartialFieldProperty implements Serializable
    {
        private static final long serialVersionUID = 53278362873888L;
        private final Partial iPartial;
        private final int iFieldIndex;
        
        Property(final Partial iPartial, final int iFieldIndex) {
            this.iPartial = iPartial;
            this.iFieldIndex = iFieldIndex;
        }
        
        @Override
        public DateTimeField getField() {
            return this.iPartial.getField(this.iFieldIndex);
        }
        
        @Override
        protected ReadablePartial getReadablePartial() {
            return this.iPartial;
        }
        
        public Partial getPartial() {
            return this.iPartial;
        }
        
        @Override
        public int get() {
            return this.iPartial.getValue(this.iFieldIndex);
        }
        
        public Partial addToCopy(final int n) {
            return new Partial(this.iPartial, this.getField().add(this.iPartial, this.iFieldIndex, this.iPartial.getValues(), n));
        }
        
        public Partial addWrapFieldToCopy(final int n) {
            return new Partial(this.iPartial, this.getField().addWrapField(this.iPartial, this.iFieldIndex, this.iPartial.getValues(), n));
        }
        
        public Partial setCopy(final int n) {
            return new Partial(this.iPartial, this.getField().set(this.iPartial, this.iFieldIndex, this.iPartial.getValues(), n));
        }
        
        public Partial setCopy(final String s, final Locale locale) {
            return new Partial(this.iPartial, this.getField().set(this.iPartial, this.iFieldIndex, this.iPartial.getValues(), s, locale));
        }
        
        public Partial setCopy(final String s) {
            return this.setCopy(s, null);
        }
        
        public Partial withMaximumValue() {
            return this.setCopy(this.getMaximumValue());
        }
        
        public Partial withMinimumValue() {
            return this.setCopy(this.getMinimumValue());
        }
    }
}
