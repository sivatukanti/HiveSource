// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.http.HttpGenerator;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.io.ChannelEndPoint;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.SharedBlockingCallback;
import org.eclipse.jetty.util.Callback;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.HttpFields;
import javax.servlet.UnavailableException;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.handler.ContextHandler;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.io.EofException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.http.HttpStatus;
import java.util.concurrent.TimeoutException;
import javax.servlet.DispatcherType;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.io.EndPoint;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jetty.util.log.Logger;

public class HttpChannel implements Runnable, HttpOutput.Interceptor
{
    private static final Logger LOG;
    private final AtomicBoolean _committed;
    private final AtomicInteger _requests;
    private final Connector _connector;
    private final Executor _executor;
    private final HttpConfiguration _configuration;
    private final EndPoint _endPoint;
    private final HttpTransport _transport;
    private final HttpChannelState _state;
    private final Request _request;
    private final Response _response;
    private MetaData.Response _committedMetaData;
    private RequestLog _requestLog;
    private long _oldIdleTimeout;
    private long _written;
    
    public HttpChannel(final Connector connector, final HttpConfiguration configuration, final EndPoint endPoint, final HttpTransport transport) {
        this._committed = new AtomicBoolean();
        this._requests = new AtomicInteger();
        this._connector = connector;
        this._configuration = configuration;
        this._endPoint = endPoint;
        this._transport = transport;
        this._state = new HttpChannelState(this);
        this._request = new Request(this, this.newHttpInput(this._state));
        this._response = new Response(this, this.newHttpOutput());
        this._executor = ((connector == null) ? null : connector.getServer().getThreadPool());
        this._requestLog = ((connector == null) ? null : connector.getServer().getRequestLog());
        if (HttpChannel.LOG.isDebugEnabled()) {
            HttpChannel.LOG.debug("new {} -> {},{},{}", this, this._endPoint, (this._endPoint == null) ? null : this._endPoint.getConnection(), this._state);
        }
    }
    
    protected HttpInput newHttpInput(final HttpChannelState state) {
        return new HttpInput(state);
    }
    
    protected HttpOutput newHttpOutput() {
        return new HttpOutput(this);
    }
    
    public HttpChannelState getState() {
        return this._state;
    }
    
    public long getBytesWritten() {
        return this._written;
    }
    
    public int getRequests() {
        return this._requests.get();
    }
    
    public Connector getConnector() {
        return this._connector;
    }
    
    public HttpTransport getHttpTransport() {
        return this._transport;
    }
    
    public RequestLog getRequestLog() {
        return this._requestLog;
    }
    
    public void setRequestLog(final RequestLog requestLog) {
        this._requestLog = requestLog;
    }
    
    public void addRequestLog(final RequestLog requestLog) {
        if (this._requestLog == null) {
            this._requestLog = requestLog;
        }
        else if (this._requestLog instanceof RequestLogCollection) {
            ((RequestLogCollection)this._requestLog).add(requestLog);
        }
        else {
            this._requestLog = new RequestLogCollection(new RequestLog[] { this._requestLog, requestLog });
        }
    }
    
    public MetaData.Response getCommittedMetaData() {
        return this._committedMetaData;
    }
    
    public long getIdleTimeout() {
        return this._endPoint.getIdleTimeout();
    }
    
    public void setIdleTimeout(final long timeoutMs) {
        this._endPoint.setIdleTimeout(timeoutMs);
    }
    
    public ByteBufferPool getByteBufferPool() {
        return this._connector.getByteBufferPool();
    }
    
    public HttpConfiguration getHttpConfiguration() {
        return this._configuration;
    }
    
    @Override
    public boolean isOptimizedForDirectBuffers() {
        return this.getHttpTransport().isOptimizedForDirectBuffers();
    }
    
    public Server getServer() {
        return this._connector.getServer();
    }
    
    public Request getRequest() {
        return this._request;
    }
    
    public Response getResponse() {
        return this._response;
    }
    
    public EndPoint getEndPoint() {
        return this._endPoint;
    }
    
