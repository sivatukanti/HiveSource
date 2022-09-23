// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.io.InputStream;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.server.ssl.SslConnector;
import org.eclipse.jetty.server.nio.NIOConnector;
import org.eclipse.jetty.util.QuotedStringTokenizer;
import org.eclipse.jetty.http.HttpContent;
import org.eclipse.jetty.io.BufferCache;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.HttpHeaderValues;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.http.HttpVersions;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.RuntimeIOException;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.continuation.ContinuationThrowable;
import org.eclipse.jetty.http.HttpException;
import java.io.Writer;
import org.eclipse.jetty.io.UncheckedPrintWriter;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import org.eclipse.jetty.io.Buffers;
import org.eclipse.jetty.http.HttpGenerator;
import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.http.HttpBuffers;
import org.eclipse.jetty.http.EncodedHttpURI;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.io.EndPoint;
import java.io.PrintWriter;
import org.eclipse.jetty.http.Generator;
import javax.servlet.ServletInputStream;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.Parser;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.io.AbstractConnection;

public abstract class AbstractHttpConnection extends AbstractConnection
{
    private static final Logger LOG;
    private static final int UNKNOWN = -2;
    private static final ThreadLocal<AbstractHttpConnection> __currentConnection;
    private int _requests;
    protected final Connector _connector;
    protected final Server _server;
    protected final HttpURI _uri;
    protected final Parser _parser;
    protected final HttpFields _requestFields;
    protected final Request _request;
    protected volatile ServletInputStream _in;
    protected final Generator _generator;
    protected final HttpFields _responseFields;
    protected final Response _response;
    protected volatile Output _out;
    protected volatile OutputWriter _writer;
    protected volatile PrintWriter _printWriter;
    int _include;
    private Object _associatedObject;
    private int _version;
    private boolean _expect;
    private boolean _expect100Continue;
    private boolean _expect102Processing;
    private boolean _head;
    private boolean _host;
    private boolean _delayedHandling;
    
    public static AbstractHttpConnection getCurrentConnection() {
        return AbstractHttpConnection.__currentConnection.get();
    }
    
    protected static void setCurrentConnection(final AbstractHttpConnection connection) {
        AbstractHttpConnection.__currentConnection.set(connection);
    }
    
    public AbstractHttpConnection(final Connector connector, final EndPoint endpoint, final Server server) {
        super(endpoint);
        this._version = -2;
        this._expect = false;
        this._expect100Continue = false;
        this._expect102Processing = false;
        this._head = false;
        this._host = false;
        this._delayedHandling = false;
        this._uri = ("UTF-8".equals(URIUtil.__CHARSET) ? new HttpURI() : new EncodedHttpURI(URIUtil.__CHARSET));
        this._connector = connector;
        final HttpBuffers ab = (HttpBuffers)this._connector;
        this._parser = (Parser)this.newHttpParser(ab.getRequestBuffers(), endpoint, new RequestHandler());
        this._requestFields = new HttpFields();
        this._responseFields = new HttpFields(server.getMaxCookieVersion());
        this._request = new Request(this);
        this._response = new Response(this);
        (this._generator = (Generator)new HttpGenerator(ab.getResponseBuffers(), this._endp)).setSendServerVersion(server.getSendServerVersion());
        this._server = server;
    }
    
    protected AbstractHttpConnection(final Connector connector, final EndPoint endpoint, final Server server, final Parser parser, final Generator generator, final Request request) {
        super(endpoint);
        this._version = -2;
        this._expect = false;
        this._expect100Continue = false;
        this._expect102Processing = false;
        this._head = false;
        this._host = false;
        this._delayedHandling = false;
        this._uri = (URIUtil.__CHARSET.equals("UTF-8") ? new HttpURI() : new EncodedHttpURI(URIUtil.__CHARSET));
        this._connector = connector;
        this._parser = parser;
        this._requestFields = new HttpFields();
        this._responseFields = new HttpFields(server.getMaxCookieVersion());
        this._request = request;
        this._response = new Response(this);
        (this._generator = generator).setSendServerVersion(server.getSendServerVersion());
        this._server = server;
    }
    
