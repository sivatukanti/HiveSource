// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.ByteArrayOutputStream2;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpParser;
import java.util.concurrent.CountDownLatch;
import org.eclipse.jetty.io.ByteArrayEndPoint;
import java.io.IOException;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.BufferUtil;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import java.util.concurrent.LinkedBlockingQueue;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.thread.Scheduler;
import java.util.concurrent.Executor;
import java.util.concurrent.BlockingQueue;

public class LocalConnector extends AbstractConnector
{
    private final BlockingQueue<LocalEndPoint> _connects;
    
    public LocalConnector(final Server server, final Executor executor, final Scheduler scheduler, final ByteBufferPool pool, final int acceptors, final ConnectionFactory... factories) {
        super(server, executor, scheduler, pool, acceptors, factories);
        this._connects = new LinkedBlockingQueue<LocalEndPoint>();
        this.setIdleTimeout(30000L);
    }
    
    public LocalConnector(final Server server) {
        this(server, null, null, null, -1, new ConnectionFactory[] { new HttpConnectionFactory() });
    }
    
    public LocalConnector(final Server server, final SslContextFactory sslContextFactory) {
        this(server, null, null, null, -1, AbstractConnectionFactory.getFactories(sslContextFactory, new HttpConnectionFactory()));
    }
    
    public LocalConnector(final Server server, final ConnectionFactory connectionFactory) {
        this(server, null, null, null, -1, new ConnectionFactory[] { connectionFactory });
    }
    
    public LocalConnector(final Server server, final ConnectionFactory connectionFactory, final SslContextFactory sslContextFactory) {
        this(server, null, null, null, -1, AbstractConnectionFactory.getFactories(sslContextFactory, connectionFactory));
    }
    
    @Override
    public Object getTransport() {
        return this;
    }
    
    public String getResponses(final String requests) throws Exception {
        return this.getResponses(requests, 5L, TimeUnit.SECONDS);
    }
    
    public String getResponses(final String requests, final long idleFor, final TimeUnit units) throws Exception {
        final ByteBuffer result = this.getResponses(BufferUtil.toBuffer(requests, StandardCharsets.UTF_8), idleFor, units);
        return (result == null) ? null : BufferUtil.toString(result, StandardCharsets.UTF_8);
    }
    
    public ByteBuffer getResponses(final ByteBuffer requestsBuffer) throws Exception {
        return this.getResponses(requestsBuffer, 5L, TimeUnit.SECONDS);
    }
    
    public ByteBuffer getResponses(final ByteBuffer requestsBuffer, final long idleFor, final TimeUnit units) throws Exception {
        if (this.LOG.isDebugEnabled()) {
            this.LOG.debug("requests {}", BufferUtil.toUTF8String(requestsBuffer));
        }
        final LocalEndPoint endp = this.executeRequest(requestsBuffer);
        endp.waitUntilClosedOrIdleFor(idleFor, units);
        final ByteBuffer responses = endp.takeOutput();
        if (endp.isOutputShutdown()) {
            endp.close();
        }
        if (this.LOG.isDebugEnabled()) {
            this.LOG.debug("responses {}", BufferUtil.toUTF8String(responses));
        }
        return responses;
    }
    
    public LocalEndPoint executeRequest(final String rawRequest) {
        return this.executeRequest(BufferUtil.toBuffer(rawRequest, StandardCharsets.UTF_8));
    }
    
    private LocalEndPoint executeRequest(final ByteBuffer rawRequest) {
        if (!this.isStarted()) {
            throw new IllegalStateException("!STARTED");
        }
        final LocalEndPoint endp = new LocalEndPoint();
        endp.addInput(rawRequest);
        this._connects.add(endp);
        return endp;
    }
    
    public LocalEndPoint connect() {
        final LocalEndPoint endp = new LocalEndPoint();
        this._connects.add(endp);
        return endp;
    }
    
