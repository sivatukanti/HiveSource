// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro;

import java.io.IOException;

public class AvroRemoteException extends IOException
{
    private Object value;
    
    protected AvroRemoteException() {
    }
    
    public AvroRemoteException(final Throwable value) {
        this((Object)value.toString());
        this.initCause(value);
    }
    
    public AvroRemoteException(final Object value) {
        super((value != null) ? value.toString() : null);
        this.value = value;
    }
    
    public AvroRemoteException(final Object value, final Throwable cause) {
        super((value != null) ? value.toString() : null, cause);
        this.value = value;
    }
    
    public Object getValue() {
        return this.value;
    }
}
