// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import parquet.ParquetRuntimeException;

public class BadConfigurationException extends ParquetRuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public BadConfigurationException() {
    }
    
    public BadConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public BadConfigurationException(final String message) {
        super(message);
    }
    
    public BadConfigurationException(final Throwable cause) {
        super(cause);
    }
}
