// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.plain;

import parquet.column.Encoding;
import parquet.bytes.BytesInput;
import java.io.IOException;
import parquet.io.ParquetEncodingException;
import parquet.io.api.Binary;
import java.io.OutputStream;
import parquet.bytes.LittleEndianDataOutputStream;
import parquet.bytes.CapacityByteArrayOutputStream;
import parquet.Log;
import parquet.column.values.ValuesWriter;

public class FixedLenByteArrayPlainValuesWriter extends ValuesWriter
{
    private static final Log LOG;
    private CapacityByteArrayOutputStream arrayOut;
    private LittleEndianDataOutputStream out;
    private int length;
    
    public FixedLenByteArrayPlainValuesWriter(final int length, final int initialSize, final int pageSize) {
        this.length = length;
        this.arrayOut = new CapacityByteArrayOutputStream(initialSize, pageSize);
        this.out = new LittleEndianDataOutputStream(this.arrayOut);
    }
    
    @Override
    public final void writeBytes(final Binary v) {
        if (v.length() != this.length) {
            throw new IllegalArgumentException("Fixed Binary size " + v.length() + " does not match field type length " + this.length);
        }
        try {
            v.writeTo(this.out);
        }
        catch (IOException e) {
            throw new ParquetEncodingException("could not write fixed bytes", e);
        }
    }
    
    @Override
    public long getBufferedSize() {
        return this.arrayOut.size();
    }
    
    @Override
    public BytesInput getBytes() {
        try {
            this.out.flush();
        }
        catch (IOException e) {
            throw new ParquetEncodingException("could not write page", e);
        }
        if (Log.DEBUG) {
            FixedLenByteArrayPlainValuesWriter.LOG.debug("writing a buffer of size " + this.arrayOut.size());
        }
        return BytesInput.from(this.arrayOut);
    }
    
    @Override
    public void reset() {
        this.arrayOut.reset();
    }
    
    @Override
    public long getAllocatedSize() {
        return this.arrayOut.getCapacity();
    }
    
    @Override
    public Encoding getEncoding() {
        return Encoding.PLAIN;
    }
    
    @Override
    public String memUsageString(final String prefix) {
        return this.arrayOut.memUsageString(prefix + " PLAIN");
    }
    
    static {
        LOG = Log.getLog(PlainValuesWriter.class);
    }
}
