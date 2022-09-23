// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io;

import parquet.ParquetRuntimeException;

public class InvalidRecordException extends ParquetRuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public InvalidRecordException() {
    }
    
    public InvalidRecordException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public InvalidRecordException(final String message) {
        super(message);
    }
    
    public InvalidRecordException(final Throwable cause) {
        super(cause);
    }
}
