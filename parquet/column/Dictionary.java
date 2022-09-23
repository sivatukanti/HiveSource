// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column;

import parquet.io.api.Binary;

public abstract class Dictionary
{
    private final Encoding encoding;
    
    public Dictionary(final Encoding encoding) {
        this.encoding = encoding;
    }
    
    public Encoding getEncoding() {
        return this.encoding;
    }
    
    public abstract int getMaxId();
    
    public Binary decodeToBinary(final int id) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public int decodeToInt(final int id) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public long decodeToLong(final int id) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public float decodeToFloat(final int id) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public double decodeToDouble(final int id) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public boolean decodeToBoolean(final int id) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
}
