// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import javax.servlet.ServletResponse;
import java.util.HashMap;
import javax.servlet.ServletRequestWrapper;
import java.util.Iterator;
import org.mortbay.util.UrlEncoded;
import java.io.UnsupportedEncodingException;
import org.mortbay.io.nio.NIOBuffer;
import org.mortbay.io.nio.IndirectNIOBuffer;
import org.mortbay.io.nio.DirectNIOBuffer;
import java.nio.ByteBuffer;
import java.util.EventListener;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequest;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequestAttributeEvent;
import org.mortbay.jetty.security.Authenticator;
import org.mortbay.jetty.security.SecurityHandler;
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.mortbay.io.BufferUtil;
import org.mortbay.util.URIUtil;
import javax.servlet.RequestDispatcher;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.mortbay.util.StringUtil;
import java.util.List;
import java.util.Locale;
import java.io.IOException;
import javax.servlet.ServletInputStream;
import org.mortbay.util.QuotedStringTokenizer;
import org.mortbay.util.LazyList;
import org.mortbay.util.AttributesMap;
import java.util.Collections;
import java.util.Enumeration;
import org.mortbay.log.Log;
import org.mortbay.jetty.security.UserRealm;
import org.mortbay.util.ajax.Continuation;
import org.mortbay.io.Buffer;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import org.mortbay.jetty.handler.ContextHandler;
import java.io.BufferedReader;
import org.mortbay.util.MultiMap;
import java.security.Principal;
import org.mortbay.util.Attributes;
import java.util.Map;
import org.mortbay.io.EndPoint;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;

public class Request implements HttpServletRequest
{
    private static final Collection __defaultLocale;
    private static final int __NONE = 0;
    private static final int _STREAM = 1;
    private static final int __READER = 2;
    private boolean _handled;
    private HttpConnection _connection;
    private EndPoint _endp;
    private Map _roleMap;
    private Attributes _attributes;
    private String _authType;
    private String _characterEncoding;
    private String _queryEncoding;
    private String _serverName;
    private String _remoteAddr;
    private String _remoteHost;
    private String _method;
    private String _pathInfo;
    private int _port;
    private String _protocol;
    private String _queryString;
    private String _requestedSessionId;
    private boolean _requestedSessionIdFromCookie;
    private String _requestURI;
    private String _scheme;
    private String _contextPath;
    private String _servletPath;
    private String _servletName;
    private HttpURI _uri;
    private Principal _userPrincipal;
    private MultiMap _parameters;
    private MultiMap _baseParameters;
    private boolean _paramsExtracted;
    private int _inputState;
    private BufferedReader _reader;
    private String _readerEncoding;
    private boolean _dns;
    private ContextHandler.SContext _context;
    private HttpSession _session;
    private SessionManager _sessionManager;
    private boolean _cookiesExtracted;
    private Cookie[] _cookies;
    private String[] _unparsedCookies;
    private long _timeStamp;
    private Buffer _timeStampBuffer;
    private Continuation _continuation;
    private Object _requestAttributeListeners;
    private Object _requestListeners;
    private Map _savedNewSessions;
    private UserRealm _userRealm;
    
    public Request() {
        this._handled = false;
        this._protocol = "HTTP/1.1";
        this._requestedSessionIdFromCookie = false;
        this._scheme = "http";
        this._inputState = 0;
        this._dns = false;
        this._cookiesExtracted = false;
    }
    
    public Request(final HttpConnection connection) {
        this._handled = false;
        this._protocol = "HTTP/1.1";
        this._requestedSessionIdFromCookie = false;
        this._scheme = "http";
        this._inputState = 0;
        this._dns = false;
        this._cookiesExtracted = false;
        this._connection = connection;
        this._endp = connection.getEndPoint();
        this._dns = this._connection.getResolveNames();
    }
    
    protected void setConnection(final HttpConnection connection) {
        this._connection = connection;
        this._endp = connection.getEndPoint();
        this._dns = connection.getResolveNames();
    }
    
    protected void recycle() {
        if (this._inputState == 2) {
            try {
                for (int r = this._reader.read(); r != -1; r = this._reader.read()) {}
            }
            catch (Exception e) {
                Log.ignore(e);
                this._reader = null;
            }
        }
        this._handled = false;
        if (this._context != null) {
            throw new IllegalStateException("Request in context!");
        }
        if (this._attributes != null) {
            this._attributes.clearAttributes();
        }
        this._authType = null;
        this._characterEncoding = null;
        this._queryEncoding = null;
        this._context = null;
        this._serverName = null;
        this._method = null;
        this._pathInfo = null;
        this._port = 0;
        this._protocol = "HTTP/1.1";
        this._queryString = null;
        this._requestedSessionId = null;
        this._requestedSessionIdFromCookie = false;
        this._session = null;
        this._sessionManager = null;
        this._requestURI = null;
        this._scheme = "http";
        this._servletPath = null;
        this._timeStamp = 0L;
        this._timeStampBuffer = null;
        this._uri = null;
        this._userPrincipal = null;
        if (this._baseParameters != null) {
            this._baseParameters.clear();
        }
        this._parameters = null;
        this._paramsExtracted = false;
        this._inputState = 0;
        this._cookiesExtracted = false;
        if (this._savedNewSessions != null) {
            this._savedNewSessions.clear();
        }
        this._savedNewSessions = null;
        if (this._continuation != null && this._continuation.isPending()) {
            this._continuation.reset();
        }
    }
    