    public InetSocketAddress getLocalAddress() {
        return this._endPoint.getLocalAddress();
    }
    
    public InetSocketAddress getRemoteAddress() {
        return this._endPoint.getRemoteAddress();
    }
    
    public void continue100(final int available) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    public void recycle() {
        this._committed.set(false);
        this._request.recycle();
        this._response.recycle();
        this._committedMetaData = null;
        this._requestLog = ((this._connector == null) ? null : this._connector.getServer().getRequestLog());
        this._written = 0L;
    }
    
    public void onAsyncWaitForContent() {
    }
    
    public void onBlockWaitForContent() {
    }
    
    public void onBlockWaitForContentFailure(final Throwable failure) {
        this.getRequest().getHttpInput().failed(failure);
    }
    
    @Override
    public void run() {
        this.handle();
    }
    
    public boolean handle() {
        if (HttpChannel.LOG.isDebugEnabled()) {
            HttpChannel.LOG.debug("{} handle {} ", this, this._request.getHttpURI());
        }
        HttpChannelState.Action action = this._state.handling();
        List<HttpConfiguration.Customizer> customizers;
        Throwable ex;
        String reason;
        ErrorHandler eh;
        String error_page;
        ContextHandler handler;
        Integer loop_detect;
        Label_1122:Label_0963_Outer:
        while (!this.getServer().isStopped()) {
            try {
                if (HttpChannel.LOG.isDebugEnabled()) {
                    HttpChannel.LOG.debug("{} action {}", this, action);
                }
            Label_0891_Outer:
                while (true) {
                Label_0901_Outer:
                    while (true) {
                    Label_0805_Outer:
                        while (true) {
                        Label_0848_Outer:
                            while (true) {
                            Label_0343_Outer:
                                while (true) {
                                Label_0406_Outer:
                                    while (true) {
                                        while (true) {
                                            switch (action) {
                                                case TERMINATED:
                                                case WAIT: {
                                                    break Label_1122;
                                                }
                                                case DISPATCH: {
                                                    try {
                                                        if (!this._request.hasMetaData()) {
                                                            throw new IllegalStateException("state=" + this._state);
                                                        }
                                                        this._request.setHandled(false);
                                                        this._response.getHttpOutput().reopen();
                                                        Label_0340: {
                                                            try {
                                                                this._request.setDispatcherType(DispatcherType.REQUEST);
                                                                customizers = this._configuration.getCustomizers();
                                                                if (!customizers.isEmpty()) {
                                                                    for (final HttpConfiguration.Customizer customizer : customizers) {
                                                                        customizer.customize(this.getConnector(), this._configuration, this._request);
                                                                        if (this._request.isHandled()) {
                                                                            break;
                                                                        }
                                                                    }
                                                                }
                                                                Label_0316: {
                                                                    if (!this._request.isHandled()) {
                                                                        this.getServer().handle(this);
                                                                        break Label_0316;
                                                                    }
                                                                    break Label_0316;
                                                                }
                                                                break Label_0340;
                                                            }
                                                            finally {
                                                                this._request.setDispatcherType(null);
                                                            }
                                                        }
                                                        // iftrue(Label_0536:, loop_detect.intValue() <= this.getHttpConfiguration().getMaxErrorDispatches())
                                                        // iftrue(Label_0442:, loop_detect != null)
                                                    Label_0533:
                                                        while (true) {
                                                            break Label_0993;
                                                            try {
                                                                Label_0536: {
                                                                    if (this._response.isCommitted()) {
                                                                        if (HttpChannel.LOG.isDebugEnabled()) {
                                                                            HttpChannel.LOG.debug("Could not perform Error Dispatch because the response is already committed, aborting", new Object[0]);
                                                                        }
                                                                        this.abort(ex);
                                                                        break Label_0993;
                                                                    }
                                                                }
                                                                this._request.setHandled(false);
                                                                this._response.resetBuffer();
                                                                this._response.getHttpOutput().reopen();
                                                                if (ex == null || ex instanceof TimeoutException) {
                                                                    reason = "Async Timeout";
                                                                }
                                                                else {
                                                                    reason = HttpStatus.Code.INTERNAL_SERVER_ERROR.getMessage();
                                                                    this._request.setAttribute("javax.servlet.error.exception", ex);
                                                                }
                                                                this._request.setAttribute("javax.servlet.error.status_code", 500);
                                                                this._request.setAttribute("javax.servlet.error.message", reason);
                                                                this._request.setAttribute("javax.servlet.error.request_uri", this._request.getRequestURI());
                                                                this._response.setStatusWithReason(500, reason);
                                                                eh = ErrorHandler.getErrorHandler(this.getServer(), this._state.getContextHandler());
                                                                if (eh instanceof ErrorHandler.ErrorPageMapper) {
                                                                    error_page = ((ErrorHandler.ErrorPageMapper)eh).getErrorPage((HttpServletRequest)this._state.getAsyncContextEvent().getSuppliedRequest());
                                                                    if (error_page != null) {
                                                                        this._state.getAsyncContextEvent().setDispatchPath(error_page);
                                                                    }
                                                                }
                                                                Label_0802: {
                                                                    try {
                                                                        this._request.setDispatcherType(DispatcherType.ERROR);
                                                                        this.getServer().handleAsync(this);
                                                                        break Label_0802;
                                                                    }
                                                                    finally {
                                                                        this._request.setDispatcherType(null);
                                                                    }
                                                                }
                                                                break Label_0993;
                                                                try {
                                                                    throw new IllegalStateException("state=" + this._state);
                                                                }
                                                                catch (QuietServletException e) {
                                                                    if (HttpChannel.LOG.isDebugEnabled()) {
                                                                        HttpChannel.LOG.debug(e);
                                                                    }
                                                                    this.handleException(e);
                                                                }
                                                                break;
                                                                // iftrue(Label_0934:, this._response.isCommitted() || this._request.isHandled())
                                                                while (true) {
                                                                    this._request.setHandled(true);
                                                                    this._state.onComplete();
                                                                    this.onCompleted();
                                                                    break Label_1122;
                                                                    handler.handle(this._request, this._response.getHttpOutput());
                                                                    continue Label_0963_Outer;
                                                                    this._state.onError();
                                                                    continue Label_0963_Outer;
                                                                    Block_35: {
                                                                        break Block_35;
                                                                        Label_0934:
                                                                        this._response.closeOutput();
                                                                        continue Label_0891_Outer;
                                                                    }
                                                                    this._response.sendError(404);
                                                                    continue Label_0891_Outer;
                                                                }
                                                                Label_0878:
                                                                this._response.getHttpOutput().run();
                                                                continue Label_0963_Outer;
                                                                // iftrue(Label_0835:, handler == null)
                                                                while (true) {
                                                                    handler.handle(this._request, this._request.getHttpInput());
                                                                    continue Label_0963_Outer;
                                                                    handler = this._state.getContextHandler();
                                                                    continue Label_0805_Outer;
                                                                }
                                                                Label_0835:
                                                                this._request.getHttpInput().run();
                                                                continue Label_0963_Outer;
                                                                handler = this._state.getContextHandler();
                                                            }
                                                            // iftrue(Label_0878:, handler == null)
                                                            catch (QuietServletException ex2) {}
                                                            Label_0403: {
                                                                while (true) {
                                                                    while (true) {
                                                                        HttpChannel.LOG.warn("ERROR_DISPATCH loop detected on {} {}", this._request, ex);
                                                                        try {
                                                                            this._response.sendError(500);
                                                                        }
                                                                        finally {
                                                                            this._state.errorComplete();
                                                                        }
                                                                        break Label_0533;
                                                                        this._request.setHandled(false);
                                                                        this._response.getHttpOutput().reopen();
                                                                        try {
                                                                            this._request.setDispatcherType(DispatcherType.ASYNC);
                                                                            this.getServer().handleAsync(this);
                                                                            break Label_0403;
                                                                        }
                                                                        finally {
                                                                            this._request.setDispatcherType(null);
                                                                        }
                                                                        break Label_0403;
                                                                        this._request.setAttribute("org.eclipse.jetty.server.ERROR_DISPATCH", loop_detect);
                                                                        continue Label_0343_Outer;
                                                                    }
                                                                    ex = this._state.getAsyncContextEvent().getThrowable();
                                                                    loop_detect = (Integer)this._request.getAttribute("org.eclipse.jetty.server.ERROR_DISPATCH");
                                                                    loop_detect = 1;
                                                                    continue Label_0406_Outer;
                                                                }
                                                            }
                                                            continue Label_0963_Outer;
                                                        }
                                                        break Label_1122;
                                                        Label_0442: {
                                                            ++loop_detect;
                                                        }
                                                    }
                                                    catch (QuietServletException ex3) {}
                                                    break;
                                                }
                                                case ASYNC_DISPATCH: {
                                                    continue Label_0406_Outer;
                                                }
                                                case ERROR_DISPATCH: {
                                                    continue;
                                                }
                                                case READ_CALLBACK: {
                                                    continue Label_0848_Outer;
                                                }
                                                case WRITE_CALLBACK: {
                                                    continue Label_0343_Outer;
                                                }
                                                case ASYNC_ERROR: {
                                                    continue Label_0901_Outer;
                                                }
                                                case COMPLETE: {
                                                    continue Label_0805_Outer;
                                                }
                                                default: {
                                                    continue Label_0891_Outer;
                                                }
                                            }
                                            break;
                                        }
                                        break;
                                    }
                                    break;
                                }
                                break;
                            }
                            break;
                        }
                        break;
                    }
                    break;
                }
            }
            catch (EofException ex4) {}
            catch (QuietServletException ex5) {}
            catch (BadMessageException ex6) {}
            catch (Throwable e2) {
                if ("ContinuationThrowable".equals(e2.getClass().getSimpleName())) {
                    HttpChannel.LOG.ignore(e2);
                }
                else {
                    if (this._connector.isStarted()) {
                        HttpChannel.LOG.warn(String.valueOf(this._request.getHttpURI()), e2);
                    }
                    else {
                        HttpChannel.LOG.debug(String.valueOf(this._request.getHttpURI()), e2);
                    }
                    this.handleException(e2);
                }
            }
            action = this._state.unhandle();
        }
        if (HttpChannel.LOG.isDebugEnabled()) {
            HttpChannel.LOG.debug("{} handle exit, result {}", this, action);
        }
        final boolean suspended = action == HttpChannelState.Action.WAIT;
        return !suspended;
    }
    
