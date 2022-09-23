// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.avro;

public class AvroObjectInspectorException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public AvroObjectInspectorException() {
    }
    
    public AvroObjectInspectorException(final String message) {
        super(message);
    }
    
    public AvroObjectInspectorException(final Throwable cause) {
        super(cause);
    }
    
    public AvroObjectInspectorException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
