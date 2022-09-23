// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.nio.channels.WritePendingException;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.util.IteratingCallback;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.http.MetaData;
import java.util.concurrent.RejectedExecutionException;
import java.io.IOException;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.http.HttpCompliance;
import org.eclipse.jetty.io.EndPoint;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.http.HttpGenerator;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.AbstractConnection;

public class HttpConnection extends AbstractConnection implements Runnable, HttpTransport, Connection.UpgradeFrom
{
    private static final Logger LOG;
    public static final HttpField CONNECTION_CLOSE;
    public static final String UPGRADE_CONNECTION_ATTRIBUTE = "org.eclipse.jetty.server.HttpConnection.UPGRADE";
    private static final boolean REQUEST_BUFFER_DIRECT = false;
    private static final boolean HEADER_BUFFER_DIRECT = false;
    private static final boolean CHUNK_BUFFER_DIRECT = false;
    private static final ThreadLocal<HttpConnection> __currentConnection;
    private final HttpConfiguration _config;
    private final Connector _connector;
    private final ByteBufferPool _bufferPool;
    private final HttpInput _input;
    private final HttpGenerator _generator;
    private final HttpChannelOverHttp _channel;
    private final HttpParser _parser;
    private final AtomicInteger _contentBufferReferences;
    private volatile ByteBuffer _requestBuffer;
    private volatile ByteBuffer _chunk;
    private final BlockingReadCallback _blockingReadCallback;
    private final AsyncReadCallback _asyncReadCallback;
    private final SendCallback _sendCallback;
    private final boolean _recordHttpComplianceViolations;
    
    public static HttpConnection getCurrentConnection() {
        return HttpConnection.__currentConnection.get();
    }
    
    protected static HttpConnection setCurrentConnection(final HttpConnection connection) {
        final HttpConnection last = HttpConnection.__currentConnection.get();
        HttpConnection.__currentConnection.set(connection);
        return last;
    }
    
    public HttpConnection(final HttpConfiguration config, final Connector connector, final EndPoint endPoint, final HttpCompliance compliance, final boolean recordComplianceViolations) {
        super(endPoint, connector.getExecutor());
        this._contentBufferReferences = new AtomicInteger();
        this._requestBuffer = null;
        this._chunk = null;
        this._blockingReadCallback = new BlockingReadCallback();
        this._asyncReadCallback = new AsyncReadCallback();
        this._sendCallback = new SendCallback();
        this._config = config;
        this._connector = connector;
        this._bufferPool = this._connector.getByteBufferPool();
        this._generator = this.newHttpGenerator();
        this._channel = this.newHttpChannel();
        this._input = this._channel.getRequest().getHttpInput();
        this._parser = this.newHttpParser(compliance);
        this._recordHttpComplianceViolations = recordComplianceViolations;
        if (HttpConnection.LOG.isDebugEnabled()) {
            HttpConnection.LOG.debug("New HTTP Connection {}", this);
        }
    }
    
    public HttpConfiguration getHttpConfiguration() {
        return this._config;
    }
    
    public boolean isRecordHttpComplianceViolations() {
        return this._recordHttpComplianceViolations;
    }
    
    protected HttpGenerator newHttpGenerator() {
        return new HttpGenerator(this._config.getSendServerVersion(), this._config.getSendXPoweredBy());
    }
    
    protected HttpChannelOverHttp newHttpChannel() {
        return new HttpChannelOverHttp(this, this._connector, this._config, this.getEndPoint(), this);
    }
    
    protected HttpParser newHttpParser(final HttpCompliance compliance) {
        return new HttpParser(this.newRequestHandler(), this.getHttpConfiguration().getRequestHeaderSize(), compliance);
    }
    
    protected HttpParser.RequestHandler newRequestHandler() {
        return this._channel;
    }
    
    public Server getServer() {
        return this._connector.getServer();
    }
    
    public Connector getConnector() {
        return this._connector;
    }
    
