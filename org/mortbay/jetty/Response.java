// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import org.mortbay.util.IO;
import org.mortbay.util.QuotedStringTokenizer;
import org.mortbay.io.Buffer;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import org.mortbay.util.URIUtil;
import java.io.IOException;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.ErrorHandler;
import java.io.OutputStream;
import org.mortbay.util.StringUtil;
import org.mortbay.util.ByteArrayISO8859Writer;
import javax.servlet.http.HttpServletRequest;
import org.mortbay.log.Log;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;
import org.mortbay.io.BufferCache;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;

public class Response implements HttpServletResponse
{
    public static final int DISABLED = -1;
    public static final int NONE = 0;
    public static final int STREAM = 1;
    public static final int WRITER = 2;
    private static PrintWriter __nullPrintWriter;
    private static ServletOutputStream __nullServletOut;
    private HttpConnection _connection;
    private int _status;
    private String _reason;
    private Locale _locale;
    private String _mimeType;
    private BufferCache.CachedBuffer _cachedMimeType;
    private String _characterEncoding;
    private boolean _explicitEncoding;
    private String _contentType;
    private int _outputState;
    private PrintWriter _writer;
    
    public Response(final HttpConnection connection) {
        this._status = 200;
        this._connection = connection;
    }
    
    protected void recycle() {
        this._status = 200;
        this._reason = null;
        this._locale = null;
        this._mimeType = null;
        this._cachedMimeType = null;
        this._characterEncoding = null;
        this._explicitEncoding = false;
        this._contentType = null;
        this._outputState = 0;
        this._writer = null;
    }
    
    public void addCookie(final Cookie cookie) {
        this._connection.getResponseFields().addSetCookie(cookie);
    }
    
    public boolean containsHeader(final String name) {
        return this._connection.getResponseFields().containsKey(name);
    }
    
    public String encodeURL(final String url) {
        final Request request = this._connection.getRequest();
        final SessionManager sessionManager = request.getSessionManager();
        if (sessionManager == null) {
            return url;
        }
        final String sessionURLPrefix = sessionManager.getSessionURLPrefix();
        if (sessionURLPrefix == null) {
            return url;
        }
        if (url == null || request == null || request.isRequestedSessionIdFromCookie()) {
            final int prefix = url.indexOf(sessionURLPrefix);
            if (prefix == -1) {
                return url;
            }
            int suffix = url.indexOf("?", prefix);
            if (suffix < 0) {
                suffix = url.indexOf("#", prefix);
            }
            if (suffix <= prefix) {
                return url.substring(0, prefix);
            }
            return url.substring(0, prefix) + url.substring(suffix);
        }
        else {
            final HttpSession session = request.getSession(false);
            if (session == null) {
                return url;
            }
            if (!sessionManager.isValid(session)) {
                return url;
            }
            final String id = sessionManager.getNodeId(session);
            final int prefix2 = url.indexOf(sessionURLPrefix);
            if (prefix2 != -1) {
                int suffix2 = url.indexOf("?", prefix2);
                if (suffix2 < 0) {
                    suffix2 = url.indexOf("#", prefix2);
                }
                if (suffix2 <= prefix2) {
                    return url.substring(0, prefix2 + sessionURLPrefix.length()) + id;
                }
                return url.substring(0, prefix2 + sessionURLPrefix.length()) + id + url.substring(suffix2);
            }
            else {
                int suffix2 = url.indexOf(63);
                if (suffix2 < 0) {
                    suffix2 = url.indexOf(35);
                }
                if (suffix2 < 0) {
                    return url + sessionURLPrefix + id;
                }
                return url.substring(0, suffix2) + sessionURLPrefix + id + url.substring(suffix2);
            }
        }
    }
    
    public String encodeRedirectURL(final String url) {
        return this.encodeURL(url);
    }
    
    public String encodeUrl(final String url) {
        return this.encodeURL(url);
    }
    
    public String encodeRedirectUrl(final String url) {
        return this.encodeURL(url);
    }
    
