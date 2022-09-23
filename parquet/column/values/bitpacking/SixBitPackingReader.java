// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

import java.io.IOException;
import java.io.InputStream;

class SixBitPackingReader extends BaseBitPackingReader
{
    private final InputStream in;
    private final long valueCount;
    private int buffer;
    private int count;
    private long totalRead;
    
    public SixBitPackingReader(final InputStream in, final long valueCount) {
        this.buffer = 0;
        this.count = 0;
        this.totalRead = 0L;
        this.in = in;
        this.valueCount = valueCount;
    }
    
    @Override
    public int read() throws IOException {
        if (this.count == 0) {
            if (this.valueCount - this.totalRead < 4L) {
                this.buffer = 0;
                final int bitsToRead = 6 * (int)(this.valueCount - this.totalRead);
                for (int bytesToRead = this.alignToBytes(bitsToRead), i = 2; i >= 3 - bytesToRead; --i) {
                    this.buffer |= this.in.read() << i * 8;
                }
                this.count = 4;
                this.totalRead = this.valueCount;
            }
            else {
                this.buffer = (this.in.read() << 16) + (this.in.read() << 8) + this.in.read();
                this.count = 4;
                this.totalRead += 4L;
            }
        }
        final int result = this.buffer >> (this.count - 1) * 6 & 0x3F;
        --this.count;
        return result;
    }
}
