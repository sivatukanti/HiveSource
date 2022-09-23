// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

import java.io.IOException;
import java.io.InputStream;

class SevenBitPackingReader extends BaseBitPackingReader
{
    private final InputStream in;
    private final long valueCount;
    private long buffer;
    private int count;
    private long totalRead;
    
    public SevenBitPackingReader(final InputStream in, final long valueCount) {
        this.buffer = 0L;
        this.count = 0;
        this.totalRead = 0L;
        this.in = in;
        this.valueCount = valueCount;
    }
    
    @Override
    public int read() throws IOException {
        if (this.count == 0) {
            if (this.valueCount - this.totalRead < 8L) {
                this.buffer = 0L;
                final int bitsToRead = 7 * (int)(this.valueCount - this.totalRead);
                for (int bytesToRead = this.alignToBytes(bitsToRead), i = 6; i >= 7 - bytesToRead; --i) {
                    this.buffer |= ((long)this.in.read() & 0xFFL) << i * 8;
                }
                this.count = 8;
                this.totalRead = this.valueCount;
            }
            else {
                this.buffer = (((long)this.in.read() & 0xFFL) << 48) + (((long)this.in.read() & 0xFFL) << 40) + (((long)this.in.read() & 0xFFL) << 32) + (((long)this.in.read() & 0xFFL) << 24) + (this.in.read() << 16) + (this.in.read() << 8) + this.in.read();
                this.count = 8;
                this.totalRead += 8L;
            }
        }
        final int result = (int)(this.buffer >> (this.count - 1) * 7) & 0x7F;
        --this.count;
        return result;
    }
}