    public void sendError(final int code, String message) throws IOException {
        if (this._connection.isIncluding()) {
            return;
        }
        if (this.isCommitted()) {
            Log.warn("Committed before " + code + " " + message);
        }
        this.resetBuffer();
        this.setHeader("Expires", this._characterEncoding = null);
        this.setHeader("Last-Modified", null);
        this.setHeader("Cache-Control", null);
        this.setHeader("Content-Type", null);
        this.setHeader("Content-Length", null);
        this._outputState = 0;
        this.setStatus(code, message);
        if (message == null) {
            message = AbstractGenerator.getReason(code);
        }
        if (code != 204 && code != 304 && code != 206 && code >= 200) {
            final Request request = this._connection.getRequest();
            ErrorHandler error_handler = null;
            final ContextHandler.SContext context = request.getContext();
            if (context != null) {
                error_handler = context.getContextHandler().getErrorHandler();
            }
            if (error_handler != null) {
                request.setAttribute("javax.servlet.error.status_code", new Integer(code));
                request.setAttribute("javax.servlet.error.message", message);
                request.setAttribute("javax.servlet.error.request_uri", request.getRequestURI());
                request.setAttribute("javax.servlet.error.servlet_name", request.getServletName());
                error_handler.handle(null, this._connection.getRequest(), this, 8);
            }
            else {
                this.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
                this.setContentType("text/html; charset=iso-8859-1");
                final ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer(2048);
                if (message != null) {
                    message = StringUtil.replace(message, "&", "&amp;");
                    message = StringUtil.replace(message, "<", "&lt;");
                    message = StringUtil.replace(message, ">", "&gt;");
                }
                String uri = request.getRequestURI();
                if (uri != null) {
                    uri = StringUtil.replace(uri, "&", "&amp;");
                    uri = StringUtil.replace(uri, "<", "&lt;");
                    uri = StringUtil.replace(uri, ">", "&gt;");
                }
                writer.write("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"/>\n");
                writer.write("<title>Error ");
                writer.write(Integer.toString(code));
                writer.write(' ');
                if (message == null) {
                    message = AbstractGenerator.getReason(code);
                }
                writer.write(message);
                writer.write("</title>\n</head>\n<body>\n<h2>HTTP ERROR: ");
                writer.write(Integer.toString(code));
                writer.write("</h2>\n<p>Problem accessing ");
                writer.write(uri);
                writer.write(". Reason:\n<pre>    ");
                writer.write(message);
                writer.write("</pre>");
                writer.write("</p>\n<hr /><i><small>Powered by Jetty://</small></i>");
                for (int i = 0; i < 20; ++i) {
                    writer.write("\n                                                ");
                }
                writer.write("\n</body>\n</html>\n");
                writer.flush();
                this.setContentLength(writer.size());
                writer.writeTo(this.getOutputStream());
                writer.destroy();
            }
        }
        else if (code != 206) {
            this._connection.getRequestFields().remove(HttpHeaders.CONTENT_TYPE_BUFFER);
            this._connection.getRequestFields().remove(HttpHeaders.CONTENT_LENGTH_BUFFER);
            this._characterEncoding = null;
            this._mimeType = null;
            this._cachedMimeType = null;
        }
        this.complete();
    }
    
    public void sendError(final int sc) throws IOException {
        if (sc == 102) {
            this.sendProcessing();
        }
        else {
            this.sendError(sc, null);
        }
    }
    
    public void sendProcessing() throws IOException {
        final Generator g = this._connection.getGenerator();
        if (g instanceof HttpGenerator) {
            final HttpGenerator generator = (HttpGenerator)g;
            final String expect = this._connection.getRequest().getHeader("Expect");
            if (expect != null && expect.startsWith("102") && generator.getVersion() >= 11) {
                final boolean was_persistent = generator.isPersistent();
                generator.setResponse(102, null);
                generator.completeHeader(null, true);
                generator.setPersistent(true);
                generator.complete();
                generator.flush();
                generator.reset(false);
                generator.setPersistent(was_persistent);
            }
        }
    }
    
    public void sendRedirect(String location) throws IOException {
        if (this._connection.isIncluding()) {
            return;
        }
        if (location == null) {
            throw new IllegalArgumentException();
        }
        if (!URIUtil.hasScheme(location)) {
            StringBuffer buf = this._connection.getRequest().getRootURL();
            if (location.startsWith("/")) {
                buf.append(location);
            }
            else {
                final String path = this._connection.getRequest().getRequestURI();
                final String parent = path.endsWith("/") ? path : URIUtil.parentPath(path);
                location = URIUtil.addPaths(parent, location);
                if (location == null) {
                    throw new IllegalStateException("path cannot be above root");
                }
                if (!location.startsWith("/")) {
                    buf.append('/');
                }
                buf.append(location);
            }
            location = buf.toString();
            final HttpURI uri = new HttpURI(location);
            final String path2 = uri.getDecodedPath();
            final String canonical = URIUtil.canonicalPath(path2);
            if (canonical == null) {
                throw new IllegalArgumentException();
            }
            if (!canonical.equals(path2)) {
                buf = this._connection.getRequest().getRootURL();
                buf.append(canonical);
                if (uri.getQuery() != null) {
                    buf.append('?');
                    buf.append(uri.getQuery());
                }
                if (uri.getFragment() != null) {
                    buf.append('#');
                    buf.append(uri.getFragment());
                }
                location = buf.toString();
            }
        }
        this.resetBuffer();
        this.setHeader("Location", location);
        this.setStatus(302);
        this.complete();
    }
    
    public void setDateHeader(final String name, final long date) {
        if (!this._connection.isIncluding()) {
            this._connection.getResponseFields().putDateField(name, date);
        }
    }
    
    public void addDateHeader(final String name, final long date) {
        if (!this._connection.isIncluding()) {
            this._connection.getResponseFields().addDateField(name, date);
        }
    }
    
    public void setHeader(final String name, final String value) {
        if (!this._connection.isIncluding()) {
            this._connection.getResponseFields().put(name, value);
            if ("Content-Length".equalsIgnoreCase(name)) {
                if (value == null) {
                    this._connection._generator.setContentLength(-1L);
                }
                else {
                    this._connection._generator.setContentLength(Long.parseLong(value));
                }
            }
        }
    }
    
    public String getHeader(final String name) {
        return this._connection.getResponseFields().getStringField(name);
    }
    
    public Enumeration getHeaders(final String name) {
        final Enumeration e = this._connection.getResponseFields().getValues(name);
        if (e == null) {
            return Collections.enumeration((Collection<Object>)Collections.EMPTY_LIST);
        }
        return e;
    }
    
    public void addHeader(final String name, final String value) {
        if (!this._connection.isIncluding()) {
            this._connection.getResponseFields().add(name, value);
            if ("Content-Length".equalsIgnoreCase(name)) {
                this._connection._generator.setContentLength(Long.parseLong(value));
            }
        }
    }
    
    public void setIntHeader(final String name, final int value) {
        if (!this._connection.isIncluding()) {
            this._connection.getResponseFields().putLongField(name, value);
            if ("Content-Length".equalsIgnoreCase(name)) {
                this._connection._generator.setContentLength(value);
            }
        }
    }
    
    public void addIntHeader(final String name, final int value) {
        if (!this._connection.isIncluding()) {
            this._connection.getResponseFields().addLongField(name, value);
            if ("Content-Length".equalsIgnoreCase(name)) {
                this._connection._generator.setContentLength(value);
            }
        }
    }
    
    public void setStatus(final int sc) {
        this.setStatus(sc, null);
    }
    
    public void setStatus(final int sc, final String sm) {
        if (!this._connection.isIncluding()) {
            this._status = sc;
            this._reason = sm;
        }
    }
    
    public String getCharacterEncoding() {
        if (this._characterEncoding == null) {
            this._characterEncoding = StringUtil.__ISO_8859_1;
        }
        return this._characterEncoding;
    }
    
    String getSetCharacterEncoding() {
        return this._characterEncoding;
    }
    
    public String getContentType() {
        return this._contentType;
    }
    
    public ServletOutputStream getOutputStream() throws IOException {
        if (this._outputState == -1) {
            return Response.__nullServletOut;
        }
        if (this._outputState != 0 && this._outputState != 1) {
            throw new IllegalStateException("WRITER");
        }
        this._outputState = 1;
        return this._connection.getOutputStream();
    }
    
    public boolean isWriting() {
        return this._outputState == 2;
    }
    
    public PrintWriter getWriter() throws IOException {
        if (this._outputState == -1) {
            return Response.__nullPrintWriter;
        }
        if (this._outputState != 0 && this._outputState != 2) {
            throw new IllegalStateException("STREAM");
        }
        if (this._writer == null) {
            String encoding = this._characterEncoding;
            if (encoding == null) {
                if (this._mimeType != null) {
                    encoding = null;
                }
                if (encoding == null) {
                    encoding = StringUtil.__ISO_8859_1;
                }
                this.setCharacterEncoding(encoding);
            }
            this._writer = this._connection.getPrintWriter(encoding);
        }
        this._outputState = 2;
        return this._writer;
    }
    
    public void setCharacterEncoding(final String encoding) {
        if (this._connection.isIncluding()) {
            return;
        }
        if (this._outputState == 0 && !this.isCommitted()) {
            this._explicitEncoding = true;
            if (encoding == null) {
                if (this._characterEncoding != null) {
                    this._characterEncoding = null;
                    if (this._cachedMimeType != null) {
                        this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, this._cachedMimeType);
                    }
                    else {
                        this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, this._mimeType);
                    }
                }
            }
            else {
                this._characterEncoding = encoding;
                if (this._contentType != null) {
                    final int i0 = this._contentType.indexOf(59);
                    if (i0 < 0) {
                        this._contentType = null;
                        if (this._cachedMimeType != null) {
                            final BufferCache.CachedBuffer content_type = this._cachedMimeType.getAssociate(this._characterEncoding);
                            if (content_type != null) {
                                this._contentType = content_type.toString();
                                this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, content_type);
                            }
                        }
                        if (this._contentType == null) {
                            this._contentType = this._mimeType + "; charset=" + QuotedStringTokenizer.quote(this._characterEncoding, ";= ");
                            this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, this._contentType);
                        }
                    }
                    else {
                        final int i2 = this._contentType.indexOf("charset=", i0);
                        if (i2 < 0) {
                            this._contentType = this._contentType + "; charset=" + QuotedStringTokenizer.quote(this._characterEncoding, ";= ");
                        }
                        else {
                            final int i3 = i2 + 8;
                            final int i4 = this._contentType.indexOf(" ", i3);
                            if (i4 < 0) {
                                this._contentType = this._contentType.substring(0, i3) + QuotedStringTokenizer.quote(this._characterEncoding, ";= ");
                            }
                            else {
                                this._contentType = this._contentType.substring(0, i3) + QuotedStringTokenizer.quote(this._characterEncoding, ";= ") + this._contentType.substring(i4);
                            }
                        }
                        this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, this._contentType);
                    }
                }
            }
        }
    }
    
    public void setContentLength(final int len) {
        if (this.isCommitted() || this._connection.isIncluding()) {
            return;
        }
        this._connection._generator.setContentLength(len);
        if (len >= 0) {
            this._connection.getResponseFields().putLongField("Content-Length", len);
            if (this._connection._generator.isContentWritten()) {
                if (this._outputState == 2) {
                    this._writer.close();
                }
                else if (this._outputState == 1) {
                    try {
                        this.getOutputStream().close();
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
    
    public void setLongContentLength(final long len) {
        if (this.isCommitted() || this._connection.isIncluding()) {
            return;
        }
        this._connection._generator.setContentLength(len);
        this._connection.getResponseFields().putLongField("Content-Length", len);
    }
    
    public void setContentType(final String contentType) {
        if (this.isCommitted() || this._connection.isIncluding()) {
            return;
        }
        if (contentType == null) {
            if (this._locale == null) {
                this._characterEncoding = null;
            }
            this._mimeType = null;
            this._cachedMimeType = null;
            this._contentType = null;
            this._connection.getResponseFields().remove(HttpHeaders.CONTENT_TYPE_BUFFER);
        }
        else {
            final int i0 = contentType.indexOf(59);
            if (i0 > 0) {
                this._mimeType = contentType.substring(0, i0).trim();
                this._cachedMimeType = MimeTypes.CACHE.get(this._mimeType);
                final int i2 = contentType.indexOf("charset=", i0 + 1);
                if (i2 >= 0) {
                    this._explicitEncoding = true;
                    final int i3 = i2 + 8;
                    final int i4 = contentType.indexOf(32, i3);
                    if (this._outputState == 2) {
                        if ((i2 == i0 + 1 && i4 < 0) || (i2 == i0 + 2 && i4 < 0 && contentType.charAt(i0 + 1) == ' ')) {
                            if (this._cachedMimeType != null) {
                                final BufferCache.CachedBuffer content_type = this._cachedMimeType.getAssociate(this._characterEncoding);
                                if (content_type != null) {
                                    this._contentType = content_type.toString();
                                    this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, content_type);
                                }
                                else {
                                    this._contentType = this._mimeType + "; charset=" + this._characterEncoding;
                                    this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, this._contentType);
                                }
                            }
                            else {
                                this._contentType = this._mimeType + "; charset=" + this._characterEncoding;
                                this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, this._contentType);
                            }
                        }
                        else if (i4 < 0) {
                            this._contentType = contentType.substring(0, i2) + " charset=" + QuotedStringTokenizer.quote(this._characterEncoding, ";= ");
                            this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, this._contentType);
                        }
                        else {
                            this._contentType = contentType.substring(0, i2) + contentType.substring(i4) + " charset=" + QuotedStringTokenizer.quote(this._characterEncoding, ";= ");
                            this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, this._contentType);
                        }
                    }
                    else if ((i2 == i0 + 1 && i4 < 0) || (i2 == i0 + 2 && i4 < 0 && contentType.charAt(i0 + 1) == ' ')) {
                        this._cachedMimeType = MimeTypes.CACHE.get(this._mimeType);
                        this._characterEncoding = QuotedStringTokenizer.unquote(contentType.substring(i3));
                        if (this._cachedMimeType != null) {
                            final BufferCache.CachedBuffer content_type = this._cachedMimeType.getAssociate(this._characterEncoding);
                            if (content_type != null) {
                                this._contentType = content_type.toString();
                                this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, content_type);
                            }
                            else {
                                this._contentType = contentType;
                                this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, this._contentType);
                            }
                        }
                        else {
                            this._contentType = contentType;
                            this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, this._contentType);
                        }
                    }
                    else if (i4 > 0) {
                        this._characterEncoding = QuotedStringTokenizer.unquote(contentType.substring(i3, i4));
                        this._contentType = contentType;
                        this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, this._contentType);
                    }
                    else {
                        this._characterEncoding = QuotedStringTokenizer.unquote(contentType.substring(i3));
                        this._contentType = contentType;
                        this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, this._contentType);
                    }
                }
                else {
                    this._cachedMimeType = null;
                    this._contentType = ((this._characterEncoding == null) ? contentType : (contentType + "; charset=" + QuotedStringTokenizer.quote(this._characterEncoding, ";= ")));
                    this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, this._contentType);
                }
            }
            else {
                this._mimeType = contentType;
                this._cachedMimeType = MimeTypes.CACHE.get(this._mimeType);
                if (this._characterEncoding != null) {
                    if (this._cachedMimeType != null) {
                        final BufferCache.CachedBuffer content_type2 = this._cachedMimeType.getAssociate(this._characterEncoding);
                        if (content_type2 != null) {
                            this._contentType = content_type2.toString();
                            this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, content_type2);
                        }
                        else {
                            this._contentType = this._mimeType + "; charset=" + QuotedStringTokenizer.quote(this._characterEncoding, ";= ");
                            this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, this._contentType);
                        }
                    }
                    else {
                        this._contentType = contentType + "; charset=" + QuotedStringTokenizer.quote(this._characterEncoding, ";= ");
                        this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, this._contentType);
                    }
                }
                else if (this._cachedMimeType != null) {
                    this._contentType = this._cachedMimeType.toString();
                    this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, this._cachedMimeType);
                }
                else {
                    this._contentType = contentType;
                    this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, this._contentType);
                }
            }
        }
    }
    
    public void setBufferSize(final int size) {
        if (this.isCommitted() || this.getContentCount() > 0L) {
            throw new IllegalStateException("Committed or content written");
        }
        this._connection.getGenerator().increaseContentBufferSize(size);
    }
    
    public int getBufferSize() {
        return this._connection.getGenerator().getContentBufferSize();
    }
    
    public void flushBuffer() throws IOException {
        this._connection.flushResponse();
    }
    
    public void reset() {
        this.resetBuffer();
        final HttpFields response_fields = this._connection.getResponseFields();
        response_fields.clear();
        final String connection = this._connection.getRequestFields().getStringField(HttpHeaders.CONNECTION_BUFFER);
        if (connection != null) {
            final QuotedStringTokenizer tok = new QuotedStringTokenizer(connection, ",");
            while (tok.hasMoreTokens()) {
                final BufferCache.CachedBuffer cb = HttpHeaderValues.CACHE.get(tok.nextToken().trim());
                if (cb != null) {
                    switch (cb.getOrdinal()) {
                        case 1: {
                            response_fields.put(HttpHeaders.CONNECTION_BUFFER, HttpHeaderValues.CLOSE_BUFFER);
                            continue;
                        }
                        case 5: {
                            if ("HTTP/1.0".equalsIgnoreCase(this._connection.getRequest().getProtocol())) {
                                response_fields.put(HttpHeaders.CONNECTION_BUFFER, "keep-alive");
                                continue;
                            }
                            continue;
                        }
                        case 8: {
                            response_fields.put(HttpHeaders.CONNECTION_BUFFER, "TE");
                            continue;
                        }
                    }
                }
            }
        }
        if (this._connection.getConnector().getServer().getSendDateHeader()) {
            final Request request = this._connection.getRequest();
            response_fields.put(HttpHeaders.DATE_BUFFER, request.getTimeStampBuffer(), request.getTimeStamp());
        }
        this._status = 200;
        this._reason = null;
        this._mimeType = null;
        this._cachedMimeType = null;
        this._contentType = null;
        this._characterEncoding = null;
        this._explicitEncoding = false;
        this._locale = null;
        this._outputState = 0;
        this._writer = null;
    }
    
    public void resetBuffer() {
        if (this.isCommitted()) {
            throw new IllegalStateException("Committed");
        }
        this._connection.getGenerator().resetBuffer();
    }
    
    public boolean isCommitted() {
        return this._connection.isResponseCommitted();
    }
    
    public void setLocale(final Locale locale) {
        if (locale == null || this.isCommitted() || this._connection.isIncluding()) {
            return;
        }
        this._locale = locale;
        this._connection.getResponseFields().put(HttpHeaders.CONTENT_LANGUAGE_BUFFER, locale.toString().replace('_', '-'));
        if (this._explicitEncoding || this._outputState != 0) {
            return;
        }
        if (this._connection.getRequest().getContext() == null) {
            return;
        }
        final String charset = this._connection.getRequest().getContext().getContextHandler().getLocaleEncoding(locale);
        if (charset != null && charset.length() > 0) {
            this._characterEncoding = charset;
            String type = this.getContentType();
            if (type != null) {
                this._characterEncoding = charset;
                final int semi = type.indexOf(59);
                if (semi < 0) {
                    this._mimeType = type;
                    type = (this._contentType = type + "; charset=" + charset);
                }
                else {
                    this._mimeType = type.substring(0, semi);
                    final String string = this._mimeType + "; charset=" + charset;
                    this._mimeType = string;
                    this._contentType = string;
                }
                this._cachedMimeType = MimeTypes.CACHE.get(this._mimeType);
                this._connection.getResponseFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, this._contentType);
            }
        }
    }
    
    public Locale getLocale() {
        if (this._locale == null) {
            return Locale.getDefault();
        }
        return this._locale;
    }
    
    public int getStatus() {
        return this._status;
    }
    
    public String getReason() {
        return this._reason;
    }
    
    public void complete() throws IOException {
        this._connection.completeResponse();
    }
    
    public long getContentCount() {
        if (this._connection == null || this._connection.getGenerator() == null) {
            return -1L;
        }
        return this._connection.getGenerator().getContentWritten();
    }
    
    public HttpFields getHttpFields() {
        return this._connection.getResponseFields();
    }
    
    public String toString() {
        return "HTTP/1.1 " + this._status + " " + ((this._reason == null) ? "" : this._reason) + System.getProperty("line.separator") + this._connection.getResponseFields().toString();
    }
    
    static {
        try {
            Response.__nullPrintWriter = new PrintWriter(IO.getNullWriter());
            Response.__nullServletOut = new NullOutput();
        }
        catch (Exception e) {
            Log.warn(e);
        }
    }
    
    private static class NullOutput extends ServletOutputStream
    {
        public void write(final int b) throws IOException {
        }
    }
}
