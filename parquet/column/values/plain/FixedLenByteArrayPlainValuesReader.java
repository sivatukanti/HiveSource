// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.plain;

import java.io.IOException;
import parquet.io.ParquetDecodingException;
import parquet.io.api.Binary;
import parquet.Log;
import parquet.column.values.ValuesReader;

public class FixedLenByteArrayPlainValuesReader extends ValuesReader
{
    private static final Log LOG;
    private byte[] in;
    private int offset;
    private int length;
    
    public FixedLenByteArrayPlainValuesReader(final int length) {
        this.length = length;
    }
    
    @Override
    public Binary readBytes() {
        try {
            final int start = this.offset;
            this.offset = start + this.length;
            return Binary.fromByteArray(this.in, start, this.length);
        }
        catch (RuntimeException e) {
            throw new ParquetDecodingException("could not read bytes at offset " + this.offset, e);
        }
    }
    
    @Override
    public void skip() {
        this.offset += this.length;
    }
    
    @Override
    public void initFromPage(final int valueCount, final byte[] in, final int offset) throws IOException {
        if (Log.DEBUG) {
            FixedLenByteArrayPlainValuesReader.LOG.debug("init from page at offset " + offset + " for length " + (in.length - offset));
        }
        this.in = in;
        this.offset = offset;
    }
    
    static {
        LOG = Log.getLog(FixedLenByteArrayPlainValuesReader.class);
    }
}
