// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.replay;

import org.jboss.netty.channel.ChannelStateEvent;
import java.net.SocketAddress;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public abstract class ReplayingDecoder<T extends Enum<T>> extends FrameDecoder
{
    private final ReplayingDecoderBuffer replayable;
    private T state;
    private int checkpoint;
    private boolean needsCleanup;
    
    protected ReplayingDecoder() {
        this(null);
    }
    
    protected ReplayingDecoder(final boolean unfold) {
        this(null, unfold);
    }
    
    protected ReplayingDecoder(final T initialState) {
        this(initialState, false);
    }
    
    protected ReplayingDecoder(final T initialState, final boolean unfold) {
        super(unfold);
        this.replayable = new ReplayingDecoderBuffer(this);
        this.state = initialState;
    }
    
    @Override
    protected ChannelBuffer internalBuffer() {
        return super.internalBuffer();
    }
    
    protected void checkpoint() {
        final ChannelBuffer cumulation = this.cumulation;
        if (cumulation != null) {
            this.checkpoint = cumulation.readerIndex();
        }
        else {
            this.checkpoint = -1;
        }
    }
    
    protected void checkpoint(final T state) {
        this.checkpoint();
        this.setState(state);
    }
    
    protected T getState() {
        return this.state;
    }
    
    protected T setState(final T newState) {
        final T oldState = this.state;
        this.state = newState;
        return oldState;
    }
    
    protected abstract Object decode(final ChannelHandlerContext p0, final Channel p1, final ChannelBuffer p2, final T p3) throws Exception;
    
    protected Object decodeLast(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer, final T state) throws Exception {
        return this.decode(ctx, channel, buffer, state);
    }
    
    @Override
    protected final Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        return this.decode(ctx, channel, buffer, this.state);
    }
    
    @Override
    protected final Object decodeLast(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        return this.decodeLast(ctx, channel, buffer, this.state);
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
        this.needsCleanup = true;
        if (this.cumulation == null) {
            this.cumulation = input;
            final int oldReaderIndex = input.readerIndex();
            final int inputSize = input.readableBytes();
            try {
                this.callDecode(ctx, e.getChannel(), input, this.replayable, e.getRemoteAddress());
            }
            finally {
                final int readableBytes = input.readableBytes();
                if (readableBytes > 0) {
                    final int inputCapacity = input.capacity();
                    final boolean copy = readableBytes != inputCapacity && inputCapacity > this.getMaxCumulationBufferCapacity();
                    if (this.checkpoint > 0) {
                        final int bytesToPreserve = inputSize - (this.checkpoint - oldReaderIndex);
                        if (copy) {
                            final ChannelBuffer cumulation = this.cumulation = this.newCumulationBuffer(ctx, bytesToPreserve);
                            cumulation.writeBytes(input, this.checkpoint, bytesToPreserve);
                        }
                        else {
                            this.cumulation = input.slice(this.checkpoint, bytesToPreserve);
                        }
                    }
                    else if (this.checkpoint == 0) {
                        if (copy) {
                            final ChannelBuffer cumulation = this.cumulation = this.newCumulationBuffer(ctx, inputSize);
                            cumulation.writeBytes(input, oldReaderIndex, inputSize);
                            cumulation.readerIndex(input.readerIndex());
                        }
                        else {
                            final ChannelBuffer cumulation = this.cumulation = input.slice(oldReaderIndex, inputSize);
                            cumulation.readerIndex(input.readerIndex());
                        }
                    }
                    else if (copy) {
                        final ChannelBuffer cumulation = this.cumulation = this.newCumulationBuffer(ctx, input.readableBytes());
                        cumulation.writeBytes(input);
                    }
                    else {
                        this.cumulation = input;
                    }
                }
                else {
                    this.cumulation = null;
                }
            }
        }
        else {
            input = this.appendToCumulation(input);
            try {
                this.callDecode(ctx, e.getChannel(), input, this.replayable, e.getRemoteAddress());
            }
            finally {
                this.updateCumulation(ctx, input);
            }
        }
    }
    
    private void callDecode(final ChannelHandlerContext context, final Channel channel, final ChannelBuffer input, final ChannelBuffer replayableInput, final SocketAddress remoteAddress) throws Exception {
        while (input.readable()) {
            final int readerIndex = input.readerIndex();
            this.checkpoint = readerIndex;
            final int oldReaderIndex = readerIndex;
            Object result = null;
            final T oldState = this.state;
            try {
                result = this.decode(context, channel, replayableInput, this.state);
                if (result == null) {
                    if (oldReaderIndex == input.readerIndex() && oldState == this.state) {
                        throw new IllegalStateException("null cannot be returned if no data is consumed and state didn't change.");
                    }
                    continue;
                }
            }
            catch (ReplayError replay) {
                final int checkpoint = this.checkpoint;
                if (checkpoint >= 0) {
                    input.readerIndex(checkpoint);
                }
            }
            if (result == null) {
                break;
            }
            if (oldReaderIndex == input.readerIndex() && oldState == this.state) {
                throw new IllegalStateException("decode() method must consume at least one byte if it returned a decoded message (caused by: " + this.getClass() + ')');
            }
            this.unfoldAndFireMessageReceived(context, remoteAddress, result);
        }
    }
    
    @Override
    protected void cleanup(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        try {
            final ChannelBuffer cumulation = this.cumulation;
            if (!this.needsCleanup) {
                return;
            }
            this.needsCleanup = false;
            this.replayable.terminate();
            if (cumulation != null && cumulation.readable()) {
                this.callDecode(ctx, e.getChannel(), cumulation, this.replayable, null);
            }
            final Object partiallyDecoded = this.decodeLast(ctx, e.getChannel(), this.replayable, this.state);
            this.cumulation = null;
            if (partiallyDecoded != null) {
                this.unfoldAndFireMessageReceived(ctx, null, partiallyDecoded);
            }
        }
        catch (ReplayError replay) {}
        finally {
            ctx.sendUpstream(e);
        }
    }
}
