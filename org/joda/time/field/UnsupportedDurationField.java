// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.field;

import org.joda.time.DurationFieldType;
import java.util.HashMap;
import java.io.Serializable;
import org.joda.time.DurationField;

public final class UnsupportedDurationField extends DurationField implements Serializable
{
    private static final long serialVersionUID = -6390301302770925357L;
    private static HashMap<DurationFieldType, UnsupportedDurationField> cCache;
    private final DurationFieldType iType;
    
    public static synchronized UnsupportedDurationField getInstance(final DurationFieldType durationFieldType) {
        UnsupportedDurationField value;
        if (UnsupportedDurationField.cCache == null) {
            UnsupportedDurationField.cCache = new HashMap<DurationFieldType, UnsupportedDurationField>(7);
            value = null;
        }
        else {
            value = UnsupportedDurationField.cCache.get(durationFieldType);
        }
        if (value == null) {
            value = new UnsupportedDurationField(durationFieldType);
            UnsupportedDurationField.cCache.put(durationFieldType, value);
        }
        return value;
    }
    
    private UnsupportedDurationField(final DurationFieldType iType) {
        this.iType = iType;
    }
    
    @Override
    public final DurationFieldType getType() {
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
    public boolean isPrecise() {
        return true;
    }
    
    @Override
    public int getValue(final long n) {
        throw this.unsupported();
    }
    
    @Override
    public long getValueAsLong(final long n) {
        throw this.unsupported();
    }
    
    @Override
    public int getValue(final long n, final long n2) {
        throw this.unsupported();
    }
    
    @Override
    public long getValueAsLong(final long n, final long n2) {
        throw this.unsupported();
    }
    
    @Override
    public long getMillis(final int n) {
        throw this.unsupported();
    }
    
    @Override
    public long getMillis(final long n) {
        throw this.unsupported();
    }
    
    @Override
    public long getMillis(final int n, final long n2) {
        throw this.unsupported();
    }
    
    @Override
    public long getMillis(final long n, final long n2) {
        throw this.unsupported();
    }
    
    @Override
    public long add(final long n, final int n2) {
        throw this.unsupported();
    }
    
    @Override
    public long add(final long n, final long n2) {
        throw this.unsupported();
    }
    
    @Override
    public int getDifference(final long n, final long n2) {
        throw this.unsupported();
    }
    
    @Override
    public long getDifferenceAsLong(final long n, final long n2) {
        throw this.unsupported();
    }
    
    @Override
    public long getUnitMillis() {
        return 0L;
    }
    
    public int compareTo(final DurationField durationField) {
        return 0;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UnsupportedDurationField)) {
            return false;
        }
        final UnsupportedDurationField unsupportedDurationField = (UnsupportedDurationField)o;
        if (unsupportedDurationField.getName() == null) {
            return this.getName() == null;
        }
        return unsupportedDurationField.getName().equals(this.getName());
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    @Override
    public String toString() {
        return "UnsupportedDurationField[" + this.getName() + ']';
    }
    
    private Object readResolve() {
        return getInstance(this.iType);
    }
    
    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException(this.iType + " field is unsupported");
    }
}
