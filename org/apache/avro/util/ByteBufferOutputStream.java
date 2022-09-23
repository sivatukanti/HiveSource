// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.util;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.List;
import java.io.OutputStream;

public class ByteBufferOutputStream extends OutputStream
{
    public static final int BUFFER_SIZE = 8192;
    private List<ByteBuffer> buffers;
    
    public ByteBufferOutputStream() {
        this.reset();
    }
    
    public List<ByteBuffer> getBufferList() {
        final List<ByteBuffer> result = this.buffers;
        this.reset();
        for (final ByteBuffer buffer : result) {
            buffer.flip();
        }
        return result;
    }
    
    public void prepend(final List<ByteBuffer> lists) {
        for (final ByteBuffer buffer : lists) {
            buffer.position(buffer.limit());
        }
        this.buffers.addAll(0, lists);
    }
    
    public void append(final List<ByteBuffer> lists) {
        for (final ByteBuffer buffer : lists) {
            buffer.position(buffer.limit());
        }
        this.buffers.addAll(lists);
    }
    
    public void reset() {
        (this.buffers = new LinkedList<ByteBuffer>()).add(ByteBuffer.allocate(8192));
    }
    
    public void write(final ByteBuffer buffer) {
        this.buffers.add(buffer);
    }
    
    @Override
    public void write(final int b) {
        ByteBuffer buffer = this.buffers.get(this.buffers.size() - 1);
        if (buffer.remaining() < 1) {
            buffer = ByteBuffer.allocate(8192);
            this.buffers.add(buffer);
        }
        buffer.put((byte)b);
    }
    
    @Override
    public void write(final byte[] b, int off, int len) {
        ByteBuffer buffer = this.buffers.get(this.buffers.size() - 1);
        for (int remaining = buffer.remaining(); len > remaining; remaining = buffer.remaining()) {
            buffer.put(b, off, remaining);
            len -= remaining;
            off += remaining;
            buffer = ByteBuffer.allocate(8192);
            this.buffers.add(buffer);
        }
        buffer.put(b, off, len);
    }
    
    public void writeBuffer(final ByteBuffer buffer) throws IOException {
        if (buffer.remaining() < 8192) {
            this.write(buffer.array(), buffer.position(), buffer.remaining());
        }
        else {
            final ByteBuffer dup = buffer.duplicate();
            dup.position(buffer.limit());
            this.buffers.add(dup);
        }
    }
}
