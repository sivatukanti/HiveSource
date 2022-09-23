// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.http;

import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.SocketChannelConfig;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.DefaultHttpChunk;
import org.jboss.netty.buffer.ChannelBuffers;
import java.nio.channels.NotYetConnectedException;
import org.jboss.netty.buffer.ChannelBuffer;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLContext;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelFuture;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.SocketChannel;
import org.jboss.netty.channel.AbstractChannel;

class HttpTunnelingClientSocketChannel extends AbstractChannel implements SocketChannel
{
    final HttpTunnelingSocketChannelConfig config;
    volatile boolean requestHeaderWritten;
    final Object interestOpsLock;
    final SocketChannel realChannel;
    private final ServletChannelHandler handler;
    
    HttpTunnelingClientSocketChannel(final ChannelFactory factory, final ChannelPipeline pipeline, final ChannelSink sink, final ClientSocketChannelFactory clientSocketChannelFactory) {
        super(null, factory, pipeline, sink);
        this.interestOpsLock = new Object();
        this.handler = new ServletChannelHandler();
        this.config = new HttpTunnelingSocketChannelConfig(this);
        final DefaultChannelPipeline channelPipeline = new DefaultChannelPipeline();
        channelPipeline.addLast("decoder", new HttpResponseDecoder());
        channelPipeline.addLast("encoder", new HttpRequestEncoder());
        channelPipeline.addLast("handler", this.handler);
        this.realChannel = clientSocketChannelFactory.newChannel((ChannelPipeline)channelPipeline);
        Channels.fireChannelOpen(this);
    }
    
    public HttpTunnelingSocketChannelConfig getConfig() {
        return this.config;
    }
    
    public InetSocketAddress getLocalAddress() {
        return this.realChannel.getLocalAddress();
    }
    
    public InetSocketAddress getRemoteAddress() {
        return this.realChannel.getRemoteAddress();
    }
    
    public boolean isBound() {
        return this.realChannel.isBound();
    }
    
    public boolean isConnected() {
        return this.realChannel.isConnected();
    }
    
    @Override
    public int getInterestOps() {
        return this.realChannel.getInterestOps();
    }
    
    @Override
    public boolean isWritable() {
        return this.realChannel.isWritable();
    }
    
    @Override
    protected boolean setClosed() {
        return super.setClosed();
    }
    
    @Override
    public ChannelFuture write(final Object message, final SocketAddress remoteAddress) {
        if (remoteAddress == null || remoteAddress.equals(this.getRemoteAddress())) {
            return super.write(message, null);
        }
        return this.getUnsupportedOperationFuture();
    }
    