    public Buffer getTimeStampBuffer() {
        if (this._timeStampBuffer == null && this._timeStamp > 0L) {
            this._timeStampBuffer = HttpFields.__dateCache.formatBuffer(this._timeStamp);
        }
        return this._timeStampBuffer;
    }
    
    public long getTimeStamp() {
        return this._timeStamp;
    }
    
    public void setTimeStamp(final long ts) {
        this._timeStamp = ts;
    }
    
    public boolean isHandled() {
        return this._handled;
    }
    
    public void setHandled(final boolean h) {
        this._handled = h;
    }
    
    public Object getAttribute(final String name) {
        if ("org.mortbay.jetty.ajax.Continuation".equals(name)) {
            return this.getContinuation(true);
        }
        if (this._attributes == null) {
            return null;
        }
        return this._attributes.getAttribute(name);
    }
    
    public Enumeration getAttributeNames() {
        if (this._attributes == null) {
            return Collections.enumeration((Collection<Object>)Collections.EMPTY_LIST);
        }
        return AttributesMap.getAttributeNamesCopy(this._attributes);
    }
    
    public String getAuthType() {
        return this._authType;
    }
    
    public String getCharacterEncoding() {
        return this._characterEncoding;
    }
    
    public long getContentRead() {
        if (this._connection == null || this._connection.getParser() == null) {
            return -1L;
        }
        return ((HttpParser)this._connection.getParser()).getContentRead();
    }
    
    public int getContentLength() {
        return (int)this._connection.getRequestFields().getLongField(HttpHeaders.CONTENT_LENGTH_BUFFER);
    }
    
    public String getContentType() {
        return this._connection.getRequestFields().getStringField(HttpHeaders.CONTENT_TYPE_BUFFER);
    }
    
    public void setContentType(final String contentType) {
        this._connection.getRequestFields().put(HttpHeaders.CONTENT_TYPE_BUFFER, contentType);
    }
    
    public String getContextPath() {
        return this._contextPath;
    }
    
