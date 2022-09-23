// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column;

import parquet.io.api.Binary;

public interface ColumnWriter
{
    void write(final int p0, final int p1, final int p2);
    
    void write(final long p0, final int p1, final int p2);
    
    void write(final boolean p0, final int p1, final int p2);
    
    void write(final Binary p0, final int p1, final int p2);
    
    void write(final float p0, final int p1, final int p2);
    
    void write(final double p0, final int p1, final int p2);
    
    void writeNull(final int p0, final int p1);
}
