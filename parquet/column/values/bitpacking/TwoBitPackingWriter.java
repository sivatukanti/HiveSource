// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

import java.io.IOException;
import java.io.OutputStream;

class TwoBitPackingWriter extends BitPacking.BitPackingWriter
{
    private OutputStream out;
    private int buffer;
    private int count;
    
    public TwoBitPackingWriter(final OutputStream out) {
        this.buffer = 0;
        this.count = 0;
        this.out = out;
    }
    
    @Override
    public void write(final int val) throws IOException {
        this.buffer <<= 2;
        this.buffer |= val;
        ++this.count;
        if (this.count == 4) {
            this.out.write(this.buffer);
            this.buffer = 0;
            this.count = 0;
        }
    }
    
    @Override
    public void finish() throws IOException {
        while (this.count != 0) {
            this.write(0);
        }
        this.out = null;
    }
}
