// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io;

import parquet.ParquetRuntimeException;

public class ParquetEncodingException extends ParquetRuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public ParquetEncodingException() {
    }
    
    public ParquetEncodingException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ParquetEncodingException(final String message) {
        super(message);
    }
    
    public ParquetEncodingException(final Throwable cause) {
        super(cause);
    }
}
