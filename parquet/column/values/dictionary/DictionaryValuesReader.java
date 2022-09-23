// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.dictionary;

import parquet.io.api.Binary;
import parquet.io.ParquetDecodingException;
import java.io.IOException;
import java.io.InputStream;
import parquet.bytes.BytesUtils;
import parquet.column.values.rle.RunLengthBitPackingHybridDecoder;
import parquet.column.Dictionary;
import java.io.ByteArrayInputStream;
import parquet.Log;
import parquet.column.values.ValuesReader;

public class DictionaryValuesReader extends ValuesReader
{
    private static final Log LOG;
    private ByteArrayInputStream in;
    private Dictionary dictionary;
    private RunLengthBitPackingHybridDecoder decoder;
    
    public DictionaryValuesReader(final Dictionary dictionary) {
        this.dictionary = dictionary;
    }
    
    @Override
    public void initFromPage(final int valueCount, final byte[] page, final int offset) throws IOException {
        this.in = new ByteArrayInputStream(page, offset, page.length - offset);
        if (page.length - offset > 0) {
            if (Log.DEBUG) {
                DictionaryValuesReader.LOG.debug("init from page at offset " + offset + " for length " + (page.length - offset));
            }
            final int bitWidth = BytesUtils.readIntLittleEndianOnOneByte(this.in);
            if (Log.DEBUG) {
                DictionaryValuesReader.LOG.debug("bit width " + bitWidth);
            }
            this.decoder = new RunLengthBitPackingHybridDecoder(bitWidth, this.in);
        }
        else {
            this.decoder = new RunLengthBitPackingHybridDecoder(1, this.in) {
                @Override
                public int readInt() throws IOException {
                    throw new IOException("Attempt to read from empty page");
                }
            };
        }
    }
    
    @Override
    public int readValueDictionaryId() {
        try {
            return this.decoder.readInt();
        }
        catch (IOException e) {
            throw new ParquetDecodingException(e);
        }
    }
    
    @Override
    public Binary readBytes() {
        try {
            return this.dictionary.decodeToBinary(this.decoder.readInt());
        }
        catch (IOException e) {
            throw new ParquetDecodingException(e);
        }
    }
    
    @Override
    public float readFloat() {
        try {
            return this.dictionary.decodeToFloat(this.decoder.readInt());
        }
        catch (IOException e) {
            throw new ParquetDecodingException(e);
        }
    }
    
    @Override
    public double readDouble() {
        try {
            return this.dictionary.decodeToDouble(this.decoder.readInt());
        }
        catch (IOException e) {
            throw new ParquetDecodingException(e);
        }
    }
    
    @Override
    public int readInteger() {
        try {
            return this.dictionary.decodeToInt(this.decoder.readInt());
        }
        catch (IOException e) {
            throw new ParquetDecodingException(e);
        }
    }
    
    @Override
    public long readLong() {
        try {
            return this.dictionary.decodeToLong(this.decoder.readInt());
        }
        catch (IOException e) {
            throw new ParquetDecodingException(e);
        }
    }
    
    @Override
    public void skip() {
        try {
            this.decoder.readInt();
        }
        catch (IOException e) {
            throw new ParquetDecodingException(e);
        }
    }
    
    static {
        LOG = Log.getLog(DictionaryValuesReader.class);
    }
}
