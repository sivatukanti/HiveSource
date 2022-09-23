// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.buffer;

import java.io.IOException;
import java.io.DataOutputStream;
import java.io.DataOutput;
import java.io.OutputStream;

public class ChannelBufferOutputStream extends OutputStream implements DataOutput
{
    private final ChannelBuffer buffer;
    private final int startIndex;
    private final DataOutputStream utf8out;
    
    public ChannelBufferOutputStream(final ChannelBuffer buffer) {
        this.utf8out = new DataOutputStream(this);
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        this.buffer = buffer;
        this.startIndex = buffer.writerIndex();
    }
    
    public int writtenBytes() {
        return this.buffer.writerIndex() - this.startIndex;
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (len == 0) {
            return;
        }
        this.buffer.writeBytes(b, off, len);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.buffer.writeBytes(b);
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.buffer.writeByte((byte)b);
    }
    
    public void writeBoolean(final boolean v) throws IOException {
        this.write(v ? 1 : 0);
    }
    
    public void writeByte(final int v) throws IOException {
        this.write(v);
    }
    
    public void writeBytes(final String s) throws IOException {
        for (int len = s.length(), i = 0; i < len; ++i) {
            this.write((byte)s.charAt(i));
        }
    }
    
    public void writeChar(final int v) throws IOException {
        this.writeShort((short)v);
    }
    
    public void writeChars(final String s) throws IOException {
        for (int len = s.length(), i = 0; i < len; ++i) {
            this.writeChar(s.charAt(i));
        }
    }
    
    public void writeDouble(final double v) throws IOException {
        this.writeLong(Double.doubleToLongBits(v));
    }
    
    public void writeFloat(final float v) throws IOException {
        this.writeInt(Float.floatToIntBits(v));
    }
    
    public void writeInt(final int v) throws IOException {
        this.buffer.writeInt(v);
    }
    
    public void writeLong(final long v) throws IOException {
        this.buffer.writeLong(v);
    }
    
    public void writeShort(final int v) throws IOException {
        this.buffer.writeShort((short)v);
    }
    
    public void writeUTF(final String s) throws IOException {
        this.utf8out.writeUTF(s);
    }
    
    public ChannelBuffer buffer() {
        return this.buffer;
    }
}
