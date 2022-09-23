// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.buffer.ChannelBuffers;
import java.util.zip.DataFormatException;
import org.jboss.netty.buffer.ChannelBuffer;
import java.util.zip.Inflater;

final class SpdyHeaderBlockZlibDecoder extends SpdyHeaderBlockRawDecoder
{
    private static final int DEFAULT_BUFFER_CAPACITY = 4096;
    private static final SpdyProtocolException INVALID_HEADER_BLOCK;
    private final Inflater decompressor;
    private ChannelBuffer decompressed;
    
    SpdyHeaderBlockZlibDecoder(final SpdyVersion spdyVersion, final int maxHeaderSize) {
        super(spdyVersion, maxHeaderSize);
        this.decompressor = new Inflater();
    }
    
    @Override
    void decode(final ChannelBuffer headerBlock, final SpdyHeadersFrame frame) throws Exception {
        if (headerBlock == null) {
            throw new NullPointerException("headerBlock");
        }
        if (frame == null) {
            throw new NullPointerException("frame");
        }
        final int len = this.setInput(headerBlock);
        int numBytes;
        do {
            numBytes = this.decompress(frame);
        } while (numBytes > 0);
        if (this.decompressor.getRemaining() != 0) {
            throw SpdyHeaderBlockZlibDecoder.INVALID_HEADER_BLOCK;
        }
        headerBlock.skipBytes(len);
    }
    
    private int setInput(final ChannelBuffer compressed) {
        final int len = compressed.readableBytes();
        if (compressed.hasArray()) {
            this.decompressor.setInput(compressed.array(), compressed.arrayOffset() + compressed.readerIndex(), len);
        }
        else {
            final byte[] in = new byte[len];
            compressed.getBytes(compressed.readerIndex(), in);
            this.decompressor.setInput(in, 0, in.length);
        }
        return len;
    }
    
    private int decompress(final SpdyHeadersFrame frame) throws Exception {
        this.ensureBuffer();
        final byte[] out = this.decompressed.array();
        final int off = this.decompressed.arrayOffset() + this.decompressed.writerIndex();
        try {
            int numBytes = this.decompressor.inflate(out, off, this.decompressed.writableBytes());
            if (numBytes == 0 && this.decompressor.needsDictionary()) {
                try {
                    this.decompressor.setDictionary(SpdyCodecUtil.SPDY_DICT);
                }
                catch (IllegalArgumentException e) {
                    throw SpdyHeaderBlockZlibDecoder.INVALID_HEADER_BLOCK;
                }
                numBytes = this.decompressor.inflate(out, off, this.decompressed.writableBytes());
            }
            this.decompressed.writerIndex(this.decompressed.writerIndex() + numBytes);
            super.decodeHeaderBlock(this.decompressed, frame);
            this.decompressed.discardReadBytes();
            return numBytes;
        }
        catch (DataFormatException e2) {
            throw SpdyHeaderBlockZlibDecoder.INVALID_HEADER_BLOCK;
        }
    }
    
    private void ensureBuffer() {
        if (this.decompressed == null) {
            this.decompressed = ChannelBuffers.dynamicBuffer(4096);
        }
        this.decompressed.ensureWritableBytes(1);
    }
    
    @Override
    void endHeaderBlock(final SpdyHeadersFrame frame) throws Exception {
        super.endHeaderBlock(frame);
        this.decompressed = null;
    }
    
    public void end() {
        super.end();
        this.decompressed = null;
        this.decompressor.end();
    }
    
    static {
        INVALID_HEADER_BLOCK = new SpdyProtocolException("Invalid Header Block");
    }
}
