// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.frame;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.buffer.ChannelBufferFactory;
import java.util.Iterator;
import org.jboss.netty.channel.Channels;
import java.net.SocketAddress;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.CompositeChannelBuffer;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public abstract class FrameDecoder extends SimpleChannelUpstreamHandler implements LifeCycleAwareChannelHandler
{
    public static final int DEFAULT_MAX_COMPOSITEBUFFER_COMPONENTS = 1024;
    private boolean unfold;
    protected ChannelBuffer cumulation;
    private volatile ChannelHandlerContext ctx;
    private int copyThreshold;
    private int maxCumulationBufferComponents;
    
    protected FrameDecoder() {
        this(false);
    }
    
    protected FrameDecoder(final boolean unfold) {
        this.maxCumulationBufferComponents = 1024;
        this.unfold = unfold;
    }
    
    public final boolean isUnfold() {
        return this.unfold;
    }
    
    public final void setUnfold(final boolean unfold) {
        if (this.ctx == null) {
            this.unfold = unfold;
            return;
        }
        throw new IllegalStateException("decoder properties cannot be changed once the decoder is added to a pipeline.");
    }
    
    public final int getMaxCumulationBufferCapacity() {
        return this.copyThreshold;
    }
    
    public final void setMaxCumulationBufferCapacity(final int copyThreshold) {
        if (copyThreshold < 0) {
            throw new IllegalArgumentException("maxCumulationBufferCapacity must be >= 0");
        }
        if (this.ctx == null) {
            this.copyThreshold = copyThreshold;
            return;
        }
        throw new IllegalStateException("decoder properties cannot be changed once the decoder is added to a pipeline.");
    }
    
    public final int getMaxCumulationBufferComponents() {
        return this.maxCumulationBufferComponents;
    }
    
    public final void setMaxCumulationBufferComponents(final int maxCumulationBufferComponents) {
        if (maxCumulationBufferComponents < 2) {
            throw new IllegalArgumentException("maxCumulationBufferComponents: " + maxCumulationBufferComponents + " (expected: >= 2)");
        }
        if (this.ctx == null) {
            this.maxCumulationBufferComponents = maxCumulationBufferComponents;
            return;
        }
        throw new IllegalStateException("decoder properties cannot be changed once the decoder is added to a pipeline.");
    }
    
    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        final Object m = e.getMessage();
        if (!(m instanceof ChannelBuffer)) {
            ctx.sendUpstream(e);
            return;
        }
        ChannelBuffer input = (ChannelBuffer)m;
        if (!input.readable()) {
            return;
        }
        if (this.cumulation == null) {
            try {
                this.callDecode(ctx, e.getChannel(), input, e.getRemoteAddress());
            }
            finally {
                this.updateCumulation(ctx, input);
            }
        }
        else {
            input = this.appendToCumulation(input);
            try {
                this.callDecode(ctx, e.getChannel(), input, e.getRemoteAddress());
            }
            finally {
                this.updateCumulation(ctx, input);
            }
        }
    }
    
    protected ChannelBuffer appendToCumulation(ChannelBuffer input) {
        ChannelBuffer cumulation = this.cumulation;
        assert cumulation.readable();
        if (cumulation instanceof CompositeChannelBuffer) {
            final CompositeChannelBuffer composite = (CompositeChannelBuffer)cumulation;
            if (composite.numComponents() >= this.maxCumulationBufferComponents) {
                cumulation = composite.copy();
            }
        }
        input = (this.cumulation = ChannelBuffers.wrappedBuffer(cumulation, input));
        return input;
    }
    
    protected ChannelBuffer updateCumulation(final ChannelHandlerContext ctx, final ChannelBuffer input) {
        final int readableBytes = input.readableBytes();
        ChannelBuffer newCumulation;
        if (readableBytes > 0) {
            final int inputCapacity = input.capacity();
            if (readableBytes < inputCapacity && inputCapacity > this.copyThreshold) {
                (this.cumulation = (newCumulation = this.newCumulationBuffer(ctx, input.readableBytes()))).writeBytes(input);
            }
            else if (input.readerIndex() != 0) {
                newCumulation = (this.cumulation = input.slice());
            }
            else {
                newCumulation = input;
                this.cumulation = input;
            }
        }
        else {
            newCumulation = (this.cumulation = null);
        }
        return newCumulation;
    }
    
    @Override
    public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.cleanup(ctx, e);
    }
    
    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.cleanup(ctx, e);
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        ctx.sendUpstream(e);
    }
    
    protected abstract Object decode(final ChannelHandlerContext p0, final Channel p1, final ChannelBuffer p2) throws Exception;
    
    protected Object decodeLast(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        return this.decode(ctx, channel, buffer);
    }
    
    private void callDecode(final ChannelHandlerContext context, final Channel channel, final ChannelBuffer cumulation, final SocketAddress remoteAddress) throws Exception {
        while (cumulation.readable()) {
            final int oldReaderIndex = cumulation.readerIndex();
            final Object frame = this.decode(context, channel, cumulation);
            if (frame == null) {
                if (oldReaderIndex == cumulation.readerIndex()) {
                    break;
                }
                continue;
            }
            else {
                if (oldReaderIndex == cumulation.readerIndex()) {
                    throw new IllegalStateException("decode() method must read at least one byte if it returned a frame (caused by: " + this.getClass() + ')');
                }
                this.unfoldAndFireMessageReceived(context, remoteAddress, frame);
            }
        }
    }
    
    protected final void unfoldAndFireMessageReceived(final ChannelHandlerContext context, final SocketAddress remoteAddress, final Object result) {
        if (this.unfold) {
            if (result instanceof Object[]) {
                for (final Object r : (Object[])result) {
                    Channels.fireMessageReceived(context, r, remoteAddress);
                }
            }
            else if (result instanceof Iterable) {
                for (final Object r2 : (Iterable)result) {
                    Channels.fireMessageReceived(context, r2, remoteAddress);
                }
            }
            else {
                Channels.fireMessageReceived(context, result, remoteAddress);
            }
        }
        else {
            Channels.fireMessageReceived(context, result, remoteAddress);
        }
    }
    
    protected void cleanup(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        try {
            final ChannelBuffer cumulation = this.cumulation;
            if (cumulation == null) {
                return;
            }
            this.cumulation = null;
            if (cumulation.readable()) {
                this.callDecode(ctx, ctx.getChannel(), cumulation, null);
            }
            final Object partialFrame = this.decodeLast(ctx, ctx.getChannel(), cumulation);
            if (partialFrame != null) {
                this.unfoldAndFireMessageReceived(ctx, null, partialFrame);
            }
        }
        finally {
            ctx.sendUpstream(e);
        }
    }
    
    protected ChannelBuffer newCumulationBuffer(final ChannelHandlerContext ctx, final int minimumCapacity) {
        final ChannelBufferFactory factory = ctx.getChannel().getConfig().getBufferFactory();
        return factory.getBuffer(Math.max(minimumCapacity, 256));
    }
    
    public void replace(final String handlerName, final ChannelHandler handler) {
        if (this.ctx == null) {
            throw new IllegalStateException("Replace cann only be called once the FrameDecoder is added to the ChannelPipeline");
        }
        final ChannelPipeline pipeline = this.ctx.getPipeline();
        pipeline.addAfter(this.ctx.getName(), handlerName, handler);
        try {
            if (this.cumulation != null) {
                Channels.fireMessageReceived(this.ctx, this.cumulation.readBytes(this.actualReadableBytes()));
            }
        }
        finally {
            pipeline.remove(this);
        }
    }
    
    protected int actualReadableBytes() {
        return this.internalBuffer().readableBytes();
    }
    
    protected ChannelBuffer internalBuffer() {
        final ChannelBuffer buf = this.cumulation;
        if (buf == null) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        return buf;
    }
    
    protected ChannelBuffer extractFrame(final ChannelBuffer buffer, final int index, final int length) {
        final ChannelBuffer frame = buffer.factory().getBuffer(length);
        frame.writeBytes(buffer, index, length);
        return frame;
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
}
