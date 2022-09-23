// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ssl;

import org.jboss.netty.channel.DefaultChannelFuture;
import org.jboss.netty.logging.InternalLoggerFactory;
import java.util.List;
import java.util.ArrayList;
import org.jboss.netty.buffer.ChannelBufferFactory;
import javax.net.ssl.SSLEngineResult;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.util.internal.DetectionUtil;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.io.IOException;
import org.jboss.netty.channel.ExceptionEvent;
import java.nio.channels.ClosedChannelException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFutureListener;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLException;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.util.TimerTask;
import org.jboss.netty.channel.Channels;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.LinkedList;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.util.internal.NonReentrantLock;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.jboss.netty.channel.ChannelFuture;
import javax.net.ssl.SSLEngine;
import org.jboss.netty.channel.ChannelHandlerContext;
import java.util.regex.Pattern;
import java.nio.ByteBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class SslHandler extends FrameDecoder implements ChannelDownstreamHandler
{
    private static final InternalLogger logger;
    private static final ByteBuffer EMPTY_BUFFER;
    private static final Pattern IGNORABLE_CLASS_IN_STACK;
    private static final Pattern IGNORABLE_ERROR_MESSAGE;
    private static SslBufferPool defaultBufferPool;
    private volatile ChannelHandlerContext ctx;
    private final SSLEngine engine;
    private final SslBufferPool bufferPool;
    private final boolean startTls;
    private volatile boolean enableRenegotiation;
    final Object handshakeLock;
    private boolean handshaking;
    private volatile boolean handshaken;
    private volatile ChannelFuture handshakeFuture;
    private volatile int sentFirstMessage;
    private volatile int sentCloseNotify;
    private volatile int closedOutboundAndChannel;
    private static final AtomicIntegerFieldUpdater<SslHandler> SENT_FIRST_MESSAGE_UPDATER;
    private static final AtomicIntegerFieldUpdater<SslHandler> SENT_CLOSE_NOTIFY_UPDATER;
    private static final AtomicIntegerFieldUpdater<SslHandler> CLOSED_OUTBOUND_AND_CHANNEL_UPDATER;
    int ignoreClosedChannelException;
    final Object ignoreClosedChannelExceptionLock;
    private final Queue<PendingWrite> pendingUnencryptedWrites;
    private final NonReentrantLock pendingUnencryptedWritesLock;
    private final Queue<MessageEvent> pendingEncryptedWrites;
    private final NonReentrantLock pendingEncryptedWritesLock;
    private volatile boolean issueHandshake;
    private volatile boolean writeBeforeHandshakeDone;
    private final SSLEngineInboundCloseFuture sslEngineCloseFuture;
    private boolean closeOnSslException;
    private int packetLength;
    private final Timer timer;
    private final long handshakeTimeoutInMillis;
    private Timeout handshakeTimeout;
    
    public static synchronized SslBufferPool getDefaultBufferPool() {
        if (SslHandler.defaultBufferPool == null) {
            SslHandler.defaultBufferPool = new SslBufferPool();
        }
        return SslHandler.defaultBufferPool;
    }
    
    public SslHandler(final SSLEngine engine) {
        this(engine, getDefaultBufferPool(), false, null, 0L);
    }
    
    public SslHandler(final SSLEngine engine, final SslBufferPool bufferPool) {
        this(engine, bufferPool, false, null, 0L);
    }
    
    public SslHandler(final SSLEngine engine, final boolean startTls) {
        this(engine, getDefaultBufferPool(), startTls);
    }
    
    public SslHandler(final SSLEngine engine, final SslBufferPool bufferPool, final boolean startTls) {
        this(engine, bufferPool, startTls, null, 0L);
    }
    
    public SslHandler(final SSLEngine engine, final SslBufferPool bufferPool, final boolean startTls, final Timer timer, final long handshakeTimeoutInMillis) {
        this.enableRenegotiation = true;
        this.handshakeLock = new Object();
        this.ignoreClosedChannelExceptionLock = new Object();
        this.pendingUnencryptedWrites = new LinkedList<PendingWrite>();
        this.pendingUnencryptedWritesLock = new NonReentrantLock();
        this.pendingEncryptedWrites = new ConcurrentLinkedQueue<MessageEvent>();
        this.pendingEncryptedWritesLock = new NonReentrantLock();
        this.sslEngineCloseFuture = new SSLEngineInboundCloseFuture();
        if (engine == null) {
            throw new NullPointerException("engine");
        }
        if (bufferPool == null) {
            throw new NullPointerException("bufferPool");
        }
        if (timer == null && handshakeTimeoutInMillis > 0L) {
            throw new IllegalArgumentException("No Timer was given but a handshakeTimeoutInMillis, need both or none");
        }
        this.engine = engine;
        this.bufferPool = bufferPool;
        this.startTls = startTls;
        this.timer = timer;
        this.handshakeTimeoutInMillis = handshakeTimeoutInMillis;
    }
    
    public SSLEngine getEngine() {
        return this.engine;
    }
    
    public ChannelFuture handshake() {
        synchronized (this.handshakeLock) {
            if (this.handshaken && !this.isEnableRenegotiation()) {
                throw new IllegalStateException("renegotiation disabled");
            }
            final ChannelHandlerContext ctx = this.ctx;
            final Channel channel = ctx.getChannel();
            Exception exception = null;
            if (this.handshaking) {
                return this.handshakeFuture;
            }
            this.handshaking = true;
            ChannelFuture handshakeFuture;
            try {
                this.engine.beginHandshake();
                this.runDelegatedTasks();
                final ChannelFuture future = Channels.future(channel);
                this.handshakeFuture = future;
                handshakeFuture = future;
                if (this.handshakeTimeoutInMillis > 0L) {
                    this.handshakeTimeout = this.timer.newTimeout(new TimerTask() {
                        public void run(final Timeout timeout) throws Exception {
                            final ChannelFuture future = SslHandler.this.handshakeFuture;
                            if (future != null && future.isDone()) {
                                return;
                            }
                            SslHandler.this.setHandshakeFailure(channel, new SSLException("Handshake did not complete within " + SslHandler.this.handshakeTimeoutInMillis + "ms"));
                        }
                    }, this.handshakeTimeoutInMillis, TimeUnit.MILLISECONDS);
                }
            }
            catch (Exception e) {
                final ChannelFuture failedFuture = Channels.failedFuture(channel, e);
                this.handshakeFuture = failedFuture;
                handshakeFuture = failedFuture;
                exception = e;
            }
            if (exception == null) {
                try {
                    final ChannelFuture hsFuture = handshakeFuture;
                    this.wrapNonAppData(ctx, channel).addListener(new ChannelFutureListener() {
                        public void operationComplete(final ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                final Throwable cause = future.getCause();
                                hsFuture.setFailure(cause);
                                Channels.fireExceptionCaught(ctx, cause);
                                if (SslHandler.this.closeOnSslException) {
                                    Channels.close(ctx, Channels.future(channel));
                                }
                            }
                        }
                    });
                }
                catch (SSLException e2) {
                    handshakeFuture.setFailure(e2);
                    Channels.fireExceptionCaught(ctx, e2);
                    if (this.closeOnSslException) {
                        Channels.close(ctx, Channels.future(channel));
                    }
                }
            }
            else {
                Channels.fireExceptionCaught(ctx, exception);
                if (this.closeOnSslException) {
                    Channels.close(ctx, Channels.future(channel));
                }
            }
            return handshakeFuture;
        }
    }
    
    public ChannelFuture close() {
        final ChannelHandlerContext ctx = this.ctx;
        final Channel channel = ctx.getChannel();
        try {
            this.engine.closeOutbound();
            return this.wrapNonAppData(ctx, channel);
        }
        catch (SSLException e) {
            Channels.fireExceptionCaught(ctx, e);
            if (this.closeOnSslException) {
                Channels.close(ctx, Channels.future(channel));
            }
            return Channels.failedFuture(channel, e);
        }
    }
    
    public boolean isEnableRenegotiation() {
        return this.enableRenegotiation;
    }
    
    public void setEnableRenegotiation(final boolean enableRenegotiation) {
        this.enableRenegotiation = enableRenegotiation;
    }
    
    public void setIssueHandshake(final boolean issueHandshake) {
        this.issueHandshake = issueHandshake;
    }
    
    public boolean isIssueHandshake() {
        return this.issueHandshake;
    }
    
    public ChannelFuture getSSLEngineInboundCloseFuture() {
        return this.sslEngineCloseFuture;
    }
    
    public long getHandshakeTimeout() {
        return this.handshakeTimeoutInMillis;
    }
    
    public void setCloseOnSSLException(final boolean closeOnSslException) {
        if (this.ctx != null) {
            throw new IllegalStateException("Can only get changed before attached to ChannelPipeline");
        }
        this.closeOnSslException = closeOnSslException;
    }
    
    public boolean getCloseOnSSLException() {
        return this.closeOnSslException;
    }
    
    public void handleDownstream(final ChannelHandlerContext context, final ChannelEvent evt) throws Exception {
        if (evt instanceof ChannelStateEvent) {
            final ChannelStateEvent e = (ChannelStateEvent)evt;
            switch (SslHandler$7.$SwitchMap$org$jboss$netty$channel$ChannelState[e.getState().ordinal()]) {
                case 0:
                case 1:
                case 2: {
                    if (Boolean.FALSE.equals(e.getValue()) || e.getValue() == null) {
                        this.closeOutboundAndChannel(context, e);
                        return;
                    }
                    break;
                }
            }
        }
        if (!(evt instanceof MessageEvent)) {
            context.sendDownstream(evt);
            return;
        }
        final MessageEvent e2 = (MessageEvent)evt;
        if (!(e2.getMessage() instanceof ChannelBuffer)) {
            context.sendDownstream(evt);
            return;
        }
        if (this.startTls && SslHandler.SENT_FIRST_MESSAGE_UPDATER.compareAndSet(this, 0, 1)) {
            context.sendDownstream(evt);
            return;
        }
        final ChannelBuffer msg = (ChannelBuffer)e2.getMessage();
        PendingWrite pendingWrite;
        if (msg.readable()) {
            pendingWrite = new PendingWrite(evt.getFuture(), msg.toByteBuffer(msg.readerIndex(), msg.readableBytes()));
        }
        else {
            pendingWrite = new PendingWrite(evt.getFuture(), null);
        }
        this.pendingUnencryptedWritesLock.lock();
        try {
            this.pendingUnencryptedWrites.add(pendingWrite);
        }
        finally {
            this.pendingUnencryptedWritesLock.unlock();
        }
        if (this.handshakeFuture == null || !this.handshakeFuture.isDone()) {
            this.writeBeforeHandshakeDone = true;
        }
        this.wrap(context, evt.getChannel());
    }
    
    private void cancelHandshakeTimeout() {
        if (this.handshakeTimeout != null) {
            this.handshakeTimeout.cancel();
        }
    }
    
    @Override
    public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        synchronized (this.handshakeLock) {
            if (this.handshaking) {
                this.cancelHandshakeTimeout();
                this.handshakeFuture.setFailure(new ClosedChannelException());
            }
        }
        try {
            super.channelDisconnected(ctx, e);
        }
        finally {
            this.unwrapNonAppData(ctx, e.getChannel(), false);
            this.closeEngine();
        }
    }
    
    private void closeEngine() {
        this.engine.closeOutbound();
        if (this.sentCloseNotify == 0 && this.handshaken) {
            try {
                this.engine.closeInbound();
            }
            catch (SSLException ex) {
                if (SslHandler.logger.isDebugEnabled()) {
                    SslHandler.logger.debug("Failed to clean up SSLEngine.", ex);
                }
            }
        }
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        final Throwable cause = e.getCause();
        if (cause instanceof IOException) {
            if (cause instanceof ClosedChannelException) {
                synchronized (this.ignoreClosedChannelExceptionLock) {
                    if (this.ignoreClosedChannelException > 0) {
                        --this.ignoreClosedChannelException;
                        if (SslHandler.logger.isDebugEnabled()) {
                            SslHandler.logger.debug("Swallowing an exception raised while writing non-app data", cause);
                        }
                        return;
                    }
                }
            }
            else if (this.ignoreException(cause)) {
                return;
            }
        }
        ctx.sendUpstream(e);
    }
    
    private boolean ignoreException(final Throwable t) {
        if (!(t instanceof SSLException) && t instanceof IOException && this.engine.isOutboundDone()) {
            final String message = String.valueOf(t.getMessage()).toLowerCase();
            if (SslHandler.IGNORABLE_ERROR_MESSAGE.matcher(message).matches()) {
                return true;
            }
            final StackTraceElement[] arr$;
            final StackTraceElement[] elements = arr$ = t.getStackTrace();
            for (final StackTraceElement element : arr$) {
                final String classname = element.getClassName();
                final String methodname = element.getMethodName();
                if (!classname.startsWith("org.jboss.netty.")) {
                    if ("read".equals(methodname)) {
                        if (SslHandler.IGNORABLE_CLASS_IN_STACK.matcher(classname).matches()) {
                            return true;
                        }
                        try {
                            final Class<?> clazz = this.getClass().getClassLoader().loadClass(classname);
                            if (SocketChannel.class.isAssignableFrom(clazz) || DatagramChannel.class.isAssignableFrom(clazz)) {
                                return true;
                            }
                            if (DetectionUtil.javaVersion() >= 7 && "com.sun.nio.sctp.SctpChannel".equals(clazz.getSuperclass().getName())) {
                                return true;
                            }
                        }
                        catch (ClassNotFoundException ex) {}
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean isEncrypted(final ChannelBuffer buffer) {
        return getEncryptedPacketLength(buffer, buffer.readerIndex()) != -1;
    }
    
    private static int getEncryptedPacketLength(final ChannelBuffer buffer, final int offset) {
        int packetLength = 0;
        boolean tls = false;
        switch (buffer.getUnsignedByte(offset)) {
            case 20:
            case 21:
            case 22:
            case 23: {
                tls = true;
                break;
            }
            default: {
                tls = false;
                break;
            }
        }
        if (tls) {
            final int majorVersion = buffer.getUnsignedByte(offset + 1);
            if (majorVersion == 3) {
                packetLength = (getShort(buffer, offset + 3) & 0xFFFF) + 5;
                if (packetLength <= 5) {
                    tls = false;
                }
            }
            else {
                tls = false;
            }
        }
        if (!tls) {
            boolean sslv2 = true;
            final int headerLength = ((buffer.getUnsignedByte(offset) & 0x80) != 0x0) ? 2 : 3;
            final int majorVersion2 = buffer.getUnsignedByte(offset + headerLength + 1);
            if (majorVersion2 == 2 || majorVersion2 == 3) {
                if (headerLength == 2) {
                    packetLength = (getShort(buffer, offset) & 0x7FFF) + 2;
                }
                else {
                    packetLength = (getShort(buffer, offset) & 0x3FFF) + 3;
                }
                if (packetLength <= headerLength) {
                    sslv2 = false;
                }
            }
            else {
                sslv2 = false;
            }
            if (!sslv2) {
                return -1;
            }
        }
        return packetLength;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer in) throws Exception {
        final int startOffset = in.readerIndex();
        final int endOffset = in.writerIndex();
        int offset = startOffset;
        int totalLength = 0;
        if (this.packetLength > 0) {
            if (endOffset - startOffset < this.packetLength) {
                return null;
            }
            offset += this.packetLength;
            totalLength = this.packetLength;
            this.packetLength = 0;
        }
        boolean nonSslRecord = false;
        while (totalLength < 18713) {
            final int readableBytes = endOffset - offset;
            if (readableBytes < 5) {
                break;
            }
            final int packetLength = getEncryptedPacketLength(in, offset);
            if (packetLength == -1) {
                nonSslRecord = true;
                break;
            }
            assert packetLength > 0;
            if (packetLength > readableBytes) {
                this.packetLength = packetLength;
                break;
            }
            final int newTotalLength = totalLength + packetLength;
            if (newTotalLength > 18713) {
                break;
            }
            offset += packetLength;
            totalLength = newTotalLength;
        }
        ChannelBuffer unwrapped = null;
        if (totalLength > 0) {
            in.skipBytes(totalLength);
            final ByteBuffer inNetBuf = in.toByteBuffer(startOffset, totalLength);
            unwrapped = this.unwrap(ctx, channel, inNetBuf, totalLength, true);
        }
        if (!nonSslRecord) {
            return unwrapped;
        }
        final NotSslRecordException e = new NotSslRecordException("not an SSL/TLS record: " + ChannelBuffers.hexDump(in));
        in.skipBytes(in.readableBytes());
        if (this.closeOnSslException) {
            Channels.fireExceptionCaught(ctx, e);
            Channels.close(ctx, Channels.future(channel));
            return null;
        }
        throw e;
    }
    
    private static short getShort(final ChannelBuffer buf, final int offset) {
        return (short)(buf.getByte(offset) << 8 | (buf.getByte(offset + 1) & 0xFF));
    }
    
    private void wrap(final ChannelHandlerContext context, final Channel channel) throws SSLException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        org/jboss/netty/handler/ssl/SslHandler.bufferPool:Lorg/jboss/netty/handler/ssl/SslBufferPool;
        //     4: invokevirtual   org/jboss/netty/handler/ssl/SslBufferPool.acquireBuffer:()Ljava/nio/ByteBuffer;
        //     7: astore          outNetBuf
        //     9: iconst_1       
        //    10: istore          success
        //    12: iconst_0       
        //    13: istore          offered
        //    15: iconst_0       
        //    16: istore          needsUnwrap
        //    18: aconst_null    
        //    19: astore          pendingWrite
        //    21: aload_0         /* this */
        //    22: getfield        org/jboss/netty/handler/ssl/SslHandler.pendingUnencryptedWritesLock:Lorg/jboss/netty/util/internal/NonReentrantLock;
        //    25: invokevirtual   org/jboss/netty/util/internal/NonReentrantLock.lock:()V
        //    28: aload_0         /* this */
        //    29: getfield        org/jboss/netty/handler/ssl/SslHandler.pendingUnencryptedWrites:Ljava/util/Queue;
        //    32: invokeinterface java/util/Queue.peek:()Ljava/lang/Object;
        //    37: checkcast       Lorg/jboss/netty/handler/ssl/SslHandler$PendingWrite;
        //    40: astore          pendingWrite
        //    42: aload           pendingWrite
        //    44: ifnonnull       53
        //    47: jsr             520
        //    50: goto            534
        //    53: aload           pendingWrite
        //    55: getfield        org/jboss/netty/handler/ssl/SslHandler$PendingWrite.outAppBuf:Ljava/nio/ByteBuffer;
        //    58: astore          outAppBuf
        //    60: aload           outAppBuf
        //    62: ifnonnull       107
        //    65: aload_0         /* this */
        //    66: getfield        org/jboss/netty/handler/ssl/SslHandler.pendingUnencryptedWrites:Ljava/util/Queue;
        //    69: invokeinterface java/util/Queue.remove:()Ljava/lang/Object;
        //    74: pop            
        //    75: aload_0         /* this */
        //    76: new             Lorg/jboss/netty/channel/DownstreamMessageEvent;
        //    79: dup            
        //    80: aload_2         /* channel */
        //    81: aload           pendingWrite
        //    83: getfield        org/jboss/netty/handler/ssl/SslHandler$PendingWrite.future:Lorg/jboss/netty/channel/ChannelFuture;
        //    86: getstatic       org/jboss/netty/buffer/ChannelBuffers.EMPTY_BUFFER:Lorg/jboss/netty/buffer/ChannelBuffer;
        //    89: aload_2         /* channel */
        //    90: invokeinterface org/jboss/netty/channel/Channel.getRemoteAddress:()Ljava/net/SocketAddress;
        //    95: invokespecial   org/jboss/netty/channel/DownstreamMessageEvent.<init>:(Lorg/jboss/netty/channel/Channel;Lorg/jboss/netty/channel/ChannelFuture;Ljava/lang/Object;Ljava/net/SocketAddress;)V
        //    98: invokespecial   org/jboss/netty/handler/ssl/SslHandler.offerEncryptedWriteRequest:(Lorg/jboss/netty/channel/MessageEvent;)V
        //   101: iconst_1       
        //   102: istore          offered
        //   104: goto            506
        //   107: aload_0         /* this */
        //   108: getfield        org/jboss/netty/handler/ssl/SslHandler.handshakeLock:Ljava/lang/Object;
        //   111: dup            
        //   112: astore          10
        //   114: monitorenter   
        //   115: aconst_null    
        //   116: astore          result
        //   118: aload_0         /* this */
        //   119: getfield        org/jboss/netty/handler/ssl/SslHandler.engine:Ljavax/net/ssl/SSLEngine;
        //   122: aload           outAppBuf
        //   124: aload           outNetBuf
        //   126: invokevirtual   javax/net/ssl/SSLEngine.wrap:(Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;)Ljavax/net/ssl/SSLEngineResult;
        //   129: astore          result
        //   131: jsr             145
        //   134: goto            167
        //   137: astore          12
        //   139: jsr             145
        //   142: aload           12
        //   144: athrow         
        //   145: astore          13
        //   147: aload           outAppBuf
        //   149: invokevirtual   java/nio/ByteBuffer.hasRemaining:()Z
        //   152: ifne            165
        //   155: aload_0         /* this */
        //   156: getfield        org/jboss/netty/handler/ssl/SslHandler.pendingUnencryptedWrites:Ljava/util/Queue;
        //   159: invokeinterface java/util/Queue.remove:()Ljava/lang/Object;
        //   164: pop            
        //   165: ret             13
        //   167: aload           result
        //   169: invokevirtual   javax/net/ssl/SSLEngineResult.bytesProduced:()I
        //   172: ifle            287
        //   175: aload           outNetBuf
        //   177: invokevirtual   java/nio/ByteBuffer.flip:()Ljava/nio/Buffer;
        //   180: pop            
        //   181: aload           outNetBuf
        //   183: invokevirtual   java/nio/ByteBuffer.remaining:()I
        //   186: istore          remaining
        //   188: aload_0         /* this */
        //   189: getfield        org/jboss/netty/handler/ssl/SslHandler.ctx:Lorg/jboss/netty/channel/ChannelHandlerContext;
        //   192: invokeinterface org/jboss/netty/channel/ChannelHandlerContext.getChannel:()Lorg/jboss/netty/channel/Channel;
        //   197: invokeinterface org/jboss/netty/channel/Channel.getConfig:()Lorg/jboss/netty/channel/ChannelConfig;
        //   202: invokeinterface org/jboss/netty/channel/ChannelConfig.getBufferFactory:()Lorg/jboss/netty/buffer/ChannelBufferFactory;
        //   207: iload           remaining
        //   209: invokeinterface org/jboss/netty/buffer/ChannelBufferFactory.getBuffer:(I)Lorg/jboss/netty/buffer/ChannelBuffer;
        //   214: astore_3        /* msg */
        //   215: aload_3         /* msg */
        //   216: aload           outNetBuf
        //   218: invokeinterface org/jboss/netty/buffer/ChannelBuffer.writeBytes:(Ljava/nio/ByteBuffer;)V
        //   223: aload           outNetBuf
        //   225: invokevirtual   java/nio/ByteBuffer.clear:()Ljava/nio/Buffer;
        //   228: pop            
        //   229: aload           pendingWrite
        //   231: getfield        org/jboss/netty/handler/ssl/SslHandler$PendingWrite.outAppBuf:Ljava/nio/ByteBuffer;
        //   234: invokevirtual   java/nio/ByteBuffer.hasRemaining:()Z
        //   237: ifeq            249
        //   240: aload_2         /* channel */
        //   241: invokestatic    org/jboss/netty/channel/Channels.succeededFuture:(Lorg/jboss/netty/channel/Channel;)Lorg/jboss/netty/channel/ChannelFuture;
        //   244: astore          future
        //   246: goto            256
        //   249: aload           pendingWrite
        //   251: getfield        org/jboss/netty/handler/ssl/SslHandler$PendingWrite.future:Lorg/jboss/netty/channel/ChannelFuture;
        //   254: astore          future
        //   256: new             Lorg/jboss/netty/channel/DownstreamMessageEvent;
        //   259: dup            
        //   260: aload_2         /* channel */
        //   261: aload           future
        //   263: aload_3         /* msg */
        //   264: aload_2         /* channel */
        //   265: invokeinterface org/jboss/netty/channel/Channel.getRemoteAddress:()Ljava/net/SocketAddress;
        //   270: invokespecial   org/jboss/netty/channel/DownstreamMessageEvent.<init>:(Lorg/jboss/netty/channel/Channel;Lorg/jboss/netty/channel/ChannelFuture;Ljava/lang/Object;Ljava/net/SocketAddress;)V
        //   273: astore          encryptedWrite
        //   275: aload_0         /* this */
        //   276: aload           encryptedWrite
        //   278: invokespecial   org/jboss/netty/handler/ssl/SslHandler.offerEncryptedWriteRequest:(Lorg/jboss/netty/channel/MessageEvent;)V
        //   281: iconst_1       
        //   282: istore          offered
        //   284: goto            492
        //   287: aload           result
        //   289: invokevirtual   javax/net/ssl/SSLEngineResult.getStatus:()Ljavax/net/ssl/SSLEngineResult$Status;
        //   292: getstatic       javax/net/ssl/SSLEngineResult$Status.CLOSED:Ljavax/net/ssl/SSLEngineResult$Status;
        //   295: if_acmpne       310
        //   298: iconst_0       
        //   299: istore          success
        //   301: aload           10
        //   303: monitorexit    
        //   304: jsr             520
        //   307: goto            534
        //   310: aload           result
        //   312: invokevirtual   javax/net/ssl/SSLEngineResult.getHandshakeStatus:()Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;
        //   315: astore          handshakeStatus
        //   317: aload_0         /* this */
        //   318: aload           handshakeStatus
        //   320: invokespecial   org/jboss/netty/handler/ssl/SslHandler.handleRenegotiation:(Ljavax/net/ssl/SSLEngineResult$HandshakeStatus;)V
        //   323: getstatic       org/jboss/netty/handler/ssl/SslHandler$7.$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus:[I
        //   326: aload           handshakeStatus
        //   328: invokevirtual   javax/net/ssl/SSLEngineResult$HandshakeStatus.ordinal:()I
        //   331: iaload         
        //   332: tableswitch {
        //                2: 368
        //                3: 388
        //                4: 400
        //                5: 407
        //                6: 435
        //          default: 464
        //        }
        //   368: aload           outAppBuf
        //   370: invokevirtual   java/nio/ByteBuffer.hasRemaining:()Z
        //   373: ifeq            379
        //   376: goto            492
        //   379: aload           10
        //   381: monitorexit    
        //   382: jsr             520
        //   385: goto            534
        //   388: iconst_1       
        //   389: istore          needsUnwrap
        //   391: aload           10
        //   393: monitorexit    
        //   394: jsr             520
        //   397: goto            534
        //   400: aload_0         /* this */
        //   401: invokespecial   org/jboss/netty/handler/ssl/SslHandler.runDelegatedTasks:()V
        //   404: goto            492
        //   407: aload_0         /* this */
        //   408: aload_2         /* channel */
        //   409: invokespecial   org/jboss/netty/handler/ssl/SslHandler.setHandshakeSuccess:(Lorg/jboss/netty/channel/Channel;)V
        //   412: aload           result
        //   414: invokevirtual   javax/net/ssl/SSLEngineResult.getStatus:()Ljavax/net/ssl/SSLEngineResult$Status;
        //   417: getstatic       javax/net/ssl/SSLEngineResult$Status.CLOSED:Ljavax/net/ssl/SSLEngineResult$Status;
        //   420: if_acmpne       426
        //   423: iconst_0       
        //   424: istore          success
        //   426: aload           10
        //   428: monitorexit    
        //   429: jsr             520
        //   432: goto            534
        //   435: aload_0         /* this */
        //   436: aload_2         /* channel */
        //   437: invokespecial   org/jboss/netty/handler/ssl/SslHandler.setHandshakeSuccessIfStillHandshaking:(Lorg/jboss/netty/channel/Channel;)Z
        //   440: pop            
        //   441: aload           result
        //   443: invokevirtual   javax/net/ssl/SSLEngineResult.getStatus:()Ljavax/net/ssl/SSLEngineResult$Status;
        //   446: getstatic       javax/net/ssl/SSLEngineResult$Status.CLOSED:Ljavax/net/ssl/SSLEngineResult$Status;
        //   449: if_acmpne       455
        //   452: iconst_0       
        //   453: istore          success
        //   455: aload           10
        //   457: monitorexit    
        //   458: jsr             520
        //   461: goto            534
        //   464: new             Ljava/lang/IllegalStateException;
        //   467: dup            
        //   468: new             Ljava/lang/StringBuilder;
        //   471: dup            
        //   472: invokespecial   java/lang/StringBuilder.<init>:()V
        //   475: ldc             "Unknown handshake status: "
        //   477: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   480: aload           handshakeStatus
        //   482: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //   485: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   488: invokespecial   java/lang/IllegalStateException.<init>:(Ljava/lang/String;)V
        //   491: athrow         
        //   492: aload           10
        //   494: monitorexit    
        //   495: goto            506
        //   498: astore          15
        //   500: aload           10
        //   502: monitorexit    
        //   503: aload           15
        //   505: athrow         
        //   506: jsr             520
        //   509: goto            531
        //   512: astore          16
        //   514: jsr             520
        //   517: aload           16
        //   519: athrow         
        //   520: astore          17
        //   522: aload_0         /* this */
        //   523: getfield        org/jboss/netty/handler/ssl/SslHandler.pendingUnencryptedWritesLock:Lorg/jboss/netty/util/internal/NonReentrantLock;
        //   526: invokevirtual   org/jboss/netty/util/internal/NonReentrantLock.unlock:()V
        //   529: ret             17
        //   531: goto            21
        //   534: jsr             563
        //   537: goto            712
        //   540: astore          e
        //   542: iconst_0       
        //   543: istore          success
        //   545: aload_0         /* this */
        //   546: aload_2         /* channel */
        //   547: aload           e
        //   549: invokespecial   org/jboss/netty/handler/ssl/SslHandler.setHandshakeFailure:(Lorg/jboss/netty/channel/Channel;Ljavax/net/ssl/SSLException;)V
        //   552: aload           e
        //   554: athrow         
        //   555: astore          18
        //   557: jsr             563
        //   560: aload           18
        //   562: athrow         
        //   563: astore          19
        //   565: aload_0         /* this */
        //   566: getfield        org/jboss/netty/handler/ssl/SslHandler.bufferPool:Lorg/jboss/netty/handler/ssl/SslBufferPool;
        //   569: aload           outNetBuf
        //   571: invokevirtual   org/jboss/netty/handler/ssl/SslBufferPool.releaseBuffer:(Ljava/nio/ByteBuffer;)V
        //   574: iload           offered
        //   576: ifeq            584
        //   579: aload_0         /* this */
        //   580: aload_1         /* context */
        //   581: invokespecial   org/jboss/netty/handler/ssl/SslHandler.flushPendingEncryptedWrites:(Lorg/jboss/netty/channel/ChannelHandlerContext;)V
        //   584: iload           success
        //   586: ifne            710
        //   589: aload_2         /* channel */
        //   590: invokeinterface org/jboss/netty/channel/Channel.isOpen:()Z
        //   595: ifeq            610
        //   598: new             Ljavax/net/ssl/SSLException;
        //   601: dup            
        //   602: ldc             "SSLEngine already closed"
        //   604: invokespecial   javax/net/ssl/SSLException.<init>:(Ljava/lang/String;)V
        //   607: goto            617
        //   610: new             Ljava/nio/channels/ClosedChannelException;
        //   613: dup            
        //   614: invokespecial   java/nio/channels/ClosedChannelException.<init>:()V
        //   617: astore          cause
        //   619: aload           pendingWrite
        //   621: ifnull          637
        //   624: aload           pendingWrite
        //   626: getfield        org/jboss/netty/handler/ssl/SslHandler$PendingWrite.future:Lorg/jboss/netty/channel/ChannelFuture;
        //   629: aload           cause
        //   631: invokeinterface org/jboss/netty/channel/ChannelFuture.setFailure:(Ljava/lang/Throwable;)Z
        //   636: pop            
        //   637: aload_0         /* this */
        //   638: getfield        org/jboss/netty/handler/ssl/SslHandler.pendingUnencryptedWritesLock:Lorg/jboss/netty/util/internal/NonReentrantLock;
        //   641: invokevirtual   org/jboss/netty/util/internal/NonReentrantLock.lock:()V
        //   644: aload_0         /* this */
        //   645: getfield        org/jboss/netty/handler/ssl/SslHandler.pendingUnencryptedWrites:Ljava/util/Queue;
        //   648: invokeinterface java/util/Queue.poll:()Ljava/lang/Object;
        //   653: checkcast       Lorg/jboss/netty/handler/ssl/SslHandler$PendingWrite;
        //   656: astore          pendingWrite
        //   658: aload           pendingWrite
        //   660: ifnonnull       669
        //   663: jsr             683
        //   666: goto            710
        //   669: jsr             683
        //   672: goto            694
        //   675: astore          21
        //   677: jsr             683
        //   680: aload           21
        //   682: athrow         
        //   683: astore          22
        //   685: aload_0         /* this */
        //   686: getfield        org/jboss/netty/handler/ssl/SslHandler.pendingUnencryptedWritesLock:Lorg/jboss/netty/util/internal/NonReentrantLock;
        //   689: invokevirtual   org/jboss/netty/util/internal/NonReentrantLock.unlock:()V
        //   692: ret             22
        //   694: aload           pendingWrite
        //   696: getfield        org/jboss/netty/handler/ssl/SslHandler$PendingWrite.future:Lorg/jboss/netty/channel/ChannelFuture;
        //   699: aload           cause
        //   701: invokeinterface org/jboss/netty/channel/ChannelFuture.setFailure:(Ljava/lang/Throwable;)Z
        //   706: pop            
        //   707: goto            637
        //   710: ret             19
        //   712: iload           needsUnwrap
        //   714: ifeq            727
        //   717: aload_0         /* this */
        //   718: aload_0         /* this */
        //   719: getfield        org/jboss/netty/handler/ssl/SslHandler.ctx:Lorg/jboss/netty/channel/ChannelHandlerContext;
        //   722: aload_2         /* channel */
        //   723: iconst_1       
        //   724: invokespecial   org/jboss/netty/handler/ssl/SslHandler.unwrapNonAppData:(Lorg/jboss/netty/channel/ChannelHandlerContext;Lorg/jboss/netty/channel/Channel;Z)V
        //   727: return         
        //    Exceptions:
        //  throws javax.net.ssl.SSLException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                        
        //  -----  -----  -----  -----  ----------------------------
        //  118    134    137    145    Any
        //  137    142    137    145    Any
        //  115    304    498    506    Any
        //  310    382    498    506    Any
        //  388    394    498    506    Any
        //  400    429    498    506    Any
        //  435    458    498    506    Any
        //  464    495    498    506    Any
        //  498    503    498    506    Any
        //  28     50     512    520    Any
        //  53     307    512    520    Any
        //  310    385    512    520    Any
        //  388    397    512    520    Any
        //  400    432    512    520    Any
        //  435    461    512    520    Any
        //  464    509    512    520    Any
        //  512    517    512    520    Any
        //  21     534    540    555    Ljavax/net/ssl/SSLException;
        //  21     537    555    563    Any
        //  540    560    555    563    Any
        //  644    666    675    683    Any
        //  669    672    675    683    Any
        //  675    680    675    683    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Inconsistent stack size at #0951 (coming from #0929).
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2183)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void offerEncryptedWriteRequest(final MessageEvent encryptedWrite) {
        final boolean locked = this.pendingEncryptedWritesLock.tryLock();
        try {
            this.pendingEncryptedWrites.add(encryptedWrite);
        }
        finally {
            if (locked) {
                this.pendingEncryptedWritesLock.unlock();
            }
        }
    }
    
    private void flushPendingEncryptedWrites(final ChannelHandlerContext ctx) {
        while (!this.pendingEncryptedWrites.isEmpty()) {
            if (!this.pendingEncryptedWritesLock.tryLock()) {
                return;
            }
            try {
                MessageEvent e;
                while ((e = this.pendingEncryptedWrites.poll()) != null) {
                    ctx.sendDownstream(e);
                }
            }
            finally {
                this.pendingEncryptedWritesLock.unlock();
            }
        }
    }
    
    private ChannelFuture wrapNonAppData(final ChannelHandlerContext ctx, final Channel channel) throws SSLException {
        ChannelFuture future = null;
        final ByteBuffer outNetBuf = this.bufferPool.acquireBuffer();
        try {
            SSLEngineResult result;
            do {
                synchronized (this.handshakeLock) {
                    result = this.engine.wrap(SslHandler.EMPTY_BUFFER, outNetBuf);
                }
                if (result.bytesProduced() > 0) {
                    outNetBuf.flip();
                    final ChannelBuffer msg = ctx.getChannel().getConfig().getBufferFactory().getBuffer(outNetBuf.remaining());
                    msg.writeBytes(outNetBuf);
                    outNetBuf.clear();
                    future = Channels.future(channel);
                    future.addListener(new ChannelFutureListener() {
                        public void operationComplete(final ChannelFuture future) throws Exception {
                            if (future.getCause() instanceof ClosedChannelException) {
                                synchronized (SslHandler.this.ignoreClosedChannelExceptionLock) {
                                    final SslHandler this$0 = SslHandler.this;
                                    ++this$0.ignoreClosedChannelException;
                                }
                            }
                        }
                    });
                    Channels.write(ctx, future, msg);
                }
                final SSLEngineResult.HandshakeStatus handshakeStatus = result.getHandshakeStatus();
                this.handleRenegotiation(handshakeStatus);
                switch (SslHandler$7.$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[handshakeStatus.ordinal()]) {
                    case 3: {
                        this.setHandshakeSuccess(channel);
                        this.runDelegatedTasks();
                        break;
                    }
                    case 2: {
                        this.runDelegatedTasks();
                        break;
                    }
                    case 1: {
                        if (!Thread.holdsLock(this.handshakeLock)) {
                            this.unwrapNonAppData(ctx, channel, true);
                            break;
                        }
                        break;
                    }
                    case 4: {
                        if (this.setHandshakeSuccessIfStillHandshaking(channel)) {
                            this.runDelegatedTasks();
                            break;
                        }
                        break;
                    }
                    case 0: {
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unexpected handshake status: " + handshakeStatus);
                    }
                }
            } while (result.bytesProduced() != 0);
        }
        catch (SSLException e) {
            this.setHandshakeFailure(channel, e);
            throw e;
        }
        finally {
            this.bufferPool.releaseBuffer(outNetBuf);
        }
        if (future == null) {
            future = Channels.succeededFuture(channel);
        }
        return future;
    }
    
    private void unwrapNonAppData(final ChannelHandlerContext ctx, final Channel channel, final boolean mightNeedHandshake) throws SSLException {
        this.unwrap(ctx, channel, SslHandler.EMPTY_BUFFER, -1, mightNeedHandshake);
    }
    
    private ChannelBuffer unwrap(final ChannelHandlerContext ctx, final Channel channel, final ByteBuffer nioInNetBuf, final int initialNettyOutAppBufCapacity, final boolean mightNeedHandshake) throws SSLException {
        final int nioInNetBufStartOffset = nioInNetBuf.position();
        final ByteBuffer nioOutAppBuf = this.bufferPool.acquireBuffer();
        ChannelBuffer nettyOutAppBuf = null;
        try {
            boolean needsWrap = false;
            while (true) {
                boolean needsHandshake = false;
                if (mightNeedHandshake) {
                    synchronized (this.handshakeLock) {
                        if (!this.handshaken && !this.handshaking && !this.engine.getUseClientMode() && !this.engine.isInboundDone() && !this.engine.isOutboundDone()) {
                            needsHandshake = true;
                        }
                    }
                }
                if (needsHandshake) {
                    this.handshake();
                }
                synchronized (this.handshakeLock) {
                    SSLEngineResult result;
                    while (true) {
                        final int outAppBufSize = this.engine.getSession().getApplicationBufferSize();
                        ByteBuffer outAppBuf;
                        if (nioOutAppBuf.capacity() < outAppBufSize) {
                            outAppBuf = ByteBuffer.allocate(outAppBufSize);
                        }
                        else {
                            outAppBuf = nioOutAppBuf;
                        }
                        try {
                            result = this.engine.unwrap(nioInNetBuf, outAppBuf);
                            switch (result.getStatus()) {
                                case CLOSED: {
                                    this.sslEngineCloseFuture.setClosed();
                                    break;
                                }
                                case BUFFER_OVERFLOW: {
                                    continue;
                                }
                            }
                        }
                        finally {
                            outAppBuf.flip();
                            if (outAppBuf.hasRemaining()) {
                                if (nettyOutAppBuf == null) {
                                    final ChannelBufferFactory factory = ctx.getChannel().getConfig().getBufferFactory();
                                    nettyOutAppBuf = factory.getBuffer(initialNettyOutAppBufCapacity);
                                }
                                nettyOutAppBuf.writeBytes(outAppBuf);
                            }
                            outAppBuf.clear();
                        }
                        break;
                    }
                    final SSLEngineResult.HandshakeStatus handshakeStatus = result.getHandshakeStatus();
                    this.handleRenegotiation(handshakeStatus);
                    switch (SslHandler$7.$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[handshakeStatus.ordinal()]) {
                        case 1: {
                            break;
                        }
                        case 0: {
                            this.wrapNonAppData(ctx, channel);
                            break;
                        }
                        case 2: {
                            this.runDelegatedTasks();
                            break;
                        }
                        case 3: {
                            this.setHandshakeSuccess(channel);
                            needsWrap = true;
                            continue;
                        }
                        case 4: {
                            if (this.setHandshakeSuccessIfStillHandshaking(channel)) {
                                needsWrap = true;
                                continue;
                            }
                            if (this.writeBeforeHandshakeDone) {
                                this.writeBeforeHandshakeDone = false;
                                needsWrap = true;
                                break;
                            }
                            break;
                        }
                        default: {
                            throw new IllegalStateException("Unknown handshake status: " + handshakeStatus);
                        }
                    }
                    if (result.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW || (result.bytesConsumed() == 0 && result.bytesProduced() == 0)) {
                        if (nioInNetBuf.hasRemaining() && !this.engine.isInboundDone()) {
                            SslHandler.logger.warn("Unexpected leftover data after SSLEngine.unwrap(): status=" + result.getStatus() + " handshakeStatus=" + result.getHandshakeStatus() + " consumed=" + result.bytesConsumed() + " produced=" + result.bytesProduced() + " remaining=" + nioInNetBuf.remaining() + " data=" + ChannelBuffers.hexDump(ChannelBuffers.wrappedBuffer(nioInNetBuf)));
                        }
                        break;
                    }
                    continue;
                }
            }
            if (needsWrap && !Thread.holdsLock(this.handshakeLock) && !this.pendingEncryptedWritesLock.isHeldByCurrentThread()) {
                this.wrap(ctx, channel);
            }
        }
        catch (SSLException e) {
            this.setHandshakeFailure(channel, e);
            throw e;
        }
        finally {
            this.bufferPool.releaseBuffer(nioOutAppBuf);
        }
        if (nettyOutAppBuf != null && nettyOutAppBuf.readable()) {
            return nettyOutAppBuf;
        }
        return null;
    }
    
    private void handleRenegotiation(final SSLEngineResult.HandshakeStatus handshakeStatus) {
        synchronized (this.handshakeLock) {
            if (handshakeStatus == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING || handshakeStatus == SSLEngineResult.HandshakeStatus.FINISHED) {
                return;
            }
            if (!this.handshaken) {
                return;
            }
            if (this.handshaking) {
                return;
            }
            if (this.engine.isInboundDone() || this.engine.isOutboundDone()) {
                return;
            }
            boolean renegotiate;
            if (this.isEnableRenegotiation()) {
                renegotiate = true;
            }
            else {
                renegotiate = false;
                this.handshaking = true;
            }
            if (renegotiate) {
                this.handshake();
            }
            else {
                Channels.fireExceptionCaught(this.ctx, new SSLException("renegotiation attempted by peer; closing the connection"));
                Channels.close(this.ctx, Channels.succeededFuture(this.ctx.getChannel()));
            }
        }
    }
    
    private void runDelegatedTasks() {
        while (true) {
            final Runnable task;
            synchronized (this.handshakeLock) {
                task = this.engine.getDelegatedTask();
            }
            if (task == null) {
                break;
            }
            task.run();
        }
    }
    
    private boolean setHandshakeSuccessIfStillHandshaking(final Channel channel) {
        if (this.handshaking && !this.handshakeFuture.isDone()) {
            this.setHandshakeSuccess(channel);
            return true;
        }
        return false;
    }
    
    private void setHandshakeSuccess(final Channel channel) {
        synchronized (this.handshakeLock) {
            this.handshaking = false;
            this.handshaken = true;
            if (this.handshakeFuture == null) {
                this.handshakeFuture = Channels.future(channel);
            }
            this.cancelHandshakeTimeout();
        }
        if (SslHandler.logger.isDebugEnabled()) {
            SslHandler.logger.debug(channel + " HANDSHAKEN: " + this.engine.getSession().getCipherSuite());
        }
        this.handshakeFuture.setSuccess();
    }
    
    private void setHandshakeFailure(final Channel channel, final SSLException cause) {
        synchronized (this.handshakeLock) {
            if (!this.handshaking) {
                return;
            }
            this.handshaking = false;
            this.handshaken = false;
            if (this.handshakeFuture == null) {
                this.handshakeFuture = Channels.future(channel);
            }
            this.cancelHandshakeTimeout();
            this.engine.closeOutbound();
            try {
                this.engine.closeInbound();
            }
            catch (SSLException e) {
                if (SslHandler.logger.isDebugEnabled()) {
                    SslHandler.logger.debug("SSLEngine.closeInbound() raised an exception after a handshake failure.", e);
                }
            }
        }
        this.handshakeFuture.setFailure(cause);
        if (this.closeOnSslException) {
            Channels.close(this.ctx, Channels.future(channel));
        }
    }
    
    private void closeOutboundAndChannel(final ChannelHandlerContext context, final ChannelStateEvent e) {
        if (!e.getChannel().isConnected()) {
            context.sendDownstream(e);
            return;
        }
        if (!SslHandler.CLOSED_OUTBOUND_AND_CHANNEL_UPDATER.compareAndSet(this, 0, 1)) {
            e.getChannel().getCloseFuture().addListener(new ChannelFutureListener() {
                public void operationComplete(final ChannelFuture future) throws Exception {
                    context.sendDownstream(e);
                }
            });
            return;
        }
        boolean passthrough = true;
        try {
            try {
                this.unwrapNonAppData(this.ctx, e.getChannel(), false);
            }
            catch (SSLException ex) {
                if (SslHandler.logger.isDebugEnabled()) {
                    SslHandler.logger.debug("Failed to unwrap before sending a close_notify message", ex);
                }
            }
            if (!this.engine.isOutboundDone() && SslHandler.SENT_CLOSE_NOTIFY_UPDATER.compareAndSet(this, 0, 1)) {
                this.engine.closeOutbound();
                try {
                    final ChannelFuture closeNotifyFuture = this.wrapNonAppData(context, e.getChannel());
                    closeNotifyFuture.addListener(new ClosingChannelFutureListener(context, e));
                    passthrough = false;
                }
                catch (SSLException ex) {
                    if (SslHandler.logger.isDebugEnabled()) {
                        SslHandler.logger.debug("Failed to encode a close_notify message", ex);
                    }
                }
            }
        }
        finally {
            if (passthrough) {
                context.sendDownstream(e);
            }
        }
    }
    
    @Override
    public void beforeAdd(final ChannelHandlerContext ctx) throws Exception {
        super.beforeAdd(ctx);
        this.ctx = ctx;
    }
    
    @Override
    public void afterRemove(final ChannelHandlerContext ctx) throws Exception {
        this.closeEngine();
        Throwable cause = null;
        while (true) {
            final PendingWrite pw = this.pendingUnencryptedWrites.poll();
            if (pw == null) {
                break;
            }
            if (cause == null) {
                cause = new IOException("Unable to write data");
            }
            pw.future.setFailure(cause);
        }
        while (true) {
            final MessageEvent ev = this.pendingEncryptedWrites.poll();
            if (ev == null) {
                break;
            }
            if (cause == null) {
                cause = new IOException("Unable to write data");
            }
            ev.getFuture().setFailure(cause);
        }
        if (cause != null) {
            Channels.fireExceptionCaughtLater(ctx, cause);
        }
    }
    
    @Override
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        if (this.issueHandshake) {
            this.handshake().addListener(new ChannelFutureListener() {
                public void operationComplete(final ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        ctx.sendUpstream(e);
                    }
                }
            });
        }
        else {
            super.channelConnected(ctx, e);
        }
    }
    
    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        ctx.getPipeline().execute(new Runnable() {
            public void run() {
                if (!SslHandler.this.pendingUnencryptedWritesLock.tryLock()) {
                    return;
                }
                List<ChannelFuture> futures = null;
                try {
                    while (true) {
                        final PendingWrite pw = SslHandler.this.pendingUnencryptedWrites.poll();
                        if (pw == null) {
                            break;
                        }
                        if (futures == null) {
                            futures = new ArrayList<ChannelFuture>();
                        }
                        futures.add(pw.future);
                    }
                    while (true) {
                        final MessageEvent ev = SslHandler.this.pendingEncryptedWrites.poll();
                        if (ev == null) {
                            break;
                        }
                        if (futures == null) {
                            futures = new ArrayList<ChannelFuture>();
                        }
                        futures.add(ev.getFuture());
                    }
                }
                finally {
                    SslHandler.this.pendingUnencryptedWritesLock.unlock();
                }
                if (futures != null) {
                    final ClosedChannelException cause = new ClosedChannelException();
                    for (int size = futures.size(), i = 0; i < size; ++i) {
                        futures.get(i).setFailure(cause);
                    }
                    Channels.fireExceptionCaught(ctx, cause);
                }
            }
        });
        super.channelClosed(ctx, e);
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(SslHandler.class);
        EMPTY_BUFFER = ByteBuffer.allocate(0);
        IGNORABLE_CLASS_IN_STACK = Pattern.compile("^.*(?:Socket|Datagram|Sctp|Udt)Channel.*$");
        IGNORABLE_ERROR_MESSAGE = Pattern.compile("^.*(?:connection.*(?:reset|closed|abort|broken)|broken.*pipe).*$", 2);
        SENT_FIRST_MESSAGE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(SslHandler.class, "sentFirstMessage");
        SENT_CLOSE_NOTIFY_UPDATER = AtomicIntegerFieldUpdater.newUpdater(SslHandler.class, "sentCloseNotify");
        CLOSED_OUTBOUND_AND_CHANNEL_UPDATER = AtomicIntegerFieldUpdater.newUpdater(SslHandler.class, "closedOutboundAndChannel");
    }
    
    private static final class PendingWrite
    {
        final ChannelFuture future;
        final ByteBuffer outAppBuf;
        
        PendingWrite(final ChannelFuture future, final ByteBuffer outAppBuf) {
            this.future = future;
            this.outAppBuf = outAppBuf;
        }
    }
    
    private static final class ClosingChannelFutureListener implements ChannelFutureListener
    {
        private final ChannelHandlerContext context;
        private final ChannelStateEvent e;
        
        ClosingChannelFutureListener(final ChannelHandlerContext context, final ChannelStateEvent e) {
            this.context = context;
            this.e = e;
        }
        
        public void operationComplete(final ChannelFuture closeNotifyFuture) throws Exception {
            if (!(closeNotifyFuture.getCause() instanceof ClosedChannelException)) {
                Channels.close(this.context, this.e.getFuture());
            }
            else {
                this.e.getFuture().setSuccess();
            }
        }
    }
    
    private final class SSLEngineInboundCloseFuture extends DefaultChannelFuture
    {
        SSLEngineInboundCloseFuture() {
            super(null, true);
        }
        
        void setClosed() {
            super.setSuccess();
        }
        
        @Override
        public Channel getChannel() {
            if (SslHandler.this.ctx == null) {
                return null;
            }
            return SslHandler.this.ctx.getChannel();
        }
        
        @Override
        public boolean setSuccess() {
            return false;
        }
        
        @Override
        public boolean setFailure(final Throwable cause) {
            return false;
        }
    }
}
