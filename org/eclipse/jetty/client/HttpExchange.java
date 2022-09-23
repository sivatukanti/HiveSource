// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.io.BufferCache;
import org.eclipse.jetty.http.HttpVersions;
import org.eclipse.jetty.io.ByteArrayBuffer;
import java.net.URI;
import java.io.IOException;
import org.eclipse.jetty.http.HttpSchemes;
import org.eclipse.jetty.util.thread.Timeout;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.InputStream;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.util.log.Logger;

public class HttpExchange
{
    static final Logger LOG;
    public static final int STATUS_START = 0;
    public static final int STATUS_WAITING_FOR_CONNECTION = 1;
    public static final int STATUS_WAITING_FOR_COMMIT = 2;
    public static final int STATUS_SENDING_REQUEST = 3;
    public static final int STATUS_WAITING_FOR_RESPONSE = 4;
    public static final int STATUS_PARSING_HEADERS = 5;
    public static final int STATUS_PARSING_CONTENT = 6;
    public static final int STATUS_COMPLETED = 7;
    public static final int STATUS_EXPIRED = 8;
    public static final int STATUS_EXCEPTED = 9;
    public static final int STATUS_CANCELLING = 10;
    public static final int STATUS_CANCELLED = 11;
    private String _method;
    private Buffer _scheme;
    private String _uri;
    private int _version;
    private Address _address;
    private final HttpFields _requestFields;
    private Buffer _requestContent;
    private InputStream _requestContentSource;
    private AtomicInteger _status;
    private boolean _retryStatus;
    private boolean _configureListeners;
    private HttpEventListener _listener;
    private volatile AbstractHttpConnection _connection;
    private Address _localAddress;
    private long _timeout;
    private volatile Timeout.Task _timeoutTask;
    private long _lastStateChange;
    private long _sent;
    private int _lastState;
    private int _lastStatePeriod;
    boolean _onRequestCompleteDone;
    boolean _onResponseCompleteDone;
    boolean _onDone;
    
    public HttpExchange() {
        this._method = "GET";
        this._scheme = HttpSchemes.HTTP_BUFFER;
        this._version = 11;
        this._requestFields = new HttpFields();
        this._status = new AtomicInteger(0);
        this._retryStatus = false;
        this._configureListeners = true;
        this._listener = new Listener();
        this._localAddress = null;
        this._timeout = -1L;
        this._lastStateChange = System.currentTimeMillis();
        this._sent = -1L;
        this._lastState = -1;
        this._lastStatePeriod = -1;
    }
    
    protected void expire(final HttpDestination destination) {
        if (this.getStatus() < 7) {
            this.setStatus(8);
        }
        destination.exchangeExpired(this);
        final AbstractHttpConnection connection = this._connection;
        if (connection != null) {
            connection.exchangeExpired(this);
        }
    }
    
    public int getStatus() {
        return this._status.get();
    }
    
    @Deprecated
    public void waitForStatus(final int status) throws InterruptedException {
        throw new UnsupportedOperationException();
    }
    
    public int waitForDone() throws InterruptedException {
        synchronized (this) {
            while (!this.isDone()) {
                this.wait();
            }
            return this._status.get();
        }
    }
    
    public void reset() {
        synchronized (this) {
            this._timeoutTask = null;
            this._onRequestCompleteDone = false;
            this._onResponseCompleteDone = false;
            this._onDone = false;
            this.setStatus(0);
        }
    }
    