    @Override
    protected void accept(final int acceptorID) throws IOException, InterruptedException {
        if (this.LOG.isDebugEnabled()) {
            this.LOG.debug("accepting {}", acceptorID);
        }
        final LocalEndPoint endPoint = this._connects.take();
        final Connection connection = this.getDefaultConnectionFactory().newConnection(this, endPoint);
        endPoint.setConnection(connection);
        endPoint.onOpen();
        this.onEndPointOpened(endPoint);
        connection.onOpen();
    }
    
    public ByteBuffer getResponse(final ByteBuffer requestsBuffer) throws Exception {
        return this.getResponse(requestsBuffer, false, 10L, TimeUnit.SECONDS);
    }
    
    public ByteBuffer getResponse(final ByteBuffer requestBuffer, final long time, final TimeUnit unit) throws Exception {
        final boolean head = BufferUtil.toString(requestBuffer).toLowerCase().startsWith("head ");
        if (this.LOG.isDebugEnabled()) {
            this.LOG.debug("requests {}", BufferUtil.toUTF8String(requestBuffer));
        }
        final LocalEndPoint endp = this.executeRequest(requestBuffer);
        return endp.waitForResponse(head, time, unit);
    }
    
    public ByteBuffer getResponse(final ByteBuffer requestBuffer, final boolean head, final long time, final TimeUnit unit) throws Exception {
        if (this.LOG.isDebugEnabled()) {
            this.LOG.debug("requests {}", BufferUtil.toUTF8String(requestBuffer));
        }
        final LocalEndPoint endp = this.executeRequest(requestBuffer);
        return endp.waitForResponse(head, time, unit);
    }
    
    public String getResponse(final String rawRequest) throws Exception {
        return this.getResponse(rawRequest, false, 30L, TimeUnit.SECONDS);
    }
    
    public String getResponse(final String rawRequest, final long time, final TimeUnit unit) throws Exception {
        final boolean head = rawRequest.toLowerCase().startsWith("head ");
        final ByteBuffer requestsBuffer = BufferUtil.toBuffer(rawRequest, StandardCharsets.ISO_8859_1);
        if (this.LOG.isDebugEnabled()) {
            this.LOG.debug("request {}", BufferUtil.toUTF8String(requestsBuffer));
        }
        final LocalEndPoint endp = this.executeRequest(requestsBuffer);
        return BufferUtil.toString(endp.waitForResponse(head, time, unit), StandardCharsets.ISO_8859_1);
    }
    
    public String getResponse(final String rawRequest, final boolean head, final long time, final TimeUnit unit) throws Exception {
        final ByteBuffer requestsBuffer = BufferUtil.toBuffer(rawRequest, StandardCharsets.ISO_8859_1);
        if (this.LOG.isDebugEnabled()) {
            this.LOG.debug("request {}", BufferUtil.toUTF8String(requestsBuffer));
        }
        final LocalEndPoint endp = this.executeRequest(requestsBuffer);
        return BufferUtil.toString(endp.waitForResponse(head, time, unit), StandardCharsets.ISO_8859_1);
    }
    
    public class LocalEndPoint extends ByteArrayEndPoint
    {
        private final CountDownLatch _closed;
        private ByteBuffer _responseData;
        
        public LocalEndPoint() {
            super(LocalConnector.this.getScheduler(), LocalConnector.this.getIdleTimeout());
            this._closed = new CountDownLatch(1);
            this.setGrowOutput(true);
        }
        
        @Override
        protected void execute(final Runnable task) {
            LocalConnector.this.getExecutor().execute(task);
        }
        
        @Override
        public void close() {
            final boolean wasOpen = this.isOpen();
            super.close();
            if (wasOpen) {
                this.getConnection().onClose();
                this.onClose();
            }
        }
        
        @Override
        public void onClose() {
            LocalConnector.this.onEndPointClosed(this);
            super.onClose();
            this._closed.countDown();
        }
        
        @Override
        public void shutdownOutput() {
            super.shutdownOutput();
            this.close();
        }
        
