// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.page;

import java.io.IOException;
import parquet.column.Encoding;
import parquet.column.statistics.Statistics;
import parquet.bytes.BytesInput;

public interface PageWriter
{
    void writePage(final BytesInput p0, final int p1, final Statistics<?> p2, final Encoding p3, final Encoding p4, final Encoding p5) throws IOException;
    
    void writePageV2(final int p0, final int p1, final int p2, final BytesInput p3, final BytesInput p4, final Encoding p5, final BytesInput p6, final Statistics<?> p7) throws IOException;
    
    long getMemSize();
    
    long allocatedSize();
    
    void writeDictionaryPage(final DictionaryPage p0) throws IOException;
    
    String memUsageString(final String p0);
}
