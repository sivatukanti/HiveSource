// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.delta;

import parquet.column.values.bitpacking.BytePacker;
import parquet.column.values.bitpacking.Packer;
import parquet.io.ParquetDecodingException;
import java.io.IOException;
import parquet.bytes.BytesUtils;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import parquet.column.values.ValuesReader;

public class DeltaBinaryPackingValuesReader extends ValuesReader
{
    private int totalValueCount;
    private int valuesRead;
    private int minDeltaInCurrentBlock;
    private byte[] page;
    private int[] valuesBuffer;
    private int valuesBuffered;
    private ByteArrayInputStream in;
    private int nextOffset;
    private DeltaBinaryPackingConfig config;
    private int[] bitWidths;
    
    @Override
    public void initFromPage(final int valueCount, final byte[] page, final int offset) throws IOException {
        this.in = new ByteArrayInputStream(page, offset, page.length - offset);
        this.config = DeltaBinaryPackingConfig.readConfig(this.in);
        this.page = page;
        this.totalValueCount = BytesUtils.readUnsignedVarInt(this.in);
        this.allocateValuesBuffer();
        this.bitWidths = new int[this.config.miniBlockNumInABlock];
        this.valuesBuffer[this.valuesBuffered++] = BytesUtils.readZigZagVarInt(this.in);
        while (this.valuesBuffered < this.totalValueCount) {
            this.loadNewBlockToBuffer();
        }
        this.nextOffset = page.length - this.in.available();
    }
    
    @Override
    public int getNextOffset() {
        return this.nextOffset;
    }
    
    private void allocateValuesBuffer() {
        final int totalMiniBlockCount = (int)Math.ceil(this.totalValueCount / (double)this.config.miniBlockSizeInValues);
        this.valuesBuffer = new int[totalMiniBlockCount * this.config.miniBlockSizeInValues + 1];
    }
    
    @Override
    public void skip() {
        this.checkRead();
        ++this.valuesRead;
    }
    
    @Override
    public int readInteger() {
        this.checkRead();
        return this.valuesBuffer[this.valuesRead++];
    }
    
    private void checkRead() {
        if (this.valuesRead >= this.totalValueCount) {
            throw new ParquetDecodingException("no more value to read, total value count is " + this.totalValueCount);
        }
    }
    
    private void loadNewBlockToBuffer() {
        try {
            this.minDeltaInCurrentBlock = BytesUtils.readZigZagVarInt(this.in);
        }
        catch (IOException e) {
            throw new ParquetDecodingException("can not read min delta in current block", e);
        }
        this.readBitWidthsForMiniBlocks();
        int i;
        for (i = 0; i < this.config.miniBlockNumInABlock && this.valuesBuffered < this.totalValueCount; ++i) {
            final BytePacker packer = Packer.LITTLE_ENDIAN.newBytePacker(this.bitWidths[i]);
            this.unpackMiniBlock(packer);
        }
        final int valueUnpacked = i * this.config.miniBlockSizeInValues;
        for (int j = this.valuesBuffered - valueUnpacked; j < this.valuesBuffered; ++j) {
            final int index = j;
            final int[] valuesBuffer = this.valuesBuffer;
            final int n = index;
            valuesBuffer[n] += this.minDeltaInCurrentBlock + this.valuesBuffer[index - 1];
        }
    }
    
    private void unpackMiniBlock(final BytePacker packer) {
        for (int j = 0; j < this.config.miniBlockSizeInValues; j += 8) {
            this.unpack8Values(packer);
        }
    }
    
    private void unpack8Values(final BytePacker packer) {
        final int pos = this.page.length - this.in.available();
        packer.unpack8Values(this.page, pos, this.valuesBuffer, this.valuesBuffered);
        this.valuesBuffered += 8;
        this.in.skip(packer.getBitWidth());
    }
    
    private void readBitWidthsForMiniBlocks() {
        for (int i = 0; i < this.config.miniBlockNumInABlock; ++i) {
            try {
                this.bitWidths[i] = BytesUtils.readIntLittleEndianOnOneByte(this.in);
            }
            catch (IOException e) {
                throw new ParquetDecodingException("Can not decode bitwidth in block header", e);
            }
        }
    }
}
