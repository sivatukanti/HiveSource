// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.page;

import parquet.column.ColumnDescriptor;

public interface PageWriteStore
{
    PageWriter getPageWriter(final ColumnDescriptor p0);
}
