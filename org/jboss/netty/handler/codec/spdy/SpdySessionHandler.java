// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import java.nio.channels.ClosedChannelException;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelEvent;
import java.net.SocketAddress;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelFutureListener;
import java.util.concurrent.atomic.AtomicInteger;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class SpdySessionHandler extends SimpleChannelUpstreamHandler implements ChannelDownstreamHandler
{
    private static final SpdyProtocolException PROTOCOL_EXCEPTION;
    private static final int DEFAULT_WINDOW_SIZE = 65536;
    private volatile int initialSendWindowSize;
    private volatile int initialReceiveWindowSize;
    private volatile int initialSessionReceiveWindowSize;
    private final SpdySession spdySession;
    private volatile int lastGoodStreamId;
    private static final int DEFAULT_MAX_CONCURRENT_STREAMS = Integer.MAX_VALUE;
    private volatile int remoteConcurrentStreams;
    private volatile int localConcurrentStreams;
    private final Object flowControlLock;
    private final AtomicInteger pings;
    private volatile boolean sentGoAwayFrame;
    private volatile boolean receivedGoAwayFrame;
    private volatile ChannelFutureListener closeSessionFutureListener;
    private final boolean server;
    private final int minorVersion;
    
    public SpdySessionHandler(final SpdyVersion spdyVersion, final boolean server) {
        this.initialSendWindowSize = 65536;
        this.initialReceiveWindowSize = 65536;
        this.initialSessionReceiveWindowSize = 65536;
        this.spdySession = new SpdySession(this.initialSendWindowSize, this.initialReceiveWindowSize);
        this.remoteConcurrentStreams = Integer.MAX_VALUE;
        this.localConcurrentStreams = Integer.MAX_VALUE;
        this.flowControlLock = new Object();
        this.pings = new AtomicInteger();
        if (spdyVersion == null) {
            throw new NullPointerException("spdyVersion");
        }
        this.server = server;
        this.minorVersion = spdyVersion.getMinorVersion();
    }
    
    public void setSessionReceiveWindowSize(final int sessionReceiveWindowSize) {
        if (sessionReceiveWindowSize < 0) {
            throw new IllegalArgumentException("sessionReceiveWindowSize");
        }
        this.initialSessionReceiveWindowSize = sessionReceiveWindowSize;
    }
    
    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        final Object msg = e.getMessage();
        if (msg instanceof SpdyDataFrame) {
            final SpdyDataFrame spdyDataFrame = (SpdyDataFrame)msg;
            final int streamId = spdyDataFrame.getStreamId();
            final int deltaWindowSize = -1 * spdyDataFrame.getData().readableBytes();
            final int newSessionWindowSize = this.spdySession.updateReceiveWindowSize(0, deltaWindowSize);
            if (newSessionWindowSize < 0) {
                this.issueSessionError(ctx, e.getChannel(), e.getRemoteAddress(), SpdySessionStatus.PROTOCOL_ERROR);
                return;
            }
            if (newSessionWindowSize <= this.initialSessionReceiveWindowSize / 2) {
                final int sessionDeltaWindowSize = this.initialSessionReceiveWindowSize - newSessionWindowSize;
                this.spdySession.updateReceiveWindowSize(0, sessionDeltaWindowSize);
                final SpdyWindowUpdateFrame spdyWindowUpdateFrame = new DefaultSpdyWindowUpdateFrame(0, sessionDeltaWindowSize);
                Channels.write(ctx, Channels.future(e.getChannel()), spdyWindowUpdateFrame, e.getRemoteAddress());
            }
            if (!this.spdySession.isActiveStream(streamId)) {
                if (streamId <= this.lastGoodStreamId) {
                    this.issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.PROTOCOL_ERROR);
                }
                else if (!this.sentGoAwayFrame) {
                    this.issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.INVALID_STREAM);
                }
                return;
            }
            if (this.spdySession.isRemoteSideClosed(streamId)) {
                this.issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.STREAM_ALREADY_CLOSED);
                return;
            }
            if (!this.isRemoteInitiatedId(streamId) && !this.spdySession.hasReceivedReply(streamId)) {
                this.issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.PROTOCOL_ERROR);
                return;
            }
            final int newWindowSize = this.spdySession.updateReceiveWindowSize(streamId, deltaWindowSize);
            if (newWindowSize < this.spdySession.getReceiveWindowSizeLowerBound(streamId)) {
                this.issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.FLOW_CONTROL_ERROR);
                return;
            }
            if (newWindowSize < 0) {
                while (spdyDataFrame.getData().readableBytes() > this.initialReceiveWindowSize) {
                    final SpdyDataFrame partialDataFrame = new DefaultSpdyDataFrame(streamId);
                    partialDataFrame.setData(spdyDataFrame.getData().readSlice(this.initialReceiveWindowSize));
                    Channels.fireMessageReceived(ctx, partialDataFrame, e.getRemoteAddress());
                }
            }
            if (newWindowSize <= this.initialReceiveWindowSize / 2 && !spdyDataFrame.isLast()) {
                final int streamDeltaWindowSize = this.initialReceiveWindowSize - newWindowSize;
                this.spdySession.updateReceiveWindowSize(streamId, streamDeltaWindowSize);
                final SpdyWindowUpdateFrame spdyWindowUpdateFrame2 = new DefaultSpdyWindowUpdateFrame(streamId, streamDeltaWindowSize);
                Channels.write(ctx, Channels.future(e.getChannel()), spdyWindowUpdateFrame2, e.getRemoteAddress());
            }
            if (spdyDataFrame.isLast()) {
                this.halfCloseStream(streamId, true, e.getFuture());
            }
        }
        else if (msg instanceof SpdySynStreamFrame) {
            final SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame)msg;
            final int streamId = spdySynStreamFrame.getStreamId();
            if (spdySynStreamFrame.isInvalid() || !this.isRemoteInitiatedId(streamId) || this.spdySession.isActiveStream(streamId)) {
                this.issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.PROTOCOL_ERROR);
                return;
            }
            if (streamId <= this.lastGoodStreamId) {
                this.issueSessionError(ctx, e.getChannel(), e.getRemoteAddress(), SpdySessionStatus.PROTOCOL_ERROR);
                return;
            }
            final byte priority = spdySynStreamFrame.getPriority();
            final boolean remoteSideClosed = spdySynStreamFrame.isLast();
            final boolean localSideClosed = spdySynStreamFrame.isUnidirectional();
            if (!this.acceptStream(streamId, priority, remoteSideClosed, localSideClosed)) {
                this.issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.REFUSED_STREAM);
                return;
            }
        }
        else if (msg instanceof SpdySynReplyFrame) {
            final SpdySynReplyFrame spdySynReplyFrame = (SpdySynReplyFrame)msg;
            final int streamId = spdySynReplyFrame.getStreamId();
            if (spdySynReplyFrame.isInvalid() || this.isRemoteInitiatedId(streamId) || this.spdySession.isRemoteSideClosed(streamId)) {
                this.issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.INVALID_STREAM);
                return;
            }
            if (this.spdySession.hasReceivedReply(streamId)) {
                this.issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.STREAM_IN_USE);
                return;
            }
            this.spdySession.receivedReply(streamId);
            if (spdySynReplyFrame.isLast()) {
                this.halfCloseStream(streamId, true, e.getFuture());
            }
        }
        else if (msg instanceof SpdyRstStreamFrame) {
            final SpdyRstStreamFrame spdyRstStreamFrame = (SpdyRstStreamFrame)msg;
            this.removeStream(spdyRstStreamFrame.getStreamId(), e.getFuture());
        }
        else if (msg instanceof SpdySettingsFrame) {
            final SpdySettingsFrame spdySettingsFrame = (SpdySettingsFrame)msg;
            final int settingsMinorVersion = spdySettingsFrame.getValue(0);
            if (settingsMinorVersion >= 0 && settingsMinorVersion != this.minorVersion) {
                this.issueSessionError(ctx, e.getChannel(), e.getRemoteAddress(), SpdySessionStatus.PROTOCOL_ERROR);
                return;
            }
            final int newConcurrentStreams = spdySettingsFrame.getValue(4);
            if (newConcurrentStreams >= 0) {
                this.remoteConcurrentStreams = newConcurrentStreams;
            }
            if (spdySettingsFrame.isPersisted(7)) {
                spdySettingsFrame.removeValue(7);
            }
            spdySettingsFrame.setPersistValue(7, false);
            final int newInitialWindowSize = spdySettingsFrame.getValue(7);
            if (newInitialWindowSize >= 0) {
                this.updateInitialSendWindowSize(newInitialWindowSize);
            }
        }
        else if (msg instanceof SpdyPingFrame) {
            final SpdyPingFrame spdyPingFrame = (SpdyPingFrame)msg;
            if (this.isRemoteInitiatedId(spdyPingFrame.getId())) {
                Channels.write(ctx, Channels.future(e.getChannel()), spdyPingFrame, e.getRemoteAddress());
                return;
            }
            if (this.pings.get() == 0) {
                return;
            }
            this.pings.getAndDecrement();
        }
        else if (msg instanceof SpdyGoAwayFrame) {
            this.receivedGoAwayFrame = true;
        }
        else if (msg instanceof SpdyHeadersFrame) {
            final SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame)msg;
            final int streamId = spdyHeadersFrame.getStreamId();
            if (spdyHeadersFrame.isInvalid()) {
                this.issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.PROTOCOL_ERROR);
                return;
            }
            if (this.spdySession.isRemoteSideClosed(streamId)) {
                this.issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.INVALID_STREAM);
                return;
            }
            if (spdyHeadersFrame.isLast()) {
                this.halfCloseStream(streamId, true, e.getFuture());
            }
        }
        else if (msg instanceof SpdyWindowUpdateFrame) {
            final SpdyWindowUpdateFrame spdyWindowUpdateFrame3 = (SpdyWindowUpdateFrame)msg;
            final int streamId = spdyWindowUpdateFrame3.getStreamId();
            final int deltaWindowSize = spdyWindowUpdateFrame3.getDeltaWindowSize();
            if (streamId != 0 && this.spdySession.isLocalSideClosed(streamId)) {
                return;
            }
            if (this.spdySession.getSendWindowSize(streamId) > Integer.MAX_VALUE - deltaWindowSize) {
                if (streamId == 0) {
                    this.issueSessionError(ctx, e.getChannel(), e.getRemoteAddress(), SpdySessionStatus.PROTOCOL_ERROR);
                }
                else {
                    this.issueStreamError(ctx, e.getRemoteAddress(), streamId, SpdyStreamStatus.FLOW_CONTROL_ERROR);
                }
                return;
            }
            this.updateSendWindowSize(ctx, streamId, deltaWindowSize);
            return;
        }
        super.messageReceived(ctx, e);
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        final Throwable cause = e.getCause();
        if (cause instanceof SpdyProtocolException) {
            this.issueSessionError(ctx, e.getChannel(), null, SpdySessionStatus.PROTOCOL_ERROR);
        }
        super.exceptionCaught(ctx, e);
    }
    
    public void handleDownstream(final ChannelHandlerContext ctx, final ChannelEvent evt) throws Exception {
        if (evt instanceof ChannelStateEvent) {
            final ChannelStateEvent e = (ChannelStateEvent)evt;
            switch (e.getState()) {
                case OPEN:
                case CONNECTED:
                case BOUND: {
                    if (Boolean.FALSE.equals(e.getValue()) || e.getValue() == null) {
                        this.sendGoAwayFrame(ctx, e);
                        return;
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
            final int streamId = spdyDataFrame.getStreamId();
            if (this.spdySession.isLocalSideClosed(streamId)) {
                e2.getFuture().setFailure(SpdySessionHandler.PROTOCOL_EXCEPTION);
                return;
            }
            synchronized (this.flowControlLock) {
                final int dataLength = spdyDataFrame.getData().readableBytes();
                int sendWindowSize = this.spdySession.getSendWindowSize(streamId);
                final int sessionSendWindowSize = this.spdySession.getSendWindowSize(0);
                sendWindowSize = Math.min(sendWindowSize, sessionSendWindowSize);
                if (sendWindowSize <= 0) {
                    this.spdySession.putPendingWrite(streamId, e2);
                    return;
                }
                if (sendWindowSize < dataLength) {
                    this.spdySession.updateSendWindowSize(streamId, -1 * sendWindowSize);
                    this.spdySession.updateSendWindowSize(0, -1 * sendWindowSize);
                    final SpdyDataFrame partialDataFrame = new DefaultSpdyDataFrame(streamId);
                    partialDataFrame.setData(spdyDataFrame.getData().readSlice(sendWindowSize));
                    this.spdySession.putPendingWrite(streamId, e2);
                    final ChannelFuture writeFuture = Channels.future(e2.getChannel());
                    final SocketAddress remoteAddress = e2.getRemoteAddress();
                    final ChannelHandlerContext context = ctx;
                    e2.getFuture().addListener(new ChannelFutureListener() {
                        public void operationComplete(final ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                final Channel channel = future.getChannel();
                                SpdySessionHandler.this.issueSessionError(context, channel, remoteAddress, SpdySessionStatus.INTERNAL_ERROR);
                            }
                        }
                    });
                    Channels.write(ctx, writeFuture, partialDataFrame, remoteAddress);
                    return;
                }
                this.spdySession.updateSendWindowSize(streamId, -1 * dataLength);
                this.spdySession.updateSendWindowSize(0, -1 * dataLength);
                final SocketAddress remoteAddress2 = e2.getRemoteAddress();
                final ChannelHandlerContext context2 = ctx;
                e2.getFuture().addListener(new ChannelFutureListener() {
                    public void operationComplete(final ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            final Channel channel = future.getChannel();
                            SpdySessionHandler.this.issueSessionError(context2, channel, remoteAddress2, SpdySessionStatus.INTERNAL_ERROR);
                        }
                    }
                });
            }
            if (spdyDataFrame.isLast()) {
                this.halfCloseStream(streamId, false, e2.getFuture());
            }
        }
        else if (msg instanceof SpdySynStreamFrame) {
            final SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame)msg;
            final int streamId = spdySynStreamFrame.getStreamId();
            if (this.isRemoteInitiatedId(streamId)) {
                e2.getFuture().setFailure(SpdySessionHandler.PROTOCOL_EXCEPTION);
                return;
            }
            final byte priority = spdySynStreamFrame.getPriority();
            final boolean remoteSideClosed = spdySynStreamFrame.isUnidirectional();
            final boolean localSideClosed = spdySynStreamFrame.isLast();
            if (!this.acceptStream(streamId, priority, remoteSideClosed, localSideClosed)) {
                e2.getFuture().setFailure(SpdySessionHandler.PROTOCOL_EXCEPTION);
                return;
            }
        }
        else if (msg instanceof SpdySynReplyFrame) {
            final SpdySynReplyFrame spdySynReplyFrame = (SpdySynReplyFrame)msg;
            final int streamId = spdySynReplyFrame.getStreamId();
            if (!this.isRemoteInitiatedId(streamId) || this.spdySession.isLocalSideClosed(streamId)) {
                e2.getFuture().setFailure(SpdySessionHandler.PROTOCOL_EXCEPTION);
                return;
            }
            if (spdySynReplyFrame.isLast()) {
                this.halfCloseStream(streamId, false, e2.getFuture());
            }
        }
        else if (msg instanceof SpdyRstStreamFrame) {
            final SpdyRstStreamFrame spdyRstStreamFrame = (SpdyRstStreamFrame)msg;
            this.removeStream(spdyRstStreamFrame.getStreamId(), e2.getFuture());
        }
        else if (msg instanceof SpdySettingsFrame) {
            final SpdySettingsFrame spdySettingsFrame = (SpdySettingsFrame)msg;
            final int settingsMinorVersion = spdySettingsFrame.getValue(0);
            if (settingsMinorVersion >= 0 && settingsMinorVersion != this.minorVersion) {
                e2.getFuture().setFailure(SpdySessionHandler.PROTOCOL_EXCEPTION);
                return;
            }
            final int newConcurrentStreams = spdySettingsFrame.getValue(4);
            if (newConcurrentStreams >= 0) {
                this.localConcurrentStreams = newConcurrentStreams;
            }
            if (spdySettingsFrame.isPersisted(7)) {
                spdySettingsFrame.removeValue(7);
            }
            spdySettingsFrame.setPersistValue(7, false);
            final int newInitialWindowSize = spdySettingsFrame.getValue(7);
            if (newInitialWindowSize >= 0) {
                this.updateInitialReceiveWindowSize(newInitialWindowSize);
            }
        }
        else if (msg instanceof SpdyPingFrame) {
            final SpdyPingFrame spdyPingFrame = (SpdyPingFrame)msg;
            if (this.isRemoteInitiatedId(spdyPingFrame.getId())) {
                e2.getFuture().setFailure(new IllegalArgumentException("invalid PING ID: " + spdyPingFrame.getId()));
                return;
            }
            this.pings.getAndIncrement();
        }
        else {
            if (msg instanceof SpdyGoAwayFrame) {
                e2.getFuture().setFailure(SpdySessionHandler.PROTOCOL_EXCEPTION);
                return;
            }
            if (msg instanceof SpdyHeadersFrame) {
                final SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame)msg;
                final int streamId = spdyHeadersFrame.getStreamId();
                if (this.spdySession.isLocalSideClosed(streamId)) {
                    e2.getFuture().setFailure(SpdySessionHandler.PROTOCOL_EXCEPTION);
                    return;
                }
                if (spdyHeadersFrame.isLast()) {
                    this.halfCloseStream(streamId, false, e2.getFuture());
                }
            }
            else if (msg instanceof SpdyWindowUpdateFrame) {
                e2.getFuture().setFailure(SpdySessionHandler.PROTOCOL_EXCEPTION);
                return;
            }
        }
        ctx.sendDownstream(evt);
    }
    
    private void issueSessionError(final ChannelHandlerContext ctx, final Channel channel, final SocketAddress remoteAddress, final SpdySessionStatus status) {
        final ChannelFuture future = this.sendGoAwayFrame(ctx, channel, remoteAddress, status);
        future.addListener(ChannelFutureListener.CLOSE);
    }
    
    private void issueStreamError(final ChannelHandlerContext ctx, final SocketAddress remoteAddress, final int streamId, final SpdyStreamStatus status) {
        final boolean fireMessageReceived = !this.spdySession.isRemoteSideClosed(streamId);
        final ChannelFuture future = Channels.future(ctx.getChannel());
        this.removeStream(streamId, future);
        final SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, status);
        Channels.write(ctx, future, spdyRstStreamFrame, remoteAddress);
        if (fireMessageReceived) {
            Channels.fireMessageReceived(ctx, spdyRstStreamFrame, remoteAddress);
        }
    }
    
    private boolean isRemoteInitiatedId(final int id) {
        final boolean serverId = SpdyCodecUtil.isServerId(id);
        return (this.server && !serverId) || (!this.server && serverId);
    }
    
    private synchronized void updateInitialSendWindowSize(final int newInitialWindowSize) {
        final int deltaWindowSize = newInitialWindowSize - this.initialSendWindowSize;
        this.initialSendWindowSize = newInitialWindowSize;
        this.spdySession.updateAllSendWindowSizes(deltaWindowSize);
    }
    
    private synchronized void updateInitialReceiveWindowSize(final int newInitialWindowSize) {
        final int deltaWindowSize = newInitialWindowSize - this.initialReceiveWindowSize;
        this.initialReceiveWindowSize = newInitialWindowSize;
        this.spdySession.updateAllReceiveWindowSizes(deltaWindowSize);
    }
    
    private synchronized boolean acceptStream(final int streamId, final byte priority, final boolean remoteSideClosed, final boolean localSideClosed) {
        if (this.receivedGoAwayFrame || this.sentGoAwayFrame) {
            return false;
        }
        final boolean remote = this.isRemoteInitiatedId(streamId);
        final int maxConcurrentStreams = remote ? this.localConcurrentStreams : this.remoteConcurrentStreams;
        if (this.spdySession.numActiveStreams(remote) >= maxConcurrentStreams) {
            return false;
        }
        this.spdySession.acceptStream(streamId, priority, remoteSideClosed, localSideClosed, this.initialSendWindowSize, this.initialReceiveWindowSize, remote);
        if (remote) {
            this.lastGoodStreamId = streamId;
        }
        return true;
    }
    
    private void halfCloseStream(final int streamId, final boolean remote, final ChannelFuture future) {
        if (remote) {
            this.spdySession.closeRemoteSide(streamId, this.isRemoteInitiatedId(streamId));
        }
        else {
            this.spdySession.closeLocalSide(streamId, this.isRemoteInitiatedId(streamId));
        }
        if (this.closeSessionFutureListener != null && this.spdySession.noActiveStreams()) {
            future.addListener(this.closeSessionFutureListener);
        }
    }
    
    private void removeStream(final int streamId, final ChannelFuture future) {
        this.spdySession.removeStream(streamId, this.isRemoteInitiatedId(streamId));
        if (this.closeSessionFutureListener != null && this.spdySession.noActiveStreams()) {
            future.addListener(this.closeSessionFutureListener);
        }
    }
    
    private void updateSendWindowSize(final ChannelHandlerContext ctx, final int streamId, final int deltaWindowSize) {
        synchronized (this.flowControlLock) {
            int newWindowSize = this.spdySession.updateSendWindowSize(streamId, deltaWindowSize);
            if (streamId != 0) {
                final int sessionSendWindowSize = this.spdySession.getSendWindowSize(0);
                newWindowSize = Math.min(newWindowSize, sessionSendWindowSize);
            }
            while (newWindowSize > 0) {
                final MessageEvent e = this.spdySession.getPendingWrite(streamId);
                if (e == null) {
                    break;
                }
                final SpdyDataFrame spdyDataFrame = (SpdyDataFrame)e.getMessage();
                final int dataFrameSize = spdyDataFrame.getData().readableBytes();
                final int writeStreamId = spdyDataFrame.getStreamId();
                if (streamId == 0) {
                    newWindowSize = Math.min(newWindowSize, this.spdySession.getSendWindowSize(writeStreamId));
                }
                if (newWindowSize >= dataFrameSize) {
                    this.spdySession.removePendingWrite(writeStreamId);
                    newWindowSize = this.spdySession.updateSendWindowSize(writeStreamId, -1 * dataFrameSize);
                    final int sessionSendWindowSize2 = this.spdySession.updateSendWindowSize(0, -1 * dataFrameSize);
                    newWindowSize = Math.min(newWindowSize, sessionSendWindowSize2);
                    final SocketAddress remoteAddress = e.getRemoteAddress();
                    final ChannelHandlerContext context = ctx;
                    e.getFuture().addListener(new ChannelFutureListener() {
                        public void operationComplete(final ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                final Channel channel = future.getChannel();
                                SpdySessionHandler.this.issueSessionError(context, channel, remoteAddress, SpdySessionStatus.INTERNAL_ERROR);
                            }
                        }
                    });
                    if (spdyDataFrame.isLast()) {
                        this.halfCloseStream(writeStreamId, false, e.getFuture());
                    }
                    Channels.write(ctx, e.getFuture(), spdyDataFrame, e.getRemoteAddress());
                }
                else {
                    this.spdySession.updateSendWindowSize(writeStreamId, -1 * newWindowSize);
                    this.spdySession.updateSendWindowSize(0, -1 * newWindowSize);
                    final SpdyDataFrame partialDataFrame = new DefaultSpdyDataFrame(writeStreamId);
                    partialDataFrame.setData(spdyDataFrame.getData().readSlice(newWindowSize));
                    final ChannelFuture writeFuture = Channels.future(e.getChannel());
                    final SocketAddress remoteAddress2 = e.getRemoteAddress();
                    final ChannelHandlerContext context2 = ctx;
                    e.getFuture().addListener(new ChannelFutureListener() {
                        public void operationComplete(final ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                final Channel channel = future.getChannel();
                                SpdySessionHandler.this.issueSessionError(context2, channel, remoteAddress2, SpdySessionStatus.INTERNAL_ERROR);
                            }
                        }
                    });
                    Channels.write(ctx, writeFuture, partialDataFrame, remoteAddress2);
                    newWindowSize = 0;
                }
            }
        }
    }
    
    private void sendGoAwayFrame(final ChannelHandlerContext ctx, final ChannelStateEvent e) {
        if (!e.getChannel().isConnected()) {
            ctx.sendDownstream(e);
            return;
        }
        final ChannelFuture future = this.sendGoAwayFrame(ctx, e.getChannel(), null, SpdySessionStatus.OK);
        if (this.spdySession.noActiveStreams()) {
            future.addListener(new ClosingChannelFutureListener(ctx, e));
        }
        else {
            this.closeSessionFutureListener = new ClosingChannelFutureListener(ctx, e);
        }
    }
    
    private synchronized ChannelFuture sendGoAwayFrame(final ChannelHandlerContext ctx, final Channel channel, final SocketAddress remoteAddress, final SpdySessionStatus status) {
        if (!this.sentGoAwayFrame) {
            this.sentGoAwayFrame = true;
            final SpdyGoAwayFrame spdyGoAwayFrame = new DefaultSpdyGoAwayFrame(this.lastGoodStreamId, status);
            final ChannelFuture future = Channels.future(channel);
            Channels.write(ctx, future, spdyGoAwayFrame, remoteAddress);
            return future;
        }
        return Channels.succeededFuture(channel);
    }
    
    static {
        PROTOCOL_EXCEPTION = new SpdyProtocolException();
    }
    
    private static final class ClosingChannelFutureListener implements ChannelFutureListener
    {
        private final ChannelHandlerContext ctx;
        private final ChannelStateEvent e;
        
        ClosingChannelFutureListener(final ChannelHandlerContext ctx, final ChannelStateEvent e) {
            this.ctx = ctx;
            this.e = e;
        }
        
        public void operationComplete(final ChannelFuture sentGoAwayFuture) throws Exception {
            if (!(sentGoAwayFuture.getCause() instanceof ClosedChannelException)) {
                Channels.close(this.ctx, this.e.getFuture());
            }
            else {
                this.e.getFuture().setSuccess();
            }
        }
    }
}
