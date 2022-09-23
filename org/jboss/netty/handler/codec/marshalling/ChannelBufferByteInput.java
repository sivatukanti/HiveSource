// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.marshalling;

import java.io.IOException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.marshalling.ByteInput;

class ChannelBufferByteInput implements ByteInput
{
    private final ChannelBuffer buffer;
    
    public ChannelBufferByteInput(final ChannelBuffer buffer) {
        this.buffer = buffer;
    }
    
    public void close() throws IOException {
    }
    
    public int available() throws IOException {
        return this.buffer.readableBytes();
    }
    
    public int read() throws IOException {
        if (this.buffer.readable()) {
            return this.buffer.readByte() & 0xFF;
        }
        return -1;
    }
    
    public int read(final byte[] array) throws IOException {
        return this.read(array, 0, array.length);
    }
    
    public int read(final byte[] dst, final int dstIndex, int length) throws IOException {
        final int available = this.available();
        if (available == 0) {
            return -1;
        }
        length = Math.min(available, length);
        this.buffer.readBytes(dst, dstIndex, length);
        return length;
    }
    
    public long skip(long bytes) throws IOException {
        final int readable = this.buffer.readableBytes();
        if (readable < bytes) {
            bytes = readable;
        }
        this.buffer.readerIndex((int)(this.buffer.readerIndex() + bytes));
        return bytes;
    }
}
