// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.ChannelBuffer;
import java.util.zip.Deflater;

class SpdyHeaderBlockZlibEncoder extends SpdyHeaderBlockRawEncoder
{
    private final Deflater compressor;
    private boolean finished;
    
    SpdyHeaderBlockZlibEncoder(final SpdyVersion spdyVersion, final int compressionLevel) {
        super(spdyVersion);
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        }
        (this.compressor = new Deflater(compressionLevel)).setDictionary(SpdyCodecUtil.SPDY_DICT);
    }
    
    private int setInput(final ChannelBuffer decompressed) {
        final int len = decompressed.readableBytes();
        if (decompressed.hasArray()) {
            this.compressor.setInput(decompressed.array(), decompressed.arrayOffset() + decompressed.readerIndex(), len);
        }
        else {
            final byte[] in = new byte[len];
            decompressed.getBytes(decompressed.readerIndex(), in);
            this.compressor.setInput(in, 0, in.length);
        }
        return len;
    }
    
    private void encode(final ChannelBuffer compressed) {
        while (this.compressInto(compressed)) {
            compressed.ensureWritableBytes(compressed.capacity() << 1);
        }
    }
    
    private boolean compressInto(final ChannelBuffer compressed) {
        final byte[] out = compressed.array();
        final int off = compressed.arrayOffset() + compressed.writerIndex();
        final int toWrite = compressed.writableBytes();
        final int numBytes = this.compressor.deflate(out, off, toWrite, 2);
        compressed.writerIndex(compressed.writerIndex() + numBytes);
        return numBytes == toWrite;
    }
    
    @Override
    public synchronized ChannelBuffer encode(final SpdyHeadersFrame frame) throws Exception {
        if (frame == null) {
            throw new IllegalArgumentException("frame");
        }
        if (this.finished) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        final ChannelBuffer decompressed = super.encode(frame);
        if (decompressed.readableBytes() == 0) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        final ChannelBuffer compressed = ChannelBuffers.dynamicBuffer(decompressed.readableBytes());
        final int len = this.setInput(decompressed);
        this.encode(compressed);
        decompressed.skipBytes(len);
        return compressed;
    }
    
    public synchronized void end() {
        if (this.finished) {
            return;
        }
        this.finished = true;
        this.compressor.end();
        super.end();
    }
}