    protected HttpParser newHttpParser(final Buffers requestBuffers, final EndPoint endpoint, final HttpParser.EventHandler requestHandler) {
        return new HttpParser(requestBuffers, endpoint, requestHandler);
    }
    
    public Parser getParser() {
        return this._parser;
    }
    
    public int getRequests() {
        return this._requests;
    }
    
    public Server getServer() {
        return this._server;
    }
    
    public Object getAssociatedObject() {
        return this._associatedObject;
    }
    
    public void setAssociatedObject(final Object associatedObject) {
        this._associatedObject = associatedObject;
    }
    
    public Connector getConnector() {
        return this._connector;
    }
    
    public HttpFields getRequestFields() {
        return this._requestFields;
    }
    
    public HttpFields getResponseFields() {
        return this._responseFields;
    }
    
    public boolean isConfidential(final Request request) {
        return this._connector != null && this._connector.isConfidential(request);
    }
    
    public boolean isIntegral(final Request request) {
        return this._connector != null && this._connector.isIntegral(request);
    }
    
    public boolean getResolveNames() {
        return this._connector.getResolveNames();
    }
    
    public Request getRequest() {
        return this._request;
    }
    
    public Response getResponse() {
        return this._response;
    }
    
    public ServletInputStream getInputStream() throws IOException {
        if (this._expect100Continue) {
            if (((HttpParser)this._parser).getHeaderBuffer() == null || ((HttpParser)this._parser).getHeaderBuffer().length() < 2) {
                if (this._generator.isCommitted()) {
                    throw new IllegalStateException("Committed before 100 Continues");
                }
                ((HttpGenerator)this._generator).send1xx(100);
            }
            this._expect100Continue = false;
        }
        if (this._in == null) {
            this._in = new HttpInput(this);
        }
        return this._in;
    }
    
    public ServletOutputStream getOutputStream() {
        if (this._out == null) {
            this._out = new Output();
        }
        return this._out;
    }
    
    public PrintWriter getPrintWriter(final String encoding) {
        this.getOutputStream();
        if (this._writer == null) {
            this._writer = new OutputWriter();
            if (this._server.isUncheckedPrintWriter()) {
                this._printWriter = new UncheckedPrintWriter(this._writer);
            }
            else {
                this._printWriter = new PrintWriter(this._writer) {
                    @Override
                    public void close() {
                        synchronized (this.lock) {
                            try {
                                this.out.close();
                            }
                            catch (IOException e) {
                                this.setError();
                            }
                        }
                    }
                };
            }
        }
        this._writer.setCharacterEncoding(encoding);
        return this._printWriter;
    }
    
    public boolean isResponseCommitted() {
        return this._generator.isCommitted();
    }
    
    public void reset() {
        this._parser.reset();
        this._parser.returnBuffers();
        this._requestFields.clear();
        this._request.recycle();
        this._generator.reset();
        this._generator.returnBuffers();
        this._responseFields.clear();
        this._response.recycle();
        this._uri.clear();
    }
    
