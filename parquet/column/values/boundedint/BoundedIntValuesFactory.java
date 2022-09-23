// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.boundedint;

import parquet.column.values.ValuesWriter;
import parquet.column.values.ValuesReader;

public abstract class BoundedIntValuesFactory
{
    public static ValuesReader getBoundedReader(final int bound) {
        return (bound == 0) ? new ZeroIntegerValuesReader() : new BoundedIntValuesReader(bound);
    }
    
    public static ValuesWriter getBoundedWriter(final int bound, final int initialCapacity, final int pageSize) {
        return (bound == 0) ? new DevNullValuesWriter() : new BoundedIntValuesWriter(bound, initialCapacity, pageSize);
    }
}
