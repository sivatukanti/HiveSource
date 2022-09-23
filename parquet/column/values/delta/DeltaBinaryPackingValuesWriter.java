// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.delta;

import parquet.column.Encoding;
import parquet.bytes.BytesInput;
import java.io.IOException;
import parquet.io.ParquetEncodingException;
import java.io.OutputStream;
import parquet.bytes.BytesUtils;
import parquet.column.values.bitpacking.BytePacker;
import parquet.column.values.bitpacking.Packer;
import parquet.bytes.CapacityByteArrayOutputStream;
import parquet.column.values.ValuesWriter;

public class DeltaBinaryPackingValuesWriter extends ValuesWriter
{
    public static final int MAX_BITWIDTH = 32;
    public static final int DEFAULT_NUM_BLOCK_VALUES = 128;
    public static final int DEFAULT_NUM_MINIBLOCKS = 4;
    private final CapacityByteArrayOutputStream baos;
    private final DeltaBinaryPackingConfig config;
    private final int[] bitWidths;
    private int totalValueCount;
    private int deltaValuesToFlush;
    private int[] deltaBlockBuffer;
    private byte[] miniBlockByteBuffer;
    private int firstValue;
    private int previousValue;
    private int minDeltaInCurrentBlock;
    
    public DeltaBinaryPackingValuesWriter(final int slabSize, final int pageSize) {
        this(128, 4, slabSize, pageSize);
    }
    
    public DeltaBinaryPackingValuesWriter(final int blockSizeInValues, final int miniBlockNum, final int slabSize, final int pageSize) {
        this.totalValueCount = 0;
        this.deltaValuesToFlush = 0;
        this.firstValue = 0;
        this.previousValue = 0;
        this.minDeltaInCurrentBlock = Integer.MAX_VALUE;
        this.config = new DeltaBinaryPackingConfig(blockSizeInValues, miniBlockNum);
        this.bitWidths = new int[this.config.miniBlockNumInABlock];
        this.deltaBlockBuffer = new int[blockSizeInValues];
        this.miniBlockByteBuffer = new byte[this.config.miniBlockSizeInValues * 32];
        this.baos = new CapacityByteArrayOutputStream(slabSize, pageSize);
    }
    
    @Override
    public long getBufferedSize() {
        return this.baos.size();
    }
    
    @Override
    public void writeInteger(final int v) {
        ++this.totalValueCount;
        if (this.totalValueCount == 1) {
            this.firstValue = v;
            this.previousValue = this.firstValue;
            return;
        }
        final int delta = v - this.previousValue;
        this.previousValue = v;
        if ((this.deltaBlockBuffer[this.deltaValuesToFlush++] = delta) < this.minDeltaInCurrentBlock) {
            this.minDeltaInCurrentBlock = delta;
        }
        if (this.config.blockSizeInValues == this.deltaValuesToFlush) {
            this.flushBlockBuffer();
        }
    }
    
    private void flushBlockBuffer() {
        for (int i = 0; i < this.deltaValuesToFlush; ++i) {
            this.deltaBlockBuffer[i] -= this.minDeltaInCurrentBlock;
        }
        this.writeMinDelta();
        final int miniBlocksToFlush = this.getMiniBlockCountToFlush(this.deltaValuesToFlush);
        this.calculateBitWidthsForDeltaBlockBuffer(miniBlocksToFlush);
        for (int j = 0; j < this.config.miniBlockNumInABlock; ++j) {
            this.writeBitWidthForMiniBlock(j);
        }
        for (int j = 0; j < miniBlocksToFlush; ++j) {
            final int currentBitWidth = this.bitWidths[j];
            final BytePacker packer = Packer.LITTLE_ENDIAN.newBytePacker(currentBitWidth);
            int k;
            for (int miniBlockStart = k = j * this.config.miniBlockSizeInValues; k < (j + 1) * this.config.miniBlockSizeInValues; k += 8) {
                packer.pack8Values(this.deltaBlockBuffer, k, this.miniBlockByteBuffer, 0);
                this.baos.write(this.miniBlockByteBuffer, 0, currentBitWidth);
            }
        }
        this.minDeltaInCurrentBlock = Integer.MAX_VALUE;
        this.deltaValuesToFlush = 0;
    }
    
    private void writeBitWidthForMiniBlock(final int i) {
        try {
            BytesUtils.writeIntLittleEndianOnOneByte(this.baos, this.bitWidths[i]);
        }
        catch (IOException e) {
            throw new ParquetEncodingException("can not write bitwith for miniblock", e);
        }
    }
    
    private void writeMinDelta() {
        try {
            BytesUtils.writeZigZagVarInt(this.minDeltaInCurrentBlock, this.baos);
        }
        catch (IOException e) {
            throw new ParquetEncodingException("can not write min delta for block", e);
        }
    }
    
    private void calculateBitWidthsForDeltaBlockBuffer(final int miniBlocksToFlush) {
        for (int miniBlockIndex = 0; miniBlockIndex < miniBlocksToFlush; ++miniBlockIndex) {
            int mask = 0;
            final int miniStart = miniBlockIndex * this.config.miniBlockSizeInValues;
            for (int miniEnd = Math.min((miniBlockIndex + 1) * this.config.miniBlockSizeInValues, this.deltaValuesToFlush), i = miniStart; i < miniEnd; ++i) {
                mask |= this.deltaBlockBuffer[i];
            }
            this.bitWidths[miniBlockIndex] = 32 - Integer.numberOfLeadingZeros(mask);
        }
    }
    
    private int getMiniBlockCountToFlush(final double numberCount) {
        return (int)Math.ceil(numberCount / this.config.miniBlockSizeInValues);
    }
    
    @Override
    public BytesInput getBytes() {
        if (this.deltaValuesToFlush != 0) {
            this.flushBlockBuffer();
        }
        return BytesInput.concat(this.config.toBytesInput(), BytesInput.fromUnsignedVarInt(this.totalValueCount), BytesInput.fromZigZagVarInt(this.firstValue), BytesInput.from(this.baos));
    }
    
    @Override
    public Encoding getEncoding() {
        return Encoding.DELTA_BINARY_PACKED;
    }
    
    @Override
    public void reset() {
        this.totalValueCount = 0;
        this.baos.reset();
        this.deltaValuesToFlush = 0;
        this.minDeltaInCurrentBlock = Integer.MAX_VALUE;
    }
    
    @Override
    public long getAllocatedSize() {
        return this.baos.getCapacity();
    }
    
    @Override
    public String memUsageString(final String prefix) {
        return String.format("%s DeltaBinaryPacking %d bytes", prefix, this.getAllocatedSize());
    }
}
