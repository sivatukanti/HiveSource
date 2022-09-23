// 
// Decompiled by Procyon v0.5.36
// 

package parquet;

public abstract class ParquetRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public ParquetRuntimeException() {
    }
    
    public ParquetRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ParquetRuntimeException(final String message) {
        super(message);
    }
    
    public ParquetRuntimeException(final Throwable cause) {
        super(cause);
    }
}
