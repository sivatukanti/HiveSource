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
import org.jboss.netty.util.internal.jzlib.JZlib;
import org.jboss.netty.channel.ChannelHandlerContext;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jboss.netty.util.internal.jzlib.ZStream;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.handler.codec.oneone.OneToOneStrictEncoder;

public class ZlibEncoder extends OneToOneStrictEncoder implements LifeCycleAwareChannelHandler
{
    private static final byte[] EMPTY_ARRAY;
    private final int wrapperOverhead;
    private final ZStream z;
    private final AtomicBoolean finished;
    private volatile ChannelHandlerContext ctx;
    
    public ZlibEncoder() {
        this(6);
    }
    
    public ZlibEncoder(final int compressionLevel) {
        this(ZlibWrapper.ZLIB, compressionLevel);
    }
    
    public ZlibEncoder(final ZlibWrapper wrapper) {
        this(wrapper, 6);
    }
    
    public ZlibEncoder(final ZlibWrapper wrapper, final int compressionLevel) {
        this(wrapper, compressionLevel, 15, 8);
    }
    
    public ZlibEncoder(final ZlibWrapper wrapper, final int compressionLevel, final int windowBits, final int memLevel) {
        this.z = new ZStream();
        this.finished = new AtomicBoolean();
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        }
        if (windowBits < 9 || windowBits > 15) {
            throw new IllegalArgumentException("windowBits: " + windowBits + " (expected: 9-15)");
        }
        if (memLevel < 1 || memLevel > 9) {
            throw new IllegalArgumentException("memLevel: " + memLevel + " (expected: 1-9)");
        }
        if (wrapper == null) {
            throw new NullPointerException("wrapper");
        }
        if (wrapper == ZlibWrapper.ZLIB_OR_NONE) {
            throw new IllegalArgumentException("wrapper '" + ZlibWrapper.ZLIB_OR_NONE + "' is not " + "allowed for compression.");
        }
        this.wrapperOverhead = ZlibUtil.wrapperOverhead(wrapper);
        synchronized (this.z) {
            final int resultCode = this.z.deflateInit(compressionLevel, windowBits, memLevel, ZlibUtil.convertWrapperType(wrapper));
            if (resultCode != 0) {
                ZlibUtil.fail(this.z, "initialization failure", resultCode);
            }
        }
    }
    
    public ZlibEncoder(final byte[] dictionary) {
        this(6, dictionary);
    }
    
    public ZlibEncoder(final int compressionLevel, final byte[] dictionary) {
        this(compressionLevel, 15, 8, dictionary);
    }
    
    public ZlibEncoder(final int compressionLevel, final int windowBits, final int memLevel, final byte[] dictionary) {
        this.z = new ZStream();
        this.finished = new AtomicBoolean();
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        }
        if (windowBits < 9 || windowBits > 15) {
            throw new IllegalArgumentException("windowBits: " + windowBits + " (expected: 9-15)");
        }
        if (memLevel < 1 || memLevel > 9) {
            throw new IllegalArgumentException("memLevel: " + memLevel + " (expected: 1-9)");
        }
        if (dictionary == null) {
            throw new NullPointerException("dictionary");
        }
        this.wrapperOverhead = ZlibUtil.wrapperOverhead(ZlibWrapper.ZLIB);
        synchronized (this.z) {
            int resultCode = this.z.deflateInit(compressionLevel, windowBits, memLevel, JZlib.W_ZLIB);
            if (resultCode != 0) {
                ZlibUtil.fail(this.z, "initialization failure", resultCode);
            }
            else {
                resultCode = this.z.deflateSetDictionary(dictionary, dictionary.length);
                if (resultCode != 0) {
                    ZlibUtil.fail(this.z, "failed to set the dictionary", resultCode);
                }
            }
        }
    }
    
    public ChannelFuture close() {
        final ChannelHandlerContext ctx = this.ctx;
        if (ctx == null) {
            throw new IllegalStateException("not added to a pipeline");
        }
        return this.finishEncode(ctx, null);
    }
    
    public boolean isClosed() {
        return this.finished.get();
    }
    
    @Override
    protected Object encode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        if (!(msg instanceof ChannelBuffer) || this.finished.get()) {
            return msg;
        }
        ChannelBuffer result;
        synchronized (this.z) {
            try {
                final ChannelBuffer uncompressed = (ChannelBuffer)msg;
                final int uncompressedLen = uncompressed.readableBytes();
                if (uncompressedLen == 0) {
                    return uncompressed;
                }
                final byte[] in = new byte[uncompressedLen];
                uncompressed.readBytes(in);
                this.z.next_in = in;
                this.z.next_in_index = 0;
                this.z.avail_in = uncompressedLen;
                final byte[] out = new byte[(int)Math.ceil(uncompressedLen * 1.001) + 12 + this.wrapperOverhead];
                this.z.next_out = out;
                this.z.next_out_index = 0;
                this.z.avail_out = out.length;
                final int resultCode = this.z.deflate(2);
                if (resultCode != 0) {
                    ZlibUtil.fail(this.z, "compression failure", resultCode);
                }
                if (this.z.next_out_index != 0) {
                    result = ctx.getChannel().getConfig().getBufferFactory().getBuffer(uncompressed.order(), out, 0, this.z.next_out_index);
                }
                else {
                    result = ChannelBuffers.EMPTY_BUFFER;
                }
            }
            finally {
                this.z.next_in = null;
                this.z.next_out = null;
            }
        }
        return result;
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
        if (!this.finished.compareAndSet(false, true)) {
            if (evt != null) {
                ctx.sendDownstream(evt);
            }
            return Channels.succeededFuture(ctx.getChannel());
        }
        ChannelFuture future;
        ChannelBuffer footer;
        synchronized (this.z) {
            try {
                this.z.next_in = ZlibEncoder.EMPTY_ARRAY;
                this.z.next_in_index = 0;
                this.z.avail_in = 0;
                final byte[] out = new byte[32];
                this.z.next_out = out;
                this.z.next_out_index = 0;
                this.z.avail_out = out.length;
                final int resultCode = this.z.deflate(4);
                if (resultCode != 0 && resultCode != 1) {
                    future = Channels.failedFuture(ctx.getChannel(), ZlibUtil.exception(this.z, "compression failure", resultCode));
                    footer = null;
                }
                else if (this.z.next_out_index != 0) {
                    future = Channels.future(ctx.getChannel());
                    footer = ctx.getChannel().getConfig().getBufferFactory().getBuffer(out, 0, this.z.next_out_index);
                }
                else {
                    future = Channels.future(ctx.getChannel());
                    footer = ChannelBuffers.EMPTY_BUFFER;
                }
            }
            finally {
                this.z.deflateEnd();
                this.z.next_in = null;
                this.z.next_out = null;
            }
        }
        if (footer != null) {
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
        EMPTY_ARRAY = new byte[0];
    }
}
