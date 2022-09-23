// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.deltalengthbytearray;

import parquet.column.Encoding;
import parquet.bytes.BytesInput;
import java.io.IOException;
import parquet.io.ParquetEncodingException;
import parquet.io.api.Binary;
import parquet.column.values.delta.DeltaBinaryPackingValuesWriter;
import java.io.OutputStream;
import parquet.bytes.LittleEndianDataOutputStream;
import parquet.bytes.CapacityByteArrayOutputStream;
import parquet.Log;
import parquet.column.values.ValuesWriter;

public class DeltaLengthByteArrayValuesWriter extends ValuesWriter
{
    private static final Log LOG;
    private ValuesWriter lengthWriter;
    private CapacityByteArrayOutputStream arrayOut;
    private LittleEndianDataOutputStream out;
    
    public DeltaLengthByteArrayValuesWriter(final int initialSize, final int pageSize) {
        this.arrayOut = new CapacityByteArrayOutputStream(initialSize, pageSize);
        this.out = new LittleEndianDataOutputStream(this.arrayOut);
        this.lengthWriter = new DeltaBinaryPackingValuesWriter(128, 4, initialSize, pageSize);
    }
    
    @Override
    public void writeBytes(final Binary v) {
        try {
            this.lengthWriter.writeInteger(v.length());
            this.out.write(v.getBytes());
        }
        catch (IOException e) {
            throw new ParquetEncodingException("could not write bytes", e);
        }
    }
    
    @Override
    public long getBufferedSize() {
        return this.lengthWriter.getBufferedSize() + this.arrayOut.size();
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
            DeltaLengthByteArrayValuesWriter.LOG.debug("writing a buffer of size " + this.arrayOut.size());
        }
        return BytesInput.concat(this.lengthWriter.getBytes(), BytesInput.from(this.arrayOut));
    }
    
    @Override
    public Encoding getEncoding() {
        return Encoding.DELTA_LENGTH_BYTE_ARRAY;
    }
    
    @Override
    public void reset() {
        this.lengthWriter.reset();
        this.arrayOut.reset();
    }
    
    @Override
    public long getAllocatedSize() {
        return this.lengthWriter.getAllocatedSize() + this.arrayOut.getCapacity();
    }
    
    @Override
    public String memUsageString(final String prefix) {
        return this.arrayOut.memUsageString(this.lengthWriter.memUsageString(prefix) + " DELTA_LENGTH_BYTE_ARRAY");
    }
    
    static {
        LOG = Log.getLog(DeltaLengthByteArrayValuesWriter.class);
    }
}
