// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.stream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class ChunkedNioStream implements ChunkedInput
{
    private final ReadableByteChannel in;
    private final int chunkSize;
    private long offset;
    private final ByteBuffer byteBuffer;
    
    public ChunkedNioStream(final ReadableByteChannel in) {
        this(in, 8192);
    }
    
    public ChunkedNioStream(final ReadableByteChannel in, final int chunkSize) {
        if (in == null) {
            throw new NullPointerException("in");
        }
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize: " + chunkSize + " (expected: a positive integer)");
        }
        this.in = in;
        this.offset = 0L;
        this.chunkSize = chunkSize;
        this.byteBuffer = ByteBuffer.allocate(chunkSize);
    }
    
    public long getTransferredBytes() {
        return this.offset;
    }
    
    public boolean hasNextChunk() throws Exception {
        if (this.byteBuffer.position() > 0) {
            return true;
        }
        if (!this.in.isOpen()) {
            return false;
        }
        final int b = this.in.read(this.byteBuffer);
        if (b < 0) {
            return false;
        }
        this.offset += b;
        return true;
    }
    
    public boolean isEndOfInput() throws Exception {
        return !this.hasNextChunk();
    }
    
    public void close() throws Exception {
        this.in.close();
    }
    
    public Object nextChunk() throws Exception {
        if (!this.hasNextChunk()) {
            return null;
        }
        int readBytes = this.byteBuffer.position();
        do {
            final int localReadBytes = this.in.read(this.byteBuffer);
            if (localReadBytes < 0) {
                break;
            }
            readBytes += localReadBytes;
            this.offset += localReadBytes;
        } while (readBytes != this.chunkSize);
        this.byteBuffer.flip();
        final ChannelBuffer buffer = ChannelBuffers.copiedBuffer(this.byteBuffer);
        this.byteBuffer.clear();
        return buffer;
    }
}