    public Cookie[] getCookies() {
        if (this._cookiesExtracted) {
            return this._cookies;
        }
        if (!this._connection.getRequestFields().containsKey(HttpHeaders.COOKIE_BUFFER)) {
            this._cookies = null;
            this._cookiesExtracted = true;
            this._unparsedCookies = null;
            return this._cookies;
        }
        if (this._unparsedCookies != null) {
            int last = 0;
            final Enumeration enm = this._connection.getRequestFields().getValues(HttpHeaders.COOKIE_BUFFER);
            while (enm.hasMoreElements()) {
                final String c = enm.nextElement();
                if (last >= this._unparsedCookies.length || !c.equals(this._unparsedCookies[last])) {
                    this._unparsedCookies = null;
                    break;
                }
                ++last;
            }
            if (this._unparsedCookies != null && this._unparsedCookies.length == last) {
                this._cookiesExtracted = true;
                return this._cookies;
            }
        }
        this._cookies = null;
        Object cookies = null;
        Object lastCookies = null;
        int version = 0;
        final Enumeration enm2 = this._connection.getRequestFields().getValues(HttpHeaders.COOKIE_BUFFER);
        while (enm2.hasMoreElements()) {
            try {
                final String hdr = enm2.nextElement();
                lastCookies = LazyList.add(lastCookies, hdr);
                String name = null;
                String value = null;
                Cookie cookie = null;
                boolean invalue = false;
                boolean quoted = false;
                boolean escaped = false;
                int tokenstart = -1;
                int tokenend = -1;
                int i = 0;
                final int length = hdr.length();
                final int last2 = length - 1;
                while (i < length) {
                    final char c2 = hdr.charAt(i);
                    Label_0945: {
                        if (quoted) {
                            if (escaped) {
                                escaped = false;
                                break Label_0945;
                            }
                            switch (c2) {
                                case '\"': {
                                    tokenend = i;
                                    quoted = false;
                                    if (i != last2) {
                                        break;
                                    }
                                    if (invalue) {
                                        value = hdr.substring(tokenstart, tokenend + 1);
                                        break;
                                    }
                                    name = hdr.substring(tokenstart, tokenend + 1);
                                    value = "";
                                    break;
                                }
                                case '\\': {
                                    escaped = true;
                                }
                                default: {
                                    break Label_0945;
                                }
                            }
                        }
                        else if (invalue) {
                            switch (c2) {
                                case '\t':
                                case ' ': {
                                    break Label_0945;
                                }
                                case '\"': {
                                    if (tokenstart < 0) {
                                        quoted = true;
                                        tokenstart = i;
                                    }
                                    if ((tokenend = i) == last2) {
                                        value = hdr.substring(tokenstart, tokenend + 1);
                                        break;
                                    }
                                    break Label_0945;
                                }
                                case ',':
                                case ';': {
                                    if (tokenstart >= 0) {
                                        value = hdr.substring(tokenstart, tokenend + 1);
                                    }
                                    else {
                                        value = "";
                                    }
                                    tokenstart = -1;
                                    invalue = false;
                                    break;
                                }
                                default: {
                                    if (tokenstart < 0) {
                                        tokenstart = i;
                                    }
                                    if ((tokenend = i) == last2) {
                                        value = hdr.substring(tokenstart, tokenend + 1);
                                        break;
                                    }
                                    break Label_0945;
                                }
                            }
                        }
                        else {
                            switch (c2) {
                                case '\t':
                                case ' ': {
                                    break Label_0945;
                                }
                                case '\"': {
                                    if (tokenstart < 0) {
                                        quoted = true;
                                        tokenstart = i;
                                    }
                                    if ((tokenend = i) == last2) {
                                        name = hdr.substring(tokenstart, tokenend + 1);
                                        value = "";
                                        break;
                                    }
                                    break Label_0945;
                                }
                                case ',':
                                case ';': {
                                    if (tokenstart >= 0) {
                                        name = hdr.substring(tokenstart, tokenend + 1);
                                        value = "";
                                    }
                                    tokenstart = -1;
                                    break;
                                }
                                case '=': {
                                    if (tokenstart >= 0) {
                                        name = hdr.substring(tokenstart, tokenend + 1);
                                    }
                                    tokenstart = -1;
                                    invalue = true;
                                    break Label_0945;
                                }
                                default: {
                                    if (tokenstart < 0) {
                                        tokenstart = i;
                                    }
                                    if ((tokenend = i) == last2) {
                                        name = hdr.substring(tokenstart, tokenend + 1);
                                        value = "";
                                        break;
                                    }
                                    break Label_0945;
                                }
                            }
                        }
                        if (value != null && name != null) {
                            name = QuotedStringTokenizer.unquote(name);
                            value = QuotedStringTokenizer.unquote(value);
                            try {
                                if (name.startsWith("$")) {
                                    final String lowercaseName = name.toLowerCase();
                                    if ("$path".equals(lowercaseName)) {
                                        if (cookie != null) {
                                            cookie.setPath(value);
                                        }
                                    }
                                    else if ("$domain".equals(lowercaseName)) {
                                        if (cookie != null) {
                                            cookie.setDomain(value);
                                        }
                                    }
                                    else if ("$port".equals(lowercaseName)) {
                                        if (cookie != null) {
                                            cookie.setComment("port=" + value);
                                        }
                                    }
                                    else if ("$version".equals(lowercaseName)) {
                                        version = Integer.parseInt(value);
                                    }
                                }
                                else {
                                    cookie = new Cookie(name, value);
                                    if (version > 0) {
                                        cookie.setVersion(version);
                                    }
                                    cookies = LazyList.add(cookies, cookie);
                                }
                            }
                            catch (Exception e) {
                                Log.warn(e.toString());
                                Log.debug(e);
                            }
                            name = null;
                            value = null;
                        }
                    }
                    ++i;
                }
            }
            catch (Exception e2) {
                Log.warn(e2);
            }
        }
        int l = LazyList.size(cookies);
        this._cookiesExtracted = true;
        if (l > 0) {
            if (this._cookies == null || this._cookies.length != l) {
                this._cookies = new Cookie[l];
            }
            for (int j = 0; j < l; ++j) {
                this._cookies[j] = (Cookie)LazyList.get(cookies, j);
            }
            l = LazyList.size(lastCookies);
            this._unparsedCookies = new String[l];
            for (int j = 0; j < l; ++j) {
                this._unparsedCookies[j] = (String)LazyList.get(lastCookies, j);
            }
        }
        else {
            this._cookies = null;
            this._unparsedCookies = null;
        }
        if (this._cookies == null || this._cookies.length == 0) {
            return null;
        }
        return this._cookies;
    }
    
    public long getDateHeader(final String name) {
        return this._connection.getRequestFields().getDateField(name);
    }
    
    public String getHeader(final String name) {
        return this._connection.getRequestFields().getStringField(name);
    }
    
    public Enumeration getHeaderNames() {
        return this._connection.getRequestFields().getFieldNames();
    }
    