    public HttpChannel getHttpChannel() {
        return this._channel;
    }
    
    public HttpParser getParser() {
        return this._parser;
    }
    
    public HttpGenerator getGenerator() {
        return this._generator;
    }
    
    @Override
    public boolean isOptimizedForDirectBuffers() {
        return this.getEndPoint().isOptimizedForDirectBuffers();
    }
    
    @Override
    public int getMessagesIn() {
        return this.getHttpChannel().getRequests();
    }
    
    @Override
    public int getMessagesOut() {
        return this.getHttpChannel().getRequests();
    }
    
    @Override
    public ByteBuffer onUpgradeFrom() {
        if (BufferUtil.hasContent(this._requestBuffer)) {
            final ByteBuffer buffer = this._requestBuffer;
            this._requestBuffer = null;
            return buffer;
        }
        return null;
    }
    
    void releaseRequestBuffer() {
        if (this._requestBuffer != null && !this._requestBuffer.hasRemaining()) {
            if (HttpConnection.LOG.isDebugEnabled()) {
                HttpConnection.LOG.debug("releaseRequestBuffer {}", this);
            }
            final ByteBuffer buffer = this._requestBuffer;
            this._requestBuffer = null;
            this._bufferPool.release(buffer);
        }
    }
    
    public ByteBuffer getRequestBuffer() {
        if (this._requestBuffer == null) {
            this._requestBuffer = this._bufferPool.acquire(this.getInputBufferSize(), false);
        }
        return this._requestBuffer;
    }
    
    public boolean isRequestBufferEmpty() {
        return BufferUtil.isEmpty(this._requestBuffer);
    }
    
    @Override
    public void onFillable() {
        if (HttpConnection.LOG.isDebugEnabled()) {
            HttpConnection.LOG.debug("{} onFillable enter {} {}", this, this._channel.getState(), BufferUtil.toDetailString(this._requestBuffer));
        }
        final HttpConnection last = setCurrentConnection(this);
        try {
            while (this.getEndPoint().isOpen()) {
                final int filled = this.fillRequestBuffer();
                final boolean handle = this.parseRequestBuffer();
                if (this.getEndPoint().getConnection() != this) {
                    break;
                }
                if (this._parser.isClose() || this._parser.isClosed()) {
                    this.close();
                    break;
                }
                if (handle) {
                    final boolean suspended = !this._channel.handle();
                    if (suspended) {
                        break;
                    }
                    if (this.getEndPoint().getConnection() != this) {
                        break;
                    }
                    continue;
                }
                else {
                    if (filled > 0) {
                        continue;
                    }
                    if (filled == 0) {
                        this.fillInterested();
                        break;
                    }
                    break;
                }
            }
        }
        finally {
            setCurrentConnection(last);
            if (HttpConnection.LOG.isDebugEnabled()) {
                HttpConnection.LOG.debug("{} onFillable exit {} {}", this, this._channel.getState(), BufferUtil.toDetailString(this._requestBuffer));
            }
        }
    }
    
    protected boolean fillAndParseForContent() {
        boolean handled = false;
        while (this._parser.inContentState()) {
            final int filled = this.fillRequestBuffer();
            handled = this.parseRequestBuffer();
            if (handled || filled <= 0) {
                break;
            }
            if (this._input.hasContent()) {
                break;
            }
        }
        return handled;
    }
    
    private int fillRequestBuffer() {
        if (this._contentBufferReferences.get() > 0) {
            HttpConnection.LOG.warn("{} fill with unconsumed content!", this);
            return 0;
        }
        if (BufferUtil.isEmpty(this._requestBuffer)) {
            if (this.getEndPoint().isInputShutdown()) {
                this._parser.atEOF();
                if (HttpConnection.LOG.isDebugEnabled()) {
                    HttpConnection.LOG.debug("{} filled -1 {}", this, BufferUtil.toDetailString(this._requestBuffer));
                }
                return -1;
            }
            this._requestBuffer = this.getRequestBuffer();
            try {
                int filled = this.getEndPoint().fill(this._requestBuffer);
                if (filled == 0) {
                    filled = this.getEndPoint().fill(this._requestBuffer);
                }
                if (filled < 0) {
                    this._parser.atEOF();
                }
                if (HttpConnection.LOG.isDebugEnabled()) {
                    HttpConnection.LOG.debug("{} filled {} {}", this, filled, BufferUtil.toDetailString(this._requestBuffer));
                }
                return filled;
            }
            catch (IOException e) {
                HttpConnection.LOG.debug(e);
                this._parser.atEOF();
                return -1;
            }
        }
        return 0;
    }
    
