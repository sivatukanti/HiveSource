// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.serialization;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.io.DataInputStream;
import java.io.ObjectInput;
import java.io.InputStream;

public class ObjectDecoderInputStream extends InputStream implements ObjectInput
{
    private final DataInputStream in;
    private final int maxObjectSize;
    private final ClassResolver classResolver;
    
    public ObjectDecoderInputStream(final InputStream in) {
        this(in, null);
    }
    
    public ObjectDecoderInputStream(final InputStream in, final ClassLoader classLoader) {
        this(in, classLoader, 1048576);
    }
    
    public ObjectDecoderInputStream(final InputStream in, final int maxObjectSize) {
        this(in, null, maxObjectSize);
    }
    
    public ObjectDecoderInputStream(final InputStream in, final ClassLoader classLoader, final int maxObjectSize) {
        if (in == null) {
            throw new NullPointerException("in");
        }
        if (maxObjectSize <= 0) {
            throw new IllegalArgumentException("maxObjectSize: " + maxObjectSize);
        }
        if (in instanceof DataInputStream) {
            this.in = (DataInputStream)in;
        }
        else {
            this.in = new DataInputStream(in);
        }
        this.classResolver = ClassResolvers.weakCachingResolver(classLoader);
        this.maxObjectSize = maxObjectSize;
    }
    
    public Object readObject() throws ClassNotFoundException, IOException {
        final int dataLen = this.readInt();
        if (dataLen <= 0) {
            throw new StreamCorruptedException("invalid data length: " + dataLen);
        }
        if (dataLen > this.maxObjectSize) {
            throw new StreamCorruptedException("data length too big: " + dataLen + " (max: " + this.maxObjectSize + ')');
        }
        return new CompactObjectInputStream(this.in, this.classResolver).readObject();
    }
    
    @Override
    public int available() throws IOException {
        return this.in.available();
    }
    
    @Override
    public void close() throws IOException {
        this.in.close();
    }
    
    @Override
    public void mark(final int readlimit) {
        this.in.mark(readlimit);
    }
    
    @Override
    public boolean markSupported() {
        return this.in.markSupported();
    }
    
    @Override
    public int read() throws IOException {
        return this.in.read();
    }
    
    @Override
    public final int read(final byte[] b, final int off, final int len) throws IOException {
        return this.in.read(b, off, len);
    }
    
    @Override
    public final int read(final byte[] b) throws IOException {
        return this.in.read(b);
    }
    
    public final boolean readBoolean() throws IOException {
        return this.in.readBoolean();
    }
    
    public final byte readByte() throws IOException {
        return this.in.readByte();
    }
    
    public final char readChar() throws IOException {
        return this.in.readChar();
    }
    
    public final double readDouble() throws IOException {
        return this.in.readDouble();
    }
    
    public final float readFloat() throws IOException {
        return this.in.readFloat();
    }
    
    public final void readFully(final byte[] b, final int off, final int len) throws IOException {
        this.in.readFully(b, off, len);
    }
    
    public final void readFully(final byte[] b) throws IOException {
        this.in.readFully(b);
    }
    
    public final int readInt() throws IOException {
        return this.in.readInt();
    }
    
    @Deprecated
    public final String readLine() throws IOException {
        return this.in.readLine();
    }
    
    public final long readLong() throws IOException {
        return this.in.readLong();
    }
    
    public final short readShort() throws IOException {
        return this.in.readShort();
    }
    
    public final int readUnsignedByte() throws IOException {
        return this.in.readUnsignedByte();
    }
    
    public final int readUnsignedShort() throws IOException {
        return this.in.readUnsignedShort();
    }
    
    public final String readUTF() throws IOException {
        return this.in.readUTF();
    }
    
    @Override
    public void reset() throws IOException {
        this.in.reset();
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return this.in.skip(n);
    }
    
    public final int skipBytes(final int n) throws IOException {
        return this.in.skipBytes(n);
    }
}
