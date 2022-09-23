// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io;

import java.nio.ByteBuffer;
import org.apache.avro.util.Utf8;
import java.io.IOException;
import java.io.Flushable;

public abstract class Encoder implements Flushable
{
    public abstract void writeNull() throws IOException;
    
    public abstract void writeBoolean(final boolean p0) throws IOException;
    
    public abstract void writeInt(final int p0) throws IOException;
    
    public abstract void writeLong(final long p0) throws IOException;
    
    public abstract void writeFloat(final float p0) throws IOException;
    
    public abstract void writeDouble(final double p0) throws IOException;
    
    public abstract void writeString(final Utf8 p0) throws IOException;
    
    public void writeString(final String str) throws IOException {
        this.writeString(new Utf8(str));
    }
    
    public void writeString(final CharSequence charSequence) throws IOException {
        if (charSequence instanceof Utf8) {
            this.writeString((Utf8)charSequence);
        }
        else {
            this.writeString(charSequence.toString());
        }
    }
    
    public abstract void writeBytes(final ByteBuffer p0) throws IOException;
    
    public abstract void writeBytes(final byte[] p0, final int p1, final int p2) throws IOException;
    
    public void writeBytes(final byte[] bytes) throws IOException {
        this.writeBytes(bytes, 0, bytes.length);
    }
    
    public abstract void writeFixed(final byte[] p0, final int p1, final int p2) throws IOException;
    
    public void writeFixed(final byte[] bytes) throws IOException {
        this.writeFixed(bytes, 0, bytes.length);
    }
    
    public void writeFixed(final ByteBuffer bytes) throws IOException {
        final int pos = bytes.position();
        final int len = bytes.limit() - pos;
        if (bytes.hasArray()) {
            this.writeFixed(bytes.array(), bytes.arrayOffset() + pos, len);
        }
        else {
            final byte[] b = new byte[len];
            bytes.duplicate().get(b, 0, len);
            this.writeFixed(b, 0, len);
        }
    }
    
    public abstract void writeEnum(final int p0) throws IOException;
    
    public abstract void writeArrayStart() throws IOException;
    
    public abstract void setItemCount(final long p0) throws IOException;
    
    public abstract void startItem() throws IOException;
    
    public abstract void writeArrayEnd() throws IOException;
    
    public abstract void writeMapStart() throws IOException;
    
    public abstract void writeMapEnd() throws IOException;
    
    public abstract void writeIndex(final int p0) throws IOException;
}
