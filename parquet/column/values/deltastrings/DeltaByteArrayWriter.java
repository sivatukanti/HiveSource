// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.deltastrings;

import parquet.io.api.Binary;
import parquet.column.Encoding;
import parquet.bytes.BytesInput;
import parquet.column.values.deltalengthbytearray.DeltaLengthByteArrayValuesWriter;
import parquet.column.values.delta.DeltaBinaryPackingValuesWriter;
import parquet.column.values.ValuesWriter;

public class DeltaByteArrayWriter extends ValuesWriter
{
    private ValuesWriter prefixLengthWriter;
    private ValuesWriter suffixWriter;
    private byte[] previous;
    
    public DeltaByteArrayWriter(final int initialCapacity, final int pageSize) {
        this.prefixLengthWriter = new DeltaBinaryPackingValuesWriter(128, 4, initialCapacity, pageSize);
        this.suffixWriter = new DeltaLengthByteArrayValuesWriter(initialCapacity, pageSize);
        this.previous = new byte[0];
    }
    
    @Override
    public long getBufferedSize() {
        return this.prefixLengthWriter.getBufferedSize() + this.suffixWriter.getBufferedSize();
    }
    
    @Override
    public BytesInput getBytes() {
        return BytesInput.concat(this.prefixLengthWriter.getBytes(), this.suffixWriter.getBytes());
    }
    
    @Override
    public Encoding getEncoding() {
        return Encoding.DELTA_BYTE_ARRAY;
    }
    
    @Override
    public void reset() {
        this.prefixLengthWriter.reset();
        this.suffixWriter.reset();
    }
    
    @Override
    public long getAllocatedSize() {
        return this.prefixLengthWriter.getAllocatedSize() + this.suffixWriter.getAllocatedSize();
    }
    
    @Override
    public String memUsageString(String prefix) {
        prefix = this.prefixLengthWriter.memUsageString(prefix);
        return this.suffixWriter.memUsageString(prefix + "  DELTA_STRINGS");
    }
    
    @Override
    public void writeBytes(final Binary v) {
        int i;
        byte[] vb;
        int length;
        for (i = 0, vb = v.getBytes(), length = ((this.previous.length < vb.length) ? this.previous.length : vb.length), i = 0; i < length && this.previous[i] == vb[i]; ++i) {}
        this.prefixLengthWriter.writeInteger(i);
        this.suffixWriter.writeBytes(Binary.fromByteArray(vb, i, vb.length - i));
        this.previous = vb;
    }
}
