// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

import parquet.column.Encoding;
import parquet.bytes.BytesInput;
import java.io.IOException;
import parquet.io.ParquetEncodingException;
import java.io.OutputStream;
import parquet.bytes.BytesUtils;
import parquet.bytes.CapacityByteArrayOutputStream;
import parquet.column.values.ValuesWriter;

public class BitPackingValuesWriter extends ValuesWriter
{
    private CapacityByteArrayOutputStream out;
    private BitPacking.BitPackingWriter bitPackingWriter;
    private int bitsPerValue;
    
    public BitPackingValuesWriter(final int bound, final int initialCapacity, final int pageSize) {
        this.bitsPerValue = BytesUtils.getWidthFromMaxInt(bound);
        this.out = new CapacityByteArrayOutputStream(initialCapacity, pageSize);
        this.init();
    }
    
    private void init() {
        this.bitPackingWriter = BitPacking.getBitPackingWriter(this.bitsPerValue, this.out);
    }
    
    @Override
    public void writeInteger(final int v) {
        try {
            this.bitPackingWriter.write(v);
        }
        catch (IOException e) {
            throw new ParquetEncodingException(e);
        }
    }
    
    @Override
    public long getBufferedSize() {
        return this.out.size();
    }
    
    @Override
    public BytesInput getBytes() {
        try {
            this.bitPackingWriter.finish();
            return BytesInput.from(this.out);
        }
        catch (IOException e) {
            throw new ParquetEncodingException(e);
        }
    }
    
    @Override
    public void reset() {
        this.out.reset();
        this.init();
    }
    
    @Override
    public long getAllocatedSize() {
        return this.out.getCapacity();
    }
    
    @Override
    public String memUsageString(final String prefix) {
        return this.out.memUsageString(prefix);
    }
    
    @Override
    public Encoding getEncoding() {
        return Encoding.BIT_PACKED;
    }
}
