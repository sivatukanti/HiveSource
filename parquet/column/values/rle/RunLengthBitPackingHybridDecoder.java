// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.rle;

import java.io.DataInputStream;
import java.io.InputStream;
import parquet.bytes.BytesUtils;
import java.io.IOException;
import parquet.io.ParquetDecodingException;
import parquet.column.values.bitpacking.Packer;
import parquet.Preconditions;
import java.io.ByteArrayInputStream;
import parquet.column.values.bitpacking.BytePacker;
import parquet.Log;

public class RunLengthBitPackingHybridDecoder
{
    private static final Log LOG;
    private final int bitWidth;
    private final BytePacker packer;
    private final ByteArrayInputStream in;
    private MODE mode;
    private int currentCount;
    private int currentValue;
    private int[] currentBuffer;
    
    public RunLengthBitPackingHybridDecoder(final int bitWidth, final ByteArrayInputStream in) {
        if (Log.DEBUG) {
            RunLengthBitPackingHybridDecoder.LOG.debug("decoding bitWidth " + bitWidth);
        }
        Preconditions.checkArgument(bitWidth >= 0 && bitWidth <= 32, "bitWidth must be >= 0 and <= 32");
        this.bitWidth = bitWidth;
        this.packer = Packer.LITTLE_ENDIAN.newBytePacker(bitWidth);
        this.in = in;
    }
    
    public int readInt() throws IOException {
        if (this.currentCount == 0) {
            this.readNext();
        }
        --this.currentCount;
        int result = 0;
        switch (this.mode) {
            case RLE: {
                result = this.currentValue;
                break;
            }
            case PACKED: {
                result = this.currentBuffer[this.currentBuffer.length - 1 - this.currentCount];
                break;
            }
            default: {
                throw new ParquetDecodingException("not a valid mode " + this.mode);
            }
        }
        return result;
    }
    
    private void readNext() throws IOException {
        Preconditions.checkArgument(this.in.available() > 0, "Reading past RLE/BitPacking stream.");
        final int header = BytesUtils.readUnsignedVarInt(this.in);
        this.mode = (((header & 0x1) == 0x0) ? MODE.RLE : MODE.PACKED);
        switch (this.mode) {
            case RLE: {
                this.currentCount = header >>> 1;
                if (Log.DEBUG) {
                    RunLengthBitPackingHybridDecoder.LOG.debug("reading " + this.currentCount + " values RLE");
                }
                this.currentValue = BytesUtils.readIntLittleEndianPaddedOnBitWidth(this.in, this.bitWidth);
                break;
            }
            case PACKED: {
                final int numGroups = header >>> 1;
                this.currentCount = numGroups * 8;
                if (Log.DEBUG) {
                    RunLengthBitPackingHybridDecoder.LOG.debug("reading " + this.currentCount + " values BIT PACKED");
                }
                this.currentBuffer = new int[this.currentCount];
                final byte[] bytes = new byte[numGroups * this.bitWidth];
                int bytesToRead = (int)Math.ceil(this.currentCount * this.bitWidth / 8.0);
                bytesToRead = Math.min(bytesToRead, this.in.available());
                new DataInputStream(this.in).readFully(bytes, 0, bytesToRead);
                for (int valueIndex = 0, byteIndex = 0; valueIndex < this.currentCount; valueIndex += 8, byteIndex += this.bitWidth) {
                    this.packer.unpack8Values(bytes, byteIndex, this.currentBuffer, valueIndex);
                }
                break;
            }
            default: {
                throw new ParquetDecodingException("not a valid mode " + this.mode);
            }
        }
    }
    
    static {
        LOG = Log.getLog(RunLengthBitPackingHybridDecoder.class);
    }
    
    private enum MODE
    {
        RLE, 
        PACKED;
    }
}
