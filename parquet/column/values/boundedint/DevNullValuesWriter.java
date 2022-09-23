// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.boundedint;

import parquet.column.Encoding;
import parquet.bytes.BytesInput;
import parquet.io.api.Binary;
import parquet.column.values.ValuesWriter;

public class DevNullValuesWriter extends ValuesWriter
{
    @Override
    public long getBufferedSize() {
        return 0L;
    }
    
    @Override
    public void reset() {
    }
    
    @Override
    public void writeInteger(final int v) {
    }
    
    @Override
    public void writeByte(final int value) {
    }
    
    @Override
    public void writeBoolean(final boolean v) {
    }
    
    @Override
    public void writeBytes(final Binary v) {
    }
    
    @Override
    public void writeLong(final long v) {
    }
    
    @Override
    public void writeDouble(final double v) {
    }
    
    @Override
    public void writeFloat(final float v) {
    }
    
    @Override
    public BytesInput getBytes() {
        return BytesInput.empty();
    }
    
    @Override
    public long getAllocatedSize() {
        return 0L;
    }
    
    @Override
    public Encoding getEncoding() {
        return Encoding.BIT_PACKED;
    }
    
    @Override
    public String memUsageString(final String prefix) {
        return prefix + "0";
    }
}