    boolean setStatus(final int newStatus) {
        boolean set = false;
        try {
            final int oldStatus = this._status.get();
            boolean ignored = false;
            if (oldStatus != newStatus) {
                final long now = System.currentTimeMillis();
                this._lastStatePeriod = (int)(now - this._lastStateChange);
                this._lastState = oldStatus;
                this._lastStateChange = now;
                if (newStatus == 3) {
                    this._sent = this._lastStateChange;
                }
            }
            Label_0900: {
                switch (oldStatus) {
                    case 0: {
                        switch (newStatus) {
                            case 0:
                            case 1:
                            case 2:
                            case 9:
                            case 10: {
                                set = this._status.compareAndSet(oldStatus, newStatus);
                                break;
                            }
                            case 8: {
                                set = this.setStatusExpired(newStatus, oldStatus);
                                break;
                            }
                        }
                        break;
                    }
                    case 1: {
                        switch (newStatus) {
                            case 2:
                            case 9:
                            case 10: {
                                set = this._status.compareAndSet(oldStatus, newStatus);
                                break;
                            }
                            case 8: {
                                set = this.setStatusExpired(newStatus, oldStatus);
                                break;
                            }
                        }
                        break;
                    }
                    case 2: {
                        switch (newStatus) {
                            case 3:
                            case 9:
                            case 10: {
                                set = this._status.compareAndSet(oldStatus, newStatus);
                                break;
                            }
                            case 8: {
                                set = this.setStatusExpired(newStatus, oldStatus);
                                break;
                            }
                        }
                        break;
                    }
                    case 3: {
                        switch (newStatus) {
                            case 4: {
                                if (set = this._status.compareAndSet(oldStatus, newStatus)) {
                                    this.getEventListener().onRequestCommitted();
                                    break;
                                }
                                break;
                            }
                            case 9:
                            case 10: {
                                set = this._status.compareAndSet(oldStatus, newStatus);
                                break;
                            }
                            case 8: {
                                set = this.setStatusExpired(newStatus, oldStatus);
                                break;
                            }
                        }
                        break;
                    }
                    case 4: {
                        switch (newStatus) {
                            case 5:
                            case 9:
                            case 10: {
                                set = this._status.compareAndSet(oldStatus, newStatus);
                                break;
                            }
                            case 8: {
                                set = this.setStatusExpired(newStatus, oldStatus);
                                break;
                            }
                        }
                        break;
                    }
                    case 5: {
                        switch (newStatus) {
                            case 6: {
                                if (set = this._status.compareAndSet(oldStatus, newStatus)) {
                                    this.getEventListener().onResponseHeaderComplete();
                                    break;
                                }
                                break;
                            }
                            case 9:
                            case 10: {
                                set = this._status.compareAndSet(oldStatus, newStatus);
                                break;
                            }
                            case 8: {
                                set = this.setStatusExpired(newStatus, oldStatus);
                                break;
                            }
                        }
                        break;
                    }
                    case 6: {
                        switch (newStatus) {
                            case 7: {
                                if (set = this._status.compareAndSet(oldStatus, newStatus)) {
                                    this.getEventListener().onResponseComplete();
                                    break;
                                }
                                break;
                            }
                            case 9:
                            case 10: {
                                set = this._status.compareAndSet(oldStatus, newStatus);
                                break;
                            }
                            case 8: {
                                set = this.setStatusExpired(newStatus, oldStatus);
                                break;
                            }
                        }
                        break;
                    }
                    case 7: {
                        switch (newStatus) {
                            case 0:
                            case 4:
                            case 9: {
                                set = this._status.compareAndSet(oldStatus, newStatus);
                                break;
                            }
                            case 8:
                            case 10: {
                                ignored = true;
                                break;
                            }
                        }
                        break;
                    }
                    case 10: {
                        switch (newStatus) {
                            case 9:
                            case 11: {
                                if (set = this._status.compareAndSet(oldStatus, newStatus)) {
                                    this.done();
                                    break Label_0900;
                                }
                                break Label_0900;
                            }
                            default: {
                                ignored = true;
                                break Label_0900;
                            }
                        }
                        break;
                    }
                    case 8:
                    case 9:
                    case 11: {
                        switch (newStatus) {
                            case 0: {
                                set = this._status.compareAndSet(oldStatus, newStatus);
                                break Label_0900;
                            }
                            case 7: {
                                ignored = true;
                                this.done();
                                break Label_0900;
                            }
                            default: {
                                ignored = true;
                                break Label_0900;
                            }
                        }
                        break;
                    }
                    default: {
                        throw new AssertionError((Object)(oldStatus + " => " + newStatus));
                    }
                }
            }
            if (!set && !ignored) {
                throw new IllegalStateException(toState(oldStatus) + " => " + toState(newStatus));
            }
            HttpExchange.LOG.debug("setStatus {} {}", newStatus, this);
        }
        catch (IOException x) {
            HttpExchange.LOG.warn(x);
        }
        return set;
    }
    
    private boolean setStatusExpired(final int newStatus, final int oldStatus) {
        final boolean set;
        if (set = this._status.compareAndSet(oldStatus, newStatus)) {
            this.getEventListener().onExpire();
        }
        return set;
    }
    