    private boolean parseRequestBuffer() {
        if (HttpConnection.LOG.isDebugEnabled()) {
            HttpConnection.LOG.debug("{} parse {} {}", this, BufferUtil.toDetailString(this._requestBuffer));
        }
        final boolean handle = this._parser.parseNext((this._requestBuffer == null) ? BufferUtil.EMPTY_BUFFER : this._requestBuffer);
        if (HttpConnection.LOG.isDebugEnabled()) {
            HttpConnection.LOG.debug("{} parsed {} {}", this, handle, this._parser);
        }
        if (this._contentBufferReferences.get() == 0) {
            this.releaseRequestBuffer();
        }
        return handle;
    }
    
    @Override
    public void onCompleted() {
        if (this._channel.getResponse().getStatus() == 101) {
            final Connection connection = (Connection)this._channel.getRequest().getAttribute("org.eclipse.jetty.server.HttpConnection.UPGRADE");
            if (connection != null) {
                if (HttpConnection.LOG.isDebugEnabled()) {
                    HttpConnection.LOG.debug("Upgrade from {} to {}", this, connection);
                }
                this._channel.getState().upgrade();
                this.getEndPoint().upgrade(connection);
                this._channel.recycle();
                this._parser.reset();
                this._generator.reset();
                if (this._contentBufferReferences.get() == 0) {
                    this.releaseRequestBuffer();
                }
                else {
                    HttpConnection.LOG.warn("{} lingering content references?!?!", this);
                    this._requestBuffer = null;
                    this._contentBufferReferences.set(0);
                }
                return;
            }
        }
        if (this._channel.isExpecting100Continue()) {
            this._parser.close();
        }
        else if (this._parser.inContentState() && this._generator.isPersistent()) {
            if (this._input.isAsync()) {
                if (HttpConnection.LOG.isDebugEnabled()) {
                    HttpConnection.LOG.debug("unconsumed async input {}", this);
                }
                this._channel.abort(new IOException("unconsumed input"));
            }
            else {
                if (HttpConnection.LOG.isDebugEnabled()) {
                    HttpConnection.LOG.debug("unconsumed input {}", this);
                }
                if (!this._input.consumeAll()) {
                    this._channel.abort(new IOException("unconsumed input"));
                }
            }
        }
        this._channel.recycle();
        if (this._generator.isPersistent() && !this._parser.isClosed()) {
            this._parser.reset();
        }
        else {
            this._parser.close();
        }
        if (this._chunk != null) {
            this._bufferPool.release(this._chunk);
        }
        this._chunk = null;
        this._generator.reset();
        if (getCurrentConnection() != this) {
            if (this._parser.isStart()) {
                if (BufferUtil.isEmpty(this._requestBuffer)) {
                    this.fillInterested();
                }
                else if (this.getConnector().isRunning()) {
                    try {
                        this.getExecutor().execute(this);
                    }
                    catch (RejectedExecutionException e) {
                        if (this.getConnector().isRunning()) {
                            HttpConnection.LOG.warn(e);
                        }
                        else {
                            HttpConnection.LOG.ignore(e);
                        }
                        this.getEndPoint().close();
                    }
                }
                else {
                    this.getEndPoint().close();
                }
            }
            else if (this.getEndPoint().isOpen()) {
                this.fillInterested();
            }
        }
    }
    