    protected void handleRequest() throws IOException {
        boolean error = false;
        String threadName = null;
        try {
            if (AbstractHttpConnection.LOG.isDebugEnabled()) {
                threadName = Thread.currentThread().getName();
                Thread.currentThread().setName(threadName + " - " + this._uri);
            }
            final Server server = this._server;
            boolean handling = this._request._async.handling() && server != null && server.isRunning();
            while (handling) {
                this._request.setHandled(false);
                String info = null;
                try {
                    this._uri.getPort();
                    info = URIUtil.canonicalPath(this._uri.getDecodedPath());
                    if (info == null && !this._request.getMethod().equals("CONNECT")) {
                        throw new HttpException(400);
                    }
                    this._request.setPathInfo(info);
                    if (this._out != null) {
                        this._out.reopen();
                    }
                    if (this._request._async.isInitial()) {
                        this._request.setDispatcherType(DispatcherType.REQUEST);
                        this._connector.customize(this._endp, this._request);
                        server.handle(this);
                    }
                    else {
                        this._request.setDispatcherType(DispatcherType.ASYNC);
                        server.handleAsync(this);
                    }
                    continue;
                }
                catch (ContinuationThrowable e) {
                    AbstractHttpConnection.LOG.ignore(e);
                }
                catch (EofException e2) {
                    AbstractHttpConnection.LOG.debug(e2);
                    error = true;
                    this._request.setHandled(true);
                }
                catch (RuntimeIOException e3) {
                    AbstractHttpConnection.LOG.debug(e3);
                    error = true;
                    this._request.setHandled(true);
                }
                catch (HttpException e4) {
                    AbstractHttpConnection.LOG.debug(e4);
                    error = true;
                    this._request.setHandled(true);
                    this._response.sendError(e4.getStatus(), e4.getReason());
                }
                catch (Throwable e5) {
                    AbstractHttpConnection.LOG.warn(String.valueOf(this._uri), e5);
                    error = true;
                    this._request.setHandled(true);
                    this._generator.sendError((info == null) ? 400 : 500, null, null, true);
                }
                finally {
                    handling = (!this._request._async.unhandle() && server.isRunning() && this._server != null);
                }
            }
        }
        finally {
            if (threadName != null) {
                Thread.currentThread().setName(threadName);
            }
            if (this._request._async.isUncompleted()) {
                this._request._async.doComplete();
                if (this._expect100Continue) {
                    AbstractHttpConnection.LOG.debug("100 continues not sent", new Object[0]);
                    this._expect100Continue = false;
                    if (!this._response.isCommitted()) {
                        this._generator.setPersistent(false);
                    }
                }
                if (this._endp.isOpen()) {
                    if (error) {
                        this._endp.shutdownOutput();
                        this._generator.setPersistent(false);
                        if (!this._generator.isComplete()) {
                            this._response.complete();
                        }
                    }
                    else {
                        if (!this._response.isCommitted() && !this._request.isHandled()) {
                            this._response.sendError(404);
                        }
                        this._response.complete();
                        if (this._generator.isPersistent()) {
                            this._connector.persist(this._endp);
                        }
                    }
                }
                else {
                    this._response.complete();
                }
                this._request.setHandled(true);
            }
        }
    }
    
    public abstract Connection handle() throws IOException;
    
    public void commitResponse(final boolean last) throws IOException {
        if (!this._generator.isCommitted()) {
            this._generator.setResponse(this._response.getStatus(), this._response.getReason());
            try {
                if (this._expect100Continue && this._response.getStatus() != 100) {
                    this._generator.setPersistent(false);
                }
                this._generator.completeHeader(this._responseFields, last);
            }
            catch (IOException io) {
                throw io;
            }
            catch (RuntimeException e) {
                AbstractHttpConnection.LOG.warn("header full: " + e, new Object[0]);
                this._response.reset();
                this._generator.reset();
                this._generator.setResponse(500, null);
                this._generator.completeHeader(this._responseFields, true);
                this._generator.complete();
                throw new HttpException(500);
            }
        }
        if (last) {
            this._generator.complete();
        }
    }
    
    public void completeResponse() throws IOException {
        if (!this._generator.isCommitted()) {
            this._generator.setResponse(this._response.getStatus(), this._response.getReason());
            try {
                this._generator.completeHeader(this._responseFields, true);
            }
            catch (IOException io) {
                throw io;
            }
            catch (RuntimeException e) {
                AbstractHttpConnection.LOG.warn("header full: " + e, new Object[0]);
                AbstractHttpConnection.LOG.debug(e);
                this._response.reset();
                this._generator.reset();
                this._generator.setResponse(500, null);
                this._generator.completeHeader(this._responseFields, true);
                this._generator.complete();
                throw new HttpException(500);
            }
        }
        this._generator.complete();
    }
    
