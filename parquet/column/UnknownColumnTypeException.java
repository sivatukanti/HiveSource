// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column;

import parquet.schema.PrimitiveType;
import parquet.ParquetRuntimeException;

public class UnknownColumnTypeException extends ParquetRuntimeException
{
    private static final long serialVersionUID = 1L;
    private final PrimitiveType.PrimitiveTypeName type;
    
    public UnknownColumnTypeException(final PrimitiveType.PrimitiveTypeName type) {
        super("Column type not found: " + type.toString());
        this.type = type;
    }
    
    public PrimitiveType.PrimitiveTypeName getType() {
        return this.type;
    }
}
