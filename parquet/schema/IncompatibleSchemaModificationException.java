// 
// Decompiled by Procyon v0.5.36
// 

package parquet.schema;

import parquet.ParquetRuntimeException;

public class IncompatibleSchemaModificationException extends ParquetRuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public IncompatibleSchemaModificationException() {
    }
    
    public IncompatibleSchemaModificationException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public IncompatibleSchemaModificationException(final String message) {
        super(message);
    }
    
    public IncompatibleSchemaModificationException(final Throwable cause) {
        super(cause);
    }
}
