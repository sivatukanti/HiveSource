// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

import java.io.InputStream;
import java.io.IOException;
import parquet.io.ParquetDecodingException;
import parquet.bytes.BytesUtils;
import java.io.ByteArrayInputStream;
import parquet.Log;
import parquet.column.values.ValuesReader;

public class BitPackingValuesReader extends ValuesReader
{
    private static final Log LOG;
    private ByteArrayInputStream in;
    private BitPacking.BitPackingReader bitPackingReader;
    private final int bitsPerValue;
    private int nextOffset;
    
    public BitPackingValuesReader(final int bound) {
        this.bitsPerValue = BytesUtils.getWidthFromMaxInt(bound);
    }
    
    @Override
    public int readInteger() {
        try {
            return this.bitPackingReader.read();
        }
        catch (IOException e) {
            throw new ParquetDecodingException(e);
        }
    }
    
    @Override
    public void initFromPage(final int valueCount, final byte[] in, final int offset) throws IOException {
        final int effectiveBitLength = valueCount * this.bitsPerValue;
        final int length = BytesUtils.paddedByteCountFromBits(effectiveBitLength);
        if (Log.DEBUG) {
            BitPackingValuesReader.LOG.debug("reading " + length + " bytes for " + valueCount + " values of size " + this.bitsPerValue + " bits.");
        }
        this.in = new ByteArrayInputStream(in, offset, length);
        this.bitPackingReader = BitPacking.createBitPackingReader(this.bitsPerValue, this.in, valueCount);
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
        LOG = Log.getLog(BitPackingValuesReader.class);
    }
}
