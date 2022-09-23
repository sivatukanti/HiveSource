// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

import parquet.bytes.BytesUtils;
import java.io.IOException;
import java.util.ArrayList;
import parquet.bytes.BytesInput;
import java.util.List;
import parquet.Log;

public class ByteBasedBitPackingEncoder
{
    private static final Log LOG;
    private static final int VALUES_WRITTEN_AT_A_TIME = 8;
    private final int bitWidth;
    private final BytePacker packer;
    private final int[] input;
    private final int slabSize;
    private int inputSize;
    private byte[] packed;
    private int packedPosition;
    private final List<BytesInput> slabs;
    private int totalValues;
    
    public ByteBasedBitPackingEncoder(final int bitWidth, final Packer packer) {
        this.input = new int[8];
        this.slabs = new ArrayList<BytesInput>();
        this.bitWidth = bitWidth;
        this.inputSize = 0;
        this.slabSize = bitWidth * 64 * 1024;
        this.initPackedSlab();
        this.packer = packer.newBytePacker(bitWidth);
    }
    
    public void writeInt(final int value) throws IOException {
        this.input[this.inputSize] = value;
        ++this.inputSize;
        if (this.inputSize == 8) {
            this.pack();
            if (this.packedPosition == this.slabSize) {
                this.slabs.add(BytesInput.from(this.packed));
                this.initPackedSlab();
            }
        }
    }
    
    private void pack() {
        this.packer.pack8Values(this.input, 0, this.packed, this.packedPosition);
        this.packedPosition += this.bitWidth;
        this.totalValues += this.inputSize;
        this.inputSize = 0;
    }
    
    private void initPackedSlab() {
        this.packed = new byte[this.slabSize];
        this.packedPosition = 0;
    }
    
    public BytesInput toBytes() throws IOException {
        final int packedByteLength = this.packedPosition + BytesUtils.paddedByteCountFromBits(this.inputSize * this.bitWidth);
        if (Log.DEBUG) {
            ByteBasedBitPackingEncoder.LOG.debug("writing " + (this.slabs.size() * this.slabSize + packedByteLength) + " bytes");
        }
        if (this.inputSize > 0) {
            for (int i = this.inputSize; i < this.input.length; ++i) {
                this.input[i] = 0;
            }
            this.pack();
        }
        return BytesInput.concat(BytesInput.concat(this.slabs), BytesInput.from(this.packed, 0, packedByteLength));
    }
    
    public long getBufferSize() {
        return BytesUtils.paddedByteCountFromBits(this.totalValues * this.bitWidth);
    }
    
    public long getAllocatedSize() {
        return this.slabs.size() * this.slabSize + this.packed.length + this.input.length * 4;
    }
    
    public String memUsageString(final String prefix) {
        return String.format("%s ByteBitPacking %d slabs, %d bytes", prefix, this.slabs.size(), this.getAllocatedSize());
    }
    
    static {
        LOG = Log.getLog(ByteBasedBitPackingEncoder.class);
    }
}
