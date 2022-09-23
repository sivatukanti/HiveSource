// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.avro;

import org.apache.hadoop.hive.serde2.SerDeException;

public class AvroSerdeException extends SerDeException
{
    public AvroSerdeException() {
    }
    
    public AvroSerdeException(final String message) {
        super(message);
    }
    
    public AvroSerdeException(final Throwable cause) {
        super(cause);
    }
    
    public AvroSerdeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
