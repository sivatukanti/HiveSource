// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

import java.io.IOException;
import java.util.Arrays;
import parquet.bytes.BytesUtils;
import parquet.Log;
import parquet.column.values.ValuesReader;

public class ByteBitPackingValuesReader extends ValuesReader
{
    private static final int VALUES_AT_A_TIME = 8;
    private static final Log LOG;
    private final int bitWidth;
    private final BytePacker packer;
    private final int[] decoded;
    private int decodedPosition;
    private byte[] encoded;
    private int encodedPos;
    private int nextOffset;
    
    public ByteBitPackingValuesReader(final int bound, final Packer packer) {
        this.decoded = new int[8];
        this.decodedPosition = 7;
        this.bitWidth = BytesUtils.getWidthFromMaxInt(bound);
        this.packer = packer.newBytePacker(this.bitWidth);
    }
    
    @Override
    public int readInteger() {
        ++this.decodedPosition;
        if (this.decodedPosition == this.decoded.length) {
            if (this.encodedPos + this.bitWidth > this.encoded.length) {
                this.packer.unpack8Values(Arrays.copyOfRange(this.encoded, this.encodedPos, this.encodedPos + this.bitWidth), 0, this.decoded, 0);
            }
            else {
                this.packer.unpack8Values(this.encoded, this.encodedPos, this.decoded, 0);
            }
            this.encodedPos += this.bitWidth;
            this.decodedPosition = 0;
        }
        return this.decoded[this.decodedPosition];
    }
    
    @Override
    public void initFromPage(final int valueCount, final byte[] page, final int offset) throws IOException {
        final int effectiveBitLength = valueCount * this.bitWidth;
        final int length = BytesUtils.paddedByteCountFromBits(effectiveBitLength);
        if (Log.DEBUG) {
            ByteBitPackingValuesReader.LOG.debug("reading " + length + " bytes for " + valueCount + " values of size " + this.bitWidth + " bits.");
        }
        this.encoded = page;
        this.encodedPos = offset;
        this.decodedPosition = 7;
        this.nextOffset = offset + length;
    }
    
    @Override
    public int getNextOffset() {
        return this.nextOffset;
    }
    
    @Override
    public void skip() {
        this.readInteger();
    }
    
    static {
        LOG = Log.getLog(ByteBitPackingValuesReader.class);
    }
}
