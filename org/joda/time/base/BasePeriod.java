// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.base;

import org.joda.time.DurationFieldType;
import org.joda.time.Duration;
import org.joda.time.convert.PeriodConverter;
import org.joda.time.MutablePeriod;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.convert.ConverterManager;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.field.FieldUtils;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadablePartial;
import org.joda.time.ReadableInstant;
import org.joda.time.DateTimeUtils;
import org.joda.time.Chronology;
import org.joda.time.PeriodType;
import java.io.Serializable;
import org.joda.time.ReadablePeriod;

public abstract class BasePeriod extends AbstractPeriod implements ReadablePeriod, Serializable
{
    private static final long serialVersionUID = -2110953284060001145L;
    private static final ReadablePeriod DUMMY_PERIOD;
    private final PeriodType iType;
    private final int[] iValues;
    
    protected BasePeriod(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, PeriodType checkPeriodType) {
        checkPeriodType = this.checkPeriodType(checkPeriodType);
        this.iType = checkPeriodType;
        this.iValues = this.setPeriodInternal(n, n2, n3, n4, n5, n6, n7, n8);
    }
    
    protected BasePeriod(final long n, final long n2, PeriodType checkPeriodType, Chronology chronology) {
        checkPeriodType = this.checkPeriodType(checkPeriodType);
        chronology = DateTimeUtils.getChronology(chronology);
        this.iType = checkPeriodType;
        this.iValues = chronology.get(this, n, n2);
    }
    
    protected BasePeriod(final ReadableInstant readableInstant, final ReadableInstant readableInstant2, PeriodType checkPeriodType) {
        checkPeriodType = this.checkPeriodType(checkPeriodType);
        if (readableInstant == null && readableInstant2 == null) {
            this.iType = checkPeriodType;
            this.iValues = new int[this.size()];
        }
        else {
            final long instantMillis = DateTimeUtils.getInstantMillis(readableInstant);
            final long instantMillis2 = DateTimeUtils.getInstantMillis(readableInstant2);
            final Chronology intervalChronology = DateTimeUtils.getIntervalChronology(readableInstant, readableInstant2);
            this.iType = checkPeriodType;
            this.iValues = intervalChronology.get(this, instantMillis, instantMillis2);
        }
    }
    
    protected BasePeriod(final ReadablePartial readablePartial, final ReadablePartial readablePartial2, PeriodType checkPeriodType) {
        if (readablePartial == null || readablePartial2 == null) {
            throw new IllegalArgumentException("ReadablePartial objects must not be null");
        }
        if (readablePartial instanceof BaseLocal && readablePartial2 instanceof BaseLocal && readablePartial.getClass() == readablePartial2.getClass()) {
            checkPeriodType = this.checkPeriodType(checkPeriodType);
            final long localMillis = ((BaseLocal)readablePartial).getLocalMillis();
            final long localMillis2 = ((BaseLocal)readablePartial2).getLocalMillis();
            final Chronology chronology = DateTimeUtils.getChronology(readablePartial.getChronology());
            this.iType = checkPeriodType;
            this.iValues = chronology.get(this, localMillis, localMillis2);
        }
        else {
            if (readablePartial.size() != readablePartial2.size()) {
                throw new IllegalArgumentException("ReadablePartial objects must have the same set of fields");
            }
            for (int i = 0; i < readablePartial.size(); ++i) {
                if (readablePartial.getFieldType(i) != readablePartial2.getFieldType(i)) {
                    throw new IllegalArgumentException("ReadablePartial objects must have the same set of fields");
                }
            }
            if (!DateTimeUtils.isContiguous(readablePartial)) {
                throw new IllegalArgumentException("ReadablePartial objects must be contiguous");
            }
            this.iType = this.checkPeriodType(checkPeriodType);
            final Chronology withUTC = DateTimeUtils.getChronology(readablePartial.getChronology()).withUTC();
            this.iValues = withUTC.get(this, withUTC.set(readablePartial, 0L), withUTC.set(readablePartial2, 0L));
        }
    }
    
