// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client;

import org.eclipse.jetty.http.HttpHeaderValues;
import org.eclipse.jetty.http.HttpVersions;
import org.eclipse.jetty.util.log.Log;
import java.util.Collections;
import java.util.Collection;
import org.eclipse.jetty.util.component.AggregateLifeCycle;
import org.eclipse.jetty.io.EofException;
import java.io.InputStream;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.client.security.Authentication;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.io.View;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.io.Connection;
import java.io.IOException;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.Buffers;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jetty.util.thread.Timeout;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.http.HttpGenerator;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.io.AbstractConnection;

public abstract class AbstractHttpConnection extends AbstractConnection implements Dumpable
{
    private static final Logger LOG;
    protected HttpDestination _destination;
    protected HttpGenerator _generator;
    protected HttpParser _parser;
    protected boolean _http11;
    protected int _status;
    protected Buffer _connectionHeader;
    protected boolean _reserved;
    protected volatile HttpExchange _exchange;
    protected HttpExchange _pipeline;
    private final Timeout.Task _idleTimeout;
    private AtomicBoolean _idle;
    
    AbstractHttpConnection(final Buffers requestBuffers, final Buffers responseBuffers, final EndPoint endp) {
        super(endp);
        this._http11 = true;
        this._idleTimeout = new ConnectionIdleTask();
        this._idle = new AtomicBoolean(false);
        this._generator = new HttpGenerator(requestBuffers, endp);
        this._parser = new HttpParser(responseBuffers, endp, (HttpParser.EventHandler)new Handler());
    }
    
    public void setReserved(final boolean reserved) {
        this._reserved = reserved;
    }
    
    public boolean isReserved() {
        return this._reserved;
    }
    
    public HttpDestination getDestination() {
        return this._destination;
    }
    
    public void setDestination(final HttpDestination destination) {
        this._destination = destination;
    }
    
    public boolean send(final HttpExchange ex) throws IOException {
        AbstractHttpConnection.LOG.debug("Send {} on {}", ex, this);
        synchronized (this) {
            if (this._exchange != null) {
                if (this._pipeline != null) {
                    throw new IllegalStateException(this + " PIPELINED!!!  _exchange=" + this._exchange);
                }
                this._pipeline = ex;
                return true;
            }
            else {
                (this._exchange = ex).associate(this);
                if (!this._endp.isOpen()) {
                    this._exchange.disassociate();
                    this._exchange = null;
                    return false;
                }
                this._exchange.setStatus(2);
                this.adjustIdleTimeout();
                return true;
            }
        }
    }
    
    private void adjustIdleTimeout() throws IOException {
        long timeout = this._exchange.getTimeout();
        if (timeout <= 0L) {
            timeout = this._destination.getHttpClient().getTimeout();
        }
        final long endPointTimeout = this._endp.getMaxIdleTime();
        if (timeout > 0L && timeout > endPointTimeout) {
            this._endp.setMaxIdleTime(2 * (int)timeout);
        }
    }
    
    public abstract Connection handle() throws IOException;
    
    public boolean isIdle() {
        synchronized (this) {
            return this._exchange == null;
        }
    }
    
    public boolean isSuspended() {
        return false;
    }
    
    @Override
    public void onClose() {
    }
    
