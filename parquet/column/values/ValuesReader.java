// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values;

import parquet.io.api.Binary;
import parquet.io.ParquetDecodingException;
import java.io.IOException;

public abstract class ValuesReader
{
    public abstract void initFromPage(final int p0, final byte[] p1, final int p2) throws IOException;
    
    public int getNextOffset() {
        throw new ParquetDecodingException("Unsupported: cannot get offset of the next section.");
    }
    
    public int readValueDictionaryId() {
        throw new UnsupportedOperationException();
    }
    
    public boolean readBoolean() {
        throw new UnsupportedOperationException();
    }
    
    public Binary readBytes() {
        throw new UnsupportedOperationException();
    }
    
    public float readFloat() {
        throw new UnsupportedOperationException();
    }
    
    public double readDouble() {
        throw new UnsupportedOperationException();
    }
    
    public int readInteger() {
        throw new UnsupportedOperationException();
    }
    
    public long readLong() {
        throw new UnsupportedOperationException();
    }
    
    public abstract void skip();
}