    protected BasePeriod(final ReadableInstant readableInstant, final ReadableDuration readableDuration, PeriodType checkPeriodType) {
        checkPeriodType = this.checkPeriodType(checkPeriodType);
        final long instantMillis = DateTimeUtils.getInstantMillis(readableInstant);
        final long safeAdd = FieldUtils.safeAdd(instantMillis, DateTimeUtils.getDurationMillis(readableDuration));
        final Chronology instantChronology = DateTimeUtils.getInstantChronology(readableInstant);
        this.iType = checkPeriodType;
        this.iValues = instantChronology.get(this, instantMillis, safeAdd);
    }
    
    protected BasePeriod(final ReadableDuration readableDuration, final ReadableInstant readableInstant, PeriodType checkPeriodType) {
        checkPeriodType = this.checkPeriodType(checkPeriodType);
        final long durationMillis = DateTimeUtils.getDurationMillis(readableDuration);
        final long instantMillis = DateTimeUtils.getInstantMillis(readableInstant);
        final long safeSubtract = FieldUtils.safeSubtract(instantMillis, durationMillis);
        final Chronology instantChronology = DateTimeUtils.getInstantChronology(readableInstant);
        this.iType = checkPeriodType;
        this.iValues = instantChronology.get(this, safeSubtract, instantMillis);
    }
    
    protected BasePeriod(final long n) {
        this.iType = PeriodType.standard();
        System.arraycopy(ISOChronology.getInstanceUTC().get(BasePeriod.DUMMY_PERIOD, n), 0, this.iValues = new int[8], 4, 4);
    }
    
    protected BasePeriod(final long n, PeriodType checkPeriodType, Chronology chronology) {
        checkPeriodType = this.checkPeriodType(checkPeriodType);
        chronology = DateTimeUtils.getChronology(chronology);
        this.iType = checkPeriodType;
        this.iValues = chronology.get(this, n);
    }
    
    protected BasePeriod(final Object o, PeriodType checkPeriodType, Chronology chronology) {
        final PeriodConverter periodConverter = ConverterManager.getInstance().getPeriodConverter(o);
        checkPeriodType = ((checkPeriodType == null) ? periodConverter.getPeriodType(o) : checkPeriodType);
        checkPeriodType = this.checkPeriodType(checkPeriodType);
        this.iType = checkPeriodType;
        if (this instanceof ReadWritablePeriod) {
            this.iValues = new int[this.size()];
            chronology = DateTimeUtils.getChronology(chronology);
            periodConverter.setInto((ReadWritablePeriod)this, o, chronology);
        }
        else {
            this.iValues = new MutablePeriod(o, checkPeriodType, chronology).getValues();
        }
    }
    
    protected BasePeriod(final int[] iValues, final PeriodType iType) {
        this.iType = iType;
        this.iValues = iValues;
    }
    
    protected PeriodType checkPeriodType(final PeriodType periodType) {
        return DateTimeUtils.getPeriodType(periodType);
    }
    
    public PeriodType getPeriodType() {
        return this.iType;
    }
    
    public int getValue(final int n) {
        return this.iValues[n];
    }
    
    public Duration toDurationFrom(final ReadableInstant readableInstant) {
        final long instantMillis = DateTimeUtils.getInstantMillis(readableInstant);
        return new Duration(instantMillis, DateTimeUtils.getInstantChronology(readableInstant).add(this, instantMillis, 1));
    }
    
    public Duration toDurationTo(final ReadableInstant readableInstant) {
        final long instantMillis = DateTimeUtils.getInstantMillis(readableInstant);
        return new Duration(DateTimeUtils.getInstantChronology(readableInstant).add(this, instantMillis, -1), instantMillis);
    }
    
    private void checkAndUpdate(final DurationFieldType durationFieldType, final int[] array, final int n) {
        final int index = this.indexOf(durationFieldType);
        if (index == -1) {
            if (n != 0) {
                throw new IllegalArgumentException("Period does not support field '" + durationFieldType.getName() + "'");
            }
        }
        else {
            array[index] = n;
        }
    }
    
