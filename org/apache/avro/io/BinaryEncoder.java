// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io;

import java.nio.ByteBuffer;
import org.apache.avro.util.Utf8;
import java.io.IOException;

public abstract class BinaryEncoder extends Encoder
{
    @Override
    public void writeNull() throws IOException {
    }
    
    @Override
    public void writeString(final Utf8 utf8) throws IOException {
        this.writeBytes(utf8.getBytes(), 0, utf8.getByteLength());
    }
    
    @Override
    public void writeString(final String string) throws IOException {
        if (0 == string.length()) {
            this.writeZero();
            return;
        }
        final byte[] bytes = string.getBytes("UTF-8");
        this.writeInt(bytes.length);
        this.writeFixed(bytes, 0, bytes.length);
    }
    
    @Override
    public void writeBytes(final ByteBuffer bytes) throws IOException {
        final int len = bytes.limit() - bytes.position();
        if (0 == len) {
            this.writeZero();
        }
        else {
            this.writeInt(len);
            this.writeFixed(bytes);
        }
    }
    
    @Override
    public void writeBytes(final byte[] bytes, final int start, final int len) throws IOException {
        if (0 == len) {
            this.writeZero();
            return;
        }
        this.writeInt(len);
        this.writeFixed(bytes, start, len);
    }
    
    @Override
    public void writeEnum(final int e) throws IOException {
        this.writeInt(e);
    }
    
    @Override
    public void writeArrayStart() throws IOException {
    }
    
    @Override
    public void setItemCount(final long itemCount) throws IOException {
        if (itemCount > 0L) {
            this.writeLong(itemCount);
        }
    }
    
    @Override
    public void startItem() throws IOException {
    }
    
    @Override
    public void writeArrayEnd() throws IOException {
        this.writeZero();
    }
    
    @Override
    public void writeMapStart() throws IOException {
    }
    
    @Override
    public void writeMapEnd() throws IOException {
        this.writeZero();
    }
    
    @Override
    public void writeIndex(final int unionIndex) throws IOException {
        this.writeInt(unionIndex);
    }
    
    protected abstract void writeZero() throws IOException;
    
    public abstract int bytesBuffered();
}
