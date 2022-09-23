// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io;

import parquet.ParquetRuntimeException;

public class CompilationException extends ParquetRuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public CompilationException() {
    }
    
    public CompilationException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public CompilationException(final String message) {
        super(message);
    }
    
    public CompilationException(final Throwable cause) {
        super(cause);
    }
}