    public void flushResponse() throws IOException {
        try {
            this.commitResponse(false);
            this._generator.flushBuffer();
        }
        catch (IOException e) {
            throw (e instanceof EofException) ? e : new EofException(e);
        }
    }
    
    public Generator getGenerator() {
        return this._generator;
    }
    
    public boolean isIncluding() {
        return this._include > 0;
    }
    
    public void include() {
        ++this._include;
    }
    
    public void included() {
        --this._include;
        if (this._out != null) {
            this._out.reopen();
        }
    }
    
    public boolean isIdle() {
        return this._generator.isIdle() && (this._parser.isIdle() || this._delayedHandling);
    }
    
    public boolean isSuspended() {
        return this._request.getAsyncContinuation().isSuspended();
    }
    
    @Override
    public void onClose() {
        AbstractHttpConnection.LOG.debug("closed {}", this);
    }
    
    public boolean isExpecting100Continues() {
        return this._expect100Continue;
    }
    
    public boolean isExpecting102Processing() {
        return this._expect102Processing;
    }
    
    public int getMaxIdleTime() {
        if (this._connector.isLowResources() && this._endp.getMaxIdleTime() == this._connector.getMaxIdleTime()) {
            return this._connector.getLowResourceMaxIdleTime();
        }
        if (this._endp.getMaxIdleTime() > 0) {
            return this._endp.getMaxIdleTime();
        }
        return this._connector.getMaxIdleTime();
    }
    
    @Override
    public String toString() {
        return String.format("%s,g=%s,p=%s,r=%d", super.toString(), this._generator, this._parser, this._requests);
    }
    
    static {
        LOG = Log.getLogger(AbstractHttpConnection.class);
        __currentConnection = new ThreadLocal<AbstractHttpConnection>();
    }
    
    private class RequestHandler extends HttpParser.EventHandler
    {
        private String _charset;
        
        @Override
        public void startRequest(final Buffer method, Buffer uri, Buffer version) throws IOException {
            uri = uri.asImmutableBuffer();
            AbstractHttpConnection.this._host = false;
            AbstractHttpConnection.this._expect = false;
            AbstractHttpConnection.this._expect100Continue = false;
            AbstractHttpConnection.this._expect102Processing = false;
            AbstractHttpConnection.this._delayedHandling = false;
            this._charset = null;
            if (AbstractHttpConnection.this._request.getTimeStamp() == 0L) {
                AbstractHttpConnection.this._request.setTimeStamp(System.currentTimeMillis());
            }
            AbstractHttpConnection.this._request.setMethod(method.toString());
            try {
                AbstractHttpConnection.this._head = false;
                switch (HttpMethods.CACHE.getOrdinal(method)) {
                    case 8: {
                        AbstractHttpConnection.this._uri.parseConnect(uri.array(), uri.getIndex(), uri.length());
                        break;
                    }
                    case 3: {
                        AbstractHttpConnection.this._head = true;
                        AbstractHttpConnection.this._uri.parse(uri.array(), uri.getIndex(), uri.length());
                        break;
                    }
                    default: {
                        AbstractHttpConnection.this._uri.parse(uri.array(), uri.getIndex(), uri.length());
                        break;
                    }
                }
                AbstractHttpConnection.this._request.setUri(AbstractHttpConnection.this._uri);
                if (version == null) {
                    AbstractHttpConnection.this._request.setProtocol("");
                    AbstractHttpConnection.this._version = 9;
                }
                else {
                    version = HttpVersions.CACHE.get(version);
                    if (version == null) {
                        throw new HttpException(400, null);
                    }
                    AbstractHttpConnection.this._version = HttpVersions.CACHE.getOrdinal(version);
                    if (AbstractHttpConnection.this._version <= 0) {
                        AbstractHttpConnection.this._version = 10;
                    }
                    AbstractHttpConnection.this._request.setProtocol(version.toString());
                }
            }
            catch (Exception e) {
                AbstractHttpConnection.LOG.debug(e);
                if (e instanceof HttpException) {
                    throw (HttpException)e;
                }
                throw new HttpException(400, null, e);
            }
        }
        
