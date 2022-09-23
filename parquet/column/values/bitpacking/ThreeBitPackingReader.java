// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

import java.io.IOException;
import java.io.InputStream;

class ThreeBitPackingReader extends BaseBitPackingReader
{
    private final InputStream in;
    private final long valueCount;
    private int buffer;
    private int count;
    private long totalRead;
    
    public ThreeBitPackingReader(final InputStream in, final long valueCount) {
        this.buffer = 0;
        this.count = 0;
        this.totalRead = 0L;
        this.in = in;
        this.valueCount = valueCount;
    }
    
    @Override
    public int read() throws IOException {
        if (this.count == 0) {
            if (this.valueCount - this.totalRead < 8L) {
                this.buffer = 0;
                final int bitsToRead = 3 * (int)(this.valueCount - this.totalRead);
                for (int bytesToRead = this.alignToBytes(bitsToRead), i = 2; i >= 3 - bytesToRead; --i) {
                    this.buffer |= this.in.read() << i * 8;
                }
                this.count = 8;
                this.totalRead = this.valueCount;
            }
            else {
                this.buffer = (this.in.read() << 16) + (this.in.read() << 8) + this.in.read();
                this.count = 8;
                this.totalRead += 8L;
            }
        }
        final int result = this.buffer >> (this.count - 1) * 3 & 0x7;
        --this.count;
        return result;
    }
}
