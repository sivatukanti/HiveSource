// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column;

public interface ColumnWriteStore
{
    ColumnWriter getColumnWriter(final ColumnDescriptor p0);
    
    void flush();
    
    void endRecord();
    
    long getAllocatedSize();
    
    long getBufferedSize();
    
    String memUsageString();
}
