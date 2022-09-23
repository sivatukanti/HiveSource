// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.util;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.io.InputStream;

public class ByteBufferInputStream extends InputStream
{
    private List<ByteBuffer> buffers;
    private int current;
    
    public ByteBufferInputStream(final List<ByteBuffer> buffers) {
        this.buffers = buffers;
    }
    
    @Override
    public int read() throws IOException {
        return this.getBuffer().get() & 0xFF;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        final ByteBuffer buffer = this.getBuffer();
        final int remaining = buffer.remaining();
        if (len > remaining) {
            buffer.get(b, off, remaining);
            return remaining;
        }
        buffer.get(b, off, len);
        return len;
    }
    
    public ByteBuffer readBuffer(final int length) throws IOException {
        if (length == 0) {
            return ByteBuffer.allocate(0);
        }
        final ByteBuffer buffer = this.getBuffer();
        if (buffer.remaining() == length) {
            ++this.current;
            return buffer;
        }
        final ByteBuffer result = ByteBuffer.allocate(length);
        for (int start = 0; start < length; start += this.read(result.array(), start, length - start)) {}
        return result;
    }
    
    private ByteBuffer getBuffer() throws IOException {
        while (this.current < this.buffers.size()) {
            final ByteBuffer buffer = this.buffers.get(this.current);
            if (buffer.hasRemaining()) {
                return buffer;
            }
            ++this.current;
        }
        throw new EOFException();
    }
}