    public Enumeration getHeaders(final String name) {
        final Enumeration e = this._connection.getRequestFields().getValues(name);
        if (e == null) {
            return Collections.enumeration((Collection<Object>)Collections.EMPTY_LIST);
        }
        return e;
    }
    
    public ServletInputStream getInputStream() throws IOException {
        if (this._inputState != 0 && this._inputState != 1) {
            throw new IllegalStateException("READER");
        }
        this._inputState = 1;
        return this._connection.getInputStream();
    }
    
    public int getIntHeader(final String name) {
        return (int)this._connection.getRequestFields().getLongField(name);
    }
    
    public String getLocalAddr() {
        return (this._endp == null) ? null : this._endp.getLocalAddr();
    }
    
    public Locale getLocale() {
        final Enumeration enm = this._connection.getRequestFields().getValues("Accept-Language", ", \t");
        if (enm == null || !enm.hasMoreElements()) {
            return Locale.getDefault();
        }
        final List acceptLanguage = HttpFields.qualityList(enm);
        if (acceptLanguage.size() == 0) {
            return Locale.getDefault();
        }
        final int size = acceptLanguage.size();
        final int i = 0;
        if (i < size) {
            String language = acceptLanguage.get(i);
            language = HttpFields.valueParameters(language, null);
            String country = "";
            final int dash = language.indexOf(45);
            if (dash > -1) {
                country = language.substring(dash + 1).trim();
                language = language.substring(0, dash).trim();
            }
            return new Locale(language, country);
        }
        return Locale.getDefault();
    }
    
    public Enumeration getLocales() {
        final Enumeration enm = this._connection.getRequestFields().getValues("Accept-Language", ", \t");
        if (enm == null || !enm.hasMoreElements()) {
            return Collections.enumeration((Collection<Object>)Request.__defaultLocale);
        }
        final List acceptLanguage = HttpFields.qualityList(enm);
        if (acceptLanguage.size() == 0) {
            return Collections.enumeration((Collection<Object>)Request.__defaultLocale);
        }
        Object langs = null;
        for (int size = acceptLanguage.size(), i = 0; i < size; ++i) {
            String language = acceptLanguage.get(i);
            language = HttpFields.valueParameters(language, null);
            String country = "";
            final int dash = language.indexOf(45);
            if (dash > -1) {
                country = language.substring(dash + 1).trim();
                language = language.substring(0, dash).trim();
            }
            langs = LazyList.ensureSize(langs, size);
            langs = LazyList.add(langs, new Locale(language, country));
        }
        if (LazyList.size(langs) == 0) {
            return Collections.enumeration((Collection<Object>)Request.__defaultLocale);
        }
        return Collections.enumeration((Collection<Object>)LazyList.getList(langs));
    }
    
    public String getLocalName() {
        if (this._dns) {
            return (this._endp == null) ? null : this._endp.getLocalHost();
        }
        return (this._endp == null) ? null : this._endp.getLocalAddr();
    }
    
    public int getLocalPort() {
        return (this._endp == null) ? 0 : this._endp.getLocalPort();
    }
    
    public String getMethod() {
        return this._method;
    }
    
    public String getParameter(final String name) {
        if (!this._paramsExtracted) {
            this.extractParameters();
        }
        return (String)this._parameters.getValue(name, 0);
    }
    
    public Map getParameterMap() {
        if (!this._paramsExtracted) {
            this.extractParameters();
        }
        return Collections.unmodifiableMap((Map<?, ?>)this._parameters.toStringArrayMap());
    }
    
    public Enumeration getParameterNames() {
        if (!this._paramsExtracted) {
            this.extractParameters();
        }
        return Collections.enumeration(this._parameters.keySet());
    }
    
    public String[] getParameterValues(final String name) {
        if (!this._paramsExtracted) {
            this.extractParameters();
        }
        final List vals = this._parameters.getValues(name);
        if (vals == null) {
            return null;
        }
        return vals.toArray(new String[vals.size()]);
    }
    
    public String getPathInfo() {
        return this._pathInfo;
    }
    
    public String getPathTranslated() {
        if (this._pathInfo == null || this._context == null) {
            return null;
        }
        return this._context.getRealPath(this._pathInfo);
    }
    
    public String getProtocol() {
        return this._protocol;
    }
    
    public BufferedReader getReader() throws IOException {
        if (this._inputState != 0 && this._inputState != 2) {
            throw new IllegalStateException("STREAMED");
        }
        if (this._inputState == 2) {
            return this._reader;
        }
        String encoding = this.getCharacterEncoding();
        if (encoding == null) {
            encoding = StringUtil.__ISO_8859_1;
        }
        if (this._reader == null || !encoding.equalsIgnoreCase(this._readerEncoding)) {
            final ServletInputStream in = this.getInputStream();
            this._readerEncoding = encoding;
            this._reader = new BufferedReader((Reader)new InputStreamReader(in, encoding)) {
                public void close() throws IOException {
                    in.close();
                }
            };
        }
        this._inputState = 2;
        return this._reader;
    }
    