    @Override
    protected void onFillInterestedFailed(final Throwable cause) {
        this._parser.close();
        super.onFillInterestedFailed(cause);
    }
    
    @Override
    public void onOpen() {
        super.onOpen();
        this.fillInterested();
    }
    
    @Override
    public void onClose() {
        this._sendCallback.close();
        super.onClose();
    }
    
    @Override
    public void run() {
        this.onFillable();
    }
    
    @Override
    public void send(final MetaData.Response info, final boolean head, final ByteBuffer content, final boolean lastContent, final Callback callback) {
        if (info == null) {
            if (!lastContent && BufferUtil.isEmpty(content)) {
                callback.succeeded();
                return;
            }
        }
        else if (this._channel.isExpecting100Continue()) {
            this._generator.setPersistent(false);
        }
        if (this._sendCallback.reset(info, head, content, lastContent, callback)) {
            this._sendCallback.iterate();
        }
    }
    
    HttpInput.Content newContent(final ByteBuffer c) {
        return new Content(c);
    }
    
    @Override
    public void abort(final Throwable failure) {
        this.getEndPoint().close();
    }
    
    @Override
    public boolean isPushSupported() {
        return false;
    }
    
    @Override
    public void push(final MetaData.Request request) {
        HttpConnection.LOG.debug("ignore push in {}", this);
    }
    
    public void asyncReadFillInterested() {
        this.getEndPoint().fillInterested(this._asyncReadCallback);
    }
    
    public void blockingReadFillInterested() {
        this.getEndPoint().fillInterested(this._blockingReadCallback);
    }
    
    public void blockingReadFailure(final Throwable e) {
        this._blockingReadCallback.failed(e);
    }
    
    @Override
    public String toString() {
        return String.format("%s[p=%s,g=%s,c=%s]", super.toString(), this._parser, this._generator, this._channel);
    }
    
    static {
        LOG = Log.getLogger(HttpConnection.class);
        CONNECTION_CLOSE = new PreEncodedHttpField(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE.asString());
        __currentConnection = new ThreadLocal<HttpConnection>();
    }
    
    private class Content extends HttpInput.Content
    {
        public Content(final ByteBuffer content) {
            super(content);
            HttpConnection.this._contentBufferReferences.incrementAndGet();
        }
        
        @Override
        public void succeeded() {
            if (HttpConnection.this._contentBufferReferences.decrementAndGet() == 0) {
                HttpConnection.this.releaseRequestBuffer();
            }
        }
        
        @Override
        public void failed(final Throwable x) {
            this.succeeded();
        }
    }
    
    private class BlockingReadCallback implements Callback
    {
        @Override
        public void succeeded() {
            HttpConnection.this._input.unblock();
        }
        
        @Override
        public void failed(final Throwable x) {
            HttpConnection.this._input.failed(x);
        }
        
        @Override
        public boolean isNonBlocking() {
            return true;
        }
    }
    
    private class AsyncReadCallback implements Callback
    {
        @Override
        public void succeeded() {
            if (HttpConnection.this.fillAndParseForContent()) {
                HttpConnection.this._channel.handle();
            }
            else if (!HttpConnection.this._input.isFinished() && !HttpConnection.this._input.hasContent()) {
                HttpConnection.this.asyncReadFillInterested();
            }
        }
        
        @Override
        public void failed(final Throwable x) {
            if (HttpConnection.this._input.failed(x)) {
                HttpConnection.this._channel.handle();
            }
        }
    }
    
    private class SendCallback extends IteratingCallback
    {
        private MetaData.Response _info;
        private boolean _head;
        private ByteBuffer _content;
        private boolean _lastContent;
        private Callback _callback;
        private ByteBuffer _header;
        private boolean _shutdownOut;
        
        private SendCallback() {
            super(true);
        }
        
        @Override
        public boolean isNonBlocking() {
            return this._callback.isNonBlocking();
        }
        
