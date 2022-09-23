// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.plain;

import java.io.IOException;
import parquet.column.values.bitpacking.Packer;
import parquet.column.values.bitpacking.ByteBitPackingValuesReader;
import parquet.Log;
import parquet.column.values.ValuesReader;

public class BooleanPlainValuesReader extends ValuesReader
{
    private static final Log LOG;
    private ByteBitPackingValuesReader in;
    
    public BooleanPlainValuesReader() {
        this.in = new ByteBitPackingValuesReader(1, Packer.LITTLE_ENDIAN);
    }
    
    @Override
    public boolean readBoolean() {
        return this.in.readInteger() != 0;
    }
    
    @Override
    public void skip() {
        this.in.readInteger();
    }
    
    @Override
    public void initFromPage(final int valueCount, final byte[] in, final int offset) throws IOException {
        if (Log.DEBUG) {
            BooleanPlainValuesReader.LOG.debug("init from page at offset " + offset + " for length " + (in.length - offset));
        }
        this.in.initFromPage(valueCount, in, offset);
    }
    
    @Override
    public int getNextOffset() {
        return this.in.getNextOffset();
    }
    
    static {
        LOG = Log.getLog(BooleanPlainValuesReader.class);
    }
}