    protected void setPeriod(final ReadablePeriod periodInternal) {
        if (periodInternal == null) {
            this.setValues(new int[this.size()]);
        }
        else {
            this.setPeriodInternal(periodInternal);
        }
    }
    
    private void setPeriodInternal(final ReadablePeriod readablePeriod) {
        final int[] values = new int[this.size()];
        for (int i = 0; i < readablePeriod.size(); ++i) {
            this.checkAndUpdate(readablePeriod.getFieldType(i), values, readablePeriod.getValue(i));
        }
        this.setValues(values);
    }
    
    protected void setPeriod(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        this.setValues(this.setPeriodInternal(n, n2, n3, n4, n5, n6, n7, n8));
    }
    
    private int[] setPeriodInternal(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        final int[] array = new int[this.size()];
        this.checkAndUpdate(DurationFieldType.years(), array, n);
        this.checkAndUpdate(DurationFieldType.months(), array, n2);
        this.checkAndUpdate(DurationFieldType.weeks(), array, n3);
        this.checkAndUpdate(DurationFieldType.days(), array, n4);
        this.checkAndUpdate(DurationFieldType.hours(), array, n5);
        this.checkAndUpdate(DurationFieldType.minutes(), array, n6);
        this.checkAndUpdate(DurationFieldType.seconds(), array, n7);
        this.checkAndUpdate(DurationFieldType.millis(), array, n8);
        return array;
    }
    
    protected void setField(final DurationFieldType durationFieldType, final int n) {
        this.setFieldInto(this.iValues, durationFieldType, n);
    }
    
    protected void setFieldInto(final int[] array, final DurationFieldType obj, final int n) {
        final int index = this.indexOf(obj);
        if (index == -1) {
            if (n != 0 || obj == null) {
                throw new IllegalArgumentException("Period does not support field '" + obj + "'");
            }
        }
        else {
            array[index] = n;
        }
    }
    
    protected void addField(final DurationFieldType durationFieldType, final int n) {
        this.addFieldInto(this.iValues, durationFieldType, n);
    }
    
    protected void addFieldInto(final int[] array, final DurationFieldType obj, final int n) {
        final int index = this.indexOf(obj);
        if (index == -1) {
            if (n != 0 || obj == null) {
                throw new IllegalArgumentException("Period does not support field '" + obj + "'");
            }
        }
        else {
            array[index] = FieldUtils.safeAdd(array[index], n);
        }
    }
    
    protected void mergePeriod(final ReadablePeriod readablePeriod) {
        if (readablePeriod != null) {
            this.setValues(this.mergePeriodInto(this.getValues(), readablePeriod));
        }
    }
    
    protected int[] mergePeriodInto(final int[] array, final ReadablePeriod readablePeriod) {
        for (int i = 0; i < readablePeriod.size(); ++i) {
            this.checkAndUpdate(readablePeriod.getFieldType(i), array, readablePeriod.getValue(i));
        }
        return array;
    }
    
    protected void addPeriod(final ReadablePeriod readablePeriod) {
        if (readablePeriod != null) {
            this.setValues(this.addPeriodInto(this.getValues(), readablePeriod));
        }
    }
    
    protected int[] addPeriodInto(final int[] array, final ReadablePeriod readablePeriod) {
        for (int i = 0; i < readablePeriod.size(); ++i) {
            final DurationFieldType fieldType = readablePeriod.getFieldType(i);
            final int value = readablePeriod.getValue(i);
            if (value != 0) {
                final int index = this.indexOf(fieldType);
                if (index == -1) {
                    throw new IllegalArgumentException("Period does not support field '" + fieldType.getName() + "'");
                }
                array[index] = FieldUtils.safeAdd(this.getValue(index), value);
            }
        }
        return array;
    }
    
    protected void setValue(final int n, final int n2) {
        this.iValues[n] = n2;
    }
    
    protected void setValues(final int[] array) {
        System.arraycopy(array, 0, this.iValues, 0, this.iValues.length);
    }
    
    static {
        DUMMY_PERIOD = new AbstractPeriod() {
            public int getValue(final int n) {
                return 0;
            }
            
            public PeriodType getPeriodType() {
                return PeriodType.time();
            }
        };
    }
}