    public String getRealPath(final String path) {
        if (this._context == null) {
            return null;
        }
        return this._context.getRealPath(path);
    }
    
    public String getRemoteAddr() {
        if (this._remoteAddr != null) {
            return this._remoteAddr;
        }
        return (this._endp == null) ? null : this._endp.getRemoteAddr();
    }
    
    public String getRemoteHost() {
        if (!this._dns) {
            return this.getRemoteAddr();
        }
        if (this._remoteHost != null) {
            return this._remoteHost;
        }
        return (this._endp == null) ? null : this._endp.getRemoteHost();
    }
    
    public int getRemotePort() {
        return (this._endp == null) ? 0 : this._endp.getRemotePort();
    }
    
    public String getRemoteUser() {
        final Principal p = this.getUserPrincipal();
        if (p == null) {
            return null;
        }
        return p.getName();
    }
    
    public RequestDispatcher getRequestDispatcher(String path) {
        if (path == null || this._context == null) {
            return null;
        }
        if (!path.startsWith("/")) {
            String relTo = URIUtil.addPaths(this._servletPath, this._pathInfo);
            final int slash = relTo.lastIndexOf("/");
            if (slash > 1) {
                relTo = relTo.substring(0, slash + 1);
            }
            else {
                relTo = "/";
            }
            path = URIUtil.addPaths(relTo, path);
        }
        return this._context.getRequestDispatcher(path);
    }
    
    public String getRequestedSessionId() {
        return this._requestedSessionId;
    }
    
    public String getRequestURI() {
        if (this._requestURI == null && this._uri != null) {
            this._requestURI = this._uri.getPathAndParam();
        }
        return this._requestURI;
    }
    
    public StringBuffer getRequestURL() {
        final StringBuffer url = new StringBuffer(48);
        synchronized (url) {
            final String scheme = this.getScheme();
            final int port = this.getServerPort();
            url.append(scheme);
            url.append("://");
            url.append(this.getServerName());
            if (this._port > 0 && ((scheme.equalsIgnoreCase("http") && port != 80) || (scheme.equalsIgnoreCase("https") && port != 443))) {
                url.append(':');
                url.append(this._port);
            }
            url.append(this.getRequestURI());
            return url;
        }
    }
    
    public String getScheme() {
        return this._scheme;
    }
    
    public String getServerName() {
        if (this._serverName != null) {
            return this._serverName;
        }
        this._serverName = this._uri.getHost();
        this._port = this._uri.getPort();
        if (this._serverName != null) {
            return this._serverName;
        }
        final Buffer hostPort = this._connection.getRequestFields().get(HttpHeaders.HOST_BUFFER);
        if (hostPort != null) {
            int i = hostPort.length();
            while (i-- > 0) {
                if (hostPort.peek(hostPort.getIndex() + i) == 58) {
                    this._serverName = BufferUtil.to8859_1_String(hostPort.peek(hostPort.getIndex(), i));
                    this._port = BufferUtil.toInt(hostPort.peek(hostPort.getIndex() + i + 1, hostPort.length() - i - 1));
                    return this._serverName;
                }
            }
            if (this._serverName == null || this._port < 0) {
                this._serverName = BufferUtil.to8859_1_String(hostPort);
                this._port = 0;
            }
            return this._serverName;
        }
        if (this._connection != null) {
            this._serverName = this.getLocalName();
            this._port = this.getLocalPort();
            if (this._serverName != null && !"0.0.0.0".equals(this._serverName)) {
                return this._serverName;
            }
        }
        try {
            this._serverName = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            Log.ignore(e);
        }
        return this._serverName;
    }
    
    public int getServerPort() {
        if (this._port <= 0) {
            if (this._serverName == null) {
                this.getServerName();
            }
            if (this._port <= 0) {
                if (this._serverName != null && this._uri != null) {
                    this._port = this._uri.getPort();
                }
                else {
                    this._port = ((this._endp == null) ? 0 : this._endp.getLocalPort());
                }
            }
        }
        if (this._port > 0) {
            return this._port;
        }
        if (this.getScheme().equalsIgnoreCase("https")) {
            return 443;
        }
        return 80;
    }
    
    public String getServletPath() {
        if (this._servletPath == null) {
            this._servletPath = "";
        }
        return this._servletPath;
    }
    
    public String getServletName() {
        return this._servletName;
    }
    
    public HttpSession getSession() {
        return this.getSession(true);
    }
    
