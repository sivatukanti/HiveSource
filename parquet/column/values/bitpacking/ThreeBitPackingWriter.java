// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

import java.io.IOException;
import java.io.OutputStream;

class ThreeBitPackingWriter extends BaseBitPackingWriter
{
    private OutputStream out;
    private int buffer;
    private int count;
    
    public ThreeBitPackingWriter(final OutputStream out) {
        this.buffer = 0;
        this.count = 0;
        this.out = out;
    }
    
    @Override
    public void write(final int val) throws IOException {
        this.buffer <<= 3;
        this.buffer |= val;
        ++this.count;
        if (this.count == 8) {
            this.out.write(this.buffer >>> 16 & 0xFF);
            this.out.write(this.buffer >>> 8 & 0xFF);
            this.out.write(this.buffer >>> 0 & 0xFF);
            this.buffer = 0;
            this.count = 0;
        }
    }
    
    @Override
    public void finish() throws IOException {
        if (this.count != 0) {
            final int numberOfBits = this.count * 3;
            this.finish(numberOfBits, this.buffer, this.out);
            this.buffer = 0;
            this.count = 0;
        }
        this.out = null;
    }
}
