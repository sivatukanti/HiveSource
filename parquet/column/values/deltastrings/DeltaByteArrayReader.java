// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.deltastrings;

import java.io.IOException;
import parquet.column.values.deltalengthbytearray.DeltaLengthByteArrayValuesReader;
import parquet.column.values.delta.DeltaBinaryPackingValuesReader;
import parquet.io.api.Binary;
import parquet.column.values.ValuesReader;

public class DeltaByteArrayReader extends ValuesReader
{
    private ValuesReader prefixLengthReader;
    private ValuesReader suffixReader;
    private Binary previous;
    
    public DeltaByteArrayReader() {
        this.prefixLengthReader = new DeltaBinaryPackingValuesReader();
        this.suffixReader = new DeltaLengthByteArrayValuesReader();
        this.previous = Binary.fromByteArray(new byte[0]);
    }
    
    @Override
    public void initFromPage(final int valueCount, final byte[] page, final int offset) throws IOException {
        this.prefixLengthReader.initFromPage(valueCount, page, offset);
        final int next = this.prefixLengthReader.getNextOffset();
        this.suffixReader.initFromPage(valueCount, page, next);
    }
    
    @Override
    public void skip() {
        this.prefixLengthReader.skip();
        this.suffixReader.skip();
    }
    
    @Override
    public Binary readBytes() {
        final int prefixLength = this.prefixLengthReader.readInteger();
        final Binary suffix = this.suffixReader.readBytes();
        final int length = prefixLength + suffix.length();
        if (prefixLength != 0) {
            final byte[] out = new byte[length];
            System.arraycopy(this.previous.getBytes(), 0, out, 0, prefixLength);
            System.arraycopy(suffix.getBytes(), 0, out, prefixLength, suffix.length());
            this.previous = Binary.fromByteArray(out);
        }
        else {
            this.previous = suffix;
        }
        return this.previous;
    }
}
