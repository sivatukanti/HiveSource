// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values;

import parquet.io.api.Binary;
import parquet.column.page.DictionaryPage;
import parquet.column.Encoding;
import parquet.bytes.BytesInput;

public abstract class ValuesWriter
{
    public abstract long getBufferedSize();
    
    public abstract BytesInput getBytes();
    
    public abstract Encoding getEncoding();
    
    public abstract void reset();
    
    public DictionaryPage createDictionaryPage() {
        return null;
    }
    
    public void resetDictionary() {
    }
    
    public abstract long getAllocatedSize();
    
    public void writeByte(final int value) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public void writeBoolean(final boolean v) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public void writeBytes(final Binary v) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public void writeInteger(final int v) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public void writeLong(final long v) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public void writeDouble(final double v) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public void writeFloat(final float v) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }
    
    public abstract String memUsageString(final String p0);
}
