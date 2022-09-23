// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.specific;

import org.apache.avro.generic.GenericData;
import org.apache.avro.Schema;
import java.lang.reflect.Constructor;
import org.apache.avro.data.ErrorBuilder;
import org.apache.avro.data.RecordBuilderBase;

public abstract class SpecificErrorBuilderBase<T extends SpecificExceptionBase> extends RecordBuilderBase<T> implements ErrorBuilder<T>
{
    private Constructor<T> errorConstructor;
    private Object value;
    private boolean hasValue;
    private Throwable cause;
    private boolean hasCause;
    
    protected SpecificErrorBuilderBase(final Schema schema) {
        super(schema, SpecificData.get());
    }
    
    protected SpecificErrorBuilderBase(final SpecificErrorBuilderBase<T> other) {
        super(other, SpecificData.get());
        this.errorConstructor = other.errorConstructor;
        this.value = other.value;
        this.hasValue = other.hasValue;
        this.cause = other.cause;
        this.hasCause = other.hasCause;
    }
    
    protected SpecificErrorBuilderBase(final T other) {
        super(other.getSchema(), SpecificData.get());
        final Object otherValue = other.getValue();
        if (otherValue != null) {
            this.setValue(otherValue);
        }
        final Throwable otherCause = other.getCause();
        if (otherCause != null) {
            this.setCause(otherCause);
        }
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public SpecificErrorBuilderBase<T> setValue(final Object value) {
        this.value = value;
        this.hasValue = true;
        return this;
    }
    
    @Override
    public boolean hasValue() {
        return this.hasValue;
    }
    
    @Override
    public SpecificErrorBuilderBase<T> clearValue() {
        this.value = null;
        this.hasValue = false;
        return this;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
    
    @Override
    public SpecificErrorBuilderBase<T> setCause(final Throwable cause) {
        this.cause = cause;
        this.hasCause = true;
        return this;
    }
    
    @Override
    public boolean hasCause() {
        return this.hasCause;
    }
    
    @Override
    public SpecificErrorBuilderBase<T> clearCause() {
        this.cause = null;
        this.hasCause = false;
        return this;
    }
}
