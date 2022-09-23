// 
// Decompiled by Procyon v0.5.36
// 

package parquet.bytes;

import java.io.IOException;
import java.io.OutputStream;

public class LittleEndianDataOutputStream extends OutputStream
{
    private final OutputStream out;
    private byte[] writeBuffer;
    
    public LittleEndianDataOutputStream(final OutputStream out) {
        this.writeBuffer = new byte[8];
        this.out = out;
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.out.write(b);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.out.write(b, off, len);
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    public final void writeBoolean(final boolean v) throws IOException {
        this.out.write(v ? 1 : 0);
    }
    
    public final void writeByte(final int v) throws IOException {
        this.out.write(v);
    }
    
    public final void writeShort(final int v) throws IOException {
        this.out.write(v >>> 0 & 0xFF);
        this.out.write(v >>> 8 & 0xFF);
    }
    
    public final void writeInt(final int v) throws IOException {
        this.out.write(v >>> 0 & 0xFF);
        this.out.write(v >>> 8 & 0xFF);
        this.out.write(v >>> 16 & 0xFF);
        this.out.write(v >>> 24 & 0xFF);
    }
    
    public final void writeLong(final long v) throws IOException {
        this.writeBuffer[7] = (byte)(v >>> 56);
        this.writeBuffer[6] = (byte)(v >>> 48);
        this.writeBuffer[5] = (byte)(v >>> 40);
        this.writeBuffer[4] = (byte)(v >>> 32);
        this.writeBuffer[3] = (byte)(v >>> 24);
        this.writeBuffer[2] = (byte)(v >>> 16);
        this.writeBuffer[1] = (byte)(v >>> 8);
        this.writeBuffer[0] = (byte)(v >>> 0);
        this.out.write(this.writeBuffer, 0, 8);
    }
    
    public final void writeFloat(final float v) throws IOException {
        this.writeInt(Float.floatToIntBits(v));
    }
    
    public final void writeDouble(final double v) throws IOException {
        this.writeLong(Double.doubleToLongBits(v));
    }
}
