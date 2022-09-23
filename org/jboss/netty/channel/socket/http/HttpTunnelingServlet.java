// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.http;

import org.jboss.netty.channel.ExceptionEvent;
import java.io.OutputStream;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.buffer.ChannelBuffers;
import java.io.IOException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channel;
import javax.servlet.ServletOutputStream;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFutureListener;
import java.io.EOFException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.Channels;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.jboss.netty.channel.local.LocalAddress;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import org.jboss.netty.channel.ChannelFactory;
import java.net.SocketAddress;
import org.jboss.netty.logging.InternalLogger;
import javax.servlet.http.HttpServlet;

public class HttpTunnelingServlet extends HttpServlet
{
    private static final long serialVersionUID = 4259910275899756070L;
    private static final String ENDPOINT = "endpoint";
    private static final String CONNECT_ATTEMPTS = "connectAttempts";
    private static final String RETRY_DELAY = "retryDelay";
    static final InternalLogger logger;
    private volatile SocketAddress remoteAddress;
    private volatile ChannelFactory channelFactory;
    private volatile long connectAttempts;
    private volatile long retryDelay;
    
    public HttpTunnelingServlet() {
        this.connectAttempts = 1L;
    }
    
    @Override
    public void init() throws ServletException {
        final ServletConfig config = this.getServletConfig();
        final String endpoint = config.getInitParameter("endpoint");
        if (endpoint == null) {
            throw new ServletException("init-param 'endpoint' must be specified.");
        }
        try {
            this.remoteAddress = this.parseEndpoint(endpoint.trim());
        }
        catch (ServletException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new ServletException("Failed to parse an endpoint.", e2);
        }
        try {
            this.channelFactory = this.createChannelFactory(this.remoteAddress);
        }
        catch (ServletException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new ServletException("Failed to create a channel factory.", e2);
        }
        String temp = config.getInitParameter("connectAttempts");
        if (temp != null) {
            try {
                this.connectAttempts = Long.parseLong(temp);
            }
            catch (NumberFormatException e3) {
                throw new ServletException("init-param 'connectAttempts' is not a valid number. Actual value: " + temp);
            }
            if (this.connectAttempts < 1L) {
                throw new ServletException("init-param 'connectAttempts' must be >= 1. Actual value: " + this.connectAttempts);
            }
        }
        temp = config.getInitParameter("retryDelay");
        if (temp != null) {
            try {
                this.retryDelay = Long.parseLong(temp);
            }
            catch (NumberFormatException e3) {
                throw new ServletException("init-param 'retryDelay' is not a valid number. Actual value: " + temp);
            }
            if (this.retryDelay < 0L) {
                throw new ServletException("init-param 'retryDelay' must be >= 0. Actual value: " + this.retryDelay);
            }
        }
    }
    
    protected SocketAddress parseEndpoint(final String endpoint) throws Exception {
        if (endpoint.startsWith("local:")) {
            return new LocalAddress(endpoint.substring(6).trim());
        }
        throw new ServletException("Invalid or unknown endpoint: " + endpoint);
    }
    
    protected ChannelFactory createChannelFactory(final SocketAddress remoteAddress) throws Exception {
        if (remoteAddress instanceof LocalAddress) {
            return new DefaultLocalClientChannelFactory();
        }
        throw new ServletException("Unsupported remote address type: " + remoteAddress.getClass().getName());
    }
    
    @Override
    public void destroy() {
        try {
            this.destroyChannelFactory(this.channelFactory);
        }
        catch (Exception e) {
            if (HttpTunnelingServlet.logger.isWarnEnabled()) {
                HttpTunnelingServlet.logger.warn("Failed to destroy a channel factory.", e);
            }
        }
    }
    
    protected void destroyChannelFactory(final ChannelFactory factory) throws Exception {
        factory.releaseExternalResources();
    }
    
    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        if (!"POST".equalsIgnoreCase(req.getMethod())) {
            if (HttpTunnelingServlet.logger.isWarnEnabled()) {
                HttpTunnelingServlet.logger.warn("Unallowed method: " + req.getMethod());
            }
            res.sendError(405);
            return;
        }
        final ChannelPipeline pipeline = Channels.pipeline();
        final ServletOutputStream out = res.getOutputStream();
        final OutboundConnectionHandler handler = new OutboundConnectionHandler(out);
        pipeline.addLast("handler", handler);
        final Channel channel = this.channelFactory.newChannel(pipeline);
        int tries = 0;
        ChannelFuture future = null;
        while (tries < this.connectAttempts) {
            future = channel.connect(this.remoteAddress).awaitUninterruptibly();
            if (future.isSuccess()) {
                break;
            }
            ++tries;
            try {
                Thread.sleep(this.retryDelay);
            }
            catch (InterruptedException e) {}
        }
        if (!future.isSuccess()) {
            if (HttpTunnelingServlet.logger.isWarnEnabled()) {
                final Throwable cause = future.getCause();
                HttpTunnelingServlet.logger.warn("Endpoint unavailable: " + cause.getMessage(), cause);
            }
            res.sendError(503);
            return;
        }
        ChannelFuture lastWriteFuture = null;
        try {
            res.setStatus(200);
            res.setHeader("Content-Type", "application/octet-stream");
            res.setHeader("Content-Transfer-Encoding", "binary");
            out.flush();
            final PushbackInputStream in = new PushbackInputStream(req.getInputStream());
            while (channel.isConnected()) {
                ChannelBuffer buffer;
                try {
                    buffer = read(in);
                }
                catch (EOFException e2) {
                    break;
                }
                if (buffer == null) {
                    break;
                }
                lastWriteFuture = channel.write(buffer);
            }
        }
        finally {
            if (lastWriteFuture == null) {
                channel.close();
            }
            else {
                lastWriteFuture.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }
    
    private static ChannelBuffer read(final PushbackInputStream in) throws IOException {
        int bytesToRead = in.available();
        byte[] buf;
        int readBytes;
        if (bytesToRead > 0) {
            buf = new byte[bytesToRead];
            readBytes = in.read(buf);
        }
        else {
            if (bytesToRead != 0) {
                return null;
            }
            final int b = in.read();
            if (b < 0 || in.available() < 0) {
                return null;
            }
            in.unread(b);
            bytesToRead = in.available();
            buf = new byte[bytesToRead];
            readBytes = in.read(buf);
        }
        assert readBytes > 0;
        ChannelBuffer buffer;
        if (readBytes == buf.length) {
            buffer = ChannelBuffers.wrappedBuffer(buf);
        }
        else {
            buffer = ChannelBuffers.wrappedBuffer(buf, 0, readBytes);
        }
        return buffer;
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(HttpTunnelingServlet.class);
    }
    
    private static final class OutboundConnectionHandler extends SimpleChannelUpstreamHandler
    {
        private final ServletOutputStream out;
        
        public OutboundConnectionHandler(final ServletOutputStream out) {
            this.out = out;
        }
        
        @Override
        public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
            final ChannelBuffer buffer = (ChannelBuffer)e.getMessage();
            synchronized (this) {
                buffer.readBytes(this.out, buffer.readableBytes());
                this.out.flush();
            }
        }
        
        @Override
        public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
            if (HttpTunnelingServlet.logger.isWarnEnabled()) {
                HttpTunnelingServlet.logger.warn("Unexpected exception while HTTP tunneling", e.getCause());
            }
            e.getChannel().close();
        }
    }
}
