// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.rle;

import parquet.column.Encoding;
import parquet.Ints;
import parquet.bytes.BytesInput;
import java.io.IOException;
import parquet.io.ParquetEncodingException;
import parquet.column.values.ValuesWriter;

public class RunLengthBitPackingHybridValuesWriter extends ValuesWriter
{
    private final RunLengthBitPackingHybridEncoder encoder;
    
    public RunLengthBitPackingHybridValuesWriter(final int bitWidth, final int initialCapacity, final int pageSize) {
        this.encoder = new RunLengthBitPackingHybridEncoder(bitWidth, initialCapacity, pageSize);
    }
    
    @Override
    public void writeInteger(final int v) {
        try {
            this.encoder.writeInt(v);
        }
        catch (IOException e) {
            throw new ParquetEncodingException(e);
        }
    }
    
    @Override
    public void writeBoolean(final boolean v) {
        this.writeInteger(v ? 1 : 0);
    }
    
    @Override
    public long getBufferedSize() {
        return this.encoder.getBufferedSize();
    }
    
    @Override
    public long getAllocatedSize() {
        return this.encoder.getAllocatedSize();
    }
    
    @Override
    public BytesInput getBytes() {
        try {
            final BytesInput rle = this.encoder.toBytes();
            return BytesInput.concat(BytesInput.fromInt(Ints.checkedCast(rle.size())), rle);
        }
        catch (IOException e) {
            throw new ParquetEncodingException(e);
        }
    }
    
    @Override
    public Encoding getEncoding() {
        return Encoding.RLE;
    }
    
    @Override
    public void reset() {
        this.encoder.reset();
    }
    
    @Override
    public String memUsageString(final String prefix) {
        return String.format("%s RunLengthBitPackingHybrid %d bytes", prefix, this.getAllocatedSize());
    }
}