    public HttpSession getSession(final boolean create) {
        if (this._sessionManager == null && create) {
            throw new IllegalStateException("No SessionHandler or SessionManager");
        }
        if (this._session != null && this._sessionManager != null && this._sessionManager.isValid(this._session)) {
            return this._session;
        }
        this._session = null;
        final String id = this.getRequestedSessionId();
        if (id != null && this._sessionManager != null) {
            this._session = this._sessionManager.getHttpSession(id);
            if (this._session == null && !create) {
                return null;
            }
        }
        if (this._session == null && this._sessionManager != null && create) {
            this._session = this._sessionManager.newHttpSession(this);
            final Cookie cookie = this._sessionManager.getSessionCookie(this._session, this.getContextPath(), this.isSecure());
            if (cookie != null) {
                this._connection.getResponse().addCookie(cookie);
            }
        }
        return this._session;
    }
    
    public Principal getUserPrincipal() {
        if (this._userPrincipal != null && this._userPrincipal instanceof SecurityHandler.NotChecked) {
            final SecurityHandler.NotChecked not_checked = (SecurityHandler.NotChecked)this._userPrincipal;
            this._userPrincipal = SecurityHandler.__NO_USER;
            final Authenticator auth = not_checked.getSecurityHandler().getAuthenticator();
            final UserRealm realm = not_checked.getSecurityHandler().getUserRealm();
            final String pathInContext = (this.getPathInfo() == null) ? this.getServletPath() : (this.getServletPath() + this.getPathInfo());
            if (realm != null && auth != null) {
                try {
                    auth.authenticate(realm, pathInContext, this, null);
                }
                catch (Exception e) {
                    Log.ignore(e);
                }
            }
        }
        if (this._userPrincipal == SecurityHandler.__NO_USER) {
            return null;
        }
        return this._userPrincipal;
    }
    
    public String getQueryString() {
        if (this._queryString == null && this._uri != null) {
            if (this._queryEncoding == null) {
                this._queryString = this._uri.getQuery();
            }
            else {
                this._queryString = this._uri.getQuery(this._queryEncoding);
            }
        }
        return this._queryString;
    }
    
    public boolean isRequestedSessionIdFromCookie() {
        return this._requestedSessionId != null && this._requestedSessionIdFromCookie;
    }
    
    public boolean isRequestedSessionIdFromUrl() {
        return this._requestedSessionId != null && !this._requestedSessionIdFromCookie;
    }
    
    public boolean isRequestedSessionIdFromURL() {
        return this._requestedSessionId != null && !this._requestedSessionIdFromCookie;
    }
    
    public boolean isRequestedSessionIdValid() {
        if (this._requestedSessionId == null) {
            return false;
        }
        final HttpSession session = this.getSession(false);
        return session != null && this._sessionManager.getIdManager().getClusterId(this._requestedSessionId).equals(this._sessionManager.getClusterId(session));
    }
    
    public boolean isSecure() {
        return this._connection.isConfidential(this);
    }
    
    public boolean isUserInRole(String role) {
        if (this._roleMap != null) {
            final String r = this._roleMap.get(role);
            if (r != null) {
                role = r;
            }
        }
        final Principal principal = this.getUserPrincipal();
        return this._userRealm != null && principal != null && this._userRealm.isUserInRole(principal, role);
    }
    
    public void removeAttribute(final String name) {
        final Object old_value = (this._attributes == null) ? null : this._attributes.getAttribute(name);
        if (this._attributes != null) {
            this._attributes.removeAttribute(name);
        }
        if (old_value != null && this._requestAttributeListeners != null) {
            final ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(this._context, this, name, old_value);
            for (int size = LazyList.size(this._requestAttributeListeners), i = 0; i < size; ++i) {
                final EventListener listener = (ServletRequestAttributeListener)LazyList.get(this._requestAttributeListeners, i);
                if (listener instanceof ServletRequestAttributeListener) {
                    final ServletRequestAttributeListener l = (ServletRequestAttributeListener)listener;
                    l.attributeRemoved(event);
                }
            }
        }
    }
    
