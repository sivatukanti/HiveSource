// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.plain;

import parquet.column.Encoding;
import parquet.bytes.BytesInput;
import parquet.column.values.bitpacking.Packer;
import parquet.column.values.bitpacking.ByteBitPackingValuesWriter;
import parquet.column.values.ValuesWriter;

public class BooleanPlainValuesWriter extends ValuesWriter
{
    private ByteBitPackingValuesWriter bitPackingWriter;
    
    public BooleanPlainValuesWriter() {
        this.bitPackingWriter = new ByteBitPackingValuesWriter(1, Packer.LITTLE_ENDIAN);
    }
    
    @Override
    public final void writeBoolean(final boolean v) {
        this.bitPackingWriter.writeInteger(v ? 1 : 0);
    }
    
    @Override
    public long getBufferedSize() {
        return this.bitPackingWriter.getBufferedSize();
    }
    
    @Override
    public BytesInput getBytes() {
        return this.bitPackingWriter.getBytes();
    }
    
    @Override
    public void reset() {
        this.bitPackingWriter.reset();
    }
    
    @Override
    public long getAllocatedSize() {
        return this.bitPackingWriter.getAllocatedSize();
    }
    
    @Override
    public Encoding getEncoding() {
        return Encoding.PLAIN;
    }
    
    @Override
    public String memUsageString(final String prefix) {
        return this.bitPackingWriter.memUsageString(prefix);
    }
}