    protected void handleException(final Throwable x) {
        if (this._state.isAsyncStarted()) {
            final Throwable root = this._state.getAsyncContextEvent().getThrowable();
            if (root == null) {
                this._state.error(x);
            }
            else {
                root.addSuppressed(x);
                HttpChannel.LOG.warn("Error while handling async error: ", root);
                this.abort(x);
                this._state.errorComplete();
            }
        }
        else {
            try {
                this._request.setHandled(true);
                this._request.setAttribute("javax.servlet.error.exception", x);
                this._request.setAttribute("javax.servlet.error.exception_type", x.getClass());
                if (this.isCommitted()) {
                    this.abort(x);
                    if (HttpChannel.LOG.isDebugEnabled()) {
                        HttpChannel.LOG.debug("Could not send response error 500, already committed", x);
                    }
                }
                else {
                    this._response.setHeader(HttpHeader.CONNECTION.asString(), HttpHeaderValue.CLOSE.asString());
                    if (x instanceof BadMessageException) {
                        final BadMessageException bme = (BadMessageException)x;
                        this._response.sendError(bme.getCode(), bme.getReason());
                    }
                    else if (x instanceof UnavailableException) {
                        if (((UnavailableException)x).isPermanent()) {
                            this._response.sendError(404);
                        }
                        else {
                            this._response.sendError(503);
                        }
                    }
                    else {
                        this._response.sendError(500);
                    }
                }
            }
            catch (Throwable e) {
                this.abort(e);
                if (HttpChannel.LOG.isDebugEnabled()) {
                    HttpChannel.LOG.debug("Could not commit response error 500", e);
                }
            }
        }
    }
    
