// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

import java.io.IOException;
import java.io.InputStream;

class FourBitPackingReader extends BitPacking.BitPackingReader
{
    private final InputStream in;
    private int buffer;
    private int count;
    
    public FourBitPackingReader(final InputStream in) {
        this.buffer = 0;
        this.count = 0;
        this.in = in;
    }
    
    @Override
    public int read() throws IOException {
        if (this.count == 0) {
            this.buffer = this.in.read();
            this.count = 2;
        }
        final int result = this.buffer >> (this.count - 1) * 4 & 0xF;
        --this.count;
        return result;
    }
}
