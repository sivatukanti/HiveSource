// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.boundedint;

import java.io.IOException;
import parquet.bytes.BytesUtils;
import parquet.io.ParquetDecodingException;
import parquet.Log;
import parquet.column.values.ValuesReader;

class BoundedIntValuesReader extends ValuesReader
{
    private static final Log LOG;
    private int currentValueCt;
    private int currentValue;
    private final int bitsPerValue;
    private BitReader bitReader;
    private int nextOffset;
    
    public BoundedIntValuesReader(final int bound) {
        this.currentValueCt = 0;
        this.currentValue = 0;
        this.bitReader = new BitReader();
        if (bound == 0) {
            throw new ParquetDecodingException("Value bound cannot be 0. Use DevNullColumnReader instead.");
        }
        this.bitsPerValue = BytesUtils.getWidthFromMaxInt(bound);
    }
    
    @Override
    public int readInteger() {
        try {
            if (this.currentValueCt > 0) {
                --this.currentValueCt;
                return this.currentValue;
            }
            if (this.bitReader.readBit()) {
                this.currentValue = this.bitReader.readNBitInteger(this.bitsPerValue);
                this.currentValueCt = this.bitReader.readUnsignedVarint() - 1;
            }
            else {
                this.currentValue = this.bitReader.readNBitInteger(this.bitsPerValue);
            }
            return this.currentValue;
        }
        catch (IOException e) {
            throw new ParquetDecodingException("could not read int", e);
        }
    }
    
    @Override
    public void initFromPage(final int valueCount, final byte[] in, final int offset) throws IOException {
        if (Log.DEBUG) {
            BoundedIntValuesReader.LOG.debug("reading size at " + offset + ": " + in[offset] + " " + in[offset + 1] + " " + in[offset + 2] + " " + in[offset + 3] + " ");
        }
        final int totalBytes = BytesUtils.readIntLittleEndian(in, offset);
        if (Log.DEBUG) {
            BoundedIntValuesReader.LOG.debug("will read " + totalBytes + " bytes");
        }
        this.currentValueCt = 0;
        this.currentValue = 0;
        this.bitReader.prepare(in, offset + 4, totalBytes);
        if (Log.DEBUG) {
            BoundedIntValuesReader.LOG.debug("will read next from " + (offset + totalBytes + 4));
        }
        this.nextOffset = offset + totalBytes + 4;
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
        LOG = Log.getLog(BoundedIntValuesReader.class);
    }
}
