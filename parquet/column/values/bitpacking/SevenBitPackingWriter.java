// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

import java.io.IOException;
import java.io.OutputStream;

class SevenBitPackingWriter extends BaseBitPackingWriter
{
    private OutputStream out;
    private long buffer;
    private int count;
    
    public SevenBitPackingWriter(final OutputStream out) {
        this.buffer = 0L;
        this.count = 0;
        this.out = out;
    }
    
    @Override
    public void write(final int val) throws IOException {
        this.buffer <<= 7;
        this.buffer |= val;
        ++this.count;
        if (this.count == 8) {
            this.out.write((int)(this.buffer >>> 48) & 0xFF);
            this.out.write((int)(this.buffer >>> 40) & 0xFF);
            this.out.write((int)(this.buffer >>> 32) & 0xFF);
            this.out.write((int)(this.buffer >>> 24) & 0xFF);
            this.out.write((int)(this.buffer >>> 16) & 0xFF);
            this.out.write((int)(this.buffer >>> 8) & 0xFF);
            this.out.write((int)(this.buffer >>> 0) & 0xFF);
            this.buffer = 0L;
            this.count = 0;
        }
    }
    
    @Override
    public void finish() throws IOException {
        if (this.count != 0) {
            final int numberOfBits = this.count * 7;
            this.finish(numberOfBits, this.buffer, this.out);
            this.buffer = 0L;
            this.count = 0;
        }
        this.out = null;
    }
}
