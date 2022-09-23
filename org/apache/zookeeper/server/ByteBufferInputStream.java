// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.apache.jute.InputArchive;
import org.apache.jute.BinaryInputArchive;
import org.apache.jute.Record;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.InputStream;

public class ByteBufferInputStream extends InputStream
{
    ByteBuffer bb;
    
    public ByteBufferInputStream(final ByteBuffer bb) {
        this.bb = bb;
    }
    
    @Override
    public int read() throws IOException {
        if (this.bb.remaining() == 0) {
            return -1;
        }
        return this.bb.get() & 0xFF;
    }
    
    @Override
    public int available() throws IOException {
        return this.bb.remaining();
    }
    
    @Override
    public int read(final byte[] b, final int off, int len) throws IOException {
        if (this.bb.remaining() == 0) {
            return -1;
        }
        if (len > this.bb.remaining()) {
            len = this.bb.remaining();
        }
        this.bb.get(b, off, len);
        return len;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public long skip(long n) throws IOException {
        final long newPos = this.bb.position() + n;
        if (newPos > this.bb.remaining()) {
            n = this.bb.remaining();
        }
        this.bb.position(this.bb.position() + (int)n);
        return n;
    }
    
    public static void byteBuffer2Record(final ByteBuffer bb, final Record record) throws IOException {
        final BinaryInputArchive ia = BinaryInputArchive.getArchive(new ByteBufferInputStream(bb));
        record.deserialize(ia, "request");
    }
}