        @Override
        public void parsedHeader(final Buffer name, Buffer value) {
            final int ho = HttpHeaders.CACHE.getOrdinal(name);
            switch (ho) {
                case 27: {
                    AbstractHttpConnection.this._host = true;
                    break;
                }
                case 24: {
                    value = HttpHeaderValues.CACHE.lookup(value);
                    switch (HttpHeaderValues.CACHE.getOrdinal(value)) {
                        case 6: {
                            AbstractHttpConnection.this._expect100Continue = (AbstractHttpConnection.this._generator instanceof HttpGenerator);
                            break;
                        }
                        case 7: {
                            AbstractHttpConnection.this._expect102Processing = (AbstractHttpConnection.this._generator instanceof HttpGenerator);
                            break;
                        }
                        default: {
                            final String[] values = value.toString().split(",");
                            for (int i = 0; values != null && i < values.length; ++i) {
                                final BufferCache.CachedBuffer cb = HttpHeaderValues.CACHE.get(values[i].trim());
                                if (cb == null) {
                                    AbstractHttpConnection.this._expect = true;
                                }
                                else {
                                    switch (cb.getOrdinal()) {
                                        case 6: {
                                            AbstractHttpConnection.this._expect100Continue = (AbstractHttpConnection.this._generator instanceof HttpGenerator);
                                            break;
                                        }
                                        case 7: {
                                            AbstractHttpConnection.this._expect102Processing = (AbstractHttpConnection.this._generator instanceof HttpGenerator);
                                            break;
                                        }
                                        default: {
                                            AbstractHttpConnection.this._expect = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                case 21:
                case 40: {
                    value = HttpHeaderValues.CACHE.lookup(value);
                    break;
                }
                case 16: {
                    value = MimeTypes.CACHE.lookup(value);
                    this._charset = MimeTypes.getCharsetFromContentType(value);
                    break;
                }
            }
            AbstractHttpConnection.this._requestFields.add(name, value);
        }
        
        @Override
        public void headerComplete() throws IOException {
            AbstractHttpConnection.this._requests++;
            AbstractHttpConnection.this._generator.setVersion(AbstractHttpConnection.this._version);
            switch (AbstractHttpConnection.this._version) {
                case 10: {
                    AbstractHttpConnection.this._generator.setHead(AbstractHttpConnection.this._head);
                    if (AbstractHttpConnection.this._parser.isPersistent()) {
                        AbstractHttpConnection.this._responseFields.add(HttpHeaders.CONNECTION_BUFFER, HttpHeaderValues.KEEP_ALIVE_BUFFER);
                        AbstractHttpConnection.this._generator.setPersistent(true);
                    }
                    else if ("CONNECT".equals(AbstractHttpConnection.this._request.getMethod())) {
                        AbstractHttpConnection.this._generator.setPersistent(true);
                        AbstractHttpConnection.this._parser.setPersistent(true);
                    }
                    if (AbstractHttpConnection.this._server.getSendDateHeader()) {
                        AbstractHttpConnection.this._generator.setDate(AbstractHttpConnection.this._request.getTimeStampBuffer());
                        break;
                    }
                    break;
                }
                case 11: {
                    AbstractHttpConnection.this._generator.setHead(AbstractHttpConnection.this._head);
                    if (!AbstractHttpConnection.this._parser.isPersistent()) {
                        AbstractHttpConnection.this._responseFields.add(HttpHeaders.CONNECTION_BUFFER, HttpHeaderValues.CLOSE_BUFFER);
                        AbstractHttpConnection.this._generator.setPersistent(false);
                    }
                    if (AbstractHttpConnection.this._server.getSendDateHeader()) {
                        AbstractHttpConnection.this._generator.setDate(AbstractHttpConnection.this._request.getTimeStampBuffer());
                    }
                    if (!AbstractHttpConnection.this._host) {
                        AbstractHttpConnection.LOG.debug("!host {}", this);
                        AbstractHttpConnection.this._generator.setResponse(400, null);
                        AbstractHttpConnection.this._responseFields.put(HttpHeaders.CONNECTION_BUFFER, HttpHeaderValues.CLOSE_BUFFER);
                        AbstractHttpConnection.this._generator.completeHeader(AbstractHttpConnection.this._responseFields, true);
                        AbstractHttpConnection.this._generator.complete();
                        return;
                    }
                    if (AbstractHttpConnection.this._expect) {
                        AbstractHttpConnection.LOG.debug("!expectation {}", this);
                        AbstractHttpConnection.this._generator.setResponse(417, null);
                        AbstractHttpConnection.this._responseFields.put(HttpHeaders.CONNECTION_BUFFER, HttpHeaderValues.CLOSE_BUFFER);
                        AbstractHttpConnection.this._generator.completeHeader(AbstractHttpConnection.this._responseFields, true);
                        AbstractHttpConnection.this._generator.complete();
                        return;
                    }
                    break;
                }
            }
            if (this._charset != null) {
                AbstractHttpConnection.this._request.setCharacterEncodingUnchecked(this._charset);
            }
            if ((((HttpParser)AbstractHttpConnection.this._parser).getContentLength() <= 0L && !((HttpParser)AbstractHttpConnection.this._parser).isChunking()) || AbstractHttpConnection.this._expect100Continue) {
                AbstractHttpConnection.this.handleRequest();
            }
            else {
                AbstractHttpConnection.this._delayedHandling = true;
            }
        }
        
        @Override
        public void content(final Buffer ref) throws IOException {
            if (AbstractHttpConnection.this._delayedHandling) {
                AbstractHttpConnection.this._delayedHandling = false;
                AbstractHttpConnection.this.handleRequest();
            }
        }
        
        @Override
        public void messageComplete(final long contentLength) throws IOException {
            if (AbstractHttpConnection.this._delayedHandling) {
                AbstractHttpConnection.this._delayedHandling = false;
                AbstractHttpConnection.this.handleRequest();
            }
        }
        
        @Override
        public void startResponse(final Buffer version, final int status, final Buffer reason) {
            if (AbstractHttpConnection.LOG.isDebugEnabled()) {
                AbstractHttpConnection.LOG.debug("Bad request!: " + version + " " + status + " " + reason, new Object[0]);
            }
        }
    }
    
    public class Output extends HttpOutput
    {
        Output() {
            super(AbstractHttpConnection.this);
        }
        
        @Override
        public void close() throws IOException {
            if (this.isClosed()) {
                return;
            }
            if (!AbstractHttpConnection.this.isIncluding() && !super._generator.isCommitted()) {
                AbstractHttpConnection.this.commitResponse(true);
            }
            else {
                AbstractHttpConnection.this.flushResponse();
            }
            super.close();
        }
        
        @Override
        public void flush() throws IOException {
            if (!super._generator.isCommitted()) {
                AbstractHttpConnection.this.commitResponse(false);
            }
            super.flush();
        }
        
        @Override
        public void print(final String s) throws IOException {
            if (this.isClosed()) {
                throw new IOException("Closed");
            }
            final PrintWriter writer = AbstractHttpConnection.this.getPrintWriter(null);
            writer.print(s);
        }
        
        public void sendResponse(final Buffer response) throws IOException {
            ((HttpGenerator)super._generator).sendResponse(response);
        }
        
        public void sendContent(Object content) throws IOException {
            Resource resource = null;
            if (this.isClosed()) {
                throw new IOException("Closed");
            }
            if (super._generator.isWritten()) {
                throw new IllegalStateException("!empty");
            }
            if (content instanceof HttpContent) {
                final HttpContent httpContent = (HttpContent)content;
                final Buffer contentType = httpContent.getContentType();
                if (contentType != null && !AbstractHttpConnection.this._responseFields.containsKey(HttpHeaders.CONTENT_TYPE_BUFFER)) {
                    final String enc = AbstractHttpConnection.this._response.getSetCharacterEncoding();
                    if (enc == null) {
                        AbstractHttpConnection.this._responseFields.add(HttpHeaders.CONTENT_TYPE_BUFFER, contentType);
                    }
                    else if (contentType instanceof BufferCache.CachedBuffer) {
                        final BufferCache.CachedBuffer content_type = ((BufferCache.CachedBuffer)contentType).getAssociate(enc);
                        if (content_type != null) {
                            AbstractHttpConnection.this._responseFields.put(HttpHeaders.CONTENT_TYPE_BUFFER, (Buffer)content_type);
                        }
                        else {
                            AbstractHttpConnection.this._responseFields.put(HttpHeaders.CONTENT_TYPE_BUFFER, contentType + ";charset=" + QuotedStringTokenizer.quoteIfNeeded(enc, ";= "));
                        }
                    }
                    else {
                        AbstractHttpConnection.this._responseFields.put(HttpHeaders.CONTENT_TYPE_BUFFER, contentType + ";charset=" + QuotedStringTokenizer.quoteIfNeeded(enc, ";= "));
                    }
                }
                if (httpContent.getContentLength() > 0L) {
                    AbstractHttpConnection.this._responseFields.putLongField(HttpHeaders.CONTENT_LENGTH_BUFFER, httpContent.getContentLength());
                }
                final Buffer lm = httpContent.getLastModified();
                final long lml = httpContent.getResource().lastModified();
                if (lm != null) {
                    AbstractHttpConnection.this._responseFields.put(HttpHeaders.LAST_MODIFIED_BUFFER, lm);
                }
                else if (httpContent.getResource() != null && lml != -1L) {
                    AbstractHttpConnection.this._responseFields.putDateField(HttpHeaders.LAST_MODIFIED_BUFFER, lml);
                }
                final boolean direct = AbstractHttpConnection.this._connector instanceof NIOConnector && ((NIOConnector)AbstractHttpConnection.this._connector).getUseDirectBuffers() && !(AbstractHttpConnection.this._connector instanceof SslConnector);
                content = (direct ? httpContent.getDirectBuffer() : httpContent.getIndirectBuffer());
                if (content == null) {
                    content = httpContent.getInputStream();
                }
            }
            else if (content instanceof Resource) {
                resource = (Resource)content;
                AbstractHttpConnection.this._responseFields.putDateField(HttpHeaders.LAST_MODIFIED_BUFFER, resource.lastModified());
                content = resource.getInputStream();
            }
            if (content instanceof Buffer) {
                super._generator.addContent((Buffer)content, true);
                AbstractHttpConnection.this.commitResponse(true);
            }
            else {
                if (!(content instanceof InputStream)) {
                    throw new IllegalArgumentException("unknown content type?");
                }
                final InputStream in = (InputStream)content;
                try {
                    int max = super._generator.prepareUncheckedAddContent();
                    Buffer buffer = super._generator.getUncheckedBuffer();
                    for (int len = buffer.readFrom(in, max); len >= 0; len = buffer.readFrom(in, max)) {
                        super._generator.completeUncheckedAddContent();
                        AbstractHttpConnection.this._out.flush();
                        max = super._generator.prepareUncheckedAddContent();
                        buffer = super._generator.getUncheckedBuffer();
                    }
                    super._generator.completeUncheckedAddContent();
                    AbstractHttpConnection.this._out.flush();
                }
                finally {
                    if (resource != null) {
                        resource.release();
                    }
                    else {
                        in.close();
                    }
                }
            }
        }
    }
    
    public class OutputWriter extends HttpWriter
    {
        OutputWriter() {
            super(AbstractHttpConnection.this._out);
        }
    }
}