    protected void commitRequest() throws IOException {
        synchronized (this) {
            this._status = 0;
            if (this._exchange.getStatus() != 2) {
                throw new IllegalStateException();
            }
            this._exchange.setStatus(3);
            this._generator.setVersion(this._exchange.getVersion());
            final String method = this._exchange.getMethod();
            String uri = this._exchange.getRequestURI();
            if (this._destination.isProxied()) {
                if (!"CONNECT".equals(method) && uri.startsWith("/")) {
                    final boolean secure = this._destination.isSecure();
                    final String host = this._destination.getAddress().getHost();
                    final int port = this._destination.getAddress().getPort();
                    final StringBuilder absoluteURI = new StringBuilder();
                    absoluteURI.append(secure ? "https" : "http");
                    absoluteURI.append("://");
                    absoluteURI.append(host);
                    if ((!secure || port != 443) && (secure || port != 80)) {
                        absoluteURI.append(":").append(port);
                    }
                    absoluteURI.append(uri);
                    uri = absoluteURI.toString();
                }
                final Authentication auth = this._destination.getProxyAuthentication();
                if (auth != null) {
                    auth.setCredentials(this._exchange);
                }
            }
            this._generator.setRequest(method, uri);
            this._parser.setHeadResponse("HEAD".equalsIgnoreCase(method));
            final HttpFields requestHeaders = this._exchange.getRequestFields();
            if (this._exchange.getVersion() >= 11 && !requestHeaders.containsKey(HttpHeaders.HOST_BUFFER)) {
                requestHeaders.add(HttpHeaders.HOST_BUFFER, this._destination.getHostHeader());
            }
            final Buffer requestContent = this._exchange.getRequestContent();
            if (requestContent != null) {
                requestHeaders.putLongField("Content-Length", requestContent.length());
                this._generator.completeHeader(requestHeaders, false);
                this._generator.addContent((Buffer)new View(requestContent), true);
            }
            else {
                final InputStream requestContentStream = this._exchange.getRequestContentSource();
                if (requestContentStream != null) {
                    this._generator.completeHeader(requestHeaders, false);
                    final int available = requestContentStream.available();
                    if (available > 0) {
                        final byte[] buf = new byte[available];
                        final int length = requestContentStream.read(buf);
                        this._generator.addContent((Buffer)new ByteArrayBuffer(buf, 0, length), false);
                    }
                }
                else {
                    requestHeaders.remove("Content-Length");
                    this._generator.completeHeader(requestHeaders, true);
                }
            }
            this._exchange.setStatus(4);
        }
    }
    
    protected void reset() throws IOException {
        this._connectionHeader = null;
        this._parser.reset();
        this._generator.reset();
        this._http11 = true;
    }
    
    @Override
    public String toString() {
        return String.format("%s %s g=%s p=%s", super.toString(), (this._destination == null) ? "?.?.?.?:??" : this._destination.getAddress(), this._generator, this._parser);
    }
    
    public String toDetailString() {
        return this.toString() + " ex=" + this._exchange + " idle for " + this._idleTimeout.getAge();
    }
    
    @Override
    public void close() throws IOException {
        final HttpExchange exchange = this._exchange;
        Label_0174: {
            if (exchange != null && !exchange.isDone()) {
                switch (exchange.getStatus()) {
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11: {
                        break Label_0174;
                    }
                    case 6: {
                        if (this._endp.isInputShutdown() && this._parser.isState(1)) {
                            break Label_0174;
                        }
                        break;
                    }
                }
                final String exch = exchange.toString();
                final String reason = this._endp.isOpen() ? (this._endp.isInputShutdown() ? "half closed: " : "local close: ") : "closed: ";
                if (exchange.setStatus(9)) {
                    exchange.getEventListener().onException(new EofException(reason + exch));
                }
            }
        }
        if (this._endp.isOpen()) {
            this._endp.close();
            this._destination.returnConnection(this, true);
        }
    }
    
    public void setIdleTimeout() {
        synchronized (this) {
            if (!this._idle.compareAndSet(false, true)) {
                throw new IllegalStateException();
            }
            this._destination.getHttpClient().scheduleIdle(this._idleTimeout);
        }
    }
    
    public boolean cancelIdleTimeout() {
        synchronized (this) {
            if (this._idle.compareAndSet(true, false)) {
                this._destination.getHttpClient().cancel(this._idleTimeout);
                return true;
            }
        }
        return false;
    }
    
    protected void exchangeExpired(final HttpExchange exchange) {
        synchronized (this) {
            if (this._exchange == exchange) {
                try {
                    this._destination.returnConnection(this, true);
                }
                catch (IOException x) {
                    AbstractHttpConnection.LOG.ignore(x);
                }
            }
        }
    }
    
    public String dump() {
        return AggregateLifeCycle.dump(this);
    }
    
    public void dump(final Appendable out, final String indent) throws IOException {
        synchronized (this) {
            out.append(String.valueOf(this)).append("\n");
            AggregateLifeCycle.dump(out, indent, Collections.singletonList(this._endp));
        }
    }
    
    static {
        LOG = Log.getLogger(AbstractHttpConnection.class);
    }
    
    private class Handler extends HttpParser.EventHandler
    {
        @Override
        public void startRequest(final Buffer method, final Buffer url, final Buffer version) throws IOException {
        }
        
