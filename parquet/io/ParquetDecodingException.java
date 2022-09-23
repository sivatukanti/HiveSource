// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io;

import parquet.ParquetRuntimeException;

public class ParquetDecodingException extends ParquetRuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public ParquetDecodingException() {
    }
    
    public ParquetDecodingException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ParquetDecodingException(final String message) {
        super(message);
    }
    
    public ParquetDecodingException(final Throwable cause) {
        super(cause);
    }
}
