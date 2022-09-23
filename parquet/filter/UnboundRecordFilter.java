// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter;

import parquet.column.ColumnReader;

public interface UnboundRecordFilter
{
    RecordFilter bind(final Iterable<ColumnReader> p0);
}