    public boolean isExpecting100Continue() {
        return false;
    }
    
    public boolean isExpecting102Processing() {
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x{r=%s,c=%b,a=%s,uri=%s}", this.getClass().getSimpleName(), this.hashCode(), this._requests, this._committed.get(), this._state.getState(), this._request.getHttpURI());
    }
    
    public void onRequest(final MetaData.Request request) {
        this._requests.incrementAndGet();
        this._request.setTimeStamp(System.currentTimeMillis());
        final HttpFields fields = this._response.getHttpFields();
        if (this._configuration.getSendDateHeader() && !fields.contains(HttpHeader.DATE)) {
            fields.put(this._connector.getServer().getDateField());
        }
        final long idleTO = this._configuration.getIdleTimeout();
        this._oldIdleTimeout = this.getIdleTimeout();
        if (idleTO >= 0L && this._oldIdleTimeout != idleTO) {
            this.setIdleTimeout(idleTO);
        }
        this._request.setMetaData(request);
        if (HttpChannel.LOG.isDebugEnabled()) {
            HttpChannel.LOG.debug("REQUEST for {} on {}{}{} {} {}{}{}", request.getURIString(), this, System.lineSeparator(), request.getMethod(), request.getURIString(), request.getHttpVersion(), System.lineSeparator(), request.getFields());
        }
    }
    
