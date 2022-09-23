// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.page;

import parquet.column.ColumnDescriptor;

public interface PageReadStore
{
    PageReader getPageReader(final ColumnDescriptor p0);
    
    long getRowCount();
}
