// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import java.io.InputStream;
import org.mortbay.resource.Resource;
import org.mortbay.io.BufferCache;
import org.mortbay.util.QuotedStringTokenizer;
import org.mortbay.io.Buffer;
import org.mortbay.io.RuntimeIOException;
import org.mortbay.util.ajax.Continuation;
import org.mortbay.io.nio.SelectChannelEndPoint;
import java.io.IOException;
import org.mortbay.log.Log;
import java.io.Writer;
import javax.servlet.ServletOutputStream;
import org.mortbay.io.Buffers;
import org.mortbay.util.URIUtil;
import java.io.PrintWriter;
import javax.servlet.ServletInputStream;
import org.mortbay.io.EndPoint;
import org.mortbay.io.Connection;

public class HttpConnection implements Connection
{
    private static int UNKNOWN;
    private static ThreadLocal __currentConnection;
    private long _timeStamp;
    private int _requests;
    private boolean _handling;
    private boolean _destroy;
    protected final Connector _connector;
    protected final EndPoint _endp;
    protected final Server _server;
    protected final HttpURI _uri;
    protected final Parser _parser;
    protected final HttpFields _requestFields;
    protected final Request _request;
    protected ServletInputStream _in;
    protected final Generator _generator;
    protected final HttpFields _responseFields;
    protected final Response _response;
    protected Output _out;
    protected OutputWriter _writer;
    protected PrintWriter _printWriter;
    int _include;
    private Object _associatedObject;
    private transient int _expect;
    private transient int _version;
    private transient boolean _head;
    private transient boolean _host;
    private transient boolean _delayedHandling;
    
    public static HttpConnection getCurrentConnection() {
        return HttpConnection.__currentConnection.get();
    }
    
    protected static void setCurrentConnection(final HttpConnection connection) {
        HttpConnection.__currentConnection.set(connection);
    }
    
    public HttpConnection(final Connector connector, final EndPoint endpoint, final Server server) {
        this._timeStamp = System.currentTimeMillis();
        this._expect = HttpConnection.UNKNOWN;
        this._version = HttpConnection.UNKNOWN;
        this._head = false;
        this._host = false;
        this._delayedHandling = false;
        this._uri = ((URIUtil.__CHARSET == "UTF-8") ? new HttpURI() : new EncodedHttpURI(URIUtil.__CHARSET));
        this._connector = connector;
        this._endp = endpoint;
        this._parser = new HttpParser(this._connector, endpoint, new RequestHandler(), this._connector.getHeaderBufferSize(), this._connector.getRequestBufferSize());
        this._requestFields = new HttpFields();
        this._responseFields = new HttpFields();
        this._request = new Request(this);
        this._response = new Response(this);
        (this._generator = new HttpGenerator(this._connector, this._endp, this._connector.getHeaderBufferSize(), this._connector.getResponseBufferSize())).setSendServerVersion(server.getSendServerVersion());
        this._server = server;
    }
    
    protected HttpConnection(final Connector connector, final EndPoint endpoint, final Server server, final Parser parser, final Generator generator, final Request request) {
        this._timeStamp = System.currentTimeMillis();
        this._expect = HttpConnection.UNKNOWN;
        this._version = HttpConnection.UNKNOWN;
        this._head = false;
        this._host = false;
        this._delayedHandling = false;
        this._uri = ((URIUtil.__CHARSET == "UTF-8") ? new HttpURI() : new EncodedHttpURI(URIUtil.__CHARSET));
        this._connector = connector;
        this._endp = endpoint;
        this._parser = parser;
        this._requestFields = new HttpFields();
        this._responseFields = new HttpFields();
        this._request = request;
        this._response = new Response(this);
        (this._generator = generator).setSendServerVersion(server.getSendServerVersion());
        this._server = server;
    }
    