    public boolean onContent(final HttpInput.Content content) {
        if (HttpChannel.LOG.isDebugEnabled()) {
            HttpChannel.LOG.debug("{} content {}", this, content);
        }
        return this._request.getHttpInput().addContent(content);
    }
    
    public boolean onContentComplete() {
        if (HttpChannel.LOG.isDebugEnabled()) {
            HttpChannel.LOG.debug("{} onContentComplete", this);
        }
        return false;
    }
    
    public boolean onRequestComplete() {
        if (HttpChannel.LOG.isDebugEnabled()) {
            HttpChannel.LOG.debug("{} onRequestComplete", this);
        }
        return this._request.getHttpInput().eof();
    }
    
    public void onCompleted() {
        if (HttpChannel.LOG.isDebugEnabled()) {
            HttpChannel.LOG.debug("COMPLETE for {} written={}", this.getRequest().getRequestURI(), this.getBytesWritten());
        }
        if (this._requestLog != null) {
            this._requestLog.log(this._request, this._response);
        }
        final long idleTO = this._configuration.getIdleTimeout();
        if (idleTO >= 0L && this.getIdleTimeout() != this._oldIdleTimeout) {
            this.setIdleTimeout(this._oldIdleTimeout);
        }
        this._transport.onCompleted();
    }
    
    public boolean onEarlyEOF() {
        return this._request.getHttpInput().earlyEOF();
    }
    
    public void onBadMessage(int status, final String reason) {
        if (status < 400 || status > 599) {
            status = 400;
        }
        HttpChannelState.Action action;
        try {
            action = this._state.handling();
        }
        catch (Throwable e) {
            this.abort(e);
            throw new BadMessageException(status, reason);
        }
        try {
            if (action == HttpChannelState.Action.DISPATCH) {
                ByteBuffer content = null;
                final HttpFields fields = new HttpFields();
                final ErrorHandler handler = this.getServer().getBean(ErrorHandler.class);
                if (handler != null) {
                    content = handler.badMessageError(status, reason, fields);
                }
                this.sendResponse(new MetaData.Response(HttpVersion.HTTP_1_1, status, reason, fields, BufferUtil.length(content)), content, true);
            }
        }
        catch (IOException e2) {
            HttpChannel.LOG.debug(e2);
            try {
                this.onCompleted();
            }
            catch (Throwable e) {
                HttpChannel.LOG.debug(e);
                this.abort(e);
            }
        }
        finally {
            try {
                this.onCompleted();
            }
            catch (Throwable e3) {
                HttpChannel.LOG.debug(e3);
                this.abort(e3);
            }
        }
    }
    
