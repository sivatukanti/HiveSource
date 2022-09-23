// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

import parquet.bytes.BytesInput;
import parquet.column.Encoding;
import java.io.IOException;
import parquet.io.ParquetEncodingException;
import parquet.bytes.BytesUtils;
import parquet.column.values.ValuesWriter;

public class ByteBitPackingValuesWriter extends ValuesWriter
{
    private final Packer packer;
    private final int bitWidth;
    private ByteBasedBitPackingEncoder encoder;
    
    public ByteBitPackingValuesWriter(final int bound, final Packer packer) {
        this.packer = packer;
        this.bitWidth = BytesUtils.getWidthFromMaxInt(bound);
        this.encoder = new ByteBasedBitPackingEncoder(this.bitWidth, packer);
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
    public Encoding getEncoding() {
        return Encoding.BIT_PACKED;
    }
    
    @Override
    public BytesInput getBytes() {
        try {
            return this.encoder.toBytes();
        }
        catch (IOException e) {
            throw new ParquetEncodingException(e);
        }
    }
    
    @Override
    public void reset() {
        this.encoder = new ByteBasedBitPackingEncoder(this.bitWidth, this.packer);
    }
    
    @Override
    public long getBufferedSize() {
        return this.encoder.getBufferSize();
    }
    
    @Override
    public long getAllocatedSize() {
        return this.encoder.getAllocatedSize();
    }
    
    @Override
    public String memUsageString(final String prefix) {
        return this.encoder.memUsageString(prefix);
    }
}