        @Override
        public void startResponse(final Buffer version, final int status, final Buffer reason) throws IOException {
            final HttpExchange exchange = AbstractHttpConnection.this._exchange;
            if (exchange == null) {
                AbstractHttpConnection.LOG.warn("No exchange for response", new Object[0]);
                AbstractHttpConnection.this._endp.close();
                return;
            }
            switch (status) {
                case 100:
                case 102: {
                    exchange.setEventListener(new NonFinalResponseListener(exchange));
                    break;
                }
                case 200: {
                    if ("CONNECT".equalsIgnoreCase(exchange.getMethod())) {
                        AbstractHttpConnection.this._parser.setHeadResponse(true);
                        break;
                    }
                    break;
                }
            }
            AbstractHttpConnection.this._http11 = HttpVersions.HTTP_1_1_BUFFER.equals(version);
            AbstractHttpConnection.this._status = status;
            exchange.getEventListener().onResponseStatus(version, status, reason);
            exchange.setStatus(5);
        }
        
        @Override
        public void parsedHeader(final Buffer name, final Buffer value) throws IOException {
            final HttpExchange exchange = AbstractHttpConnection.this._exchange;
            if (exchange != null) {
                if (HttpHeaders.CACHE.getOrdinal(name) == 1) {
                    AbstractHttpConnection.this._connectionHeader = HttpHeaderValues.CACHE.lookup(value);
                }
                exchange.getEventListener().onResponseHeader(name, value);
            }
        }
        
        @Override
        public void headerComplete() throws IOException {
            final HttpExchange exchange = AbstractHttpConnection.this._exchange;
            if (exchange != null) {
                exchange.setStatus(6);
            }
        }
        
        @Override
        public void content(final Buffer ref) throws IOException {
            final HttpExchange exchange = AbstractHttpConnection.this._exchange;
            if (exchange != null) {
                exchange.getEventListener().onResponseContent(ref);
            }
        }
        
        @Override
        public void messageComplete(final long contextLength) throws IOException {
            final HttpExchange exchange = AbstractHttpConnection.this._exchange;
            if (exchange != null) {
                exchange.setStatus(7);
            }
        }
        
        @Override
        public void earlyEOF() {
            final HttpExchange exchange = AbstractHttpConnection.this._exchange;
            if (exchange != null && !exchange.isDone() && exchange.setStatus(9)) {
                exchange.getEventListener().onException(new EofException("early EOF"));
            }
        }
    }
    
    private class ConnectionIdleTask extends Timeout.Task
    {
        @Override
        public void expired() {
            if (AbstractHttpConnection.this._idle.compareAndSet(true, false)) {
                AbstractHttpConnection.this._destination.returnIdleConnection(AbstractHttpConnection.this);
            }
        }
    }
    
    private class NonFinalResponseListener implements HttpEventListener
    {
        final HttpExchange _exchange;
        final HttpEventListener _next;
        
        public NonFinalResponseListener(final HttpExchange exchange) {
            this._exchange = exchange;
            this._next = exchange.getEventListener();
        }
        
        public void onRequestCommitted() throws IOException {
        }
        
        public void onRequestComplete() throws IOException {
        }
        
        public void onResponseStatus(final Buffer version, final int status, final Buffer reason) throws IOException {
        }
        
        public void onResponseHeader(final Buffer name, final Buffer value) throws IOException {
            this._next.onResponseHeader(name, value);
        }
        
        public void onResponseHeaderComplete() throws IOException {
            this._next.onResponseHeaderComplete();
        }
        
        public void onResponseContent(final Buffer content) throws IOException {
        }
        
        public void onResponseComplete() throws IOException {
            this._exchange.setEventListener(this._next);
            this._exchange.setStatus(4);
            AbstractHttpConnection.this._parser.reset();
        }
        
        public void onConnectionFailed(final Throwable ex) {
            this._exchange.setEventListener(this._next);
            this._next.onConnectionFailed(ex);
        }
        
        public void onException(final Throwable ex) {
            this._exchange.setEventListener(this._next);
            this._next.onException(ex);
        }
        
        public void onExpire() {
            this._exchange.setEventListener(this._next);
            this._next.onExpire();
        }
        
        public void onRetry() {
            this._exchange.setEventListener(this._next);
            this._next.onRetry();
        }
    }
}
