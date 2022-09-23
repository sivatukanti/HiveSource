// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.plain;

import parquet.io.ParquetDecodingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import parquet.bytes.LittleEndianDataInputStream;
import parquet.Log;
import parquet.column.values.ValuesReader;

public abstract class PlainValuesReader extends ValuesReader
{
    private static final Log LOG;
    protected LittleEndianDataInputStream in;
    
    @Override
    public void initFromPage(final int valueCount, final byte[] in, final int offset) throws IOException {
        if (Log.DEBUG) {
            PlainValuesReader.LOG.debug("init from page at offset " + offset + " for length " + (in.length - offset));
        }
        this.in = new LittleEndianDataInputStream(new ByteArrayInputStream(in, offset, in.length - offset));
    }
    
    static {
        LOG = Log.getLog(PlainValuesReader.class);
    }
    
    public static class DoublePlainValuesReader extends PlainValuesReader
    {
        @Override
        public void skip() {
            try {
                this.in.skipBytes(8);
            }
            catch (IOException e) {
                throw new ParquetDecodingException("could not skip double", e);
            }
        }
        
        @Override
        public double readDouble() {
            try {
                return this.in.readDouble();
            }
            catch (IOException e) {
                throw new ParquetDecodingException("could not read double", e);
            }
        }
    }
    
    public static class FloatPlainValuesReader extends PlainValuesReader
    {
        @Override
        public void skip() {
            try {
                this.in.skipBytes(4);
            }
            catch (IOException e) {
                throw new ParquetDecodingException("could not skip float", e);
            }
        }
        
        @Override
        public float readFloat() {
            try {
                return this.in.readFloat();
            }
            catch (IOException e) {
                throw new ParquetDecodingException("could not read float", e);
            }
        }
    }
    
    public static class IntegerPlainValuesReader extends PlainValuesReader
    {
        @Override
        public void skip() {
            try {
                this.in.skipBytes(4);
            }
            catch (IOException e) {
                throw new ParquetDecodingException("could not skip int", e);
            }
        }
        
        @Override
        public int readInteger() {
            try {
                return this.in.readInt();
            }
            catch (IOException e) {
                throw new ParquetDecodingException("could not read int", e);
            }
        }
    }
    
    public static class LongPlainValuesReader extends PlainValuesReader
    {
        @Override
        public void skip() {
            try {
                this.in.skipBytes(8);
            }
            catch (IOException e) {
                throw new ParquetDecodingException("could not skip long", e);
            }
        }
        
        @Override
        public long readLong() {
            try {
                return this.in.readLong();
            }
            catch (IOException e) {
                throw new ParquetDecodingException("could not read long", e);
            }
        }
    }
}
