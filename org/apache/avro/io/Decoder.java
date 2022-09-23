// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io;

import java.nio.ByteBuffer;
import org.apache.avro.util.Utf8;
import java.io.IOException;

public abstract class Decoder
{
    public abstract void readNull() throws IOException;
    
    public abstract boolean readBoolean() throws IOException;
    
    public abstract int readInt() throws IOException;
    
    public abstract long readLong() throws IOException;
    
    public abstract float readFloat() throws IOException;
    
    public abstract double readDouble() throws IOException;
    
    public abstract Utf8 readString(final Utf8 p0) throws IOException;
    
    public abstract String readString() throws IOException;
    
    public abstract void skipString() throws IOException;
    
    public abstract ByteBuffer readBytes(final ByteBuffer p0) throws IOException;
    
    public abstract void skipBytes() throws IOException;
    
    public abstract void readFixed(final byte[] p0, final int p1, final int p2) throws IOException;
    
    public void readFixed(final byte[] bytes) throws IOException {
        this.readFixed(bytes, 0, bytes.length);
    }
    
    public abstract void skipFixed(final int p0) throws IOException;
    
    public abstract int readEnum() throws IOException;
    
    public abstract long readArrayStart() throws IOException;
    
    public abstract long arrayNext() throws IOException;
    
    public abstract long skipArray() throws IOException;
    
    public abstract long readMapStart() throws IOException;
    
    public abstract long mapNext() throws IOException;
    
    public abstract long skipMap() throws IOException;
    
    public abstract int readIndex() throws IOException;
}
