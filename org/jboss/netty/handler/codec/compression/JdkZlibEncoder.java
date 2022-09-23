// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.compression;

import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import java.util.zip.CRC32;
import org.jboss.netty.channel.ChannelHandlerContext;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.Deflater;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.handler.codec.oneone.OneToOneStrictEncoder;

public class JdkZlibEncoder extends OneToOneStrictEncoder implements LifeCycleAwareChannelHandler
{
    private final ZlibWrapper wrapper;
    private final Deflater deflater;
    private final AtomicBoolean finished;
    private volatile ChannelHandlerContext ctx;
    private byte[] out;
    private final CRC32 crc;
    private static final byte[] gzipHeader;
    private boolean writeHeader;
    
    public JdkZlibEncoder() {
        this(6);
    }
    
    public JdkZlibEncoder(final int compressionLevel) {
        this(ZlibWrapper.ZLIB, compressionLevel);
    }
    
    public JdkZlibEncoder(final ZlibWrapper wrapper) {
        this(wrapper, 6);
    }
    
    public JdkZlibEncoder(final ZlibWrapper wrapper, final int compressionLevel) {
        this.finished = new AtomicBoolean();
        this.writeHeader = true;
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        }
        if (wrapper == null) {
            throw new NullPointerException("wrapper");
        }
        if (wrapper == ZlibWrapper.ZLIB_OR_NONE) {
            throw new IllegalArgumentException("wrapper '" + ZlibWrapper.ZLIB_OR_NONE + "' is not " + "allowed for compression.");
        }
        this.wrapper = wrapper;
        this.deflater = new Deflater(compressionLevel, wrapper != ZlibWrapper.ZLIB);
        if (wrapper == ZlibWrapper.GZIP) {
            this.crc = new CRC32();
        }
        else {
            this.crc = null;
        }
    }
    
    public JdkZlibEncoder(final byte[] dictionary) {
        this(6, dictionary);
    }
    
    public JdkZlibEncoder(final int compressionLevel, final byte[] dictionary) {
        this.finished = new AtomicBoolean();
        this.writeHeader = true;
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        }
        if (dictionary == null) {
            throw new NullPointerException("dictionary");
        }
        this.wrapper = ZlibWrapper.ZLIB;
        this.crc = null;
        (this.deflater = new Deflater(compressionLevel)).setDictionary(dictionary);
    }
    
    public ChannelFuture close() {
        final ChannelHandlerContext ctx = this.ctx;
        if (ctx == null) {
            throw new IllegalStateException("not added to a pipeline");
        }
        return this.finishEncode(ctx, null);
    }
    
    private boolean isGzip() {
        return this.wrapper == ZlibWrapper.GZIP;
    }
    
    public boolean isClosed() {
        return this.finished.get();
    }
    
    @Override
    protected Object encode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        if (!(msg instanceof ChannelBuffer) || this.finished.get()) {
            return msg;
        }
        final ChannelBuffer uncompressed = (ChannelBuffer)msg;
        final int uncompressedLen = uncompressed.readableBytes();
        if (uncompressedLen == 0) {
            return uncompressed;
        }
        final byte[] in = new byte[uncompressedLen];
        uncompressed.readBytes(in);
        final int sizeEstimate = this.estimateCompressedSize(uncompressedLen);
        final ChannelBuffer compressed = ChannelBuffers.dynamicBuffer(sizeEstimate, channel.getConfig().getBufferFactory());
        synchronized (this.deflater) {
            if (this.isGzip()) {
                this.crc.update(in);
                if (this.writeHeader) {
                    compressed.writeBytes(JdkZlibEncoder.gzipHeader);
                    this.writeHeader = false;
                }
            }
            this.deflater.setInput(in);
            while (!this.deflater.needsInput()) {
                this.deflate(compressed);
            }
        }
        return compressed;
    }
    
    private int estimateCompressedSize(final int originalSize) {
        int sizeEstimate = (int)Math.ceil(originalSize * 1.001) + 12;
        if (this.writeHeader) {
            switch (this.wrapper) {
                case GZIP: {
                    sizeEstimate += JdkZlibEncoder.gzipHeader.length;
                    break;
                }
                case ZLIB: {
                    sizeEstimate += 2;
                    break;
                }
            }
        }
        return sizeEstimate;
    }
    
    @Override
    public void handleDownstream(final ChannelHandlerContext ctx, final ChannelEvent evt) throws Exception {
        if (evt instanceof ChannelStateEvent) {
            final ChannelStateEvent e = (ChannelStateEvent)evt;
            switch (e.getState()) {
                case OPEN:
                case CONNECTED:
                case BOUND: {
                    if (Boolean.FALSE.equals(e.getValue()) || e.getValue() == null) {
                        this.finishEncode(ctx, evt);
                        return;
                    }
                    break;
                }
            }
        }
        super.handleDownstream(ctx, evt);
    }
    
    private ChannelFuture finishEncode(final ChannelHandlerContext ctx, final ChannelEvent evt) {
        ChannelFuture future = Channels.succeededFuture(ctx.getChannel());
        if (!this.finished.compareAndSet(false, true)) {
            if (evt != null) {
                ctx.sendDownstream(evt);
            }
            return future;
        }
        final ChannelBuffer footer = ChannelBuffers.dynamicBuffer(ctx.getChannel().getConfig().getBufferFactory());
        final boolean gzip = this.isGzip();
        synchronized (this.deflater) {
            if (gzip && this.writeHeader) {
                this.writeHeader = false;
                footer.writeBytes(JdkZlibEncoder.gzipHeader);
            }
            this.deflater.finish();
            while (!this.deflater.finished()) {
                this.deflate(footer);
            }
            if (gzip) {
                final int crcValue = (int)this.crc.getValue();
                final int uncBytes = this.deflater.getTotalIn();
                footer.writeByte(crcValue);
                footer.writeByte(crcValue >>> 8);
                footer.writeByte(crcValue >>> 16);
                footer.writeByte(crcValue >>> 24);
                footer.writeByte(uncBytes);
                footer.writeByte(uncBytes >>> 8);
                footer.writeByte(uncBytes >>> 16);
                footer.writeByte(uncBytes >>> 24);
            }
            this.deflater.end();
        }
        if (footer.readable()) {
            future = Channels.future(ctx.getChannel());
            Channels.write(ctx, future, footer);
        }
        if (evt != null) {
            future.addListener(new ChannelFutureListener() {
                public void operationComplete(final ChannelFuture future) throws Exception {
                    ctx.sendDownstream(evt);
                }
            });
        }
        return future;
    }
    
    private void deflate(final ChannelBuffer out) {
        if (out.hasArray()) {
            int numBytes;
            do {
                final int writerIndex = out.writerIndex();
                numBytes = this.deflater.deflate(out.array(), out.arrayOffset() + writerIndex, out.writableBytes(), 2);
                out.writerIndex(writerIndex + numBytes);
            } while (numBytes > 0);
        }
        else {
            byte[] tmpOut = this.out;
            if (tmpOut == null) {
                final byte[] out2 = new byte[8192];
                this.out = out2;
                tmpOut = out2;
            }
            int numBytes;
            do {
                numBytes = this.deflater.deflate(tmpOut, 0, tmpOut.length, 2);
                out.writeBytes(tmpOut, 0, numBytes);
            } while (numBytes > 0);
        }
    }
    
    public void beforeAdd(final ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }
    
    public void afterAdd(final ChannelHandlerContext ctx) throws Exception {
    }
    
    public void beforeRemove(final ChannelHandlerContext ctx) throws Exception {
    }
    
    public void afterRemove(final ChannelHandlerContext ctx) throws Exception {
    }
    
    static {
        gzipHeader = new byte[] { 31, -117, 8, 0, 0, 0, 0, 0, 0, 0 };
    }
}