    public boolean isDone() {
        synchronized (this) {
            return this._onDone;
        }
    }
    
    @Deprecated
    public boolean isDone(final int status) {
        return this.isDone();
    }
    
    public HttpEventListener getEventListener() {
        return this._listener;
    }
    
    public void setEventListener(final HttpEventListener listener) {
        this._listener = listener;
    }
    
    public void setTimeout(final long timeout) {
        this._timeout = timeout;
    }
    
    public long getTimeout() {
        return this._timeout;
    }
    
    public void setURL(final String url) {
        this.setURI(URI.create(url));
    }
    
    public void setAddress(final Address address) {
        this._address = address;
    }
    
    public Address getAddress() {
        return this._address;
    }
    
    public Address getLocalAddress() {
        return this._localAddress;
    }
    
    public void setScheme(final Buffer scheme) {
        this._scheme = scheme;
    }
    
    public void setScheme(final String scheme) {
        if (scheme != null) {
            if ("http".equalsIgnoreCase(scheme)) {
                this.setScheme(HttpSchemes.HTTP_BUFFER);
            }
            else if ("https".equalsIgnoreCase(scheme)) {
                this.setScheme(HttpSchemes.HTTPS_BUFFER);
            }
            else {
                this.setScheme(new ByteArrayBuffer(scheme));
            }
        }
    }
    
    public Buffer getScheme() {
        return this._scheme;
    }
    
    public void setVersion(final int version) {
        this._version = version;
    }
    
    public void setVersion(final String version) {
        final BufferCache.CachedBuffer v = HttpVersions.CACHE.get(version);
        if (v == null) {
            this._version = 10;
        }
        else {
            this._version = v.getOrdinal();
        }
    }
    
    public int getVersion() {
        return this._version;
    }
    
    public void setMethod(final String method) {
        this._method = method;
    }
    
    public String getMethod() {
        return this._method;
    }
    
    @Deprecated
    public String getURI() {
        return this.getRequestURI();
    }
    
    public String getRequestURI() {
        return this._uri;
    }
    
    @Deprecated
    public void setURI(final String uri) {
        this.setRequestURI(uri);
    }
    
    public void setRequestURI(final String uri) {
        this._uri = uri;
    }
    
    public void setURI(final URI uri) {
        if (!uri.isAbsolute()) {
            throw new IllegalArgumentException("!Absolute URI: " + uri);
        }
        if (uri.isOpaque()) {
            throw new IllegalArgumentException("Opaque URI: " + uri);
        }
        if (HttpExchange.LOG.isDebugEnabled()) {
            HttpExchange.LOG.debug("URI = {}", uri.toASCIIString());
        }
        final String scheme = uri.getScheme();
        int port = uri.getPort();
        if (port <= 0) {
            port = ("https".equalsIgnoreCase(scheme) ? 443 : 80);
        }
        this.setScheme(scheme);
        this.setAddress(new Address(uri.getHost(), port));
        final HttpURI httpUri = new HttpURI(uri);
        final String completePath = httpUri.getCompletePath();
        this.setRequestURI((completePath == null) ? "/" : completePath);
    }
    
    public void addRequestHeader(final String name, final String value) {
        this.getRequestFields().add(name, value);
    }
    
    public void addRequestHeader(final Buffer name, final Buffer value) {
        this.getRequestFields().add(name, value);
    }
    
    public void setRequestHeader(final String name, final String value) {
        this.getRequestFields().put(name, value);
    }
    
    public void setRequestHeader(final Buffer name, final Buffer value) {
        this.getRequestFields().put(name, value);
    }
    