        public void waitUntilClosed() {
            while (this.isOpen()) {
                try {
                    if (!this._closed.await(10L, TimeUnit.SECONDS)) {
                        break;
                    }
                    continue;
                }
                catch (Exception e) {
                    LocalConnector.this.LOG.warn(e);
                }
            }
        }
        
        public void waitUntilClosedOrIdleFor(final long idleFor, final TimeUnit units) {
            Thread.yield();
            int size = this.getOutput().remaining();
            while (this.isOpen()) {
                try {
                    if (this._closed.await(idleFor, units)) {
                        continue;
                    }
                    if (size == this.getOutput().remaining()) {
                        if (LocalConnector.this.LOG.isDebugEnabled()) {
                            LocalConnector.this.LOG.debug("idle for {} {}", idleFor, units);
                        }
                        return;
                    }
                    size = this.getOutput().remaining();
                }
                catch (Exception e) {
                    LocalConnector.this.LOG.warn(e);
                }
            }
        }
        
        public String getResponse() throws Exception {
            return this.getResponse(false, 30L, TimeUnit.SECONDS);
        }
        
        public String getResponse(final boolean head, final long time, final TimeUnit unit) throws Exception {
            final ByteBuffer response = this.waitForResponse(head, time, unit);
            if (response != null) {
                return BufferUtil.toString(response);
            }
            return null;
        }
        
        public ByteBuffer waitForResponse(final boolean head, final long time, final TimeUnit unit) throws Exception {
            final HttpParser.ResponseHandler handler = new HttpParser.ResponseHandler() {
                @Override
                public void parsedHeader(final HttpField field) {
                }
                
                @Override
                public boolean contentComplete() {
                    return false;
                }
                
                @Override
                public boolean messageComplete() {
                    return true;
                }
                
                @Override
                public boolean headerComplete() {
                    return false;
                }
                
                @Override
                public int getHeaderCacheSize() {
                    return 0;
                }
                
                @Override
                public void earlyEOF() {
                }
                
                @Override
                public boolean content(final ByteBuffer item) {
                    return false;
                }
                
                @Override
                public void badMessage(final int status, final String reason) {
                }
                
                @Override
                public boolean startResponse(final HttpVersion version, final int status, final String reason) {
                    return false;
                }
            };
            final HttpParser parser = new HttpParser(handler);
            parser.setHeadResponse(head);
            final ByteArrayOutputStream2 bout = new ByteArrayOutputStream2();
            Throwable t = null;
            try {
            Label_0241:
                while (true) {
                    ByteBuffer chunk;
                    if (BufferUtil.hasContent(this._responseData)) {
                        chunk = this._responseData;
                    }
                    else {
                        chunk = this.waitForOutput(time, unit);
                        if (BufferUtil.isEmpty(chunk) && (!this.isOpen() || this.isOutputShutdown())) {
                            parser.atEOF();
                            parser.parseNext(BufferUtil.EMPTY_BUFFER);
                            break;
                        }
                    }
                    while (BufferUtil.hasContent(chunk)) {
                        final int pos = chunk.position();
                        final boolean complete = parser.parseNext(chunk);
                        if (chunk.position() == pos) {
                            if (BufferUtil.isEmpty(chunk)) {
                                break;
                            }
                            return null;
                        }
                        else {
                            bout.write(chunk.array(), chunk.arrayOffset() + pos, chunk.position() - pos);
                            if (!complete) {
                                continue;
                            }
                            if (BufferUtil.hasContent(chunk)) {
                                this._responseData = chunk;
                                break Label_0241;
                            }
                            break Label_0241;
                        }
                    }
                }
                if (bout.getCount() == 0 && this.isOutputShutdown()) {
                    return null;
                }
                return ByteBuffer.wrap(bout.getBuf(), 0, bout.getCount());
            }
            catch (Throwable t2) {
                t = t2;
                throw t2;
            }
            finally {
                if (t != null) {
                    try {
                        bout.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                else {
                    bout.close();
                }
            }
        }
    }
}
