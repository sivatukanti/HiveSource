// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class SpdyFrameCodec extends FrameDecoder implements SpdyFrameDecoderDelegate, ChannelDownstreamHandler
{
    private static final SpdyProtocolException INVALID_FRAME;
    private final SpdyFrameDecoder spdyFrameDecoder;
    private final SpdyFrameEncoder spdyFrameEncoder;
    private final SpdyHeaderBlockDecoder spdyHeaderBlockDecoder;
    private final SpdyHeaderBlockEncoder spdyHeaderBlockEncoder;
    private SpdyHeadersFrame spdyHeadersFrame;
    private SpdySettingsFrame spdySettingsFrame;
    private volatile ChannelHandlerContext ctx;
    
    public SpdyFrameCodec(final SpdyVersion version) {
        this(version, 8192, 16384, 6, 15, 8);
    }
    
    public SpdyFrameCodec(final SpdyVersion version, final int maxChunkSize, final int maxHeaderSize, final int compressionLevel, final int windowBits, final int memLevel) {
        this(version, maxChunkSize, SpdyHeaderBlockDecoder.newInstance(version, maxHeaderSize), SpdyHeaderBlockEncoder.newInstance(version, compressionLevel, windowBits, memLevel));
    }
    
    protected SpdyFrameCodec(final SpdyVersion version, final int maxChunkSize, final SpdyHeaderBlockDecoder spdyHeaderBlockDecoder, final SpdyHeaderBlockEncoder spdyHeaderBlockEncoder) {
        this.spdyFrameDecoder = new SpdyFrameDecoder(version, this, maxChunkSize);
        this.spdyFrameEncoder = new SpdyFrameEncoder(version);
        this.spdyHeaderBlockDecoder = spdyHeaderBlockDecoder;
        this.spdyHeaderBlockEncoder = spdyHeaderBlockEncoder;
    }
    
    @Override
    public void beforeAdd(final ChannelHandlerContext ctx) throws Exception {
        super.beforeAdd(ctx);
        this.ctx = ctx;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        this.spdyFrameDecoder.decode(buffer);
        return null;
    }
    
    @Override
    protected void cleanup(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        try {
            super.cleanup(ctx, e);
        }
        finally {
            this.spdyHeaderBlockDecoder.end();
            synchronized (this.spdyHeaderBlockEncoder) {
                this.spdyHeaderBlockEncoder.end();
            }
        }
    }
    
    public void handleDownstream(final ChannelHandlerContext ctx, final ChannelEvent evt) throws Exception {
        if (evt instanceof ChannelStateEvent) {
            final ChannelStateEvent e = (ChannelStateEvent)evt;
            switch (e.getState()) {
                case OPEN:
                case CONNECTED:
                case BOUND: {
                    if (Boolean.FALSE.equals(e.getValue()) || e.getValue() == null) {
                        synchronized (this.spdyHeaderBlockEncoder) {
                            this.spdyHeaderBlockEncoder.end();
                        }
                        break;
                    }
                    break;
                }
            }
        }
        if (!(evt instanceof MessageEvent)) {
            ctx.sendDownstream(evt);
            return;
        }
        final MessageEvent e2 = (MessageEvent)evt;
        final Object msg = e2.getMessage();
        if (msg instanceof SpdyDataFrame) {
            final SpdyDataFrame spdyDataFrame = (SpdyDataFrame)msg;
            final ChannelBuffer frame = this.spdyFrameEncoder.encodeDataFrame(spdyDataFrame.getStreamId(), spdyDataFrame.isLast(), spdyDataFrame.getData());
            Channels.write(ctx, e2.getFuture(), frame, e2.getRemoteAddress());
            return;
        }
        if (msg instanceof SpdySynStreamFrame) {
            synchronized (this.spdyHeaderBlockEncoder) {
                final SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame)msg;
                final ChannelBuffer frame2 = this.spdyFrameEncoder.encodeSynStreamFrame(spdySynStreamFrame.getStreamId(), spdySynStreamFrame.getAssociatedToStreamId(), spdySynStreamFrame.getPriority(), spdySynStreamFrame.isLast(), spdySynStreamFrame.isUnidirectional(), this.spdyHeaderBlockEncoder.encode(spdySynStreamFrame));
                Channels.write(ctx, e2.getFuture(), frame2, e2.getRemoteAddress());
            }
            return;
        }
        if (msg instanceof SpdySynReplyFrame) {
            synchronized (this.spdyHeaderBlockEncoder) {
                final SpdySynReplyFrame spdySynReplyFrame = (SpdySynReplyFrame)msg;
                final ChannelBuffer frame2 = this.spdyFrameEncoder.encodeSynReplyFrame(spdySynReplyFrame.getStreamId(), spdySynReplyFrame.isLast(), this.spdyHeaderBlockEncoder.encode(spdySynReplyFrame));
                Channels.write(ctx, e2.getFuture(), frame2, e2.getRemoteAddress());
            }
            return;
        }
        if (msg instanceof SpdyRstStreamFrame) {
            final SpdyRstStreamFrame spdyRstStreamFrame = (SpdyRstStreamFrame)msg;
            final ChannelBuffer frame = this.spdyFrameEncoder.encodeRstStreamFrame(spdyRstStreamFrame.getStreamId(), spdyRstStreamFrame.getStatus().getCode());
            Channels.write(ctx, e2.getFuture(), frame, e2.getRemoteAddress());
            return;
        }
        if (msg instanceof SpdySettingsFrame) {
            final SpdySettingsFrame spdySettingsFrame = (SpdySettingsFrame)msg;
            final ChannelBuffer frame = this.spdyFrameEncoder.encodeSettingsFrame(spdySettingsFrame);
            Channels.write(ctx, e2.getFuture(), frame, e2.getRemoteAddress());
            return;
        }
        if (msg instanceof SpdyPingFrame) {
            final SpdyPingFrame spdyPingFrame = (SpdyPingFrame)msg;
            final ChannelBuffer frame = this.spdyFrameEncoder.encodePingFrame(spdyPingFrame.getId());
            Channels.write(ctx, e2.getFuture(), frame, e2.getRemoteAddress());
            return;
        }
        if (msg instanceof SpdyGoAwayFrame) {
            final SpdyGoAwayFrame spdyGoAwayFrame = (SpdyGoAwayFrame)msg;
            final ChannelBuffer frame = this.spdyFrameEncoder.encodeGoAwayFrame(spdyGoAwayFrame.getLastGoodStreamId(), spdyGoAwayFrame.getStatus().getCode());
            Channels.write(ctx, e2.getFuture(), frame, e2.getRemoteAddress());
            return;
        }
        if (msg instanceof SpdyHeadersFrame) {
            synchronized (this.spdyHeaderBlockEncoder) {
                final SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame)msg;
                final ChannelBuffer frame2 = this.spdyFrameEncoder.encodeHeadersFrame(spdyHeadersFrame.getStreamId(), spdyHeadersFrame.isLast(), this.spdyHeaderBlockEncoder.encode(spdyHeadersFrame));
                Channels.write(ctx, e2.getFuture(), frame2, e2.getRemoteAddress());
            }
            return;
        }
        if (msg instanceof SpdyWindowUpdateFrame) {
            final SpdyWindowUpdateFrame spdyWindowUpdateFrame = (SpdyWindowUpdateFrame)msg;
            final ChannelBuffer frame = this.spdyFrameEncoder.encodeWindowUpdateFrame(spdyWindowUpdateFrame.getStreamId(), spdyWindowUpdateFrame.getDeltaWindowSize());
            Channels.write(ctx, e2.getFuture(), frame, e2.getRemoteAddress());
            return;
        }
        ctx.sendDownstream(evt);
    }
    
    public void readDataFrame(final int streamId, final boolean last, final ChannelBuffer data) {
        final SpdyDataFrame spdyDataFrame = new DefaultSpdyDataFrame(streamId);
        spdyDataFrame.setLast(last);
        spdyDataFrame.setData(data);
        Channels.fireMessageReceived(this.ctx, spdyDataFrame);
    }
    
    public void readSynStreamFrame(final int streamId, final int associatedToStreamId, final byte priority, final boolean last, final boolean unidirectional) {
        final SpdySynStreamFrame spdySynStreamFrame = new DefaultSpdySynStreamFrame(streamId, associatedToStreamId, priority);
        spdySynStreamFrame.setLast(last);
        spdySynStreamFrame.setUnidirectional(unidirectional);
        this.spdyHeadersFrame = spdySynStreamFrame;
    }
    
    public void readSynReplyFrame(final int streamId, final boolean last) {
        final SpdySynReplyFrame spdySynReplyFrame = new DefaultSpdySynReplyFrame(streamId);
        spdySynReplyFrame.setLast(last);
        this.spdyHeadersFrame = spdySynReplyFrame;
    }
    
    public void readRstStreamFrame(final int streamId, final int statusCode) {
        final SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, statusCode);
        Channels.fireMessageReceived(this.ctx, spdyRstStreamFrame);
    }
    
    public void readSettingsFrame(final boolean clearPersisted) {
        (this.spdySettingsFrame = new DefaultSpdySettingsFrame()).setClearPreviouslyPersistedSettings(clearPersisted);
    }
    
    public void readSetting(final int id, final int value, final boolean persistValue, final boolean persisted) {
        this.spdySettingsFrame.setValue(id, value, persistValue, persisted);
    }
    
    public void readSettingsEnd() {
        final Object frame = this.spdySettingsFrame;
        this.spdySettingsFrame = null;
        Channels.fireMessageReceived(this.ctx, frame);
    }
    
    public void readPingFrame(final int id) {
        final SpdyPingFrame spdyPingFrame = new DefaultSpdyPingFrame(id);
        Channels.fireMessageReceived(this.ctx, spdyPingFrame);
    }
    
    public void readGoAwayFrame(final int lastGoodStreamId, final int statusCode) {
        final SpdyGoAwayFrame spdyGoAwayFrame = new DefaultSpdyGoAwayFrame(lastGoodStreamId, statusCode);
        Channels.fireMessageReceived(this.ctx, spdyGoAwayFrame);
    }
    
    public void readHeadersFrame(final int streamId, final boolean last) {
        (this.spdyHeadersFrame = new DefaultSpdyHeadersFrame(streamId)).setLast(last);
    }
    
    public void readWindowUpdateFrame(final int streamId, final int deltaWindowSize) {
        final SpdyWindowUpdateFrame spdyWindowUpdateFrame = new DefaultSpdyWindowUpdateFrame(streamId, deltaWindowSize);
        Channels.fireMessageReceived(this.ctx, spdyWindowUpdateFrame);
    }
    
    public void readHeaderBlock(final ChannelBuffer headerBlock) {
        try {
            this.spdyHeaderBlockDecoder.decode(headerBlock, this.spdyHeadersFrame);
        }
        catch (Exception e) {
            Channels.fireExceptionCaught(this.ctx, e);
        }
    }
    
    public void readHeaderBlockEnd() {
        Object frame = null;
        try {
            this.spdyHeaderBlockDecoder.endHeaderBlock(this.spdyHeadersFrame);
            frame = this.spdyHeadersFrame;
            this.spdyHeadersFrame = null;
        }
        catch (Exception e) {
            Channels.fireExceptionCaught(this.ctx, e);
        }
        if (frame != null) {
            Channels.fireMessageReceived(this.ctx, frame);
        }
    }
    
    public void readFrameError(final String message) {
        Channels.fireExceptionCaught(this.ctx, SpdyFrameCodec.INVALID_FRAME);
    }
    
    static {
        INVALID_FRAME = new SpdyProtocolException("Received invalid frame");
    }
}
