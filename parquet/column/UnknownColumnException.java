// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column;

import parquet.ParquetRuntimeException;

public class UnknownColumnException extends ParquetRuntimeException
{
    private static final long serialVersionUID = 1L;
    private final ColumnDescriptor descriptor;
    
    public UnknownColumnException(final ColumnDescriptor descriptor) {
        super("Column not found: " + descriptor.toString());
        this.descriptor = descriptor;
    }
    
    public ColumnDescriptor getDescriptor() {
        return this.descriptor;
    }
}
