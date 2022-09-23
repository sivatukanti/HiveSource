// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.plain;

import java.io.IOException;
import parquet.io.ParquetDecodingException;
import parquet.bytes.BytesUtils;
import parquet.io.api.Binary;
import parquet.Log;
import parquet.column.values.ValuesReader;

public class BinaryPlainValuesReader extends ValuesReader
{
    private static final Log LOG;
    private byte[] in;
    private int offset;
    
    @Override
    public Binary readBytes() {
        try {
            final int length = BytesUtils.readIntLittleEndian(this.in, this.offset);
            final int start = this.offset + 4;
            this.offset = start + length;
            return Binary.fromByteArray(this.in, start, length);
        }
        catch (IOException e) {
            throw new ParquetDecodingException("could not read bytes at offset " + this.offset, e);
        }
        catch (RuntimeException e2) {
            throw new ParquetDecodingException("could not read bytes at offset " + this.offset, e2);
        }
    }
    
    @Override
    public void skip() {
        try {
            final int length = BytesUtils.readIntLittleEndian(this.in, this.offset);
            this.offset += 4 + length;
        }
        catch (IOException e) {
            throw new ParquetDecodingException("could not skip bytes at offset " + this.offset, e);
        }
        catch (RuntimeException e2) {
            throw new ParquetDecodingException("could not skip bytes at offset " + this.offset, e2);
        }
    }
    
    @Override
    public void initFromPage(final int valueCount, final byte[] in, final int offset) throws IOException {
        if (Log.DEBUG) {
            BinaryPlainValuesReader.LOG.debug("init from page at offset " + offset + " for length " + (in.length - offset));
        }
        this.in = in;
        this.offset = offset;
    }
    
    static {
        LOG = Log.getLog(BinaryPlainValuesReader.class);
    }
}
