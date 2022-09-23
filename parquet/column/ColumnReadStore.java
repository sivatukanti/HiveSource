// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column;

public interface ColumnReadStore
{
    ColumnReader getColumnReader(final ColumnDescriptor p0);
}
