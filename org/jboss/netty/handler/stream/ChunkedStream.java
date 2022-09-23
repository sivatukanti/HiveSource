// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.stream;

import org.jboss.netty.buffer.ChannelBuffers;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class ChunkedStream implements ChunkedInput
{
    static final int DEFAULT_CHUNK_SIZE = 8192;
    private final PushbackInputStream in;
    private final int chunkSize;
    private long offset;
    
    public ChunkedStream(final InputStream in) {
        this(in, 8192);
    }
    
    public ChunkedStream(final InputStream in, final int chunkSize) {
        if (in == null) {
            throw new NullPointerException("in");
        }
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize: " + chunkSize + " (expected: a positive integer)");
        }
        if (in instanceof PushbackInputStream) {
            this.in = (PushbackInputStream)in;
        }
        else {
            this.in = new PushbackInputStream(in);
        }
        this.chunkSize = chunkSize;
    }
    
    public long getTransferredBytes() {
        return this.offset;
    }
    
    public boolean hasNextChunk() throws Exception {
        final int b = this.in.read();
        if (b < 0) {
            return false;
        }
        this.in.unread(b);
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
        final int availableBytes = this.in.available();
        int chunkSize;
        if (availableBytes <= 0) {
            chunkSize = this.chunkSize;
        }
        else {
            chunkSize = Math.min(this.chunkSize, this.in.available());
        }
        final byte[] chunk = new byte[chunkSize];
        int readBytes = 0;
        do {
            final int localReadBytes = this.in.read(chunk, readBytes, chunkSize - readBytes);
            if (localReadBytes < 0) {
                break;
            }
            readBytes += localReadBytes;
            this.offset += localReadBytes;
        } while (readBytes != chunkSize);
        return ChannelBuffers.wrappedBuffer(chunk, 0, readBytes);
    }
}
