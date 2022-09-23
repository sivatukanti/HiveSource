// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.rle;

import parquet.io.ParquetDecodingException;
import java.io.IOException;
import java.io.InputStream;
import parquet.bytes.BytesUtils;
import java.io.ByteArrayInputStream;
import parquet.column.values.ValuesReader;

public class RunLengthBitPackingHybridValuesReader extends ValuesReader
{
    private final int bitWidth;
    private RunLengthBitPackingHybridDecoder decoder;
    private int nextOffset;
    
    public RunLengthBitPackingHybridValuesReader(final int bitWidth) {
        this.bitWidth = bitWidth;
    }
    
    @Override
    public void initFromPage(final int valueCountL, final byte[] page, final int offset) throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(page, offset, page.length - offset);
        final int length = BytesUtils.readIntLittleEndian(in);
        this.decoder = new RunLengthBitPackingHybridDecoder(this.bitWidth, in);
        this.nextOffset = offset + length + 4;
    }
    
    @Override
    public int getNextOffset() {
        return this.nextOffset;
    }
    
    @Override
    public int readInteger() {
        try {
            return this.decoder.readInt();
        }
        catch (IOException e) {
            throw new ParquetDecodingException(e);
        }
    }
    
    @Override
    public boolean readBoolean() {
        return this.readInteger() != 0;
    }
    
    @Override
    public void skip() {
        this.readInteger();
    }
}
