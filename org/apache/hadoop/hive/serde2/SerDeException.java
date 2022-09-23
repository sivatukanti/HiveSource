// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

public class SerDeException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public SerDeException() {
    }
    
    public SerDeException(final String message) {
        super(message);
    }
    
    public SerDeException(final Throwable cause) {
        super(cause);
    }
    
    public SerDeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