    public void setRequestContentType(final String value) {
        this.getRequestFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, value);
    }
    
    public HttpFields getRequestFields() {
        return this._requestFields;
    }
    
    public void setRequestContent(final Buffer requestContent) {
        this._requestContent = requestContent;
    }
    
    public void setRequestContentSource(final InputStream stream) {
        this._requestContentSource = stream;
        if (this._requestContentSource != null && this._requestContentSource.markSupported()) {
            this._requestContentSource.mark(Integer.MAX_VALUE);
        }
    }
    
    public InputStream getRequestContentSource() {
        return this._requestContentSource;
    }
    
    public Buffer getRequestContentChunk(Buffer buffer) throws IOException {
        synchronized (this) {
            if (this._requestContentSource != null) {
                if (buffer == null) {
                    buffer = new ByteArrayBuffer(8192);
                }
                final int space = buffer.space();
                final int length = this._requestContentSource.read(buffer.array(), buffer.putIndex(), space);
                if (length >= 0) {
                    buffer.setPutIndex(buffer.putIndex() + length);
                    return buffer;
                }
            }
            return null;
        }
    }
    
    public Buffer getRequestContent() {
        return this._requestContent;
    }
    
    public boolean getRetryStatus() {
        return this._retryStatus;
    }
    
    public void setRetryStatus(final boolean retryStatus) {
        this._retryStatus = retryStatus;
    }
    
    public void cancel() {
        this.setStatus(10);
        this.abort();
    }
    
    private void done() {
        synchronized (this) {
            this.disassociate();
            this._onDone = true;
            this.notifyAll();
        }
    }
    
    private void abort() {
        final AbstractHttpConnection httpConnection = this._connection;
        if (httpConnection != null) {
            try {
                httpConnection.close();
            }
            catch (IOException x) {
                HttpExchange.LOG.debug(x);
            }
            finally {
                this.disassociate();
            }
        }
    }
    
    void associate(final AbstractHttpConnection connection) {
        if (connection.getEndPoint().getLocalHost() != null) {
            this._localAddress = new Address(connection.getEndPoint().getLocalHost(), connection.getEndPoint().getLocalPort());
        }
        this._connection = connection;
        if (this.getStatus() == 10) {
            this.abort();
        }
    }
    
    boolean isAssociated() {
        return this._connection != null;
    }
    
    AbstractHttpConnection disassociate() {
        final AbstractHttpConnection result = this._connection;
        this._connection = null;
        if (this.getStatus() == 10) {
            this.setStatus(11);
        }
        return result;
    }
    
    public static String toState(final int s) {
        String state = null;
        switch (s) {
            case 0: {
                state = "START";
                break;
            }
            case 1: {
                state = "CONNECTING";
                break;
            }
            case 2: {
                state = "CONNECTED";
                break;
            }
            case 3: {
                state = "SENDING";
                break;
            }
            case 4: {
                state = "WAITING";
                break;
            }
            case 5: {
                state = "HEADERS";
                break;
            }
            case 6: {
                state = "CONTENT";
                break;
            }
            case 7: {
                state = "COMPLETED";
                break;
            }
            case 8: {
                state = "EXPIRED";
                break;
            }
            case 9: {
                state = "EXCEPTED";
                break;
            }
            case 10: {
                state = "CANCELLING";
                break;
            }
            case 11: {
                state = "CANCELLED";
                break;
            }
            default: {
                state = "UNKNOWN";
                break;
            }
        }
        return state;
    }
    
    @Override
    public String toString() {
        final String state = toState(this.getStatus());
        final long now = System.currentTimeMillis();
        final long forMs = now - this._lastStateChange;
        String s = (this._lastState >= 0) ? String.format("%s@%x=%s//%s%s#%s(%dms)->%s(%dms)", this.getClass().getSimpleName(), this.hashCode(), this._method, this._address, this._uri, toState(this._lastState), this._lastStatePeriod, state, forMs) : String.format("%s@%x=%s//%s%s#%s(%dms)", this.getClass().getSimpleName(), this.hashCode(), this._method, this._address, this._uri, state, forMs);
        if (this.getStatus() >= 3 && this._sent > 0L) {
            s = s + "sent=" + (now - this._sent) + "ms";
        }
        return s;
    }
    
    protected Connection onSwitchProtocol(final EndPoint endp) throws IOException {
        return null;
    }
    
    protected void onRequestCommitted() throws IOException {
    }
    
    protected void onRequestComplete() throws IOException {
    }
    
    protected void onResponseStatus(final Buffer version, final int status, final Buffer reason) throws IOException {
    }
    
    protected void onResponseHeader(final Buffer name, final Buffer value) throws IOException {
    }
    
    protected void onResponseHeaderComplete() throws IOException {
    }
    
    protected void onResponseContent(final Buffer content) throws IOException {
    }
    
    protected void onResponseComplete() throws IOException {
    }
    
    protected void onConnectionFailed(final Throwable x) {
        HttpExchange.LOG.warn("CONNECTION FAILED " + this, x);
    }
    
    protected void onException(final Throwable x) {
        HttpExchange.LOG.warn("EXCEPTION " + this, x);
    }
    
    protected void onExpire() {
        HttpExchange.LOG.warn("EXPIRED " + this, new Object[0]);
    }
    
    protected void onRetry() throws IOException {
        if (this._requestContentSource != null) {
            if (!this._requestContentSource.markSupported()) {
                throw new IOException("Unsupported retry attempt");
            }
            this._requestContent = null;
            this._requestContentSource.reset();
        }
    }
    
    public boolean configureListeners() {
        return this._configureListeners;
    }
    
    public void setConfigureListeners(final boolean autoConfigure) {
        this._configureListeners = autoConfigure;
    }
    
    protected void scheduleTimeout(final HttpDestination destination) {
        assert this._timeoutTask == null;
        this._timeoutTask = new Timeout.Task() {
            @Override
            public void expired() {
                HttpExchange.this.expire(destination);
            }
        };
        final HttpClient httpClient = destination.getHttpClient();
        final long timeout = this.getTimeout();
        if (timeout > 0L) {
            httpClient.schedule(this._timeoutTask, timeout);
        }
        else {
            httpClient.schedule(this._timeoutTask);
        }
    }
    
    protected void cancelTimeout(final HttpClient httpClient) {
        final Timeout.Task task = this._timeoutTask;
        if (task != null) {
            httpClient.cancel(task);
        }
        this._timeoutTask = null;
    }
    
    static {
        LOG = Log.getLogger(HttpExchange.class);
    }
    
    private class Listener implements HttpEventListener
    {
        public void onConnectionFailed(final Throwable ex) {
            try {
                HttpExchange.this.onConnectionFailed(ex);
            }
            finally {
                HttpExchange.this.done();
            }
        }
        
        public void onException(final Throwable ex) {
            try {
                HttpExchange.this.onException(ex);
            }
            finally {
                HttpExchange.this.done();
            }
        }
        
        public void onExpire() {
            try {
                HttpExchange.this.onExpire();
            }
            finally {
                HttpExchange.this.done();
            }
        }
        
        public void onRequestCommitted() throws IOException {
            HttpExchange.this.onRequestCommitted();
        }
        
        public void onRequestComplete() throws IOException {
            try {
                HttpExchange.this.onRequestComplete();
            }
            finally {
                synchronized (HttpExchange.this) {
                    HttpExchange.this._onRequestCompleteDone = true;
                    final HttpExchange this$0 = HttpExchange.this;
                    this$0._onDone |= HttpExchange.this._onResponseCompleteDone;
                    if (HttpExchange.this._onDone) {
                        HttpExchange.this.disassociate();
                    }
                    HttpExchange.this.notifyAll();
                }
            }
        }
        
        public void onResponseComplete() throws IOException {
            try {
                HttpExchange.this.onResponseComplete();
            }
            finally {
                synchronized (HttpExchange.this) {
                    HttpExchange.this._onResponseCompleteDone = true;
                    final HttpExchange this$0 = HttpExchange.this;
                    this$0._onDone |= HttpExchange.this._onRequestCompleteDone;
                    if (HttpExchange.this._onDone) {
                        HttpExchange.this.disassociate();
                    }
                    HttpExchange.this.notifyAll();
                }
            }
        }
        
        public void onResponseContent(final Buffer content) throws IOException {
            HttpExchange.this.onResponseContent(content);
        }
        
        public void onResponseHeader(final Buffer name, final Buffer value) throws IOException {
            HttpExchange.this.onResponseHeader(name, value);
        }
        
        public void onResponseHeaderComplete() throws IOException {
            HttpExchange.this.onResponseHeaderComplete();
        }
        
        public void onResponseStatus(final Buffer version, final int status, final Buffer reason) throws IOException {
            HttpExchange.this.onResponseStatus(version, status, reason);
        }
        
        public void onRetry() {
            HttpExchange.this.setRetryStatus(true);
            try {
                HttpExchange.this.onRetry();
            }
            catch (IOException e) {
                HttpExchange.LOG.debug(e);
            }
        }
    }
    
    @Deprecated
    public static class CachedExchange extends org.eclipse.jetty.client.CachedExchange
    {
        public CachedExchange(final boolean cacheFields) {
            super(cacheFields);
        }
    }
    
    @Deprecated
    public static class ContentExchange extends org.eclipse.jetty.client.ContentExchange
    {
    }
}