        private boolean reset(final MetaData.Response info, final boolean head, final ByteBuffer content, final boolean last, final Callback callback) {
            if (this.reset()) {
                this._info = info;
                this._head = head;
                this._content = content;
                this._lastContent = last;
                this._callback = callback;
                this._header = null;
                this._shutdownOut = false;
                return true;
            }
            if (this.isClosed()) {
                callback.failed(new EofException());
            }
            else {
                callback.failed(new WritePendingException());
            }
            return false;
        }
        
        public Action process() throws Exception {
            if (this._callback == null) {
                throw new IllegalStateException();
            }
            ByteBuffer chunk = HttpConnection.this._chunk;
            while (true) {
                final HttpGenerator.Result result = HttpConnection.this._generator.generateResponse(this._info, this._head, this._header, chunk, this._content, this._lastContent);
                if (HttpConnection.LOG.isDebugEnabled()) {
                    HttpConnection.LOG.debug("{} generate: {} ({},{},{})@{}", this, result, BufferUtil.toSummaryString(this._header), BufferUtil.toSummaryString(this._content), this._lastContent, HttpConnection.this._generator.getState());
                }
                switch (result) {
                    case NEED_INFO: {
                        throw new EofException("request lifecycle violation");
                    }
                    case NEED_HEADER: {
                        this._header = HttpConnection.this._bufferPool.acquire(HttpConnection.this._config.getResponseHeaderSize(), false);
                        continue;
                    }
                    case NEED_CHUNK: {
                        chunk = (HttpConnection.this._chunk = HttpConnection.this._bufferPool.acquire(12, false));
                        continue;
                    }
                    case FLUSH: {
                        if (this._head || HttpConnection.this._generator.isNoContent()) {
                            BufferUtil.clear(chunk);
                            BufferUtil.clear(this._content);
                        }
                        if (BufferUtil.hasContent(this._header)) {
                            if (BufferUtil.hasContent(this._content)) {
                                if (BufferUtil.hasContent(chunk)) {
                                    HttpConnection.this.getEndPoint().write(this, this._header, chunk, this._content);
                                }
                                else {
                                    HttpConnection.this.getEndPoint().write(this, this._header, this._content);
                                }
                            }
                            else {
                                HttpConnection.this.getEndPoint().write(this, this._header);
                            }
                        }
                        else if (BufferUtil.hasContent(chunk)) {
                            if (BufferUtil.hasContent(this._content)) {
                                HttpConnection.this.getEndPoint().write(this, chunk, this._content);
                            }
                            else {
                                HttpConnection.this.getEndPoint().write(this, chunk);
                            }
                        }
                        else if (BufferUtil.hasContent(this._content)) {
                            HttpConnection.this.getEndPoint().write(this, this._content);
                        }
                        else {
                            this.succeeded();
                        }
                        return Action.SCHEDULED;
                    }
                    case SHUTDOWN_OUT: {
                        this._shutdownOut = true;
                        continue;
                    }
                    case DONE: {
                        return Action.SUCCEEDED;
                    }
                    case CONTINUE: {
                        continue;
                    }
                    default: {
                        throw new IllegalStateException("generateResponse=" + result);
                    }
                }
            }
        }
        
        private void releaseHeader() {
            final ByteBuffer h = this._header;
            this._header = null;
            if (h != null) {
                HttpConnection.this._bufferPool.release(h);
            }
        }
        
        @Override
        protected void onCompleteSuccess() {
            this.releaseHeader();
            this._callback.succeeded();
            if (this._shutdownOut) {
                HttpConnection.this.getEndPoint().shutdownOutput();
            }
        }
        
        public void onCompleteFailure(final Throwable x) {
            this.releaseHeader();
            AbstractConnection.this.failedCallback(this._callback, x);
            if (this._shutdownOut) {
                HttpConnection.this.getEndPoint().shutdownOutput();
            }
        }
        
        @Override
        public String toString() {
            return String.format("%s[i=%s,cb=%s]", super.toString(), this._info, this._callback);
        }
    }
}
