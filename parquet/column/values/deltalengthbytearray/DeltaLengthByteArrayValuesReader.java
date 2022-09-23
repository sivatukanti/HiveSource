// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.deltalengthbytearray;

import parquet.io.api.Binary;
import java.io.IOException;
import parquet.column.values.delta.DeltaBinaryPackingValuesReader;
import parquet.Log;
import parquet.column.values.ValuesReader;

public class DeltaLengthByteArrayValuesReader extends ValuesReader
{
    private static final Log LOG;
    private ValuesReader lengthReader;
    private byte[] in;
    private int offset;
    
    public DeltaLengthByteArrayValuesReader() {
        this.lengthReader = new DeltaBinaryPackingValuesReader();
    }
    
    @Override
    public void initFromPage(final int valueCount, final byte[] in, int offset) throws IOException {
        if (Log.DEBUG) {
            DeltaLengthByteArrayValuesReader.LOG.debug("init from page at offset " + offset + " for length " + (in.length - offset));
        }
        this.lengthReader.initFromPage(valueCount, in, offset);
        offset = this.lengthReader.getNextOffset();
        this.in = in;
        this.offset = offset;
    }
    
    @Override
    public Binary readBytes() {
        final int length = this.lengthReader.readInteger();
        final int start = this.offset;
        this.offset = start + length;
        return Binary.fromByteArray(this.in, start, length);
    }
    
    @Override
    public void skip() {
        final int length = this.lengthReader.readInteger();
        this.offset += length;
    }
    
    static {
        LOG = Log.getLog(DeltaLengthByteArrayValuesReader.class);
    }
}