    protected boolean sendResponse(MetaData.Response info, final ByteBuffer content, final boolean complete, final Callback callback) {
        final boolean committing = this._committed.compareAndSet(false, true);
        if (HttpChannel.LOG.isDebugEnabled()) {
            HttpChannel.LOG.debug("sendResponse info={} content={} complete={} committing={} callback={}", info, BufferUtil.toDetailString(content), complete, committing, callback);
        }
        if (committing) {
            if (info == null) {
                info = this._response.newResponseMetaData();
            }
            this.commit(info);
            final int status = info.getStatus();
            final Callback committed = (status < 200 && status >= 100) ? callback.new Commit100Callback() : callback.new CommitCallback();
            this._transport.send(info, this._request.isHead(), content, complete, committed);
        }
        else if (info == null) {
            this._transport.send(null, this._request.isHead(), content, complete, callback);
        }
        else {
            callback.failed(new IllegalStateException("committed"));
        }
        return committing;
    }
    
    protected boolean sendResponse(final MetaData.Response info, final ByteBuffer content, final boolean complete) throws IOException {
        try (final SharedBlockingCallback.Blocker blocker = this._response.getHttpOutput().acquireWriteBlockingCallback()) {
            final boolean committing = this.sendResponse(info, content, complete, blocker);
            blocker.block();
            return committing;
        }
        catch (Throwable failure) {
            if (HttpChannel.LOG.isDebugEnabled()) {
                HttpChannel.LOG.debug(failure);
            }
            this.abort(failure);
            throw failure;
        }
    }
    
    protected void commit(final MetaData.Response info) {
        this._committedMetaData = info;
        if (HttpChannel.LOG.isDebugEnabled()) {
            HttpChannel.LOG.debug("COMMIT for {} on {}{}{} {} {}{}{}", this.getRequest().getRequestURI(), this, System.lineSeparator(), info.getStatus(), info.getReason(), info.getHttpVersion(), System.lineSeparator(), info.getFields());
        }
    }
    
    public boolean isCommitted() {
        return this._committed.get();
    }
    
    @Override
    public void write(final ByteBuffer content, final boolean complete, final Callback callback) {
        this._written += BufferUtil.length(content);
        this.sendResponse(null, content, complete, callback);
    }
    
    @Override
    public void resetBuffer() {
        if (this.isCommitted()) {
            throw new IllegalStateException("Committed");
        }
    }
    
    @Override
    public HttpOutput.Interceptor getNextInterceptor() {
        return null;
    }
    
    protected void execute(final Runnable task) {
        this._executor.execute(task);
    }
    
    public Scheduler getScheduler() {
        return this._connector.getScheduler();
    }
    
    public boolean useDirectBuffers() {
        return this.getEndPoint() instanceof ChannelEndPoint;
    }
    
    public void abort(final Throwable failure) {
        this._transport.abort(failure);
    }
    
    static {
        LOG = Log.getLogger(HttpChannel.class);
    }
    
    private class CommitCallback extends Callback.Nested
    {
        private CommitCallback(final Callback callback) {
            super(callback);
        }
        
        @Override
        public void failed(final Throwable x) {
            if (HttpChannel.LOG.isDebugEnabled()) {
                HttpChannel.LOG.debug("Commit failed", x);
            }
            if (x instanceof BadMessageException) {
                HttpChannel.this._transport.send(HttpGenerator.RESPONSE_500_INFO, false, null, true, new Callback.Nested(this) {
                    @Override
                    public void succeeded() {
                        super.failed(x);
                        HttpChannel.this._response.getHttpOutput().closed();
                    }
                    
                    @Override
                    public void failed(final Throwable th) {
                        HttpChannel.this.abort(x);
                        super.failed(x);
                    }
                });
            }
            else {
                HttpChannel.this.abort(x);
                super.failed(x);
            }
        }
    }
    
    private class Commit100Callback extends CommitCallback
    {
        private Commit100Callback(final Callback callback) {
            callback.super();
        }
        
        @Override
        public void succeeded() {
            if (HttpChannel.this._committed.compareAndSet(true, false)) {
                super.succeeded();
            }
            else {
                super.failed(new IllegalStateException());
            }
        }
    }
}