    public void destroy() {
        synchronized (this) {
            this._destroy = true;
            if (!this._handling) {
                if (this._parser != null) {
                    this._parser.reset(true);
                }
                if (this._generator != null) {
                    this._generator.reset(true);
                }
                if (this._requestFields != null) {
                    this._requestFields.destroy();
                }
                if (this._responseFields != null) {
                    this._responseFields.destroy();
                }
            }
        }
    }
    
    public Parser getParser() {
        return this._parser;
    }
    
    public int getRequests() {
        return this._requests;
    }
    
    public long getTimeStamp() {
        return this._timeStamp;
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
    
    public EndPoint getEndPoint() {
        return this._endp;
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
    
    public ServletInputStream getInputStream() {
        if (this._in == null) {
            this._in = new HttpParser.Input((HttpParser)this._parser, this._connector.getMaxIdleTime());
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
            this._printWriter = new PrintWriter((Writer)this._writer) {
                public void close() {
                    try {
                        this.out.close();
                    }
                    catch (IOException e) {
                        Log.debug(e);
                        this.setError();
                    }
                }
            };
        }
        this._writer.setCharacterEncoding(encoding);
        return this._printWriter;
    }
    
    public boolean isResponseCommitted() {
        return this._generator.isCommitted();
    }
    
    public void handle() throws IOException {
        boolean more_in_buffer = true;
        int no_progress = 0;
        while (more_in_buffer) {
            try {
                synchronized (this) {
                    if (this._handling) {
                        throw new IllegalStateException();
                    }
                    this._handling = true;
                }
                setCurrentConnection(this);
                long io = 0L;
                final Continuation continuation = this._request.getContinuation();
                if (continuation != null && continuation.isPending()) {
                    Log.debug("resume continuation {}", continuation);
                    if (this._request.getMethod() == null) {
                        throw new IllegalStateException();
                    }
                    this.handleRequest();
                }
                else {
                    if (!this._parser.isComplete()) {
                        io = this._parser.parseAvailable();
                    }
                    while (this._generator.isCommitted() && !this._generator.isComplete()) {
                        final long written = this._generator.flush();
                        io += written;
                        if (written <= 0L) {
                            break;
                        }
                        if (!this._endp.isBufferingOutput()) {
                            continue;
                        }
                        this._endp.flush();
                    }
                    if (this._endp.isBufferingOutput()) {
                        this._endp.flush();
                        if (!this._endp.isBufferingOutput()) {
                            no_progress = 0;
                        }
                    }
                    if (io > 0L) {
                        no_progress = 0;
                    }
                    else if (no_progress++ >= 2) {
                        return;
                    }
                }
            }
            catch (HttpException e) {
                if (Log.isDebugEnabled()) {
                    Log.debug("uri=" + this._uri);
                    Log.debug("fields=" + this._requestFields);
                    Log.debug(e);
                }
                this._generator.sendError(e.getStatus(), e.getReason(), null, true);
                this._parser.reset(true);
                this._endp.close();
                throw e;
            }
            finally {
                setCurrentConnection(null);
                more_in_buffer = (this._parser.isMoreInBuffer() || this._endp.isBufferingInput());
                synchronized (this) {
                    this._handling = false;
                    if (this._destroy) {
                        this.destroy();
                        return;
                    }
                }
                if (this._parser.isComplete() && this._generator.isComplete() && !this._endp.isBufferingOutput()) {
                    if (!this._generator.isPersistent()) {
                        this._parser.reset(true);
                        more_in_buffer = false;
                    }
                    if (more_in_buffer) {
                        this.reset(false);
                        more_in_buffer = (this._parser.isMoreInBuffer() || this._endp.isBufferingInput());
                    }
                    else {
                        this.reset(true);
                    }
                    no_progress = 0;
                }
                final Continuation continuation2 = this._request.getContinuation();
                if (continuation2 != null && continuation2.isPending()) {
                    break;
                }
                if (this._generator.isCommitted() && !this._generator.isComplete() && this._endp instanceof SelectChannelEndPoint) {
                    ((SelectChannelEndPoint)this._endp).setWritable(false);
                }
            }
        }
    }
    
    public void reset(final boolean returnBuffers) {
        this._parser.reset(returnBuffers);
        this._requestFields.clear();
        this._request.recycle();
        this._generator.reset(returnBuffers);
        this._responseFields.clear();
        this._response.recycle();
        this._uri.clear();
    }
    
    protected void handleRequest() throws IOException {
        if (this._server.isRunning()) {
            boolean retrying = false;
            boolean error = false;
            String threadName = null;
            String info = null;
            try {
                info = URIUtil.canonicalPath(this._uri.getDecodedPath());
                if (info == null) {
                    throw new HttpException(400);
                }
                this._request.setPathInfo(info);
                if (this._out != null) {
                    this._out.reopen();
                }
                if (Log.isDebugEnabled()) {
                    threadName = Thread.currentThread().getName();
                    Thread.currentThread().setName(threadName + " - " + this._uri);
                }
                this._connector.customize(this._endp, this._request);
                this._server.handle(this);
            }
            catch (RetryRequest r) {
                if (Log.isDebugEnabled()) {
                    Log.ignore(r);
                }
                retrying = true;
            }
            catch (EofException e) {
                Log.ignore(e);
                error = true;
            }
            catch (HttpException e2) {
                Log.debug(e2);
                this._request.setHandled(true);
                this._response.sendError(e2.getStatus(), e2.getReason());
                error = true;
            }
            catch (RuntimeIOException e3) {
                Log.debug(e3);
                this._request.setHandled(true);
                error = true;
            }
            catch (Throwable e4) {
                if (e4 instanceof ThreadDeath) {
                    throw (ThreadDeath)e4;
                }
                if (info == null) {
                    Log.warn(this._uri + ": " + e4);
                    Log.debug(e4);
                    this._request.setHandled(true);
                    this._generator.sendError(400, null, null, true);
                }
                else {
                    Log.warn("" + this._uri, e4);
                    this._request.setHandled(true);
                    this._generator.sendError(500, null, null, true);
                }
                error = true;
            }
            finally {
                if (threadName != null) {
                    Thread.currentThread().setName(threadName);
                }
                if (!retrying) {
                    if (this._request.getContinuation() != null) {
                        Log.debug("continuation still pending {}");
                        this._request.getContinuation().reset();
                    }
                    if (this._endp.isOpen()) {
                        if (this._generator.isPersistent()) {
                            this._connector.persist(this._endp);
                        }
                        if (error) {
                            this._endp.close();
                        }
                        else {
                            if (!this._response.isCommitted() && !this._request.isHandled()) {
                                this._response.sendError(404);
                            }
                            this._response.complete();
                        }
                    }
                    else {
                        this._response.complete();
                    }
                }
            }
        }
    }
    
    public void commitResponse(final boolean last) throws IOException {
        if (!this._generator.isCommitted()) {
            this._generator.setResponse(this._response.getStatus(), this._response.getReason());
            try {
                this._generator.completeHeader(this._responseFields, last);
            }
            catch (IOException io) {
                throw io;
            }
            catch (RuntimeException e) {
                Log.warn("header full: " + e);
                if (Log.isDebugEnabled() && this._generator instanceof HttpGenerator) {
                    Log.debug(((HttpGenerator)this._generator)._header.toDetailString(), e);
                }
                this._response.reset();
                this._generator.reset(true);
                this._generator.setResponse(500, null);
                this._generator.completeHeader(this._responseFields, true);
                this._generator.complete();
                throw e;
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
                Log.warn("header full: " + e);
                Log.debug(e);
                this._response.reset();
                this._generator.reset(true);
                this._generator.setResponse(500, null);
                this._generator.completeHeader(this._responseFields, true);
                this._generator.complete();
                throw e;
            }
        }
        this._generator.complete();
    }
    
    public void flushResponse() throws IOException {
        try {
            this.commitResponse(false);
            this._generator.flush();
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
    
    static {
        HttpConnection.UNKNOWN = -2;
        HttpConnection.__currentConnection = new ThreadLocal();
    }
    
    private class RequestHandler extends HttpParser.EventHandler
    {
        private String _charset;
        
        public void startRequest(final Buffer method, final Buffer uri, Buffer version) throws IOException {
            HttpConnection.this._host = false;
            HttpConnection.this._expect = HttpConnection.UNKNOWN;
            HttpConnection.this._delayedHandling = false;
            this._charset = null;
            if (HttpConnection.this._request.getTimeStamp() == 0L) {
                HttpConnection.this._request.setTimeStamp(System.currentTimeMillis());
            }
            HttpConnection.this._request.setMethod(method.toString());
            try {
                HttpConnection.this._uri.parse(uri.array(), uri.getIndex(), uri.length());
                HttpConnection.this._request.setUri(HttpConnection.this._uri);
                if (version == null) {
                    HttpConnection.this._request.setProtocol("");
                    HttpConnection.this._version = 9;
                }
                else {
                    version = HttpVersions.CACHE.get(version);
                    HttpConnection.this._version = HttpVersions.CACHE.getOrdinal(version);
                    if (HttpConnection.this._version <= 0) {
                        HttpConnection.this._version = 10;
                    }
                    HttpConnection.this._request.setProtocol(version.toString());
                }
                HttpConnection.this._head = (method == HttpMethods.HEAD_BUFFER);
            }
            catch (Exception e) {
                Log.debug(e);
                throw new HttpException(400, null, e);
            }
        }
        
        public void parsedHeader(final Buffer name, Buffer value) {
            final int ho = HttpHeaders.CACHE.getOrdinal(name);
            Label_0379: {
                switch (ho) {
                    case 27: {
                        HttpConnection.this._host = true;
                        break;
                    }
                    case 24: {
                        value = HttpHeaderValues.CACHE.lookup(value);
                        HttpConnection.this._expect = HttpHeaderValues.CACHE.getOrdinal(value);
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
                    case 1: {
                        final int ordinal = HttpHeaderValues.CACHE.getOrdinal(value);
                        switch (ordinal) {
                            case -1: {
                                final QuotedStringTokenizer tok = new QuotedStringTokenizer(value.toString(), ",");
                                while (tok.hasMoreTokens()) {
                                    final BufferCache.CachedBuffer cb = HttpHeaderValues.CACHE.get(tok.nextToken().trim());
                                    if (cb != null) {
                                        switch (cb.getOrdinal()) {
                                            case 1: {
                                                HttpConnection.this._responseFields.add(HttpHeaders.CONNECTION_BUFFER, HttpHeaderValues.CLOSE_BUFFER);
                                                HttpConnection.this._generator.setPersistent(false);
                                                continue;
                                            }
                                            case 5: {
                                                if (HttpConnection.this._version == 10) {
                                                    HttpConnection.this._responseFields.add(HttpHeaders.CONNECTION_BUFFER, HttpHeaderValues.KEEP_ALIVE_BUFFER);
                                                    continue;
                                                }
                                                continue;
                                            }
                                        }
                                    }
                                }
                                break Label_0379;
                            }
                            case 1: {
                                HttpConnection.this._responseFields.put(HttpHeaders.CONNECTION_BUFFER, HttpHeaderValues.CLOSE_BUFFER);
                                HttpConnection.this._generator.setPersistent(false);
                                break Label_0379;
                            }
                            case 5: {
                                if (HttpConnection.this._version == 10) {
                                    HttpConnection.this._responseFields.put(HttpHeaders.CONNECTION_BUFFER, HttpHeaderValues.KEEP_ALIVE_BUFFER);
                                    break Label_0379;
                                }
                                break Label_0379;
                            }
                        }
                        break;
                    }
                }
            }
            HttpConnection.this._requestFields.add(name, value);
        }
        
        public void headerComplete() throws IOException {
            if (HttpConnection.this._endp instanceof SelectChannelEndPoint) {
                ((SelectChannelEndPoint)HttpConnection.this._endp).scheduleIdle();
            }
            HttpConnection.this._requests++;
            HttpConnection.this._generator.setVersion(HttpConnection.this._version);
            switch (HttpConnection.this._version) {
                case 10: {
                    HttpConnection.this._generator.setHead(HttpConnection.this._head);
                    break;
                }
                case 11: {
                    HttpConnection.this._generator.setHead(HttpConnection.this._head);
                    if (HttpConnection.this._server.getSendDateHeader()) {
                        HttpConnection.this._responseFields.put(HttpHeaders.DATE_BUFFER, HttpConnection.this._request.getTimeStampBuffer(), HttpConnection.this._request.getTimeStamp());
                    }
                    if (!HttpConnection.this._host) {
                        HttpConnection.this._generator.setResponse(400, null);
                        HttpConnection.this._responseFields.put(HttpHeaders.CONNECTION_BUFFER, HttpHeaderValues.CLOSE_BUFFER);
                        HttpConnection.this._generator.completeHeader(HttpConnection.this._responseFields, true);
                        HttpConnection.this._generator.complete();
                        return;
                    }
                    if (HttpConnection.this._expect == HttpConnection.UNKNOWN) {
                        break;
                    }
                    if (HttpConnection.this._expect == 6) {
                        if (((HttpParser)HttpConnection.this._parser).getHeaderBuffer() == null || ((HttpParser)HttpConnection.this._parser).getHeaderBuffer().length() < 2) {
                            HttpConnection.this._generator.setResponse(100, null);
                            HttpConnection.this._generator.completeHeader(null, true);
                            HttpConnection.this._generator.complete();
                            HttpConnection.this._generator.reset(false);
                            break;
                        }
                        break;
                    }
                    else {
                        if (HttpConnection.this._expect == 7) {
                            break;
                        }
                        HttpConnection.this._generator.setResponse(417, null);
                        HttpConnection.this._responseFields.put(HttpHeaders.CONNECTION_BUFFER, HttpHeaderValues.CLOSE_BUFFER);
                        HttpConnection.this._generator.completeHeader(HttpConnection.this._responseFields, true);
                        HttpConnection.this._generator.complete();
                        return;
                    }
                    break;
                }
            }
            if (this._charset != null) {
                HttpConnection.this._request.setCharacterEncodingUnchecked(this._charset);
            }
            if (((HttpParser)HttpConnection.this._parser).getContentLength() <= 0L && !((HttpParser)HttpConnection.this._parser).isChunking()) {
                HttpConnection.this.handleRequest();
            }
            else {
                HttpConnection.this._delayedHandling = true;
            }
        }
        
        public void content(final Buffer ref) throws IOException {
            if (HttpConnection.this._endp instanceof SelectChannelEndPoint) {
                ((SelectChannelEndPoint)HttpConnection.this._endp).scheduleIdle();
            }
            if (HttpConnection.this._delayedHandling) {
                HttpConnection.this._delayedHandling = false;
                HttpConnection.this.handleRequest();
            }
        }
        
        public void messageComplete(final long contentLength) throws IOException {
            if (HttpConnection.this._delayedHandling) {
                HttpConnection.this._delayedHandling = false;
                HttpConnection.this.handleRequest();
            }
        }
        
        public void startResponse(final Buffer version, final int status, final Buffer reason) {
            Log.debug("Bad request!: " + version + " " + status + " " + reason);
        }
    }
    
    public class Output extends AbstractGenerator.Output
    {
        Output() {
            super((AbstractGenerator)HttpConnection.this._generator, HttpConnection.this._connector.getMaxIdleTime());
        }
        
        public void close() throws IOException {
            if (this._closed) {
                return;
            }
            if (!HttpConnection.this.isIncluding() && !this._generator.isCommitted()) {
                HttpConnection.this.commitResponse(true);
            }
            else {
                HttpConnection.this.flushResponse();
            }
            super.close();
        }
        
        public void flush() throws IOException {
            if (!this._generator.isCommitted()) {
                HttpConnection.this.commitResponse(false);
            }
            super.flush();
        }
        
        public void print(final String s) throws IOException {
            if (this._closed) {
                throw new IOException("Closed");
            }
            final PrintWriter writer = HttpConnection.this.getPrintWriter(null);
            writer.print(s);
        }
        
        public void sendResponse(final Buffer response) throws IOException {
            ((HttpGenerator)this._generator).sendResponse(response);
        }
        
        public void sendContent(Object content) throws IOException {
            Resource resource = null;
            if (this._closed) {
                throw new IOException("Closed");
            }
            if (this._generator.getContentWritten() > 0L) {
                throw new IllegalStateException("!empty");
            }
            if (content instanceof HttpContent) {
                final HttpContent c = (HttpContent)content;
                final Buffer contentType = c.getContentType();
                if (contentType != null && !HttpConnection.this._responseFields.containsKey(HttpHeaders.CONTENT_TYPE_BUFFER)) {
                    final String enc = HttpConnection.this._response.getSetCharacterEncoding();
                    if (enc == null) {
                        HttpConnection.this._responseFields.add(HttpHeaders.CONTENT_TYPE_BUFFER, contentType);
                    }
                    else if (contentType instanceof BufferCache.CachedBuffer) {
                        final BufferCache.CachedBuffer content_type = ((BufferCache.CachedBuffer)contentType).getAssociate(enc);
                        if (content_type != null) {
                            HttpConnection.this._responseFields.put(HttpHeaders.CONTENT_TYPE_BUFFER, content_type);
                        }
                        else {
                            HttpConnection.this._responseFields.put(HttpHeaders.CONTENT_TYPE_BUFFER, contentType + ";charset=" + QuotedStringTokenizer.quote(enc, ";= "));
                        }
                    }
                    else {
                        HttpConnection.this._responseFields.put(HttpHeaders.CONTENT_TYPE_BUFFER, contentType + ";charset=" + QuotedStringTokenizer.quote(enc, ";= "));
                    }
                }
                if (c.getContentLength() > 0L) {
                    HttpConnection.this._responseFields.putLongField(HttpHeaders.CONTENT_LENGTH_BUFFER, c.getContentLength());
                }
                final Buffer lm = c.getLastModified();
                final long lml = c.getResource().lastModified();
                if (lm != null) {
                    HttpConnection.this._responseFields.put(HttpHeaders.LAST_MODIFIED_BUFFER, lm, lml);
                }
                else if (c.getResource() != null && lml != -1L) {
                    HttpConnection.this._responseFields.putDateField(HttpHeaders.LAST_MODIFIED_BUFFER, lml);
                }
                content = c.getBuffer();
                if (content == null) {
                    content = c.getInputStream();
                }
            }
            else if (content instanceof Resource) {
                resource = (Resource)content;
                HttpConnection.this._responseFields.putDateField(HttpHeaders.LAST_MODIFIED_BUFFER, resource.lastModified());
                content = resource.getInputStream();
            }
            if (content instanceof Buffer) {
                this._generator.addContent((Buffer)content, true);
                HttpConnection.this.commitResponse(true);
            }
            else {
                if (!(content instanceof InputStream)) {
                    throw new IllegalArgumentException("unknown content type?");
                }
                final InputStream in = (InputStream)content;
                try {
                    int max = this._generator.prepareUncheckedAddContent();
                    Buffer buffer = this._generator.getUncheckedBuffer();
                    for (int len = buffer.readFrom(in, max); len >= 0; len = buffer.readFrom(in, max)) {
                        this._generator.completeUncheckedAddContent();
                        HttpConnection.this._out.flush();
                        max = this._generator.prepareUncheckedAddContent();
                        buffer = this._generator.getUncheckedBuffer();
                    }
                    this._generator.completeUncheckedAddContent();
                    HttpConnection.this._out.flush();
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
    
    public class OutputWriter extends AbstractGenerator.OutputWriter
    {
        OutputWriter() {
            super(HttpConnection.this._out);
        }
    }
}
