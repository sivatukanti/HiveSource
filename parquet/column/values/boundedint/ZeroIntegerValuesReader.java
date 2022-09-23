// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.boundedint;

import java.io.IOException;
import parquet.column.values.ValuesReader;

public class ZeroIntegerValuesReader extends ValuesReader
{
    private int nextOffset;
    
    @Override
    public int readInteger() {
        return 0;
    }
    
    @Override
    public void initFromPage(final int valueCount, final byte[] in, final int offset) throws IOException {
        this.nextOffset = offset;
    }
    
    @Override
    public int getNextOffset() {
        return this.nextOffset;
    }
    
    @Override
    public void skip() {
    }
}
