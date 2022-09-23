// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.data;

public interface ErrorBuilder<T> extends RecordBuilder<T>
{
    Object getValue();
    
    ErrorBuilder<T> setValue(final Object p0);
    
    boolean hasValue();
    
    ErrorBuilder<T> clearValue();
    
    Throwable getCause();
    
    ErrorBuilder<T> setCause(final Throwable p0);
    
    boolean hasCause();
    
    ErrorBuilder<T> clearCause();
}