    public void setAttribute(final String name, final Object value) {
        final Object old_value = (this._attributes == null) ? null : this._attributes.getAttribute(name);
        if ("org.mortbay.jetty.Request.queryEncoding".equals(name)) {
            this.setQueryEncoding((value == null) ? null : value.toString());
        }
        else if ("org.mortbay.jetty.ResponseBuffer".equals(name)) {
            try {
                final ByteBuffer byteBuffer = (ByteBuffer)value;
                synchronized (byteBuffer) {
                    final NIOBuffer buffer = byteBuffer.isDirect() ? new DirectNIOBuffer(byteBuffer, true) : new IndirectNIOBuffer(byteBuffer, true);
                    ((HttpConnection.Output)this.getServletResponse().getOutputStream()).sendResponse(buffer);
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (this._attributes == null) {
            this._attributes = new AttributesMap();
        }
        this._attributes.setAttribute(name, value);
        if (this._requestAttributeListeners != null) {
            final ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(this._context, this, name, (old_value == null) ? value : old_value);
            for (int size = LazyList.size(this._requestAttributeListeners), i = 0; i < size; ++i) {
                final EventListener listener = (ServletRequestAttributeListener)LazyList.get(this._requestAttributeListeners, i);
                if (listener instanceof ServletRequestAttributeListener) {
                    final ServletRequestAttributeListener l = (ServletRequestAttributeListener)listener;
                    if (old_value == null) {
                        l.attributeAdded(event);
                    }
                    else if (value == null) {
                        l.attributeRemoved(event);
                    }
                    else {
                        l.attributeReplaced(event);
                    }
                }
            }
        }
    }
    
    public void setCharacterEncoding(final String encoding) throws UnsupportedEncodingException {
        if (this._inputState != 0) {
            return;
        }
        this._characterEncoding = encoding;
        if (!StringUtil.isUTF8(encoding)) {
            "".getBytes(encoding);
        }
    }
    
    public void setCharacterEncodingUnchecked(final String encoding) {
        this._characterEncoding = encoding;
    }
    
    private void extractParameters() {
        if (this._baseParameters == null) {
            this._baseParameters = new MultiMap(16);
        }
        if (this._paramsExtracted) {
            if (this._parameters == null) {
                this._parameters = this._baseParameters;
            }
            return;
        }
        this._paramsExtracted = true;
        if (this._uri != null && this._uri.hasQuery()) {
            if (this._queryEncoding == null) {
                this._uri.decodeQueryTo(this._baseParameters);
            }
            else {
                try {
                    this._uri.decodeQueryTo(this._baseParameters, this._queryEncoding);
                }
                catch (UnsupportedEncodingException e) {
                    if (Log.isDebugEnabled()) {
                        Log.warn(e);
                    }
                    else {
                        Log.warn(e.toString());
                    }
                }
            }
        }
        final String encoding = this.getCharacterEncoding();
        String content_type = this.getContentType();
        if (content_type != null && content_type.length() > 0) {
            content_type = HttpFields.valueParameters(content_type, null);
            if ("application/x-www-form-urlencoded".equalsIgnoreCase(content_type) && this._inputState == 0 && ("POST".equals(this.getMethod()) || "PUT".equals(this.getMethod()))) {
                final int content_length = this.getContentLength();
                if (content_length != 0) {
                    try {
                        int maxFormContentSize = -1;
                        if (this._context != null) {
                            maxFormContentSize = this._context.getContextHandler().getMaxFormContentSize();
                        }
                        else {
                            final Integer size = (Integer)this._connection.getConnector().getServer().getAttribute("org.mortbay.jetty.Request.maxFormContentSize");
                            if (size != null) {
                                maxFormContentSize = size;
                            }
                        }
                        if (content_length > maxFormContentSize && maxFormContentSize > 0) {
                            throw new IllegalStateException("Form too large" + content_length + ">" + maxFormContentSize);
                        }
                        final InputStream in = this.getInputStream();
                        UrlEncoded.decodeTo(in, this._baseParameters, encoding, (content_length < 0) ? maxFormContentSize : -1);
                    }
                    catch (IOException e2) {
                        if (Log.isDebugEnabled()) {
                            Log.warn(e2);
                        }
                        else {
                            Log.warn(e2.toString());
                        }
                    }
                }
            }
        }
        if (this._parameters == null) {
            this._parameters = this._baseParameters;
        }
        else if (this._parameters != this._baseParameters) {
            for (final Map.Entry entry : this._baseParameters.entrySet()) {
                final String name = entry.getKey();
                final Object values = entry.getValue();
                for (int i = 0; i < LazyList.size(values); ++i) {
                    this._parameters.add(name, LazyList.get(values, i));
                }
            }
        }
    }
    
    public void setServerName(final String host) {
        this._serverName = host;
    }
    
    public void setServerPort(final int port) {
        this._port = port;
    }
    
    public void setRemoteAddr(final String addr) {
        this._remoteAddr = addr;
    }
    
    public void setRemoteHost(final String host) {
        this._remoteHost = host;
    }
    
    public HttpURI getUri() {
        return this._uri;
    }
    
    public void setUri(final HttpURI uri) {
        this._uri = uri;
    }
    
    public HttpConnection getConnection() {
        return this._connection;
    }
    
    public int getInputState() {
        return this._inputState;
    }
    
    public void setAuthType(final String authType) {
        this._authType = authType;
    }
    
    public void setCookies(final Cookie[] cookies) {
        this._cookies = cookies;
    }
    
    public void setMethod(final String method) {
        this._method = method;
    }
    
    public void setPathInfo(final String pathInfo) {
        this._pathInfo = pathInfo;
    }
    
    public void setProtocol(final String protocol) {
        this._protocol = protocol;
    }
    
    public void setRequestedSessionId(final String requestedSessionId) {
        this._requestedSessionId = requestedSessionId;
    }
    
    public SessionManager getSessionManager() {
        return this._sessionManager;
    }
    
    public void setSessionManager(final SessionManager sessionManager) {
        this._sessionManager = sessionManager;
    }
    
    public void setRequestedSessionIdFromCookie(final boolean requestedSessionIdCookie) {
        this._requestedSessionIdFromCookie = requestedSessionIdCookie;
    }
    
    public void setSession(final HttpSession session) {
        this._session = session;
    }
    
    public void setScheme(final String scheme) {
        this._scheme = scheme;
    }
    
    public void setQueryString(final String queryString) {
        this._queryString = queryString;
    }
    
    public void setRequestURI(final String requestURI) {
        this._requestURI = requestURI;
    }
    
    public void setContextPath(final String contextPath) {
        this._contextPath = contextPath;
    }
    
    public void setServletPath(final String servletPath) {
        this._servletPath = servletPath;
    }
    
    public void setServletName(final String name) {
        this._servletName = name;
    }
    
    public void setUserPrincipal(final Principal userPrincipal) {
        this._userPrincipal = userPrincipal;
    }
    
    public void setContext(final ContextHandler.SContext context) {
        this._context = context;
    }
    
    public ContextHandler.SContext getContext() {
        return this._context;
    }
    
    public StringBuffer getRootURL() {
        final StringBuffer url = new StringBuffer(48);
        synchronized (url) {
            final String scheme = this.getScheme();
            final int port = this.getServerPort();
            url.append(scheme);
            url.append("://");
            url.append(this.getServerName());
            if (port > 0 && ((scheme.equalsIgnoreCase("http") && port != 80) || (scheme.equalsIgnoreCase("https") && port != 443))) {
                url.append(':');
                url.append(port);
            }
            return url;
        }
    }
    
    public Attributes getAttributes() {
        if (this._attributes == null) {
            this._attributes = new AttributesMap();
        }
        return this._attributes;
    }
    
    public void setAttributes(final Attributes attributes) {
        this._attributes = attributes;
    }
    
    public Continuation getContinuation() {
        return this._continuation;
    }
    
    public Continuation getContinuation(final boolean create) {
        if (this._continuation == null && create) {
            this._continuation = this.getConnection().getConnector().newContinuation();
        }
        return this._continuation;
    }
    
    void setContinuation(final Continuation cont) {
        this._continuation = cont;
    }
    
    public MultiMap getParameters() {
        return this._parameters;
    }
    
    public void setParameters(final MultiMap parameters) {
        this._parameters = ((parameters == null) ? this._baseParameters : parameters);
        if (this._paramsExtracted && this._parameters == null) {
            throw new IllegalStateException();
        }
    }
    
    public String toString() {
        return this.getMethod() + " " + this._uri + " " + this.getProtocol() + "\n" + this._connection.getRequestFields().toString();
    }
    
    public static Request getRequest(HttpServletRequest request) {
        if (request instanceof Request) {
            return (Request)request;
        }
        while (request instanceof ServletRequestWrapper) {
            request = (HttpServletRequest)((ServletRequestWrapper)request).getRequest();
        }
        if (request instanceof Request) {
            return (Request)request;
        }
        return HttpConnection.getCurrentConnection().getRequest();
    }
    
    public void addEventListener(final EventListener listener) {
        if (listener instanceof ServletRequestAttributeListener) {
            this._requestAttributeListeners = LazyList.add(this._requestAttributeListeners, listener);
        }
    }
    
    public void removeEventListener(final EventListener listener) {
        this._requestAttributeListeners = LazyList.remove(this._requestAttributeListeners, listener);
    }
    
    public void setRequestListeners(final Object requestListeners) {
        this._requestListeners = requestListeners;
    }
    
    public Object takeRequestListeners() {
        final Object listeners = this._requestListeners;
        this._requestListeners = null;
        return listeners;
    }
    
    public void saveNewSession(final Object key, final HttpSession session) {
        if (this._savedNewSessions == null) {
            this._savedNewSessions = new HashMap();
        }
        this._savedNewSessions.put(key, session);
    }
    
    public HttpSession recoverNewSession(final Object key) {
        if (this._savedNewSessions == null) {
            return null;
        }
        return this._savedNewSessions.get(key);
    }
    
    public UserRealm getUserRealm() {
        return this._userRealm;
    }
    
    public void setUserRealm(final UserRealm userRealm) {
        this._userRealm = userRealm;
    }
    
    public String getQueryEncoding() {
        return this._queryEncoding;
    }
    
    public void setQueryEncoding(final String queryEncoding) {
        this._queryEncoding = queryEncoding;
        this._queryString = null;
    }
    
    public void setRoleMap(final Map map) {
        this._roleMap = map;
    }
    
    public Map getRoleMap() {
        return this._roleMap;
    }
    
    public ServletContext getServletContext() {
        return this._context;
    }
    
    public ServletResponse getServletResponse() {
        return this._connection.getResponse();
    }
    
    static {
        __defaultLocale = Collections.singleton(Locale.getDefault());
    }
}
