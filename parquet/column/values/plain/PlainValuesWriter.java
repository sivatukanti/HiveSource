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
import java.nio.charset.Charset;
import parquet.Log;
import parquet.column.values.ValuesWriter;

public class PlainValuesWriter extends ValuesWriter
{
    private static final Log LOG;
    public static final Charset CHARSET;
    private CapacityByteArrayOutputStream arrayOut;
    private LittleEndianDataOutputStream out;
    
    public PlainValuesWriter(final int initialSize, final int pageSize) {
        this.arrayOut = new CapacityByteArrayOutputStream(initialSize, pageSize);
        this.out = new LittleEndianDataOutputStream(this.arrayOut);
    }
    
    @Override
    public final void writeBytes(final Binary v) {
        try {
            this.out.writeInt(v.length());
            v.writeTo(this.out);
        }
        catch (IOException e) {
            throw new ParquetEncodingException("could not write bytes", e);
        }
    }
    
    @Override
    public final void writeInteger(final int v) {
        try {
            this.out.writeInt(v);
        }
        catch (IOException e) {
            throw new ParquetEncodingException("could not write int", e);
        }
    }
    
    @Override
    public final void writeLong(final long v) {
        try {
            this.out.writeLong(v);
        }
        catch (IOException e) {
            throw new ParquetEncodingException("could not write long", e);
        }
    }
    
    @Override
    public final void writeFloat(final float v) {
        try {
            this.out.writeFloat(v);
        }
        catch (IOException e) {
            throw new ParquetEncodingException("could not write float", e);
        }
    }
    
    @Override
    public final void writeDouble(final double v) {
        try {
            this.out.writeDouble(v);
        }
        catch (IOException e) {
            throw new ParquetEncodingException("could not write double", e);
        }
    }
    
    @Override
    public void writeByte(final int value) {
        try {
            this.out.write(value);
        }
        catch (IOException e) {
            throw new ParquetEncodingException("could not write byte", e);
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
            PlainValuesWriter.LOG.debug("writing a buffer of size " + this.arrayOut.size());
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
        CHARSET = Charset.forName("UTF-8");
    }
}
