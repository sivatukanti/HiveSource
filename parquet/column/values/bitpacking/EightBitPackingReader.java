// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

import java.io.IOException;
import java.io.InputStream;

class EightBitPackingReader extends BitPacking.BitPackingReader
{
    private final InputStream in;
    
    public EightBitPackingReader(final InputStream in) {
        this.in = in;
    }
    
    @Override
    public int read() throws IOException {
        return this.in.read();
    }
}
