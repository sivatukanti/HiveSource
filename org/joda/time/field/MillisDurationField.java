// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.DurationFieldType;
import java.io.Serializable;
import org.joda.time.DurationField;

public final class MillisDurationField extends DurationField implements Serializable
{
    private static final long serialVersionUID = 2656707858124633367L;
    public static final DurationField INSTANCE;
    
    private MillisDurationField() {
    }
    
    @Override
    public DurationFieldType getType() {
        return DurationFieldType.millis();
    }
    
    @Override
    public String getName() {
        return "millis";
    }
    
    @Override
    public boolean isSupported() {
        return true;
    }
    
    @Override
    public final boolean isPrecise() {
        return true;
    }
    
    @Override
    public final long getUnitMillis() {
        return 1L;
    }
    
    @Override
    public int getValue(final long n) {
        return FieldUtils.safeToInt(n);
    }
    
    @Override
    public long getValueAsLong(final long n) {
        return n;
    }
    
    @Override
    public int getValue(final long n, final long n2) {
        return FieldUtils.safeToInt(n);
    }
    
    @Override
    public long getValueAsLong(final long n, final long n2) {
        return n;
    }
    
    @Override
    public long getMillis(final int n) {
        return n;
    }
    
    @Override
    public long getMillis(final long n) {
        return n;
    }
    
    @Override
    public long getMillis(final int n, final long n2) {
        return n;
    }
    
    @Override
    public long getMillis(final long n, final long n2) {
        return n;
    }
    
    @Override
    public long add(final long n, final int n2) {
        return FieldUtils.safeAdd(n, n2);
    }
    
    @Override
    public long add(final long n, final long n2) {
        return FieldUtils.safeAdd(n, n2);
    }
    
    @Override
    public int getDifference(final long n, final long n2) {
        return FieldUtils.safeToInt(FieldUtils.safeSubtract(n, n2));
    }
    
    @Override
    public long getDifferenceAsLong(final long n, final long n2) {
        return FieldUtils.safeSubtract(n, n2);
    }
    
    public int compareTo(final DurationField durationField) {
        final long unitMillis = durationField.getUnitMillis();
        final long unitMillis2 = this.getUnitMillis();
        if (unitMillis2 == unitMillis) {
            return 0;
        }
        if (unitMillis2 < unitMillis) {
            return -1;
        }
        return 1;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof MillisDurationField && this.getUnitMillis() == ((MillisDurationField)o).getUnitMillis();
    }
    
    @Override
    public int hashCode() {
        return (int)this.getUnitMillis();
    }
    
    @Override
    public String toString() {
        return "DurationField[millis]";
    }
    
    private Object readResolve() {
        return MillisDurationField.INSTANCE;
    }
    
    static {
        INSTANCE = new MillisDurationField();
    }
}
