// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.stream;

import org.jboss.netty.buffer.ChannelBuffers;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.nio.channels.FileChannel;

public class ChunkedNioFile implements ChunkedInput
{
    private final FileChannel in;
    private final long startOffset;
    private final long endOffset;
    private final int chunkSize;
    private long offset;
    
    public ChunkedNioFile(final File in) throws IOException {
        this(new FileInputStream(in).getChannel());
    }
    
    public ChunkedNioFile(final File in, final int chunkSize) throws IOException {
        this(new FileInputStream(in).getChannel(), chunkSize);
    }
    
    public ChunkedNioFile(final FileChannel in) throws IOException {
        this(in, 8192);
    }
    
    public ChunkedNioFile(final FileChannel in, final int chunkSize) throws IOException {
        this(in, 0L, in.size(), chunkSize);
    }
    
    public ChunkedNioFile(final FileChannel in, final long offset, final long length, final int chunkSize) throws IOException {
        if (in == null) {
            throw new NullPointerException("in");
        }
        if (offset < 0L) {
            throw new IllegalArgumentException("offset: " + offset + " (expected: 0 or greater)");
        }
        if (length < 0L) {
            throw new IllegalArgumentException("length: " + length + " (expected: 0 or greater)");
        }
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize: " + chunkSize + " (expected: a positive integer)");
        }
        if (offset != 0L) {
            in.position(offset);
        }
        this.in = in;
        this.chunkSize = chunkSize;
        this.startOffset = offset;
        this.offset = offset;
        this.endOffset = offset + length;
    }
    
    public long getStartOffset() {
        return this.startOffset;
    }
    
    public long getEndOffset() {
        return this.endOffset;
    }
    
    public long getCurrentOffset() {
        return this.offset;
    }
    
    public boolean hasNextChunk() throws Exception {
        return this.offset < this.endOffset && this.in.isOpen();
    }
    
    public boolean isEndOfInput() throws Exception {
        return !this.hasNextChunk();
    }
    
    public void close() throws Exception {
        this.in.close();
    }
    
    public Object nextChunk() throws Exception {
        final long offset = this.offset;
        if (offset >= this.endOffset) {
            return null;
        }
        final int chunkSize = (int)Math.min(this.chunkSize, this.endOffset - offset);
        final byte[] chunkArray = new byte[chunkSize];
        final ByteBuffer chunk = ByteBuffer.wrap(chunkArray);
        int readBytes = 0;
        do {
            final int localReadBytes = this.in.read(chunk);
            if (localReadBytes < 0) {
                break;
            }
            readBytes += localReadBytes;
        } while (readBytes != chunkSize);
        this.offset += readBytes;
        return ChannelBuffers.wrappedBuffer(chunkArray);
    }
}