    void bindReal(final SocketAddress localAddress, final ChannelFuture future) {
        this.realChannel.bind(localAddress).addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture f) {
                if (f.isSuccess()) {
                    future.setSuccess();
                }
                else {
                    future.setFailure(f.getCause());
                }
            }
        });
    }
    
    void connectReal(final SocketAddress remoteAddress, final ChannelFuture future) {
        final SocketChannel virtualChannel = this;
        this.realChannel.connect(remoteAddress).addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture f) {
                final String serverName = HttpTunnelingClientSocketChannel.this.config.getServerName();
                final int serverPort = ((InetSocketAddress)remoteAddress).getPort();
                final String serverPath = HttpTunnelingClientSocketChannel.this.config.getServerPath();
                if (f.isSuccess()) {
                    final SSLContext sslContext = HttpTunnelingClientSocketChannel.this.config.getSslContext();
                    ChannelFuture sslHandshakeFuture = null;
                    if (sslContext != null) {
                        SSLEngine engine;
                        if (serverName != null) {
                            engine = sslContext.createSSLEngine(serverName, serverPort);
                        }
                        else {
                            engine = sslContext.createSSLEngine();
                        }
                        engine.setUseClientMode(true);
                        engine.setEnableSessionCreation(HttpTunnelingClientSocketChannel.this.config.isEnableSslSessionCreation());
                        final String[] enabledCipherSuites = HttpTunnelingClientSocketChannel.this.config.getEnabledSslCipherSuites();
                        if (enabledCipherSuites != null) {
                            engine.setEnabledCipherSuites(enabledCipherSuites);
                        }
                        final String[] enabledProtocols = HttpTunnelingClientSocketChannel.this.config.getEnabledSslProtocols();
                        if (enabledProtocols != null) {
                            engine.setEnabledProtocols(enabledProtocols);
                        }
                        final SslHandler sslHandler = new SslHandler(engine);
                        HttpTunnelingClientSocketChannel.this.realChannel.getPipeline().addFirst("ssl", sslHandler);
                        sslHandshakeFuture = sslHandler.handshake();
                    }
                    final HttpRequest req = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, serverPath);
                    if (serverName != null) {
                        req.headers().set("Host", serverName);
                    }
                    req.headers().set("Content-Type", "application/octet-stream");
                    req.headers().set("Transfer-Encoding", "chunked");
                    req.headers().set("Content-Transfer-Encoding", "binary");
                    req.headers().set("User-Agent", HttpTunnelingClientSocketChannel.class.getName());
                    if (sslHandshakeFuture == null) {
                        HttpTunnelingClientSocketChannel.this.realChannel.write(req);
                        HttpTunnelingClientSocketChannel.this.requestHeaderWritten = true;
                        future.setSuccess();
                        Channels.fireChannelConnected(virtualChannel, remoteAddress);
                    }
                    else {
                        sslHandshakeFuture.addListener(new ChannelFutureListener() {
                            public void operationComplete(final ChannelFuture f) {
                                if (f.isSuccess()) {
                                    HttpTunnelingClientSocketChannel.this.realChannel.write(req);
                                    HttpTunnelingClientSocketChannel.this.requestHeaderWritten = true;
                                    future.setSuccess();
                                    Channels.fireChannelConnected(virtualChannel, remoteAddress);
                                }
                                else {
                                    future.setFailure(f.getCause());
                                    Channels.fireExceptionCaught(virtualChannel, f.getCause());
                                }
                            }
                        });
                    }
                }
                else {
                    future.setFailure(f.getCause());
                    Channels.fireExceptionCaught(virtualChannel, f.getCause());
                }
            }
        });
    }
    
    void writeReal(final ChannelBuffer a, final ChannelFuture future) {
        if (!this.requestHeaderWritten) {
            throw new NotYetConnectedException();
        }
        final int size = a.readableBytes();
        ChannelFuture f;
        if (size == 0) {
            f = this.realChannel.write(ChannelBuffers.EMPTY_BUFFER);
        }
        else {
            f = this.realChannel.write(new DefaultHttpChunk(a));
        }
        f.addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture f) {
                if (f.isSuccess()) {
                    future.setSuccess();
                    if (size != 0) {
                        Channels.fireWriteComplete(HttpTunnelingClientSocketChannel.this, size);
                    }
                }
                else {
                    future.setFailure(f.getCause());
                }
            }
        });
    }
    
    private ChannelFuture writeLastChunk() {
        if (!this.requestHeaderWritten) {
            return Channels.failedFuture(this, new NotYetConnectedException());
        }
        return this.realChannel.write(HttpChunk.LAST_CHUNK);
    }
    
    void setInterestOpsReal(final int interestOps, final ChannelFuture future) {
        this.realChannel.setInterestOps(interestOps).addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture f) {
                if (f.isSuccess()) {
                    future.setSuccess();
                }
                else {
                    future.setFailure(f.getCause());
                }
            }
        });
    }
    
    void disconnectReal(final ChannelFuture future) {
        this.writeLastChunk().addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture f) {
                HttpTunnelingClientSocketChannel.this.realChannel.disconnect().addListener(new ChannelFutureListener() {
                    public void operationComplete(final ChannelFuture f) {
                        if (f.isSuccess()) {
                            future.setSuccess();
                        }
                        else {
                            future.setFailure(f.getCause());
                        }
                    }
                });
            }
        });
    }
    
    void unbindReal(final ChannelFuture future) {
        this.writeLastChunk().addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture f) {
                HttpTunnelingClientSocketChannel.this.realChannel.unbind().addListener(new ChannelFutureListener() {
                    public void operationComplete(final ChannelFuture f) {
                        if (f.isSuccess()) {
                            future.setSuccess();
                        }
                        else {
                            future.setFailure(f.getCause());
                        }
                    }
                });
            }
        });
    }
    
    void closeReal(final ChannelFuture future) {
        this.writeLastChunk().addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture f) {
                HttpTunnelingClientSocketChannel.this.realChannel.close().addListener(new ChannelFutureListener() {
                    public void operationComplete(final ChannelFuture f) {
                        if (f.isSuccess()) {
                            future.setSuccess();
                        }
                        else {
                            future.setFailure(f.getCause());
                        }
                        HttpTunnelingClientSocketChannel.this.setClosed();
                    }
                });
            }
        });
    }
    
    final class ServletChannelHandler extends SimpleChannelUpstreamHandler
    {
        private volatile boolean readingChunks;
        final SocketChannel virtualChannel;
        
        ServletChannelHandler() {
            this.virtualChannel = HttpTunnelingClientSocketChannel.this;
        }
        
        @Override
        public void channelBound(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
            Channels.fireChannelBound(this.virtualChannel, (SocketAddress)e.getValue());
        }
        
        @Override
        public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
            if (!this.readingChunks) {
                final HttpResponse res = (HttpResponse)e.getMessage();
                if (res.getStatus().getCode() != HttpResponseStatus.OK.getCode()) {
                    throw new ChannelException("Unexpected HTTP response status: " + res.getStatus());
                }
                if (res.isChunked()) {
                    this.readingChunks = true;
                }
                else {
                    final ChannelBuffer content = res.getContent();
                    if (content.readable()) {
                        Channels.fireMessageReceived(HttpTunnelingClientSocketChannel.this, content);
                    }
                    HttpTunnelingClientSocketChannel.this.closeReal(Channels.succeededFuture(this.virtualChannel));
                }
            }
            else {
                final HttpChunk chunk = (HttpChunk)e.getMessage();
                if (!chunk.isLast()) {
                    Channels.fireMessageReceived(HttpTunnelingClientSocketChannel.this, chunk.getContent());
                }
                else {
                    this.readingChunks = false;
                    HttpTunnelingClientSocketChannel.this.closeReal(Channels.succeededFuture(this.virtualChannel));
                }
            }
        }
        
        @Override
        public void channelInterestChanged(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
            Channels.fireChannelInterestChanged(this.virtualChannel);
        }
        
        @Override
        public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
            Channels.fireChannelDisconnected(this.virtualChannel);
        }
        
        @Override
        public void channelUnbound(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
            Channels.fireChannelUnbound(this.virtualChannel);
        }
        
        @Override
        public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
            Channels.fireChannelClosed(this.virtualChannel);
        }
        
        @Override
        public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
            Channels.fireExceptionCaught(this.virtualChannel, e.getCause());
            HttpTunnelingClientSocketChannel.this.realChannel.close();
        }
    }
}
