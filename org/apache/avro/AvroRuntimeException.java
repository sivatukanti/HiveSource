// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro;

public class AvroRuntimeException extends RuntimeException
{
    public AvroRuntimeException(final Throwable cause) {
        super(cause);
    }
    
    public AvroRuntimeException(final String message) {
        super(message);
    }
    
    public AvroRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
